package evm;

import evm.view.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    private long seed;

    /**
     * ABSOLUTELY BEAUTIFUL PERMANENT CONSTRUCTOR
     * Starts the application flow by instantiating the login view
     * @param stage the javafx stage
     * @param models the list of VotingModels the user has to fill out
     */
    public Controller(Stage stage, List<VotingModel> models) {
        this.stage = stage;
        this.currentModel = models.get(0);
        this.models = models;
        currentModelIndex = 0;

        AbstractView start = setupLoginWindow();
        this.currentView = start;
        stage.setScene(initialise(start));
        stage.getScene().getStylesheets().add(new File("styles/styles.css").toURI().toString());
        this.stage.setFullScreenExitHint("");
        this.stage.setFullScreen(true);
    }

    // Helper to make scene from view
    private Scene initialise(AbstractView start) {
        return new Scene(start.getRoot());
    }

    // Change the view to another view
    private void changeView(AbstractView view) {
        this.currentView = view;
        stage.getScene().setRoot(view.getRoot());
    }

    // Change to the next ballot in the list of ballots to vote on
    private void changeToNextBallot() {
        AbstractView newView;
        if (currentModel instanceof SenateVotingModel) {
            newView = setupSenateVoteWindow(0);
        } else {
            newView = setupVoteWindow();
        }
        changeView(newView);
        showHelpMessage();
    }

    // Go to the next model in the list of models
    private void nextModel() {
        currentModelIndex++;
        currentModel = models.get(currentModelIndex);
    }

    /**
     * Show the help info message in an Alert box
     */
    private void showHelpMessage() {
        Alert alert = new Alert(AlertType.INFORMATION);
        if (currentModel instanceof SenateVotingModel) {
            alert.setContentText(genSenateHelpMsg((SenateVotingModel)currentModel));
        } else {
            alert.setContentText(genVoteHelpMsg(currentModel));
        }
        alert.setTitle("IMPORTANT INFORMATION");
        alert.getDialogPane().getStyleClass().add(".dialog-pane");
        alert.setHeaderText("How to vote");
        alert.initOwner(stage);
        alert.initStyle(StageStyle.UNDECORATED);

        alert.showAndWait();
    }

    // Generates the text of the help message for the senate
    private String genSenateHelpMsg(SenateVotingModel s) {
        StringBuilder sb = new StringBuilder();
        sb.append("You can vote one of two ways: ");
        sb.append("Above the line by numbering at least " + s.getAboveLine().getNumVotesNeeded() +
                        " candidates in the order of your choice with 1 as your highest preference.\n");
        sb.append("OR\n");
        sb.append("Below the line by numbering at least " + s.getBelowLine().getNumVotesNeeded() +
                        " candidates in the order of your choice with 1 as your highest preference.\n");
        return sb.toString();
    }

    // Generates the text of the normal help message
    private String genVoteHelpMsg(VotingModel s) {
        return "Vote by numbering at least " + s.getBallot().getNumVotesNeeded() +
                " candidates in the order of your choice with 1 as your highest preference.";
    }

    /**
     * Creates a new LoginView, sets up event handlers and then sets the current view to
     * the new view.
     */
    private AbstractView setupLoginWindow() {
        // Generate the seed we'll use for the shuffling
        // Here we generate a random seed, so between vote runs
        // (i.e. one user voting on every ballot) the shuffling is random,
        // but every time we shuffle we get the first random number using the
        // seed so we always shuffle the same way (within one vote run)
        seed = ThreadLocalRandom.current().nextLong();

        LoginView lv = new LoginView(stage.getWidth(), stage.getHeight());

        lv.getConfirmButton().setOnAction(actionEvent -> changeToNextBallot());
        return lv;
    }

    /**
     * Creates a new VoteWindowView, sets up event handlers and then sets the current view to
     * the new view.
     */
    private AbstractView setupVoteWindow() {
        VoteWindowView vw = new VoteWindowView(stage.getWidth(), stage.getHeight(), currentModel.getBallot().getName(), currentModel.getBallot().getNumVotesNeeded());

        vw.drawCandidateCards(currentModel.getCandidateList(), seed);
        vw.setCandidatePreferences(currentModel.getFullMap());

        // Draw the candidate boxes
        for (Map.Entry<Candidate, HBox> entry : vw.getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new CandidateClickHandler(entry.getKey(), entry.getValue()));
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
                ShakeTransition anim = new ShakeTransition(vw.getConfirmButton());
                anim.playFromStart();
                System.out.println("Not enough candidates voted for");
            }
        });

        vw.getHelpButton().setOnAction(actionEvent -> showHelpMessage());

        return vw;
    }

    /**
     * Creates a new ConfirmWindowView, sets up event handlers and then sets the current view to
     * the new view.
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
                // BallotPrinter call for a senate print
                if (((SenateVotingModel) currentModel).getIsAboveLine()) {
                    BallotPrinter.createPDF(currentModel, false, ((SenateVotingModel) currentModel).getCandidatesByParty(), ((SenateVotingModel) currentModel).getParties(),  true, true, "templates/other.txt");
                } else {
                    BallotPrinter.createPDF(currentModel, false, ((SenateVotingModel) currentModel).getCandidatesByParty(), ((SenateVotingModel) currentModel).getParties(),true, false, "templates/other.txt");
                }
            } else {
                // BallotPrinter call for a regular print
                BallotPrinter.createPDF(currentModel, true, new HashMap<>(), new ArrayList<>(),false, false, "templates/default.txt");
            }

        });

        return cw;
    }

    /**
     * Creates a new AcceptWindowView, sets up event handlers and then sets the current view to
     * the new view.
     */
    private AbstractView setupAcceptWindow() {

        AcceptView av = new AcceptView(stage.getWidth(), stage.getHeight(), currentModel.getBallotString());

        av.getConfirmButton().setOnAction(actionEvent -> {
            if (currentModelIndex != models.size() - 1) {
                nextModel();
                changeToNextBallot();
            } else {
                // We've finished the last ballot
                // We can reset to the first model like this
                currentModelIndex = -1;
                nextModel();
                // clear all the models
                for (VotingModel model: models) {
                    model.deselectAll();
                    if (model instanceof SenateVotingModel) {
                        // A cheeky way to reset the SenateVotingModel;
                        ((SenateVotingModel) model).switchBallot();
                        ((SenateVotingModel) model).switchBallot();
                        if (!((SenateVotingModel) model).getIsAboveLine()) {
                            // Set to above line
                            ((SenateVotingModel) model).switchBallot();
                        }
                    }
                }
                AbstractView newView = setupLoginWindow();
                changeView(newView);
            }
        });

        return av;
    }

    /**
     * Creates a new SenateView, sets up event handlers and then sets the current view to
     * the new view.
     * @param state the state to start the senateView in (0 == above line, 1 == below line)
     */
    private AbstractView setupSenateVoteWindow(int state) {
        // We're pretty sure that our currentModel is a SenateVotingModel, so
        // let's cast it here so we don't cast it everywhere
        SenateVotingModel senateModel = (SenateVotingModel)currentModel;

        SenateView uw = new SenateView(stage.getWidth(), stage.getHeight());

        if(state == 0) {
            uw.setAboveLine(senateModel.getAboveLine().getNumVotesNeeded());
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), false, seed);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), true);

            if(!senateModel.getIsAboveLine()) {
                // Set to above line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());


            for (Map.Entry<Candidate, HBox> entry : uw.getPartyVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey(), entry.getValue()));
            }
        } else {
            uw.setBelowLine(senateModel.getBelowLine().getNumVotesNeeded());
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), true, seed);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), false);

            if(senateModel.getIsAboveLine()) {
                // Set to below line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey(), entry.getValue()));
            }
        }

        uw.setConfirmButtonColoured(senateModel.checkValidVote());
        uw.setAboveBelowColoured(senateModel.getIsAboveLine());

        uw.getAboveButton().setOnAction(actionEvent -> {
            uw.setAboveLine(senateModel.getAboveLine().getNumVotesNeeded());
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), false, seed);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), true);

            if(!senateModel.getIsAboveLine()) {
                // Set to above line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getPartyVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey(), entry.getValue()));
            }

            uw.setConfirmButtonColoured(senateModel.checkValidVote());
            uw.setAboveBelowColoured(true);
        });

        uw.getBelowButton().setOnAction(actionEvent -> {
            uw.setBelowLine(senateModel.getBelowLine().getNumVotesNeeded());
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), true, seed);
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), false);

            if(senateModel.getIsAboveLine()) {
                // Set to below line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey(), entry.getValue()));
            }

            uw.setConfirmButtonColoured(senateModel.checkValidVote());
            uw.setAboveBelowColoured(false);
        });

        uw.getClearButton().setOnAction(actionEvent -> {
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
        });

        uw.getConfirmButton().setOnAction(actionEvent -> {
            if (senateModel.checkValidVote()) {
                AbstractView newView = setupConfirmWindow();
                changeView(newView);
            } else {
                ShakeTransition anim = new ShakeTransition(uw.getConfirmButton());
                anim.playFromStart();
                System.out.println("Not enough candidates voted for");
            }
        });

        uw.getHelpButton().setOnAction(actionEvent -> showHelpMessage());

        return uw;
    }

    // Handler for the button presses on the candidate cards
    private class CandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        private HBox voteCard;

        public CandidateClickHandler(Candidate candidate, HBox voteCard) {
            this.candidate = candidate;
            this.voteCard = voteCard;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = currentModel.tryVoteNext(candidate);
            if (!success) {
                success = currentModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Shake the voteCard
                    ShakeTransition anim = new ShakeTransition(voteCard);
                    anim.playFromStart();
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

        private HBox voteCard;

        public SenateCandidateClickHandler(Candidate candidate, HBox voteCard) {
            this.candidate = candidate;
            this.voteCard = voteCard;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = currentModel.tryVoteNext(candidate);
            if (!success) {
                success = currentModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Shake the voteCard
                    ShakeTransition anim = new ShakeTransition(voteCard);
                    anim.playFromStart();
                    return;
                }
            }

            // Grey out or un-grey out the confirm button
            ((SenateView)currentView).setConfirmButtonColoured(currentModel.checkValidVote());

            // Redraw all the candidate preference numbers because why not
            ((SenateView)currentView).setCandidatePreferences(currentModel.getFullMap());
        }
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

}
