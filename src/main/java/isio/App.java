package isio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        ArrayList<File> files = new ArrayList<>();
        if (App.class.getResource("json/") != null) {
            files = new ArrayList<File>(Arrays.asList(
                new File(App.class.getResource("json/").toURI()).listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.getName().endsWith(".json");
                    }
                })
            ));
        }

        scene = new Scene(loadFXML("timeTable"), 640, 480);
        if (files.size() < 1) {
            scene = new Scene(loadFXML("importView"), 640, 480);
        }
        stage.setScene(scene);
        stage.show();
    }
    
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws URISyntaxException {
        launch();
    }

}