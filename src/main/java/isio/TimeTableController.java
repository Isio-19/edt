package isio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TimeTableController {
    @FXML private VBox monday;
    @FXML private VBox tuesday;
    @FXML private VBox wednesday;
    @FXML private VBox thursday;
    @FXML private VBox friday;

    private List<EventCalendar> calendars;
    private EventCalendar currentCalendar = null;

    @FXML
    public void initialize() {
        // get the ics links from the icsLinks file
        ArrayList<String> links = (ArrayList<String>) readLinksFile();

        // build calendar objects from the links
        calendars = new ArrayList<EventCalendar>();
        for (String link : links)
            calendars.add(new EventCalendar(link));

        // select first calendar
        if (calendars.size() > 0)
            currentCalendar = calendars.get(0);

        // print the events of the selected calendar
        showCurrentCalendar();
    }

    private List<String> readLinksFile() {
        List<String> returnList = new ArrayList<String>();

        try {
            String fileName = "src/main/resources/isio/icsLinks.txt";
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null)
                returnList.add(line);

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnList;
    }

    private void showCurrentCalendar() {
        try {
            if (currentCalendar == null)
                return;

            ArrayList<Event> events = currentCalendar.getListEvents();

            // get the first and last day of the current week
            List<LocalDate> temp = getFirstAndLastDates(LocalDate.now());
            LocalDate firstDay = temp.get(0);
            LocalDate lastDay = temp.get(1);

            LocalDateTime firstDayTime = firstDay.atStartOfDay();    
            LocalDateTime lastDayTime = lastDay.atTime(23, 59, 59, 999999999);    

            // find the index corresponding to the dates
            int firstDayIndex = 0;
            int lastDayIndex = currentCalendar.getListEvents().size();

            for (int i=0; i<currentCalendar.getListEvents().size(); i++)
                if ( firstDayTime.compareTo(events.get(i).getStartDate()) < 0) {
                    firstDayIndex = i;                    
                    break;
                }
            
            for (int i=events.size()-1; i>=0; i--)
                if ( lastDayTime.compareTo(events.get(i).getStartDate()) > 0) {
                    lastDayIndex = i;                    
                    break;
                }

            ArrayList<Event> eventsToDisplay = new ArrayList<>();
            int index = firstDayIndex;
            for (int i=0; i<5; i++) {
                ArrayList<Event> listEvents = new ArrayList<>();
                for (int tempI = index; tempI<lastDayIndex+1; tempI++) {
                    listEvents.add(events.get(tempI));
                }
                eventsToDisplay.add(events.get());
            }


            // display the events for the week


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private List<LocalDate> getFirstAndLastDates(LocalDate date) throws ParseException {
        LocalDate firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDayOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        return Arrays.asList(firstDayOfWeek, lastDayOfWeek);
    }

    @FXML
    public void openAddLinkPopUp() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("importView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 540, 150);
            stage.setTitle("Import ICS link");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
