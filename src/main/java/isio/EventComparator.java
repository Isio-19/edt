package isio;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event arg0, Event arg1) {
        return arg0.getStartDate().compareTo(arg1.getStartDate());
    }

}
