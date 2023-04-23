package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public interface TSAClient {
  int getTokenSizeEstimate();
  
  byte[] getTimeStampToken(PdfPKCS7 paramPdfPKCS7, byte[] paramArrayOfbyte) throws Exception;
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\TSAClient.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */