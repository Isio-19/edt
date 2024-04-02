package isio.componentController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import isio.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class WeekController {
    @FXML
    private HBox weekContainer;

    public void insertDays(ArrayList<ArrayList<Event>> eventsPerDay, LocalDate firstDay) throws IOException {
        for (int i = 0; i < eventsPerDay.size(); i++) {
            ArrayList<Event> eventForTheDay = eventsPerDay.get(i);

            FXMLLoader eventLoader = new FXMLLoader(getClass().getClassLoader().getResource("components/event.fxml"));

            weekContainer.getChildren().add(eventLoader.load());
            // DayController controller = eventLoader.getController();
            // controller.setCalendarHBox(weekContainer);
            // controller.insertEvents(firstDay.plusDays(i), eventForTheDay);
            // controller.setDimensions(true);
        }
    }
}
