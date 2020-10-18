package com.vedrax.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.vedrax.util.CellItem;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.vedrax.util.NumUtils.toNumberFormat;

@Service
public class PDFCreationImpl implements PDFCreation {

    @Override
    public PdfPTable createTable(TableConfig tableConfig) throws DocumentException {
        Validate.notNull(tableConfig, "tableConfig must be provided");
        Validate.isTrue(tableConfig.getNumColumns() > 0, "numColumns must be greater than 0");
        Validate.isTrue(tableConfig.getWidths().length == tableConfig.getNumColumns(), "The length of the widths values does not match");

        PdfPTable table = new PdfPTable(tableConfig.getNumColumns());
        table.setWidthPercentage(100);
        table.setWidths(tableConfig.getWidths());
        table.getDefaultCell().setBorder(tableConfig.getBorder());
        table.getDefaultCell().setBorderColor(BaseColor.GRAY);
        table.getDefaultCell().setHorizontalAlignment(tableConfig.getHorizontalAlignment());
        table.getDefaultCell().setVerticalAlignment(tableConfig.getVerticalAlignment());
        return table;
    }

    @Override
    public Font getFont(String fontName, float size) {
        Validate.notNull(fontName, "fontName must be provided");
        Validate.isTrue(size > 0, "size must be greater than 0");

        return FontFactory.getFont(fontName, size, BaseColor.BLACK);
    }

    @Override
    public PdfPCell createAddress(AddressFormat addressFormat) {
        Validate.notNull(addressFormat, "addressFormat must be provided");

        Font normalFont = getFont(FontFactory.COURIER, 12);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
        cell.addElement(createParagraph(addressFormat.getCompanyName(), getFont(FontFactory.COURIER_BOLD, 12)));
        cell.addElement(createParagraph(addressFormat.getAddress(), normalFont));
        cell.addElement(createParagraph(String.format("%s %s", addressFormat.getZip(), addressFormat.getCity()), normalFont));
        return cell;
    }

    @Override
    public Paragraph createParagraph(String value, Font font) {
        value = value == null ? "" : value;

        if (font == null) {
            return new Paragraph(value);
        }

        return new Paragraph(value, font);
    }

    @Override
    public PdfPTable createNameValuePair(List<CellItem> items, int horizontalAlignment) throws DocumentException {
        Validate.notEmpty(items, "No values provided");

        PdfPTable table = createTable(getTableConfigForNVP());

        CellConfig keyConfig = getCellConfigForNVP(true, Element.ALIGN_LEFT);
        CellConfig valueConfig = getCellConfigForNVP(false, horizontalAlignment);

        for (CellItem item : items) {
            addKeyToNVP(keyConfig, table, item);
            addValueToNVP(valueConfig, table, item);
        }

        return table;
    }

    private TableConfig getTableConfigForNVP() {
        TableConfig tableConfig = new TableConfig();
        tableConfig.setNumColumns(2);
        tableConfig.setWidths(new int[]{1, 1});
        tableConfig.setBorder(Rectangle.NO_BORDER);
        tableConfig.setHorizontalAlignment(Element.ALIGN_LEFT);
        tableConfig.setVerticalAlignment(Element.ALIGN_LEFT);
        return tableConfig;
    }

    private CellConfig getCellConfigForNVP(boolean isBold, int horizontalAlignment) {
        CellConfig cellConfig = new CellConfig();
        cellConfig.setFont(getBoldOrNormalFontForNVP(isBold));
        cellConfig.setHorizontalAlignment(horizontalAlignment);
        cellConfig.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cellConfig;
    }

    private void addKeyToNVP(CellConfig cellConfig, PdfPTable table, CellItem item) {
        cellConfig.setText(item.getKey());
        table.addCell(createCell(cellConfig));
    }

    private void addValueToNVP(CellConfig cellConfig, PdfPTable table, CellItem item) {
        cellConfig.setText(item.getValue());
        cellConfig.setFont(getBoldOrNormalFontForNVP(item.isBold()));
        table.addCell(createCell(cellConfig));
    }

    private Font getBoldOrNormalFontForNVP(boolean isBold) {
        return getFont(isBold ? FontFactory.COURIER_BOLD : FontFactory.COURIER, 10);
    }

    private PdfPCell createCell(CellConfig cellConfig) {
        Validate.notNull(cellConfig, "cellConfig must be provided");

        if (cellConfig.getText() == null) {
            cellConfig.setText("");
        }

        if (cellConfig.getBackgroundColor() == null) {
            cellConfig.setBackgroundColor(BaseColor.WHITE);
        }

        if (cellConfig.getFont() == null) {
            cellConfig.setFont(getFont(FontFactory.COURIER, 12));
        }

        FontSelector fs = new FontSelector();
        fs.addFont(cellConfig.getFont());
        Phrase phrase = fs.process(cellConfig.getText());
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(3.0f);
        cell.setHorizontalAlignment(cellConfig.getHorizontalAlignment());

        if (cellConfig.getVerticalAlignment() == Element.ALIGN_CENTER) {
            cell.setUseAscender(true);
        }

        cell.setVerticalAlignment(cellConfig.getVerticalAlignment());
        cell.setBackgroundColor(cellConfig.getBackgroundColor());
        return cell;
    }

    @Override
    public PdfPTable getCard(String title, NameValuePairFormat nvp) throws DocumentException {
        return getCard(title, createNameValuePair(nvp.getValues(), nvp.getHorizontalAlignment()));
    }

    @Override
    public PdfPTable getCard(String title, PdfPTable items)
            throws DocumentException {
        Validate.notNull(items, "items must be provided");

        PdfPTable table = getTableWithHeader(title);
        table.addCell(items);
        return table;
    }

    @Override
    public PdfPTable getCard(String title, PdfPCell cell)
            throws DocumentException {
        Validate.notNull(cell, "cell must be provided");

        PdfPTable table = getTableWithHeader(title);
        table.addCell(cell);
        return table;
    }

    @Override
    public PdfPTable getCard(String title, String value)
            throws DocumentException {
        Validate.notNull(value, "value must be provided");

        PdfPTable table = getTableWithHeader(title);
        table.addCell(createParagraph(value, getFont(FontFactory.COURIER, 8)));
        return table;
    }

    private PdfPTable getTableWithHeader(String title) throws DocumentException {
        Validate.notNull(title, "title must be provided");

        PdfPTable table = createTable(getTableConfigForCard(Rectangle.BOX, Element.ALIGN_CENTER));
        table.addCell(createCell(getCellConfigForCard(title)));
        return table;
    }

    private TableConfig getTableConfigForCard(int border, int horizontalAlignment) {
        TableConfig tableConfig = new TableConfig();
        tableConfig.setNumColumns(1);
        tableConfig.setWidths(new int[]{1});
        tableConfig.setBorder(border);
        tableConfig.setHorizontalAlignment(horizontalAlignment);
        tableConfig.setVerticalAlignment(Element.ALIGN_LEFT);
        return tableConfig;
    }

    private CellConfig getCellConfigForCard(String title) {
        CellConfig cellConfig = new CellConfig();
        cellConfig.setText(title);
        cellConfig.setFont(getBoldOrNormalFontForNVP(true));
        cellConfig.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellConfig.setVerticalAlignment(Element.ALIGN_CENTER);
        return cellConfig;
    }

    @Override
    public PdfPTable getTwoColumnLayout(PdfPTable left,
                                        PdfPTable right,
                                        int border,
                                        int horizontalAlignment,
                                        int verticalAlignment) throws DocumentException {
        Validate.notNull(left, "left element must be provided");
        Validate.notNull(right, "Right element must be provided");

        PdfPTable table = createTable(getTableConfigForTwoColumnLayout(
                border, horizontalAlignment, verticalAlignment));
        table.addCell(left);
        addSeparator(table);
        table.addCell(right);
        return table;
    }

    @Override
    public PdfPTable getTwoColumnLayout(PdfPTable left,
                                        PdfPCell right,
                                        int border,
                                        int horizontalAlignment,
                                        int verticalAlignment) throws DocumentException {
        Validate.notNull(left, "left element must be provided");
        Validate.notNull(right, "Right element must be provided");

        PdfPTable table = createTable(getTableConfigForTwoColumnLayout(
                border, horizontalAlignment, verticalAlignment));
        table.addCell(left);
        addSeparator(table);
        table.addCell(right);
        return table;
    }

    private TableConfig getTableConfigForTwoColumnLayout(int border, int horizontalAlignment,
                                                         int verticalAlignment) {
        TableConfig tableConfig = new TableConfig();
        tableConfig.setNumColumns(3);
        tableConfig.setWidths(new int[]{10, 1, 10});
        tableConfig.setBorder(border);
        tableConfig.setHorizontalAlignment(horizontalAlignment);
        tableConfig.setVerticalAlignment(verticalAlignment);
        return tableConfig;
    }

    private void addSeparator(PdfPTable table) {
        PdfPCell middleCell = new PdfPCell();
        middleCell.setBorder(0);
        table.addCell(middleCell);
    }

    @Override
    public PdfPCell getCellAsTitle(String title) {
        Validate.notNull(title, "title must be provided");

        return createCell(getCellConfigForTitle(title));
    }

    private CellConfig getCellConfigForTitle(String title) {
        CellConfig cellConfig = new CellConfig();
        cellConfig.setText(title);
        cellConfig.setFont(getFont(FontFactory.COURIER_BOLD, 8));
        cellConfig.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellConfig.setVerticalAlignment(Element.ALIGN_CENTER);
        cellConfig.setBackgroundColor(BaseColor.LIGHT_GRAY);
        return cellConfig;
    }

    @Override
    public PdfPCell getCellAsValue(String value, int horizontalAlignment) {
        return createCell(getCellConfigForValue(value, horizontalAlignment));
    }

    private CellConfig getCellConfigForValue(String value, int horizontalAlignment) {
        CellConfig cellConfig = new CellConfig();
        cellConfig.setText(value == null ? "" : value);
        cellConfig.setFont(getFont(FontFactory.COURIER, 8));
        cellConfig.setHorizontalAlignment(horizontalAlignment);
        cellConfig.setVerticalAlignment(Element.ALIGN_CENTER);
        return cellConfig;
    }

    public PdfPCell getCellAsValue(BigDecimal value, int decimal, int horizontalAlignment) {
        return createCell(getCellConfigForValue(value, decimal, horizontalAlignment));
    }

    private CellConfig getCellConfigForValue(BigDecimal value, int decimal, int horizontalAlignment) {
        CellConfig cellConfig = new CellConfig();
        cellConfig.setText(toNumberFormat(value, decimal));
        cellConfig.setFont(getFont(FontFactory.COURIER, 8));
        cellConfig.setHorizontalAlignment(horizontalAlignment);
        cellConfig.setVerticalAlignment(Element.ALIGN_CENTER);
        return cellConfig;
    }

    @Override
    public PdfPTable createNote(String note) throws DocumentException {
        PdfPTable table = createTable(getTableConfigForCard(Rectangle.NO_BORDER, Element.ALIGN_LEFT));
        table.addCell(createParagraph(note, getFont(FontFactory.COURIER, 8)));
        return table;
    }

}
