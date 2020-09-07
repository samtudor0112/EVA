import javafx.scene.Scene;

/**
 * Abstract Implementation of a view. Simply has a javafx Scene as a protected field and a getter.
 */
public abstract class AbstractView {

    // The scene we're drawing
    protected Scene scene;

    /**
     * Getter for the javafx scene
     * @return the javafx scene
     */
    public Scene getScene() {
        return scene;
    }
}