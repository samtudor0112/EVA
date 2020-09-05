import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ConfigReader is a class that reader the config text file and loads the
 * ballots and candidates in to program.
 */
public class ConfigReader {

    /**
     * Reads the config file in format of
     * numBallots
     * name:numCandidates:numVotesNeeded:Candidate~Party
     *
     * 1
     * Senate:1:1:yeet~labor
     *
     * @param filepath - the filepath of the config file
     * @return - A list of ballot objects read from the config file
     */
    public static List<Ballot> read(String filepath) throws IOException {
        List<Ballot> ballots = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));

        String line = reader.readLine();

        int numBallots = Integer.parseInt(line);

        for (int i = 0; i < numBallots; i++) {
            line = reader.readLine();
            ballots.add(readBallot(line));
        }
        return ballots;
    }


    // Private, no javadoc needed ;)
    private static Ballot readBallot(String line) {
        int numCandidates, minPrefs;
        List<Candidate> candidates = new ArrayList<>();

        String[] parts = line.split(":", 4);

        String name = parts[0];
        numCandidates = Integer.parseInt(parts[1]);
        minPrefs = Integer.parseInt(parts[2]);

        String[] candidatesRaw = parts[3].split("\\|");
        for (int i = 0; i < numCandidates; i++) {
            candidates.add(readCandidate(candidatesRaw[i]));
        }

        return new Ballot(name, numCandidates ,minPrefs, candidates);
    }

    private static Candidate readCandidate(String candidate) {
        String[] parts = candidate.split("~");
        if (parts.length != 2) {
            throw new RuntimeException();
        }

        return new Candidate(parts[0], parts[1]);
    }

}
