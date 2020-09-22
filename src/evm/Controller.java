package evm;

import evm.view.VoteWindowView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import evm.view.AbstractView;
import evm.view.AcceptView;
import evm.view.ConfirmWindowView;

import java.util.Map;

/**
 * The Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /* The current model of the MVC */
    private VotingModel model;

    /* The current view of the MVC */
    private AbstractView currentView;

    /* The javafx stage */
    private Stage stage;

    /**
     * Instantiates the controller to display on a given stage and with a given VotingModel.
     * Sets up a VoteWindowView that is based on the VotingModel passed
     * @param stage the javafx stage to display on
     * @param model the VotingModel to work from
     */
    public Controller(Stage stage, VotingModel model) {
        this.stage = stage;
        this.model = model;
        AbstractView start = setupVoteWindow();
        this.currentView = start;
        stage.setScene(initialise(start));
        stage.getScene().getStylesheets().add("evm/styles/styles.css");
        this.stage.setFullScreenExitHint("");
        this.stage.setFullScreen(true);
        //stage.show();
;
    }

    private Scene initialise(AbstractView start) {
        return new Scene(start.getRoot());
    }

    public void changeView(AbstractView view) {
        this.currentView = view;
        stage.getScene().setRoot(view.getRoot());
    }

    /* TODO idk if this is even a good way to setup/change views but it works - can figure it out later */

    /**
     * Creates a new VoteWindowView, sets up event handlers and then sets the current view to
     * the new VoteWindowView.
     * TODO make it full screen
     */
    private AbstractView setupVoteWindow() {
        VoteWindowView vw = new VoteWindowView(stage.getWidth(), stage.getHeight());
        vw.drawCandidateCards(model.getCandidateList());
        vw.setCandidatePreferences(model.getFullMap());

        // Draw the candidate boxes
        for (Map.Entry<Candidate, HBox> entry : vw.getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new CandidateClickHandler(entry.getKey()));
        }

        // Set up the button handlers
        vw.getClearButton().setOnAction(actionEvent -> {
            model.deselectAll();
            vw.setCandidatePreferences(model.getFullMap());
        });

        vw.getConfirmButton().setOnAction(actionEvent -> {
            if (model.checkValidVote()) {
                AbstractView newView = setupConfirmWindow();
                changeView(newView);
            } else {
                // TODO - maybe grey out button until valid ??
                System.out.println("Not enough candidates voted for");
            }
        });
        return vw;
    }

    /**
     * sets up a new ConfirmWindowView and sets the stage to the new view.
     */
    private AbstractView setupConfirmWindow() {
        ConfirmWindowView cw = new ConfirmWindowView(stage.getWidth(),
                stage.getHeight());
        cw.updateList(model.orderedList(), model.getFullMap());
        // Set up the button handlers
        cw.getBackButton().setOnAction(actionEvent -> {
             AbstractView nextView = setupVoteWindow();
             changeView(nextView);
        });
        cw.getConfirmButton().setOnAction(actionEvent -> {
            AbstractView nextView = setupAcceptWindow();
            changeView(nextView);
            BallotPrinter.createPDF(model.getCandidateList(), model.getFullMap());
        });
        return cw;
    }

    /**
     * sets up a new AcceptWindow and then sets the stage to the new view
     */
    private AbstractView setupAcceptWindow() {
        return new AcceptView(stage.getWidth(), stage.getHeight(), model.getBallotString());
    }

    /**
     * Getter for the current view
     * @return the current view
     */
    public AbstractView getCurrentView() {
        return currentView;
    }

    /**
     * Getter for the javafx stage
     * @return the javafx stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Getter for the voting model
     * @return the voting model
     */
    public VotingModel getModel() {
        return model;
    }

    // Handler for the button presses on the candidate cards
    private class CandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public CandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = model.tryVoteNext(candidate);
            if (!success) {
                success = model.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?
                    return;
                }
            }

            // Redraw all the candidate preference numbers because why not
            ((VoteWindowView)currentView).setCandidatePreferences(model.getFullMap());
        }
    }


}
