package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;

class PdfColor extends PdfArray {
  PdfColor(int red, int green, int blue) {
    super(new PdfNumber((red & 0xFF) / 255.0D));
    add(new PdfNumber((green & 0xFF) / 255.0D));
    add(new PdfNumber((blue & 0xFF) / 255.0D));
  }
  
  PdfColor(BaseColor color) {
    this(color.getRed(), color.getGreen(), color.getBlue());
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfColor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */