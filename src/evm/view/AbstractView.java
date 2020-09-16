package evm.view;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Abstract Implementation of a evm.view. Simply has a javafx Node as a protected field and a getter.
 */
public abstract class AbstractView {

    /* a view is simply a new root node now */
    protected Parent root;

    /* getter for the root node */
    public Parent getRoot() { return root; }
}