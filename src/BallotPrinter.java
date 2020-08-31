import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BallotPrinter {

    public static void createPDF(HashMap<Candidate, Integer> currentVotes) {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PDPageContentStream contents;
        try {
            contents = new PDPageContentStream(doc, page);

            contents.beginText();
            PDFont font = PDType1Font.HELVETICA_BOLD;
            contents.setFont(font, 30);

            ArrayList<Candidate> candidates = new ArrayList<Candidate> (currentVotes.keySet());

            Collections.sort(candidates);

            for (int i = 0; i < candidates.size(); i++) {
                contents.newLineAtOffset(50, 700 + 50 * i);
                contents.showText(Integer.toString(currentVotes.get(candidates.indexOf(i))) + candidates.indexOf(i));
                contents.endText();
            }

            contents.close();
            //doc.save("D:\\Files\\Desktop\\testpdf.pdf");

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(doc));
            job.setPrintService(job.getPrintService()); //Use default printer
            job.print();

            doc.close();
        } catch (Exception e) {
            System.out.println("contents couldn't be created");
        }
    }
}
