package evm.view;

import evm.Candidate;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import evm.view.AbstractView;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The evm.view implementing the main voting screen.
 */
public class PrototypeSenateVoteWindowView extends AbstractView {

    // currentState == 0 : above line
    // currentState == 1 : below line
    private int currentState = 0;

    private int VOTE_TABLE_COLUMNS = 2;

    public GridPane votePane;

    public GridPane partyPane;

    private Button aboveButton;

    private Button belowButton;

    private Button confirmButton;

    private Button clearButton;

    private double width;

    private double height;



    public ScrollPane scrolly;

    /* TODO change this to a Map<evm.Candidate, Integer> ??? */
    private Map<Candidate, Label> preferenceBoxMap;

    private Map<Candidate, HBox> voteCardMap;

    private Map<Candidate, Label> partyPreferenceBoxMap;
    private Map<Candidate, HBox> partyVoteCardMap;

    private TreeMap<String, Integer> partyPositions;

    /**
     * Instantiate the vote window from a stage of size width by height.
     * Sets up some of the ui elements (the static ones), but not the candidate
     * @param width the width of the javafx stage
     * @param height the height of the javafx stage
     */
    public PrototypeSenateVoteWindowView(double width, double height) {

        this.width = width;
        this.height = height;

        /* set the root node */
        BorderPane root = new BorderPane();
        this.root = root;

        aboveButton = new Button("Above line");
        belowButton = new Button("Below line");

//        aboveButton.setOnAction(actionEvent -> {
//
//            setAboveLine();
//        });
//
//        belowButton.setOnAction(actionEvent -> {
//
//            setBelowLine();
//        });

        // create hbox with above/below options at top of page
        HBox optionBox = new HBox(aboveButton, belowButton);
        optionBox.setPrefWidth(width);
        optionBox.setSpacing(5);
        optionBox.setPadding(new Insets(0, 5, 0, 5));
        Text titleLabel = new Text("Place vote:");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        HBox titleBox = new HBox(titleLabel);
        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(width);

        VBox topBox = new VBox(optionBox, titleBox);
        topBox.setPrefWidth(width);

        votePane = new GridPane();
        votePane.setPrefWidth(width);
        votePane.setHgap(5);
        votePane.setVgap(5);
        votePane.setPadding(new Insets(0, 5, 0, 5));

        partyPane = new GridPane();
        partyPane.setPrefWidth(width);
        partyPane.setHgap(5);
        partyPane.setVgap(5);
        partyPane.setPadding(new Insets(0, 5, 0, 5));

        // Populating the votePane now occurs in drawCandidateCards

        // Spacer between vote options and buttons
        Region spacer = new Region();

        // Button pane
        clearButton = new Button("Clear all");
        confirmButton = new Button("Confirm");

        clearButton.getStyleClass().add("cancel-button");
        confirmButton.getStyleClass().add("confirm-button");

        HBox buttonRow = new HBox(clearButton, confirmButton);
        buttonRow.setPrefWidth(200);
        buttonRow.setSpacing(5);
        buttonRow.setPadding(new Insets(0, 5, 0, 5));

        clearButton.setPrefWidth((width - 20) / 2);
        confirmButton.setPrefWidth((width - 20) / 2);

        clearButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        vbox.getChildren().add(partyPane);
        vbox.getChildren().add(votePane);

        root.setTop(topBox);

        root.setBottom(buttonRow);

        scrolly = new ScrollPane();
        scrolly.setContent(vbox);
        scrolly.pannableProperty().set(true);
        scrolly.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrolly.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.setCenter(scrolly);
    }

    /**
     * Draws the candidate cards from a list of candidates. Also populates the voteCardMap and preferenceBoxMap.
     * @param candidateList the list of candidates to draw
     */
    public void drawCandidateCards(List<Candidate> candidateList, boolean canVoteFor) {
        votePane.getChildren().clear();
        // Each "evm.Candidate" object is assigned a TextArea, which can be changed when
        // user changes their vote
        preferenceBoxMap = new HashMap<>();

        // Each "evm.Candidate" object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        voteCardMap = new HashMap<>();

        // number of candidates written to screen for each party
        Map<String, Integer> partyCandidates = new HashMap<>();
        partyPositions = new TreeMap<>();

        ArrayList<String> parties = new ArrayList<>();

        // get and sort parties
        for(int i = 0; i < candidateList.size(); i++) {

            if(!parties.contains(candidateList.get(i).getParty())) {
                partyCandidates.put(candidateList.get(i).getParty(), 0);
                parties.add(candidateList.get(i).getParty());
            }


            Collections.shuffle(parties);

            double newWidth = Math.max(parties.size() * 0.5, 1.0);
            newWidth = newWidth * width;
            votePane.setPrefWidth(newWidth);
        }
        // sort maybe (if u want)

        // get position of each party
        for(int i = 0; i < parties.size(); i++) {

            partyPositions.put(parties.get(i), i);
        }

        // Iterates through all the candidates and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            Label preferenceLabel = new Label();
            if (canVoteFor) {
                //preferenceLabel.setText("1");
                preferenceLabel.getStyleClass().add("preference-label");
                preferenceLabel.setPrefSize(50, 50);

                // Here, the evm.Candidate is assigned a TextArea
                // (the box with the preference number inside)
                preferenceBoxMap.put(candidateList.get(i), preferenceLabel);
            }

            Text candidateName = new Text(candidateList.get(i).getName());
            Text candidateParty = new Text(candidateList.get(i).getParty());

            candidateName.getStyleClass().add("candidate-name");
            candidateParty.getStyleClass().add("party-name");

            /* TODO check wrapping for longer party names */
            // Wrap the name and party text labels so it doesn't squash other vote card elements
            // MaGiC NuMbErS, just leave these,
            //candidateName.setWrappingWidth(200);
            //candidateParty.setWrappingWidth(250);

            VBox candidateVbox = new VBox();
            candidateVbox.getChildren().addAll(candidateName, candidateParty);
            candidateVbox.getStyleClass().add("vote-candidate-display");
            candidateVbox.setPadding(new Insets(0, 10, 0, 10));

            HBox voteCard = new HBox();
            voteCard.setPrefWidth(width/2);
            voteCard.getStyleClass().add("vote-card");
            if (canVoteFor) {
                voteCard.getChildren().addAll(preferenceLabel, candidateVbox);
            } else {
                voteCard.getChildren().addAll(candidateVbox);
            }


            // Shadow to make the cards look a bit more pretty and professional
            DropShadow cardShadow = new DropShadow();
            cardShadow.setRadius(2.0);
            cardShadow.setOffsetX(1.0);
            cardShadow.setOffsetY(1.0);
            cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
            voteCard.setEffect(cardShadow);

            // We also assign each candidate a vote "card" (just an HBox)
            voteCardMap.put(candidateList.get(i), voteCard);
            //VOTE_TABLE_COLUMNS = 3;
            //votePane.add(voteCard, i % VOTE_TABLE_COLUMNS, i / VOTE_TABLE_COLUMNS);

            // each column is a party
            // each row is a candidate
            String party = candidateList.get(i).getParty();
            int col = partyPositions.get(party);
            int row = partyCandidates.get(party);
            partyCandidates.put(party, row + 1);
            votePane.add(voteCard, col, row);


        }
    }

    /**
     * Draws the party cards from a list of candidates. Also populates the voteCardMap and preferenceBoxMap.
     * @param candidateList the list of candidates to draw
     */
    public void drawPartyCards(List<Candidate> candidateList, boolean canVoteFor) {
        partyPane.getChildren().clear();
        // Each "evm.Candidate" object is assigned a TextArea, which can be changed when
        // user changes their vote
        partyPreferenceBoxMap = new HashMap<>();

        // Each "evm.Candidate" object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        partyVoteCardMap = new HashMap<>();

        partyPane.setPrefWidth(votePane.getWidth());

        // Iterates through all the candidates and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            Label preferenceLabel = new Label();
            if (canVoteFor) {
                preferenceLabel.setText("1");
                preferenceLabel.getStyleClass().add("preference-label");
                preferenceLabel.setPrefSize(50, 50);

//              Here, the evm.Candidate is assigned a TextArea
//              (the box with the preference number inside)
                partyPreferenceBoxMap.put(candidateList.get(i), preferenceLabel);
            }
//
//            Text candidateName = new Text(candidateList.get(i).getName());
            Text candidateParty = new Text(candidateList.get(i).getParty());

            // Note this is reversed
//            candidateName.getStyleClass().add("candidate-name");
            candidateParty.getStyleClass().add("candidate-name");

            /* TODO check wrapping for longer party names */
            // Wrap the name and party text labels so it doesn't squash other vote card elements
            // MaGiC NuMbErS, just leave these,
            //candidateName.setWrappingWidth(200);
            //candidateParty.setWrappingWidth(250);

            VBox candidateVbox = new VBox();
            candidateVbox.getChildren().addAll(candidateParty);
            candidateVbox.getStyleClass().add("vote-candidate-display");
            candidateVbox.setPadding(new Insets(0, 10, 0, 10));

            HBox voteCard = new HBox();
            voteCard.setPrefWidth(width/2);
            voteCard.getStyleClass().add("vote-card");
            if (canVoteFor) {
                voteCard.getChildren().addAll(preferenceLabel, candidateVbox);
            } else {
                voteCard.getChildren().addAll(candidateVbox);
            }

            // Shadow to make the cards look a bit more pretty and professional
            DropShadow cardShadow = new DropShadow();
            cardShadow.setRadius(2.0);
            cardShadow.setOffsetX(1.0);
            cardShadow.setOffsetY(1.0);
            cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
            voteCard.setEffect(cardShadow);

            // We also assign each candidate a vote "card" (just an HBox)
            partyVoteCardMap.put(candidateList.get(i), voteCard);

            //VOTE_TABLE_COLUMNS = 3;
            //votePane.add(voteCard, i % VOTE_TABLE_COLUMNS, i / VOTE_TABLE_COLUMNS);

            // each column is a party
            // each row is a candidate
            String party = candidateList.get(i).getParty();
            int col = partyPositions.get(party);
            partyPane.add(voteCard, col, 0);


        }
    }

    /**
     * Sets the text of each candidate's label according to the preferences map
     * @param preferences the map of preferences
     */
    public void setCandidatePreferences(Map<Candidate, Integer> preferences) {
        for (Map.Entry<Candidate, Integer> entry: preferences.entrySet()) {
            String textVote = entry.getValue() == Integer.MAX_VALUE ? " " : Integer.toString(entry.getValue());
            if (getCurrentState() == 1) {
                preferenceBoxMap.get(entry.getKey()).setText(textVote);
            } else {
                partyPreferenceBoxMap.get(entry.getKey()).setText(textVote);
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

    /**
     * Getter for the part vote card map
     * @return the party vote card map
     */
    public Map<Candidate, HBox> getPartyVoteCardMap() {
        return partyVoteCardMap;
    }

    /**
     * Getter for the preference box map
     * @return the preference box map
     */
    public Map<Candidate, Label> getpreferenceBoxMap() {
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

    public int getCurrentState() { return this.currentState; }

    private void setCurrentState(int newState) {

        this.currentState = newState;
    }
    /**
     * Swaps to showing below the line voting
     */
    public void setBelowLine() {


        scrolly.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // below the line is state 1
        setCurrentState(1);
    }

    /**
     * Swaps to showing above the line voting
     */
    public void setAboveLine() {

        // set the displayed voting model as

        scrolly.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        // above the line is state 0
        setCurrentState(0);
    }

    public Button getAboveButton() {

        return aboveButton;
    }

    public Button getBelowButton() {

        return belowButton;
    }
}
