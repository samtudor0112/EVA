package evm;

import evm.YAMLpublic.PublicBallot;
import javafx.application.Platform;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin {

    public static void main(String[] args) {
        // TODO
    }

    /**
     * Converts an old config file into a new config file
     * This function is for "backwards compatibility" for our old config format
     * It will assume that every 2nd ballot is an upper house ballot, where
     * the voter only has to vote for 1 party
     * Note: every ballot has the same default print msg
     * @param oldFilePath the file path of the old format config
     * @param newFilePath the file path to write the new format config
     */
    public static void convertOldConfig(String oldFilePath, String newFilePath) {

        // Read the old config
        List<Ballot> ballots;
        try {
            // get ballots
            ballots = ConfigReader.read(oldFilePath);

        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Invalid old ballot");
            return;
        }

        // Guess some info
        HashMap<String, Object> extraData = new HashMap<>();
        for (int i = 0; i < ballots.size(); i++) {
            if (i % 2 == 0) {
                extraData.put("Ballot" + i + "Type", "Lower House");
            } else {
                extraData.put("Ballot" + i + "Type", "Upper House");
                extraData.put("Ballot" + i + "PartyVotesRequired", 1);
            }

        }

        // Create the Config instance storing the config
        Config config = new Config();

        List<PublicBallot> publicBallots = new ArrayList<>();
        for (Ballot ballot: ballots) {
            publicBallots.add(new PublicBallot(ballot));
        }

        config.setBallots(publicBallots);
        config.setExtraData(extraData);

        // Write the Config instance to the Yaml output file
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(config, writer);
        try {
            PrintWriter out = new PrintWriter(newFilePath);
            out.write(writer.toString());
            out.close();
        } catch (IOException e) {
            System.out.println("Invalid new ballot");
        }
    }
}
