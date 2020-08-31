import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.print.PrinterJob;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
//        RepAcceptView view = new RepAcceptView(stage.getWidth(), stage.getHeight());
        stage.setTitle("Printing...");

        stage.setFullScreen(true);

        stage.setResizable(false);

        stage.show();

        RepAcceptView view = new RepAcceptView(stage.getWidth(), stage.getHeight());
        stage.setScene(view.getScene());



    }
    public static void main(String[] args) {

        launch();
    }
}
