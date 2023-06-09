package com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode;

public final class ByteMatrix {
  private final byte[][] bytes;
  
  private final int width;
  
  private final int height;
  
  public ByteMatrix(int width, int height) {
    this.bytes = new byte[height][width];
    this.width = width;
    this.height = height;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public byte get(int x, int y) {
    return this.bytes[y][x];
  }
  
  public byte[][] getArray() {
    return this.bytes;
  }
  
  public void set(int x, int y, byte value) {
    this.bytes[y][x] = value;
  }
  
  public void set(int x, int y, int value) {
    this.bytes[y][x] = (byte)value;
  }
  
  public void clear(byte value) {
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++)
        this.bytes[y][x] = value; 
    } 
  }
  
  public String toString() {
    StringBuffer result = new StringBuffer(2 * this.width * this.height + 2);
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        switch (this.bytes[y][x]) {
          case 0:
            result.append(" 0");
            break;
          case 1:
            result.append(" 1");
            break;
          default:
            result.append("  ");
            break;
        } 
      } 
      result.append('\n');
    } 
    return result.toString();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\qrcode\ByteMatrix.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */