package sample;

/**
 * DUMMY CANDIDATE OBJECT FOR TESTING - NOT FOR FINAL USE
 */
public class DummyCandidate {

    private int preference;
    private String candidate;
    private String party;

    /**
     * Candidate in election
     * DUMMY OBJECT FOR TESTING - NOT FOR FINAL USE
     * @param preference        Preference of candidate (through votes)
     * @param candidate         Name of candidate
     * @param party             Name of candidate's party
     */
    public DummyCandidate(int preference, String candidate, String party) {
        this.preference = preference;
        this.candidate = candidate;
        this.party = party;
    }

    public int getPreference() {
        return preference;
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidateName(String candidate) {
        this.candidate = candidate;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }
}
