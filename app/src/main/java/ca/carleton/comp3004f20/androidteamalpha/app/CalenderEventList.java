package ca.carleton.comp3004f20.androidteamalpha.app;

import java.util.List;

public class CalenderEventList {

    private List<CalenderEvent> listOfEvents;

    public void addEvent(CalenderEvent event) {
        listOfEvents.add(event);
    }

    public void removeEvent(CalenderEvent event) { listOfEvents.remove(event); }

    public CalenderEvent getEvent(int counter) {
        if (0 <= counter && counter < listOfEvents.size()) {
            final CalenderEvent calenderEvent = listOfEvents.get(listOfEvents.size());
            return calenderEvent;
        }
        return null;
    }

    public int getSize() {
        return listOfEvents.size();
    }
}
