package evm.view;

import evm.Candidate;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

/**
 * The view implementing the confirm screen.
 */
public class ConfirmWindowView extends evm.view.AbstractView {

    /* allows us to display a list of the candidates on screen */
    private ListView<Candidate> list;

    private Button backButton;
    private Button confirmButton;
    private double width;
    private double height;

    /**
     * Instantiate the confirm window
     * @param width the width of the javafx stage
     * @param height the height of the javafx stage
     */
    public ConfirmWindowView(double width, double height, String lineState) {
        this.width = width;
        this.height = height; 

        BorderPane root = new BorderPane();
        root.setPrefSize(width, height);
        this.root = root;

        String title;
        if(lineState.compareTo("") == 0) {

            title = "Please confirm your vote:";
        } else {
            title = "Please confirm your vote: " + lineState;
        }
        Text titleLabel = new Text(title);
        titleLabel.getStyleClass().add("text-header-purple");
        titleLabel.setFill(Color.WHITE);

        HBox titleBox = new HBox(titleLabel);
        titleBox.getStyleClass().add("purple-header");
        titleBox.setPrefWidth(width);

        root.setTop(titleBox);

        addButtons(root);
    }

    /**
     * Draw the candidate list
     * @param data the List of candidates
     * @param preferences the preference map
     */
    public void updateList(List<Candidate> data, Map<Candidate, Integer> preferences) {
        list = new ListView<>(FXCollections.observableArrayList(data));
        ((BorderPane)root).setCenter(list);
        list.setCellFactory(listView -> new ConfirmWindowView.CandidateCell(preferences));

        list.setMinWidth(width);
        list.getStyleClass().add("confirm-list-evm.view");

        list.setFocusTraversable(false);
    }

    /**
     * The cell factory for the ListView
     */
    public class CandidateCell extends ListCell<Candidate> {

        Map<Candidate, Integer> preferences;

        public CandidateCell(Map<Candidate, Integer> preferences) {
            this.preferences = preferences;
        }

        @Override
        protected void updateItem(Candidate candidate, boolean empty) {
            super.updateItem(candidate, empty);
            if (candidate == null) {
                setText("");
            } else {
                if (!candidate.getParty().equals("")) {
                    this.setText(candidate.getName() + ", " + candidate.getParty());
                } else {
                    this.setText(candidate.getName());
                }

                int preference = preferences.get(candidate);
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

                if (preference == Integer.MAX_VALUE) {
                    pref.getStyleClass().add("preference-style-greyed");
                    this.setTextFill(Color.LIGHTGRAY);
                }

                pref.setMinWidth(60);
                pref.setMinHeight(60);
                pref.setAlignment(Pos.CENTER);
                setGraphicTextGap(40);
            }
            setPadding(new Insets(10));

        }
    }


    /**
     * Adds the confirm and back buttons to the root
     * @param root the pane to add the buttons to.
     */
    private void addButtons(BorderPane root) {
        backButton = new Button("Back");
        confirmButton = new Button("Print");

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
}
