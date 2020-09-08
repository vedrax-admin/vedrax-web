package com.vedrax.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.springframework.util.Assert;

import java.util.List;

public class PdfUtils {

    public static Font font6 = FontFactory.getFont(FontFactory.COURIER, 6, BaseColor.BLACK);
    public static Font font7 = FontFactory.getFont(FontFactory.COURIER, 7, BaseColor.BLACK);
    public static Font font8 = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);
    public static Font font8b = FontFactory.getFont(FontFactory.COURIER_BOLD, 8, BaseColor.BLACK);
    public static Font font9 = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
    public static Font font10 = FontFactory.getFont(FontFactory.COURIER, 10, BaseColor.BLACK);
    public static Font font10o = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.BLACK);
    public static Font font10b = FontFactory.getFont(FontFactory.COURIER_BOLD, 10, BaseColor.BLACK);
    public static Font font12 = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
    public static Font font12Red = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.RED);
    public static Font font12o = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 12, BaseColor.BLACK);
    public static Font font12b = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.BLACK);
    public static Font font12bRed = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.RED);
    public static Font font14 = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
    public static Font font14o = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 14, BaseColor.BLACK);
    public static Font font14b = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);

    public static PdfPCell getAddress(String companyName, String address, String zip, String city) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
        Paragraph p = new Paragraph(companyName, font12b);
        cell.addElement(p);
        p = new Paragraph(address, font12);
        cell.addElement(p);
        p = new Paragraph(String.format("%s %s", zip, city), font12);
        cell.addElement(p);
        return cell;
    }

    public static PdfPCell getAddressRed(String companyName, String address, String zip, String city) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
        Paragraph p = new Paragraph(companyName, font12bRed);
        cell.addElement(p);
        p = new Paragraph(address, font12Red);
        cell.addElement(p);
        p = new Paragraph(String.format("%s %s", zip, city), font12Red);
        cell.addElement(p);
        return cell;
    }

    public static PdfPTable getNameValuePair(String title, List<CellItem> values, int verticalAlignment) throws DocumentException {
        PdfPTable nvps = getNameValuePair(values, verticalAlignment);
        return getOneColumnLayout(title, nvps);
    }

    public static PdfPTable getNote(String note) throws DocumentException {
        PdfPTable table = getTable(1, new int[]{1}, Rectangle.NO_BORDER,
                Element.ALIGN_LEFT, Element.ALIGN_LEFT);

        table.addCell(getCell(font8, note, Rectangle.NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, BaseColor.WHITE));
        return table;
    }

    public static PdfPTable getNote(String title, String note) throws DocumentException {
        return getOneColumnLayout(title, getCell(font8, note, Rectangle.BOX, Element.ALIGN_LEFT, Element.ALIGN_LEFT, BaseColor.WHITE));
    }

    public static PdfPTable getNameValuePair(List<CellItem> values, int horizontalAlignment) throws DocumentException {
        Assert.notEmpty(values, "values must be provided");

        PdfPTable table = getTable(2, new int[]{1, 1}, Rectangle.NO_BORDER,
                horizontalAlignment, Element.ALIGN_LEFT);

        values.forEach(item -> {
            table.addCell(getCell(font10b, item.getKey(), Rectangle.NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, BaseColor.WHITE));
            table.addCell(getCell(item.isBold() ? font10b : font10, item.getValue(), Rectangle.NO_BORDER, horizontalAlignment, Element.ALIGN_MIDDLE, BaseColor.WHITE));
        });
        return table;
    }

    public static PdfPTable getOneColumnLayout(String title, PdfPTable items) throws DocumentException {
        Assert.notNull(title, "title must be provided");
        Assert.notNull(items, "items must be provided");

        PdfPTable table = getTable(1, new int[]{1}, Rectangle.BOX,
                Element.ALIGN_CENTER, Element.ALIGN_LEFT);
        //table.setSpacingBefore(10f);

        table.addCell(getCell(font10b, title, Rectangle.BOX, Element.ALIGN_CENTER, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY));
        table.addCell(items);

        return table;
    }

    public static PdfPTable getOneColumnLayout(String title, PdfPCell cell) throws DocumentException {
        Assert.notNull(title, "title must be provided");
        Assert.notNull(cell, "cell must be provided");

        PdfPTable table = getTable(1, new int[]{1}, Rectangle.BOX,
                Element.ALIGN_CENTER, Element.ALIGN_LEFT);
        table.setSpacingBefore(10f);

        table.addCell(getCell(font10b, title, Rectangle.BOX, Element.ALIGN_CENTER, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY));
        table.addCell(cell);

        return table;
    }


    public static PdfPTable getTwoColumnLayout(PdfPTable left,
                                               PdfPTable right,
                                               int border,
                                               int horizontalAlignment,
                                               int verticalAlignment) throws DocumentException {
        Assert.notNull(left, "left element must be provided");
        Assert.notNull(right, "Right element must be provided");

        PdfPTable table = getTable(3, new int[]{10, 1, 10}, border,
                horizontalAlignment, verticalAlignment);

        table.setSpacingBefore(10f);

        //right
        table.addCell(left);

        //middle
        PdfPCell middleCell = new PdfPCell();
        middleCell.setBorder(0);
        table.addCell(middleCell);

        //left
        table.addCell(right);

        return table;
    }

    public static PdfPTable getTwoColumnLayout(PdfPTable left,
                                               PdfPCell right,
                                               int border,
                                               int horizontalAlignment,
                                               int verticalAlignment) throws DocumentException {
        Assert.notNull(left, "left element must be provided");
        Assert.notNull(right, "Right element must be provided");

        PdfPTable table = getTable(3, new int[]{10, 1, 10}, border,
                horizontalAlignment, verticalAlignment);

        //right
        table.addCell(left);

        //middle
        PdfPCell middleCell = new PdfPCell();
        middleCell.setBorder(0);
        table.addCell(middleCell);

        //left
        table.addCell(right);
        return table;
    }

    public static PdfPTable getTable(int numColumns,
                                     int[] widths,
                                     int border,
                                     int horizontalAlignment,
                                     int verticalAlignment) throws DocumentException {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.getDefaultCell().setBorder(border);
        table.getDefaultCell().setBorderColor(BaseColor.GRAY);
        table.getDefaultCell().setHorizontalAlignment(horizontalAlignment);
        table.getDefaultCell().setVerticalAlignment(verticalAlignment);
        return table;
    }

    public static PdfPCell getCell(Font font,
                                   String text,
                                   int border,
                                   int horizontalAlignment,
                                   int verticalAlignment,
                                   BaseColor backgroundColor) {
        Assert.notNull(font, "font must be provided");

        text = text == null ? "" : text;
        backgroundColor = backgroundColor == null ? BaseColor.WHITE : backgroundColor;

        FontSelector fs = new FontSelector();
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(3.0f);
        cell.setBorder(border);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(horizontalAlignment);

        if (verticalAlignment == Element.ALIGN_CENTER) {
            cell.setUseAscender(true);
        }

        cell.setVerticalAlignment(verticalAlignment);
        cell.setBackgroundColor(backgroundColor);
        return cell;
    }

    public static PdfPCell getCellAsTitle(String title) {
        return getCell(font10b, title, Rectangle.BOX, Element.ALIGN_CENTER, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
    }

    public static PdfPCell getCellAsDefault(String text, int border, int horizontalAlignment){
        return getCell(font10, text, border, horizontalAlignment, Element.ALIGN_CENTER, BaseColor.WHITE);
    }

}
