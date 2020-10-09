package evm.view;

import evm.Candidate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class SenateView extends AbstractView {

    private double width, height;
    private VBox cards;
    private Map<String, VBox> partyCards;
    private Map<String, VBox> candidateVBoxes;
    private VBox candidateCards;
    private VBox candidateMenu;
    private VBox focussedParty;
    private Button confirmButton;
    private Button clearButton;
    private Button lineButton;

    private Boolean aboveLine;

    /* TODO change this to a Map<evm.Candidate, Integer> ??? */
    private Map<Candidate, Label> preferenceBoxMap;

    private Map<Candidate, HBox> voteCardMap;

    public SenateView(double width, double height, List<String> parties) {
        this.width = width;
        this.height = height;
        this.aboveLine = true;

        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);
        this.root = root;

        Boolean aboveLine = true;


        /** Title Stuff */
        Text titleLabel = new Text("Place vote:");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        //remove
        lineButton = new Button("Above Line");
        lineButton.getStyleClass().add("confirm-button");
        lineButton.setPrefWidth(250);

        HBox titleBox = new HBox(titleLabel);
        titleBox.getChildren().add(lineButton);
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

        for (String partyName : parties) {
            VBox partyCard = new VBox();
            partyCard.getStyleClass().add("vote-card");
            partyCard.setPrefWidth(width / 2);
            partyCard.setMinHeight(85);
            partyCard.setAlignment(Pos.CENTER_LEFT);
            // partyCard.setPadding(new Insets(0, 10, 0, 10));

            Text party = new Text(partyName);
            party.getStyleClass().add("party-card");

            DropShadow cardShadow = new DropShadow();
            cardShadow.setRadius(2.0);
            cardShadow.setOffsetX(1.0);
            cardShadow.setOffsetY(1.0);
            cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
            partyCard.setEffect(cardShadow);

            partyCard.getChildren().add(party);

            cards.getChildren().add(partyCard);

            partyCards.put(partyName, partyCard);
        }

    }

    public void drawCandidateMenus(Map<String, List<Candidate>> theMap) {
        candidateVBoxes = new HashMap<>();

        preferenceBoxMap = new HashMap<>();

        voteCardMap = new HashMap<>();

        for (String party: theMap.keySet()) {
            VBox candidateMenu = new VBox();

            for (Candidate candidate : theMap.get(party)) {
                Label preferenceLabel = new Label();
                //preferenceLabel.setText("1");
                preferenceLabel.getStyleClass().add("preference-label");
                preferenceLabel.setPrefSize(50, 50);

                preferenceBoxMap.put(candidate, preferenceLabel);

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
                voteCard.getChildren().addAll(preferenceLabel, candidateVbox);

                voteCardMap.put(candidate, voteCard);

                // Shadow to make the cards look a bit more pretty and professional
                DropShadow cardShadow = new DropShadow();
                cardShadow.setRadius(2.0);
                cardShadow.setOffsetX(1.0);
                cardShadow.setOffsetY(1.0);
                cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
                voteCard.setEffect(cardShadow);

                candidateMenu.getChildren().add(voteCard);
            }

            candidateMenu.setSpacing(5);
            candidateVBoxes.put(party, candidateMenu);
        }
    }

    public Map<String,VBox> getPartyCards() {
        return partyCards;
    }

    public Map<String, VBox> getCandidateVBoxes() {
        return candidateVBoxes;
    }

    public void partyClick(VBox party, VBox candidate) {
        setCandidateMenu(candidate);
        setFocussedParty(party);
    }

    public void setCandidateMenu(VBox candidates) {
        candidateMenu.getChildren().remove(candidateCards);
        candidateMenu.getChildren().add(candidates);
        candidateCards = candidates;
    }

    public void setFocussedParty(VBox party) {
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
        for (Map.Entry<Candidate, Integer> entry: preferences.entrySet()) {
            String textVote = entry.getValue() == Integer.MAX_VALUE ? " " : Integer.toString(entry.getValue());
            preferenceBoxMap.get(entry.getKey()).setText(textVote);
        }
    }

    /**
     * Getter for the vote card map
     * @return the vote card map
     */
    public Map<Candidate, HBox> getVoteCardMap() {
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
        if (aboveLine) {
            aboveLine = false;
            lineButton.setText("Below Line");
        } else {
            aboveLine = true;
            lineButton.setText("Above Line");
        }
    }
}
