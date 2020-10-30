package evm;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;

import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BallotPrinter {

    /**
     * Creates a PDF from a voting model and prints it to the device's default printer
     * @param currentModel the voting model to print from
     * @param portrait whether to print portrait
     * @param parties the map of party names to their candidates as a list
     * @param partyNames the list of party names
     * @param senateModel whether the model is a senate model
     * @param aboveLine whether we are voting above or below the line
     * @param template which template to print with
     */
    public static void createPDF(VotingModel currentModel, Boolean portrait, Map<String, List<Candidate>> parties, List<String> partyNames, boolean senateModel, boolean aboveLine, String template) {
        List<Candidate> candidates = currentModel.getCandidateList();
        Map<Candidate, Integer> currentVotes = currentModel.getFullMap();

        PDDocument doc = new PDDocument();
        PDPage page;
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        if (portrait) {
            page = new PDPage();
        } else {
            page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        }
        doc.addPage(page);
        PDPageContentStream contents;
        try {
            candidates.sort(Comparator.comparing(Candidate::getName));
            if (!aboveLine) {
                candidates.sort(Comparator.comparing(Candidate::getParty));
            }

            contents = new PDPageContentStream(doc, page);
            BufferedReader reader;
            PDFont font = PDType1Font.HELVETICA;
            try {
                reader = new BufferedReader(new FileReader(template));
                String line = reader.readLine();
                while (line != null) {
                    // do stuff
                    String seg[] = line.split(",");

                    switch(seg[0]) {
                        case "font":
                            if (seg[1].equals("helvetica")) {
                                font = PDType1Font.HELVETICA;
                            } else if (seg[1].equals("helvetica_bold")) {
                                font = PDType1Font.HELVETICA_BOLD;
                            }
                            // might need add more if required for certain ballot(s)
                            break;
                        case "fsize":
                            contents.setFont(font, Integer.parseInt(seg[1]));
                            break;
                        case "text":
                            contents.beginText();
                            contents.newLineAtOffset(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                            contents.showText(seg[3]);
                            contents.endText();
                            break;
                        case "image":
                            seg[3] = seg[3].replace("\\", File.separator);
                            PDImageXObject pdImage = PDImageXObject.createFromFile(seg[3], doc);
                            contents.drawImage(pdImage, Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                            break;
                        case "line":
                            contents.moveTo(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                            contents.lineTo(Integer.parseInt(seg[3]), Integer.parseInt(seg[4]));
                            contents.stroke();
                            break;
                        case "box":
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
                            break;
                        case "cname":
                            for (int i = 0; i < candidates.size(); i++) {
                                contents.beginText();
                                contents.newLineAtOffset(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]) - Integer.parseInt(seg[3]) * i);
                                contents.showText(candidates.get(i).getName());
                                contents.endText();
                            }
                            break;
                        case "cvote":
                            for (int i = 0; i < candidates.size(); i++) {
                                int vote = currentVotes.get(candidates.get(i));
                                if (vote != Integer.MAX_VALUE) {
                                    contents.beginText();
                                    contents.newLineAtOffset(50, 530 - 70 * i);
                                    contents.showText(vote + "");
                                    contents.endText();
                                }
                            }
                            break;
                        case "area":
                            contents.beginText();
                            contents.newLineAtOffset(Integer.parseInt(seg[1]), Integer.parseInt(seg[2]));
                            contents.showText(currentModel.getBallot().getName());
                            contents.endText();
                            break;
                        case "senate":
                            // map for parties
                            int x1 = Integer.parseInt(seg[1]);
                            int y2 = Integer.parseInt(seg[2]);
                            int pWidth3 = Integer.parseInt(seg[3]);
                            int pHeight4 = Integer.parseInt(seg[4]);
                            int pName5 = Integer.parseInt(seg[5]);
                            int pBox6 = Integer.parseInt(seg[6]);
                            int pSpac7 = Integer.parseInt(seg[7]);
                            int partyNo = 0;
                            // draw left column
                            contents.moveTo(x1, y2);
                            contents.lineTo(x1, 10);
                            contents.stroke();
                            int doubleUpRem = 0;
                            //draw below line stuff
                            while (partyNo + doubleUpRem < candidates.size()) {
                                contents.setFont(font, 10);
                                System.out.println(candidates.get(partyNo + doubleUpRem) + " = " + parties.get(candidates.get(partyNo + doubleUpRem).getParty()));
                                if (!aboveLine) {
                                    doubleUpRem += parties.get(candidates.get(partyNo + doubleUpRem).getParty()).size() - 1;
                                }
                                System.out.println(doubleUpRem);
                                // draw right column
                                contents.moveTo(x1 + pWidth3 * (partyNo + 1), y2);
                                contents.lineTo(x1 + pWidth3 * (partyNo + 1), 10);
                                contents.stroke();
                                // draw letters
                                contents.beginText();
                                contents.newLineAtOffset(x1 + 5 + pWidth3 * (partyNo), y2 - 10);
                                contents.showText(String.valueOf(alphabet.charAt(partyNo)) + ".");
                                contents.endText();


                                String partyName;
                                // draw party name
                                if (aboveLine) {
                                    partyName = candidates.get(partyNo).getName();
                                } else {
                                    partyName = candidates.get(partyNo + doubleUpRem).getParty();
                                }
                                // wrap party name //////////
                                font = PDType1Font.HELVETICA_BOLD;
                                contents.setFont(font, 10);
                                String[] partynameParts = partyName.split(" ");
                                int linesize = 0;
                                int partpointer = 0;
                                int linenum = 0;
                                String drawnpartyName = "";
                                while (partpointer < partynameParts.length) {
                                    if (linesize + partynameParts[partpointer].length() <= 20 || linesize == 0) {
                                        linesize += partynameParts[partpointer].length();
                                        drawnpartyName += partynameParts[partpointer] + " ";
                                        partpointer++;
                                    } else {
                                        // below
                                        contents.beginText();
                                        contents.newLineAtOffset(x1 + pWidth3 * partyNo + 3, y2 - pName5 + (-12 * linenum));
                                        contents.showText(drawnpartyName);
                                        contents.endText();
                                        //above
                                        contents.beginText();
                                        contents.newLineAtOffset(x1 + pWidth3 * partyNo + 3, y2 - pName5 + (-12 * linenum) + 40);
                                        contents.showText(drawnpartyName);
                                        contents.endText();
                                        linenum++;
                                        drawnpartyName = "";
                                        linesize = 0;
                                    }
                                }
                                // draw last line
                                contents.beginText();
                                contents.newLineAtOffset(x1 + pWidth3 * partyNo + 3, y2 - pName5 + (-12 * linenum));
                                contents.showText(drawnpartyName);
                                contents.endText();
                                contents.beginText();
                                contents.newLineAtOffset(x1 + pWidth3 * partyNo + 3, y2 - pName5 + (-12 * linenum) + 40);
                                contents.showText(drawnpartyName);
                                contents.endText();
                                font = PDType1Font.HELVETICA;
                                ///////////////////////////

                                //contents.beginText();
                                //contents.newLineAtOffset(x1 + pWidth3 * partyNo + 10, y2 - pname5);
                                //contents.showText(partyname);
                                //contents.endText();

                                // draw candidates
                                List<Candidate> partyCandidates;
                                if (aboveLine) {
                                    partyCandidates = (List<Candidate>) parties.get(candidates.get(partyNo).getName());
                                } else {
                                    partyCandidates = (List<Candidate>) parties.get(candidates.get(partyNo + doubleUpRem).getParty());
                                }
                                for (int c = 0; c < partyCandidates.size(); ++c) {
                                    contents.setFont(font, 10);
                                    // draw candidate name
                                    contents.beginText();
                                    contents.newLineAtOffset(x1 + pWidth3 * partyNo + pBox6 + 8, y2 - pHeight4 - (pBox6 + pSpac7) * c);
                                    contents.showText(partyCandidates.get(c).getName().split(" ", 2)[0]);
                                    contents.endText();
                                    if (partyCandidates.get(c).getName().split(" ", 2).length >= 2){
                                        contents.beginText();
                                        contents.newLineAtOffset(x1 + pWidth3 * partyNo + pBox6 + 8, y2 - pHeight4 - (pBox6 + pSpac7) * c - 15);
                                        contents.showText(partyCandidates.get(c).getName().split(" ", 2)[1]);
                                        contents.endText();
                                    }
                                    // draw box (end me)
                                    contents.moveTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.lineTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.stroke();
                                    contents.moveTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.lineTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.stroke();
                                    contents.moveTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.lineTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.stroke();
                                    contents.moveTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.lineTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.stroke();
                                    // write vote number in box
                                    if (!aboveLine) {
                                        try {
                                            int vote = currentVotes.get(partyCandidates.get(c));
                                            if (vote != Integer.MAX_VALUE) {
                                                contents.setFont(font, 30);
                                                contents.beginText();
                                                contents.newLineAtOffset(x1 + pWidth3 * partyNo + 14, y2 - pHeight4 - (pBox6 + pSpac7) * c - 17);
                                                contents.showText(vote + "");
                                                contents.endText();
                                            }
                                        } catch (NullPointerException ne) {
                                        }
                                    }
                                }
                                if (!aboveLine) {
                                    // draw above line box but leave blank
                                    float c = (float) -2.6;
                                    contents.moveTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.lineTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.stroke();
                                    contents.moveTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.lineTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.stroke();
                                    contents.moveTo(x1 + pWidth3 * partyNo + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.lineTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.stroke();
                                    contents.moveTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                    contents.lineTo(x1 + pWidth3 * partyNo + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                    contents.stroke();
                                }
                                partyNo++;
                            }
                            //draw above line stuff
                            if (aboveLine) {
                                Iterator prtys = parties.entrySet().iterator();
                                int i = 0;
                                while (prtys.hasNext()) {
                                    int vote = 0;
                                    Map.Entry pair = (Map.Entry) prtys.next();
                                    int x = 0;
                                    while (candidates.get(x).getName() != pair.getKey()) {
                                        x++;
                                        if (x >= candidates.size()) {
                                            break;
                                        }
                                    }
                                    if (x < candidates.size()) {
                                        vote = currentVotes.get(candidates.get(x));
                                        float c = (float) -2.6;
                                        contents.moveTo(x1 + pWidth3 * i + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                        contents.lineTo(x1 + pWidth3 * i + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                        contents.stroke();
                                        contents.moveTo(x1 + pWidth3 * i + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                        contents.lineTo(x1 + pWidth3 * i + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                        contents.stroke();
                                        contents.moveTo(x1 + pWidth3 * i + pBox6 + 4, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                        contents.lineTo(x1 + pWidth3 * i + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                        contents.stroke();
                                        contents.moveTo(x1 + pWidth3 * i + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10 - pBox6);
                                        contents.lineTo(x1 + pWidth3 * i + 6, y2 - pHeight4 - (pBox6 + pSpac7) * c + 10);
                                        contents.stroke();
                                        if (vote != Integer.MAX_VALUE) {
                                            contents.setFont(font, 30);
                                            contents.beginText();
                                            contents.newLineAtOffset(x1 + pWidth3 * x + 14, y2 - pHeight4 - (pBox6 + pSpac7) * -2 + 8);
                                            contents.showText(vote + "");
                                            contents.endText();
                                        }
                                        i++;
                                    }
                                }
                            }
                            break;
                        default:
                            // Just go to the next line
                            break;
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
