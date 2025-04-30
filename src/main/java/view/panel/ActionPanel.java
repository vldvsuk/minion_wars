package view.panel;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import models.GameState;
import models.powers.Power;
import view.button.PowerButtonHelper;
import java.util.function.Consumer;

public class ActionPanel {
    private Button specialAttackButton;
    private Button healButton;
    private Button stayButton;
    private Button basicAttackButton;
    private final GameState gameState;


    private final Runnable onStayAction;
    private final Runnable onBasicAttackAction;
    private final Runnable onHealAction;
    private final Runnable onSpecialAttackAction;
    private final Consumer<Power> onPowerSelected;
    private final Consumer<String> onTabChanged;
    private final PowerButtonHelper powerButtonHelper;

    public ActionPanel(GameState gameState, Runnable onStayAction,
                       Runnable onBasicAttackAction,
                       Runnable onHealAction,
                       Runnable onSpecialAttackAction,
                       Consumer<Power> onPowerSelected,
                       Consumer<String> onTabChanged) {
        this.gameState = gameState;
        this.onStayAction = onStayAction;
        this.onBasicAttackAction = onBasicAttackAction;
        this.onHealAction = onHealAction;
        this.onPowerSelected = onPowerSelected;
        this.onTabChanged = onTabChanged;
        this.onSpecialAttackAction = onSpecialAttackAction;
        this.powerButtonHelper = new PowerButtonHelper(gameState, onPowerSelected);
    }

    public VBox initializePanel() {
        VBox topVBox = new VBox(10);
        topVBox.setId("topVBox");
        topVBox.setPrefWidth(180);
        topVBox.setAlignment(Pos.TOP_CENTER);
        return topVBox;
    }

    public VBox createLabelBox() {
        VBox labelBox = new VBox();
        labelBox.setAlignment(Pos.CENTER);
        Label label = new Label("Kies een minion!");
        label.setFont(Font.font("System", FontWeight.BOLD, 24));
        labelBox.setMinHeight(100);
        return labelBox;
    }

    public VBox createTabPane() {
        VBox tabBox = new VBox();
        tabBox.setAlignment(Pos.BOTTOM_CENTER);
        tabBox.setPrefHeight(300);
        tabBox.setPrefWidth(180);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMinHeight(600);
        tabPane.setPrefWidth(180);
        Tab bewegenTab = createMovementTab();
        Tab aanvallenTab = createAttackTab();
        Tab bonusTab = createBonusTab();

        tabPane.getTabs().addAll(bewegenTab, aanvallenTab, bonusTab);
        tabBox.getChildren().add(tabPane);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                onTabChanged.accept(newTab.getText());
            }
        });
        return tabBox;
    }

    private Tab createMovementTab() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(170);

        Label label = new Label("Selecteer een groen veld op het spelbord of kies om te blijven staan");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("System", FontWeight.BOLD, 17));
        label.setWrapText(true);
        label.setMaxWidth(160);

        stayButton = new Button("Blijven staan");
        stayButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        stayButton.setOnAction(e -> onStayAction.run());

        content.getChildren().addAll(label, stayButton);
        return new Tab("Bewegen", content);
    }

    private Tab createAttackTab() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(170);

        Label label = new Label("Selecteer een aanval uit de lijst en klik op een vijandelijke minion");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font("System", FontWeight.BOLD, 17));
        label.setWrapText(true);
        label.setMaxWidth(160);

        basicAttackButton = new Button("Basis aanval");
        basicAttackButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        basicAttackButton.setOnAction(e -> onBasicAttackAction.run());

        specialAttackButton = new Button("Speciale aanval");
        specialAttackButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        specialAttackButton.setOnAction(e -> onSpecialAttackAction.run());

        Label orLabel = new Label("of");
        orLabel.setFont(Font.font("System", FontWeight.BOLD, 17));

        healButton = new Button("Genees minion (+2hp)");
        healButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        healButton.setOnAction(e -> onHealAction.run());


        content.getChildren().addAll(
                label,
                basicAttackButton,
                specialAttackButton,
                orLabel,
                healButton
        );

        return new Tab("Aanvallen", new StackPane(content));
    }

    public Tab createBonusTab() {
        return new BonusTab(gameState, onPowerSelected, powerButtonHelper).createTab();
    }

    public void setSpecialAttackVisible(boolean visible) {
        specialAttackButton.setDisable(!visible);
    }

    public void setBasicAndSpecialAttackDisabled(boolean attackDisabled) {
        basicAttackButton.setDisable(attackDisabled);
        specialAttackButton.setDisable(attackDisabled);
    }

    public void setHealDisabled(boolean healDisabled) {
        healButton.setDisable(healDisabled);
    }

    public void setStayButtonDisabled(boolean stayDisabled) {
        stayButton.setDisable(stayDisabled);
    }

    public void updatePowerButtonsStyle() {
        powerButtonHelper.updatePowerButtonsStyle();
    }

}