package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.awt.Color;
import java.awt.Image;

public class BarcodeEANSUPP extends Barcode {
  protected Barcode ean;
  
  protected Barcode supp;
  
  public BarcodeEANSUPP(Barcode ean, Barcode supp) {
    this.n = 8.0F;
    this.ean = ean;
    this.supp = supp;
  }
  
  public Rectangle getBarcodeSize() {
    Rectangle rect = this.ean.getBarcodeSize();
    rect.setRight(rect.getWidth() + this.supp.getBarcodeSize().getWidth() + this.n);
    return rect;
  }
  
  public Rectangle placeBarcode(PdfContentByte cb, BaseColor barColor, BaseColor textColor) {
    if (this.supp.getFont() != null) {
      this.supp.setBarHeight(this.ean.getBarHeight() + this.supp.getBaseline() - this.supp.getFont().getFontDescriptor(2, this.supp.getSize()));
    } else {
      this.supp.setBarHeight(this.ean.getBarHeight());
    } 
    Rectangle eanR = this.ean.getBarcodeSize();
    cb.saveState();
    this.ean.placeBarcode(cb, barColor, textColor);
    cb.restoreState();
    cb.saveState();
    cb.concatCTM(1.0F, 0.0F, 0.0F, 1.0F, eanR.getWidth() + this.n, eanR.getHeight() - this.ean.getBarHeight());
    this.supp.placeBarcode(cb, barColor, textColor);
    cb.restoreState();
    return getBarcodeSize();
  }
  
  public Image createAwtImage(Color foreground, Color background) {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("the.two.barcodes.must.be.composed.externally", new Object[0]));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\BarcodeEANSUPP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */