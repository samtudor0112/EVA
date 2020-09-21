package evm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * The main entrypoint to the voting application. Should be called with java evm.Main /path/to/config/file
 * (in our case, config/config.txt should be good)
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // TEMPORARY
        stage.setTitle("Printing...");

        int width = (int) Screen.getPrimary().getBounds().getWidth();
        int height = (int) Screen.getPrimary().getBounds().getHeight();

        stage.setMaximized(true);
        stage.setWidth(width);
        stage.setHeight(height);

        stage.setResizable(false);

        List<Ballot> ballots = null;
        try {
            // get ballots
            ballots = ConfigReader.read(getParameters().getRaw().get(1));

        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Invalid filepath to ballot config");
            Platform.exit();
            System.exit(1);
        }


        for (Ballot ballot: ballots) {

            // randomize our ballot
            ballot.randomize();
            VotingModel model = new VotingModel(ballot);

            // evm.Controller instantiates the evm.view
            Controller controller = new Controller(stage, model);

            // This will only show the last controller I think so that's a problem
            // We also need a way to change the view to keep going to the next screen
            controller.getStage().show();
            // controller.getStage().setScene(controller.getCurrentView().getScene());
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
