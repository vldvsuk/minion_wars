package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EndController {
    @FXML
    private Label winnaarLabel;

    public void setWinnaar(String winnaarNaam) {
        winnaarLabel.setText("Spel werd gewonnen door " + winnaarNaam + "!");
    }

    @FXML
    private void handleAfsluiten() {
        Stage stage = (Stage) winnaarLabel.getScene().getWindow();
        stage.close();
    }
}
