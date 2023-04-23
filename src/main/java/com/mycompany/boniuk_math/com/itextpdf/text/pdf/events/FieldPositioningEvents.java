package com.mycompany.boniuk_math.com.itextpdf.text.pdf.events;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAnnotation;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfContentByte;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfFormField;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPCell;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPCellEvent;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPageEventHelper;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfRectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.TextField;
import java.io.IOException;
import java.util.HashMap;

public class FieldPositioningEvents extends PdfPageEventHelper implements PdfPCellEvent {
  protected HashMap<String, PdfFormField> genericChunkFields = new HashMap<String, PdfFormField>();
  
  protected PdfFormField cellField = null;
  
  protected PdfWriter fieldWriter = null;
  
  protected PdfFormField parent = null;
  
  public float padding;
  
  public FieldPositioningEvents() {}
  
  public void addField(String text, PdfFormField field) {
    this.genericChunkFields.put(text, field);
  }
  
  public FieldPositioningEvents(PdfWriter writer, PdfFormField field) {
    this.cellField = field;
    this.fieldWriter = writer;
  }
  
  public FieldPositioningEvents(PdfFormField parent, PdfFormField field) {
    this.cellField = field;
    this.parent = parent;
  }
  
  public FieldPositioningEvents(PdfWriter writer, String text) throws IOException, DocumentException {
    this.fieldWriter = writer;
    TextField tf = new TextField(writer, new Rectangle(0.0F, 0.0F), text);
    tf.setFontSize(14.0F);
    this.cellField = tf.getTextField();
  }
  
  public FieldPositioningEvents(PdfWriter writer, PdfFormField parent, String text) throws IOException, DocumentException {
    this.parent = parent;
    TextField tf = new TextField(writer, new Rectangle(0.0F, 0.0F), text);
    tf.setFontSize(14.0F);
    this.cellField = tf.getTextField();
  }
  
  public void setPadding(float padding) {
    this.padding = padding;
  }
  
  public void setParent(PdfFormField parent) {
    this.parent = parent;
  }
  
  public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
    rect.setBottom(rect.getBottom() - 3.0F);
    PdfFormField field = this.genericChunkFields.get(text);
    if (field == null) {
      TextField tf = new TextField(writer, new Rectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)), text);
      tf.setFontSize(14.0F);
      try {
        field = tf.getTextField();
      } catch (Exception e) {
        throw new ExceptionConverter(e);
      } 
    } else {
      field.put(PdfName.RECT, (PdfObject)new PdfRectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)));
    } 
    if (this.parent == null) {
      writer.addAnnotation((PdfAnnotation)field);
    } else {
      this.parent.addKid(field);
    } 
  }
  
  public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvases) {
    if (this.cellField == null || (this.fieldWriter == null && this.parent == null))
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.used.the.wrong.constructor.for.this.fieldpositioningevents.class", new Object[0])); 
    this.cellField.put(PdfName.RECT, (PdfObject)new PdfRectangle(rect.getLeft(this.padding), rect.getBottom(this.padding), rect.getRight(this.padding), rect.getTop(this.padding)));
    if (this.parent == null) {
      this.fieldWriter.addAnnotation((PdfAnnotation)this.cellField);
    } else {
      this.parent.addKid(this.cellField);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\events\FieldPositioningEvents.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */