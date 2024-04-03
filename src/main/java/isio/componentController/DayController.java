package isio.componentController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import isio.App;
import isio.Event;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class DayController {
    @FXML
    private VBox dayLabelHeader;
    @FXML
    private Label dayValue;
    @FXML
    private Label dayLabel;
    @FXML
    private VBox dayVBox;

    private Pane contentPane;

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    public void insertEvents(LocalDate day, List<Event> eventList) throws IOException {
        // change the text of the labels
        dayValue.setText(StringUtils.capitalize(StringUtils.lowerCase(day.getDayOfWeek().toString())));
        dayLabel.setText(
                StringUtils.capitalize(StringUtils.lowerCase(day.getMonth().toString())) + " " + day.getDayOfMonth());

        // inserts the events
        Event tempEvent;

        // with LocalDateTime.now() because there is no event
        LocalDateTime eightThirtyAm = LocalDateTime.now().withHour(8).withMinute(30).withSecond(0)
                .withNano(0);
        LocalDateTime sevenPm = LocalDateTime.now().withHour(19).withMinute(0).withSecond(0)
                .withNano(0);

        if (eventList.size() == 0) {
            makeFillerEvent(eightThirtyAm, sevenPm, dayVBox.getChildren());
            return;
        }

        tempEvent = eventList.get(0);
        eightThirtyAm = tempEvent.getStartDate().withHour(8).withMinute(30).withSecond(0).withNano(0);
        sevenPm = tempEvent.getStartDate().withHour(19).withMinute(0).withSecond(0).withNano(0);
        makeFillerEvent(eightThirtyAm, tempEvent.getStartDate(), dayVBox.getChildren());

        for (int eventIndex = 0; eventIndex < eventList.size(); eventIndex++) {
            Event currentEvent = eventList.get(eventIndex);

            FXMLLoader eventLoader = new FXMLLoader(
                    App.class.getResource("components/event.fxml"));
            dayVBox.getChildren().add(eventLoader.load());

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

            controller.setText(timeString, currentEvent.getLocation(), currentEvent.getDescription());
            controller.setCssClass(currentEvent.getType());
            controller.setDayLabel(dayLabelHeader);
            controller.setContentPane(contentPane);
            controller.setDimensions(startDateTime.until(currentEvent.getEndDate(), ChronoUnit.MINUTES) / 30);

            if (eventIndex < eventList.size() - 1) {
                tempEvent = eventList.get(eventIndex + 1);
                makeFillerEvent(currentEvent.getEndDate(), tempEvent.getStartDate(), dayVBox.getChildren());
            }
        }

        tempEvent = eventList.get(eventList.size() - 1);
        makeFillerEvent(tempEvent.getEndDate(), sevenPm, dayVBox.getChildren());
    }

    private void makeFillerEvent(LocalDateTime start, LocalDateTime end, ObservableList<Node> nodeList)
            throws IOException {
        // get the size of the element in units of 30 minutes
        float size = start.until(end, ChronoUnit.MINUTES) / 30;

        FXMLLoader eventLoader = new FXMLLoader(
                App.class.getResource("components/event.fxml"));
        nodeList.add(eventLoader.load());
        EventController controller = eventLoader.getController();
        controller.setText(null, null, null);
        controller.setCssClass("filler");
        controller.setDayLabel(dayLabelHeader);
        controller.setContentPane(contentPane);
        controller.setDimensions(size);
    }

    public void setDimensions(boolean week) {
        if (week) {
            dayVBox.prefHeightProperty().bind(contentPane.prefHeightProperty());
            dayVBox.prefWidthProperty().bind(contentPane.prefWidthProperty().divide(5));
            return;
        }

        dayVBox.prefHeightProperty().bind(contentPane.prefHeightProperty());
        dayVBox.prefWidthProperty().bind(contentPane.prefWidthProperty());
    }
}
