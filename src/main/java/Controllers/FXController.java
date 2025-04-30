package Controllers;

import be.ugent.objprog.minionwars.MinionWars;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.parsers.XmlLoader;

import java.io.IOException;

public class FXController {
    @FXML
    private TextField speler1Field; // Speler 1

    @FXML
    private TextField speler2Field; // Speler 2

    @FXML
    private TextField muntenField; // Munten


    @FXML
    private Label speler1ErrorLabel;

    @FXML
    private Label speler2ErrorLabel;

    @FXML
    private Label muntenErrorLabel;

    @FXML
    private Stage stage;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize(String configPath) {
        XmlLoader.setConfigPath(configPath);
    }


    @FXML
    protected void onStartButtonClick() throws IOException {
        String naamSpeler1 = speler1Field.getText();
        String naamSpeler2 = speler2Field.getText();
        String muntenText = muntenField.getText();

        boolean fout = false;

        speler1ErrorLabel.setOpacity(0);
        speler2ErrorLabel.setOpacity(0);
        muntenErrorLabel.setOpacity(0);

        try {
            if (naamSpeler1.isEmpty()) {
                speler1ErrorLabel.setOpacity(1);
                fout = true;
            }
            if (naamSpeler2.isEmpty()) {
                speler2ErrorLabel.setOpacity(1);
                fout = true;
            }

            int munten = Integer.parseInt(muntenText);
            if (munten < 10 || munten > 50) {
                muntenErrorLabel.setText("Munten moeten tussen 10 en 50 zijn!");
                muntenErrorLabel.setOpacity(1);
                fout = true;
            }

        } catch (NumberFormatException e) {
            muntenErrorLabel.setText("Voer een geldig getal in voor munten!");
            muntenErrorLabel.setOpacity(1);
            fout = true;
        }

        if (!fout) {
            FXMLLoader fxmlLoader = new FXMLLoader(MinionWars.class.getResource("SpelBegint.fxml"));
            Scene nieuweScene = new Scene(fxmlLoader.load(), 1600, 900);
            stage.setScene(nieuweScene);

            Controller2 controller2 = fxmlLoader.getController();
            controller2.setInfo(naamSpeler1, naamSpeler2, Integer.parseInt(muntenText));
            stage.centerOnScreen();
            stage.setResizable(true);
        }

    }
}







