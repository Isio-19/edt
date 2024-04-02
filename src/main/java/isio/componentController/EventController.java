package isio.componentController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EventController {
    @FXML
    private Label mondayLabel;
    @FXML
    private VBox boxVBox;
    @FXML
    private VBox headerVBox;
    @FXML
    private Label timeLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label descriptionLabel;

    public void initialize() {

    }

    public void setText(String time, String location, String description) {
        timeLabel.setText(time);
        locationLabel.setText(location);
        descriptionLabel.setText(description);
    }

    public void setDimensions(float height, HBox hbox, Label label) {
        boxVBox.prefHeightProperty()
                .bind(hbox.heightProperty().subtract(label.getHeight()).divide(21).multiply(height));
        boxVBox.prefWidthProperty().bind(hbox.widthProperty().divide(5));
    }

    public void setCssClass(String string) {
        // unique classes for event
        // neutral-header-color
        // no-border-color
        // neutral-content-color
        // disabled-header-color
        // disabled-content-color
        // accented-header-color
        // accent-border-color
        // transparent-content-color
        
        // default
        headerVBox.getStyleClass().add("neutral-header-color");
        boxVBox.getStyleClass().add("no-border-color");
        boxVBox.getStyleClass().add("neutral-content-color");

        switch (string) {
            case "Ferie":
            case "Vacances":
                headerVBox.getStyleClass().add("disabled-header-color");
                boxVBox.getStyleClass().add("no-border-color");
                boxVBox.getStyleClass().add("disabled-content-color");
                break;

            case "Evaluation":
            case "Rattrapage":
                headerVBox.getStyleClass().add("accented-header-color");
                boxVBox.getStyleClass().add("accent-border-color");
                boxVBox.getStyleClass().add("neutral-content-color");
                break;

            // for fillers
            default:
                boxVBox.getStyleClass().add("transparent-content-color");
                break;
        }
    }
}
