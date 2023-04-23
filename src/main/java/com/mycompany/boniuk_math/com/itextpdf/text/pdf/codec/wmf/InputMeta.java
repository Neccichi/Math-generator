package com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.wmf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.Utilities;
import java.io.IOException;
import java.io.InputStream;

public class InputMeta {
  InputStream in;
  
  int length;
  
  public InputMeta(InputStream in) {
    this.in = in;
  }
  
  public int readWord() throws IOException {
    this.length += 2;
    int k1 = this.in.read();
    if (k1 < 0)
      return 0; 
    return k1 + (this.in.read() << 8) & 0xFFFF;
  }
  
  public int readShort() throws IOException {
    int k = readWord();
    if (k > 32767)
      k -= 65536; 
    return k;
  }
  
  public int readInt() throws IOException {
    this.length += 4;
    int k1 = this.in.read();
    if (k1 < 0)
      return 0; 
    int k2 = this.in.read() << 8;
    int k3 = this.in.read() << 16;
    return k1 + k2 + k3 + (this.in.read() << 24);
  }
  
  public int readByte() throws IOException {
    this.length++;
    return this.in.read() & 0xFF;
  }
  
  public void skip(int len) throws IOException {
    this.length += len;
    Utilities.skip(this.in, len);
  }
  
  public int getLength() {
    return this.length;
  }
  
  public BaseColor readColor() throws IOException {
    int red = readByte();
    int green = readByte();
    int blue = readByte();
    readByte();
    return new BaseColor(red, green, blue);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\codec\wmf\InputMeta.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */