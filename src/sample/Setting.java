package sample;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Setting {
    public Stage window ;
    public Main main;
    public Setting(Stage window,Main main){
        this.window = window;
        this.main = main;
    }
    public void Init(){
        final Stage[] dialog = {new Stage()};

        dialog[0] = new Stage();
        VBox dialogVbox = new VBox();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField playerSpeedField = new TextField(Integer.toString(Settings.playerSpeed));
        TextField attackSpeedField = new TextField(Integer.toString(Settings.attackSpeed));
        TextField attackIntervalField = new TextField(Integer.toString(Settings.attackInterval));
        grid.add(new Label("Player speed"), 0, 0);
        grid.add(new Label(":"), 1, 0);
        grid.add(playerSpeedField, 2, 0);
        grid.add(new Label("Attack speed"), 0, 1);
        grid.add(new Label(":"), 1, 1);
        grid.add(attackSpeedField, 2, 1);
        grid.add(new Label("Attack interval"), 0, 2);
        grid.add(new Label(":"), 1, 2);
        grid.add(attackIntervalField, 2, 2);


        HBox btnsBox = new HBox();
        Button restartBtn = new Button("Save");
        restartBtn.setOnAction(e->{
            Settings.playerSpeed = (Settings.isLong(playerSpeedField.getText()) && Long.parseLong(playerSpeedField.getText())> 0) ? Integer.parseInt(playerSpeedField.getText()) :Settings.playerSpeed;
            Settings.attackSpeed = (Settings.isLong(attackSpeedField.getText()) && Long.parseLong(attackSpeedField.getText())> 0) ? Integer.parseInt(attackSpeedField.getText()) :Settings.attackSpeed;
            Settings.attackInterval = (Settings.isLong(attackIntervalField.getText()) && Long.parseLong(attackIntervalField.getText())> 0) ? Integer.parseInt(attackIntervalField.getText()) :Settings.attackInterval;
            this.main.restart();
            dialog[0].close();
        });
        Button canceltBtn = new Button("Cancel");
        canceltBtn.setOnAction(e->{
            window.close();
        });
        btnsBox.getChildren().addAll(restartBtn,canceltBtn);
        btnsBox.setAlignment(Pos.CENTER_RIGHT);

        dialogVbox.getChildren().add(grid);
        dialogVbox.getChildren().add(btnsBox);
        dialogVbox.setPadding(new Insets(10));


        dialogVbox.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(dialogVbox);

        dialog[0].initModality(Modality.APPLICATION_MODAL);
        dialog[0].initOwner(window);
        dialog[0].getIcons().add(new Image(getClass().getResource("/settings.png").toExternalForm()));
        dialog[0].setTitle("Settings");
        dialog[0].resizableProperty().setValue(Boolean.FALSE);
        dialog[0].setScene(dialogScene);
        dialog[0].show();
    }
}
