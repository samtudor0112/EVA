package sample;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ConfirmWindow extends Application {

    private TableView table = new TableView();

    /*  Just set up a bunch of dummy candidates
        I KNOW WE ARE USING A HASHMAP. I am a shit programmer and couldn't get this working with hashmaps
        I just need to put stuff in the table to know what it's going to look like with data    */
    private final ObservableList<DummyCandidate> data = FXCollections.observableArrayList(
            new DummyCandidate(1, "Scott Morrison" ,"LNP"),
            new DummyCandidate(2, "Anthony Albanese", "ALP"),
            new DummyCandidate(3, "Adam Bandt", "Greens"),
            new DummyCandidate(4, "Pauline Hanson", "One Nation"),
            new DummyCandidate(5, "Robbie Katter", "Katter's Australian Party")
    );

    /* FOR STANDALONE TESTING ONLY
    public static void main(String[] args) {
        launch(args);
    }
    */
    
    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(new Group());
        stage.setTitle("Confirm Vote");
        stage.setWidth(620);
        stage.setHeight(600);

        Text titleLabel = new Text("Confirm Vote");
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setFont(new Font(30));

        table.setEditable(true);
        table.setPlaceholder(new Label("No votes to display"));

        TableColumn preferenceCol = new TableColumn("Preference");
        preferenceCol.setCellValueFactory(new PropertyValueFactory<>("preference"));

        TableColumn candidateCol = new TableColumn("Candidate");
        candidateCol.setCellValueFactory(new PropertyValueFactory<>("candidate"));

        TableColumn partyCol = new TableColumn("Party");
        partyCol.setCellValueFactory(new PropertyValueFactory<>("party"));

        preferenceCol.setMinWidth(100);
        candidateCol.setMinWidth(250);
        partyCol.setMinWidth(250);

        table.setItems(data);
        table.getColumns().addAll(preferenceCol, candidateCol, partyCol);

        // TODO pretty CSS for table

        Button backButton = new Button("Back");
        Button confirmButton = new Button("CONFIRM");

        HBox buttonRow = new HBox(backButton, confirmButton);

        buttonRow.setPrefWidth(200);

        buttonRow.setSpacing(10);
        //buttonRow.setMargin(backButton, new Insets(10, 10, 10, 10));
        //buttonRow.setMargin(confirmButton, new Insets(10, 10, 10, 10));

        backButton.setPrefWidth((stage.getWidth() - 20) / 2);
        confirmButton.setPrefWidth((stage.getWidth() - 20) / 2);

        backButton.setPrefHeight(100);
        confirmButton.setPrefHeight(100);

        // TODO pretty CSS for buttons

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(titleLabel, table, buttonRow);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }
}