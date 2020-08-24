import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Model part of the MVC. Controls the currently selected
 * vote for the user and determines if it is a valid vote.
 * We check most of whether a vote is valid as the voting occurs,
 * only waiting to check if enough votes have been placed until the end
 */
public class VotingModel {

    // The map of candidates and their current vote. Integer.MAX_VALUE means not selected
    // We need to never allow dupe votes
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
            deselectVote(candidate);
        }
    }

    /**
     * Try to deselect a candidate, checking if it's a valid deselection first.
     * @param candidate who to unvote for
     * @return whether the deselection is successful
     */
    public boolean tryDeselectVote(Candidate candidate) {
        int currentVoteValue = currentVotes.get(candidate);

        // Check if the candidate has been voted for
        if (currentVoteValue == Integer.MAX_VALUE) {
            return false;
        }

        // Check if this is the last vote placed
        if (getCandidateByVote(currentVoteValue + 1) != null) {
            return false;
        }

        // Do we need more checks?

        deselectVote(candidate);
        return true;
    }

    /**
     * Try to vote for a a candidate, checking if it's a valid vote first.
     * @param candidate who we're voting for
     * @param vote the value of our vote
     * @return whether the vote is successful
     */
    public boolean trySetVote(Candidate candidate, int vote) {
        int currentVoteValue = currentVotes.get(candidate);

        // Check if this candidate has already been voted for
        if (currentVoteValue != Integer.MAX_VALUE) {
            return false;
        }

        // Check if this vote value is not a duplicate
        if (getCandidateByVote(vote) != null) {
            return false;
        }

        // Ensure the previous vote value has been placed
        if (getCandidateByVote(vote - 1) == null) {
            return false;
        }

        // Do we need more checks?

        setVote(candidate, vote);
        return true;
    }

    /**
     * Deselect a candidate from the vote ("unvote" for a candidate)
     * @param candidate who to unvote for
     */
    private void deselectVote(Candidate candidate) {
        setVote(candidate, Integer.MAX_VALUE);
    }

    /**
     * Set the current vote for a candidate to a specific value
     * @param candidate who we're voting for
     * @param vote the value of our vote
     */
    private void setVote(Candidate candidate, int vote) {
        currentVotes.put(candidate, vote);
    }

    // Return the candidate by vote value. Returns null if no candidate has this vote value
    private Candidate getCandidateByVote(int vote) {
        for (Map.Entry<Candidate, Integer> entry: currentVotes.entrySet()) {
            if (entry.getValue() == vote) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Get the highest vote.
     * @return the highest current vote. MAX_VALUE if no votes
     */
    public int getHighestVote() {
        int maxVote = 0;
        for (int vote: currentVotes.values()) {
            if (vote != Integer.MAX_VALUE && vote > maxVote) {
                maxVote = vote;
            }
        }
        return maxVote == 0 ? Integer.MAX_VALUE : maxVote;
    }

    /**
     * Check if the current vote is valid. Most checking occurs during voting
     * so we only check whether enough votes have been placed.
     * @return whether the current vote is valid
     */
    public boolean checkValidVote() {
        return getHighestVote() >= ballot.getNumVotesNeeded();
    }
}
