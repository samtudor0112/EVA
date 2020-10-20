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
        List<Ballot> ballots = null;
        try {
            // get ballots
            ballots = ConfigReader.read("config/config.txt");

        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Invalid ballot config");
            Platform.exit();
            System.exit(1);
        }

        HashMap<String, Object> extraData = new HashMap<>();
        extraData.put("Ballot0Type", "Lower House");
        extraData.put("Ballot1Type", "Lower House");
        extraData.put("Ballot2Type", "Lower House");
        extraData.put("Ballot3Type", "Lower House");
        extraData.put("Ballot4Type", "Upper House");
        extraData.put("Ballot5Type", "Upper House");
        extraData.put("Ballot4PartyVotesRequired", 3);
        extraData.put("Ballot5PartyVotesRequired", 3);

        Config config = new Config();

//        List<PublicBallot> publicBallots = Stream.of(ballots).map(ballot -> new PublicBallot(ballot)).toArray();
        List<PublicBallot> publicBallots = new ArrayList<>();
        for (Ballot ballot: ballots) {
            publicBallots.add(new PublicBallot(ballot));
        }

        config.setBallots(publicBallots);
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
    }
}
