package evm;

import java.util.Collections;
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

    // The string to print on the accept screen
    private String printMsg;

    /**
     * Create a new Ballot
     * @param numCandidates the number of candidates on the ballot
     * @param votesNeeded the number of votes needed for a vote on this ballot to be legal
     * @param candidates the list of candidates on the ballot
     */
    public Ballot(String name, int numCandidates, int votesNeeded, List<Candidate> candidates, String printMsg) {
        this.name = name;
        this.numCandidates = numCandidates;
        this.numVotesNeeded = votesNeeded;
        this.candidateList = candidates;
        this.printMsg = printMsg;
    }

    /**
     * Randomizes order of candidates on ballot
     */
    public void randomize() {

        Collections.shuffle(candidateList);
    }

    /**
     * Getter for name
     * @return name of the ballot
     */
    public String getName() {
        return name;
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

    /**
     * Getter for the print msg for the accept screen
     * @return a string to display on the print screen
     */
    public String getPrintMsg() { return printMsg; }
}
