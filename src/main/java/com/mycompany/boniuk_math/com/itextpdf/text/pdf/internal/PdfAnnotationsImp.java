package com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal;

import com.mycompany.boniuk_math.com.itextpdf.text.Annotation;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAcroForm;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAction;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAnnotation;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfFileSpecification;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfFormField;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfRectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfTemplate;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class PdfAnnotationsImp {
  protected PdfAcroForm acroForm;
  
  protected ArrayList<PdfAnnotation> annotations;
  
  protected ArrayList<PdfAnnotation> delayedAnnotations = new ArrayList<PdfAnnotation>();
  
  public PdfAnnotationsImp(PdfWriter writer) {
    this.acroForm = new PdfAcroForm(writer);
  }
  
  public boolean hasValidAcroForm() {
    return this.acroForm.isValid();
  }
  
  public PdfAcroForm getAcroForm() {
    return this.acroForm;
  }
  
  public void setSigFlags(int f) {
    this.acroForm.setSigFlags(f);
  }
  
  public void addCalculationOrder(PdfFormField formField) {
    this.acroForm.addCalculationOrder(formField);
  }
  
  public void addAnnotation(PdfAnnotation annot) {
    if (annot.isForm()) {
      PdfFormField field = (PdfFormField)annot;
      if (field.getParent() == null)
        addFormFieldRaw(field); 
    } else {
      this.annotations.add(annot);
    } 
  }
  
  public void addPlainAnnotation(PdfAnnotation annot) {
    this.annotations.add(annot);
  }
  
  void addFormFieldRaw(PdfFormField field) {
    this.annotations.add(field);
    ArrayList<PdfFormField> kids = field.getKids();
    if (kids != null)
      for (int k = 0; k < kids.size(); k++)
        addFormFieldRaw(kids.get(k));  
  }
  
  public boolean hasUnusedAnnotations() {
    return !this.annotations.isEmpty();
  }
  
  public void resetAnnotations() {
    this.annotations = this.delayedAnnotations;
    this.delayedAnnotations = new ArrayList<PdfAnnotation>();
  }
  
  public PdfArray rotateAnnotations(PdfWriter writer, Rectangle pageSize) {
    PdfArray array = new PdfArray();
    int rotation = pageSize.getRotation() % 360;
    int currentPage = writer.getCurrentPageNumber();
    for (int k = 0; k < this.annotations.size(); k++) {
      PdfAnnotation dic = this.annotations.get(k);
      int page = dic.getPlaceInPage();
      if (page > currentPage) {
        this.delayedAnnotations.add(dic);
      } else {
        if (dic.isForm()) {
          if (!dic.isUsed()) {
            HashSet<PdfTemplate> templates = dic.getTemplates();
            if (templates != null)
              this.acroForm.addFieldTemplates(templates); 
          } 
          PdfFormField field = (PdfFormField)dic;
          if (field.getParent() == null)
            this.acroForm.addDocumentField(field.getIndirectReference()); 
        } 
        if (dic.isAnnotation()) {
          array.add((PdfObject)dic.getIndirectReference());
          if (!dic.isUsed()) {
            PdfRectangle rect = (PdfRectangle)dic.get(PdfName.RECT);
            if (rect != null)
              switch (rotation) {
                case 90:
                  dic.put(PdfName.RECT, (PdfObject)new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                  break;
                case 180:
                  dic.put(PdfName.RECT, (PdfObject)new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                  break;
                case 270:
                  dic.put(PdfName.RECT, (PdfObject)new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                  break;
              }  
          } 
        } 
        if (!dic.isUsed()) {
          dic.setUsed();
          try {
            writer.addToBody((PdfObject)dic, dic.getIndirectReference());
          } catch (IOException e) {
            throw new ExceptionConverter(e);
          } 
        } 
      } 
    } 
    return array;
  }
  
  public static PdfAnnotation convertAnnotation(PdfWriter writer, Annotation annot, Rectangle defaultRect) throws IOException {
    boolean[] sparams;
    String fname;
    String mimetype;
    PdfFileSpecification fs;
    PdfAnnotation ann;
    switch (annot.annotationType()) {
      case 1:
        return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((URL)annot.attributes().get("url")));
      case 2:
        return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.attributes().get("file")));
      case 3:
        return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.attributes().get("file"), (String)annot.attributes().get("destination")));
      case 7:
        sparams = (boolean[])annot.attributes().get("parameters");
        fname = (String)annot.attributes().get("file");
        mimetype = (String)annot.attributes().get("mime");
        if (sparams[0]) {
          fs = PdfFileSpecification.fileEmbedded(writer, fname, fname, null);
        } else {
          fs = PdfFileSpecification.fileExtern(writer, fname);
        } 
        ann = PdfAnnotation.createScreen(writer, new Rectangle(annot.llx(), annot.lly(), annot.urx(), annot.ury()), fname, fs, mimetype, sparams[1]);
        return ann;
      case 4:
        return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.attributes().get("file"), ((Integer)annot.attributes().get("page")).intValue()));
      case 5:
        return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction(((Integer)annot.attributes().get("named")).intValue()));
      case 6:
        return new PdfAnnotation(writer, annot.llx(), annot.lly(), annot.urx(), annot.ury(), new PdfAction((String)annot.attributes().get("application"), (String)annot.attributes().get("parameters"), (String)annot.attributes().get("operation"), (String)annot.attributes().get("defaultdir")));
    } 
    return new PdfAnnotation(writer, defaultRect.getLeft(), defaultRect.getBottom(), defaultRect.getRight(), defaultRect.getTop(), new PdfString(annot.title(), "UnicodeBig"), new PdfString(annot.content(), "UnicodeBig"));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\internal\PdfAnnotationsImp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */