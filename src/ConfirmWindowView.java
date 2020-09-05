import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Collections;
import java.util.HashMap;

public class ConfirmWindowView extends AbstractView {

    /* allows us to display a list of the candidates on screen */
    private ListView<Candidate> list;

    /* a mapping of candidates to how they have been preferenced by the voter */
    private HashMap<Candidate, Integer> prefList;

    private Button backButton;
    private Button confirmButton;


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
                Label pref = new Label(Integer.toString(prefList.get(candidate)));
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
    public ConfirmWindowView(double width, double height, HashMap<Candidate, Integer> prefs) {
        this.prefList = prefs;
        scene = new Scene(new Group());

        scene.getStylesheets().add("styles/styles.css");

        Text titleLabel = new Text("Please confirm your vote:");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));
        Text subtitleLabel = new Text("Candidates should be numbered from 1 to x in the order of your choice.");
        subtitleLabel.setTextAlignment(TextAlignment.CENTER);

        ObservableList<Candidate> data = FXCollections.observableArrayList();
        data.addAll(prefList.keySet());

        list = new ListView(data);
        list.setCellFactory(listView -> new ConfirmWindowView.CandidateCell());

        list.setMinWidth(600);
        list.getStyleClass().add("confirm-list-view");

        list.setFocusTraversable(false);

        backButton = new Button("Back");
        confirmButton = new Button("CONFIRM");

        backButton.getStyleClass().add("cancel-button");
        confirmButton.getStyleClass().add("confirm-button");

        HBox buttonRow = new HBox(backButton, confirmButton);

        buttonRow.setPrefWidth(200);

        buttonRow.setSpacing(10);
        //buttonRow.setMargin(backButton, new Insets(10, 10, 10, 10));
        //buttonRow.setMargin(confirmButton, new Insets(10, 10, 10, 10));

        backButton.setPrefWidth((width - 20) / 2);
        confirmButton.setPrefWidth((height - 20) / 2);

        backButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(titleLabel, list, buttonRow);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
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
