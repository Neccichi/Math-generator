package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.Paragraph;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;

public class PdfPageEventHelper implements PdfPageEvent {
  public void onOpenDocument(PdfWriter writer, Document document) {}
  
  public void onStartPage(PdfWriter writer, Document document) {}
  
  public void onEndPage(PdfWriter writer, Document document) {}
  
  public void onCloseDocument(PdfWriter writer, Document document) {}
  
  public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {}
  
  public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {}
  
  public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {}
  
  public void onChapterEnd(PdfWriter writer, Document document, float position) {}
  
  public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {}
  
  public void onSectionEnd(PdfWriter writer, Document document, float position) {}
  
  public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {}
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPageEventHelper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */