package sample;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOver {
    public Stage window ;
    public Main main;
    public GameOver(Stage window,Main main){
        this.window = window;
        this.main = main;
    }
    public void Init(){
        final Stage[] dialog = {new Stage()};

        dialog[0] = new Stage();
        VBox dialogVbox = new VBox();


        /*Label lblGameOver = new Label("Game Over!");
        lblGameOver.setTextAlignment(TextAlignment.CENTER);
        lblGameOver.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, FontPosture.ITALIC, 35));
        lblGameOver.setEffect(new InnerShadow(10, Color.DARKRED));*/
        ImageView iv = new ImageView(new Image(getClass().getResource("/game_over.jpg").toExternalForm()));
        iv.setCursor(Cursor.HAND);
        FadeTransition gameOverAnimation = new FadeTransition(Duration.millis(1500), iv);
        gameOverAnimation.setFromValue(0.1);
        gameOverAnimation.setToValue(1);
        gameOverAnimation.setCycleCount(1);
        gameOverAnimation.setAutoReverse(true);
        gameOverAnimation.play();
        HBox btnsBox = new HBox();
        Button restartBtn = new Button("Restart");
        restartBtn.setOnAction(e->{
            this.main.restart();
            dialog[0].close();
        });
        Button canceltBtn = new Button("Cancel");
        canceltBtn.setOnAction(e->{
            window.close();
        });
        btnsBox.getChildren().addAll(restartBtn,canceltBtn);
        btnsBox.setAlignment(Pos.CENTER_RIGHT);

        dialogVbox.getChildren().add(iv);
        dialogVbox.getChildren().add(btnsBox);


        dialogVbox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVbox);

        dialog[0].initModality(Modality.APPLICATION_MODAL);
        dialog[0].initOwner(window);
        dialog[0].getIcons().add(new Image(getClass().getResource("/game_over_icon.png").toExternalForm()));
        dialog[0].setTitle("Game over");
        dialog[0].resizableProperty().setValue(Boolean.FALSE);
        dialog[0].setScene(dialogScene);
        dialog[0].show();
    }
}
