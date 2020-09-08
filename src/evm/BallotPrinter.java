package evm;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;
import java.awt.print.PrinterJob;
import java.util.*;

public class BallotPrinter {

    /**
     * Creates a PDF from a list of candidates and prints it to the device's default printer
     * @param candidates a list of candidates that can be sorted into alphabetical order
     * @param currentVotes a mapping of candidates to how they've been preferenced by the voter
     */
    public static void createPDF(List<Candidate> candidates, Map<Candidate, Integer> currentVotes) {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PDPageContentStream contents;
        try {
            contents = new PDPageContentStream(doc, page);

            PDFont font = PDType1Font.HELVETICA_BOLD;
            contents.setFont(font, 30);

            Collections.sort(candidates);

            /* TODO make this look like an actual nice ballot */
            for (int i = 0; i < candidates.size(); i++) {
                contents.beginText();
                contents.newLineAtOffset(50, 700 - 50 * i);
                int vote = currentVotes.get(candidates.get(i));
                if (vote == Integer.MAX_VALUE) {
                    // Not sure what to do here
                    contents.showText(" " + candidates.get(i).getName());
                } else {
                    contents.showText(vote + " " + candidates.get(i).getName());
                }
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
            /* TODO closing program prematurely throws a PrinterAbortException which we need to handle */
            e.printStackTrace();
            //System.out.println("contents couldn't be created");
        }
    }
}
