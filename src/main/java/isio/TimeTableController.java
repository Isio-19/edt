package isio;

import java.io.IOException;
import javafx.fxml.FXML;

public class TimeTableController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}