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
        Canvas canvas = new Canvas(1920, 1080);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Stage currentStage = (Stage) bottone1.getScene().getWindow();
        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene1 = new Scene(root);
        currentStage.setScene(scene1);
        currentStage.setTitle("Smash the paguri");
        currentStage.show();
        currentStage.setResizable(false);
        currentStage.setFullScreen(true);
        String resourceDuck = getClass().getResource("/sounds/fluffingDuck.mp3").toString();
        
        Media soundDuck = new Media(resourceDuck);
        MediaPlayer mediaPlayerDuck = new MediaPlayer(soundDuck);

        mediaPlayerDuck.play();

        ArrayList<String> input = new ArrayList<String>();

        scene1.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();
                    if (!input.contains(code))
                        input.add(code);
                });

        scene1.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove(code);
                });

        Font theFont = Font.font("cavolini", FontWeight.BOLD, 80);
        gc.setFont(theFont);
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        Sprite piedino = new Sprite();
        piedino.setImage(new Image(new File("PaguriSmasher/src/images/piedino.png").toURI().toString()));
        piedino.setPosition(1, 1);

        ArrayList<Sprite> paguroneList = new ArrayList<Sprite>();

        for (int i = 0; i < 50; i++) {
            Sprite pagurone = new Sprite();
            pagurone.setImage(new Image(new File("PaguriSmasher/src/images/pagurone.png").toURI().toString()));
            double px = 1700 * Math.random() + 50;
            double py = 800 * Math.random() + 50;
            pagurone.setPosition(px, py);
            paguroneList.add(pagurone);
        }

        // Usa un array per il valore finale di lastNanoTime
        long[] lastNanoTime = {System.nanoTime()};

        // Usa un array per il punteggio
        final int[] score = {0};

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                double elapsedTime = (currentNanoTime - lastNanoTime[0]) / 1000000000.0;
                lastNanoTime[0] = currentNanoTime;  // Modifica il valore dell'array

                piedino.setVelocity(0, 0);
                if (input.contains("LEFT"))
                    piedino.addVelocity(-350, 0);
                if (input.contains("RIGHT"))
                    piedino.addVelocity(350, 0);
                if (input.contains("UP"))
                    piedino.addVelocity(0, -200);
                if (input.contains("DOWN"))
                    piedino.addVelocity(0, 200);

                piedino.update(elapsedTime);

                Iterator<Sprite> paguroneIter = paguroneList.iterator();
                while (paguroneIter.hasNext()) {
                    Sprite pagurone = paguroneIter.next();
                    if (piedino.intersects(pagurone)) {
                    	String resourceClick = getClass().getResource("/sounds/click.mp3").toString();
                        
                        Media soundClick = new Media(resourceClick);
                        MediaPlayer mediaPlayerClick = new MediaPlayer(soundClick);

                        mediaPlayerClick.play();
                        paguroneIter.remove();
                        score[0]++; // Modifica il punteggio nell'array

                        if (score[0] == 50) {
                            // Rimuovi Thread.sleep e usa un Task per il caricamento
                            Task<Void> loadVictoryTask = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                        Platform.runLater(() -> {
                                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/vittoria.fxml"));
                                            Parent root = null;
                                            try {
                                                root = loader.load();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Stage stage = new Stage();
                                            stage.setScene(new Scene(root));
                                            stage.setTitle("Hai vinto!");
                                            stage.setResizable(false);
                                            stage.show();
                                            stage.setFullScreen(true);
                                            currentStage.close();
                                        });
                                    return null;
                                }
                            };

                            new Thread(loadVictoryTask).start();  // Avvia il task in un nuovo thread
                        }
                    }
                }

                gc.clearRect(0, 0, 1920, 1080);
                piedino.render(gc);

                for (Sprite pagurone : paguroneList)
                    pagurone.render(gc);

                String pointsText = "paguri killati 😠: " + score[0]; // Usa score[0] per leggere il punteggio

                gc.fillText(pointsText, 700, 70);
                gc.strokeText(pointsText, 700, 70);
            }
        }.start();
    }

}
