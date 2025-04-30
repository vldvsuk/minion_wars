package be.ugent.objprog.minionwars;

import Controllers.FXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MinionWars extends Application {
    private static String configPath;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MinionWars.class.getResource("start.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        FXController controller = fxmlLoader.getController();
        controller.initialize(configPath);
        controller.setStage(stage);
        stage.setTitle("Minion Wars");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            configPath = args[0]; // Eerste argument is het pad
        }
        launch();
    }
}