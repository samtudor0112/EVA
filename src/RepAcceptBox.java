import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class RepAcceptBox extends Application {
    @Override



    public void start(Stage stage) throws Exception {



        Scene scene = new Scene(new Group());
        stage.setTitle("Printing...");

        stage.setFullScreen(true);

        scene.getStylesheets().add("styles/styles.css");

        Text titleLabel = new Text("Lower house ballot complete, ballot printing...");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));


        Button confirmButton = new Button("Ok");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                // end current window and start senate voting
            }
        });
        confirmButton.setFont(Font.font("Verdana", FontWeight.BOLD, 30));;

        ((Group) scene.getRoot()).getChildren().addAll(confirmButton, titleLabel);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        double WIDTH = stage.getWidth();
        double HEIGHT = stage.getHeight();

        confirmButton.setPrefWidth(WIDTH * 0.20);
        confirmButton.setPrefHeight(HEIGHT * 0.13);

        confirmButton.setLayoutX(WIDTH * 0.78);
        confirmButton.setLayoutY(HEIGHT * 0.83);


        titleLabel.setLayoutX(WIDTH * 0);
        titleLabel.setLayoutY(HEIGHT * 0.2);

        confirmButton.getStyleClass().add("confirm-button");
        titleLabel.setWrappingWidth(WIDTH * 0.93);

    }

}
