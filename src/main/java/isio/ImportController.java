package isio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ImportController {
    @FXML private TextField importLink;
    @FXML private Text errorText;

    @FXML
    private void addLink() {
        try {
            String fileName = "src/main/resources/isio/icsLinks.txt";
            File file = new File(fileName);
            
            // create the file, in case it doesn't exist
            file.createNewFile();
            
            // check if the link is already in the file
            Boolean isInFile = false;
            String link = importLink.getText();
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(link)) {
                    isInFile = true;
                    break;
                }
            }
            br.close();

            if (isInFile) {
                // update the label to tell the user that the link already exists in the file
                errorText.setText("The link you're trying to import is already in the in database");
                return;
            }

            // write the link into the file
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(writer);

            bw.write(link);
            bw.newLine();
            
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }   

    }

}
