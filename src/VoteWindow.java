import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class VoteWindow extends Application {

    /* FOR STANDALONE TESTING ONLY */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(new Group());
        stage.setTitle("Place Vote");
        stage.setWidth(620);
        stage.setHeight(600);

        scene.getStylesheets().add("styles/styles.css");

        Text titleLabel = new Text("Place Vote");
        titleLabel.getStyleClass().add("purple-header");

        final HBox titleHbox = new HBox();
        titleHbox.setPadding(new Insets(15, 15, 15, 15));
        titleHbox.setSpacing(10);
        titleHbox.getStyleClass().add("purple-header");
        titleHbox.getChildren().addAll(titleLabel);

        final VBox voteVbox = new VBox();
        voteVbox.setSpacing(5);
        voteVbox.setPadding(new Insets(10, 0, 0, 10));
        voteVbox.getChildren().addAll(titleHbox);

        ((Group) scene.getRoot()).getChildren().addAll(voteVbox);

        stage.setScene(scene);
        stage.show();
    }
}
