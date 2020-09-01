import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteWindowView extends AbstractView {

    private final int VOTE_TABLE_COLUMNS = 2;

    private GridPane votePane;

    private double width;

    private double height;

    public VoteWindowView(double width, double height) {

        this.width = width;
        this.height = height;

        scene = new Scene(new Group());

        scene.getStylesheets().add("styles/styles.css");

        Text titleLabel = new Text("Place vote:");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        HBox titleBox = new HBox(titleLabel);
        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(width);

        votePane = new GridPane();
        votePane.setPrefWidth(width);
        votePane.setHgap(5);
        votePane.setVgap(5);
        votePane.setPadding(new Insets(0, 5, 0, 5));

        // Populating the votePane now occurs in drawCandidateCards

        // Spacer between vote options and buttons
        Region spacer = new Region();

        // Button pane
        Button clearButton = new Button("Clear all");
        Button confirmButton = new Button("Confirm");

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
        vbox.getChildren().addAll(titleBox, votePane, spacer, buttonRow);

        ScrollPane scrolly = new ScrollPane();
        scrolly.setContent(vbox);
        scrolly.pannableProperty().set(true);
        scrolly.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);

        ((Group) scene.getRoot()).getChildren().addAll(scrolly);
    }

    public void drawCandidateCards(List<Candidate> candidateList) {
        // Each "Candidate" object is assigned a TextArea, which can be changed when
        // user changes their vote
        Map<Candidate, Label> preferenceBoxMap = new HashMap<>();

        // Each "Candidate" object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        Map<Candidate, HBox> voteCardMap = new HashMap<>();

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
            candidateName.getStyleClass().add("party-name");

            // Wrap the name and party text labels so it doesn't squash other vote card elements
            // MaGiC NuMbErS, just leave these,
            candidateName.setWrappingWidth(200);
            candidateParty.setWrappingWidth(200);

            VBox candidateVbox = new VBox();
            candidateVbox.getChildren().addAll(candidateName, candidateParty);
            candidateVbox.getStyleClass().add("vote-candidate-display");
            candidateVbox.setPadding(new Insets(0, 10, 0, 10));

            HBox voteCard = new HBox();
            voteCard.setPrefWidth(width);
            voteCard.getStyleClass().add("vote-card");
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

            // Event handler for when the card is clicked, and the user votes/unvotes for this candidate
            voteCard.setOnMouseClicked(mouseEvent -> {
                // TODO Implement click vote functionality, and reset preference field

                // You can reset this card's preference number with the following:
                // preferenceLabel.setText(NEW_NUMBER_HERE);

                // You can also use candidateList.get(i) to get this candidate, but pls
                // replace the "DummyCandidate" class with the actual proper candidate class

                // If you want to change the preference number outside of this function, then
                // use the preferenceBoxMap to access the Label associated with a candidate,
                // then use .setText(NEW_NUMBER_HERE)
            });
        }
    }
}
