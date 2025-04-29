package view.panel;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import models.GameState;
import models.powers.Power;
import models.parsers.PowerParser;
import view.button.PowerButtonHelper;

import java.util.List;
import java.util.function.Consumer;

public class BonusTab {
    private final GameState gameState;
    private final Consumer<Power> onPowerSelected;
    private final PowerButtonHelper powerButtonHelper;

    public BonusTab(GameState gameState, Consumer<Power> onPowerSelected, PowerButtonHelper powerButtonHelper) {
        this.gameState = gameState;
        this.onPowerSelected = onPowerSelected;
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