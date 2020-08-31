import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Control;

/**
 * The Controller class of MVC. Controls the whole application.
 */
public class Controller {

    /** The current model of the MVC */
    public VotingModel model;

    /** The current view of the MVC */
    public Object currentView;

    public Controller(Object currentView) {
        this.currentView = currentView;
    }
}
