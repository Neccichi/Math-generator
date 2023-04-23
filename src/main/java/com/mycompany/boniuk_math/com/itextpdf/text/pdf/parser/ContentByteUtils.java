package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PRIndirectReference;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PRStream;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ListIterator;

public class ContentByteUtils {
  public static byte[] getContentBytesFromContentObject(PdfObject contentObject) throws IOException {
    byte[] result;
    PRIndirectReference ref;
    PdfObject directObject;
    PRStream stream;
    ByteArrayOutputStream allBytes;
    PdfArray contentArray;
    ListIterator<PdfObject> iter;
    switch (contentObject.type()) {
      case 10:
        ref = (PRIndirectReference)contentObject;
        directObject = PdfReader.getPdfObject((PdfObject)ref);
        result = getContentBytesFromContentObject(directObject);
        return result;
      case 7:
        stream = (PRStream)PdfReader.getPdfObject(contentObject);
        result = PdfReader.getStreamBytes(stream);
        return result;
      case 5:
        allBytes = new ByteArrayOutputStream();
        contentArray = (PdfArray)contentObject;
        iter = contentArray.listIterator();
        while (iter.hasNext()) {
          PdfObject element = iter.next();
          allBytes.write(getContentBytesFromContentObject(element));
          allBytes.write(32);
        } 
        result = allBytes.toByteArray();
        return result;
    } 
    String msg = "Unable to handle Content of type " + contentObject.getClass();
    throw new IllegalStateException(msg);
  }
  
  public static byte[] getContentBytesForPage(PdfReader reader, int pageNum) throws IOException {
    PdfDictionary pageDictionary = reader.getPageN(pageNum);
    PdfObject contentObject = pageDictionary.get(PdfName.CONTENTS);
    if (contentObject == null)
      return new byte[0]; 
    byte[] contentBytes = getContentBytesFromContentObject(contentObject);
    return contentBytes;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\ContentByteUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */