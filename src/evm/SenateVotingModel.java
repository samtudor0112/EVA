package evm;

import java.util.*;

public class SenateVotingModel extends VotingModel {
    Boolean isAboveLine;

    Ballot aboveLine;
    Ballot belowLine;

    Map<String, Candidate> partyNameCandidate;

    /**
     * Creates a new voting model from a given ballot
     *
     * @param ballot The ballot of candidates
     */
    public SenateVotingModel(Ballot ballot) {
        super(ballot);
        partyNameCandidate = new HashMap<>();

        belowLine = ballot;
        aboveLine = makePartyBallot();

        isAboveLine = true;
        setBallot(aboveLine);
    }


    private Ballot makePartyBallot() {
        List<String> parties = this.getParties();

        List<Candidate> candidates = new ArrayList<>();

        for (String party : parties) {
            Candidate partyCandidate = new Candidate(party, "");
            candidates.add(partyCandidate);
            partyNameCandidate.put(party, partyCandidate);
        }

        return new Ballot("Above Line", candidates.size(), candidates.size(), candidates);
    }

    public void switchBallot() {
        if (isAboveLine) {
            setBallot(belowLine);
        } else {
            setBallot(aboveLine);
        }

        isAboveLine = !isAboveLine;
    }

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

    public Ballot getAboveLine() {
        return aboveLine;
    }

    public void setAboveLine(Ballot aboveLine) {
        this.aboveLine = aboveLine;
    }

    public Ballot getBelowLine() {
        return belowLine;
    }

    public void setBelowLine(Ballot belowLine) {
        this.belowLine = belowLine;
    }

}
