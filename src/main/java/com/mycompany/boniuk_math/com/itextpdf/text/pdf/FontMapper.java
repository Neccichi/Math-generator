package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.awt.Font;

public interface FontMapper {
  BaseFont awtToPdf(Font paramFont);
  
  Font pdfToAwt(BaseFont paramBaseFont, int paramInt);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\FontMapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */