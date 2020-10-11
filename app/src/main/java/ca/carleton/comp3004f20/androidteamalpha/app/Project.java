package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project extends Task {

    private List<Task> projectTasks;

    public Project() {
        this.projectTasks = new ArrayList<>();
    }

    public Project(long id, String name, Project project, Date dueDate, float weight,
                   int timeRequired, int timeSpent, List<Task> projectTasks) {
        super(id, name, project, dueDate, weight, timeRequired, timeSpent);
        this.projectTasks = projectTasks;
    }

    public List<Task> getProjectTasks() {
        return projectTasks;
    }

    public void setProjectTasks(List<Task> projectTasks) {
        this.projectTasks = projectTasks;
    }
}
