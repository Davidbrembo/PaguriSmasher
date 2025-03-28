package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage theStage) throws Exception {
    	Parent root = FXMLLoader.load(getClass().getResource("/views/Sample.fxml"));

        Scene scene1 = new Scene(root, 1000, 600);
        
        theStage.setScene(scene1);
        theStage.setTitle("Smash the paguri");
        theStage.setResizable(false);
        theStage.show();
    }
}
