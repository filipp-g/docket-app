package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.List;

public class CalenderEventList {

    private List<CalenderEvent> listOfEvents;

    public void addEvent(CalenderEvent event) {
        listOfEvents.add(event);
    }

    public CalenderEvent getEvent() {
        final CalenderEvent calenderEvent = listOfEvents.get(listOfEvents.size());
        return calenderEvent;
    }
}
