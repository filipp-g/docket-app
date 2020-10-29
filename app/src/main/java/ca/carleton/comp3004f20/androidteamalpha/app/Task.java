package ca.carleton.comp3004f20.androidteamalpha.app;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class Task {

    private String id;
    private String name;
    private String projectId;
    private String dueDate;
    private String dueTime;
    private int weight;
    private int timeRequired;
    private int timeSpent;
    private boolean complete;

    public Task() {
        this.id = "";
        this.name = "";
        this.projectId = "";
        this.dueDate = "";
        this.dueTime = "";
        this.weight = 0;
        this.timeRequired = 0;
        this.timeSpent = 0;
        this.complete = false;
    }

    public Task(String id, String name, String projectId, String dueDate, String dueTime,
                int weight, int timeRequired, int timeSpent, boolean complete) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.weight = weight;
        this.timeRequired = timeRequired;
        this.timeSpent = timeSpent;
        this.complete = complete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDueDate() throws ParseException {
        return dueDate;
    }

    public Date getDueDateAsDateFormat() throws ParseException {
        return new SimpleDateFormat("MMM dd yyyy").parse(dueDate);
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() throws ParseException {
        return dueTime;
    }

    public Date getDueTimeAsSimpleDate() throws ParseException {
        return new SimpleDateFormat("hh:mm aa").parse(dueTime);
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(int timeRequired) {
        this.timeRequired = timeRequired;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
