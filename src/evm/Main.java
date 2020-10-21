package evm;

import evm.YAMLpublic.PublicBallot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * The main entrypoint to the voting application. Should be called with java Main /path/to/config/file
 * (in our case, config/config.txt.old should be good)
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

//        List<Ballot> ballots = null;
//        try {
//            // get ballots
//            ballots = ConfigReader.read(getParameters().getRaw().get(1));
//
//        } catch (IOException | IndexOutOfBoundsException e) {
//            System.out.println("Invalid ballot config");
//            Platform.exit();
//            System.exit(1);
//        }

        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream input = null;
        try {
            input = new FileInputStream(new File(getParameters().getRaw().get(1)));
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
            System.exit(1);
        }
        Config config = yaml.load(input);

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


        /** TEMP REPLACE THIS SHIT */
//        List<VotingModel> models = new ArrayList<>();
//        for (Ballot ballot : ballots) {
//            models.add(new VotingModel(ballot));
//        }

        Controller controller = new Controller(stage, models);
        controller.getStage().show();
        /*
        VotingModel model = null;
        VotingModel aboveModel = null;
        VotingModel belowModel = null;
        int index = 0;
        for (Ballot ballot: ballots) {

            // randomize our ballot

            index += 1;

            if(index == 1) {
                ballot.randomize();
                model = new VotingModel(ballot);
            } else if (index == 2) {

                ballot.randomize();
                aboveModel = new VotingModel(ballot);
            } else if (index == 3) {
                belowModel = new VotingModel(ballot);

                // all ballots are now set
                // evm.Controller instantiates the evm.view
                Controller controller = new Controller(stage, model, aboveModel, belowModel);

                // This will only show the last controller I think so that's a problem
                // We also need a way to change the view to keep going to the next screen
                controller.getStage().show();

                // restart counter of 3s
                index = 0;
            }

        }

         */

    }

    public static void main(String[] args) {
        launch(args);
    }
}
