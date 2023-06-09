package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;

public class PdfSmartCopy extends PdfCopy {
  private HashMap<ByteStore, PdfIndirectReference> streamMap = null;
  
  public PdfSmartCopy(Document document, OutputStream os) throws DocumentException {
    super(document, os);
    this.streamMap = new HashMap<ByteStore, PdfIndirectReference>();
  }
  
  protected PdfIndirectReference copyIndirect(PRIndirectReference in) throws IOException, BadPdfFormatException {
    PdfIndirectReference theRef;
    PdfObject srcObj = PdfReader.getPdfObjectRelease(in);
    ByteStore streamKey = null;
    boolean validStream = false;
    if (srcObj.isStream()) {
      streamKey = new ByteStore((PRStream)srcObj);
      validStream = true;
      PdfIndirectReference streamRef = this.streamMap.get(streamKey);
      if (streamRef != null)
        return streamRef; 
    } 
    PdfCopy.RefKey key = new PdfCopy.RefKey(in);
    PdfCopy.IndirectReferences iRef = this.indirects.get(key);
    if (iRef != null) {
      theRef = iRef.getRef();
      if (iRef.getCopied())
        return theRef; 
    } else {
      theRef = this.body.getPdfIndirectReference();
      iRef = new PdfCopy.IndirectReferences(theRef);
      this.indirects.put(key, iRef);
    } 
    if (srcObj.isDictionary()) {
      PdfObject type = PdfReader.getPdfObjectRelease(((PdfDictionary)srcObj).get(PdfName.TYPE));
      if (type != null && PdfName.PAGE.equals(type))
        return theRef; 
    } 
    iRef.setCopied();
    if (validStream)
      this.streamMap.put(streamKey, theRef); 
    PdfObject obj = copyObject(srcObj);
    addToBody(obj, theRef);
    return theRef;
  }
  
  static class ByteStore {
    private byte[] b;
    
    private int hash;
    
    private MessageDigest md5;
    
    private void serObject(PdfObject obj, int level, ByteBuffer bb) throws IOException {
      if (level <= 0)
        return; 
      if (obj == null) {
        bb.append("$Lnull");
        return;
      } 
      obj = PdfReader.getPdfObject(obj);
      if (obj.isStream()) {
        bb.append("$B");
        serDic((PdfDictionary)obj, level - 1, bb);
        if (level > 0) {
          this.md5.reset();
          bb.append(this.md5.digest(PdfReader.getStreamBytesRaw((PRStream)obj)));
        } 
      } else if (obj.isDictionary()) {
        serDic((PdfDictionary)obj, level - 1, bb);
      } else if (obj.isArray()) {
        serArray((PdfArray)obj, level - 1, bb);
      } else if (obj.isString()) {
        bb.append("$S").append(obj.toString());
      } else if (obj.isName()) {
        bb.append("$N").append(obj.toString());
      } else {
        bb.append("$L").append(obj.toString());
      } 
    }
    
    private void serDic(PdfDictionary dic, int level, ByteBuffer bb) throws IOException {
      bb.append("$D");
      if (level <= 0)
        return; 
      Object[] keys = dic.getKeys().toArray();
      Arrays.sort(keys);
      for (int k = 0; k < keys.length; k++) {
        serObject((PdfObject)keys[k], level, bb);
        serObject(dic.get((PdfName)keys[k]), level, bb);
      } 
    }
    
    private void serArray(PdfArray array, int level, ByteBuffer bb) throws IOException {
      bb.append("$A");
      if (level <= 0)
        return; 
      for (int k = 0; k < array.size(); k++)
        serObject(array.getPdfObject(k), level, bb); 
    }
    
    ByteStore(PRStream str) throws IOException {
      try {
        this.md5 = MessageDigest.getInstance("MD5");
      } catch (Exception e) {
        throw new ExceptionConverter(e);
      } 
      ByteBuffer bb = new ByteBuffer();
      int level = 100;
      serObject(str, level, bb);
      this.b = bb.toByteArray();
      this.md5 = null;
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof ByteStore))
        return false; 
      if (hashCode() != obj.hashCode())
        return false; 
      return Arrays.equals(this.b, ((ByteStore)obj).b);
    }
    
    public int hashCode() {
      if (this.hash == 0) {
        int len = this.b.length;
        for (int k = 0; k < len; k++)
          this.hash = this.hash * 31 + (this.b[k] & 0xFF); 
      } 
      return this.hash;
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfSmartCopy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */