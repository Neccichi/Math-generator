package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BadElementException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.CCITTG4Encoder;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode.ByteMatrix;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode.QRCodeWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode.WriterException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.Map;

public class BarcodeQRCode {
  ByteMatrix bm;
  
  public BarcodeQRCode(String content, int width, int height, Map<EncodeHintType, Object> hints) {
    try {
      QRCodeWriter qc = new QRCodeWriter();
      this.bm = qc.encode(content, width, height, hints);
    } catch (WriterException ex) {
      throw new ExceptionConverter(ex);
    } 
  }
  
  private byte[] getBitMatrix() {
    int width = this.bm.getWidth();
    int height = this.bm.getHeight();
    int stride = (width + 7) / 8;
    byte[] b = new byte[stride * height];
    byte[][] mt = this.bm.getArray();
    for (int y = 0; y < height; y++) {
      byte[] line = mt[y];
      for (int x = 0; x < width; x++) {
        if (line[x] != 0) {
          int offset = stride * y + x / 8;
          b[offset] = (byte)(b[offset] | (byte)(128 >> x % 8));
        } 
      } 
    } 
    return b;
  }
  
  public Image getImage() throws BadElementException {
    byte[] b = getBitMatrix();
    byte[] g4 = CCITTG4Encoder.compress(b, this.bm.getWidth(), this.bm.getHeight());
    return Image.getInstance(this.bm.getWidth(), this.bm.getHeight(), false, 256, 1, g4, null);
  }
  
  public Image createAwtImage(Color foreground, Color background) {
    int f = foreground.getRGB();
    int g = background.getRGB();
    Canvas canvas = new Canvas();
    int width = this.bm.getWidth();
    int height = this.bm.getHeight();
    int[] pix = new int[width * height];
    byte[][] mt = this.bm.getArray();
    for (int y = 0; y < height; y++) {
      byte[] line = mt[y];
      for (int x = 0; x < width; x++)
        pix[y * width + x] = (line[x] == 0) ? f : g; 
    } 
    Image img = canvas.createImage(new MemoryImageSource(width, height, pix, 0, width));
    return img;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\BarcodeQRCode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */