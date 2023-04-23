package com.mycompany.boniuk_math.com.itextpdf.text.pdf.crypto;

public final class IVGenerator {
  private static ARCFOUREncryption arcfour = new ARCFOUREncryption();
  
  static {
    long time = System.currentTimeMillis();
    long mem = Runtime.getRuntime().freeMemory();
    String s = time + "+" + mem;
    arcfour.prepareARCFOURKey(s.getBytes());
  }
  
  public static byte[] getIV() {
    return getIV(16);
  }
  
  public static byte[] getIV(int len) {
    byte[] b = new byte[len];
    synchronized (arcfour) {
      arcfour.encryptARCFOUR(b);
    } 
    return b;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\crypto\IVGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */