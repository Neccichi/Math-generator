package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCounter extends OutputStream {
  protected OutputStream out;
  
  protected int counter = 0;
  
  public OutputStreamCounter(OutputStream out) {
    this.out = out;
  }
  
  public void close() throws IOException {
    this.out.close();
  }
  
  public void flush() throws IOException {
    this.out.flush();
  }
  
  public void write(byte[] b) throws IOException {
    this.counter += b.length;
    this.out.write(b);
  }
  
  public void write(int b) throws IOException {
    this.counter++;
    this.out.write(b);
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    this.counter += len;
    this.out.write(b, off, len);
  }
  
  public int getCounter() {
    return this.counter;
  }
  
  public void resetCounter() {
    this.counter = 0;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\OutputStreamCounter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */