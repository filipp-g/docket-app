package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {

    private long project_id;
    private String project_name;
    private List<Task> project_tasks;
    private int time_required;
    private int time_spent;
    private boolean complete;

    public Project(long project_id, String project_name, List<Task> project_tasks,
                   int time_required, int time_spent) {
        this.project_id = project_id;
        this.project_name = project_name;
        this.project_tasks = project_tasks;
        this.time_required = time_required;
        this.time_spent = time_spent;
        complete = false;
    }

    public void addTask(Task task) {
        this.project_tasks.add(task);
    }

    public void removeTask(Task task) {
        this.project_tasks.remove(task);
    }

    public void removeTask(int counter) {
        this.project_tasks.remove(counter);
    }

    public Map<String, Object> return_task() {
        HashMap<String, Object> task_To_Database = new HashMap<>();
        task_To_Database.put("project id", project_id);
        task_To_Database.put("project name", project_name);
        task_To_Database.put("project tasks", project_tasks);
        task_To_Database.put("time required", time_required);
        task_To_Database.put("time spent", time_spent);
        task_To_Database.put("complete", complete);

        return task_To_Database;
    }

}
