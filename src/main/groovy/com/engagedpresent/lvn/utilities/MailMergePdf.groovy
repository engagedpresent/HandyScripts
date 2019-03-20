package com.engagedpresent.lvn.utilities

import au.com.bytecode.opencsv.CSVReader
import com.itextpdf.text.DocumentException
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import org.apache.commons.lang.StringUtils


public class MailMergePdf {

    static String REGULAR_FONT = "/Users/nguyen/ZenPortal/IntelliJWorkspaces/triplemap/triplemap/web-app/font/OpenSans-Regular.ttf"
    public static String NAME_FONT = "/Users/nguyen/Downloads/Fonts/UVNBucThu.TTF"

    public void producePdf(BangKhenInfo bangKhenInfo) {
        try {
            PdfReader pdfReader = new PdfReader("/Users/nguyen/Downloads/LVN/BangKhen_${bangKhenInfo.achievement}.pdf")
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream("/Users/nguyen/Downloads/LVN/BangKhen/BangKhen_${bangKhenInfo.achievement}_${bangKhenInfo.serial}.pdf"))

            PdfContentByte content = pdfStamper.getOverContent(1)

            BaseFont scriptFont = BaseFont.createFont(NAME_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            content.beginText()
            content.setFontAndSize(scriptFont, 48)

            int nameY = (bangKhenInfo.achievement == "1") ? 346 : 345
            int serialY = (bangKhenInfo.achievement == "3") ? 512 : 509

            content.showTextAligned(PdfContentByte.ALIGN_CENTER, bangKhenInfo.name, 405, nameY, 0)
            content.endText();

            BaseFont regularFont = BaseFont.createFont(REGULAR_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            content.beginText()
            content.setFontAndSize(regularFont, 14)
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, bangKhenInfo.serial, 675, serialY, 0)
            content.endText();

            pdfStamper.close()

        } catch (IOException e) {
            e.printStackTrace()
        } catch (DocumentException e) {
            e.printStackTrace()
        }

    }


    public void producePdf(ChungChiInfo chungChiInfo) {
        try {
            PdfReader pdfReader = new PdfReader("/Users/nguyen/Downloads/LVN/ChungChi.pdf")
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream("/Users/nguyen/Downloads/LVN/ChungChi/ChungChi_${chungChiInfo.currentLevel}_${chungChiInfo.serial}.pdf"))

            PdfContentByte content = pdfStamper.getOverContent(1)
            BaseFont scriptFont = BaseFont.createFont(NAME_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            content.beginText()
            content.setFontAndSize(scriptFont, 48)
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, chungChiInfo.name, 405, 350, 0)
            content.endText();

            BaseFont regularFont = BaseFont.createFont(REGULAR_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            content.beginText()
            content.setFontAndSize(regularFont, 18)
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, "Cấp ${chungChiInfo.currentLevel}", 405, 289, 0)
            content.endText();

            content.beginText()
            content.setFontAndSize(regularFont, 18)
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, chungChiInfo.newLevel, 542, 232, 0)
            content.endText();

            content.beginText()
            content.setFontAndSize(regularFont, 14)
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, chungChiInfo.serial, 675, 515, 0)
            content.endText();

            pdfStamper.close()

        } catch (IOException e) {
            e.printStackTrace()
        } catch (DocumentException e) {
            e.printStackTrace()
        }
    }


    public void readFile(String filename) {
        def file = new File(filename)
        LineNumberReader fileReader = file.newReader()
        CSVReader reader = new CSVReader(fileReader, '\t'.toCharArray()[0])
        String[] currentLine
        def lineNumber = 0

        try {
            while ((currentLine = reader.readNext()) != null) {
                lineNumber++
                def cells = new ArrayList(Arrays.asList(currentLine))

                def currentLevel = StringUtils.trim(cells.get(0))
                def serial = StringUtils.trim(cells.get(1))
                def name = StringUtils.trim(cells.get(2))
                def newLevel = StringUtils.trim(cells.get(4))
                def achievement = StringUtils.trim(cells.get(5))

                if (currentLevel && serial && name && newLevel) {
                    def chungChiInfo = new ChungChiInfo(currentLevel: currentLevel, newLevel: newLevel, name: name, serial: serial)
                    // println "line $lineNumber:$chungChiInfo"
                    // producePdf(chungChiInfo)
                }

                if (achievement && name && serial) {
                    def bangKhenInfo = new BangKhenInfo(name: name, achievement: achievement, serial: serial)
                    println "line $lineNumber:$bangKhenInfo"
                    producePdf(bangKhenInfo)
                }
            }
        } catch (e) {
            println "Failed to convert at line: ${lineNumber}"
            e.printStackTrace()
        } finally {
            reader.close()
        }
    }

    public static void main(String[] args) {
        new MailMergePdf().readFile("/Users/nguyen/Downloads/LVN/ChungChiBangKhen.tsv")
    }
}

public class ChungChiInfo {
    String serial
    String name
    String currentLevel
    String newLevel

    String toString() {
        return "$serial:$name:$currentLevel:$newLevel"
    }
}

public class BangKhenInfo {
    String name
    String achievement
    String serial

    String toString() {
        return "$serial:$name:$achievement"
    }
}