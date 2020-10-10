package evm;

import evm.view.*;
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

import java.util.*;

/**
 * The Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /* The current model of the MVC */
    private VotingModel model;

    /* Voting Model for senate voting */
    // above the line
    private VotingModel aboveModel;
    // below the line
    private VotingModel belowModel;

    private VotingModel senateModel;

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
    public Controller(Stage stage, VotingModel model, VotingModel aboveModel, VotingModel belowModel) {
        this.stage = stage;
        this.model = model;
        this.aboveModel = aboveModel;
        senateModel = aboveModel;
        this.belowModel = belowModel;
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
            vw.setConfirmButtonGrey();
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
                stage.getHeight(), "");
        cw.updateList(model.orderedList(), model.getFullMap());
        // Set up the button handlers
        cw.getBackButton().setOnAction(actionEvent -> {
             AbstractView nextView = setupVoteWindow();
             changeView(nextView);
        });
        cw.getConfirmButton().setOnAction(actionEvent -> {
            AbstractView nextView = setupAcceptWindow();
            changeView(nextView);
            BallotPrinter.createPDF(model.getCandidateList(), model.getFullMap(), false);
        });
        return cw;
    }


    private AbstractView setupUpperConfirmWindow(int state) {

        VotingModel currentModel;
        String stateString = "";
        if(state == 0) {

            stateString = "(above the line)";
            currentModel = aboveModel;
        } else {

            stateString = "(below the line)";
            currentModel = belowModel;
        }
        ConfirmWindowView cw = new ConfirmWindowView(stage.getWidth(),
                stage.getHeight(), stateString);

        cw.updateList(currentModel.orderedList(), currentModel.getFullMap());
        cw.getBackButton().setOnAction(actionEvent -> {

            AbstractView nextView = setupUpperVoteWindow(state);
            this.currentView = nextView;
            stage.getScene().setRoot(nextView.getRoot());
        });
        cw.getConfirmButton().setOnAction(actionEvent -> {

            /* idk bro do some new accept window or something
            AbstractView nextView = setupAcceptWindow();
            stage.getScene().setRoot(nextView.getRoot());
             */

            BallotPrinter.createPDF(currentModel.getCandidateList(), currentModel.getFullMap(), false);
        });
        return cw;
    }


    /**
     * sets up a new AcceptWindow and then sets the stage to the new view
     */
    private AbstractView setupAcceptWindow() {

        AcceptView av = new AcceptView(stage.getWidth(), stage.getHeight(), model.getBallotString());


        av.getConfirmButton().setOnAction(actionEvent -> {

             // goto above/below the line vote ballot
            AbstractView newView = setupSenateWindow();
            this.currentView = newView;
            stage.getScene().setRoot(newView.getRoot());
        });
        return av;
    }

    private AbstractView setupPrototypeSenateUpperVoteWindow(int state) {

        PrototypeSenateVoteWindowView uw = new PrototypeSenateVoteWindowView(stage.getWidth(), stage.getHeight());

        if(state == 0) {
            uw.drawCandidateCards(belowModel.getCandidateList(), false);
            uw.drawPartyCards(aboveModel.getCandidateList(), true);
            uw.setCandidatePreferences(aboveModel.getFullMap());


            for (Map.Entry<Candidate, HBox> entry : uw.getPartyVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new PrototypeSenateAboveCandidateClickHandler(entry.getKey()));
            }
        } else {

            uw.drawCandidateCards(belowModel.getCandidateList(), true);
//             TEMP
            uw.drawPartyCards(aboveModel.getCandidateList(), false);
            uw.setCandidatePreferences(belowModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new PrototypeSenateBelowCandidateClickHandler(entry.getKey()));
            }
        }


        uw.getAboveButton().setOnAction(actionEvent -> {



            // update state
            uw.setAboveLine();
            // show above the line voting if state == 0...

            uw.drawCandidateCards(belowModel.getCandidateList(), false);
            uw.drawPartyCards(aboveModel.getCandidateList(), true);

            uw.setCandidatePreferences(aboveModel.getFullMap());


            for (Map.Entry<Candidate, HBox> entry : uw.getPartyVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new PrototypeSenateAboveCandidateClickHandler(entry.getKey()));
            }

        });

        uw.getBelowButton().setOnAction(actionEvent -> {


            uw.setBelowLine();
            // show below the line voting
            uw.drawCandidateCards(belowModel.getCandidateList(), true);
            // TEMP
            uw.drawPartyCards(aboveModel.getCandidateList(), false);
            uw.setCandidatePreferences(belowModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new PrototypeSenateBelowCandidateClickHandler(entry.getKey()));
            }

        });


        uw.getClearButton().setOnAction(actionEvent -> {

            // this version deselects everything from currently
            // viewed ballot
            if(uw.getCurrentState() == 0) {

                // currently viewing above the line ballot
                aboveModel.deselectAll();
                uw.setCandidatePreferences(aboveModel.getFullMap());
            } else {

                // currently viewing below the line ballot
                belowModel.deselectAll();
                uw.setCandidatePreferences(belowModel.getFullMap());
            }

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

            VotingModel currentModel = null;
            // put through currently selected ballot
            if(uw.getCurrentState() == 0) {

                // put through above line ballot
                currentModel = aboveModel;
            } else {

                currentModel = belowModel;
            }

            if (currentModel.checkValidVote()) {
                AbstractView newView = setupUpperConfirmWindow(uw.getCurrentState());
                this.currentView = newView;
                stage.getScene().setRoot(newView.getRoot());
            } else {

                System.out.println("Not enough candidates voted for");
            }
        });

        return uw;

    }

    private AbstractView setupUpperVoteWindow(int state) {

        UpperVoteWindowView uw = new UpperVoteWindowView(stage.getWidth(), stage.getHeight());

        if(state == 0) {
            uw.drawCandidateCards(aboveModel.getCandidateList());
            uw.setCandidatePreferences(aboveModel.getFullMap());


            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new AboveCandidateClickHandler(entry.getKey()));
            }
        } else {

            uw.drawCandidateCards(belowModel.getCandidateList());
            uw.setCandidatePreferences(belowModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new BelowCandidateClickHandler(entry.getKey()));
            }
        }


        uw.getAboveButton().setOnAction(actionEvent -> {

            // update state
            uw.setAboveLine();
            // show above the line voting if state == 0...

            uw.drawCandidateCards(aboveModel.getCandidateList());
            uw.setCandidatePreferences(aboveModel.getFullMap());


            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new AboveCandidateClickHandler(entry.getKey()));
            }

        });

        uw.getBelowButton().setOnAction(actionEvent -> {


            uw.setBelowLine();
            // show below the line voting
            uw.drawCandidateCards(belowModel.getCandidateList());
            uw.setCandidatePreferences(belowModel.getFullMap());

            for (Map.Entry<Candidate, HBox> entry : uw.getVoteCardMap().entrySet()) {
                entry.getValue().setOnMouseClicked(new BelowCandidateClickHandler(entry.getKey()));
            }

        });


        uw.getClearButton().setOnAction(actionEvent -> {

            // this version deselects everything from currently
            // viewed ballot
            if(uw.getCurrentState() == 0) {

                // currently viewing above the line ballot
                aboveModel.deselectAll();
                uw.setCandidatePreferences(aboveModel.getFullMap());
            } else {

                // currently viewing below the line ballot
                belowModel.deselectAll();
                uw.setCandidatePreferences(belowModel.getFullMap());
            }

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

            VotingModel currentModel = null;
            // put through currently selected ballot
            if(uw.getCurrentState() == 0) {

                // put through above line ballot
                currentModel = aboveModel;
            } else {

                currentModel = belowModel;
            }

            if (currentModel.checkValidVote()) {
                AbstractView newView = setupUpperConfirmWindow(uw.getCurrentState());
                this.currentView = newView;
                stage.getScene().setRoot(newView.getRoot());
            } else {

                System.out.println("Not enough candidates voted for");
            }
        });

        return uw;

    }

    private AbstractView setupSenateWindow() {
        SenateView view = new SenateView(stage.getWidth(), stage.getHeight(), senateModel.getParties());
        view.drawCandidateMenus(senateModel.getCandidatesByParty());


        for (String party: senateModel.getParties()) {
            view.getPartyCards().get(party).setOnMouseClicked(mouseEvent ->
                    view.partyClick(view.getPartyCards().get(party), view.getCandidateVBoxes().get(party)));
        }



        // Draw the candidate boxes
        for (Map.Entry<Candidate, HBox> entry : view.getVoteCardMap().entrySet()) {
            entry.getValue().setOnMouseClicked(new SenateCandidateClickHandler(entry.getKey()));
        }

        // Set up the button handlers
        view.getClearButton().setOnAction(actionEvent -> {
            senateModel.deselectAll();
            view.setCandidatePreferences(senateModel.getFullMap());
        });

        view.getConfirmButton().setOnAction(actionEvent -> {
            if (senateModel.checkValidVote()) {
                AbstractView newView = setupConfirmWindow();
                changeView(newView);
            } else {
                // TODO - maybe grey out button until valid ??
                System.out.println("Not enough candidates voted for");
            }
        });

        view.getLineButton().setOnAction(actionEvent -> {
            view.clickButton();
        });


        return view;
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


            if (model.checkValidVote()) {
                ((VoteWindowView)currentView).setConfirmButtonColor();
            }



            // Redraw all the candidate preference numbers because why not
            ((VoteWindowView)currentView).setCandidatePreferences(model.getFullMap());
        }
    }

     // Handler for the button presses on the candidate cards
    private class PrototypeSenateAboveCandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public PrototypeSenateAboveCandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model


            boolean success = aboveModel.tryVoteNext(candidate);
            if (!success) {
                success = aboveModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?

                    return;
                }
            }

            // Redraw all the candidate preference numbers because why not
            ((PrototypeSenateVoteWindowView)currentView).setCandidatePreferences(aboveModel.getFullMap());
        }
    }
    // Handler for the button presses on the candidate cards
    private class PrototypeSenateBelowCandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public PrototypeSenateBelowCandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = belowModel.tryVoteNext(candidate);
            if (!success) {
                success = belowModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?
                    return;
                }
            }

            // Redraw all the candidate preference numbers because why not
            ((PrototypeSenateVoteWindowView)currentView).setCandidatePreferences(belowModel.getFullMap());
        }
    }

    // Handler for the button presses on the candidate cards
    private class AboveCandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public AboveCandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model


            boolean success = aboveModel.tryVoteNext(candidate);
            if (!success) {
                success = aboveModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?

                    return;
                }
            }

            // Redraw all the candidate preference numbers because why not
            ((UpperVoteWindowView)currentView).setCandidatePreferences(aboveModel.getFullMap());
        }
    }
    // Handler for the button presses on the candidate cards
    private class BelowCandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public BelowCandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = belowModel.tryVoteNext(candidate);
            if (!success) {
                success = belowModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?
                    return;
                }
            }

            // Redraw all the candidate preference numbers because why not
            ((UpperVoteWindowView)currentView).setCandidatePreferences(belowModel.getFullMap());
        }
    }



    private class SenateCandidateClickHandler implements EventHandler<MouseEvent> {

        private Candidate candidate;

        public SenateCandidateClickHandler(Candidate candidate) {
            this.candidate = candidate;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            // Vote in the model
            boolean success = senateModel.tryVoteNext(candidate);
            if (!success) {
                success = senateModel.tryDeselectVote(candidate);
                if (!success) {
                    // The candidate can't be voted for or deselected.
                    // Do nothing?
                    return;
                }
            }


            if (senateModel.checkValidVote()) {
                ((SenateView)currentView).setConfirmButtonColor();
            }

            // Redraw all the candidate preference numbers because why not
            ((SenateView)currentView).setCandidatePreferences(senateModel.getFullMap());
        }
    }


}
