package com.vedrax.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.commons.lang3.Validate;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

import static com.vedrax.util.NumUtils.toNumberFormat;

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

    /**
     * Method for displaying address
     *
     * @param companyName the company name
     * @param address     the company address
     * @param zip         the company postal code
     * @param city        the city of the company
     * @return the formatted company address
     */
    public static PdfPCell getAddress(String companyName,
                                      String address,
                                      String zip,
                                      String city) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
        cell.addElement(createParagraph(companyName, font12b));
        cell.addElement(createParagraph(address, font12));
        cell.addElement(createParagraph(String.format("%s %s", zip, city), font12));
        return cell;
    }

    /**
     * Method for creating a paragraph
     *
     * @param value the value to display
     * @param font  the font to be used
     * @return the paragraph
     */
    public static Paragraph createParagraph(String value, Font font) {
        value = value == null ? "" : value;

        if (font == null) {
            return new Paragraph(value);
        }

        return new Paragraph(value, font);
    }

    /**
     * Method for displaying NVP
     *
     * @param title               the bax title
     * @param values              the values to be displayed
     * @param horizontalAlignment the horizontal alignment
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getNameValuePair(String title,
                                             List<CellItem> values,
                                             int horizontalAlignment)
            throws DocumentException {
        return getOneColumnLayout(title, getNameValuePair(values, horizontalAlignment));
    }

    /**
     * Method for displaying values as NVP
     *
     * @param values              the values to be displayed
     * @param horizontalAlignment the horizontal alignment
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getNameValuePair(List<CellItem> values,
                                             int horizontalAlignment)
            throws DocumentException {
        Validate.notEmpty(values, "values must be provided");

        PdfPTable table = getTable(2, new int[]{1, 1}, Rectangle.NO_BORDER,
                horizontalAlignment, Element.ALIGN_LEFT);

        values.forEach(item -> {
            table.addCell(getCell(font10b, item.getKey(), Rectangle.NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, BaseColor.WHITE));
            table.addCell(getCell(item.isBold() ? font10b : font10, item.getValue(), Rectangle.NO_BORDER, horizontalAlignment, Element.ALIGN_MIDDLE, BaseColor.WHITE));
        });
        return table;
    }

    /**
     * Method for creating a one-column layout
     *
     * @param title the title
     * @param items the items to be displayed as table
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getOneColumnLayout(String title,
                                               PdfPTable items)
            throws DocumentException {
        Validate.notNull(title, "title must be provided");
        Validate.notNull(items, "items must be provided");

        PdfPTable table = getTable(1, new int[]{1}, Rectangle.BOX,
                Element.ALIGN_CENTER, Element.ALIGN_LEFT);

        table.addCell(getCell(font10b, title, Rectangle.BOX, Element.ALIGN_CENTER, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY));
        table.addCell(items);

        return table;
    }

    /**
     * Method for creating a one-column layout
     *
     * @param title the title
     * @param cell  the items to be displayed as cell
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getOneColumnLayout(String title,
                                               PdfPCell cell)
            throws DocumentException {
        Validate.notNull(title, "title must be provided");
        Validate.notNull(cell, "cell must be provided");

        PdfPTable table = getTable(1, new int[]{1}, Rectangle.BOX,
                Element.ALIGN_CENTER, Element.ALIGN_LEFT);
        table.setSpacingBefore(10f);

        table.addCell(getCell(font10b, title, Rectangle.BOX, Element.ALIGN_CENTER, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY));
        table.addCell(cell);

        return table;
    }

    /**
     * Method for inserting note
     *
     * @param note the note
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getNote(String note)
            throws DocumentException {
        PdfPTable table = getTable(1, new int[]{1}, Rectangle.NO_BORDER,
                Element.ALIGN_LEFT, Element.ALIGN_LEFT);

        table.addCell(getCell(font8, note, Rectangle.NO_BORDER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, BaseColor.WHITE));
        return table;
    }

    /**
     * Method for inserting note
     *
     * @param title the title
     * @param note  the note
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getNote(String title,
                                    String note)
            throws DocumentException {
        PdfPTable table = getOneColumnLayout(title, getCell(font8, note, Rectangle.BOX, Element.ALIGN_LEFT, Element.ALIGN_LEFT, BaseColor.WHITE));
        table.setSpacingBefore(10);
        return table;
    }

    /**
     * Method for creating a two-column layout
     *
     * @param left                the left side as table
     * @param right               the right side as table
     * @param border              the border type
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment   the vertical alignment
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getTwoColumnLayout(PdfPTable left,
                                               PdfPTable right,
                                               int border,
                                               int horizontalAlignment,
                                               int verticalAlignment) throws DocumentException {
        Validate.notNull(left, "left element must be provided");
        Validate.notNull(right, "Right element must be provided");

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

    /**
     * Method for creating a two-column layout
     *
     * @param left                the left side as table
     * @param right               the right side as cell
     * @param border              the border type
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment   the vertical alignment
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getTwoColumnLayout(PdfPTable left,
                                               PdfPCell right,
                                               int border,
                                               int horizontalAlignment,
                                               int verticalAlignment) throws DocumentException {
        Validate.notNull(left, "left element must be provided");
        Validate.notNull(right, "Right element must be provided");

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

    /**
     * Method for creating a table
     *
     * @param numColumns          the number of columns
     * @param widths              the widths of each column as an array of integer
     * @param border              the border type
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment   the vertical alignment
     * @return table
     * @throws DocumentException exception
     */
    public static PdfPTable getTable(int numColumns,
                                     int[] widths,
                                     int border,
                                     int horizontalAlignment,
                                     int verticalAlignment) throws DocumentException {
        Validate.isTrue(numColumns > 0, "numColumns must be greater than 0");
        Validate.isTrue(widths.length == numColumns, "The length of the widths values does not match");

        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.getDefaultCell().setBorder(border);
        table.getDefaultCell().setBorderColor(BaseColor.GRAY);
        table.getDefaultCell().setHorizontalAlignment(horizontalAlignment);
        table.getDefaultCell().setVerticalAlignment(verticalAlignment);
        return table;
    }

    /**
     * Method for creating a cell
     *
     * @param font                the font
     * @param text                the cell value
     * @param border              the border type
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment   the vertical alignment
     * @param backgroundColor     the background color
     * @return cell
     */
    public static PdfPCell getCell(Font font,
                                   String text,
                                   int border,
                                   int horizontalAlignment,
                                   int verticalAlignment,
                                   BaseColor backgroundColor) {
        Validate.notNull(font, "font must be provided");

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

    /**
     * Method for creating cell (header)
     *
     * @param title the cell value
     * @return cell
     */
    public static PdfPCell getCellAsTitle(String title) {
        return getCell(font8b, title, Rectangle.BOX, Element.ALIGN_CENTER, Element.ALIGN_CENTER, BaseColor.LIGHT_GRAY);
    }

    /**
     * Method for creating cell (value - text)
     *
     * @param text                the cell value as text
     * @param horizontalAlignment the horizontal alignment
     * @return cell
     */
    public static PdfPCell getCellAsDefault(String text, int horizontalAlignment) {
        return getCell(font8, text, Rectangle.BOX, horizontalAlignment, Element.ALIGN_CENTER, BaseColor.WHITE);
    }

    /**
     * Method for creating cell (value - number)
     *
     * @param value               the cell value as number
     * @param decimal             the decimal number
     * @param horizontalAlignment the horizontal alignment
     * @return cell
     */
    public static PdfPCell getCellAsDefault(BigDecimal value, int decimal, int horizontalAlignment) {
        return getCell(font8, toNumberFormat(value, decimal), Rectangle.BOX, horizontalAlignment, Element.ALIGN_CENTER, BaseColor.WHITE);
    }

}
