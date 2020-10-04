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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
            Collections.sort(candidates);
            contents = new PDPageContentStream(doc, page);
            BufferedReader reader;
            PDFont font = PDType1Font.HELVETICA;
            try {
                reader = new BufferedReader(new FileReader(
                        "src\\evm\\templates\\default.txt"));
                String line = reader.readLine();
                while (line != null) {
                    // do stuff
                    String seg[] = line.split(",");

                    if (seg[0] == "font") {
                        if (seg[1] == "helvetica") {
                            font = PDType1Font.HELVETICA;
                        } else if (seg[1] == "helvetica_bold") {
                            font = PDType1Font.HELVETICA_BOLD;
                        }
                        // might need add more if required for certain ballot(s)
                    } else if (seg[0] == "fsize") {
                        contents.setFont(font, Integer.parseInt(seg[1]));
                    } else if (seg[0] == "text") {
                        contents.beginText();
                        contents.newLineAtOffset(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                        contents.showText(seg[3]);
                        contents.endText();
                    } else if (seg[0] == "image") {
                        PDImageXObject pdImage = PDImageXObject.createFromFile(seg[3], doc);
                        contents.drawImage(pdImage, Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                    } else if (seg[0] == "line") {
                        contents.moveTo(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                        contents.lineTo(Integer.parseInt(seg[3]), Integer.parseInt(seg[4]));
                        contents.stroke();
                    } else if (seg[0] == "box") {
                        for (int i = 0; i < candidates.size(); i++) {
                            // its looks complicated but its been found and replaced, check previous commit when it was hardcoded before if u need to edit
                            contents.moveTo(Integer.parseInt(seg[1]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i);
                            contents.lineTo(Integer.parseInt(seg[2]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i);
                            contents.stroke();
                            contents.moveTo(Integer.parseInt(seg[2]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i);
                            contents.lineTo(Integer.parseInt(seg[2]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i - (Integer.parseInt(seg[2]) - Integer.parseInt(seg[1])));
                            contents.stroke();
                            contents.moveTo(Integer.parseInt(seg[2]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i - (Integer.parseInt(seg[2]) - Integer.parseInt(seg[1])));
                            contents.lineTo(Integer.parseInt(seg[1]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i - (Integer.parseInt(seg[2]) - Integer.parseInt(seg[1])));
                            contents.stroke();
                            contents.moveTo(Integer.parseInt(seg[1]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i - (Integer.parseInt(seg[2]) - Integer.parseInt(seg[1])));
                            contents.lineTo(Integer.parseInt(seg[1]), Integer.parseInt(seg[3]) - Integer.parseInt(seg[4]) * i);
                            contents.stroke();
                        }
                    } else if (seg[0] == "cname") {
                        for (int i = 0; i < candidates.size(); i++) {
                            contents.beginText();
                            contents.newLineAtOffset(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]) - Integer.parseInt(seg[3]) * i);
                            contents.showText(candidates.get(i).getName());
                            contents.endText();
                        }
                    } else if (seg[0] == "cvote") {
                        for (int i = 0; i < candidates.size(); i++) {
                            int vote = currentVotes.get(candidates.get(i));
                            if (vote != Integer.MAX_VALUE) {
                                contents.beginText();
                                contents.newLineAtOffset(50, 530 - 70 * i);
                                contents.showText(vote + "");
                                contents.endText();
                            }
                        }
                    }

                    // read next line
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
