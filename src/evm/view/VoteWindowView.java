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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The view implementing the main voting screen.
 */
public class VoteWindowView extends AbstractView {

    private final int VOTE_TABLE_COLUMNS = 2;

    private GridPane votePane;

    private Button confirmButton;

    private Button clearButton;

    private double width;

    private double height;

    /* TODO change this to a Map<evm.Candidate, Integer> ??? */
    private Map<Candidate, Label> preferenceBoxMap;

    private Map<Candidate, HBox> voteCardMap;

    public Text topLabel = null;

    /**
     * Instantiate the vote window from a stage of size width by height.
     * Sets up some of the ui elements (the static ones), but not the candidate
     * @param width the width of the javafx stage
     * @param height the height of the javafx stage
     */
    public VoteWindowView(double width, double height, String ballotName, Integer minPrefs) {

        this.width = width;
        this.height = height;

        /* set the root node */
        BorderPane root = new BorderPane();
        this.root = root;

        Text titleLabel = new Text("Place vote:");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        String topLabelText = ballotName + " - please place at least " + minPrefs.toString() + " preferences";
        topLabel = new Text(topLabelText);
        HBox ballotNameBox = new HBox(topLabel);

        HBox titleBox = new HBox(titleLabel);
        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(width);

        VBox topBox = new VBox(ballotNameBox, titleBox);
        topBox.setPrefWidth(width);

        votePane = new GridPane();
        votePane.setPrefWidth(width);
        votePane.setHgap(5);
        votePane.setVgap(5);
        votePane.setPadding(new Insets(0, 5, 0, 5));

        // Populating the votePane now occurs in drawCandidateCards

        // Spacer between vote options and buttons
        Region spacer = new Region();

        // Button pane
        clearButton = new Button("Clear all");
        confirmButton = new Button("Confirm");

        clearButton.getStyleClass().add("cancel-button");

        confirmButton.getStyleClass().add("confirm-button-grey");


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
        vbox.getChildren().add(votePane);

        root.setTop(topBox);
        root.setBottom(buttonRow);

        ScrollPane scrolly = new ScrollPane();
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
    public void drawCandidateCards(List<Candidate> candidateList) {
        // Each Candidate object is assigned a TextArea, which can be changed when
        // user changes their vote
        preferenceBoxMap = new HashMap<>();

        // Each Candidate object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        voteCardMap = new HashMap<>();

        // Iterates through all the candidates and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            Label preferenceLabel = new Label();
            //preferenceLabel.setText("1");
            preferenceLabel.getStyleClass().add("preference-label");
            preferenceLabel.setPrefSize(50, 50);

            // Here, the Candidate is assigned a TextArea
            // (the box with the preference number inside)
            preferenceBoxMap.put(candidateList.get(i), preferenceLabel);

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
            voteCard.getChildren().addAll(preferenceLabel, candidateVbox);

            // Shadow to make the cards look a bit more pretty and professional
            DropShadow cardShadow = new DropShadow();
            cardShadow.setRadius(2.0);
            cardShadow.setOffsetX(1.0);
            cardShadow.setOffsetY(1.0);
            cardShadow.setColor(Color.color(0.5, 0.5, 0.5));
            voteCard.setEffect(cardShadow);

            // We also assign each candidate a vote "card" (just an HBox)
            voteCardMap.put(candidateList.get(i), voteCard);

            // This is why we need the numeric index, every other candidate is put onto a new line
            votePane.add(voteCard, i % VOTE_TABLE_COLUMNS, i / VOTE_TABLE_COLUMNS);

        }
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

    public void setConfirmButtonColoured(boolean coloured) {
        confirmButton.getStyleClass().clear();
        if (coloured) {
            confirmButton.getStyleClass().add("confirm-button");
        } else {
            confirmButton.getStyleClass().add("confirm-button-grey");
        }
    }
}
