package com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;

public class PdfTargetDictionary extends PdfDictionary {
  public PdfTargetDictionary(PdfTargetDictionary nested) {
    put(PdfName.R, (PdfObject)PdfName.P);
    if (nested != null)
      setAdditionalPath(nested); 
  }
  
  public PdfTargetDictionary(boolean child) {
    if (child) {
      put(PdfName.R, (PdfObject)PdfName.C);
    } else {
      put(PdfName.R, (PdfObject)PdfName.P);
    } 
  }
  
  public void setEmbeddedFileName(String target) {
    put(PdfName.N, (PdfObject)new PdfString(target, null));
  }
  
  public void setFileAttachmentPagename(String name) {
    put(PdfName.P, (PdfObject)new PdfString(name, null));
  }
  
  public void setFileAttachmentPage(int page) {
    put(PdfName.P, (PdfObject)new PdfNumber(page));
  }
  
  public void setFileAttachmentName(String name) {
    put(PdfName.A, (PdfObject)new PdfString(name, "UnicodeBig"));
  }
  
  public void setFileAttachmentIndex(int annotation) {
    put(PdfName.A, (PdfObject)new PdfNumber(annotation));
  }
  
  public void setAdditionalPath(PdfTargetDictionary nested) {
    put(PdfName.T, (PdfObject)nested);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\collection\PdfTargetDictionary.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */