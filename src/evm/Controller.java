package evm;

import evm.view.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.*;

/**
 * The Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /* The current model of the MVC */
    private VotingModel currentModel;

    /* Voting Model for senate voting */
    // above the line
//    private VotingModel aboveModel;
//    // below the line
//    private VotingModel belowModel;
//
//    private SenateVotingModel SsenateModel;
//
//    private SenateVotingModel senateModel;
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

    /** TEMP TO GET THE THING WORKING */
//    private int ballotNum = 0;

    /**
     * Instantiates the controller to display on a given stage and with a given VotingModel.
     * Sets up a VoteWindowView that is based on the VotingModel passed
     * @param stage the javafx stage to display on
     * @param model the VotingModel to work from
     */
//    public Controller(Stage stage, VotingModel model, VotingModel aboveModel, VotingModel belowModel) {
//        this.stage = stage;
//        this.model = model;
//        this.aboveModel = aboveModel;
//        senateModel = new SenateVotingModel(aboveModel.getBallot(), /*TEMP*/ 3);
//        this.belowModel = belowModel;
//        AbstractView start = setupVoteWindow();
//        this.currentView = start;
//        stage.setScene(initialise(start));
//        stage.getScene().getStylesheets().add("evm/styles/styles.css");
//        this.stage.setFullScreenExitHint("");
//        this.stage.setFullScreen(true);
//        //stage.show();
//;
//    }

    /** ABSOLUTE PIECE OF SHIT TEMP CONSTRUCTOR */
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



    /* TODO idk if this is even a good way to setup/change views but it works - can figure it out later */

    /**
     * Creates a new VoteWindowView, sets up event handlers and then sets the current view to
     * the new VoteWindowView.
     * TODO make it full screen
     */
    private AbstractView setupVoteWindow() {
        VoteWindowView vw = new VoteWindowView(stage.getWidth(), stage.getHeight());
        vw.drawCandidateCards(currentModel.getCandidateList());
        vw.setCandidatePreferences(currentModel.getFullMap());

        // Draw the candidate boxes
        for (Map.Entry<Candidate, HBox> entry : vw.getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new CandidateClickHandler(entry.getKey()));
        }

        // Set up the button handlers
        vw.getClearButton().setOnAction(actionEvent -> {
            currentModel.deselectAll();
            vw.setCandidatePreferences(currentModel.getFullMap());
            vw.setConfirmButtonGrey();
        });

        vw.getConfirmButton().setOnAction(actionEvent -> {
            if (currentModel.checkValidVote()) {
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
                //BallotPrinter.createPDF(currentModel.getCandidateList(), currentModel.getFullMap(), false, currentModel.getCandidatesByParty());
                // TODO
            } else {
                // Some BallotPrinter call for a regular print
                // TODO
            }

        });
        return cw;
    }


//    private AbstractView setupUpperConfirmWindow(int state) {
//
//        VotingModel currentModel;
//        String stateString = "";
//        if(state == 0) {
//
//            stateString = "(above the line)";
//            currentModel = aboveModel;
//        } else {
//
//            stateString = "(below the line)";
//            currentModel = belowModel;
//        }
//        ConfirmWindowView cw = new ConfirmWindowView(stage.getWidth(),
//                stage.getHeight(), stateString);
//
//        cw.updateList(currentModel.orderedList(), currentModel.getFullMap());
//        cw.getBackButton().setOnAction(actionEvent -> {
//
//            AbstractView nextView = setupUpperVoteWindow(state);
//            this.currentView = nextView;
//            stage.getScene().setRoot(nextView.getRoot());
//        });
//        cw.getConfirmButton().setOnAction(actionEvent -> {
//
//            /* idk bro do some new accept window or something
//            AbstractView nextView = setupAcceptWindow();
//            stage.getScene().setRoot(nextView.getRoot());
//             */
//
//            BallotPrinter.createPDF(currentModel.getCandidateList(), currentModel.getFullMap(), false, SsenateModel.getCandidatesByParty());
//        });
//        return cw;
//    }


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
            this.currentView = newView;
            stage.getScene().setRoot(newView.getRoot());
        });
        return av;
    }

    private AbstractView setupSenateVoteWindow(int state) {

        // We're pretty sure that our currentModel is a SenateVotingModel, so
        // let's cast it here so we don't cast it everywhere
        SenateVotingModel senateModel = (SenateVotingModel)currentModel;

        SenateView uw = new SenateView(stage.getWidth(), stage.getHeight());

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
//             TEMP
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


        uw.getAboveButton().setOnAction(actionEvent -> {



            // update state
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

        });

        uw.getBelowButton().setOnAction(actionEvent -> {


            uw.setBelowLine();
            // show below the line voting
            uw.drawCandidateCards(senateModel.getBelowLine().getCandidateList(), true);
            // TEMP
            uw.drawPartyCards(senateModel.getAboveLine().getCandidateList(), false);
            if(senateModel.getIsAboveLine()) {
                // Set to below line
                senateModel.switchBallot();
            }
            uw.setCandidatePreferences(senateModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey()));
            }

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

//    private AbstractView setupUpperVoteWindow(int state) {
//
//        SenateViewHamishDeprecated uw = new SenateViewHamishDeprecated(stage.getWidth(), stage.getHeight());
//
//        if(state == 0) {
//            uw.drawCandidateCards(aboveModel.getCandidateList());
//            uw.setCandidatePreferences(aboveModel.getFullMap());
//
//
//            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
//                entry.getValue().setOnMouseClicked(new AboveCandidateClickHandler(entry.getKey()));
//            }
//        } else {
//
//            uw.drawCandidateCards(belowModel.getCandidateList());
//            uw.setCandidatePreferences(belowModel.getFullMap());
//
//            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
//                entry.getValue().setOnMouseClicked(new BelowCandidateClickHandler(entry.getKey()));
//            }
//        }
//
//
//        uw.getAboveButton().setOnAction(actionEvent -> {
//
//            // update state
//            uw.setAboveLine();
//            // show above the line voting if state == 0...
//
//            uw.drawCandidateCards(aboveModel.getCandidateList());
//            uw.setCandidatePreferences(aboveModel.getFullMap());
//
//
//            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
//                entry.getValue().setOnMouseClicked(new AboveCandidateClickHandler(entry.getKey()));
//            }
//
//        });
//
//        uw.getBelowButton().setOnAction(actionEvent -> {
//
//
//            uw.setBelowLine();
//            // show below the line voting
//            uw.drawCandidateCards(belowModel.getCandidateList());
//            uw.setCandidatePreferences(belowModel.getFullMap());
//
//            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
//                entry.getValue().setOnMouseClicked(new BelowCandidateClickHandler(entry.getKey()));
//            }
//
//        });
//
//
//        uw.getClearButton().setOnAction(actionEvent -> {
//
//            // this version deselects everything from currently
//            // viewed ballot
//            if(uw.getCurrentState() == 0) {
//
//                // currently viewing above the line ballot
//                aboveModel.deselectAll();
//                uw.setCandidatePreferences(aboveModel.getFullMap());
//            } else {
//
//                // currently viewing below the line ballot
//                belowModel.deselectAll();
//                uw.setCandidatePreferences(belowModel.getFullMap());
//            }
//
//            // this version deselects everything from both ballots
//            /*
//            aboveModel.deselectAll();
//            belowModel.deselectAll();
//
//            if(uw.getCurrentState() == 0) {
//                uw.setCandidatePreferences(aboveModel.getFullMap());
//            } else {
//                uw.setCandidatePreferences(belowModel.getFullMap());
//            }
//            */
//
//        });
//
//
//        uw.getConfirmButton().setOnAction(actionEvent -> {
//
//            VotingModel currentModel = null;
//            // put through currently selected ballot
//            if(uw.getCurrentState() == 0) {
//
//                // put through above line ballot
//                currentModel = aboveModel;
//            } else {
//
//                currentModel = belowModel;
//            }
//
//            if (currentModel.checkValidVote()) {
//                AbstractView newView = setupConfirmWindow(currentModel);
//                this.currentView = newView;
//                stage.getScene().setRoot(newView.getRoot());
//            } else {
//
//                System.out.println("Not enough candidates voted for");
//            }
//        });
//
//        return uw;
//
//    }

//    private AbstractView setupSenateWindow() {
//        SenateViewEllaDeprecated view = new SenateViewEllaDeprecated(stage.getWidth(), stage.getHeight(), senateModel.getParties());
//        view.drawCandidateMenus(senateModel.getCandidatesByParty());
//
//
//        for (String party: senateModel.getParties()) {
//            view.getPartyExpand().get(party).setOnAction(actionEvent ->
//                    view.partyClick(view.getPartyCards().get(party), view.getCandidateVBoxes().get(party)));
//            view.getPartyCards().get(party).setOnMouseClicked(
//                    new SenateCandidateClickHandler(party));
//        }
//
//
//
//        // Draw the candidate boxes
//        for (Map.Entry<Candidate, HBox> entry : view.getVoteCardMap().entrySet()) {
//            entry.getValue().setOnMouseClicked(
//                    new SenateCandidateClickHandler(entry.getKey()));
//        }
//
//
//        // Set up the button handlers
//        view.getClearButton().setOnAction(actionEvent -> {
//            senateModel.deselectAll();
//            view.setCandidatePreferences(senateModel.getFullMap());
//        });
//
//        view.getConfirmButton().setOnAction(actionEvent -> {
//            if (senateModel.checkValidVote()) {
//                AbstractView newView = setupConfirmWindow(senateModel);
//                changeView(newView);
//            } else {
//                // TODO - maybe grey out button until valid ??
//                System.out.println("Not enough candidates voted for");
//            }
//        });
//
//        view.getLineButton().setOnAction(actionEvent -> {
//            senateModel.switchBallot();
//            view.clickButton();
//            senateModel.deselectAll();
//        });
//
//
//        return view;
//    }


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


            if (currentModel.checkValidVote()) {
                ((VoteWindowView)currentView).setConfirmButtonColor();
            }



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

            // Redraw all the candidate preference numbers because why not
            ((SenateView)currentView).setCandidatePreferences(currentModel.getFullMap());
        }
    }

//    // Handler for the button presses on the candidate cards
//    private class AboveCandidateClickHandler implements EventHandler<MouseEvent> {
//
//        private Candidate candidate;
//
//        public AboveCandidateClickHandler(Candidate candidate) {
//            this.candidate = candidate;
//        }
//
//        @Override
//        public void handle(MouseEvent mouseEvent) {
//            // Vote in the model
//
//
//            boolean success = aboveModel.tryVoteNext(candidate);
//            if (!success) {
//                success = aboveModel.tryDeselectVote(candidate);
//                if (!success) {
//                    // The candidate can't be voted for or deselected.
//                    // Do nothing?
//
//                    return;
//                }
//            }
//
//            // Redraw all the candidate preference numbers because why not
//            ((SenateViewHamishDeprecated)currentView).setCandidatePreferences(aboveModel.getFullMap());
//        }
//    }
//    // Handler for the button presses on the candidate cards
//    private class BelowCandidateClickHandler implements EventHandler<MouseEvent> {
//
//        private Candidate candidate;
//
//        public BelowCandidateClickHandler(Candidate candidate) {
//            this.candidate = candidate;
//        }
//
//        @Override
//        public void handle(MouseEvent mouseEvent) {
//            // Vote in the model
//            boolean success = belowModel.tryVoteNext(candidate);
//            if (!success) {
//                success = belowModel.tryDeselectVote(candidate);
//                if (!success) {
//                    // The candidate can't be voted for or deselected.
//                    // Do nothing?
//                    return;
//                }
//            }
//
//            // Redraw all the candidate preference numbers because why not
//            ((SenateViewHamishDeprecated)currentView).setCandidatePreferences(belowModel.getFullMap());
//        }
//    }
//
//
//
//    private class SenateCandidateClickHandler implements EventHandler<MouseEvent> {
//
//        private Candidate candidate;
//
//        private Boolean aboveLine;
//
//        public SenateCandidateClickHandler(Candidate candidate) {
//            this.candidate = candidate;
//            this.aboveLine = false;
//        }
//
//        public SenateCandidateClickHandler(String partyName) {
//            this.candidate = senateModel.getPartyNameCandidateMap().get(partyName);
//            this.aboveLine = true;
//        }
//
//        @Override
//        public void handle(MouseEvent mouseEvent) {
//            // Vote in the model
//            if (this.aboveLine == senateModel.getIsAboveLine()) {
//                boolean success = senateModel.tryVoteNext(candidate);
//                if (!success) {
//                    success = senateModel.tryDeselectVote(candidate);
//                    if (!success) {
//                        // The candidate can't be voted for or deselected.
//                        // Do nothing?
//                        return;
//                    }
//                }
//
//                if (senateModel.checkValidVote()) {
//                    ((SenateViewEllaDeprecated) currentView).setConfirmButtonColor();
//                }
//
//                // Redraw all the candidate preference numbers because why not
//                ((SenateViewEllaDeprecated) currentView).setCandidatePreferences(senateModel.getFullMap());
//            }
//        }
//    }


}
