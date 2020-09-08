package evm.view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * General class for the accept screen because the only thing that really needs to change is
 * the name of the ballot. I've created a new class instead of changing an old one because I don't
 * want to break anything/get rid of something important.
 */
public class AcceptView extends AbstractView {

    /**
     * Constructor for the evm.view
     * @param width width of the stage
     * @param height height of the stage
     * @param s the name of the ballot/the message to show while the ballot is printing
     */
    public AcceptView(double width, double height, String s) {
        scene = new Scene(new Group());
        scene.getStylesheets().add("evm/styles/styles.css");

        Text titleLabel = new Text(s);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));


        Button confirmButton = new Button("Ok");
        /*confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // do button stuff here

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

        titleLabel.setLayoutX(width * 0);
        titleLabel.setLayoutY(height * 0.2);

        titleLabel.setWrappingWidth(width * 0.93);
    }
}
