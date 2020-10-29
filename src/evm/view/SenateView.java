package evm.view;

import evm.Arrow;
import evm.Candidate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import evm.view.AbstractView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

/**
 * The evm.view implementing the main voting screen.
 */
public class SenateView extends AbstractView {

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

    private Button helpButton;

    private StackPane stack;

    private ImageView leftArrow;

    private ImageView rightArrow;

    private double width;

    private double height;

    private Text titleLabel;

    ArrayList<String> parties = null;

    public ScrollPane scrolly;

    /* TODO change this to a Map<evm.Candidate, Integer> ??? */
    private Map<Candidate, Label> preferenceBoxMap;

    private Map<Candidate, HBox> voteCardMap;

    private Map<Candidate, Label> partyPreferenceBoxMap;
    private Map<Candidate, HBox> partyVoteCardMap;

    private TreeMap<String, Integer> partyPositions;

    public Text topLabel = null;

    /**
     * Instantiate the vote window from a stage of size width by height.
     * Sets up some of the ui elements (the static ones), but not the candidate
     * @param width the width of the javafx stage
     * @param height the height of the javafx stage
     */
    public SenateView(double width, double height, String ballotName) {

        this.width = width;
        this.height = height;

        /* set the root node */
        BorderPane root = new BorderPane();
        this.root = root;

        aboveButton = new Button("Above line");
        belowButton = new Button("Below line");

        aboveButton.getStyleClass().add("confirm-button");
        belowButton.getStyleClass().add("confirm-button");

        // create hbox with above/below options at top of page
//        HBox optionBox = new HBox(aboveButton, belowButton);
//        optionBox.setPrefWidth(width);
//        optionBox.setSpacing(5);
//        optionBox.setPadding(new Insets(0, 5, 0, 5));

        titleLabel = new Text("");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);


        HBox padBox = new HBox();
        padBox.setPrefWidth(0.2 * width);
        HBox titleBox = new HBox(titleLabel, padBox, aboveButton, belowButton);

        /*String topLabelText = ballotName + " - please place at least " + minPrefsAbove.toString()
                + " preferences above the line or " + minPrefsBelow.toString() + " below the line";
        topLabel = new Text(topLabelText);
        HBox ballotNameBox = new HBox(topLabel); */

        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(width);

        VBox topBox = new VBox(titleBox);
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
        helpButton = new Button("?");

        clearButton.getStyleClass().add("cancel-button");
        confirmButton.getStyleClass().add("confirm-button-grey");
        helpButton.getStyleClass().add("help-button");

        HBox buttonRow = new HBox(helpButton, clearButton, confirmButton);
        buttonRow.setPrefWidth(200);
        buttonRow.setSpacing(5);
        buttonRow.setPadding(new Insets(0, 5, 0, 5));

        clearButton.setPrefWidth(3 * ((width - 20) / 8));
        confirmButton.setPrefWidth((width - 20) / 2);
        helpButton.setPrefWidth((width - 20) / 8);

        clearButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);
        helpButton.setPrefHeight(100);

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
        scrolly.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrolly.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrolly.getStyleClass().add("scrolly-pane");
//        scrolly.setStyle("-fx-font-size: 50px;");

        Image image = new Image(new File("img/arrow.png").toURI().toString());

        rightArrow = new ImageView(image);
        rightArrow.setPreserveRatio(true);
        rightArrow.setFitWidth(150);

        leftArrow = new ImageView(image);
        leftArrow.setRotate(180);
        leftArrow.setPreserveRatio(true);
        leftArrow.setFitWidth(150);

//        leftArrow = new Arrow();
//        rightArrow = new Arrow();
//
//        rightArrow.setStartX(width - 200);
//        rightArrow.setEndX(width - 100);
//
//        rightArrow.setStartY(height/2);
//        rightArrow.setEndY(height/2);
//
//        leftArrow.setStartX(200);
//        leftArrow.setEndX(100);
//
//        leftArrow.setStartY(height/2);
//        leftArrow.setEndY(height/2);
//
//        leftArrow.setStrokeWidth(5);
//        rightArrow.setStrokeWidth(5);

        scrolly.hvalueProperty().addListener(observable -> checkArrowVisibility());
        checkArrowVisibility();

        stack = new StackPane();
        stack.setPrefSize(width, height);
        stack.getChildren().addAll(scrolly, rightArrow, leftArrow);
        StackPane.setAlignment(rightArrow, Pos.CENTER_RIGHT);
        StackPane.setAlignment(leftArrow, Pos.CENTER_LEFT);

        root.setCenter(stack);
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
        ArrayList<String> sortedParties = null;
        boolean sorted = false;
        if(parties != null) {
            sorted = true;
            sortedParties = (ArrayList<String>) this.parties.clone();
        }
        // number of candidates written to screen for each party
        Map<String, Integer> partyCandidates = new HashMap<>();
        partyPositions = new TreeMap<>();

        parties = new ArrayList<>();

        // get and sort parties
        for(int i = 0; i < candidateList.size(); i++) {

            if(!parties.contains(candidateList.get(i).getParty())) {
                partyCandidates.put(candidateList.get(i).getParty(), 0);
                parties.add(candidateList.get(i).getParty());
            }


            double newWidth = Math.max(parties.size() * 0.4, 1.0);
            newWidth = newWidth * width;
            votePane.setPrefWidth(newWidth);
        }
        // sort maybe (if u want)

        if(sorted) {

            this.parties = sortedParties;
        } else {

            Collections.shuffle(this.parties);
        }

        // get position of each party
        for(int i = 0; i < parties.size(); i++) {

            partyPositions.put(parties.get(i), i);
        }

        // Iterates through all the candidates and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            Label preferenceLabel = new Label();
            preferenceLabel.getStyleClass().add("preference-label");
            preferenceLabel.setPrefSize(50, 50);
            if (canVoteFor) {

                // Here, the evm.Candidate is assigned a TextArea
                // (the box with the preference number inside)
                preferenceBoxMap.put(candidateList.get(i), preferenceLabel);
            } else {
                preferenceLabel.setVisible(false);
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
            voteCard.getStyleClass().add("vote-card-candidate");

            if (canVoteFor) {
                voteCard.getChildren().addAll(preferenceLabel, candidateVbox);
            } else {
                voteCard.getChildren().addAll(candidateVbox, preferenceLabel);
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
     * Note: MUST be called after drawCandidateCards, otherwise the parties field will be null
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
            preferenceLabel.getStyleClass().add("preference-label");
            preferenceLabel.setPrefSize(50, 50);
            if (canVoteFor) {

//              Here, the evm.Candidate is assigned a TextArea
//              (the box with the preference number inside)
                partyPreferenceBoxMap.put(candidateList.get(i), preferenceLabel);
            } else {
                preferenceLabel.setVisible(false);
            }
//
            Text candidateName = new Text(candidateList.get(i).getName());
//            Text candidateParty = new Text(candidateList.get(i).getParty());

//            candidateName.getStyleClass().add("candidate-name");
            candidateName.getStyleClass().add("candidate-name");

            /* TODO check wrapping for longer party names */
            // Wrap the name and party text labels so it doesn't squash other vote card elements
            // MaGiC NuMbErS, just leave these,
            //candidateName.setWrappingWidth(200);
            //candidateParty.setWrappingWidth(250);

            VBox candidateVbox = new VBox();
            candidateVbox.getChildren().addAll(candidateName);
            candidateVbox.getStyleClass().add("vote-candidate-display");
            candidateVbox.setPadding(new Insets(0, 10, 0, 10));
            candidateVbox.setAlignment(Pos.CENTER_LEFT);

            HBox voteCard = new HBox();
            voteCard.setPrefWidth(width/2);
            voteCard.getStyleClass().add("vote-card-party");
            if (canVoteFor) {
                voteCard.getChildren().addAll(preferenceLabel, candidateVbox);
            } else {
                voteCard.getChildren().addAll(candidateVbox, preferenceLabel);
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
            String party = candidateList.get(i).getName();
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

    private void checkArrowVisibility() {
        double hval = scrolly.getHvalue();
        if (hval != scrolly.getHmax()) {
            // hide right arrow
            rightArrow.setVisible(true);
        } else {
            rightArrow.setVisible(false);
        }
        if (hval != scrolly.getHmin()) {
            // hide left arrow
            leftArrow.setVisible(true);
        } else {
            leftArrow.setVisible(false);
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

    public Button getHelpButton() {
        return helpButton;
    }

    public int getCurrentState() { return this.currentState; }

    private void setCurrentState(int newState) {

        this.currentState = newState;
    }
    /**
     * Swaps to showing below the line voting
     */
    public void setBelowLine(int numPrefs) {

        titleLabel.setText("Voting below the line: place at least " + numPrefs + " preferences");

        scrolly.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // below the line is state 1
        setCurrentState(1);
    }

    /**
     * Swaps to showing above the line voting
     */
    public void setAboveLine(int numPrefs) {
        titleLabel.setText("Voting above the line: place at least " + numPrefs + " preferences");

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

    public void setConfirmButtonColoured(boolean coloured) {
        confirmButton.getStyleClass().clear();
        if (coloured) {
            confirmButton.getStyleClass().add("confirm-button");
        } else {
            confirmButton.getStyleClass().add("confirm-button-grey");
        }
    }

    public void setAboveBelowColoured(boolean aboveTheLine) {
        aboveButton.getStyleClass().clear();
        belowButton.getStyleClass().clear();
        if (aboveTheLine) {
            aboveButton.getStyleClass().add("line-button-grey");
            belowButton.getStyleClass().add("line-button");
        } else {
            aboveButton.getStyleClass().add("line-button");
            belowButton.getStyleClass().add("line-button-grey");
        }
    }
}
