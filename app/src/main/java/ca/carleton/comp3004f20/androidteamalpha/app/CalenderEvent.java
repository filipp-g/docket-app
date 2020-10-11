package ca.carleton.comp3004f20.androidteamalpha.app;

import android.graphics.Color;

import java.util.Calendar;
import java.util.Date;

public class CalenderEvent {

    private int color;
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private Task task;

    public void setStartEvent(int year, int month, int day) {
        start.set(year, month, day);
    }

    public void setEndEvent(int year, int month, int day) {
        end.set(year, month, day);
    }

    public void getTask(Task task) {
        this.task = task;
    }

    public long returnStartEvent() {
        return start.getTimeInMillis();
    }

    public long returnEndEvent() {
        return end.getTimeInMillis();
    }

    public Date returnStartEventDate() {
        return start.getTime();
    }

    public Date returnEndEventDate() {
        return end.getTime();
    }

    public int returnColour() {
        return color;
    }

    public String returnName() {
        return task.getName();
    }
}
