package isio;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PrimaryController {
    @FXML private TextField importLink;

    @FXML
    private void createFile() throws IOException {
        String link = importLink.getText();
        try {
            File RawFile = CalendarParser.createRawFile(link, "M1-AI-Classique");
            CalendarParser.createJsonFile(RawFile);  
        } catch (IOException e) {
            System.err.println("File error");
            e.printStackTrace();
        }
    }
}
