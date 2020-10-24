package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task {

    private boolean complete;
    private Date due_date;
    private Date due_time;
    private String task_id;
    private String task_name;
    private int time_required;
    private int time_spent;
    private float weight;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Task(boolean complete, String due_date, String due_time, String task_id, String task_name,
                int time_required, int time_spent, float weight) throws ParseException {

        this.complete = complete;
        this.due_date = new SimpleDateFormat("MMM dd yyyy").parse(due_date);
        this.due_time = new SimpleDateFormat("hh:mm aa").parse(due_time);;
        this.task_id = task_id;
        this.task_name = task_name;
        this.time_required = time_required;
        this.time_spent = time_spent;
        this.weight = weight;
    }

    public Map<String, Object> return_task() {
        HashMap<String, Object> task_To_Database = new HashMap<>();

        task_To_Database.put("complete", complete);
        task_To_Database.put("dueDate", due_date.toString());
        task_To_Database.put("dueTime", due_time.toString());
        task_To_Database.put("name", task_name);
        task_To_Database.put("projectId", task_id);
        task_To_Database.put("timeRequired", time_required);
        task_To_Database.put("timeSpent", time_spent);
        task_To_Database.put("weight", weight);

        return task_To_Database;
    }

    public Date getDue_Date() {
        return due_date;
    }

    public Date getDue_time() {
        return due_time;
    }

    public String getName() {
        return task_name;
    }
}
