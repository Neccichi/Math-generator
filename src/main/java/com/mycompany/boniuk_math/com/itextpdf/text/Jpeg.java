package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Jpeg extends Image {
  public static final int NOT_A_MARKER = -1;
  
  public static final int VALID_MARKER = 0;
  
  public static final int[] VALID_MARKERS = new int[] { 192, 193, 194 };
  
  public static final int UNSUPPORTED_MARKER = 1;
  
  public static final int[] UNSUPPORTED_MARKERS = new int[] { 
      195, 197, 198, 199, 200, 201, 202, 203, 205, 206, 
      207 };
  
  public static final int NOPARAM_MARKER = 2;
  
  public static final int[] NOPARAM_MARKERS = new int[] { 208, 209, 210, 211, 212, 213, 214, 215, 216, 1 };
  
  public static final int M_APP0 = 224;
  
  public static final int M_APP2 = 226;
  
  public static final int M_APPE = 238;
  
  public static final byte[] JFIF_ID = new byte[] { 74, 70, 73, 70, 0 };
  
  private byte[][] icc;
  
  Jpeg(Image image) {
    super(image);
  }
  
  public Jpeg(URL url) throws BadElementException, IOException {
    super(url);
    processParameters();
  }
  
  public Jpeg(byte[] img) throws BadElementException, IOException {
    super((URL)null);
    this.rawData = img;
    this.originalData = img;
    processParameters();
  }
  
  public Jpeg(byte[] img, float width, float height) throws BadElementException, IOException {
    this(img);
    this.scaledWidth = width;
    this.scaledHeight = height;
  }
  
  private static final int getShort(InputStream is) throws IOException {
    return (is.read() << 8) + is.read();
  }
  
  private static final int marker(int marker) {
    int i;
    for (i = 0; i < VALID_MARKERS.length; i++) {
      if (marker == VALID_MARKERS[i])
        return 0; 
    } 
    for (i = 0; i < NOPARAM_MARKERS.length; i++) {
      if (marker == NOPARAM_MARKERS[i])
        return 2; 
    } 
    for (i = 0; i < UNSUPPORTED_MARKERS.length; i++) {
      if (marker == UNSUPPORTED_MARKERS[i])
        return 1; 
    } 
    return -1;
  }
  
  private void processParameters() throws BadElementException, IOException {
    this.type = 32;
    this.originalType = 1;
    InputStream is = null;
    try {
      String errorID;
      if (this.rawData == null) {
        is = this.url.openStream();
        errorID = this.url.toString();
      } else {
        is = new ByteArrayInputStream(this.rawData);
        errorID = "Byte array";
      } 
      if (is.read() != 255 || is.read() != 216)
        throw new BadElementException(MessageLocalization.getComposedMessage("1.is.not.a.valid.jpeg.file", new Object[] { errorID })); 
      boolean firstPass = true;
      while (true) {
        int v = is.read();
        if (v < 0)
          throw new IOException(MessageLocalization.getComposedMessage("premature.eof.while.reading.jpg", new Object[0])); 
        if (v == 255) {
          int marker = is.read();
          if (firstPass && marker == 224) {
            firstPass = false;
            int len = getShort(is);
            if (len < 16) {
              Utilities.skip(is, len - 2);
              continue;
            } 
            byte[] bcomp = new byte[JFIF_ID.length];
            int r = is.read(bcomp);
            if (r != bcomp.length)
              throw new BadElementException(MessageLocalization.getComposedMessage("1.corrupted.jfif.marker", new Object[] { errorID })); 
            boolean found = true;
            for (int k = 0; k < bcomp.length; k++) {
              if (bcomp[k] != JFIF_ID[k]) {
                found = false;
                break;
              } 
            } 
            if (!found) {
              Utilities.skip(is, len - 2 - bcomp.length);
              continue;
            } 
            Utilities.skip(is, 2);
            int units = is.read();
            int dx = getShort(is);
            int dy = getShort(is);
            if (units == 1) {
              this.dpiX = dx;
              this.dpiY = dy;
            } else if (units == 2) {
              this.dpiX = (int)(dx * 2.54F + 0.5F);
              this.dpiY = (int)(dy * 2.54F + 0.5F);
            } 
            Utilities.skip(is, len - 2 - bcomp.length - 7);
            continue;
          } 
          if (marker == 238) {
            int len = getShort(is) - 2;
            byte[] byteappe = new byte[len];
            for (int k = 0; k < len; k++)
              byteappe[k] = (byte)is.read(); 
            if (byteappe.length >= 12) {
              String appe = new String(byteappe, 0, 5, "ISO-8859-1");
              if (appe.equals("Adobe"))
                this.invert = true; 
            } 
            continue;
          } 
          if (marker == 226) {
            int len = getShort(is) - 2;
            byte[] byteapp2 = new byte[len];
            for (int k = 0; k < len; k++)
              byteapp2[k] = (byte)is.read(); 
            if (byteapp2.length >= 14) {
              String app2 = new String(byteapp2, 0, 11, "ISO-8859-1");
              if (app2.equals("ICC_PROFILE")) {
                int order = byteapp2[12] & 0xFF;
                int count = byteapp2[13] & 0xFF;
                if (order < 1)
                  order = 1; 
                if (count < 1)
                  count = 1; 
                if (this.icc == null)
                  this.icc = new byte[count][]; 
                this.icc[order - 1] = byteapp2;
              } 
            } 
            continue;
          } 
          firstPass = false;
          int markertype = marker(marker);
          if (markertype == 0) {
            Utilities.skip(is, 2);
            if (is.read() != 8)
              throw new BadElementException(MessageLocalization.getComposedMessage("1.must.have.8.bits.per.component", new Object[] { errorID })); 
            this.scaledHeight = getShort(is);
            setTop(this.scaledHeight);
            this.scaledWidth = getShort(is);
            setRight(this.scaledWidth);
            this.colorspace = is.read();
            this.bpc = 8;
            break;
          } 
          if (markertype == 1)
            throw new BadElementException(MessageLocalization.getComposedMessage("1.unsupported.jpeg.marker.2", new Object[] { errorID, String.valueOf(marker) })); 
          if (markertype != 2)
            Utilities.skip(is, getShort(is) - 2); 
        } 
      } 
    } finally {
      if (is != null)
        is.close(); 
    } 
    this.plainWidth = getWidth();
    this.plainHeight = getHeight();
    if (this.icc != null) {
      int total = 0;
      for (int k = 0; k < this.icc.length; k++) {
        if (this.icc[k] == null) {
          this.icc = (byte[][])null;
          return;
        } 
        total += (this.icc[k]).length - 14;
      } 
      byte[] ficc = new byte[total];
      total = 0;
      for (int i = 0; i < this.icc.length; i++) {
        System.arraycopy(this.icc[i], 14, ficc, total, (this.icc[i]).length - 14);
        total += (this.icc[i]).length - 14;
      } 
      try {
        ICC_Profile icc_prof = ICC_Profile.getInstance(ficc);
        tagICC(icc_prof);
      } catch (IllegalArgumentException e) {}
      this.icc = (byte[][])null;
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Jpeg.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */