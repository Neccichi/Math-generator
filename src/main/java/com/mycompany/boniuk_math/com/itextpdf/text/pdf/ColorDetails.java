package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

class ColorDetails {
  PdfIndirectReference indirectReference;
  
  PdfName colorName;
  
  PdfSpotColor spotcolor;
  
  ColorDetails(PdfName colorName, PdfIndirectReference indirectReference, PdfSpotColor scolor) {
    this.colorName = colorName;
    this.indirectReference = indirectReference;
    this.spotcolor = scolor;
  }
  
  PdfIndirectReference getIndirectReference() {
    return this.indirectReference;
  }
  
  PdfName getColorName() {
    return this.colorName;
  }
  
  PdfObject getSpotColor(PdfWriter writer) {
    return this.spotcolor.getSpotObject(writer);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\ColorDetails.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */