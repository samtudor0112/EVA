import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " +
                javafxVersion + ", running on Java " +
                javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
        createPDF();
    }
    public static void main(String[] args) {

        launch();
    }

    public static void createPDF() {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PDPageContentStream contents;
        try {
            contents = new PDPageContentStream(doc, page);

            contents.beginText();
            PDFont font = PDType1Font.HELVETICA_BOLD;
            contents.setFont(font, 30);

            contents.newLineAtOffset(50, 700);
            contents.showText("Test");
            contents.endText();

            contents.close();
            doc.save("D:\\Files\\Desktop\\testpdf.pdf");
            doc.close();
        } catch (Exception e) {
            System.out.println("contents couldn't be created");
        }
    }
}
