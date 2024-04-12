package isio.componentController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import isio.Event;
import isio.TimeTableController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class WeekController {
    @FXML
    private HBox weekContainer;

    private Pane contentPane;

    public void setContentContainer(Pane contentPane) {
        this.contentPane = contentPane;
    }

    public void insertDays(ArrayList<ArrayList<Event>> eventsPerDay, LocalDate firstDay) throws IOException {
        for (int i = 0; i < eventsPerDay.size(); i++) {
            ArrayList<Event> eventForTheDay = eventsPerDay.get(i);

            FXMLLoader eventLoader = new FXMLLoader(TimeTableController.class.getResource("components/day.fxml"));

            weekContainer.getChildren().add(eventLoader.load());
            DayController controller = eventLoader.getController();
            controller.setContentPane(weekContainer);
            controller.insertEvents(firstDay.plusDays(i), eventForTheDay);
            controller.setDimensions(true);
        }
    }

    public void setDimensions() {
        weekContainer.prefHeightProperty().bind(contentPane.prefHeightProperty());
        weekContainer.prefWidthProperty().bind(contentPane.prefWidthProperty());
    }
}
