package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;

public class PdfSpotColor {
  public PdfName name;
  
  public BaseColor altcs;
  
  public PdfSpotColor(String name, BaseColor altcs) {
    this.name = new PdfName(name);
    this.altcs = altcs;
  }
  
  public BaseColor getAlternativeCS() {
    return this.altcs;
  }
  
  protected PdfObject getSpotObject(PdfWriter writer) {
    PdfArray array = new PdfArray(PdfName.SEPARATION);
    array.add(this.name);
    PdfFunction func = null;
    if (this.altcs instanceof ExtendedColor) {
      CMYKColor cmyk;
      int type = ((ExtendedColor)this.altcs).type;
      switch (type) {
        case 1:
          array.add(PdfName.DEVICEGRAY);
          func = PdfFunction.type2(writer, new float[] { 0.0F, 1.0F }, null, new float[] { 0.0F }, new float[] { ((GrayColor)this.altcs).getGray() }, 1.0F);
          array.add(func.getReference());
          return array;
        case 2:
          array.add(PdfName.DEVICECMYK);
          cmyk = (CMYKColor)this.altcs;
          func = PdfFunction.type2(writer, new float[] { 0.0F, 1.0F }, null, new float[] { 0.0F, 0.0F, 0.0F, 0.0F }, new float[] { cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack() }, 1.0F);
          array.add(func.getReference());
          return array;
      } 
      throw new RuntimeException(MessageLocalization.getComposedMessage("only.rgb.gray.and.cmyk.are.supported.as.alternative.color.spaces", new Object[0]));
    } 
    array.add(PdfName.DEVICERGB);
    func = PdfFunction.type2(writer, new float[] { 0.0F, 1.0F }, null, new float[] { 1.0F, 1.0F, 1.0F }, new float[] { this.altcs.getRed() / 255.0F, this.altcs.getGreen() / 255.0F, this.altcs.getBlue() / 255.0F }, 1.0F);
    array.add(func.getReference());
    return array;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfSpotColor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */