package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection.PdfCollection;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfEncryptionSettings;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfViewerPreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfStamper implements PdfViewerPreferences, PdfEncryptionSettings {
  protected PdfStamperImp stamper;
  
  private Map<String, String> moreInfo;
  
  private boolean hasSignature;
  
  private PdfSignatureAppearance sigApp;
  
  public PdfStamper(PdfReader reader, OutputStream os) throws DocumentException, IOException {
    this.stamper = new PdfStamperImp(reader, os, false, false);
  }
  
  public PdfStamper(PdfReader reader, OutputStream os, char pdfVersion) throws DocumentException, IOException {
    this.stamper = new PdfStamperImp(reader, os, pdfVersion, false);
  }
  
  public PdfStamper(PdfReader reader, OutputStream os, char pdfVersion, boolean append) throws DocumentException, IOException {
    this.stamper = new PdfStamperImp(reader, os, pdfVersion, append);
  }
  
  public Map<String, String> getMoreInfo() {
    return this.moreInfo;
  }
  
  public void setMoreInfo(Map<String, String> moreInfo) {
    this.moreInfo = moreInfo;
  }
  
  public void replacePage(PdfReader r, int pageImported, int pageReplaced) {
    this.stamper.replacePage(r, pageImported, pageReplaced);
  }
  
  public void insertPage(int pageNumber, Rectangle mediabox) {
    this.stamper.insertPage(pageNumber, mediabox);
  }
  
  public PdfSignatureAppearance getSignatureAppearance() {
    return this.sigApp;
  }
  
  public void close() throws DocumentException, IOException {
    if (!this.hasSignature) {
      this.stamper.close(this.moreInfo);
      return;
    } 
    this.sigApp.preClose();
    PdfSigGenericPKCS sig = this.sigApp.getSigStandard();
    PdfLiteral lit = (PdfLiteral)sig.get(PdfName.CONTENTS);
    int totalBuf = (lit.getPosLength() - 2) / 2;
    byte[] buf = new byte[8192];
    InputStream inp = this.sigApp.getRangeStream();
    try {
      int n;
      while ((n = inp.read(buf)) > 0)
        sig.getSigner().update(buf, 0, n); 
    } catch (SignatureException se) {
      throw new ExceptionConverter(se);
    } 
    buf = new byte[totalBuf];
    byte[] bsig = sig.getSignerContents();
    System.arraycopy(bsig, 0, buf, 0, bsig.length);
    PdfString str = new PdfString(buf);
    str.setHexWriting(true);
    PdfDictionary dic = new PdfDictionary();
    dic.put(PdfName.CONTENTS, str);
    this.sigApp.close(dic);
    this.stamper.reader.close();
  }
  
  public PdfContentByte getUnderContent(int pageNum) {
    return this.stamper.getUnderContent(pageNum);
  }
  
  public PdfContentByte getOverContent(int pageNum) {
    return this.stamper.getOverContent(pageNum);
  }
  
  public boolean isRotateContents() {
    return this.stamper.isRotateContents();
  }
  
  public void setRotateContents(boolean rotateContents) {
    this.stamper.setRotateContents(rotateContents);
  }
  
  public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException {
    if (this.stamper.isAppend())
      throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status", new Object[0])); 
    if (this.stamper.isContentWritten())
      throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output", new Object[0])); 
    this.stamper.setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
  }
  
  public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
    if (this.stamper.isAppend())
      throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status", new Object[0])); 
    if (this.stamper.isContentWritten())
      throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output", new Object[0])); 
    this.stamper.setEncryption(userPassword, ownerPassword, permissions, encryptionType);
  }
  
  public void setEncryption(boolean strength, String userPassword, String ownerPassword, int permissions) throws DocumentException {
    setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, strength);
  }
  
  public void setEncryption(int encryptionType, String userPassword, String ownerPassword, int permissions) throws DocumentException {
    setEncryption(DocWriter.getISOBytes(userPassword), DocWriter.getISOBytes(ownerPassword), permissions, encryptionType);
  }
  
  public void setEncryption(Certificate[] certs, int[] permissions, int encryptionType) throws DocumentException {
    if (this.stamper.isAppend())
      throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.does.not.support.changing.the.encryption.status", new Object[0])); 
    if (this.stamper.isContentWritten())
      throw new DocumentException(MessageLocalization.getComposedMessage("content.was.already.written.to.the.output", new Object[0])); 
    this.stamper.setEncryption(certs, permissions, encryptionType);
  }
  
  public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
    return this.stamper.getImportedPage(reader, pageNumber);
  }
  
  public PdfWriter getWriter() {
    return this.stamper;
  }
  
  public PdfReader getReader() {
    return this.stamper.reader;
  }
  
  public AcroFields getAcroFields() {
    return this.stamper.getAcroFields();
  }
  
  public void setFormFlattening(boolean flat) {
    this.stamper.setFormFlattening(flat);
  }
  
  public void setFreeTextFlattening(boolean flat) {
    this.stamper.setFreeTextFlattening(flat);
  }
  
  public void addAnnotation(PdfAnnotation annot, int page) {
    this.stamper.addAnnotation(annot, page);
  }
  
  public PdfFormField addSignature(String name, int page, float llx, float lly, float urx, float ury) {
    PdfAcroForm acroForm = this.stamper.getAcroForm();
    PdfFormField signature = PdfFormField.createSignature(this.stamper);
    acroForm.setSignatureParams(signature, name, llx, lly, urx, ury);
    acroForm.drawSignatureAppearences(signature, llx, lly, urx, ury);
    addAnnotation(signature, page);
    return signature;
  }
  
  public void addComments(FdfReader fdf) throws IOException {
    this.stamper.addComments(fdf);
  }
  
  public void setOutlines(List<HashMap<String, Object>> outlines) {
    this.stamper.setOutlines(outlines);
  }
  
  public void setThumbnail(Image image, int page) throws PdfException, DocumentException {
    this.stamper.setThumbnail(image, page);
  }
  
  public boolean partialFormFlattening(String name) {
    return this.stamper.partialFormFlattening(name);
  }
  
  public void addJavaScript(String js) {
    this.stamper.addJavaScript(js, !PdfEncodings.isPdfDocEncoding(js));
  }
  
  public void addFileAttachment(String description, byte[] fileStore, String file, String fileDisplay) throws IOException {
    addFileAttachment(description, PdfFileSpecification.fileEmbedded(this.stamper, file, fileDisplay, fileStore));
  }
  
  public void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
    this.stamper.addFileAttachment(description, fs);
  }
  
  public void makePackage(PdfName initialView) {
    PdfCollection collection = new PdfCollection(0);
    collection.put(PdfName.VIEW, initialView);
    this.stamper.makePackage(collection);
  }
  
  public void makePackage(PdfCollection collection) {
    this.stamper.makePackage(collection);
  }
  
  public void setViewerPreferences(int preferences) {
    this.stamper.setViewerPreferences(preferences);
  }
  
  public void addViewerPreference(PdfName key, PdfObject value) {
    this.stamper.addViewerPreference(key, value);
  }
  
  public void setXmpMetadata(byte[] xmp) {
    this.stamper.setXmpMetadata(xmp);
  }
  
  public boolean isFullCompression() {
    return this.stamper.isFullCompression();
  }
  
  public void setFullCompression() {
    if (this.stamper.isAppend())
      return; 
    this.stamper.setFullCompression();
  }
  
  public void setPageAction(PdfName actionType, PdfAction action, int page) throws PdfException {
    this.stamper.setPageAction(actionType, action, page);
  }
  
  public void setDuration(int seconds, int page) {
    this.stamper.setDuration(seconds, page);
  }
  
  public void setTransition(PdfTransition transition, int page) {
    this.stamper.setTransition(transition, page);
  }
  
  public static PdfStamper createSignature(PdfReader reader, OutputStream os, char pdfVersion, File tempFile, boolean append) throws DocumentException, IOException {
    PdfStamper stp;
    if (tempFile == null) {
      ByteBuffer bout = new ByteBuffer();
      stp = new PdfStamper(reader, bout, pdfVersion, append);
      stp.sigApp = new PdfSignatureAppearance(stp.stamper);
      stp.sigApp.setSigout(bout);
    } else {
      if (tempFile.isDirectory())
        tempFile = File.createTempFile("pdf", null, tempFile); 
      FileOutputStream fout = new FileOutputStream(tempFile);
      stp = new PdfStamper(reader, fout, pdfVersion, append);
      stp.sigApp = new PdfSignatureAppearance(stp.stamper);
      stp.sigApp.setTempFile(tempFile);
    } 
    stp.sigApp.setOriginalout(os);
    stp.sigApp.setStamper(stp);
    stp.hasSignature = true;
    PdfDictionary catalog = reader.getCatalog();
    PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
    if (acroForm != null) {
      acroForm.remove(PdfName.NEEDAPPEARANCES);
      stp.stamper.markUsed(acroForm);
    } 
    return stp;
  }
  
  public static PdfStamper createSignature(PdfReader reader, OutputStream os, char pdfVersion) throws DocumentException, IOException {
    return createSignature(reader, os, pdfVersion, null, false);
  }
  
  public static PdfStamper createSignature(PdfReader reader, OutputStream os, char pdfVersion, File tempFile) throws DocumentException, IOException {
    return createSignature(reader, os, pdfVersion, tempFile, false);
  }
  
  public Map<String, PdfLayer> getPdfLayers() {
    return this.stamper.getPdfLayers();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfStamper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */