package evm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

/**
 * The main entrypoint to the voting application. Should be called with java Main /path/to/config/file
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
            System.out.println("Invalid ballot config");
            Platform.exit();
            System.exit(1);
        }

        HashMap<String, Object> extraData = new HashMap<>();
        extraData.put("Ballot1Type", SenateVotingModel.class);
        extraData.put("Ballot2Type", SenateVotingModel.class);
        extraData.put("Ballot3Type", SenateVotingModel.class);
        extraData.put("Ballot4Type", SenateVotingModel.class);
        extraData.put("Ballot2PartyVotesRequired", 3);
        extraData.put("Ballot3PartyVotesRequired", 3);
        extraData.put("Ballot4PartyVotesRequired", 3);

        Config config = new Config();

        config.setBallots(ballots);
        config.setExtraData(extraData);

        // Test
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(config, writer);
        try {
            PrintWriter out = new PrintWriter("test.yaml");
            out.write(writer.toString());
            out.close();
        } catch (IOException e) {
            System.out.println("yeet");
        }

        /** TEMP REPLACE THIS SHIT */
        List<VotingModel> models = new ArrayList<>();
        for (Ballot ballot : ballots) {
            models.add(new VotingModel(ballot));
        }

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
