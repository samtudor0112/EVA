package evm;

import evm.view.VoteWindowView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import evm.view.AbstractView;
import evm.view.AcceptView;
import evm.view.ConfirmWindowView;

import java.util.Map;

/**
 * The evm.Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /* The current model of the MVC */
    private VotingModel model;

    /* The current evm.view of the MVC */
    private AbstractView currentView;

    /* The javafx stage */
    private Stage stage;

    /**
     * Instantiates the controller to display on a given stage and with a given evm.VotingModel.
     * Sets up a evm.view.VoteWindowView that is based on the evm.VotingModel passed
     * @param stage the javafx stage to display on
     * @param model the evm.VotingModel to work from
     */
    public Controller(Stage stage, VotingModel model) {
        this.stage = stage;
        this.model = model;
        setupVoteWindow();
    }

    /* TODO idk if this is even a good way to setup/change views but it works - can figure it out later */

    /**
     * Creates a new evm.view.VoteWindowView, sets up event handlers and then sets the current evm.view to
     * the new evm.view.VoteWindowView.
     * TODO make it full screen
     */
    private void setupVoteWindow() {
        VoteWindowView vw = new VoteWindowView(stage.getWidth(), stage.getHeight());
        vw.drawCandidateCards(model.getCandidateList());
        vw.setCandidatePreferences(model.getFullMap());
        for (Map.Entry<Candidate, HBox> entry : vw.getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new CandidateClickHandler(entry.getKey()));
        }

        vw.getClearButton().setOnAction(actionEvent -> {
            model.deselectAll();
            vw.setCandidatePreferences(model.getFullMap());
        });

        vw.getConfirmButton().setOnAction(actionEvent -> {
            if (model.checkValidVote()) {
                setupConfirmWindow();
            } else {
                // TODO - maybe grey out button until valid ??
                System.out.println("Not enough candidates voted for");
            }
        });
        setCurrentView(vw);
    }

    /**
     * sets up a new evm.ConfirmWindowView and sets the stage to the new evm.view.
     */
    private void setupConfirmWindow() {
        ConfirmWindowView cw = new ConfirmWindowView(stage.getWidth(),
                stage.getHeight(), model.getFullMap(), model.orderedList());

        cw.getBackButton().setOnAction(actionEvent -> setupVoteWindow());
        cw.getConfirmButton().setOnAction(actionEvent -> {
            setupAcceptWindow();
            BallotPrinter.createPDF(model.getCandidateList(), model.getFullMap());
        });
        setCurrentView(cw);
    }

    /**
     * sets up a new AcceptWindow and then sets the stage to the new evm.view
     */
    private void setupAcceptWindow() {
        setCurrentView(
                new AcceptView(stage.getWidth(), stage.getHeight(), model.getBallotString()));
    }

    /**
     * Getter for the current evm.view
     * @return the current evm.view
     */
    public AbstractView getCurrentView() {
        return currentView;
    }

    /**
     * changes the current scene to a new one (i.e. on a button click)
     * @param view the evm.view to change to
     */
    public void setCurrentView(AbstractView view) {
        this.currentView = view;
        stage.setScene(currentView.getScene());
        stage.show();
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
