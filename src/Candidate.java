/**
 * Represents a single candidate on a ballot
 */
public class Candidate {

    // The candidate's name
    private String name;

    // The candidate's party
    private String party;

    /**
     * Create a candidate from a name and party
     * @param name The name of the candidate
     * @param party The name of the candidate's party
     */
    public Candidate(String name, String party) {
        this.name = name;
        this.party = party;
    }

    /**
     * Getter for the name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the party
     * @return Party
     */
    public String getParty() {
        return party;
    }
}
