package com.mycompany.boniuk_math.com.itextpdf.text.pdf.events;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.Paragraph;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPageEvent;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;
import java.util.ArrayList;

public class PdfPageEventForwarder implements PdfPageEvent {
  protected ArrayList<PdfPageEvent> events = new ArrayList<PdfPageEvent>();
  
  public void addPageEvent(PdfPageEvent event) {
    this.events.add(event);
  }
  
  public void onOpenDocument(PdfWriter writer, Document document) {
    for (PdfPageEvent event : this.events)
      event.onOpenDocument(writer, document); 
  }
  
  public void onStartPage(PdfWriter writer, Document document) {
    for (PdfPageEvent event : this.events)
      event.onStartPage(writer, document); 
  }
  
  public void onEndPage(PdfWriter writer, Document document) {
    for (PdfPageEvent event : this.events)
      event.onEndPage(writer, document); 
  }
  
  public void onCloseDocument(PdfWriter writer, Document document) {
    for (PdfPageEvent event : this.events)
      event.onCloseDocument(writer, document); 
  }
  
  public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
    for (PdfPageEvent event : this.events)
      event.onParagraph(writer, document, paragraphPosition); 
  }
  
  public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
    for (PdfPageEvent event : this.events)
      event.onParagraphEnd(writer, document, paragraphPosition); 
  }
  
  public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
    for (PdfPageEvent event : this.events)
      event.onChapter(writer, document, paragraphPosition, title); 
  }
  
  public void onChapterEnd(PdfWriter writer, Document document, float position) {
    for (PdfPageEvent event : this.events)
      event.onChapterEnd(writer, document, position); 
  }
  
  public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
    for (PdfPageEvent event : this.events)
      event.onSection(writer, document, paragraphPosition, depth, title); 
  }
  
  public void onSectionEnd(PdfWriter writer, Document document, float position) {
    for (PdfPageEvent event : this.events)
      event.onSectionEnd(writer, document, position); 
  }
  
  public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
    for (PdfPageEvent element : this.events) {
      PdfPageEvent event = element;
      event.onGenericTag(writer, document, rect, text);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\events\PdfPageEventForwarder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */