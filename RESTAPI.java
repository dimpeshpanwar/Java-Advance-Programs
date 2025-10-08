import static spark.Spark.*;

import com.google.gson.Gson;
import java.util.*;

public class SimpleApiServer {
    static List<String> tasks = new ArrayList<>();

    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();

        get("/tasks", (req, res) -> gson.toJson(tasks));

        post("/add", (req, res) -> {
            String task = req.queryParams("task");
            if (task != null && !task.isEmpty()) {
                tasks.add(task);
                return "Task added: " + task;
            }
            res.status(400);
            return "Invalid task!";
        });

        delete("/delete/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (id >= 0 && id < tasks.size()) {
                String removed = tasks.remove(id);
                return "Deleted: " + removed;
            }
            res.status(404);
            return "Task not found!";
        });
    }
}
