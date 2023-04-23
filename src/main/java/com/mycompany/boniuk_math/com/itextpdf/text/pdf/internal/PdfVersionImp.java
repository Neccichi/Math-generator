package com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal;

import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.OutputStreamCounter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDeveloperExtension;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfVersion;
import java.io.IOException;

public class PdfVersionImp implements PdfVersion {
  public static final byte[][] HEADER = new byte[][] { DocWriter.getISOBytes("\n"), DocWriter.getISOBytes("%PDF-"), DocWriter.getISOBytes("\n%âãÏÓ\n") };
  
  protected boolean headerWasWritten = false;
  
  protected boolean appendmode = false;
  
  protected char header_version = '4';
  
  protected PdfName catalog_version = null;
  
  protected PdfDictionary extensions = null;
  
  public void setPdfVersion(char version) {
    if (this.headerWasWritten || this.appendmode) {
      setPdfVersion(getVersionAsName(version));
    } else {
      this.header_version = version;
    } 
  }
  
  public void setAtLeastPdfVersion(char version) {
    if (version > this.header_version)
      setPdfVersion(version); 
  }
  
  public void setPdfVersion(PdfName version) {
    if (this.catalog_version == null || this.catalog_version.compareTo(version) < 0)
      this.catalog_version = version; 
  }
  
  public void setAppendmode(boolean appendmode) {
    this.appendmode = appendmode;
  }
  
  public void writeHeader(OutputStreamCounter os) throws IOException {
    if (this.appendmode) {
      os.write(HEADER[0]);
    } else {
      os.write(HEADER[1]);
      os.write(getVersionAsByteArray(this.header_version));
      os.write(HEADER[2]);
      this.headerWasWritten = true;
    } 
  }
  
  public PdfName getVersionAsName(char version) {
    switch (version) {
      case '2':
        return PdfWriter.PDF_VERSION_1_2;
      case '3':
        return PdfWriter.PDF_VERSION_1_3;
      case '4':
        return PdfWriter.PDF_VERSION_1_4;
      case '5':
        return PdfWriter.PDF_VERSION_1_5;
      case '6':
        return PdfWriter.PDF_VERSION_1_6;
      case '7':
        return PdfWriter.PDF_VERSION_1_7;
    } 
    return PdfWriter.PDF_VERSION_1_4;
  }
  
  public byte[] getVersionAsByteArray(char version) {
    return DocWriter.getISOBytes(getVersionAsName(version).toString().substring(1));
  }
  
  public void addToCatalog(PdfDictionary catalog) {
    if (this.catalog_version != null)
      catalog.put(PdfName.VERSION, (PdfObject)this.catalog_version); 
    if (this.extensions != null)
      catalog.put(PdfName.EXTENSIONS, (PdfObject)this.extensions); 
  }
  
  public void addDeveloperExtension(PdfDeveloperExtension de) {
    if (this.extensions == null) {
      this.extensions = new PdfDictionary();
    } else {
      PdfDictionary extension = this.extensions.getAsDict(de.getPrefix());
      if (extension != null) {
        int diff = de.getBaseversion().compareTo(extension.getAsName(PdfName.BASEVERSION));
        if (diff < 0)
          return; 
        diff = de.getExtensionLevel() - extension.getAsNumber(PdfName.EXTENSIONLEVEL).intValue();
        if (diff <= 0)
          return; 
      } 
    } 
    this.extensions.put(de.getPrefix(), (PdfObject)de.getDeveloperExtensions());
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\internal\PdfVersionImp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */