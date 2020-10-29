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
 * The first view of the program. Displays the logo and a "Begin voting" button
 */
public class LoginView extends AbstractView {

    private Button confirmButton;
    /**
     * Instantiates the login view
     * @param width width of the javafx stage
     * @param height height of the javafx stage
     */
    public LoginView(double width, double height) {
        /* set the root node */
        root = new Group();

        Button confirmButton = new Button("Begin voting");
        confirmButton.setFont(Font.font("Roboto", FontWeight.BOLD, 30));

        Image logo = new Image(new File("img/our-logo.png").toURI().toString());
        ImageView iv = new ImageView(logo);

        ((Group) root).getChildren().addAll(confirmButton, iv);

        confirmButton.setPrefWidth(width * 0.20);
        confirmButton.setPrefHeight(height * 0.13);

        confirmButton.setLayoutX(width * 0.4);
        confirmButton.setLayoutY(height * 0.7);

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
