package evm.view;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Abstract implementation of a view. Simply has a javafx Node as a protected field and a getter.
 */
public abstract class AbstractView {

    /* a view is simply a new root node now */
    protected Parent root;

    /**
     * Getter for the root Parent
     * @return the root Parent
     */
    public Parent getRoot() { return root; }
}