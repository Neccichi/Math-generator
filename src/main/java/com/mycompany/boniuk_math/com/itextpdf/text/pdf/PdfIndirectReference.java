package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class PdfIndirectReference extends PdfObject {
  protected int number;
  
  protected int generation = 0;
  
  protected PdfIndirectReference() {
    super(0);
  }
  
  PdfIndirectReference(int type, int number, int generation) {
    super(0, number + " " + generation + " R");
    this.number = number;
    this.generation = generation;
  }
  
  PdfIndirectReference(int type, int number) {
    this(type, number, 0);
  }
  
  public int getNumber() {
    return this.number;
  }
  
  public int getGeneration() {
    return this.generation;
  }
  
  public String toString() {
    return this.number + " " + this.generation + " R";
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfIndirectReference.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */