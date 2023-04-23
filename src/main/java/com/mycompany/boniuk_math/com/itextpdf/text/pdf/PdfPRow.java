package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;

public class PdfPRow {
  public static final float BOTTOM_LIMIT = -1.0737418E9F;
  
  public static final float RIGHT_LIMIT = 20000.0F;
  
  protected PdfPCell[] cells;
  
  protected float[] widths;
  
  protected float[] extraHeights;
  
  protected float maxHeight = 0.0F;
  
  protected boolean calculated = false;
  
  private int[] canvasesPos;
  
  public PdfPRow(PdfPCell[] cells) {
    this.cells = cells;
    this.widths = new float[cells.length];
    initExtraHeights();
  }
  
  public PdfPRow(PdfPRow row) {
    this.maxHeight = row.maxHeight;
    this.calculated = row.calculated;
    this.cells = new PdfPCell[row.cells.length];
    for (int k = 0; k < this.cells.length; k++) {
      if (row.cells[k] != null)
        this.cells[k] = new PdfPCell(row.cells[k]); 
    } 
    this.widths = new float[this.cells.length];
    System.arraycopy(row.widths, 0, this.widths, 0, this.cells.length);
    initExtraHeights();
  }
  
  public boolean setWidths(float[] widths) {
    if (widths.length != this.cells.length)
      return false; 
    System.arraycopy(widths, 0, this.widths, 0, this.cells.length);
    float total = 0.0F;
    this.calculated = false;
    for (int k = 0; k < widths.length; k++) {
      PdfPCell cell = this.cells[k];
      if (cell == null) {
        total += widths[k];
      } else {
        cell.setLeft(total);
        int last = k + cell.getColspan();
        for (; k < last; k++)
          total += widths[k]; 
        k--;
        cell.setRight(total);
        cell.setTop(0.0F);
      } 
    } 
    return true;
  }
  
  protected void initExtraHeights() {
    this.extraHeights = new float[this.cells.length];
    for (int i = 0; i < this.extraHeights.length; i++)
      this.extraHeights[i] = 0.0F; 
  }
  
  public void setExtraHeight(int cell, float height) {
    if (cell < 0 || cell >= this.cells.length)
      return; 
    this.extraHeights[cell] = height;
  }
  
  protected void calculateHeights() {
    this.maxHeight = 0.0F;
    for (int k = 0; k < this.cells.length; k++) {
      PdfPCell cell = this.cells[k];
      float height = 0.0F;
      if (cell != null) {
        height = cell.getMaxHeight();
        if (height > this.maxHeight && cell.getRowspan() == 1)
          this.maxHeight = height; 
      } 
    } 
    this.calculated = true;
  }
  
  public void writeBorderAndBackground(float xPos, float yPos, float currentMaxHeight, PdfPCell cell, PdfContentByte[] canvases) {
    BaseColor background = cell.getBackgroundColor();
    if (background != null || cell.hasBorders()) {
      float right = cell.getRight() + xPos;
      float top = cell.getTop() + yPos;
      float left = cell.getLeft() + xPos;
      float bottom = top - currentMaxHeight;
      if (background != null) {
        PdfContentByte backgr = canvases[1];
        backgr.setColorFill(background);
        backgr.rectangle(left, bottom, right - left, top - bottom);
        backgr.fill();
      } 
      if (cell.hasBorders()) {
        Rectangle newRect = new Rectangle(left, bottom, right, top);
        newRect.cloneNonPositionParameters(cell);
        newRect.setBackgroundColor(null);
        PdfContentByte lineCanvas = canvases[2];
        lineCanvas.rectangle(newRect);
      } 
    } 
  }
  
  protected void saveAndRotateCanvases(PdfContentByte[] canvases, float a, float b, float c, float d, float e, float f) {
    int last = 4;
    if (this.canvasesPos == null)
      this.canvasesPos = new int[last * 2]; 
    for (int k = 0; k < last; k++) {
      ByteBuffer bb = canvases[k].getInternalBuffer();
      this.canvasesPos[k * 2] = bb.size();
      canvases[k].saveState();
      canvases[k].concatCTM(a, b, c, d, e, f);
      this.canvasesPos[k * 2 + 1] = bb.size();
    } 
  }
  
  protected void restoreCanvases(PdfContentByte[] canvases) {
    int last = 4;
    for (int k = 0; k < last; k++) {
      ByteBuffer bb = canvases[k].getInternalBuffer();
      int p1 = bb.size();
      canvases[k].restoreState();
      if (p1 == this.canvasesPos[k * 2 + 1])
        bb.setSize(this.canvasesPos[k * 2]); 
    } 
  }
  
  public static float setColumn(ColumnText ct, float left, float bottom, float right, float top) {
    if (left > right)
      right = left; 
    if (bottom > top)
      top = bottom; 
    ct.setSimpleColumn(left, bottom, right, top);
    return top;
  }
  
  public void writeCells(int colStart, int colEnd, float xPos, float yPos, PdfContentByte[] canvases, boolean reusable) {
    if (!this.calculated)
      calculateHeights(); 
    if (colEnd < 0) {
      colEnd = this.cells.length;
    } else {
      colEnd = Math.min(colEnd, this.cells.length);
    } 
    if (colStart < 0)
      colStart = 0; 
    if (colStart >= colEnd)
      return; 
    int newStart;
    for (newStart = colStart; newStart >= 0 && 
      this.cells[newStart] == null; newStart--) {
      if (newStart > 0)
        xPos -= this.widths[newStart - 1]; 
    } 
    if (newStart < 0)
      newStart = 0; 
    if (this.cells[newStart] != null)
      xPos -= this.cells[newStart].getLeft(); 
    for (int k = newStart; k < colEnd; k++) {
      PdfPCell cell = this.cells[k];
      if (cell != null) {
        float currentMaxHeight = this.maxHeight + this.extraHeights[k];
        writeBorderAndBackground(xPos, yPos, currentMaxHeight, cell, canvases);
        Image img = cell.getImage();
        float tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
        if (cell.getHeight() <= currentMaxHeight)
          switch (cell.getVerticalAlignment()) {
            case 6:
              tly = cell.getTop() + yPos - currentMaxHeight + cell.getHeight() - cell.getEffectivePaddingTop();
              break;
            case 5:
              tly = cell.getTop() + yPos + (cell.getHeight() - currentMaxHeight) / 2.0F - cell.getEffectivePaddingTop();
              break;
          }  
        if (img != null) {
          if (cell.getRotation() != 0) {
            img = Image.getInstance(img);
            img.setRotation(img.getImageRotation() + (float)(cell.getRotation() * Math.PI / 180.0D));
          } 
          boolean vf = false;
          if (cell.getHeight() > currentMaxHeight) {
            img.scalePercent(100.0F);
            float scale = (currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom()) / img.getScaledHeight();
            img.scalePercent(scale * 100.0F);
            vf = true;
          } 
          float left = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
          if (vf) {
            switch (cell.getHorizontalAlignment()) {
              case 1:
                left = xPos + (cell.getLeft() + cell.getEffectivePaddingLeft() + cell.getRight() - cell.getEffectivePaddingRight() - img.getScaledWidth()) / 2.0F;
                break;
              case 2:
                left = xPos + cell.getRight() - cell.getEffectivePaddingRight() - img.getScaledWidth();
                break;
            } 
            tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
          } 
          img.setAbsolutePosition(left, tly - img.getScaledHeight());
          try {
            canvases[3].addImage(img);
          } catch (DocumentException e) {
            throw new ExceptionConverter(e);
          } 
        } else if (cell.getRotation() == 90 || cell.getRotation() == 270) {
          float netWidth = currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom();
          float netHeight = cell.getWidth() - cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight();
          ColumnText ct = ColumnText.duplicate(cell.getColumn());
          ct.setCanvases(canvases);
          ct.setSimpleColumn(0.0F, 0.0F, netWidth + 0.001F, -netHeight);
          try {
            ct.go(true);
          } catch (DocumentException e) {
            throw new ExceptionConverter(e);
          } 
          float calcHeight = -ct.getYLine();
          if (netWidth <= 0.0F || netHeight <= 0.0F)
            calcHeight = 0.0F; 
          if (calcHeight > 0.0F) {
            if (cell.isUseDescender())
              calcHeight -= ct.getDescender(); 
            ct = ColumnText.duplicate(cell.getColumn());
            ct.setCanvases(canvases);
            ct.setSimpleColumn(-0.003F, -0.001F, netWidth + 0.003F, calcHeight);
            if (cell.getRotation() == 90) {
              float pivotX, pivotY = cell.getTop() + yPos - currentMaxHeight + cell.getEffectivePaddingBottom();
              switch (cell.getVerticalAlignment()) {
                case 6:
                  pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight();
                  break;
                case 5:
                  pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() + calcHeight) / 2.0F;
                  break;
                default:
                  pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft() + calcHeight;
                  break;
              } 
              saveAndRotateCanvases(canvases, 0.0F, 1.0F, -1.0F, 0.0F, pivotX, pivotY);
            } else {
              float pivotX, pivotY = cell.getTop() + yPos - cell.getEffectivePaddingTop();
              switch (cell.getVerticalAlignment()) {
                case 6:
                  pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                  break;
                case 5:
                  pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() - calcHeight) / 2.0F;
                  break;
                default:
                  pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight() - calcHeight;
                  break;
              } 
              saveAndRotateCanvases(canvases, 0.0F, -1.0F, 1.0F, 0.0F, pivotX, pivotY);
            } 
            try {
              ct.go();
            } catch (DocumentException e) {
              throw new ExceptionConverter(e);
            } finally {
              restoreCanvases(canvases);
            } 
          } 
        } else {
          ColumnText ct;
          float fixedHeight = cell.getFixedHeight();
          float rightLimit = cell.getRight() + xPos - cell.getEffectivePaddingRight();
          float leftLimit = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
          if (cell.isNoWrap())
            switch (cell.getHorizontalAlignment()) {
              case 1:
                rightLimit += 10000.0F;
                leftLimit -= 10000.0F;
                break;
              case 2:
                if (cell.getRotation() == 180) {
                  rightLimit += 20000.0F;
                  break;
                } 
                leftLimit -= 20000.0F;
                break;
              default:
                if (cell.getRotation() == 180) {
                  leftLimit -= 20000.0F;
                  break;
                } 
                rightLimit += 20000.0F;
                break;
            }  
          if (reusable) {
            ct = ColumnText.duplicate(cell.getColumn());
          } else {
            ct = cell.getColumn();
          } 
          ct.setCanvases(canvases);
          float bry = tly - currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom();
          if (fixedHeight > 0.0F && 
            cell.getHeight() > currentMaxHeight) {
            tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
            bry = cell.getTop() + yPos - currentMaxHeight + cell.getEffectivePaddingBottom();
          } 
          if ((tly > bry || ct.zeroHeightElement()) && leftLimit < rightLimit) {
            ct.setSimpleColumn(leftLimit, bry - 0.001F, rightLimit, tly);
            if (cell.getRotation() == 180) {
              float shx = leftLimit + rightLimit;
              float shy = yPos + yPos - currentMaxHeight + cell.getEffectivePaddingBottom() - cell.getEffectivePaddingTop();
              saveAndRotateCanvases(canvases, -1.0F, 0.0F, 0.0F, -1.0F, shx, shy);
            } 
            try {
              ct.go();
            } catch (DocumentException e) {
              throw new ExceptionConverter(e);
            } finally {
              if (cell.getRotation() == 180)
                restoreCanvases(canvases); 
            } 
          } 
        } 
        PdfPCellEvent evt = cell.getCellEvent();
        if (evt != null) {
          Rectangle rect = new Rectangle(cell.getLeft() + xPos, cell.getTop() + yPos - currentMaxHeight, cell.getRight() + xPos, cell.getTop() + yPos);
          evt.cellLayout(cell, rect, canvases);
        } 
      } 
    } 
  }
  
  public boolean isCalculated() {
    return this.calculated;
  }
  
  public float getMaxHeights() {
    if (!this.calculated)
      calculateHeights(); 
    return this.maxHeight;
  }
  
  public void setMaxHeights(float maxHeight) {
    this.maxHeight = maxHeight;
  }
  
  float[] getEventWidth(float xPos, float[] absoluteWidths) {
    int n = 1;
    for (int k = 0; k < this.cells.length; ) {
      if (this.cells[k] != null) {
        n++;
        k += this.cells[k].getColspan();
        continue;
      } 
      while (k < this.cells.length && this.cells[k] == null) {
        n++;
        k++;
      } 
    } 
    float[] width = new float[n];
    width[0] = xPos;
    n = 1;
    for (int i = 0; i < this.cells.length && n < width.length; ) {
      if (this.cells[i] != null) {
        int colspan = this.cells[i].getColspan();
        width[n] = width[n - 1];
        for (int j = 0; j < colspan && i < absoluteWidths.length; j++)
          width[n] = width[n] + absoluteWidths[i++]; 
        n++;
        continue;
      } 
      width[n] = width[n - 1];
      while (i < this.cells.length && this.cells[i] == null)
        width[n] = width[n] + absoluteWidths[i++]; 
      n++;
    } 
    return width;
  }
  
  public void copyRowContent(PdfPTable table, int idx) {
    if (table == null)
      return; 
    for (int i = 0; i < this.cells.length; i++) {
      int lastRow = idx;
      PdfPCell copy = table.getRow(lastRow).getCells()[i];
      while (copy == null && lastRow > 0)
        copy = table.getRow(--lastRow).getCells()[i]; 
      if (this.cells[i] != null && copy != null) {
        this.cells[i].setColumn(copy.getColumn());
        this.calculated = false;
      } 
    } 
  }
  
  public PdfPRow splitRow(PdfPTable table, int rowIndex, float new_height) {
    PdfPCell[] newCells = new PdfPCell[this.cells.length];
    float[] fixHs = new float[this.cells.length];
    float[] minHs = new float[this.cells.length];
    boolean allEmpty = true;
    int k;
    for (k = 0; k < this.cells.length; k++) {
      float newHeight = new_height;
      PdfPCell cell = this.cells[k];
      if (cell == null) {
        int index = rowIndex;
        if (table.rowSpanAbove(index, k)) {
          while (table.rowSpanAbove(--index, k))
            newHeight += table.getRow(index).getMaxHeights(); 
          PdfPRow row = table.getRow(index);
          if (row != null && row.getCells()[k] != null) {
            newCells[k] = new PdfPCell(row.getCells()[k]);
            newCells[k].setColumn((ColumnText)null);
            newCells[k].setRowspan(row.getCells()[k].getRowspan() - rowIndex + index);
            allEmpty = false;
          } 
        } 
      } else {
        fixHs[k] = cell.getFixedHeight();
        minHs[k] = cell.getMinimumHeight();
        Image img = cell.getImage();
        PdfPCell newCell = new PdfPCell(cell);
        if (img != null) {
          if (newHeight > cell.getEffectivePaddingBottom() + cell.getEffectivePaddingTop() + 2.0F) {
            newCell.setPhrase((Phrase)null);
            allEmpty = false;
          } 
        } else {
          float y;
          int status;
          ColumnText ct = ColumnText.duplicate(cell.getColumn());
          float left = cell.getLeft() + cell.getEffectivePaddingLeft();
          float bottom = cell.getTop() + cell.getEffectivePaddingBottom() - newHeight;
          float right = cell.getRight() - cell.getEffectivePaddingRight();
          float top = cell.getTop() - cell.getEffectivePaddingTop();
          switch (cell.getRotation()) {
            case 90:
            case 270:
              y = setColumn(ct, bottom, left, top, right);
              break;
            default:
              y = setColumn(ct, left, bottom + 1.0E-5F, cell.isNoWrap() ? 20000.0F : right, top);
              break;
          } 
          try {
            status = ct.go(true);
          } catch (DocumentException e) {
            throw new ExceptionConverter(e);
          } 
          boolean thisEmpty = (ct.getYLine() == y);
          if (thisEmpty) {
            newCell.setColumn(ColumnText.duplicate(cell.getColumn()));
            ct.setFilledWidth(0.0F);
          } else if ((status & 0x1) == 0) {
            newCell.setColumn(ct);
            ct.setFilledWidth(0.0F);
          } else {
            newCell.setPhrase((Phrase)null);
          } 
          allEmpty = (allEmpty && thisEmpty);
        } 
        newCells[k] = newCell;
        cell.setFixedHeight(newHeight);
      } 
    } 
    if (allEmpty) {
      for (k = 0; k < this.cells.length; k++) {
        PdfPCell cell = this.cells[k];
        if (cell != null)
          if (fixHs[k] > 0.0F) {
            cell.setFixedHeight(fixHs[k]);
          } else {
            cell.setMinimumHeight(minHs[k]);
          }  
      } 
      return null;
    } 
    calculateHeights();
    PdfPRow split = new PdfPRow(newCells);
    split.widths = (float[])this.widths.clone();
    return split;
  }
  
  public PdfPCell[] getCells() {
    return this.cells;
  }
  
  public boolean hasRowspan() {
    for (int i = 0; i < this.cells.length; i++) {
      if (this.cells[i] != null && this.cells[i].getRowspan() > 1)
        return true; 
    } 
    return false;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPRow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */