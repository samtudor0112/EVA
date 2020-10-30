package evm;

import java.util.*;

public class SenateVotingModel extends VotingModel {

    /* whether the ballot and votes for this VotingModel are currently above or below the line */
    private Boolean isAboveLine;

    /* The two ballots corresponding to above and below the line */
    private Ballot aboveLine;
    private Ballot belowLine;

    /* Map of party names to their corresponding Candidate object */
    private Map<String, Candidate> partyNameCandidateMap;

    /**
     * Creates a new Senate voting model from a given (below the line) ballot
     * Automatically generates the above the line ballot.
     * @param ballot The ballot of candidates
     * @param numVotesParties the number of parties required to be selected
     *                        for a above the line valid vote
     */
    public SenateVotingModel(Ballot ballot, int numVotesParties) {
        super(ballot);
        partyNameCandidateMap = new HashMap<>();

        belowLine = ballot;
        aboveLine = makePartyBallot(numVotesParties);

        isAboveLine = true;
        setBallot(aboveLine);
    }


    /**
     * Generates a above the line ballot, where the Candidates have
     * field name=party and field party blank
     * @param numVotesParties the number of parties required to be selected
     *                        for a above the line valid vote
     * @return the above the line ballot
     */
    private Ballot makePartyBallot(int numVotesParties) {
        List<String> parties = this.getParties();

        List<Candidate> candidates = new ArrayList<>();

        for (String party : parties) {
            Candidate partyCandidate = new Candidate(party, "");
            candidates.add(partyCandidate);
            partyNameCandidateMap.put(party, partyCandidate);
        }

        return new Ballot(belowLine.getName(), candidates.size(), numVotesParties, candidates, belowLine.getPrintMsg());
    }

    /**
     * Switch the ballot between above and below the line
     */
    public void switchBallot() {
        if (isAboveLine) {
            setBallot(belowLine);
        } else {
            setBallot(aboveLine);
        }

        isAboveLine = !isAboveLine;
    }

    /**
     * Gets a map of party names to a list of candidates in that party
     * @return the map of party names to list of candidates
     */
    public Map<String, List<Candidate>> getCandidatesByParty() {
        Map<String, List<Candidate>> candidatesByParty = new HashMap<>();

        for (Candidate candidate : belowLine.getCandidateList()) {
            String party = candidate.getParty();
            if (!candidatesByParty.containsKey(party)) {
                candidatesByParty.put(party, new ArrayList<>());
            }
            candidatesByParty.get(party).add(candidate);
        }

        return candidatesByParty;
    }

    /**
     * Returns a the comprehensive list of parties for all the candidates
     * below the line
     * @return the list of parties
     */
    public List<String> getParties() {
        List<String> parties = new ArrayList<>();

        for (Candidate candidate : belowLine.getCandidateList()) {
            String party = candidate.getParty();

            if (!parties.contains(party)) {
                parties.add(party);
            }
        }

        Collections.sort(parties);
        return parties;
    }

    /**
     * Getter for aboveLine ballot
     * @return the aboveLine ballot
     */
    public Ballot getAboveLine() {
        return aboveLine;
    }

    /**
     * Sets aboveLine to a new ballot
     * @param aboveLine the new ballot
     */
    public void setAboveLine(Ballot aboveLine) {
        this.aboveLine = aboveLine;
    }

    /**
     * Getter for belowLine ballot
     * @return the belowLine ballot
     */
    public Ballot getBelowLine() {
        return belowLine;
    }

    /**
     * Sets belowLine to a new ballot
     * @param belowLine the new ballot
     */
    public void setBelowLine(Ballot belowLine) {
        this.belowLine = belowLine;
    }

    /**
     * Getter for isAboveLine
     * @return whether the model is above the line
     */
    public Boolean getIsAboveLine() {
        return isAboveLine;
    }

    /**
     * Getter for the partyNameCandidateMap
     * @return the partyNameCandidateMap
     */
    public Map<String, Candidate> getPartyNameCandidateMap() {
        return partyNameCandidateMap;
    }
}
