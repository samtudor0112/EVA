import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.List;

/**
 * The main entrypoint to the voting application. Should be called with java Main /path/to/config/file
 * (in our case, config/config.txt should be good)
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // TEMPORARY
        stage.setTitle("Printing...");

        stage.setFullScreen(true);

        stage.setResizable(false);

        stage.show();

        List<Ballot> ballots = null;
        try {
            ballots = ConfigReader.read("config/config.txt");
        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Invalid filepath to ballot config");
            Platform.exit();
            System.exit(1);
        }

        // 
        for (Ballot ballot: ballots) {
            VotingModel model = new VotingModel(ballot);

            // Controller instantiates the view
            Controller controller = new Controller(stage, model);

            // This will only show the last controller I think so that's a problem
            // We also need a way to change the view to keep going to the next screen
            controller.getStage().setScene(controller.getCurrentView().getScene());
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
