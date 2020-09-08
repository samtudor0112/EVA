package evm.view;

import evm.Candidate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;

public class ConfirmWindowView extends evm.view.AbstractView {

    private ObservableList<Candidate> data;

    /* allows us to display a list of the candidates on screen */
    private ListView<Candidate> list;

    /* a mapping of candidates to how they have been preferenced by the voter */
    private HashMap<Candidate, Integer> prefList;

    private Button backButton;
    private Button confirmButton;
    private double width;
    private double height;



    /**
     * The cell factory for the ListView
     */
    private class CandidateCell extends ListCell<Candidate> {

        @Override
        protected void updateItem(Candidate candidate, boolean empty) {
            super.updateItem(candidate, empty);
            if (candidate == null) {
                setText("");
            } else {
                this.setText(candidate.getName() + ", " + candidate.getParty());
                int preference = prefList.get(candidate);
                String preferenceText;
                if (preference == Integer.MAX_VALUE) {
                    preferenceText = "";
                } else {
                    preferenceText = Integer.toString(preference);
                }
                Label pref = new Label(preferenceText);
                pref.getStyleClass().add("preference-style");
                this.setGraphic(pref);
                this.getStyleClass().add("candidate-style");

                pref.setMinWidth(60);
                pref.setMinHeight(60);
                pref.setAlignment(Pos.CENTER);
                setGraphicTextGap(40);
            }
            setPadding(new Insets(10));

        }
    }

    /**
     * Instantiate the confirm window
     * @param width the width of the javafx stage
     * @param height the height of the javafx stage
     * @param prefs a mapping of candidates to how they have been preferenced by the voters
     */
    public ConfirmWindowView(double width, double height,
                             HashMap<Candidate, Integer> prefs,
                             ObservableList<Candidate> data) {
        this.prefList = prefs;
        this.width = width;
        this.height = height;
        this.data = data;

        BorderPane root = new BorderPane();
        scene = new Scene(root);

        scene.getStylesheets().add("evm/styles/styles.css");

        Text titleLabel = new Text("Please confirm your vote:");
        root.setTop(titleLabel);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));
        Text subtitleLabel = new Text("Candidates should be numbered from 1 to x in the order of your choice.");
        subtitleLabel.setTextAlignment(TextAlignment.CENTER);

        addButtons(root);

        list = new ListView(data);
        root.setCenter(list);
        list.setCellFactory(listView -> new ConfirmWindowView.CandidateCell());

        list.setMinWidth(width);
        list.getStyleClass().add("confirm-list-evm.view");

        list.setFocusTraversable(false);

    }



    private void addButtons(BorderPane root) {
        backButton = new Button("Back");
        confirmButton = new Button("CONFIRM");

        backButton.getStyleClass().add("cancel-button");
        confirmButton.getStyleClass().add("confirm-button");

        backButton.setPrefWidth((width - 20) / 2);
        confirmButton.setPrefWidth((width - 20) / 2);

        backButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

        HBox buttonRow = new HBox(backButton, confirmButton);
        root.setBottom(buttonRow);
        buttonRow.setPrefWidth(200);
        buttonRow.setSpacing(10);

    }

    /**
     * A getter for the back button
     * @return the back button
     */
    public Button getBackButton() { return this.backButton; }

    /**
     * A getter for the confirm button
     * @return the confirm button
     */
    public Button getConfirmButton() { return this.confirmButton; }


    public ObservableList<Candidate> getData() {
        return data;

    }
}
