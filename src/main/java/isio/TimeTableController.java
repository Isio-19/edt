package isio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import isio.componentController.EventController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    @FXML
    private Label mondayLabel;

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
                Event tempEvent;

                LocalDateTime eightThirtyAm = LocalDateTime.now().withHour(8).withMinute(30).withSecond(0)
                .withNano(0);
                LocalDateTime sevenPm = LocalDateTime.now().withHour(19).withMinute(0).withSecond(0)
                .withNano(0);

                if (eventForTheDay.size() == 0) {
                    makeFillerEvent(eightThirtyAm, sevenPm, week.get(i).getChildren());
                    continue;
                }

                tempEvent = eventForTheDay.get(0);
                makeFillerEvent(eightThirtyAm, tempEvent.getStartDate(), week.get(i).getChildren());

                for (int eventIndex = 0; eventIndex < eventForTheDay.size(); eventIndex++) {
                    Event currentEvent = eventForTheDay.get(eventIndex);

                    FXMLLoader eventLoader = new FXMLLoader(
                            TimeTableController.class.getResource("components/event.fxml"));
                    week.get(i).getChildren().add(eventLoader.load());

                    EventController controller = eventLoader.getController();

                    LocalDateTime startDateTime = currentEvent.getStartDate();
                    String hour = "" + startDateTime.getHour();
                    if (hour.length() == 1)
                        hour = "0" + hour;

                    String minute = "" + startDateTime.getMinute();
                    if (minute.length() == 1)
                        minute = "0" + minute;

                    String timeString = hour + ":" + minute;

                    if (currentEvent.getType() != null)
                        timeString += " / " + currentEvent.getType();

                    controller.setTime(timeString);
                    controller.setLocation(currentEvent.getLocation());
                    controller.setDescription(currentEvent.getDescription());
                    controller.setCssClass(currentEvent.getType());
                    controller.setDimensions(startDateTime.until(currentEvent.getEndDate(), ChronoUnit.MINUTES) / 30, calendarHBox, mondayLabel);

                    if (eventIndex < eventForTheDay.size() - 1) {
                        tempEvent = eventForTheDay.get(eventIndex+1);
                        makeFillerEvent(currentEvent.getEndDate(), tempEvent.getStartDate(), week.get(i).getChildren());
                    }
                }

                tempEvent = eventForTheDay.get(eventForTheDay.size() - 1);
                makeFillerEvent(tempEvent.getEndDate(), sevenPm, week.get(i).getChildren());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeFillerEvent(LocalDateTime start, LocalDateTime end, ObservableList<Node> nodeList)
            throws IOException {
        // get the size of the element in units of 30 minutes
        float size = start.until(end, ChronoUnit.MINUTES) / 30;

        FXMLLoader eventLoader = new FXMLLoader(
                TimeTableController.class.getResource("components/event.fxml"));
        nodeList.add(eventLoader.load());
        EventController controller = eventLoader.getController();
        controller.setTime(null);
        controller.setLocation(null);
        controller.setDescription(null);
        controller.setCssClass("filler");
        controller.setDimensions(size, calendarHBox, mondayLabel);
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
