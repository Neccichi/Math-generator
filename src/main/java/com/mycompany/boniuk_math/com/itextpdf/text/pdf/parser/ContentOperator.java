package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfLiteral;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import java.util.ArrayList;

public interface ContentOperator {
  void invoke(PdfContentStreamProcessor paramPdfContentStreamProcessor, PdfLiteral paramPdfLiteral, ArrayList<PdfObject> paramArrayList) throws Exception;
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\ContentOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */