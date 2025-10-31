/*
 * ReverseKGroup.java
 * 
 * Description:
 * Given the head of a singly linked list and an integer k, reverse the list in groups of k nodes.
 * 
 * Approach:
 * Use an iterative approach to reverse each group of k nodes. 
 * If fewer than k nodes remain, leave them as is.
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

public class ReverseKGroup {

    public static ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1)
            return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prevGroupEnd = dummy;

        while (true) {
            ListNode kth = prevGroupEnd;
            for (int i = 0; i < k && kth != null; i++)
                kth = kth.next;
            if (kth == null)
                break;

            ListNode groupStart = prevGroupEnd.next;
            ListNode nextGroupStart = kth.next;

            // Reverse k nodes
            ListNode prev = nextGroupStart;
            ListNode curr = groupStart;
            while (curr != nextGroupStart) {
                ListNode nxt = curr.next;
                curr.next = prev;
                prev = curr;
                curr = nxt;
            }

            prevGroupEnd.next = prev;
            prevGroupEnd = groupStart;
        }

        return dummy.next;
    }

    public static void printList(ListNode head) {
        ListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val + " ");
            curr = curr.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Example: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7, k=3
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
        head.next.next.next.next.next = new ListNode(6);
        head.next.next.next.next.next.next = new ListNode(7);

        int k = 3;

        System.out.println("Original Linked List:");
        printList(head);

        head = reverseKGroup(head, k);

        System.out.println("Reversed in Groups of " + k + ":");
        printList(head);
    }
}
