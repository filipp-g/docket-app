package ca.carleton.comp3004f20.androidteamalpha.app;

import android.graphics.Color;

import java.util.Calendar;
import java.util.Date;

public class CalenderEvent {

    private int color;
    private Date start;
    private Date end;
    private Task task;


    public void setColor(int color) {
        this.color = color;
    }
    public void setStartEvent(Date date) {
        start = date;
    }

    public void setEndEvent(Date date) {
        end = date;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getColour() {
        return color;
    }

    public long getStartEvent() {
        return start.getTime();
    }

    public long getEndEvent() {
        return end.getTime();
    }

    public Task getTask() {
        return task;
    }

    public String returnName() {
        return task.getName();
    }
}
