package view.panel;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import models.powers.Power;
import models.parsers.PowerParser;
import view.button.PowerButtonHelper;
import java.util.List;
/** Klasse die Bonus Tab aanmaakt **/
public class BonusTab {
    private final PowerButtonHelper powerButtonHelper;

    public BonusTab(PowerButtonHelper powerButtonHelper) {
        this.powerButtonHelper = powerButtonHelper;
    }

    public Tab createTab() {
        VBox content = new VBox(10);
        content.setPrefWidth(170);

        PowerParser powerParser = new PowerParser();
        List<Power> powers = powerParser.parsePowers();

        for (Power power : powers) {
            Button powerButton = powerButtonHelper.createPowerButton(power);
            content.getChildren().add(powerButton);
        }

        return new Tab("Bonus", content);
    }
}