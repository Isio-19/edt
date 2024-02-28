package isio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static File createRawFile(String fileUrl, String fileName) throws IOException {
        try {
            // create the file, if it doesn't exist already
            fileName = "src/main/resources/isio/raw/"+fileName+".txt";
            File file = new File(fileName); 
            if (!file.createNewFile()) {
                file.delete();
                file = new File(fileName);
            }
            
            FileWriter writer = new FileWriter(file);
            
            URL url = new URL(fileUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null)
                writer.write(line+"\n");

            writer.close();

            return file;
        } catch (MalformedURLException e) {
            System.err.println("The given file url '"+fileUrl+"' is not valid.\n");
        }

        return null;
    }

    public static void createJsonFile(File rawFile)throws IOException {        
        BufferedReader reader = new BufferedReader(new FileReader(rawFile));
        
        String fileContent = "";
        String line;
        while((line = reader.readLine()) != null) {
            fileContent += line+"\n";
        }
        reader.close();
        
        // Create a JSON file from the event of the calendar for future use
        ICalendar ical = Biweekly.parse(fileContent).first();
        List<VEvent> events = ical.getEvents();

        JSONArray json = new JSONArray();

        for (VEvent event : events) {
            JSONObject eventObject = new JSONObject();
            
            if (event.getLastModified() != null) eventObject.put("lastModified", event.getLastModified().getValue());
            if (event.getUid() != null) eventObject.put("UId", event.getUid().getValue());
            if (event.getDateStart() != null) eventObject.put("dateStart", event.getDateStart().getValue());
            if (event.getDateEnd() != null) eventObject.put("dateEnd", event.getDateEnd().getValue());
            if (event.getSummary() != null) eventObject.put("summary", event.getSummary().getValue());
            if (event.getLocation() != null) eventObject.put("location", event.getLocation().getValue());

            if (event.getDescription() == null) {
                json.put(eventObject);
                continue;
            }

            // parse desription
            ArrayList<String> description = new ArrayList<>(
                Arrays.asList(event.getDescription().getValue().split("\n"))
            );

            JSONObject descriptionObject = new JSONObject();

            for (String item : description) {
                String[] itemNameContent = item.toString().split(" : ");
                String[] listContent = itemNameContent[1].split(", ");

                if (listContent.length == 1) {
                    descriptionObject.put(itemNameContent[0], itemNameContent[1]);
                    continue;
                }
                
                descriptionObject.put(itemNameContent[0], listContent);
            }

            eventObject.put("description", descriptionObject);
            json.put(eventObject);
        }

        // get the group name for the file
        String groupName = ical.getExperimentalProperties("X-WR-CALNAME").get(0).getValue();
        groupName = org.apache.commons.lang3.StringUtils.substringBetween(groupName, "<", ">");
        groupName = groupName.replace(" ", "_");
        String fileName = "src/main/resources/isio/json/"+groupName+".json";
        
        // save the json 
        FileWriter writer = new FileWriter(fileName);
        json.write(writer, 4, 0);
        writer.close();

        // rename the raw file to be clearer
        File newName = new File("src/main/resources/isio/raw/"+groupName+".txt");
        rawFile.renameTo(newName);
    }
}
