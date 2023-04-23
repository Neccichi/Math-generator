package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.itextpdf.text.pdf.events.PdfPCellEventForwarder;
import java.util.List;

public class PdfPCell extends Rectangle {
  private ColumnText column = new ColumnText(null);
  
  private int verticalAlignment = 4;
  
  private float paddingLeft = 2.0F;
  
  private float paddingRight = 2.0F;
  
  private float paddingTop = 2.0F;
  
  private float paddingBottom = 2.0F;
  
  private float fixedHeight = 0.0F;
  
  private float minimumHeight;
  
  private boolean noWrap = false;
  
  private PdfPTable table;
  
  private int colspan = 1;
  
  private int rowspan = 1;
  
  private Image image;
  
  private PdfPCellEvent cellEvent;
  
  private boolean useDescender = true;
  
  private boolean useBorderPadding = false;
  
  protected Phrase phrase;
  
  private int rotation;
  
  public PdfPCell() {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    this.column.setLeading(0.0F, 1.0F);
    this.column.setUseAscender(true);
  }
  
  public PdfPCell(Phrase phrase) {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    this.column.addText(this.phrase = phrase);
    this.column.setLeading(0.0F, 1.0F);
    this.column.setUseAscender(true);
  }
  
  public PdfPCell(Image image) {
    this(image, false);
  }
  
  public PdfPCell(Image image, boolean fit) {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    if (fit) {
      this.image = image;
      this.column.setLeading(0.0F, 1.0F);
      setPadding(this.borderWidth / 2.0F);
    } else {
      this.column.addText(this.phrase = new Phrase(new Chunk(image, 0.0F, 0.0F)));
      this.column.setLeading(0.0F, 1.0F);
      setPadding(0.0F);
    } 
  }
  
  public PdfPCell(PdfPTable table) {
    this(table, (PdfPCell)null);
  }
  
  public PdfPCell(PdfPTable table, PdfPCell style) {
    super(0.0F, 0.0F, 0.0F, 0.0F);
    this.borderWidth = 0.5F;
    this.border = 15;
    this.column.setLeading(0.0F, 1.0F);
    this.table = table;
    table.setWidthPercentage(100.0F);
    table.setExtendLastRow(true);
    this.column.addElement((Element)table);
    if (style != null) {
      cloneNonPositionParameters(style);
      this.verticalAlignment = style.verticalAlignment;
      this.paddingLeft = style.paddingLeft;
      this.paddingRight = style.paddingRight;
      this.paddingTop = style.paddingTop;
      this.paddingBottom = style.paddingBottom;
      this.colspan = style.colspan;
      this.rowspan = style.rowspan;
      this.cellEvent = style.cellEvent;
      this.useDescender = style.useDescender;
      this.useBorderPadding = style.useBorderPadding;
      this.rotation = style.rotation;
    } else {
      setPadding(0.0F);
    } 
  }
  
  public PdfPCell(PdfPCell cell) {
    super(cell.llx, cell.lly, cell.urx, cell.ury);
    cloneNonPositionParameters(cell);
    this.verticalAlignment = cell.verticalAlignment;
    this.paddingLeft = cell.paddingLeft;
    this.paddingRight = cell.paddingRight;
    this.paddingTop = cell.paddingTop;
    this.paddingBottom = cell.paddingBottom;
    this.phrase = cell.phrase;
    this.fixedHeight = cell.fixedHeight;
    this.minimumHeight = cell.minimumHeight;
    this.noWrap = cell.noWrap;
    this.colspan = cell.colspan;
    this.rowspan = cell.rowspan;
    if (cell.table != null)
      this.table = new PdfPTable(cell.table); 
    this.image = Image.getInstance(cell.image);
    this.cellEvent = cell.cellEvent;
    this.useDescender = cell.useDescender;
    this.column = ColumnText.duplicate(cell.column);
    this.useBorderPadding = cell.useBorderPadding;
    this.rotation = cell.rotation;
  }
  
  public void addElement(Element element) {
    if (this.table != null) {
      this.table = null;
      this.column.setText(null);
    } 
    this.column.addElement(element);
  }
  
  public Phrase getPhrase() {
    return this.phrase;
  }
  
  public void setPhrase(Phrase phrase) {
    this.table = null;
    this.image = null;
    this.column.setText(this.phrase = phrase);
  }
  
  public int getHorizontalAlignment() {
    return this.column.getAlignment();
  }
  
  public void setHorizontalAlignment(int horizontalAlignment) {
    this.column.setAlignment(horizontalAlignment);
  }
  
  public int getVerticalAlignment() {
    return this.verticalAlignment;
  }
  
  public void setVerticalAlignment(int verticalAlignment) {
    if (this.table != null)
      this.table.setExtendLastRow((verticalAlignment == 4)); 
    this.verticalAlignment = verticalAlignment;
  }
  
  public float getEffectivePaddingLeft() {
    if (isUseBorderPadding()) {
      float border = getBorderWidthLeft() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingLeft + border;
    } 
    return this.paddingLeft;
  }
  
  public float getPaddingLeft() {
    return this.paddingLeft;
  }
  
  public void setPaddingLeft(float paddingLeft) {
    this.paddingLeft = paddingLeft;
  }
  
  public float getEffectivePaddingRight() {
    if (isUseBorderPadding()) {
      float border = getBorderWidthRight() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingRight + border;
    } 
    return this.paddingRight;
  }
  
  public float getPaddingRight() {
    return this.paddingRight;
  }
  
  public void setPaddingRight(float paddingRight) {
    this.paddingRight = paddingRight;
  }
  
  public float getEffectivePaddingTop() {
    if (isUseBorderPadding()) {
      float border = getBorderWidthTop() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingTop + border;
    } 
    return this.paddingTop;
  }
  
  public float getPaddingTop() {
    return this.paddingTop;
  }
  
  public void setPaddingTop(float paddingTop) {
    this.paddingTop = paddingTop;
  }
  
  public float getEffectivePaddingBottom() {
    if (isUseBorderPadding()) {
      float border = getBorderWidthBottom() / (isUseVariableBorders() ? 1.0F : 2.0F);
      return this.paddingBottom + border;
    } 
    return this.paddingBottom;
  }
  
  public float getPaddingBottom() {
    return this.paddingBottom;
  }
  
  public void setPaddingBottom(float paddingBottom) {
    this.paddingBottom = paddingBottom;
  }
  
  public void setPadding(float padding) {
    this.paddingBottom = padding;
    this.paddingTop = padding;
    this.paddingLeft = padding;
    this.paddingRight = padding;
  }
  
  public boolean isUseBorderPadding() {
    return this.useBorderPadding;
  }
  
  public void setUseBorderPadding(boolean use) {
    this.useBorderPadding = use;
  }
  
  public void setLeading(float fixedLeading, float multipliedLeading) {
    this.column.setLeading(fixedLeading, multipliedLeading);
  }
  
  public float getLeading() {
    return this.column.getLeading();
  }
  
  public float getMultipliedLeading() {
    return this.column.getMultipliedLeading();
  }
  
  public void setIndent(float indent) {
    this.column.setIndent(indent);
  }
  
  public float getIndent() {
    return this.column.getIndent();
  }
  
  public float getExtraParagraphSpace() {
    return this.column.getExtraParagraphSpace();
  }
  
  public void setExtraParagraphSpace(float extraParagraphSpace) {
    this.column.setExtraParagraphSpace(extraParagraphSpace);
  }
  
  public void setFixedHeight(float fixedHeight) {
    this.fixedHeight = fixedHeight;
    this.minimumHeight = 0.0F;
  }
  
  public float getFixedHeight() {
    return this.fixedHeight;
  }
  
  public boolean hasFixedHeight() {
    return (getFixedHeight() > 0.0F);
  }
  
  public void setMinimumHeight(float minimumHeight) {
    this.minimumHeight = minimumHeight;
    this.fixedHeight = 0.0F;
  }
  
  public float getMinimumHeight() {
    return this.minimumHeight;
  }
  
  public boolean hasMinimumHeight() {
    return (getMinimumHeight() > 0.0F);
  }
  
  public boolean isNoWrap() {
    return this.noWrap;
  }
  
  public void setNoWrap(boolean noWrap) {
    this.noWrap = noWrap;
  }
  
  public PdfPTable getTable() {
    return this.table;
  }
  
  void setTable(PdfPTable table) {
    this.table = table;
    this.column.setText(null);
    this.image = null;
    if (table != null) {
      table.setExtendLastRow((this.verticalAlignment == 4));
      this.column.addElement((Element)table);
      table.setWidthPercentage(100.0F);
    } 
  }
  
  public int getColspan() {
    return this.colspan;
  }
  
  public void setColspan(int colspan) {
    this.colspan = colspan;
  }
  
  public int getRowspan() {
    return this.rowspan;
  }
  
  public void setRowspan(int rowspan) {
    this.rowspan = rowspan;
  }
  
  public void setFollowingIndent(float indent) {
    this.column.setFollowingIndent(indent);
  }
  
  public float getFollowingIndent() {
    return this.column.getFollowingIndent();
  }
  
  public void setRightIndent(float indent) {
    this.column.setRightIndent(indent);
  }
  
  public float getRightIndent() {
    return this.column.getRightIndent();
  }
  
  public float getSpaceCharRatio() {
    return this.column.getSpaceCharRatio();
  }
  
  public void setSpaceCharRatio(float spaceCharRatio) {
    this.column.setSpaceCharRatio(spaceCharRatio);
  }
  
  public void setRunDirection(int runDirection) {
    this.column.setRunDirection(runDirection);
  }
  
  public int getRunDirection() {
    return this.column.getRunDirection();
  }
  
  public Image getImage() {
    return this.image;
  }
  
  public void setImage(Image image) {
    this.column.setText(null);
    this.table = null;
    this.image = image;
  }
  
  public PdfPCellEvent getCellEvent() {
    return this.cellEvent;
  }
  
  public void setCellEvent(PdfPCellEvent cellEvent) {
    if (cellEvent == null) {
      this.cellEvent = null;
    } else if (this.cellEvent == null) {
      this.cellEvent = cellEvent;
    } else if (this.cellEvent instanceof PdfPCellEventForwarder) {
      ((PdfPCellEventForwarder)this.cellEvent).addCellEvent(cellEvent);
    } else {
      PdfPCellEventForwarder forward = new PdfPCellEventForwarder();
      forward.addCellEvent(this.cellEvent);
      forward.addCellEvent(cellEvent);
      this.cellEvent = (PdfPCellEvent)forward;
    } 
  }
  
  public int getArabicOptions() {
    return this.column.getArabicOptions();
  }
  
  public void setArabicOptions(int arabicOptions) {
    this.column.setArabicOptions(arabicOptions);
  }
  
  public boolean isUseAscender() {
    return this.column.isUseAscender();
  }
  
  public void setUseAscender(boolean useAscender) {
    this.column.setUseAscender(useAscender);
  }
  
  public boolean isUseDescender() {
    return this.useDescender;
  }
  
  public void setUseDescender(boolean useDescender) {
    this.useDescender = useDescender;
  }
  
  public ColumnText getColumn() {
    return this.column;
  }
  
  public List<Element> getCompositeElements() {
    return (getColumn()).compositeElements;
  }
  
  public void setColumn(ColumnText column) {
    this.column = column;
  }
  
  public int getRotation() {
    return this.rotation;
  }
  
  public void setRotation(int rotation) {
    rotation %= 360;
    if (rotation < 0)
      rotation += 360; 
    if (rotation % 90 != 0)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("rotation.must.be.a.multiple.of.90", new Object[0])); 
    this.rotation = rotation;
  }
  
  public float getMaxHeight() {
    boolean pivoted = (getRotation() == 90 || getRotation() == 270);
    Image img = getImage();
    if (img != null) {
      img.scalePercent(100.0F);
      float refWidth = pivoted ? img.getScaledHeight() : img.getScaledWidth();
      float scale = (getRight() - getEffectivePaddingRight() - getEffectivePaddingLeft() - getLeft()) / refWidth;
      img.scalePercent(scale * 100.0F);
      float refHeight = pivoted ? img.getScaledWidth() : img.getScaledHeight();
      setBottom(getTop() - getEffectivePaddingTop() - getEffectivePaddingBottom() - refHeight);
    } else if (pivoted && hasFixedHeight()) {
      setBottom(getTop() - getFixedHeight());
    } else {
      float right, top, left, bottom;
      ColumnText ct = ColumnText.duplicate(getColumn());
      if (pivoted) {
        right = 20000.0F;
        top = getRight() - getEffectivePaddingRight();
        left = 0.0F;
        bottom = getLeft() + getEffectivePaddingLeft();
      } else {
        right = isNoWrap() ? 20000.0F : (getRight() - getEffectivePaddingRight());
        top = getTop() - getEffectivePaddingTop();
        left = getLeft() + getEffectivePaddingLeft();
        bottom = hasFixedHeight() ? (getTop() + getEffectivePaddingBottom() - getFixedHeight()) : -1.0737418E9F;
      } 
      PdfPRow.setColumn(ct, left, bottom, right, top);
      try {
        ct.go(true);
      } catch (DocumentException e) {
        throw new ExceptionConverter(e);
      } 
      if (pivoted) {
        setBottom(getTop() - getEffectivePaddingTop() - getEffectivePaddingBottom() - ct.getFilledWidth());
      } else {
        float yLine = ct.getYLine();
        if (isUseDescender())
          yLine += ct.getDescender(); 
        setBottom(yLine - getEffectivePaddingBottom());
      } 
    } 
    float height = getHeight();
    if (hasFixedHeight()) {
      height = getFixedHeight();
    } else if (hasMinimumHeight() && height < getMinimumHeight()) {
      height = getMinimumHeight();
    } 
    return height;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPCell.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */