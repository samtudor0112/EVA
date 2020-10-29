package evm.view;

import javafx.scene.Parent;

/**
 * Abstract implementation of a view. Simply has a javafx Parent as a protected field and a getter.
 */
public abstract class AbstractView {

    /* A view is simply a new root node */
    protected Parent root;

    /**
     * Getter for the root Parent
     * @return the root Parent
     */
    public Parent getRoot() { return root; }
}