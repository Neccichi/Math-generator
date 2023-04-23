package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class PdfContents extends PdfStream {
  static final byte[] SAVESTATE = DocWriter.getISOBytes("q\n");
  
  static final byte[] RESTORESTATE = DocWriter.getISOBytes("Q\n");
  
  static final byte[] ROTATE90 = DocWriter.getISOBytes("0 1 -1 0 ");
  
  static final byte[] ROTATE180 = DocWriter.getISOBytes("-1 0 0 -1 ");
  
  static final byte[] ROTATE270 = DocWriter.getISOBytes("0 -1 1 0 ");
  
  static final byte[] ROTATEFINAL = DocWriter.getISOBytes(" cm\n");
  
  PdfContents(PdfContentByte under, PdfContentByte content, PdfContentByte text, PdfContentByte secondContent, Rectangle page) throws BadPdfFormatException {
    try {
      OutputStream out = null;
      Deflater deflater = null;
      this.streamBytes = new ByteArrayOutputStream();
      if (Document.compress) {
        this.compressed = true;
        this.compressionLevel = text.getPdfWriter().getCompressionLevel();
        deflater = new Deflater(this.compressionLevel);
        out = new DeflaterOutputStream(this.streamBytes, deflater);
      } else {
        out = this.streamBytes;
      } 
      int rotation = page.getRotation();
      switch (rotation) {
        case 90:
          out.write(ROTATE90);
          out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
          out.write(32);
          out.write(48);
          out.write(ROTATEFINAL);
          break;
        case 180:
          out.write(ROTATE180);
          out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
          out.write(32);
          out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
          out.write(ROTATEFINAL);
          break;
        case 270:
          out.write(ROTATE270);
          out.write(48);
          out.write(32);
          out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
          out.write(ROTATEFINAL);
          break;
      } 
      if (under.size() > 0) {
        out.write(SAVESTATE);
        under.getInternalBuffer().writeTo(out);
        out.write(RESTORESTATE);
      } 
      if (content.size() > 0) {
        out.write(SAVESTATE);
        content.getInternalBuffer().writeTo(out);
        out.write(RESTORESTATE);
      } 
      if (text != null) {
        out.write(SAVESTATE);
        text.getInternalBuffer().writeTo(out);
        out.write(RESTORESTATE);
      } 
      if (secondContent.size() > 0)
        secondContent.getInternalBuffer().writeTo(out); 
      out.close();
      if (deflater != null)
        deflater.end(); 
    } catch (Exception e) {
      throw new BadPdfFormatException(e.getMessage());
    } 
    put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
    if (this.compressed)
      put(PdfName.FILTER, PdfName.FLATEDECODE); 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfContents.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */