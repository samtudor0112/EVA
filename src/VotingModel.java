import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * The Model part of the MVC. Controls the currently selected
 * vote for the user and determines if it is a valid vote.
 */
public class VotingModel {

    // The map of candidates and their current vote. -1 means not selected
    private HashMap<Candidate, Integer> currentVotes;

    // The ballot to vote on
    private Ballot ballot;

    /**
     * Creates a new voting model from a given ballot
     * @param ballot The ballot of candidates
     */
    public VotingModel(Ballot ballot) {
        this.ballot = ballot;

        // Add all the candidates to the map of votes as not selected
        for (Candidate candidate: ballot.getCandidateList()) {
            currentVotes.put(candidate, -1);
        }
    }

    // Deselect a candidate from the vote ("unvote" for a candidate)
    public void deselectVote(Candidate candidate) {
        setVote(candidate, -1);
    }

    // Set the current vote for a candidate
    public void setVote(Candidate candidate, int vote) {
        currentVotes.put(candidate, vote);
    }

    // Check if the current vote is valid
    public boolean checkValidVote() {
        // TODO
    }
}
