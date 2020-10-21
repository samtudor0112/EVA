package evm.YAMLpublic;

import evm.Ballot;
import evm.Candidate;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a wrapper class for ballot with zero functionality. It is
 * designed to be printed by the YAML printer.
 */
public class PublicBallot {
    // These are the private fields of Ballots
    public String name;
    public int numCandidates;
    public int numVotesNeeded;
    public List<PublicCandidate> candidateList;
    public String printMsg;

    // "unused" constructor for YAML instantiation - do not remove!
    public PublicBallot(){}

    public PublicBallot(Ballot ballot) {
        this.name = ballot.getName();
        this.numCandidates = ballot.getNumCandidates();
        this.numVotesNeeded = ballot.getNumVotesNeeded();

        // We have to convert our candidates to PublicCandidates
        this.candidateList = new ArrayList<>();
        for (Candidate candidate: ballot.getCandidateList()) {
            candidateList.add(new PublicCandidate(candidate));
        }

        this.printMsg = ballot.getPrintMsg();
    }

    public Ballot getBallot() {
        // Get back
        // We have to construct the original candidate list
        List<Candidate> originalCandidateList = new ArrayList<>();
        for (PublicCandidate candidate: candidateList) {
            originalCandidateList.add(candidate.getCandidate());
        }
        return new Ballot(name, numCandidates, numVotesNeeded, originalCandidateList, printMsg);
    }
}
