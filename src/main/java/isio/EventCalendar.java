package isio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

/**
 * Calendar
 */
public class EventCalendar {
    private String name = null;
    private String description = null;
    private LocalDateTime startDate = null;
    private LocalDateTime endDate = null;

    // TODO: change to enum
    private String type = null;
    private ArrayList<Event> listEvents = null;

    public EventCalendar(String link) {
        try {
            URL url = new URL(link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String linkContent = "";
            String line;
            while ((line = reader.readLine()) != null)
                linkContent += line + "\n";

            ICalendar ical = Biweekly.parse(linkContent).first();

            // set the values of the calendar's attributes
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");            
            dtf.withZone(ZoneId.of("Europe/Paris"));
            
            startDate = LocalDateTime.parse(ical.getExperimentalProperties().get(0).getValue().toString(), dtf);
            endDate = LocalDateTime.parse(ical.getExperimentalProperties().get(1).getValue().toString(), dtf);

            description = ical.getExperimentalProperties().get(3).getValue().toString();

            String tempString = ical.getExperimentalProperties().get(2).getValue().toString();

            name = StringUtils.substringBetween(tempString, "HYP - ", " - du");
            type = "salle";

            // clean up the name depending on the type of time table
            if (link.contains("tdoption")) {
                name = StringUtils.substringBetween(name, "<", ">");
                type = "formation";
            } else if (link.contains("enseignant")) {
                name = name.substring(name.indexOf(" ") + 1);
                type = "enseignant";
            }

            List<VEvent> events = ical.getEvents();
            listEvents = new ArrayList<Event>();

            for (VEvent event : events) {
                // TODO: find a way to convert the time correctly while taking account of the timezone
                dtf = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);            
                dtf.withZone(ZoneId.of("Europe/Paris"));

                LocalDateTime eventStartDate = null;
                if (event.getDateStart() != null) {
                    String date = event.getDateStart().getValue().toString();
                    eventStartDate = LocalDateTime.parse(date, dtf);
                }
                
                LocalDateTime eventEndDate = null;
                if (event.getDateEnd() != null) {
                    String date = event.getDateEnd().getValue().toString();
                    eventEndDate = LocalDateTime.parse(date, dtf);
                }

                String eventLocation = null;
                if (event.getLocation() != null)
                    eventLocation = event.getLocation().getValue();

                String eventSummary = null;
                if (event.getSummary() != null)
                    eventSummary = event.getSummary().getValue();

                String eventDescription = null;
                if (event.getDescription() != null)
                    eventDescription = event.getDescription().getValue();

                Event eventToAdd = new Event(eventStartDate, eventEndDate, eventLocation, eventSummary,
                        eventDescription);
                        
                listEvents.add(eventToAdd);
            }

            Collections.sort(listEvents, new EventComparator());
        } catch (MalformedURLException e) {
            System.err.println("The given file url '" + link + "' is not valid.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // catch (ParseException e) {
        // e.printStackTrace();
        // }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Event> getListEvents() {
        ArrayList<Event> returnList = new ArrayList<Event>(this.listEvents);
        return returnList;
    }
}