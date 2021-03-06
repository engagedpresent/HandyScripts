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

    Collection<String> getEntries(String filename) {
        new File(filename).readLines()
    }

    void createPdf(String inputFilename, String outputFilename) {
        Document document = new Document()
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFilename))
        document.open()

        Rectangle cropbox = new Rectangle(0.0, 0.0, 599.0, 792.0)
        //Rectangle cropbox = new Rectangle(0.0, 0.0, 640, 870)
        int left = cropbox.left
        int right = cropbox.right
        int top = cropbox.top
        int bottom = cropbox.bottom
        int midX = (right - left) / 2

        int unitPerInch = right / 8.5

        int marginX = ((8.5 / 2) - 3.5) * unitPerInch

        int imageYOffset = 75
        int nameYOffset = 45
        int activityYOffset = 25
        int groupYOffset = 75
        int groupXOffset = 5

        CMYKColor blackColor = new CMYKColor(0.75f, 0.68f, 0.67f, 0.89f)

        def entries = getEntries(inputFilename)
        int currentIndex = 0
        int pageNumber = 0
        int extraEmptyTags = 5

        while (currentIndex < (entries.size() + extraEmptyTags)) {
            pageNumber++

            boolean oddPage = (pageNumber % 2 == 1)

            println "Workinng on page ${pageNumber} - starting with $currentIndex"

            PdfContentByte canvas = writer.getDirectContent()
            if (oddPage) {
                canvas.setColorStroke(blackColor)

                canvas.moveTo(midX, bottom)
                canvas.lineTo(midX, top)
                canvas.closePathStroke()

                canvas.moveTo(left +  marginX, bottom)
                canvas.lineTo(left +  marginX, top)
                canvas.closePathStroke()

                canvas.moveTo(right -  marginX, bottom)
                canvas.lineTo(right -  marginX, top)
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
                    int centerX =  marginX + (cellWidth / 2)
                    int imageX = centerX - logoSize / 2
                    int imageY = posY + imageYOffset

                    int nameX = centerX
                    int nameY = posY + nameYOffset

                    int activityX = centerX
                    int activityY = posY + activityYOffset

                    int groupX =  marginX + cellWidth - groupXOffset
                    int groupY = posY + groupYOffset

                    while (column++ < 2) {

                        // write logo
                        Image logo = Image.getInstance(LOGO_PATH)
                        logo.setAbsolutePosition(imageX, imageY)
                        logo.scaleToFit(logoSize, logoSize)
                        document.add(logo)

                        // write name
                        BaseFont font = BaseFont.createFont(EXTRA_BOLD_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)

                        def participant = getParticipant(entries, currentIndex++, oddPage)
                        def fontSize = 32 //getFontSize(font, name)

                        def name = participant.name
                        canvas.beginText()
                        canvas.setFontAndSize(font, fontSize)
                        canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, name, nameX, nameY, 0)
                        canvas.endText()

                        def activity = participant.activity
                        canvas.beginText()
                        canvas.setFontAndSize(font, 14)
                        canvas.showTextAligned(PdfContentByte.ALIGN_CENTER, activity, activityX, activityY, 0)
                        canvas.endText()

                        def group = participant.group
                        canvas.beginText()
                        canvas.setFontAndSize(font, 10)
                        canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, group, groupX, groupY, 90)
                        canvas.endText()



                        imageX += cellWidth
                        nameX += cellWidth
                        activityX += cellWidth
                        groupX += cellWidth
                    }
                }

                y -= unitPerInch * 2.125

                if (oddPage) {
                    canvas.moveTo(left +  marginX, posY)
                    canvas.lineTo(right -  marginX, posY)
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

    Map getParticipant(Collection<String> entries, int index, boolean oddPage) {
        def newIndex = index
        if (!oddPage) {
            newIndex = (index % 2 == 0) ? index + 1 : index - 1
        }

        if (newIndex >= entries.size()) {
            return [name:'', group: '', activity: '']
        }

        def parts = entries[newIndex].split(',')
        return [name: parts[0], group: parts[1], activity: parts[2]]
    }

    static void main(String[] args) {
        ['A', 'B', 'C', 'D'].each { group ->
            new NameTagMaker().createPdf("/Users/nguyen/Projects/HandyScripts/2019LivingLifeToTheFullest_Group${group}.csv",
                    "2019LivingLifeToTheFullest_Group${group}.pdf")
        }
    }

}

