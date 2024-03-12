package isio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.util.StringUtils;

/**
 * Static CalendarParser class
 * @author Isio19
 */
public final class CalendarParser {
    /**
     * Private constructor of the static CalendarParser class
     */
    private CalendarParser() { }

    public static ArrayList<Calendar> createCalendar(String fileUrl) throws IOException {
        ArrayList<Calendar> calendars = new ArrayList<>();
        try {
            URL url = new URL(fileUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String linkContent = "";
            String line;
            while ((line = reader.readLine()) != null)
                linkContent += line = "\n";

            // Create a JSON file from the event of the calendar for future use
            ICalendar ical = Biweekly.parse(linkContent).first();
            List<VEvent> events = ical.getEvents();
        
            
        } catch (MalformedURLException e) {
            System.err.println("The given file url '"+fileUrl+"' is not valid.\n");
        }

        return calendars;
    }
}
