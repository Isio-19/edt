package isio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import isio.componentController.DayController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TimeTableController {
    @FXML
    private VBox monday;
    @FXML
    private VBox tuesday;
    @FXML
    private VBox wednesday;
    @FXML
    private VBox thursday;
    @FXML
    private VBox friday;
    @FXML
    private HBox calendarHBox;

    private List<EventCalendar> calendars;
    private EventCalendar currentCalendar = null;
    private LocalDate today;

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

        today = LocalDate.now();

        // print the events of the selected calendar
        showWeek();
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

    private void showWeek() {
        try {
            if (currentCalendar == null)
                return;

            ArrayList<Event> events = currentCalendar.getListEvents();

            // get the first and last day of the current week
            List<LocalDate> temp = getFirstAndLastDates(today);
            LocalDate firstDay = temp.get(0);
            LocalDate lastDay = temp.get(1);

            LocalDateTime firstDayTime = firstDay.atStartOfDay();
            LocalDateTime lastDayTime = lastDay.atTime(23, 59, 59, 999999999);

            // find the index corresponding to the dates
            int firstDayIndex = 0;
            int lastDayIndex = currentCalendar.getListEvents().size();

            for (int i = 0; i < currentCalendar.getListEvents().size(); i++)
                if (firstDayTime.compareTo(events.get(i).getStartDate()) < 0) {
                    firstDayIndex = i;
                    break;
                }

            for (int i = events.size() - 1; i >= 0; i--)
                if (lastDayTime.compareTo(events.get(i).getStartDate()) > 0) {
                    lastDayIndex = i;
                    break;
                }

            ArrayList<Event> eventsToDisplay = new ArrayList<>();
            for (int i = firstDayIndex; i <= lastDayIndex; i++)
                eventsToDisplay.add(events.get(i));

            ArrayList<Event> mondayEvent = new ArrayList<>();
            ArrayList<Event> tuesdayEvent = new ArrayList<>();
            ArrayList<Event> wednesdayEvent = new ArrayList<>();
            ArrayList<Event> thursdayEvent = new ArrayList<>();
            ArrayList<Event> fridayEvent = new ArrayList<>();

            for (Event event : eventsToDisplay)
                switch (event.getStartDate().getDayOfWeek()) {
                    case MONDAY:
                        mondayEvent.add(event);
                        break;

                    case TUESDAY:
                        tuesdayEvent.add(event);
                        break;

                    case WEDNESDAY:
                        wednesdayEvent.add(event);
                        break;

                    case THURSDAY:
                        thursdayEvent.add(event);
                        break;

                    case FRIDAY:
                        fridayEvent.add(event);
                        break;

                    default:
                        break;
                }

            // display the events for the week
            ArrayList<ArrayList<Event>> eventWeek = new ArrayList<>(
                    Arrays.asList(mondayEvent, tuesdayEvent, wednesdayEvent, thursdayEvent, fridayEvent));
            ArrayList<VBox> week = new ArrayList<>(Arrays.asList(monday, tuesday, wednesday, thursday, friday));

            for (int i = 0; i < week.size(); i++) {
                ArrayList<Event> eventForTheDay = eventWeek.get(i);

                FXMLLoader eventLoader = new FXMLLoader(
                        TimeTableController.class.getResource("components/day.fxml"));
                calendarHBox.getChildren().add(eventLoader.load());
                DayController controller = eventLoader.getController();
                controller.setCalendarHBox(calendarHBox);
                controller.insertEvents(firstDay.plusDays(i), eventForTheDay);
                controller.setDimensions(true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LocalDate> getFirstAndLastDates(LocalDate date) throws ParseException {
        LocalDate firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDayOfWeek = firstDayOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        return Arrays.asList(firstDayOfWeek, lastDayOfWeek);
    }

    @FXML
    public void openAddLinkPopUp() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/importView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 540, 150);
            stage.setTitle("Import ICS link");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
