import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * The Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /** The current model of the MVC */
    private VotingModel model;

    /** The current view of the MVC */
    private AbstractView currentView;

    /** The javafx stage **/
    private Stage stage;

    public Controller(Stage stage, VotingModel model) {
        this.stage = stage;
        this.model = model;
        this.currentView = new VoteWindowView(stage.getWidth(), stage.getHeight());

        // This should only be done for the voteWindow view
        ((VoteWindowView)currentView).drawCandidateCards(model.getCandidateList());

    }

    public AbstractView getCurrentView() {
        return currentView;
    }

    public Stage getStage() {
        return stage;
    }

    public VotingModel getModel() {
        return model;
    }
}
