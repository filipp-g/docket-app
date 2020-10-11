package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.Date;

public class Task {

    private long id;
    private String name;
    private Project project;
    private Date dueDate;
    private float weight;
    private int timeRequired;
    private int timeSpent;
    private boolean complete;

    public Task() {
        this.id = 0;
        this.name = "";
        this.project = null;
        this.dueDate = new Date();
        this.weight = 0;
        this.timeRequired = 0;
        this.timeSpent = 0;
        this.complete = false;
    }

    public Task(long id, String name, Project project, Date dueDate, float weight,
                int timeRequired, int timeSpent) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.dueDate = dueDate;
        this.weight = weight;
        this.timeRequired = timeRequired;
        this.timeSpent = timeSpent;
        this.complete = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
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
