package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.ArrayList;
import java.util.List;

public class Project extends Task {

    private List<Task> projectTasks;

    public Project() {
        this.projectTasks = new ArrayList<>();
    }

    public Project(String id, String name, String projectId, String dueDate, String dueTime,
                   int weight, int timeRequired, int timeSpent, boolean complete, List<Task> projectTasks) {
        super(id, name, projectId, dueDate, dueTime, weight, timeRequired, timeSpent, complete);
        this.projectTasks = projectTasks;
    }

    public List<Task> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(List<Task> projectTasks) {
        this.projectTasks = projectTasks;
    }
}
