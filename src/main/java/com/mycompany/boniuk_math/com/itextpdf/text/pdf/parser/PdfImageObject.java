package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PRStream;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfReader;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.PngWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.TiffWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PdfImageObject {
  private PdfDictionary dictionary;
  
  private byte[] streamBytes;
  
  private int pngColorType = -1;
  
  private int pngBitDepth;
  
  private int width;
  
  private int height;
  
  private int bpc;
  
  private byte[] palette;
  
  private byte[] icc;
  
  private int stride;
  
  private boolean decoded;
  
  public static final String TYPE_PNG = "png";
  
  public static final String TYPE_JPG = "jpg";
  
  public static final String TYPE_JP2 = "jp2";
  
  public static final String TYPE_TIF = "tif";
  
  protected String fileType;
  
  public String getFileType() {
    return this.fileType;
  }
  
  public PdfImageObject(PRStream stream) throws IOException {
    this((PdfDictionary)stream, PdfReader.getStreamBytesRaw(stream));
  }
  
  protected PdfImageObject(PdfDictionary dictionary, byte[] samples) throws IOException {
    this.dictionary = dictionary;
    try {
      this.streamBytes = PdfReader.decodeBytes(samples, dictionary);
      this.decoded = true;
    } catch (UnsupportedPdfException e) {
      this.streamBytes = samples;
      this.decoded = false;
    } 
  }
  
  public PdfObject get(PdfName key) {
    return this.dictionary.get(key);
  }
  
  public PdfDictionary getDictionary() {
    return this.dictionary;
  }
  
  public byte[] getStreamBytes() {
    return this.streamBytes;
  }
  
  private void findColorspace(PdfObject colorspace, boolean allowIndexed) throws IOException {
    if (PdfName.DEVICEGRAY.equals(colorspace)) {
      this.stride = (this.width * this.bpc + 7) / 8;
      this.pngColorType = 0;
    } else if (PdfName.DEVICERGB.equals(colorspace)) {
      if (this.bpc == 8 || this.bpc == 16) {
        this.stride = (this.width * this.bpc * 3 + 7) / 8;
        this.pngColorType = 2;
      } 
    } else if (colorspace instanceof PdfArray) {
      PdfArray ca = (PdfArray)colorspace;
      PdfObject tyca = ca.getDirectObject(0);
      if (PdfName.CALGRAY.equals(tyca)) {
        this.stride = (this.width * this.bpc + 7) / 8;
        this.pngColorType = 0;
      } else if (PdfName.CALRGB.equals(tyca)) {
        if (this.bpc == 8 || this.bpc == 16) {
          this.stride = (this.width * this.bpc * 3 + 7) / 8;
          this.pngColorType = 2;
        } 
      } else if (PdfName.ICCBASED.equals(tyca)) {
        PRStream pr = (PRStream)ca.getDirectObject(1);
        int n = pr.getAsNumber(PdfName.N).intValue();
        if (n == 1) {
          this.stride = (this.width * this.bpc + 7) / 8;
          this.pngColorType = 0;
          this.icc = PdfReader.getStreamBytes(pr);
        } else if (n == 3) {
          this.stride = (this.width * this.bpc * 3 + 7) / 8;
          this.pngColorType = 2;
          this.icc = PdfReader.getStreamBytes(pr);
        } 
      } else if (allowIndexed && PdfName.INDEXED.equals(tyca)) {
        findColorspace(ca.getDirectObject(1), false);
        if (this.pngColorType == 2) {
          PdfObject id2 = ca.getDirectObject(3);
          if (id2 instanceof PdfString) {
            this.palette = ((PdfString)id2).getBytes();
          } else if (id2 instanceof PRStream) {
            this.palette = PdfReader.getStreamBytes((PRStream)id2);
          } 
          this.stride = (this.width * this.bpc + 7) / 8;
          this.pngColorType = 3;
        } 
      } 
    } 
  }
  
  public byte[] getImageAsBytes() throws IOException {
    if (this.streamBytes == null)
      return null; 
    if (!this.decoded) {
      PdfName filter = this.dictionary.getAsName(PdfName.FILTER);
      if (filter == null) {
        PdfArray filterArray = this.dictionary.getAsArray(PdfName.FILTER);
        if (filterArray.size() == 1) {
          filter = filterArray.getAsName(0);
        } else {
          throw new UnsupportedPdfException("Multi-stage filters not supported here (" + filterArray + ")");
        } 
      } 
      if (PdfName.DCTDECODE.equals(filter)) {
        this.fileType = "jpg";
        return this.streamBytes;
      } 
      if (PdfName.JPXDECODE.equals(filter)) {
        this.fileType = "jp2";
        return this.streamBytes;
      } 
      throw new UnsupportedPdfException("Unsupported stream filter " + filter);
    } 
    this.pngColorType = -1;
    this.width = this.dictionary.getAsNumber(PdfName.WIDTH).intValue();
    this.height = this.dictionary.getAsNumber(PdfName.HEIGHT).intValue();
    this.bpc = this.dictionary.getAsNumber(PdfName.BITSPERCOMPONENT).intValue();
    this.pngBitDepth = this.bpc;
    PdfObject colorspace = this.dictionary.getDirectObject(PdfName.COLORSPACE);
    this.palette = null;
    this.icc = null;
    this.stride = 0;
    findColorspace(colorspace, true);
    ByteArrayOutputStream ms = new ByteArrayOutputStream();
    if (this.pngColorType < 0) {
      if (this.bpc != 8)
        return null; 
      if (!PdfName.DEVICECMYK.equals(colorspace))
        if (colorspace instanceof PdfArray) {
          PdfArray ca = (PdfArray)colorspace;
          PdfObject tyca = ca.getDirectObject(0);
          if (!PdfName.ICCBASED.equals(tyca))
            return null; 
          PRStream pr = (PRStream)ca.getDirectObject(1);
          int n = pr.getAsNumber(PdfName.N).intValue();
          if (n != 4)
            return null; 
          this.icc = PdfReader.getStreamBytes(pr);
        } else {
          return null;
        }  
      this.stride = 4 * this.width;
      TiffWriter wr = new TiffWriter();
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldShort(277, 4));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldShort(258, new int[] { 8, 8, 8, 8 }));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldShort(262, 5));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldLong(256, this.width));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldLong(257, this.height));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldShort(259, 5));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldShort(317, 2));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldLong(278, this.height));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldRational(282, new int[] { 300, 1 }));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldRational(283, new int[] { 300, 1 }));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldShort(296, 2));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldAscii(305, Document.getVersion()));
      ByteArrayOutputStream comp = new ByteArrayOutputStream();
      TiffWriter.compressLZW(comp, 2, this.streamBytes, this.height, 4, this.stride);
      byte[] buf = comp.toByteArray();
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldImage(buf));
      wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldLong(279, buf.length));
      if (this.icc != null)
        wr.addField((TiffWriter.FieldBase)new TiffWriter.FieldUndefined(34675, this.icc)); 
      wr.writeFile(ms);
      this.fileType = "tif";
      return ms.toByteArray();
    } 
    PngWriter png = new PngWriter(ms);
    png.writeHeader(this.width, this.height, this.pngBitDepth, this.pngColorType);
    if (this.icc != null)
      png.writeIccProfile(this.icc); 
    if (this.palette != null)
      png.writePalette(this.palette); 
    png.writeData(this.streamBytes, this.stride);
    png.writeEnd();
    this.fileType = "png";
    return ms.toByteArray();
  }
  
  public BufferedImage getBufferedImage() throws IOException {
    byte[] img = getImageAsBytes();
    if (img == null)
      return null; 
    return ImageIO.read(new ByteArrayInputStream(img));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\PdfImageObject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */