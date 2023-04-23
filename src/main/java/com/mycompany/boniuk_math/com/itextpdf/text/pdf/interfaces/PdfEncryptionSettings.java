package com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import java.security.cert.Certificate;

public interface PdfEncryptionSettings {
  void setEncryption(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) throws DocumentException;
  
  void setEncryption(Certificate[] paramArrayOfCertificate, int[] paramArrayOfint, int paramInt) throws DocumentException;
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\interfaces\PdfEncryptionSettings.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */