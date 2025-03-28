package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Vittoria {

    @FXML
    private ImageView victory;
    @FXML
    public void initialize() {
        victory.setImage(new Image(new File("PaguriSmasher/src/images/win.jpeg").toURI().toString()));
    }
}
