package evm.view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import evm.view.AbstractView;

public class SenateAcceptView extends AbstractView {

    public SenateAcceptView(double width, double height) {
        scene = new Scene(new Group());

        scene.getStylesheets().add("evm/styles/styles.css");

        Text titleLabel = new Text("Upper house ballot complete, ballot printing...");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));


        Button confirmButton = new Button("Finish");

        /*confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                // exit
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
            }
        });*/
        confirmButton.setFont(Font.font("Verdana", FontWeight.BOLD, 30));;

        ((Group) scene.getRoot()).getChildren().addAll(confirmButton, titleLabel);

        confirmButton.setPrefWidth(width * 0.20);
        confirmButton.setPrefHeight(height * 0.13);

        confirmButton.setLayoutX(width * 0.78);
        confirmButton.setLayoutY(height * 0.83);

        confirmButton.getStyleClass().add("confirm-button");

        titleLabel.setLayoutX(width* 0);
        titleLabel.setLayoutY(height * 0.2);

        titleLabel.setWrappingWidth(width * 0.93);
    }
}
