import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;


public class VoteWindow extends Application {

    /* FOR STANDALONE TESTING ONLY */
    public static void main(String[] args) {
        launch(args);
    }

    // TESTING PURPOSES ONLY - NOT FOR FINAL USE
    private final ObservableList<DummyCandidate> candidateList = FXCollections.observableArrayList(
            new DummyCandidate(1, "Scott Morrison" ,"Liberal National Party"),
            new DummyCandidate(2, "Anthony Albanese", "Australian Labor Party"),
            new DummyCandidate(3, "Adam Bandt", "Australian Greens"),
            new DummyCandidate(4, "Pauline Hanson", "Pauline Hanson's One Nation"),
            new DummyCandidate(5, "Robbie Katter", "Katter's Australian Party"),
            new DummyCandidate(6, "Jacqui Lambie", "Jacqui Lambie Network"),
            new DummyCandidate(7, "Robert Brown", "Shooters, Fishers and Farmers Party")
    );

    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(new Group());
        stage.setTitle("Place Vote");
        stage.setWidth(700);
        stage.setHeight(600);

        scene.getStylesheets().add("styles/styles.css");

        Text titleLabel = new Text("Place vote:");
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        HBox titleBox = new HBox(titleLabel);
        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(stage.getWidth());

        GridPane votePane = new GridPane();
        votePane.setPrefWidth(stage.getWidth());
        votePane.setHgap(5);
        votePane.setVgap(5);
        votePane.setPadding(new Insets(0, 5, 0, 5));

        // Each "DummyCandidate" object is assigned a TextArea, which can be changed when
        // user changes their vote
        Map<DummyCandidate, Label> preferenceBoxMap = new HashMap<>();

        // Each "DummyCandidate" object is also assigned a box that, when clicked, will register
        // a vote for that candidate
        Map<DummyCandidate, HBox> voteCardMap = new HashMap<>();

        // Iterates through all the candidates and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            Label preferenceLabel = new Label();
            //preferenceLabel.setText("1");
            preferenceLabel.getStyleClass().add("preference-label");
            preferenceLabel.setPrefSize(50, 50);

            // Here, the DummyCandidate is assigned a TextArea
            // (the box with the preference number inside)
            preferenceBoxMap.put(candidateList.get(i), preferenceLabel);

            Text candidateName = new Text(candidateList.get(i).getCandidate());
            Text candidateParty = new Text(candidateList.get(i).getParty());

            VBox candidateVbox = new VBox();
            candidateVbox.getChildren().addAll(candidateName, candidateParty);
            candidateVbox.getStyleClass().add("vote-candidate-display");
            candidateVbox.setPadding(new Insets(0, 10, 0, 10));

            HBox voteCard = new HBox();
            voteCard.setPrefWidth(stage.getWidth());
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
            votePane.add(voteCard, i % 2, i / 2);

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

        clearButton.setPrefWidth((stage.getWidth() - 20) / 2);
        confirmButton.setPrefWidth((stage.getWidth() - 20) / 2);

        clearButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        vbox.getChildren().addAll(titleBox, votePane, spacer, buttonRow);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }
}
