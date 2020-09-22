package evm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
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
        currentVotes = new HashMap<>();
        deselectAll();
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

    // This might be unnecessary - maybe voteNext / tryVoteNext is all that's required.
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

    /*
     * Vote for the candidate as the next non-voted-for vote
     * (e.g. if 3 candidates have been voted for as 1,2,3, vote for this candidate as 4).
     * @param candidate The candidate to vote for
     */
    private void voteNext(Candidate candidate) {
        int highestVote = getHighestVote();
        if (highestVote != Integer.MAX_VALUE) {
            setVote(candidate, getHighestVote() + 1);
        } else {
            setVote(candidate, 1);
        }
    }
    /**
     * Try to vote for the candidate as the next non-voted-for vote.
     * (e.g. if 3 candidates have been voted for as 1,2,3, vote for this candidate as 4).
     * If the candidate already has a vote, returns false
     * @param candidate The candidate to try to vote for
     */
    public boolean tryVoteNext(Candidate candidate) {
        int currentVoteValue = currentVotes.get(candidate);
        // Check if this candidate has already been voted for
        if (currentVoteValue != Integer.MAX_VALUE) {
            return false;
        }

        voteNext(candidate);
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
     * Returns the map of votes, but only with candidates who have been voted for
     * @return the filtered map
     */
    public Map<Candidate, Integer> getVotedForMap() {
        return currentVotes.entrySet().stream()
                .filter(entry -> entry.getValue() != Integer.MAX_VALUE)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
        if (getHighestVote() == Integer.MAX_VALUE) {
            return false;
        }
        return getHighestVote() >= ballot.getNumVotesNeeded();
    }

    /**
     * Returns an unsorted list of the candidates
     * @return the candidates
     */
    public List<Candidate> getCandidateList() {
        return ballot.getCandidateList();
    }

    /**
     * Returns the whole map of votes, including candidates with no vote with value Integer.MAX_VALUE
     * @return the map of preferences
     */
    public HashMap<Candidate, Integer> getFullMap() {
        return currentVotes;
    }

    /**
     * Deselects all candidates from the vote ("unvotes" for all candidates)
     */
    public void deselectAll() {
        for (Candidate candidate: ballot.getCandidateList()) {
            deselectVote(candidate);
        }
    }

//    private Map<Integer, Candidate> orderMap() {
//        Map<Integer, Candidate> output = new TreeMap<>();
//
//        Set<Candidate> candidates = currentVotes.keySet();
//
//        for (Candidate c : candidates) {
//            if (currentVotes.get(c) == Integer.MAX_VALUE) {
//                continue;
//            }
//            output.put(currentVotes.get(c), c);
//        }
//
//        return output;
//    }
//
//    public ObservableList<Candidate> orderedList() {
//        ObservableList<Candidate> candidates = FXCollections.observableArrayList();
//        Map<Integer, Candidate> order = orderMap();
//        ArrayList<Candidate> allCandidates = new ArrayList<>(currentVotes.keySet());
//
//        for (int index : order.keySet()) {
//            Candidate c = order.get(index);
//            candidates.add(c);
//            allCandidates.remove(c);
//        }
//
//        candidates.addAll(allCandidates);
//
//        return candidates;
//    }

    /**
     * Returns a list of the candidates sorted by their current preference, ascending
     * @return the list of candidates sorted
     */
    public List<Candidate> orderedList() {
        ArrayList<Candidate> candidates = new ArrayList(getCandidateList());
        candidates.sort(Comparator.comparingInt(candidate -> currentVotes.get(candidate)));
        return candidates;
    }

    /**
     * A way to differentiate ballots (Lower house, upper house etc)
     * Not really relevant right now but will be used later on.
     * TODO add multiple ballots - this can be used to store the name of the ballot
     * @return a string message to be displayed when the ballot is confirmed
     */
    public String getBallotString() { return ballot.getPrintMsg(); }
}
