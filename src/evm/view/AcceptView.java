package evm.view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * A generic accept view for any given string s.
 */
public class AcceptView extends AbstractView {

    /**
     * Constructor for the evm.view
     * @param width width of the stage
     * @param height height of the stage
     * @param s the name of the ballot/the message to show while the ballot is printing
     */
    public AcceptView(double width, double height, String s) {
        /* set the root node */
        root = new Group();

        Text titleLabel = new Text(s);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));


        Button confirmButton = new Button("Ok");
        confirmButton.setFont(Font.font("Verdana", FontWeight.BOLD, 30));;

        ((Group) root).getChildren().addAll(confirmButton, titleLabel);

        confirmButton.setPrefWidth(width * 0.20);
        confirmButton.setPrefHeight(height * 0.13);

        confirmButton.setLayoutX(width * 0.78);
        confirmButton.setLayoutY(height * 0.83);

        titleLabel.setLayoutX(width * 0);
        titleLabel.setLayoutY(height * 0.2);

        titleLabel.setWrappingWidth(width * 0.93);
    }
}
