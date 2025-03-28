package controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Sprite;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Sample{
    @FXML
    private Button bottone1;
    @FXML
    public Button closeButton;
    @FXML
    private ImageView sfondo;
    @FXML
    public void initialize() {
        sfondo.setImage(new Image(new File("PaguriSmasher/src/images/tenor.png").toURI().toString()));
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void startGame() {
        Stage currentStage = (Stage) bottone1.getScene().getWindow();

        // Crea un AnchorPane per il layout
        AnchorPane root = new AnchorPane();

        // Crea un Canvas che occupa tutta la scena
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(currentStage.widthProperty());
        canvas.heightProperty().bind(currentStage.heightProperty());

        // Aggiungi il Canvas all'AnchorPane
        root.getChildren().add(canvas);

        // Crea la scena
        Scene scene1 = new Scene(root);
        currentStage.setScene(scene1);
        currentStage.setTitle("Smash the paguri");
        currentStage.show();
        currentStage.setResizable(true); // Rendi la finestra ridimensionabile
        currentStage.setFullScreen(true); // Imposta la finestra in modalità fullscreen

        // Riproduzione del suono
        String resourceDuck = getClass().getResource("/sounds/fluffingDuck.mp3").toString();
        Media soundDuck = new Media(resourceDuck);
        MediaPlayer mediaPlayerDuck = new MediaPlayer(soundDuck);
        mediaPlayerDuck.play();

        ArrayList<String> input = new ArrayList<>();
        scene1.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code)) input.add(code);
        });
        scene1.setOnKeyReleased(e -> input.remove(e.getCode().toString()));

        // Imposta il font del punteggio in base alla dimensione della finestra
        Font theFont = Font.font("Comic Sans MS", FontWeight.BOLD, currentStage.getHeight() * 0.07);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFont(theFont);
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        // Creazione del piedino e dei nemici
        Sprite piedino = new Sprite();
        piedino.setImage(new Image(new File("PaguriSmasher/src/images/piedino.png").toURI().toString()));
        piedino.setPosition(currentStage.getWidth() * 0.05, currentStage.getHeight() * 0.05);

        ArrayList<Sprite> paguroneList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Sprite pagurone = new Sprite();
            pagurone.setImage(new Image(new File("PaguriSmasher/src/images/pagurone.png").toURI().toString()));
            double px = currentStage.getWidth() * Math.random();
            double py = currentStage.getHeight() * Math.random();
            pagurone.setPosition(px, py);
            paguroneList.add(pagurone);
        }

        long[] lastNanoTime = {System.nanoTime()};
        final int[] score = {0};

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime - lastNanoTime[0]) / 1_000_000_000.0;
                lastNanoTime[0] = currentNanoTime;

                piedino.setVelocity(0, 0);
                if (input.contains("LEFT")) piedino.addVelocity(-350, 0);
                if (input.contains("RIGHT")) piedino.addVelocity(350, 0);
                if (input.contains("UP")) piedino.addVelocity(0, -200);
                if (input.contains("DOWN")) piedino.addVelocity(0, 200);

                piedino.update(elapsedTime);

                Iterator<Sprite> paguroneIter = paguroneList.iterator();
                while (paguroneIter.hasNext()) {
                    Sprite pagurone = paguroneIter.next();
                    if (piedino.intersects(pagurone)) {
                        String resourceClick = getClass().getResource("/sounds/click.mp3").toString();
                        new MediaPlayer(new Media(resourceClick)).play();
                        paguroneIter.remove();
                        score[0]++;

                        if (score[0] == 50) {
                            Task<Void> loadVictoryTask = new Task<>() {
                                @Override
                                protected Void call() {
                                    Platform.runLater(() -> {
                                        try {
                                            Parent root = FXMLLoader.load(getClass().getResource("/views/vittoria.fxml"));
                                            Stage stage = new Stage();
                                            stage.setScene(new Scene(root));
                                            stage.setTitle("Hai vinto!");
                                            stage.setResizable(false);
                                            stage.show();
                                            stage.setFullScreen(true);
                                            currentStage.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    return null;
                                }
                            };
                            new Thread(loadVictoryTask).start();
                        }
                    }
                }

                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                piedino.render(gc);
                for (Sprite pagurone : paguroneList) pagurone.render(gc);

                String pointsText = "Paguri killati 😠: " + score[0];
                gc.fillText(pointsText, canvas.getWidth() * 0.35, canvas.getHeight() * 0.07);
                gc.strokeText(pointsText, canvas.getWidth() * 0.35, canvas.getHeight() * 0.07);
            }
        }.start();
    }

}
