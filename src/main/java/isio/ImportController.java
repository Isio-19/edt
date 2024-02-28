package isio;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImportController {
    @FXML private TextField importLink;

    @FXML
    private void createFile() throws IOException {
        try {
            String link = importLink.getText();
            File RawFile = CalendarParser.createRawFile(link, "temp");
            CalendarParser.createJsonFile(RawFile);  
        } catch (IOException e) {
            System.err.println("File error");
            e.printStackTrace();
        }
    }

    @FXML
    private void importFile() throws IOException {
        Stage stage = (Stage) importLink.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json) or TXT files (*.txt)", "*.json", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;

        if (file.getName().matches("*.txt")) {
            CalendarParser.createJsonFile(file);
        }

        App.setRoot("timeTable");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(new File(App.class.getResource("json/").toURI()).listFiles());
    }
}
