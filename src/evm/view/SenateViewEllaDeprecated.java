package evm.view;

import evm.Candidate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* !!!!DEPRECATED!!!! */
public class SenateViewEllaDeprecated extends AbstractView {

    private double width, height;
    private VBox cards;

    //a map of party names to cards
    private Map<String, HBox> partyCards;
    //a map of party names to expand buttons
    private Map<String, Button> partyExpand;

    private Map<HBox,Candidate> candidateVoteCard;

    private Map<String, VBox> candidateVBoxes;
    private VBox candidateCards;
    private VBox candidateMenu;
    // the currently clicked party
    private HBox focussedParty;

    private Button confirmButton;
    private Button clearButton;
    private Button lineButton;

    private Boolean aboveLine;

    /* TODO change this to a Map<evm.Candidate, Integer> ??? */
    private Map<Candidate, Label> preferenceBoxMap;

    private Map<String, Label> partyPreferenceBoxMap;

    private Map<Candidate, HBox> voteCardMap;

    public SenateViewEllaDeprecated(double width, double height, List<String> parties) {
        this.width = width;
        this.height = height;
        this.aboveLine = true;

        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);
        this.root = root;


        /** Title Stuff */
        Text titleLabel = new Text("Place vote:");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        //remove
        lineButton = new Button("Above Line");
        lineButton.getStyleClass().add("confirm-button");
        lineButton.setPrefWidth(250);

        HBox titleBox = new HBox(titleLabel, region, lineButton);
        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(width);

        titleBox.setSpacing(5);
        titleBox.setPadding(new Insets(0, 50, 0, 50));

        root.setTop(titleBox);



        /** Body Stuff */

        HBox partyMenu = new HBox();
        candidateMenu = new VBox();

        partyMenu.setPrefWidth(width/2);
        candidateMenu.setPrefWidth(width/2);

        HBox body = new HBox();
        body.getChildren().addAll(partyMenu, candidateMenu);

        root.setCenter(body);

        //party cards
        cards = new VBox();
        drawPartyMenu(parties);
        cards.setSpacing(5);

        //candidate cards
        candidateCards = new VBox();
        candidateMenu.getChildren().add(candidateCards);

        //Make scrollpane and set candidates
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(cards);
        scroll.pannableProperty().set(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        partyMenu.getChildren().add(scroll);

        /** bottom stuff */
        // Button pane
        clearButton = new Button("Clear all");
        confirmButton = new Button("Confirm");

        clearButton.getStyleClass().add("cancel-button");

        confirmButton.getStyleClass().add("confirm-button-grey");


        HBox buttonRow = new HBox(clearButton, confirmButton);
        root.setBottom(buttonRow);
        buttonRow.setPrefWidth(200);
        buttonRow.setSpacing(5);
        buttonRow.setPadding(new Insets(0, 5, 0, 5));

        clearButton.setPrefWidth((width - 20) / 2);
        confirmButton.setPrefWidth((width - 20) / 2);

        clearButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

    }

    public void drawPartyMenu(List<String> parties) {
        partyCards = new HashMap<>();

        partyExpand = new HashMap<>();

        partyPreferenceBoxMap = new HashMap<>();

        for (String partyName : parties) {

            VBox partyCard = new VBox();
            partyCard.setAlignment(Pos.CENTER_LEFT);
            partyCard.setPadding(new Insets(0, 10, 0, 10));

            //expand button
            Button expand = new Button("→");
            expand.setPrefSize(100, 100);
            expand.getStyleClass().add("confirm-button");

            Text party = new Text(partyName);
            party.getStyleClass().add("party-card");

            partyCard.getChildren().add(party);

            HBox voteCard = new HBox();
            voteCard.setPrefWidth(width/2 - 100);
            voteCard.setMinHeight(85);
            voteCard.getStyleClass().add("vote-card");
            voteCard.getChildren().addAll(partyCard);

            DropShadow cardShadow = new DropShadow();
            cardShadow.setRadius(2.0);
            cardShadow.setOffsetX(1.0);
            cardShadow.setOffsetY(1.0);
            cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
            voteCard.setEffect(cardShadow);

            HBox card = new HBox();
            card.getChildren().addAll(voteCard, expand);

            cards.getChildren().add(card);

            partyCards.put(partyName, voteCard);

            partyExpand.put(partyName, expand);
        }

        addPartyPreferenceBoxes();

    }

    public void drawCandidateMenus(Map<String, List<Candidate>> theMap) {
        candidateVBoxes = new HashMap<>();

        candidateVoteCard = new HashMap<>();

        preferenceBoxMap = new HashMap<>();

        voteCardMap = new HashMap<>();

        for (String party: theMap.keySet()) {
            VBox candidateMenu = new VBox();

            for (Candidate candidate : theMap.get(party)) {

                Text candidateName = new Text(candidate.getName());
                Text candidateParty = new Text(candidate.getParty());

                candidateName.getStyleClass().add("candidate-name");
                candidateParty.getStyleClass().add("party-name");

                VBox candidateVbox = new VBox();
                candidateVbox.getChildren().addAll(candidateName, candidateParty);
                candidateVbox.getStyleClass().add("vote-candidate-display");
                candidateVbox.setPadding(new Insets(0, 10, 0, 10));

                HBox voteCard = new HBox();
                voteCard.setPrefWidth(width/2);
                voteCard.setMinHeight(85);
                voteCard.getStyleClass().add("vote-card");
                voteCard.getChildren().addAll(candidateVbox);

                voteCardMap.put(candidate, voteCard);

                // Shadow to make the cards look a bit more pretty and professional
                DropShadow cardShadow = new DropShadow();
                cardShadow.setRadius(2.0);
                cardShadow.setOffsetX(1.0);
                cardShadow.setOffsetY(1.0);
                cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
                voteCard.setEffect(cardShadow);

                candidateMenu.getChildren().add(voteCard);
                candidateVoteCard.put(voteCard, candidate);
            }

            candidateMenu.setSpacing(5);
            candidateVBoxes.put(party, candidateMenu);
        }

    }

    public Map<String,HBox> getPartyCards() {
        return partyCards;
    }

    public Map<String,Button> getPartyExpand() {
        return partyExpand;
    }

    public Map<String, VBox> getCandidateVBoxes() {
        return candidateVBoxes;
    }

    public void partyClick(HBox party, VBox candidate) {
        setCandidateMenu(candidate);
        setFocussedParty(party);
    }

    public void setCandidateMenu(VBox candidates) {
        candidateMenu.getChildren().remove(candidateCards);
        candidateMenu.getChildren().add(candidates);
        candidateCards = candidates;
    }

    public void setFocussedParty(HBox party) {
        if (focussedParty != null) {
            focussedParty.getStyleClass().clear();
            focussedParty.getStyleClass().add("vote-card");
        }
        party.getStyleClass().clear();
        party.getStyleClass().add("focus-vote-card");
        focussedParty = party;
    }

    /**
     * Sets the text of each candidate's label according to the preferences map
     * @param preferences the map of preferences
     */
    public void setCandidatePreferences(Map<Candidate, Integer> preferences) {
        if (aboveLine) {
            for (Map.Entry<Candidate, Integer> entry : preferences.entrySet()) {
                String textVote = entry.getValue() == Integer.MAX_VALUE ? " " : Integer.toString(entry.getValue());
                partyPreferenceBoxMap.get(entry.getKey().getName()).setText(textVote);
            }
        } else {
            for (Map.Entry<Candidate, Integer> entry : preferences.entrySet()) {
                String textVote = entry.getValue() == Integer.MAX_VALUE ? " " : Integer.toString(entry.getValue());
                preferenceBoxMap.get(entry.getKey()).setText(textVote);
            }
        }
    }

    /**
     * Getter for the vote card map
     * @return the vote card map
     */
    public Map<Candidate, HBox> getVoteCardMap() {
        return voteCardMap;
    }

    public Map<Candidate, HBox> getPartyVoteCardMap() {
        return voteCardMap;
    }

    /**
     * Getter for the preference box map
     * @return the preference box map
     */
    public Map<Candidate, Label> getPreferenceBoxMap() {
        return preferenceBoxMap;
    }

    /**
     * Getter for the clear button
     * @return the clear button
     */
    public Button getClearButton() {
        return clearButton;
    }

    /**
     * Getter for the confirm button
     * @return the confirm button
     */
    public Button getConfirmButton() {
        return confirmButton;
    }

    public void setConfirmButtonColor() {
        confirmButton.getStyleClass().clear();
        confirmButton.getStyleClass().add("confirm-button");
    }

    public void setConfirmButtonGrey() {
        confirmButton.getStyleClass().clear();
        confirmButton.getStyleClass().add("confirm-button-grey");
    }

    public Button getLineButton() {
        return lineButton;
    }

    public void clickButton() {
        changeVoting();
        if (aboveLine) {
            aboveLine = false;
            lineButton.setText("Below Line");
        } else {
            aboveLine = true;
            lineButton.setText("Above Line");
        }
    }

    public void changeVoting() {
        if (aboveLine) {
            removePartyPreferenceBoxes();
            addCandidatePreferenceBoxes();
        } else {
            addPartyPreferenceBoxes();
            removeCandidatePreferenceBoxes();
        }
    }

    public void addPartyPreferenceBoxes() {
        for (String party: partyCards.keySet()) {
            Label preferenceLabel = new Label();
            preferenceLabel.getStyleClass().add("preference-label");
            preferenceLabel.setPrefSize(50, 50);

            partyCards.get(party).getChildren().add(0, preferenceLabel);

            partyPreferenceBoxMap.put(party, preferenceLabel);
        }
    }

    public void removePartyPreferenceBoxes() {
        partyPreferenceBoxMap.clear();
        for (String party: partyCards.keySet()) {
            partyCards.get(party).getChildren().remove(0);
        }
    }

    public void addCandidatePreferenceBoxes() {
        for (String party: candidateVBoxes.keySet()) {

            for (Node voteCard: candidateVBoxes.get(party).getChildren()) {
                Label preferenceLabel = new Label();
                preferenceLabel.getStyleClass().add("preference-label");
                preferenceLabel.setPrefSize(50, 50);

                HBox child = (HBox) voteCard;
                child.getChildren().add(0, preferenceLabel);

                preferenceBoxMap.put(candidateVoteCard.get(child), preferenceLabel);
            }

        }
    }

    public void removeCandidatePreferenceBoxes() {
        preferenceBoxMap.clear();

        for (String party: candidateVBoxes.keySet()) {
            for (Node voteCard: candidateVBoxes.get(party).getChildren()) {

                HBox child = (HBox) voteCard;
                child.getChildren().remove(0);

            }
        }
    }


}
