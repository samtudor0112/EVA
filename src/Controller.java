import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
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
     * Temporarily just instantiates the current view as the vote window
     * @param stage the javafx stage to display on
     * @param model the VotingModel to work from
     */
    public Controller(Stage stage, VotingModel model) {
        this.stage = stage;
        this.model = model;

        // TEMPORARY

        this.currentView = new VoteWindowView(stage.getWidth(), stage.getHeight());

        // Below this should only be done for the voteWindow view
        ((VoteWindowView)currentView).drawCandidateCards(model.getCandidateList());
        ((VoteWindowView)currentView).setCandidatePreferences(model.getFullMap());

        // Set the button presses for the candidate cards
        for (Map.Entry<Candidate, HBox> entry : ((VoteWindowView)currentView).getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new CandidateClickHandler(entry.getKey()));
        }

        // Set the button presses for the clear and confirm buttons
        ((VoteWindowView)currentView).getClearButton().setOnAction(actionEvent -> {
            model.deselectAll();
            ((VoteWindowView)currentView).setCandidatePreferences(model.getFullMap());
        });

        ((VoteWindowView)currentView).getConfirmButton().setOnAction(actionEvent -> {
            if (model.checkValidVote()) {
                BallotPrinter.createPDF(model.getCandidateList(), model.getFullMap());
            } else {
                // TODO
                System.out.println("Not enough candidates voted for");
            }
        });

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
