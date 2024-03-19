package isio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

/**
 * Calendar
 */
public class Calendar {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;

    // TODO: change to enum
    private String type;
    private ArrayList<Event> listEvents;

    public Calendar(String link) {
        try {
            URL url = new URL(link);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String linkContent = "";
            String line;
            while ((line = reader.readLine()) != null)
                linkContent += line + "\n";

            ICalendar ical = Biweekly.parse(linkContent).first();

            // set the values of the calendar's attributes
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            startDate = sdf.parse(ical.getExperimentalProperties().get(0).getValue());
            endDate = sdf.parse(ical.getExperimentalProperties().get(1).getValue());
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
                sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

                Date eventStartDate = null;
                if (event.getDateStart() != null) {
                    String date = event.getDateStart().getValue().toString();
                    eventStartDate = sdf.parse(date);
                }

                Date eventEndDate = null;
                if (event.getDateStart() != null) {
                    String date = event.getDateStart().getValue().toString();
                    eventEndDate = sdf.parse(date);
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

                Event eventToAdd = new Event(eventStartDate, eventEndDate, eventLocation, eventSummary, eventDescription);
                listEvents.add(eventToAdd);
                System.out.println(listEvents.size());
            }
        } catch (MalformedURLException e) {
            System.err.println("The given file url '" + link + "' is not valid.\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Event> getListEvents() {
        System.out.println(listEvents.size());
        return listEvents;
    }
}