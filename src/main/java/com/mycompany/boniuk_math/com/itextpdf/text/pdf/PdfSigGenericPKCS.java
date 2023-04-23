package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;

public abstract class PdfSigGenericPKCS extends PdfSignature {
  protected String hashAlgorithm;
  
  protected String provider = null;
  
  protected PdfPKCS7 pkcs;
  
  protected String name;
  
  private byte[] externalDigest;
  
  private byte[] externalRSAdata;
  
  private String digestEncryptionAlgorithm;
  
  public PdfSigGenericPKCS(PdfName filter, PdfName subFilter) {
    super(filter, subFilter);
  }
  
  public void setSignInfo(PrivateKey privKey, Certificate[] certChain, CRL[] crlList) {
    try {
      this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(get(PdfName.SUBFILTER)));
      this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
      if (PdfName.ADBE_X509_RSA_SHA1.equals(get(PdfName.SUBFILTER))) {
        if (certChain.length > 1) {
          PdfArray arr = new PdfArray();
          for (int ii = 0; ii < certChain.length; ii++)
            arr.add(new PdfString(certChain[ii].getEncoded())); 
          put(PdfName.CERT, arr);
        } else {
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          for (int k = 0; k < certChain.length; k++)
            bout.write(certChain[k].getEncoded()); 
          bout.close();
          setCert(bout.toByteArray());
        } 
        setContents(this.pkcs.getEncodedPKCS1());
      } else {
        setContents(this.pkcs.getEncodedPKCS7());
      } 
      this.name = PdfPKCS7.getSubjectFields(this.pkcs.getSigningCertificate()).getField("CN");
      if (this.name != null)
        put(PdfName.NAME, new PdfString(this.name, "UnicodeBig")); 
      this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(get(PdfName.SUBFILTER)));
      this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
    this.externalDigest = digest;
    this.externalRSAdata = RSAdata;
    this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
  }
  
  public String getName() {
    return this.name;
  }
  
  public PdfPKCS7 getSigner() {
    return this.pkcs;
  }
  
  public byte[] getSignerContents() {
    if (PdfName.ADBE_X509_RSA_SHA1.equals(get(PdfName.SUBFILTER)))
      return this.pkcs.getEncodedPKCS1(); 
    return this.pkcs.getEncodedPKCS7();
  }
  
  public static class VeriSign extends PdfSigGenericPKCS {
    public VeriSign() {
      super(PdfName.VERISIGN_PPKVS, PdfName.ADBE_PKCS7_DETACHED);
      this.hashAlgorithm = "MD5";
      put(PdfName.R, new PdfNumber(65537));
    }
    
    public VeriSign(String provider) {
      this();
      this.provider = provider;
    }
  }
  
  public static class PPKLite extends PdfSigGenericPKCS {
    public PPKLite() {
      super(PdfName.ADOBE_PPKLITE, PdfName.ADBE_X509_RSA_SHA1);
      this.hashAlgorithm = "SHA1";
      put(PdfName.R, new PdfNumber(65541));
    }
    
    public PPKLite(String provider) {
      this();
      this.provider = provider;
    }
  }
  
  public static class PPKMS extends PdfSigGenericPKCS {
    public PPKMS() {
      super(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_SHA1);
      this.hashAlgorithm = "SHA1";
    }
    
    public PPKMS(String provider) {
      this();
      this.provider = provider;
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfSigGenericPKCS.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */