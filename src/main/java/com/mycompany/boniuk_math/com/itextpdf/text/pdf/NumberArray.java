package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.util.List;

public class NumberArray extends PdfArray {
  public NumberArray(float... numbers) {
    for (float f : numbers)
      add(new PdfNumber(f)); 
  }
  
  public NumberArray(List<PdfNumber> numbers) {
    for (PdfNumber n : numbers)
      add(n); 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\NumberArray.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */