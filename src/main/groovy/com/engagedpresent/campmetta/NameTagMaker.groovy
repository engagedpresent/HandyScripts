package com.engagedpresent.campmetta

import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.CMYKColor
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter

class NameTagMaker {

    static int TAGS_PER_PAGE = 10
    static String LOGO_PATH = "/Users/nguyen/Dropbox/Camp Metta/CampMetta logo.png"
    static String REGULAR_FONT = "/Users/nguyen/Projects/HandyScripts/OpenSans-Regular.ttf"
    static String EXTRA_BOLD_FONT = "/Users/nguyen/Projects/HandyScripts/OpenSans-ExtraBold.ttf"

    Collection<String> getParticipants(String filename) {
        new File(filename).readLines()
    }

    void createPdf(String inputFilename, String outputFilename) {
        Document document = new Document()
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilename))
        document.open()

        Rectangle cropbox = new Rectangle(0.0, 0.0, 599.0, 792.0)
        int left = cropbox.left
        int right = cropbox.right
        int top = cropbox.top
        int bottom = cropbox.bottom
        int midX = cropbox.right / 2

        int unitPerInch = right / 8.5

        int marginX = ((8.5 / 2) - 3.5) * unitPerInch
        int imageYOffset = 75
        int nameYOffset = 25

        CMYKColor blackColor = new CMYKColor(0.75f, 0.68f, 0.67f, 0.89f)

        def participants = getParticipants(inputFilename)
        int currentIndex = 0
        int pageNumber = 0
        int extraEmptyTags = 5

        while (currentIndex < (participants.size() + extraEmptyTags)) {
            pageNumber++

            boolean oddPage = (pageNumber % 2 == 1)

            println "Workinng on page ${pageNumber} - starting with $currentIndex"

            PdfContentByte canvas = writer.getDirectContent()
            if (oddPage) {
                canvas.setColorStroke(blackColor)

                canvas.moveTo(midX, bottom)
                canvas.lineTo(midX, top)
                canvas.closePathStroke()

                canvas.moveTo(left + marginX, bottom)
                canvas.lineTo(left + marginX, top)
                canvas.closePathStroke()

                canvas.moveTo(right - marginX, bottom)
                canvas.lineTo(right - marginX, top)
                canvas.closePathStroke()
            }

            int cellWidth = midX - left - marginX
            int logoSize = 50

            int y = top
            int topOffset = 30


            while (y > 0) {
                int posY = y - topOffset

                if (y != top) {
                    int column = 0
                    int centerX = marginX + (cellWidth / 2)
                    int imageX = centerX - logoSize / 2
                    int imageY = posY + imageYOffset

                    int nameX = centerX
                    int nameY = posY + nameYOffset
                    while (column++ < 2) {

                        // write logo
                        Image logo = Image.getInstance(LOGO_PATH)
                        logo.setAbsolutePosition(imageX, imageY)
                        logo.scaleToFit(logoSize, logoSize)
                        document.add(logo)

                        // write name
                        BaseFont font = BaseFont.createFont(EXTRA_BOLD_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
                        canvas.beginText()

                        def name = getParticipant(participants, currentIndex++, oddPage)
                        def fontSize = getFontSize(font, name)
                        canvas.setFontAndSize(font, fontSize)

                        canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, name, nameX, nameY, 0)
                        canvas.endText()

                        imageX += cellWidth
                        nameX += cellWidth
                    }
                }

                y -= unitPerInch * 2

                if (oddPage) {
                    canvas.moveTo(left + marginX, posY)
                    canvas.lineTo(right - marginX, posY)
                    canvas.closePathStroke()
                }
            }

            document.newPage()

            if (pageNumber % 2 == 1) {
                currentIndex -= TAGS_PER_PAGE
            }
        }

        document.close()
    }

    int getFontSize(BaseFont font, String name) {
        int width = font.getWidth(name)
        int extra = 5000 - width
        int fontSize = 40 + (Math.ceil(extra/1000) * 3.5)
        //println "For ${name}:${width}, ${fontSize} is used"

        return fontSize
    }

    String getParticipant(Collection<String> participants, int index, boolean oddPage) {
        def newIndex = index
        if (!oddPage) {
            newIndex = (index % 2 == 0) ? index + 1 : index - 1
        }

        if (newIndex >= participants.size()) {
            return ''
        }

        return participants[newIndex]
    }

    static void main(String[] args) {
        new NameTagMaker().createPdf('/Users/nguyen/Projects/HandyScripts/2018DiscoverLivingDaisy.txt', '2018BodyMindSpirit_Daisy_Blue.pdf')
        new NameTagMaker().createPdf('/Users/nguyen/Projects/HandyScripts/2018DiscoverLivingPeony.txt', '2018BodyMindSpirit_Peony_Yellow.pdf')
    }

}

