package evm.view;

import evm.Candidate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.util.*;

/**
 * The view implementing the Senate voting screen.
 */
public class SenateView extends AbstractView {

    // currentState == 0 : above line
    // currentState == 1 : below line
    private int currentState = 0;

    /* The UI elements */
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
    private Text titleLabel;
    public ScrollPane scrolly;

    private double width;
    private double height;

    private ArrayList<String> parties = null;

    /* The maps of candidates/parties to their voting cards and labels */
    private Map<Candidate, Label> preferenceBoxMap;
    private Map<Candidate, HBox> voteCardMap;
    private Map<Candidate, Label> partyPreferenceBoxMap;
    private Map<Candidate, HBox> partyVoteCardMap;

    private TreeMap<String, Integer> partyPositions;

    /**
     * Instantiate the vote window from a stage of size width by height.
     * Sets up some of the ui elements (the static ones), but not the
     * candidate or party cards.
     * @param width the width of the javafx stage
     * @param height the height of the javafx stage
     */
    public SenateView(double width, double height) {
        this.width = width;
        this.height = height;

        /* set the root node */
        BorderPane root = new BorderPane();
        this.root = root;

        aboveButton = new Button("Above line");
        belowButton = new Button("Below line");

        aboveButton.getStyleClass().add("confirm-button");
        belowButton.getStyleClass().add("confirm-button");

        titleLabel = new Text("");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        HBox padBox = new HBox();
        padBox.setPrefWidth(0.2 * width);
        HBox titleBox = new HBox(titleLabel, padBox, aboveButton, belowButton);

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

        // Populating the votePane and partyPane occurs in drawCandidateCards
        // and drawPartyCards respectively

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

        Image arrow = new Image(new File("img/arrow.png").toURI().toString());

        rightArrow = new ImageView(arrow);
        rightArrow.setPreserveRatio(true);
        rightArrow.setFitWidth(150);

        leftArrow = new ImageView(arrow);
        leftArrow.setRotate(180);
        leftArrow.setPreserveRatio(true);
        leftArrow.setFitWidth(150);

        // Set the listener to show and hide the arrows correctly
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
     * Draws the candidate cards from a list of candidates.
     * Also populates the voteCardMap, preferenceBoxMap and parties maps.
     * @param candidateList the list of candidates to draw
     * @param canVoteFor whether we can vote for candidates
     * @param seed the seed to shuffle the candidates with before displaying
     */
    public void drawCandidateCards(List<Candidate> candidateList, boolean canVoteFor, long seed) {
        votePane.getChildren().clear();
        // Each Candidate object is assigned a TextArea, which can be changed when
        // user changes their vote
        preferenceBoxMap = new HashMap<>();

        // Each Candidate object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        voteCardMap = new HashMap<>();

        // number of candidates written to screen for each party
        Map<String, Integer> partyCandidates = new HashMap<>();
        partyPositions = new TreeMap<>();

        // List of all parties without duplicates
        parties = new ArrayList<>();

        // Populate parties
        for(int i = 0; i < candidateList.size(); i++) {
            if (!parties.contains(candidateList.get(i).getParty())) {
                partyCandidates.put(candidateList.get(i).getParty(), 0);
                parties.add(candidateList.get(i).getParty());
            }
        }

        double newWidth = Math.max(parties.size() * 0.4, 1.0);
        newWidth = newWidth * width;
        votePane.setPrefWidth(newWidth);

        // See comment in Controller.setUpLoginWindow
        Collections.shuffle(parties, new Random(seed));

        // Get position of each party
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
                // Here, the Candidate is assigned a TextArea
                // (the box with the preference number inside)
                preferenceBoxMap.put(candidateList.get(i), preferenceLabel);
            } else {
                // Hide the label, but don't remove it, so the card is
                // still the right size
                preferenceLabel.setVisible(false);
            }

            Text candidateName = new Text(candidateList.get(i).getName());
            Text candidateParty = new Text(candidateList.get(i).getParty());

            candidateName.getStyleClass().add("candidate-name");
            candidateParty.getStyleClass().add("party-name");

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
                // They're in the other order so the candidate text isn't
                // blocked by an invisible box
                voteCard.getChildren().addAll(candidateVbox, preferenceLabel);
            }

            // Shadow to make the cards look a bit more pretty and professional
            DropShadow cardShadow = new DropShadow();
            cardShadow.setRadius(2.0);
            cardShadow.setOffsetX(1.0);
            cardShadow.setOffsetY(1.0);
            cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
            voteCard.setEffect(cardShadow);

            // We also assign each Candidate a vote "card" (just an HBox)
            voteCardMap.put(candidateList.get(i), voteCard);

            // Each column is a party
            // Each row is a candidate
            String party = candidateList.get(i).getParty();
            int col = partyPositions.get(party);
            int row = partyCandidates.get(party);
            partyCandidates.put(party, row + 1);
            votePane.add(voteCard, col, row);


        }
    }

    /**
     * Draws the party cards from a list of candidates.
     * Also populates the voteCardMap and preferenceBoxMap.
     * Note: MUST be called after drawCandidateCards,
     * otherwise the parties field will be null
     * @param candidateList the list of parties to draw (Candidates with
     *                      field name=party name and filed party blank)
     * @param canVoteFor whether we can vote for parties
     */
    public void drawPartyCards(List<Candidate> candidateList, boolean canVoteFor) {
        partyPane.getChildren().clear();
        // Each Candidate object is assigned a TextArea, which can be changed when
        // user changes their vote
        partyPreferenceBoxMap = new HashMap<>();

        // Each Candidate object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        partyVoteCardMap = new HashMap<>();

        partyPane.setPrefWidth(votePane.getWidth());

        // Iterates through all the parties and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            Label preferenceLabel = new Label();
            preferenceLabel.getStyleClass().add("preference-label");
            preferenceLabel.setPrefSize(50, 50);
            if (canVoteFor) {
                // Here, the Candidate is assigned a TextArea
                // (the box with the preference number inside)
                partyPreferenceBoxMap.put(candidateList.get(i), preferenceLabel);
            } else {
                // Hide the label, but don't remove it, so the card is
                // still the right size
                preferenceLabel.setVisible(false);
            }

            Text candidateName = new Text(candidateList.get(i).getName());
            candidateName.getStyleClass().add("candidate-name");

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
                // They're in the other order so the candidate text isn't
                // blocked by an invisible box
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

            // Each column is a party
            String party = candidateList.get(i).getName();
            int col = partyPositions.get(party);
            partyPane.add(voteCard, col, 0);


        }
    }

    /**
     * Sets the text of each candidate's label according to the preferences map.
     * Depending on the state (above/below the line), will set the preferences
     * of the parties or the candidates
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

    // Checks if either or both arrows should be drawn
    private void checkArrowVisibility() {
        double hval = scrolly.getHvalue();
        if (hval != scrolly.getHmax()) {
            rightArrow.setVisible(true);
        } else {
            // hide right arrow
            rightArrow.setVisible(false);
        }
        if (hval != scrolly.getHmin()) {
            leftArrow.setVisible(true);
        } else {
            // hide left arrow
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

    /**
     * Getter for the help button
     * @return the help button
     */
    public Button getHelpButton() {
        return helpButton;
    }

    /**
     * Getter for the current state
     * @return the current state
     */
    public int getCurrentState() { return this.currentState; }

    /**
     * Setter for the current state
     * @param newState the new state
     */
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
        scrolly.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        // above the line is state 0
        setCurrentState(0);
    }

    /**
     * Getter for the above button
     * @return the above button
     */
    public Button getAboveButton() {
        return aboveButton;
    }

    /**
     * Getter for the below button
     * @return the below button
     */
    public Button getBelowButton() {
        return belowButton;
    }

    /**
     * Set the confirm button to be either greyed out or normal purple
     * @param coloured whether the confirm button should be purple
     */
    public void setConfirmButtonColoured(boolean coloured) {
        confirmButton.getStyleClass().clear();
        if (coloured) {
            confirmButton.getStyleClass().add("confirm-button");
        } else {
            confirmButton.getStyleClass().add("confirm-button-grey");
        }
    }

    /**
     * Set the above and below buttons to be either greyed out or normal purple
     * @param aboveTheLine whether we're above the line
     */
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
