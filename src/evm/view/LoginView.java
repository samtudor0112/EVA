package evm.view;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * A generic accept view for any given string s.
 */
public class LoginView extends AbstractView {

    private Button confirmButton;
    /**
     * Constructor for the evm.view
     * @param width width of the stage
     * @param height height of the stage
     * @param s the name of the ballot/the message to show while the ballot is printing
     */
    public LoginView(double width, double height, String s) {
        /* set the root node */
        root = new Group();

        s = "voting machine, don't draw benis";
        Text titleLabel = new Text(s);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));


        Button confirmButton = new Button("Begin voting");
        confirmButton.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

        Image image = new Image("evm/img/e34.png");
        ImageView iv = new ImageView(image);

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

    public Button getConfirmButton() {

        return this.confirmButton;
    }
}
