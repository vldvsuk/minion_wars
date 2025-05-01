package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** Controller die de eind fase afhandeld **/
public class EndController {
    @FXML
    private Label winnaarLabel;

    public void setWinnaar(String winnaarNaam) {
        winnaarLabel.setText("Spel werd gewonnen door " + winnaarNaam + "!"); // eind naam label
    }

    @FXML
    private void handleAfsluiten() {  //afsluiten van de spel
        Stage stage = (Stage) winnaarLabel.getScene().getWindow();
        stage.close();
    }
}
