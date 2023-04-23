package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.ElementListener;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.LargeElement;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.api.Spaceable;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.events.PdfPTableEventForwarder;
import java.util.ArrayList;
import java.util.List;

public class PdfPTable implements LargeElement, Spaceable {
  public static final int BASECANVAS = 0;
  
  public static final int BACKGROUNDCANVAS = 1;
  
  public static final int LINECANVAS = 2;
  
  public static final int TEXTCANVAS = 3;
  
  protected ArrayList<PdfPRow> rows = new ArrayList<PdfPRow>();
  
  protected float totalHeight = 0.0F;
  
  protected PdfPCell[] currentRow;
  
  protected int currentColIdx = 0;
  
  protected PdfPCell defaultCell = new PdfPCell((Phrase)null);
  
  protected float totalWidth = 0.0F;
  
  protected float[] relativeWidths;
  
  protected float[] absoluteWidths;
  
  protected PdfPTableEvent tableEvent;
  
  protected int headerRows;
  
  protected float widthPercentage = 80.0F;
  
  private int horizontalAlignment = 1;
  
  private boolean skipFirstHeader = false;
  
  private boolean skipLastFooter = false;
  
  protected boolean isColspan = false;
  
  protected int runDirection = 0;
  
  private boolean lockedWidth = false;
  
  private boolean splitRows = true;
  
  protected float spacingBefore;
  
  protected float spacingAfter;
  
  private boolean[] extendLastRow = new boolean[] { false, false };
  
  private boolean headersInEvent;
  
  private boolean splitLate = true;
  
  private boolean keepTogether;
  
  protected boolean complete = true;
  
  private int footerRows;
  
  protected boolean rowCompleted = true;
  
  protected PdfPTable() {}
  
  public PdfPTable(float[] relativeWidths) {
    if (relativeWidths == null)
      throw new NullPointerException(MessageLocalization.getComposedMessage("the.widths.array.in.pdfptable.constructor.can.not.be.null", new Object[0])); 
    if (relativeWidths.length == 0)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.widths.array.in.pdfptable.constructor.can.not.have.zero.length", new Object[0])); 
    this.relativeWidths = new float[relativeWidths.length];
    System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
    this.absoluteWidths = new float[relativeWidths.length];
    calculateWidths();
    this.currentRow = new PdfPCell[this.absoluteWidths.length];
    this.keepTogether = false;
  }
  
  public PdfPTable(int numColumns) {
    if (numColumns <= 0)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.columns.in.pdfptable.constructor.must.be.greater.than.zero", new Object[0])); 
    this.relativeWidths = new float[numColumns];
    for (int k = 0; k < numColumns; k++)
      this.relativeWidths[k] = 1.0F; 
    this.absoluteWidths = new float[this.relativeWidths.length];
    calculateWidths();
    this.currentRow = new PdfPCell[this.absoluteWidths.length];
    this.keepTogether = false;
  }
  
  public PdfPTable(PdfPTable table) {
    copyFormat(table);
    int k;
    for (k = 0; k < this.currentRow.length && 
      table.currentRow[k] != null; k++)
      this.currentRow[k] = new PdfPCell(table.currentRow[k]); 
    for (k = 0; k < table.rows.size(); k++) {
      PdfPRow row = table.rows.get(k);
      if (row != null)
        row = new PdfPRow(row); 
      this.rows.add(row);
    } 
  }
  
  public static PdfPTable shallowCopy(PdfPTable table) {
    PdfPTable nt = new PdfPTable();
    nt.copyFormat(table);
    return nt;
  }
  
  protected void copyFormat(PdfPTable sourceTable) {
    this.relativeWidths = new float[sourceTable.getNumberOfColumns()];
    this.absoluteWidths = new float[sourceTable.getNumberOfColumns()];
    System.arraycopy(sourceTable.relativeWidths, 0, this.relativeWidths, 0, getNumberOfColumns());
    System.arraycopy(sourceTable.absoluteWidths, 0, this.absoluteWidths, 0, getNumberOfColumns());
    this.totalWidth = sourceTable.totalWidth;
    this.totalHeight = sourceTable.totalHeight;
    this.currentColIdx = 0;
    this.tableEvent = sourceTable.tableEvent;
    this.runDirection = sourceTable.runDirection;
    this.defaultCell = new PdfPCell(sourceTable.defaultCell);
    this.currentRow = new PdfPCell[sourceTable.currentRow.length];
    this.isColspan = sourceTable.isColspan;
    this.splitRows = sourceTable.splitRows;
    this.spacingAfter = sourceTable.spacingAfter;
    this.spacingBefore = sourceTable.spacingBefore;
    this.headerRows = sourceTable.headerRows;
    this.footerRows = sourceTable.footerRows;
    this.lockedWidth = sourceTable.lockedWidth;
    this.extendLastRow = sourceTable.extendLastRow;
    this.headersInEvent = sourceTable.headersInEvent;
    this.widthPercentage = sourceTable.widthPercentage;
    this.splitLate = sourceTable.splitLate;
    this.skipFirstHeader = sourceTable.skipFirstHeader;
    this.skipLastFooter = sourceTable.skipLastFooter;
    this.horizontalAlignment = sourceTable.horizontalAlignment;
    this.keepTogether = sourceTable.keepTogether;
    this.complete = sourceTable.complete;
  }
  
  public void setWidths(float[] relativeWidths) throws DocumentException {
    if (relativeWidths.length != getNumberOfColumns())
      throw new DocumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns", new Object[0])); 
    this.relativeWidths = new float[relativeWidths.length];
    System.arraycopy(relativeWidths, 0, this.relativeWidths, 0, relativeWidths.length);
    this.absoluteWidths = new float[relativeWidths.length];
    this.totalHeight = 0.0F;
    calculateWidths();
    calculateHeights();
  }
  
  public void setWidths(int[] relativeWidths) throws DocumentException {
    float[] tb = new float[relativeWidths.length];
    for (int k = 0; k < relativeWidths.length; k++)
      tb[k] = relativeWidths[k]; 
    setWidths(tb);
  }
  
  protected void calculateWidths() {
    if (this.totalWidth <= 0.0F)
      return; 
    float total = 0.0F;
    int numCols = getNumberOfColumns();
    int k;
    for (k = 0; k < numCols; k++)
      total += this.relativeWidths[k]; 
    for (k = 0; k < numCols; k++)
      this.absoluteWidths[k] = this.totalWidth * this.relativeWidths[k] / total; 
  }
  
  public void setTotalWidth(float totalWidth) {
    if (this.totalWidth == totalWidth)
      return; 
    this.totalWidth = totalWidth;
    this.totalHeight = 0.0F;
    calculateWidths();
    calculateHeights();
  }
  
  public void setTotalWidth(float[] columnWidth) throws DocumentException {
    if (columnWidth.length != getNumberOfColumns())
      throw new DocumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns", new Object[0])); 
    this.totalWidth = 0.0F;
    for (int k = 0; k < columnWidth.length; k++)
      this.totalWidth += columnWidth[k]; 
    setWidths(columnWidth);
  }
  
  public void setWidthPercentage(float[] columnWidth, Rectangle pageSize) throws DocumentException {
    if (columnWidth.length != getNumberOfColumns())
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("wrong.number.of.columns", new Object[0])); 
    float totalWidth = 0.0F;
    for (int k = 0; k < columnWidth.length; k++)
      totalWidth += columnWidth[k]; 
    this.widthPercentage = totalWidth / (pageSize.getRight() - pageSize.getLeft()) * 100.0F;
    setWidths(columnWidth);
  }
  
  public float getTotalWidth() {
    return this.totalWidth;
  }
  
  public float calculateHeights() {
    if (this.totalWidth <= 0.0F)
      return 0.0F; 
    this.totalHeight = 0.0F;
    for (int k = 0; k < this.rows.size(); k++)
      this.totalHeight += getRowHeight(k, true); 
    return this.totalHeight;
  }
  
  public void resetColumnCount(int newColCount) {
    if (newColCount <= 0)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.columns.in.pdfptable.constructor.must.be.greater.than.zero", new Object[0])); 
    this.relativeWidths = new float[newColCount];
    for (int k = 0; k < newColCount; k++)
      this.relativeWidths[k] = 1.0F; 
    this.absoluteWidths = new float[this.relativeWidths.length];
    calculateWidths();
    this.currentRow = new PdfPCell[this.absoluteWidths.length];
    this.totalHeight = 0.0F;
  }
  
  public PdfPCell getDefaultCell() {
    return this.defaultCell;
  }
  
  public void addCell(PdfPCell cell) {
    this.rowCompleted = false;
    PdfPCell ncell = new PdfPCell(cell);
    int colspan = ncell.getColspan();
    colspan = Math.max(colspan, 1);
    colspan = Math.min(colspan, this.currentRow.length - this.currentColIdx);
    ncell.setColspan(colspan);
    if (colspan != 1)
      this.isColspan = true; 
    int rdir = ncell.getRunDirection();
    if (rdir == 0)
      ncell.setRunDirection(this.runDirection); 
    skipColsWithRowspanAbove();
    boolean cellAdded = false;
    if (this.currentColIdx < this.currentRow.length) {
      this.currentRow[this.currentColIdx] = ncell;
      this.currentColIdx += colspan;
      cellAdded = true;
    } 
    skipColsWithRowspanAbove();
    while (this.currentColIdx >= this.currentRow.length) {
      int numCols = getNumberOfColumns();
      if (this.runDirection == 3) {
        PdfPCell[] rtlRow = new PdfPCell[numCols];
        int rev = this.currentRow.length;
        for (int k = 0; k < this.currentRow.length; k++) {
          PdfPCell rcell = this.currentRow[k];
          int cspan = rcell.getColspan();
          rev -= cspan;
          rtlRow[rev] = rcell;
          k += cspan - 1;
        } 
        this.currentRow = rtlRow;
      } 
      PdfPRow row = new PdfPRow(this.currentRow);
      if (this.totalWidth > 0.0F) {
        row.setWidths(this.absoluteWidths);
        this.totalHeight += row.getMaxHeights();
      } 
      this.rows.add(row);
      this.currentRow = new PdfPCell[numCols];
      this.currentColIdx = 0;
      skipColsWithRowspanAbove();
      this.rowCompleted = true;
    } 
    if (!cellAdded) {
      this.currentRow[this.currentColIdx] = ncell;
      this.currentColIdx += colspan;
    } 
  }
  
  private void skipColsWithRowspanAbove() {
    int direction = 1;
    if (this.runDirection == 3)
      direction = -1; 
    while (rowSpanAbove(this.rows.size(), this.currentColIdx))
      this.currentColIdx += direction; 
  }
  
  PdfPCell cellAt(int row, int col) {
    PdfPCell[] cells = ((PdfPRow)this.rows.get(row)).getCells();
    for (int i = 0; i < cells.length; i++) {
      if (cells[i] != null && 
        col >= i && col < i + cells[i].getColspan())
        return cells[i]; 
    } 
    return null;
  }
  
  boolean rowSpanAbove(int currRow, int currCol) {
    if (currCol >= getNumberOfColumns() || currCol < 0 || currRow < 1)
      return false; 
    int row = currRow - 1;
    PdfPRow aboveRow = this.rows.get(row);
    if (aboveRow == null)
      return false; 
    PdfPCell aboveCell = cellAt(row, currCol);
    while (aboveCell == null && row > 0) {
      aboveRow = this.rows.get(--row);
      if (aboveRow == null)
        return false; 
      aboveCell = cellAt(row, currCol);
    } 
    int distance = currRow - row;
    if (aboveCell.getRowspan() == 1 && distance > 1) {
      int col = currCol - 1;
      aboveRow = this.rows.get(row + 1);
      distance--;
      aboveCell = aboveRow.getCells()[col];
      while (aboveCell == null && col > 0)
        aboveCell = aboveRow.getCells()[--col]; 
    } 
    return (aboveCell != null && aboveCell.getRowspan() > distance);
  }
  
  public void addCell(String text) {
    addCell(new Phrase(text));
  }
  
  public void addCell(PdfPTable table) {
    this.defaultCell.setTable(table);
    addCell(this.defaultCell);
    this.defaultCell.setTable(null);
  }
  
  public void addCell(Image image) {
    this.defaultCell.setImage(image);
    addCell(this.defaultCell);
    this.defaultCell.setImage(null);
  }
  
  public void addCell(Phrase phrase) {
    this.defaultCell.setPhrase(phrase);
    addCell(this.defaultCell);
    this.defaultCell.setPhrase(null);
  }
  
  public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
    return writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvases);
  }
  
  public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases) {
    return writeSelectedRows(colStart, colEnd, rowStart, rowEnd, xPos, yPos, canvases, true);
  }
  
  public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte[] canvases, boolean reusable) {
    if (this.totalWidth <= 0.0F)
      throw new RuntimeException(MessageLocalization.getComposedMessage("the.table.width.must.be.greater.than.zero", new Object[0])); 
    int totalRows = this.rows.size();
    if (rowStart < 0)
      rowStart = 0; 
    if (rowEnd < 0) {
      rowEnd = totalRows;
    } else {
      rowEnd = Math.min(rowEnd, totalRows);
    } 
    if (rowStart >= rowEnd)
      return yPos; 
    int totalCols = getNumberOfColumns();
    if (colStart < 0) {
      colStart = 0;
    } else {
      colStart = Math.min(colStart, totalCols);
    } 
    if (colEnd < 0) {
      colEnd = totalCols;
    } else {
      colEnd = Math.min(colEnd, totalCols);
    } 
    float yPosStart = yPos;
    for (int k = rowStart; k < rowEnd; k++) {
      PdfPRow row = this.rows.get(k);
      if (row != null) {
        row.writeCells(colStart, colEnd, xPos, yPos, canvases, reusable);
        yPos -= row.getMaxHeights();
      } 
    } 
    if (this.tableEvent != null && colStart == 0 && colEnd == totalCols) {
      float[] heights = new float[rowEnd - rowStart + 1];
      heights[0] = yPosStart;
      for (int i = rowStart; i < rowEnd; i++) {
        PdfPRow row = this.rows.get(i);
        float hr = 0.0F;
        if (row != null)
          hr = row.getMaxHeights(); 
        heights[i - rowStart + 1] = heights[i - rowStart] - hr;
      } 
      this.tableEvent.tableLayout(this, getEventWidths(xPos, rowStart, rowEnd, this.headersInEvent), heights, this.headersInEvent ? this.headerRows : 0, rowStart, canvases);
    } 
    return yPos;
  }
  
  public float writeSelectedRows(int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
    return writeSelectedRows(0, -1, rowStart, rowEnd, xPos, yPos, canvas);
  }
  
  public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas) {
    return writeSelectedRows(colStart, colEnd, rowStart, rowEnd, xPos, yPos, canvas, true);
  }
  
  public float writeSelectedRows(int colStart, int colEnd, int rowStart, int rowEnd, float xPos, float yPos, PdfContentByte canvas, boolean reusable) {
    int totalCols = getNumberOfColumns();
    if (colStart < 0) {
      colStart = 0;
    } else {
      colStart = Math.min(colStart, totalCols);
    } 
    if (colEnd < 0) {
      colEnd = totalCols;
    } else {
      colEnd = Math.min(colEnd, totalCols);
    } 
    boolean clip = (colStart != 0 || colEnd != totalCols);
    if (clip) {
      float w = 0.0F;
      for (int k = colStart; k < colEnd; k++)
        w += this.absoluteWidths[k]; 
      canvas.saveState();
      float lx = (colStart == 0) ? 10000.0F : 0.0F;
      float rx = (colEnd == totalCols) ? 10000.0F : 0.0F;
      canvas.rectangle(xPos - lx, -10000.0F, w + lx + rx, 20000.0F);
      canvas.clip();
      canvas.newPath();
    } 
    PdfContentByte[] canvases = beginWritingRows(canvas);
    float y = writeSelectedRows(colStart, colEnd, rowStart, rowEnd, xPos, yPos, canvases, reusable);
    endWritingRows(canvases);
    if (clip)
      canvas.restoreState(); 
    return y;
  }
  
  public static PdfContentByte[] beginWritingRows(PdfContentByte canvas) {
    return new PdfContentByte[] { canvas, canvas.getDuplicate(), canvas.getDuplicate(), canvas.getDuplicate() };
  }
  
  public static void endWritingRows(PdfContentByte[] canvases) {
    PdfContentByte canvas = canvases[0];
    canvas.saveState();
    canvas.add(canvases[1]);
    canvas.restoreState();
    canvas.saveState();
    canvas.setLineCap(2);
    canvas.resetRGBColorStroke();
    canvas.add(canvases[2]);
    canvas.restoreState();
    canvas.add(canvases[3]);
  }
  
  public int size() {
    return this.rows.size();
  }
  
  public float getTotalHeight() {
    return this.totalHeight;
  }
  
  public float getRowHeight(int idx) {
    return getRowHeight(idx, false);
  }
  
  protected float getRowHeight(int idx, boolean firsttime) {
    if (this.totalWidth <= 0.0F || idx < 0 || idx >= this.rows.size())
      return 0.0F; 
    PdfPRow row = this.rows.get(idx);
    if (row == null)
      return 0.0F; 
    if (firsttime)
      row.setWidths(this.absoluteWidths); 
    float height = row.getMaxHeights();
    for (int i = 0; i < this.relativeWidths.length; i++) {
      if (rowSpanAbove(idx, i)) {
        int rs = 1;
        while (rowSpanAbove(idx - rs, i))
          rs++; 
        PdfPRow tmprow = this.rows.get(idx - rs);
        PdfPCell cell = tmprow.getCells()[i];
        float tmp = 0.0F;
        if (cell != null && cell.getRowspan() == rs + 1) {
          tmp = cell.getMaxHeight();
          while (rs > 0) {
            tmp -= getRowHeight(idx - rs);
            rs--;
          } 
        } 
        if (tmp > height)
          height = tmp; 
      } 
    } 
    row.setMaxHeights(height);
    return height;
  }
  
  public float getRowspanHeight(int rowIndex, int cellIndex) {
    if (this.totalWidth <= 0.0F || rowIndex < 0 || rowIndex >= this.rows.size())
      return 0.0F; 
    PdfPRow row = this.rows.get(rowIndex);
    if (row == null || cellIndex >= (row.getCells()).length)
      return 0.0F; 
    PdfPCell cell = row.getCells()[cellIndex];
    if (cell == null)
      return 0.0F; 
    float rowspanHeight = 0.0F;
    for (int j = 0; j < cell.getRowspan(); j++)
      rowspanHeight += getRowHeight(rowIndex + j); 
    return rowspanHeight;
  }
  
  public boolean hasRowspan(int rowIdx) {
    if (rowIdx < this.rows.size() && getRow(rowIdx).hasRowspan())
      return true; 
    for (int i = 0; i < getNumberOfColumns(); i++) {
      if (rowSpanAbove(rowIdx - 1, i))
        return true; 
    } 
    return false;
  }
  
  public void normalizeHeadersFooters() {
    if (this.footerRows > this.headerRows)
      this.footerRows = this.headerRows; 
  }
  
  public float getHeaderHeight() {
    float total = 0.0F;
    int size = Math.min(this.rows.size(), this.headerRows);
    for (int k = 0; k < size; k++) {
      PdfPRow row = this.rows.get(k);
      if (row != null)
        total += row.getMaxHeights(); 
    } 
    return total;
  }
  
  public float getFooterHeight() {
    float total = 0.0F;
    int start = Math.max(0, this.headerRows - this.footerRows);
    int size = Math.min(this.rows.size(), this.headerRows);
    for (int k = start; k < size; k++) {
      PdfPRow row = this.rows.get(k);
      if (row != null)
        total += row.getMaxHeights(); 
    } 
    return total;
  }
  
  public boolean deleteRow(int rowNumber) {
    if (rowNumber < 0 || rowNumber >= this.rows.size())
      return false; 
    if (this.totalWidth > 0.0F) {
      PdfPRow row = this.rows.get(rowNumber);
      if (row != null)
        this.totalHeight -= row.getMaxHeights(); 
    } 
    this.rows.remove(rowNumber);
    if (rowNumber < this.headerRows) {
      this.headerRows--;
      if (rowNumber >= this.headerRows - this.footerRows)
        this.footerRows--; 
    } 
    return true;
  }
  
  public boolean deleteLastRow() {
    return deleteRow(this.rows.size() - 1);
  }
  
  public void deleteBodyRows() {
    ArrayList<PdfPRow> rows2 = new ArrayList<PdfPRow>();
    for (int k = 0; k < this.headerRows; k++)
      rows2.add(this.rows.get(k)); 
    this.rows = rows2;
    this.totalHeight = 0.0F;
    if (this.totalWidth > 0.0F)
      this.totalHeight = getHeaderHeight(); 
  }
  
  public int getNumberOfColumns() {
    return this.relativeWidths.length;
  }
  
  public int getHeaderRows() {
    return this.headerRows;
  }
  
  public void setHeaderRows(int headerRows) {
    if (headerRows < 0)
      headerRows = 0; 
    this.headerRows = headerRows;
  }
  
  public List<Chunk> getChunks() {
    return new ArrayList<Chunk>();
  }
  
  public int type() {
    return 23;
  }
  
  public boolean isContent() {
    return true;
  }
  
  public boolean isNestable() {
    return true;
  }
  
  public boolean process(ElementListener listener) {
    try {
      return listener.add((Element)this);
    } catch (DocumentException de) {
      return false;
    } 
  }
  
  public float getWidthPercentage() {
    return this.widthPercentage;
  }
  
  public void setWidthPercentage(float widthPercentage) {
    this.widthPercentage = widthPercentage;
  }
  
  public int getHorizontalAlignment() {
    return this.horizontalAlignment;
  }
  
  public void setHorizontalAlignment(int horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }
  
  public PdfPRow getRow(int idx) {
    return this.rows.get(idx);
  }
  
  public ArrayList<PdfPRow> getRows() {
    return this.rows;
  }
  
  public ArrayList<PdfPRow> getRows(int start, int end) {
    ArrayList<PdfPRow> list = new ArrayList<PdfPRow>();
    if (start < 0 || end > size())
      return list; 
    for (int i = start; i < end; i++)
      list.add(adjustCellsInRow(i, end)); 
    return list;
  }
  
  protected PdfPRow adjustCellsInRow(int start, int end) {
    PdfPRow row = new PdfPRow(getRow(start));
    PdfPCell[] cells = row.getCells();
    for (int i = 0; i < cells.length; i++) {
      PdfPCell cell = cells[i];
      if (cell != null && cell.getRowspan() != 1) {
        int stop = Math.min(end, start + cell.getRowspan());
        float extra = 0.0F;
        for (int k = start + 1; k < stop; k++)
          extra += getRow(k).getMaxHeights(); 
        row.setExtraHeight(i, extra);
      } 
    } 
    return row;
  }
  
  public void setTableEvent(PdfPTableEvent event) {
    if (event == null) {
      this.tableEvent = null;
    } else if (this.tableEvent == null) {
      this.tableEvent = event;
    } else if (this.tableEvent instanceof PdfPTableEventForwarder) {
      ((PdfPTableEventForwarder)this.tableEvent).addTableEvent(event);
    } else {
      PdfPTableEventForwarder forward = new PdfPTableEventForwarder();
      forward.addTableEvent(this.tableEvent);
      forward.addTableEvent(event);
      this.tableEvent = (PdfPTableEvent)forward;
    } 
  }
  
  public PdfPTableEvent getTableEvent() {
    return this.tableEvent;
  }
  
  public float[] getAbsoluteWidths() {
    return this.absoluteWidths;
  }
  
  float[][] getEventWidths(float xPos, int firstRow, int lastRow, boolean includeHeaders) {
    if (includeHeaders) {
      firstRow = Math.max(firstRow, this.headerRows);
      lastRow = Math.max(lastRow, this.headerRows);
    } 
    float[][] widths = new float[(includeHeaders ? this.headerRows : 0) + lastRow - firstRow][];
    if (this.isColspan) {
      int n = 0;
      if (includeHeaders)
        for (int k = 0; k < this.headerRows; k++) {
          PdfPRow row = this.rows.get(k);
          if (row == null) {
            n++;
          } else {
            widths[n++] = row.getEventWidth(xPos, this.absoluteWidths);
          } 
        }  
      for (; firstRow < lastRow; firstRow++) {
        PdfPRow row = this.rows.get(firstRow);
        if (row == null) {
          n++;
        } else {
          widths[n++] = row.getEventWidth(xPos, this.absoluteWidths);
        } 
      } 
    } else {
      int numCols = getNumberOfColumns();
      float[] width = new float[numCols + 1];
      width[0] = xPos;
      int k;
      for (k = 0; k < numCols; k++)
        width[k + 1] = width[k] + this.absoluteWidths[k]; 
      for (k = 0; k < widths.length; k++)
        widths[k] = width; 
    } 
    return widths;
  }
  
  public boolean isSkipFirstHeader() {
    return this.skipFirstHeader;
  }
  
  public boolean isSkipLastFooter() {
    return this.skipLastFooter;
  }
  
  public void setSkipFirstHeader(boolean skipFirstHeader) {
    this.skipFirstHeader = skipFirstHeader;
  }
  
  public void setSkipLastFooter(boolean skipLastFooter) {
    this.skipLastFooter = skipLastFooter;
  }
  
  public void setRunDirection(int runDirection) {
    switch (runDirection) {
      case 0:
      case 1:
      case 2:
      case 3:
        this.runDirection = runDirection;
        return;
    } 
    throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection));
  }
  
  public int getRunDirection() {
    return this.runDirection;
  }
  
  public boolean isLockedWidth() {
    return this.lockedWidth;
  }
  
  public void setLockedWidth(boolean lockedWidth) {
    this.lockedWidth = lockedWidth;
  }
  
  public boolean isSplitRows() {
    return this.splitRows;
  }
  
  public void setSplitRows(boolean splitRows) {
    this.splitRows = splitRows;
  }
  
  public void setSpacingBefore(float spacing) {
    this.spacingBefore = spacing;
  }
  
  public void setSpacingAfter(float spacing) {
    this.spacingAfter = spacing;
  }
  
  public float spacingBefore() {
    return this.spacingBefore;
  }
  
  public float spacingAfter() {
    return this.spacingAfter;
  }
  
  public boolean isExtendLastRow() {
    return this.extendLastRow[0];
  }
  
  public void setExtendLastRow(boolean extendLastRows) {
    this.extendLastRow[0] = extendLastRows;
    this.extendLastRow[1] = extendLastRows;
  }
  
  public void setExtendLastRow(boolean extendLastRows, boolean extendFinalRow) {
    this.extendLastRow[0] = extendLastRows;
    this.extendLastRow[1] = extendFinalRow;
  }
  
  public boolean isExtendLastRow(boolean newPageFollows) {
    if (newPageFollows)
      return this.extendLastRow[0]; 
    return this.extendLastRow[1];
  }
  
  public boolean isHeadersInEvent() {
    return this.headersInEvent;
  }
  
  public void setHeadersInEvent(boolean headersInEvent) {
    this.headersInEvent = headersInEvent;
  }
  
  public boolean isSplitLate() {
    return this.splitLate;
  }
  
  public void setSplitLate(boolean splitLate) {
    this.splitLate = splitLate;
  }
  
  public void setKeepTogether(boolean keepTogether) {
    this.keepTogether = keepTogether;
  }
  
  public boolean getKeepTogether() {
    return this.keepTogether;
  }
  
  public int getFooterRows() {
    return this.footerRows;
  }
  
  public void setFooterRows(int footerRows) {
    if (footerRows < 0)
      footerRows = 0; 
    this.footerRows = footerRows;
  }
  
  public void completeRow() {
    while (!this.rowCompleted)
      addCell(this.defaultCell); 
  }
  
  public void flushContent() {
    deleteBodyRows();
    setSkipFirstHeader(true);
  }
  
  public boolean isComplete() {
    return this.complete;
  }
  
  public void setComplete(boolean complete) {
    this.complete = complete;
  }
  
  public float getSpacingBefore() {
    return this.spacingBefore;
  }
  
  public float getSpacingAfter() {
    return this.spacingAfter;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPTable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */