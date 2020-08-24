import java.util.ArrayList;
import java.util.List;

/**
 * Represents the rules and candidates for a single ballot paper
 */
public class Ballot {

    // Name of the Ballot
    private String name;

    // Number of Candidates
    private int numCandidates;

    // How many votes are required for a valid vote
    private int numVotesNeeded;

    // The list of candidates on the ballot
    private List<Candidate> candidateList;

    /**
     * Create a new Ballot
     * @param numCandidates the number of candidates on the ballot
     * @param votesNeeded the number of votes needed for a vote on this ballot to be legal
     * @param candidates the list of candidates on the ballot
     */
    public Ballot(String name, int numCandidates, int votesNeeded, List<Candidate> candidates) {
        this.numCandidates = numCandidates;
        this.numVotesNeeded = votesNeeded;
        this.candidateList = candidates;
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
    public List<Candidate> getCandidateList() {
        return candidateList;
    }
}
