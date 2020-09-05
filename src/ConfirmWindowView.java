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

    private ListView<Candidate> list;
    private HashMap<Candidate, Integer> prefList;


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

        Button backButton = new Button("Back");
        Button confirmButton = new Button("CONFIRM");

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
}
