package com.vedrax.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.vedrax.util.CellItem;

import java.math.BigDecimal;
import java.util.List;

public interface PDFCreation {

    /**
     * Method for creating a table
     *
     * @param tableConfig the config
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable createTable(TableConfig tableConfig) throws DocumentException;

    /**
     * Method for getting a cell
     * @param value the value to display
     * @param bold is bold
     * @param size the size
     * @return cell
     */
    PdfPCell getCell(String value, boolean bold, int size);

    /**
     * Method for creating a table header cell
     *
     * @param title the title
     * @return cell
     */
    PdfPCell getCellAsTitle(String title);

    /**
     * Method for creating a cell for displaying textual value
     *
     * @param value               the value
     * @param horizontalAlignment the alignment
     * @return cell
     */
    PdfPCell getCellAsValue(String value, int horizontalAlignment);

    /**
     * Method for creating a cell for displaying numerical value
     *
     * @param value               the numerical value
     * @param decimal             the number of decimal
     * @param horizontalAlignment the alignment
     * @return cell
     */
    PdfPCell getCellAsValue(BigDecimal value, int decimal, int horizontalAlignment);

    /**
     * Method for getting font
     *
     * @param fontName font name
     * @param size     size
     * @return font
     */
    Font getFont(String fontName, float size);

    /**
     * Method for formatting an address
     *
     * @param addressFormat the address data
     * @return the formatting address as a  cell
     */
    PdfPCell createAddress(AddressFormat addressFormat);

    /**
     * Method for creating a paragraph
     *
     * @param value the text
     * @param font  the font
     * @return paragraph
     */
    Paragraph createParagraph(String value, Font font);

    /**
     * Method for formatting key/value as a list
     *
     * @param items the data
     * @param horizontalAlignment horizontal alignment
     * @return table of key/value as list
     * @throws DocumentException exception
     */
    PdfPTable createNameValuePair(List<CellItem> items, int horizontalAlignment) throws DocumentException;

    /**
     * Method for creating a card
     *
     * @param title the title
     * @param items the items as a table to be inserted
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable getCard(String title, PdfPTable items) throws DocumentException;

    /**
     * Method for creating a card
     *
     * @param title the title
     * @param nvp  the items as a cell to be inserted
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable getCard(String title, NameValuePairFormat nvp) throws DocumentException;

    /**
     * Method for creating a card
     *
     * @param title the title
     * @param cell  the items as a cell to be inserted
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable getCard(String title, PdfPCell cell) throws DocumentException;

    /**
     * Method for creating a card
     *
     * @param title the title
     * @param value  the value as a paragraph to be inserted
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable getCard(String title, String value) throws DocumentException;

    /**
     * Method for creating a 2 column layout
     *
     * @param left                the left side as table
     * @param right               the right side as table
     * @param border              the border
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment   the vertical alignment
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable getTwoColumnLayout(PdfPTable left,
                                 PdfPTable right,
                                 int border,
                                 int horizontalAlignment,
                                 int verticalAlignment) throws DocumentException;

    /**
     * Method for creating a 2 column layout
     *
     * @param left                the left side as table
     * @param right               the right side as cell
     * @param border              the border
     * @param horizontalAlignment the horizontal alignment
     * @param verticalAlignment   the vertical alignment
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable getTwoColumnLayout(PdfPTable left,
                                 PdfPCell right,
                                 int border,
                                 int horizontalAlignment,
                                 int verticalAlignment) throws DocumentException;

    /**
     * Method for creating a note
     *
     * @param note the note
     * @return table
     * @throws DocumentException exception
     */
    PdfPTable createNote(String note) throws DocumentException;

}
