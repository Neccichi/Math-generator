package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.security.cert.Certificate;

public class PdfPublicKeyRecipient {
  private Certificate certificate = null;
  
  private int permission = 0;
  
  protected byte[] cms = null;
  
  public PdfPublicKeyRecipient(Certificate certificate, int permission) {
    this.certificate = certificate;
    this.permission = permission;
  }
  
  public Certificate getCertificate() {
    return this.certificate;
  }
  
  public int getPermission() {
    return this.permission;
  }
  
  protected void setCms(byte[] cms) {
    this.cms = cms;
  }
  
  protected byte[] getCms() {
    return this.cms;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPublicKeyRecipient.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */