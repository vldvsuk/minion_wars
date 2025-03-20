package be.ugent.objprog.minionwars;

import Controllers.FXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MinionWars extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MinionWars.class.getResource("start.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Minion Wars");
        stage.setScene(scene);
        stage.setResizable(false);
        FXController controller = fxmlLoader.getController();
        controller.setStage(stage);


        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}