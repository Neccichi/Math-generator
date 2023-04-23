package com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec;

import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

public class PngWriter {
  private static final byte[] PNG_SIGNTURE = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
  
  private static final byte[] IHDR = DocWriter.getISOBytes("IHDR");
  
  private static final byte[] PLTE = DocWriter.getISOBytes("PLTE");
  
  private static final byte[] IDAT = DocWriter.getISOBytes("IDAT");
  
  private static final byte[] IEND = DocWriter.getISOBytes("IEND");
  
  private static final byte[] iCCP = DocWriter.getISOBytes("iCCP");
  
  private static long[] crc_table;
  
  private OutputStream outp;
  
  public PngWriter(OutputStream outp) throws IOException {
    this.outp = outp;
    outp.write(PNG_SIGNTURE);
  }
  
  public void writeHeader(int width, int height, int bitDepth, int colorType) throws IOException {
    ByteArrayOutputStream ms = new ByteArrayOutputStream();
    outputInt(width, ms);
    outputInt(height, ms);
    ms.write(bitDepth);
    ms.write(colorType);
    ms.write(0);
    ms.write(0);
    ms.write(0);
    writeChunk(IHDR, ms.toByteArray());
  }
  
  public void writeEnd() throws IOException {
    writeChunk(IEND, new byte[0]);
  }
  
  public void writeData(byte[] data, int stride) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DeflaterOutputStream zip = new DeflaterOutputStream(stream);
    int k;
    for (k = 0; k < data.length; k += stride) {
      zip.write(0);
      zip.write(data, k, stride);
    } 
    zip.finish();
    writeChunk(IDAT, stream.toByteArray());
  }
  
  public void writePalette(byte[] data) throws IOException {
    writeChunk(PLTE, data);
  }
  
  public void writeIccProfile(byte[] data) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    stream.write(73);
    stream.write(67);
    stream.write(67);
    stream.write(0);
    stream.write(0);
    DeflaterOutputStream zip = new DeflaterOutputStream(stream);
    zip.write(data);
    zip.finish();
    writeChunk(iCCP, stream.toByteArray());
  }
  
  private static void make_crc_table() {
    if (crc_table != null)
      return; 
    long[] crc2 = new long[256];
    for (int n = 0; n < 256; n++) {
      long c = n;
      for (int k = 0; k < 8; k++) {
        if ((c & 0x1L) != 0L) {
          c = 0xEDB88320L ^ c >> 1L;
        } else {
          c >>= 1L;
        } 
      } 
      crc2[n] = c;
    } 
    crc_table = crc2;
  }
  
  private static long update_crc(long crc, byte[] buf, int offset, int len) {
    long c = crc;
    if (crc_table == null)
      make_crc_table(); 
    for (int n = 0; n < len; n++)
      c = crc_table[(int)((c ^ buf[n + offset]) & 0xFFL)] ^ c >> 8L; 
    return c;
  }
  
  private static long crc(byte[] buf, int offset, int len) {
    return update_crc(4294967295L, buf, offset, len) ^ 0xFFFFFFFFL;
  }
  
  private static long crc(byte[] buf) {
    return update_crc(4294967295L, buf, 0, buf.length) ^ 0xFFFFFFFFL;
  }
  
  public void outputInt(int n) throws IOException {
    outputInt(n, this.outp);
  }
  
  public static void outputInt(int n, OutputStream s) throws IOException {
    s.write((byte)(n >> 24));
    s.write((byte)(n >> 16));
    s.write((byte)(n >> 8));
    s.write((byte)n);
  }
  
  public void writeChunk(byte[] chunkType, byte[] data) throws IOException {
    outputInt(data.length);
    this.outp.write(chunkType, 0, 4);
    this.outp.write(data);
    long c = update_crc(4294967295L, chunkType, 0, chunkType.length);
    c = update_crc(c, data, 0, data.length) ^ 0xFFFFFFFFL;
    outputInt((int)c);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\codec\PngWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */