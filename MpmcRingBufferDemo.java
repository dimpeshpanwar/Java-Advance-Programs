import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Single-file: Lock-free MPMC ring buffer + throughput demo.
 * Compile: javac MpmcRingBufferDemo.java
 * Run:     java MpmcRingBufferDemo
 */
public class MpmcRingBufferDemo {

  // ================== Queue ==================
  public static final class MpmcRingBuffer<E> {
    private final int capacity;
    private final long mask;
    private final Cell<E>[] buffer;

    private volatile long head;      // next sequence to consume
    private volatile long tail;      // next sequence to produce
    private static final VarHandle HEAD, TAIL;

    static {
      try {
        MethodHandles.Lookup l = MethodHandles.lookup();
        HEAD = l.findVarHandle(MpmcRingBuffer.class, "head", long.class);
        TAIL = l.findVarHandle(MpmcRingBuffer.class, "tail", long.class);
      } catch (ReflectiveOperationException e) {
        throw new ExceptionInInitializerError(e);
      }
    }

    @SuppressWarnings("unchecked")
    public MpmcRingBuffer(int capacityPowerOfTwo) {
      if (capacityPowerOfTwo < 2 || (capacityPowerOfTwo & (capacityPowerOfTwo - 1)) != 0)
        throw new IllegalArgumentException("capacity must be power of two >= 2");
      this.capacity = capacityPowerOfTwo;
      this.mask = capacity - 1L;
      this.buffer = new Cell[capacity];
      for (int i = 0; i < capacity; i++) buffer[i] = new Cell<>(i);
    }

    public boolean offer(E e) {
      if (e == null) throw new NullPointerException();
      long backoff = 1L;
      while (true) {
        long t = (long) TAIL.getAcquire(this);
        Cell<E> c = buffer[(int) (t & mask)];
        long seq = c.sequence;
        long dif = seq - t; // 0 means slot ready to write
        if (dif == 0) {
          if (TAIL.compareAndSet(this, t, t + 1)) {
            c.value = e;
            c.sequence = t + 1; // publish to consumers
            return true;
          }
        } else if (dif < 0) {
          if (!pause(backoff)) return false; // appears full
          backoff = Math.min(backoff << 1, 1_000);
        } else {
          Thread.onSpinWait();
        }
      }
    }

    public E poll() {
      long backoff = 1L;
      while (true) {
        long h = (long) HEAD.getAcquire(this);
        Cell<E> c = buffer[(int) (h & mask)];
        long seq = c.sequence;
        long dif = seq - (h + 1); // 0 means ready to read
        if (dif == 0) {
          if (HEAD.compareAndSet(this, h, h + 1)) {
            E e = c.value;
            c.value = null;
            c.sequence = h + capacity; // reset for next producer
            return e;
          }
        } else if (dif < 0) {
          if (!pause(backoff)) return null; // appears empty
          backoff = Math.min(backoff << 1, 1_000);
        } else {
          Thread.onSpinWait();
        }
      }
    }

    public int capacity() { return capacity; }

    private static boolean pause(long nanos) {
      if (nanos > 50_000) {
        try { TimeUnit.NANOSECONDS.sleep(nanos); }
        catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
      } else {
        for (int i = 0; i < 64; i++) Thread.onSpinWait();
      }
      return true;
    }

    private static final class Cell<E> {
      volatile long sequence;
      volatile E value;
      Cell(long s) { this.sequence = s; }
    }
  }

  // ================== Demo ==================
  public static void main(String[] args) throws Exception {
    int capacity = argOr("CAP", 1 << 12);          // 4096
    int producers = argOr("P", 4);
    int consumers = argOr("C", 4);
    long messagesPerProducer = argOr("N", 200_000L);

    MpmcRingBuffer<long[]> q = new MpmcRingBuffer<>(capacity);
    CountDownLatch done = new CountDownLatch(producers);
    List<Thread> threads = new ArrayList<>();

    for (int p = 0; p < producers; p++) {
      final int id = p;
      Thread t = new Thread(() -> {
        for (long i = 0; i < messagesPerProducer; i++) {
          long stamp = (id << 48) | i;
          while (!q.offer(new long[]{stamp, System.nanoTime()})) { /* backoff */ }
        }
        done.countDown();
      }, "prod-" + p);
      threads.add(t);
    }

    final long total = messagesPerProducer * producers;
    final long[] count = {0};
    for (int c = 0; c < consumers; c++) {
      Thread t = new Thread(() -> {
        while (count[0] < total) {
          long[] item = q.poll();
          if (item != null) {
            // do work; here just count
            count[0]++;
          } else {
            Thread.onSpinWait();
          }
        }
      }, "cons-" + c);
      threads.add(t);
    }

    long start = System.nanoTime();
    threads.forEach(Thread::start);
    done.await();
    while (count[0] < total) Thread.onSpinWait();
    long durNanos = System.nanoTime() - start;
    double sec = durNanos / 1e9;
    System.out.printf(
        "Queue cap=%d P=%d C=%d -> processed %,d in %.3f s (%,.0f msg/s)%n",
        q.capacity(), producers, consumers, total, sec, total / sec);
  }

  // Helper: read system property or default
  private static int argOr(String key, int def) {
    String v = System.getProperty(key);
    return v == null ? def : Integer.parseInt(v);
  }
  private static long argOr(String key, long def) {
    String v = System.getProperty(key);
    return v == null ? def : Long.parseLong(v);
  }
}
