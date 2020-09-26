package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task {

    private long task_id;
    private String task_name;
    private Project task_project;
    private Date due_date;
    private float weight;
    private int time_required;
    private int time_spent;
    private boolean complete;

    public Task(long task_id, String task_name, Project task_project, Date due_date, float weight,
         int time_required, int time_spent) {
        this.task_id = task_id;
        this.task_name = task_name;
        this.task_project = task_project;
        this.due_date = due_date;
        this.weight = weight;
        this.time_required = time_required;
        this.time_spent = time_spent;
        complete = false;
    }

    public Map<String, Object> return_task() {
        HashMap<String, Object> task_To_Database = new HashMap<>();
        task_To_Database.put("task id", task_id);
        task_To_Database.put("task name", task_name);
        task_To_Database.put("task project", task_project);
        task_To_Database.put("due date", due_date);
        task_To_Database.put("weight", weight);
        task_To_Database.put("time required", time_required);
        task_To_Database.put("time spent", time_spent);
        task_To_Database.put("complete", complete);

        return task_To_Database;
    }
}
