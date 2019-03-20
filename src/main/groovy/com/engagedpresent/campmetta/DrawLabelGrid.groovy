package com.engagedpresent.campmetta

import com.itextpdf.text.DocumentException
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.CMYKColor
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import org.apache.commons.io.FilenameUtils


class DrawLabelGrid {
    void producePdf(String pdfFile) {
        try {
            PdfReader pdfReader = new PdfReader(pdfFile)
            String baseName = FilenameUtils.removeExtension(pdfFile)
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream("${baseName}_withLines.pdf"))

            Rectangle cropbox = pdfReader.getCropBox(1)
            int left = cropbox.left
            int right = cropbox.right
            int top = cropbox.top
            int bottom = cropbox.bottom
            int midX = cropbox.right / 2

            int unitPerInch = right / 8.5

            int marginX = ((8.5 / 2) - 3.5) * unitPerInch

            int numPages = pdfReader.getNumberOfPages()
            for (int pageNum = 1; pageNum <= numPages; pageNum += 2) {
                CMYKColor blackColor = new CMYKColor(0.75f, 0.68f, 0.67f, 0.89f)
                PdfContentByte canvas = pdfStamper.getOverContent(pageNum)
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

                int y = top
                int topOffset = 30
                while (y > 0) {
                    int posY = y - topOffset
                    y -= unitPerInch * 2
                    canvas.moveTo(left + marginX, posY)
                    canvas.lineTo(right - marginX, posY)
                    canvas.closePathStroke()
                }
            }
            pdfStamper.close()
        } catch (IOException e) {
            e.printStackTrace()
        } catch (DocumentException e) {
            e.printStackTrace()
        }

    }

    static void main(String[] args) {
        new File(args[0]).listFiles().each { File file ->
            if (file.isFile() && file.path.endsWith('.pdf') && !file.path.contains('_withLines')) {
                println "Working on ${file.path}"
                new DrawLabelGrid().producePdf(file.path)
            }
        }
    }
}

