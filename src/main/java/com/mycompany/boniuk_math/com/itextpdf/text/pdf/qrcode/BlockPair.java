package com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode;

final class BlockPair {
  private final ByteArray dataBytes;
  
  private final ByteArray errorCorrectionBytes;
  
  BlockPair(ByteArray data, ByteArray errorCorrection) {
    this.dataBytes = data;
    this.errorCorrectionBytes = errorCorrection;
  }
  
  public ByteArray getDataBytes() {
    return this.dataBytes;
  }
  
  public ByteArray getErrorCorrectionBytes() {
    return this.errorCorrectionBytes;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\qrcode\BlockPair.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */