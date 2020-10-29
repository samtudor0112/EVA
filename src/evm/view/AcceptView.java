package evm.view;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;

/**
 * A generic view which displays the logo, a given string s and an OK button.
 */
public class AcceptView extends AbstractView {

    private Button confirmButton;
    /**
     * Instantiate the accept view
     * @param width width of the javafx stage
     * @param height height of the javafx stage
     * @param s the message to display in the view
     */
    public AcceptView(double width, double height, String s) {
        /* set the root node */
        root = new Group();

        Text titleLabel = new Text(s);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));

        Button confirmButton = new Button("OK");
        confirmButton.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

        Image logo = new Image(new File("img/our-logo.png").toURI().toString());
        ImageView iv = new ImageView(logo);

        ((Group) root).getChildren().addAll(confirmButton, titleLabel, iv);

        confirmButton.setPrefWidth(width * 0.20);
        confirmButton.setPrefHeight(height * 0.13);

        confirmButton.setLayoutX(width * 0.78);
        confirmButton.setLayoutY(height * 0.83);

        titleLabel.setLayoutX(width * 0.15);
        titleLabel.setLayoutY(height * 0.6);

        titleLabel.setWrappingWidth(width * 0.7);

        iv.setFitWidth(width * 0.4);
        iv.setFitHeight(width * 0.2);

        iv.setX(width * 0.3);
        iv.setY(height * 0.2);

        this.confirmButton = confirmButton;
    }

    /**
     * Getter for the confirm button
     * @return the confirm button
     */
    public Button getConfirmButton() {
        return this.confirmButton;
    }
}
