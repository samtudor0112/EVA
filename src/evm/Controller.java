package evm;

import evm.view.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

/**
 * The Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /* The current model of the MVC */
    private VotingModel currentModel;


    // The list of all the models to go through
    private List<VotingModel> models;

    // The index of the current model
    // This technically makes currentModel obsolete (since currentModel should
    // always be models.get(currentModelIndex) but it's just convenience
    private int currentModelIndex;

    /* The current view of the MVC */
    private AbstractView currentView;

    /* The javafx stage */
    private Stage stage;

    /** ABSOLUTELY BEAUTIFUL PERMANENT CONSTRUCTOR */
    public Controller(Stage stage, List<VotingModel> models) {
        this.stage = stage;
        this.currentModel = models.get(0);
        this.models = models;

        AbstractView start = setupVoteWindow();
        this.currentView = start;
        stage.setScene(initialise(start));
        stage.getScene().getStylesheets().add("evm/styles/styles.css");
        this.stage.setFullScreenExitHint("");
        this.stage.setFullScreen(true);
    }

    private Scene initialise(AbstractView start) {
        return new Scene(start.getRoot());
    }

    public void changeView(AbstractView view) {
        this.currentView = view;
        stage.getScene().setRoot(view.getRoot());
    }

    public void nextModel() {
        currentModelIndex++;
        currentModel = models.get(currentModelIndex);
    }

    /**
     * Creates a new VoteWindowView, sets up event handlers and then sets the current view to
     * the new VoteWindowView.
     */
    private AbstractView setupVoteWindow() {
        VoteWindowView vw = new VoteWindowView(stage.getWidth(), stage.getHeight(), currentModel.getBallot().getName());
        vw.drawCandidateCards(currentModel.getCandidateList());
        vw.setCandidatePreferences(currentModel.getFullMap());

        // Draw the candidate boxes
        for (Map.Entry<Candidate, HBox> entry : vw.getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new CandidateClickHandler(entry.getKey()));
        }

        vw.setConfirmButtonColoured(currentModel.checkValidVote());

        // Set up the button handlers
        vw.getClearButton().setOnAction(actionEvent -> {
            currentModel.deselectAll();
            vw.setCandidatePreferences(currentModel.getFullMap());
            vw.setConfirmButtonColoured(false);
        });

        vw.getConfirmButton().setOnAction(actionEvent -> {
            if (currentModel.checkValidVote()) {
                AbstractView newView = setupConfirmWindow();
                changeView(newView);
            } else {
                // TODO - animation or something
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
                stage.getHeight(), "");
        cw.updateList(currentModel.orderedList(), currentModel.getFullMap());
        // Set up the button handlers
        cw.getBackButton().setOnAction(actionEvent -> {
             AbstractView nextView;
             if (currentModel instanceof SenateVotingModel) {
                 int currentState = ((SenateVotingModel) currentModel).getIsAboveLine() ? 0 : 1;
                 nextView = setupSenateVoteWindow(currentState);
             } else {
                 nextView = setupVoteWindow();
             }
            changeView(nextView);
        });

        cw.getConfirmButton().setOnAction(actionEvent -> {
            AbstractView nextView = setupAcceptWindow();
            changeView(nextView);
            if (currentModel instanceof SenateVotingModel) {
                // Some BallotPrinter call for a senate print
                // Something like this, idk
                if (((SenateVotingModel) currentModel).getIsAboveLine()) {
                    BallotPrinter.createPDF(currentModel.getCandidateList(), currentModel.getFullMap(), false, ((SenateVotingModel) currentModel).getCandidatesByParty(), true, true, "src" + File.separator + "evm" + File.separator + "templates" + File.separator + "other.txt");
                } else {
                    BallotPrinter.createPDF(currentModel.getCandidateList(), currentModel.getFullMap(), false, ((SenateVotingModel) currentModel).getCandidatesByParty(), true, false, "src" + File.separator + "evm" + File.separator + "templates" + File.separator + "other.txt");
                }
            } else {
                // Some BallotPrinter call for a regular print
                BallotPrinter.createPDF(currentModel.getCandidateList(), currentModel.getFullMap(), true, new HashMap<>(), false, false, "src" + File.separator + "evm" + File.separator + "templates" + File.separator + "default.txt");
            }

        });
        return cw;
    }

    /**
     * sets up a new AcceptWindow and then sets the stage to the new view
     */
    private AbstractView setupAcceptWindow() {

        AcceptView av = new AcceptView(stage.getWidth(), stage.getHeight(), currentModel.getBallotString());

        av.getConfirmButton().setOnAction(actionEvent -> {
            // We change to the next voting model here, very important
            if (currentModelIndex != models.size() - 1) {
                nextModel();
            } else {
                // We've finished the last ballot
                // Exits for now
                // TODO
                Platform.exit();
                System.exit(1);
            }

             // goto above/below the line vote ballot
            AbstractView newView;
            if (currentModel instanceof SenateVotingModel) {
                newView = setupSenateVoteWindow(0);
            } else {
                newView = setupVoteWindow();
            }
            changeView(newView);
        });
        return av;
    }

    private AbstractView setupSenateVoteWindow(int state) {
        // We're pretty sure that our currentModel is a SenateVotingModel, so
        // let's cast it here so we don't cast it everywhere
        SenateVotingModel senateModel = (SenateVotingModel)currentModel;

        SenateView uw = new SenateView(stage.getWidth(), stage.getHeight(), currentModel.getBallot().getName());

        if(state == 0) {
            uw.setAboveLine();
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), false);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), true);

            if(!senateModel.getIsAboveLine()) {
                // Set to above line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());


            for (Map.Entry<Candidate, HBox> entry : uw.getPartyVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey()));
            }
        } else {
            uw.setBelowLine();
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), true);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), false);

            if(senateModel.getIsAboveLine()) {
                // Set to below line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey()));
            }
        }

        uw.setConfirmButtonColoured(senateModel.checkValidVote());

        uw.getAboveButton().setOnAction(actionEvent -> {
            uw.setAboveLine();
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), false);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), true);

            if(!senateModel.getIsAboveLine()) {
                // Set to above line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getPartyVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey()));
            }

            uw.setConfirmButtonColoured(senateModel.checkValidVote());
        });

        uw.getBelowButton().setOnAction(actionEvent -> {
            uw.setBelowLine();
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), true);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), false);

            if(senateModel.getIsAboveLine()) {
                // Set to below line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey()));
            }

            uw.setConfirmButtonColoured(senateModel.checkValidVote());
        });

        uw.getClearButton().setOnAction(actionEvent -> {
            // this version deselects everything from currently
            // viewed ballot
            if(uw.getCurrentState() == 0) {
                // currently viewing above the line ballot
                if(!senateModel.getIsAboveLine()) {
                    // Set to above line
                    senateModel.switchBallot();
                }
            } else {
                // currently viewing below the line ballot
                if(senateModel.getIsAboveLine()) {
                    // Set to above line
                    senateModel.switchBallot();
                }
            }
            senateModel.deselectAll();
            uw.setCandidatePreferences(senateModel.getFullMap());
            uw.setConfirmButtonColoured(false);

            // this version deselects everything from both ballots
            /*
            aboveModel.deselectAll();
            belowModel.deselectAll();

            if(uw.getCurrentState() == 0) {
                uw.setCandidatePreferences(aboveModel.getFullMap());
            } else {
                uw.setCandidatePreferences(belowModel.getFullMap());
            }
            */
        });

        uw.getConfirmButton().setOnAction(actionEvent -> {
            if (senateModel.checkValidVote()) {
                AbstractView newView = setupConfirmWindow();
                changeView(newView);
            } else {
                System.out.println("Not enough candidates voted for");
            }
        });

        return uw;
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
        return currentModel;
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
            boolean success = currentModel.tryVoteNext(candidate);
            if (!success) {
                success = currentModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?
                    return;
                }
            }

            // Grey out or un-grey out the confirm button
            ((VoteWindowView)currentView).setConfirmButtonColoured(currentModel.checkValidVote());

            // Redraw all the candidate preference numbers because why not
            ((VoteWindowView)currentView).setCandidatePreferences(currentModel.getFullMap());
        }
    }

     // Handler for the button presses on the candidate cards
    private class SenateCandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public SenateCandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = currentModel.tryVoteNext(candidate);
            if (!success) {
                success = currentModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?
                    return;
                }
            }

            // Grey out or un-grey out the confirm button
            ((SenateView)currentView).setConfirmButtonColoured(currentModel.checkValidVote());

            // Redraw all the candidate preference numbers because why not
            ((SenateView)currentView).setCandidatePreferences(currentModel.getFullMap());
        }
    }

}
