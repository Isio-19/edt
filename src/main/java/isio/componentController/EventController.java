package isio.componentController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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

    public void setTime(String string) {
        timeLabel.setText(string);
    }

    public void setLocation(String string) {
        locationLabel.setText(string);
    }

    public void setDescription(String string) {
        descriptionLabel.setText(string);
    }

    public void setDimensions(float height, HBox hbox, Label label) {
        boxVBox.prefHeightProperty().bind(hbox.heightProperty().subtract(label.getHeight()).divide(21).multiply(height));
        boxVBox.prefWidthProperty().bind(hbox.widthProperty().divide(5));
    }

    public void setCssClass(String string) {
        switch (string) {
            case "TP":
            case "TD":
            case "CM":
            case "CM/TP":
            case "CM/TD":
            case "CM distanciel":
            case "Oraux":
            case "Reservation de salles":
            case "Rentree specifique":
                boxVBox.getStyleClass().add("neutral-border-color");
                headerVBox.getStyleClass().add("neutral-color");
                break;

            case "Evaluation":
            case "Rattrapage":
                boxVBox.getStyleClass().add("accent-border-color");
                headerVBox.getStyleClass().add("accent-color");
                break;

            case "Ferie":
            case "Vacances":
                boxVBox.getStyleClass().add("disabled-border-color");
                headerVBox.getStyleClass().add("disabled-color");
                break;

            default:
                break;
        }
    }
}
