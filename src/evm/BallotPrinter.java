package evm;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterAbortException;
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

            PDFont font = PDType1Font.HELVETICA;
            contents.setFont(font, 20);

            Collections.sort(candidates);

            // create top text stuff
            contents.beginText();
            contents.newLineAtOffset(200, 750);
            contents.showText("House of Representatives");
            contents.endText();

            contents.beginText();
            contents.newLineAtOffset(200, 720);
            contents.showText("Ballot Paper");
            contents.endText();

            // add images
            PDImageXObject pdImage = PDImageXObject.createFromFile("src\\evm\\img\\commonwealth-trans-bw-full.jpg", doc);
            contents.drawImage(pdImage, 10, 660);

            contents.setFont(font, 25);
            contents.beginText();
            contents.newLineAtOffset(70, 630);
            contents.showText("Number the boxes from 1 to 8 in");
            contents.endText();

            contents.beginText();
            contents.newLineAtOffset(70, 600);
            contents.showText("the order of your choice");
            contents.endText();


            // draw lines
            contents.moveTo(30, 590);
            contents.lineTo(580, 590);
            contents.stroke();

            contents.moveTo(30, 660);
            contents.lineTo(580, 660);
            contents.stroke();

            contents.setFont(font, 30);
            for (int i = 0; i < candidates.size(); i++) {
                int vote = currentVotes.get(candidates.get(i));
                // draw box
                // basically etch-a-sketch
                contents.moveTo(35, 565 - 70 * i);
                contents.lineTo(85, 565 - 70 * i);
                contents.stroke();
                contents.moveTo(85, 565 - 70 * i);
                contents.lineTo(85, 565 - 70 * i - 50);
                contents.stroke();
                contents.moveTo(85, 565 - 70 * i - 50);
                contents.lineTo(35, 565 - 70 * i - 50);
                contents.stroke();
                contents.moveTo(35, 565 - 70 * i - 50);
                contents.lineTo(35, 565 - 70 * i);
                contents.stroke();

                // candidate name
                contents.beginText();
                contents.newLineAtOffset(100, 530 - 70 * i);
                contents.showText(candidates.get(i).getName());
                contents.endText();

                // candidate vote
                if (vote != Integer.MAX_VALUE) {
                    contents.beginText();
                    contents.newLineAtOffset(50, 530 - 70 * i);
                    contents.showText(vote + "");
                    contents.endText();
                }
            }

            // draw bottom part
            contents.moveTo(30, 50);
            contents.lineTo(580, 50);
            contents.stroke();

            contents.setFont(font, 15);
            contents.beginText();
            contents.newLineAtOffset(100, 30);
            contents.showText("Remember... number every box to make your vote count");
            contents.endText();

            contents.close();
            //doc.save("D:\\Files\\Desktop\\testpdf.pdf");

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(doc));
            job.setPrintService(job.getPrintService()); //Use default printer
            job.print();

            doc.close();
        } catch (PrinterAbortException e ) {
            /* do nothing */
        } catch (Exception e) {
            /* any other exception print a stack trace */
            e.printStackTrace();
        }
    }
}
