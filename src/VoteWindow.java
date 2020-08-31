import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
            new DummyCandidate(5, "Robbie Katter", "Katter's Australian Party")
    );

    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(new Group());
        stage.setTitle("Place Vote");
        stage.setWidth(620);
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
        votePane.setHgap(10);
        votePane.setVgap(10);

        // Each "DummyCandidate" object is assigned a TextArea, which can be changed when
        // user changes their vote
        Map<DummyCandidate, TextArea> preferenceBoxMap = new HashMap<>();

        // Iterates through all the candidates and displays them on the screen
        // Do not use a for-each loop here, we need a numeric index
        for (int i = 0; i < candidateList.size(); i++) {
            TextArea preferenceBox = new TextArea();
            preferenceBox.setText("1");
            preferenceBox.setPrefSize(20, 20);
            preferenceBox.getStyleClass().add("input-vote");

            // Here, the DummyCandidate is assigned a TextArea
            preferenceBoxMap.put(candidateList.get(i), preferenceBox);

            Text candidateName = new Text(candidateList.get(i).getCandidate());
            Text candidateParty = new Text(candidateList.get(i).getParty());
            VBox candidateVbox = new VBox();
            candidateVbox.getChildren().addAll(candidateName, candidateParty);
            candidateVbox.getStyleClass().add("vote-candidate-display");
            HBox voteCard = new HBox();
            voteCard.setPrefWidth(stage.getWidth());
            voteCard.getStyleClass().add("vote-card");
            voteCard.getChildren().addAll(preferenceBox, candidateVbox);

            // This is why we need the numeric index, every other candidate is put onto a new line
            votePane.add(voteCard, i % 2, i / 2);
        }

        // Button pane
        Button clearButton = new Button("Clear all");
        Button confirmButton = new Button("Confirm");

        clearButton.getStyleClass().add("cancel-button");
        confirmButton.getStyleClass().add("confirm-button");

        HBox buttonRow = new HBox(clearButton, confirmButton);

        buttonRow.setPrefWidth(200);

        buttonRow.setSpacing(10);
        //buttonRow.setMargin(clearButton, new Insets(10, 10, 10, 10));
        //buttonRow.setMargin(confirmButton, new Insets(10, 10, 10, 10));

        clearButton.setPrefWidth((stage.getWidth() - 20) / 2);
        confirmButton.setPrefWidth((stage.getWidth() - 20) / 2);

        clearButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(titleBox, votePane, buttonRow);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }
}
