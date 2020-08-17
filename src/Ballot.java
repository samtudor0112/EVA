import java.util.ArrayList;

/**
 * Represents the rules and candidates for a single ballot paper
 */
public class Ballot {

    // Number of Candidates
    private int numCandidates;

    // How many votes are required for a valid vote
    private int numVotesNeeded;

    // The list of candidates on the ballot
    private ArrayList<Candidate> candidateList;

    /**
     * Parse a file filename to create the ballot
     * format is numCandidates,numVotesNeeded,Candidate,
     * Party,Candidate,Party...
     * @param filename The file to parse
     */
    public Ballot(String filename) {
        // TODO
    }

    /**
     * Getter for numCandidates
     * @return Number of candidates
     */
    public int getNumCandidates() {
        return numCandidates;
    }

    /**
     * Getter for numVotes
     * @return Number of votes required
     */
    public int getNumVotesNeeded() {
        return numVotesNeeded;
    }

    /**
     * Getter for the list of candidates
     * @return The list of candidates
     */
    public ArrayList<Candidate> getCandidateList() {
        return candidateList;
    }
}
