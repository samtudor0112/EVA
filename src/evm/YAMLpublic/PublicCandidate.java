package evm.YAMLpublic;

import evm.Candidate;

/**
 * This class is a wrapper class for candidate with zero functionality. It is
 * designed to be printed by the YAML printer.
 */
public class PublicCandidate {
    // These are the private fields of Candidate
    public String name;
    public String party;

    public PublicCandidate(Candidate candidate) {
        this.name = candidate.getName();
        this.party = candidate.getParty();
    }

    public Candidate getCandidate() {
        return new Candidate(name, party);
    }
}
