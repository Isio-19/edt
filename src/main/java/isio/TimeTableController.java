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

import org.apache.commons.lang3.StringUtils;

import isio.componentController.DayController;
import isio.componentController.WeekController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TimeTableController {
    @FXML
    private VBox root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private HBox buttonHBox;
    @FXML
    private Pane contentPane;
    @FXML
    private VBox settingPane;
    @FXML
    private Label contentLabel;

    private List<EventCalendar> calendars;
    private EventCalendar currentCalendar = null;
    private LocalDate today;

    // TODO: make into an enum
    private String displayMode = "week";

    @FXML
    public void initialize() {
        contentPane.prefHeightProperty().bind(
                root.heightProperty().subtract(menuBar.prefHeightProperty()).subtract(buttonHBox.prefHeightProperty()));
        contentPane.prefWidthProperty().bind(root.widthProperty().subtract(settingPane.prefWidthProperty()));

        // get the ics links from the icsLinks file
        ArrayList<String> links = (ArrayList<String>) readLinksFile();

        // TODO: add buttons in the menuBar for every calendar

        // build calendar objects from the links
        calendars = new ArrayList<EventCalendar>();
        for (String link : links)
            calendars.add(new EventCalendar(link));

        // select first calendar
        if (calendars.size() > 0)
            currentCalendar = calendars.get(0);

        today = LocalDate.now();

        // print the events of the selected calendar depending on the display type
        // TODO: get the saved displayType depending on the user
        // displayType = getDisplayType()

        displayCalendar();
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

    private void resetContentPane() {
        contentPane.getChildren().clear();
    }

    private void displayCalendar() {
        try {
            resetContentPane();
            switch (displayMode) {
                case "day":
                    showDay();
                    break;
                case "week":
                    showWeek();
                    break;
                case "month":
                    showMonth();
                    break;
                default:
                    break;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void showDay() throws IOException {
        if (currentCalendar == null)
            return;

        ArrayList<Event> events = currentCalendar.getListEvents();

        // set the label to the correct value
        String tempText = StringUtils.capitalize(today.getMonth().toString().toLowerCase()) + " "
                + today.getDayOfMonth();
        contentLabel.setText("Time table of " + tempText);

        // get the events for today
        ArrayList<Event> eventForTheDay = new ArrayList<>();
        for (Event event : events)
            if (event.getStartDate().toLocalDate() == today)
                eventForTheDay.add(event);

        FXMLLoader eventLoader = new FXMLLoader(
                App.class.getResource("components/day.fxml"));
        contentPane.getChildren().add(eventLoader.load());
        DayController controller = eventLoader.getController();
        controller.setContentPane(contentPane);
        controller.insertEvents(today, eventForTheDay);
        controller.setDimensions(false);
    }

    private void showWeek() throws IOException, ParseException {
        if (currentCalendar == null)
            return;

        ArrayList<Event> events = currentCalendar.getListEvents();

        // get the first and last day of the current week
        List<LocalDate> temp = getFirstAndLastDates(today);
        LocalDate firstDay = temp.get(0);
        LocalDate lastDay = temp.get(1);

        // set the label to the correct values
        String tempString = "";
        tempString += StringUtils.capitalize(firstDay.getMonth().toString().toLowerCase()) + " "
                + firstDay.getDayOfMonth() + " to ";
        tempString += StringUtils.capitalize(lastDay.getMonth().toString().toLowerCase()) + " "
                + lastDay.getDayOfMonth();
        contentLabel.setText("Time table from " + tempString);

        LocalDateTime firstDayTime = firstDay.atStartOfDay();
        LocalDateTime lastDayTime = lastDay.atTime(23, 59, 59, 999999999);

        // find the index corresponding to the dates
        int firstDayIndex = 0;
        int lastDayIndex = currentCalendar.getSize();

        for (int i = 0; i < currentCalendar.getSize(); i++)
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

        FXMLLoader eventLoader = new FXMLLoader(
                App.class.getResource("components/week.fxml"));
        contentPane.getChildren().add(eventLoader.load());
        WeekController controller = eventLoader.getController();
        controller.setContentContainer(contentPane);
        controller.insertDays(eventWeek, firstDay);
        controller.setDimensions();
    }

    private void showMonth() {

    }

    private List<LocalDate> getFirstAndLastDates(LocalDate date) throws ParseException {
        LocalDate firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDayOfWeek = firstDayOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        return Arrays.asList(firstDayOfWeek, lastDayOfWeek);
    }

    @FXML
    public void previousDate() {
        switch (displayMode) {
            case "day":
                today = today.minusDays(1);
                break;
            case "week":
                today = today.minusWeeks(1);
                break;
            case "month":
                today = today.minusMonths(1);
                break;

            default:
                break;
        }

        displayCalendar();
    }

    @FXML
    public void nextDate() {
        switch (displayMode) {
            case "day":
                today = today.plusDays(1);
                break;
            case "week":
                today = today.plusWeeks(1);
                break;
            case "month":
                today = today.plusMonths(1);
                break;
        
            default:
                break;
        }

        displayCalendar();
}

    // TODO: intergrate the dateSelector in the FXMl to interact with LocalDate
    // today

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
