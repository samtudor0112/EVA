package evm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The main entry point to the voting application. Should be called with
 * java Main /path/to/config/file
 * (in our case, config/config.yaml should be good)
 */
public class Main extends Application {

    /**
     * Starts the main javafx application. Instantiates the Controller, which
     * manages the main program flow
     * @param stage the javafx stage
     */
    @Override
    public void start(Stage stage) {

        stage.setTitle("I can't think of a joke");

        // Set up the javajx stage
        int width = (int) Screen.getPrimary().getBounds().getWidth();
        int height = (int) Screen.getPrimary().getBounds().getHeight();

        stage.setMaximized(true);
        stage.setWidth(width);
        stage.setHeight(height);

        stage.setResizable(false);

        // Read the config
        Config config;
        try {
            config = Config.readConfig(getParameters().getRaw().get(1));
        } catch (FileNotFoundException e) {
            System.out.println("Invalid config file.");
            Platform.exit();
            System.exit(1);
            // for Java's sake
            return;
        }

        // Make the voting models from the config
        List<VotingModel> models = new ArrayList<>();
        // We can't do a foreach here since we need to access the extraData
        for (int i = 0; i < config.getBallots().size(); i++) {
            VotingModel model;
            // Check the type of the model
            String typeProperty = "Ballot" + i + "Type";
            String type = (String)config.getExtraData().get(typeProperty);
            if (type == null) {
                type = "";
            }

            switch (type) {
                case "Lower House":
                    model = new VotingModel(config.getBallots().get(i).getBallot());
                    break;
                case "Upper House":
                    // Need to get the number of parties required
                    String partyProperty = "Ballot" + i + "PartyVotesRequired";
                    int numPartyVotesRequired = (int)config.getExtraData().get(partyProperty);
                    model = new SenateVotingModel(config.getBallots().get(i).getBallot(), numPartyVotesRequired);
                    break;
                default:
                    // By default it can be Lower House, why not
                    model = new VotingModel(config.getBallots().get(i).getBallot());
                    break;
            }
            models.add(model);
        }

        Controller controller = new Controller(stage, models);
        controller.getStage().show();

    }

    /**
     * Javafx app start
     * @param args commandline args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
