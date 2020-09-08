package evm;

import javafx.scene.image.Image;

public class CandidateCard {

    private int currentPreference;
    private String candidate;
    private String party;
    private Image partyIcon;

    /**
     * Card for each candidate in election, to be shown on the voting screen
     * @param currentPreference     This candidate's current vote
     * @param candidate             evm.Candidate's name
     * @param party                 evm.Candidate's party
     * @param partyIcon             evm.Candidate party icon
     */
    public CandidateCard(int currentPreference, String candidate, String party, Image partyIcon) {

    }

    public int getCurrentPreference() {
        return currentPreference;
    }

    public void setCurrentPreference(int currentPreference) {
        this.currentPreference = currentPreference;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public Image getPartyIcon() {
        return partyIcon;
    }

    public void setPartyIcon(Image partyIcon) {
        this.partyIcon = partyIcon;
    }
}
