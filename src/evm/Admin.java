package evm;

import evm.YAMLpublic.PublicBallot;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class Admin {

    public static void main(String[] args) {
        readAndWriteUserInputBallot();
    }

    /**
     * Scan stdin and instruct stdout to create a Config instance
     * @return the Config instance to be printed
     */
    private static void readAndWriteUserInputBallot() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the config file maker. For a default value, type nothing then enter.");
        String filePath = getInput(scanner, "Enter a directory for the config file", "config/config.yaml");
        List<PublicBallot> ballots = new ArrayList<>();
            Map<String, Object> extraData = new HashMap<>();
            int numBallots = Integer.parseInt(getInput(scanner, "Enter the number of ballots for the election", "2"));

            // While going through the loop, we'll enter the extra data as soon as we get it but not enter the ballots until they're done
            for (int ballotNum = 0; ballotNum < numBallots; ballotNum++) {
                String ballotName = getInput(scanner, String.format("Enter the name of ballot %d", ballotNum + 1), String.format("Ballot%d", ballotNum + 1));
                String ballotType = getInput(scanner, String.format("Enter the type of %s (\"Lower House\" or \"Upper House\")", ballotName), "Lower House");
                extraData.put("Ballot" + ballotNum + "Type", ballotType);
                int numCandidates = Integer.parseInt(getInput(scanner, String.format("Enter the number of candidates on %s", ballotName), "1"));
                int numCandidatesRequired = Integer.parseInt(getInput(scanner, String.format("Enter the number of candidates required for a valid vote on %s", ballotName), "1"));
                if (ballotType.equals("Upper House")) {
                    int numPartiesRequired = Integer.parseInt(getInput(scanner, String.format("Enter the number of parties required for a valid vote above the line on %s", ballotName), "1"));
                    extraData.put("Ballot" + ballotNum + "PartyVotesRequired", numPartiesRequired);
                }
                String printMsg = getInput(scanner, String.format("Enter the print message of %s", ballotName), "Lower house ballot complete, ballot printing...");

                // Get the candidates
                List<Candidate> candidates = new ArrayList<>();
                for (int candidateNum = 0; candidateNum < numCandidates; candidateNum++) {
                    String candidateName = getInput(scanner, String.format("Enter the name of candidate %d", candidateNum + 1), "Jim Bob");
                    String candidateParty = getInput(scanner, String.format("Enter %s's party", candidateName), "Labour");
                    candidates.add(new Candidate(candidateName, candidateParty));
                }

                // We're done with the ballot, we can construct it, then construct the wrapper then add it to the list
                Ballot ballot = new Ballot(ballotName, numCandidates, numCandidatesRequired, candidates, printMsg);
                PublicBallot publicBallot = new PublicBallot(ballot);
                ballots.add(publicBallot);
        }
        writeConfigToYaml(filePath, new Config(ballots, extraData));
    }

    private static String getInput(Scanner scanner, String msg, String defaultVal) {
        System.out.println(String.format("%s (default: %s): ", msg, defaultVal));
        while(!scanner.hasNextLine()) {}
        String out = scanner.nextLine();
        return out.equals("") ? defaultVal : out;
    }

    /**
     * Converts an old config file into a new Config instance
     * This function is for "backwards compatibility" for our old config format
     * It will assume that every 2nd ballot is an upper house ballot, where
     * the voter only has to vote for 1 party
     * Note: every ballot has the same default print msg
     * @param filePath the file path of the old format config
     * @return the Config instance to be printed
     */
    private static Config readOldConfig(String filePath) {

        // Read the old config
        List<Ballot> ballots;
        try {
            // get ballots
            ballots = ConfigReader.read(filePath);

        } catch (IOException | IndexOutOfBoundsException e) {
            System.out.println("Invalid old ballot");
            return null;
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

        return config;
    }

    // Write a Config instance to the Yaml output file at filePath
    private static void writeConfigToYaml(String filePath, Config config) {
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(config, writer);
        try {
            PrintWriter out = new PrintWriter(filePath);
            out.write(writer.toString());
            out.close();
        } catch (IOException e) {
            System.out.println("Invalid new ballot");
        }
    }
}
