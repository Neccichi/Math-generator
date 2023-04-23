package com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.ImgJBIG2;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.RandomAccessFileOrArray;

public class JBIG2Image {
  public static byte[] getGlobalSegment(RandomAccessFileOrArray ra) {
    try {
      JBIG2SegmentReader sr = new JBIG2SegmentReader(ra);
      sr.read();
      return sr.getGlobal(true);
    } catch (Exception e) {
      return null;
    } 
  }
  
  public static Image getJbig2Image(RandomAccessFileOrArray ra, int page) {
    if (page < 1)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.page.number.must.be.gt.eq.1", new Object[0])); 
    try {
      JBIG2SegmentReader sr = new JBIG2SegmentReader(ra);
      sr.read();
      JBIG2SegmentReader.JBIG2Page p = sr.getPage(page);
      return (Image)new ImgJBIG2(p.pageBitmapWidth, p.pageBitmapHeight, p.getData(true), sr.getGlobal(true));
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public static int getNumberOfPages(RandomAccessFileOrArray ra) {
    try {
      JBIG2SegmentReader sr = new JBIG2SegmentReader(ra);
      sr.read();
      return sr.numberOfPages();
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\codec\JBIG2Image.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */