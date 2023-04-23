package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class RandomAccessFileOrArray implements DataInput {
  MappedRandomAccessFile rf;
  
  RandomAccessFile trf;
  
  boolean plainRandomAccess;
  
  String filename;
  
  byte[] arrayIn;
  
  int arrayInPtr;
  
  byte back;
  
  boolean isBack = false;
  
  private int startOffset = 0;
  
  public RandomAccessFileOrArray(String filename) throws IOException {
    this(filename, false, Document.plainRandomAccess);
  }
  
  public RandomAccessFileOrArray(String filename, boolean forceRead, boolean plainRandomAccess) throws IOException {
    this.plainRandomAccess = plainRandomAccess;
    File file = new File(filename);
    if (!file.canRead()) {
      if (filename.startsWith("file:/") || filename.startsWith("http://") || filename.startsWith("https://") || filename.startsWith("jar:") || filename.startsWith("wsjar:")) {
        InputStream inputStream = (new URL(filename)).openStream();
        try {
          this.arrayIn = InputStreamToArray(inputStream);
          return;
        } finally {
          try {
            inputStream.close();
          } catch (IOException ioe) {}
        } 
      } 
      InputStream is = BaseFont.getResourceStream(filename);
      if (is == null)
        throw new IOException(MessageLocalization.getComposedMessage("1.not.found.as.file.or.resource", new Object[] { filename })); 
      try {
        this.arrayIn = InputStreamToArray(is);
        return;
      } finally {
        try {
          is.close();
        } catch (IOException ioe) {}
      } 
    } 
    if (forceRead) {
      InputStream s = null;
      try {
        s = new FileInputStream(file);
        this.arrayIn = InputStreamToArray(s);
      } finally {
        try {
          if (s != null)
            s.close(); 
        } catch (Exception e) {}
      } 
      return;
    } 
    this.filename = filename;
    if (plainRandomAccess) {
      this.trf = new RandomAccessFile(filename, "r");
    } else {
      try {
        this.rf = new MappedRandomAccessFile(filename, "r");
      } catch (IOException e) {
        if (exceptionIsMapFailureException(e)) {
          this.plainRandomAccess = true;
          this.trf = new RandomAccessFile(filename, "r");
        } else {
          throw e;
        } 
      } 
    } 
  }
  
  private static boolean exceptionIsMapFailureException(IOException e) {
    if (e.getMessage().indexOf("Map failed") >= 0)
      return true; 
    return false;
  }
  
  public RandomAccessFileOrArray(URL url) throws IOException {
    InputStream is = url.openStream();
    try {
      this.arrayIn = InputStreamToArray(is);
    } finally {
      try {
        is.close();
      } catch (IOException ioe) {}
    } 
  }
  
  public RandomAccessFileOrArray(InputStream is) throws IOException {
    this.arrayIn = InputStreamToArray(is);
  }
  
  public static byte[] InputStreamToArray(InputStream is) throws IOException {
    byte[] b = new byte[8192];
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    while (true) {
      int read = is.read(b);
      if (read < 1)
        break; 
      out.write(b, 0, read);
    } 
    out.close();
    return out.toByteArray();
  }
  
  public RandomAccessFileOrArray(byte[] arrayIn) {
    this.arrayIn = arrayIn;
  }
  
  public RandomAccessFileOrArray(RandomAccessFileOrArray file) {
    this.filename = file.filename;
    this.arrayIn = file.arrayIn;
    this.startOffset = file.startOffset;
    this.plainRandomAccess = file.plainRandomAccess;
  }
  
  public void pushBack(byte b) {
    this.back = b;
    this.isBack = true;
  }
  
  public int read() throws IOException {
    if (this.isBack) {
      this.isBack = false;
      return this.back & 0xFF;
    } 
    if (this.arrayIn == null)
      return this.plainRandomAccess ? this.trf.read() : this.rf.read(); 
    if (this.arrayInPtr >= this.arrayIn.length)
      return -1; 
    return this.arrayIn[this.arrayInPtr++] & 0xFF;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    if (len == 0)
      return 0; 
    int n = 0;
    if (this.isBack) {
      this.isBack = false;
      if (len == 1) {
        b[off] = this.back;
        return 1;
      } 
      n = 1;
      b[off++] = this.back;
      len--;
    } 
    if (this.arrayIn == null)
      return (this.plainRandomAccess ? this.trf.read(b, off, len) : this.rf.read(b, off, len)) + n; 
    if (this.arrayInPtr >= this.arrayIn.length)
      return -1; 
    if (this.arrayInPtr + len > this.arrayIn.length)
      len = this.arrayIn.length - this.arrayInPtr; 
    System.arraycopy(this.arrayIn, this.arrayInPtr, b, off, len);
    this.arrayInPtr += len;
    return len + n;
  }
  
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }
  
  public void readFully(byte[] b) throws IOException {
    readFully(b, 0, b.length);
  }
  
  public void readFully(byte[] b, int off, int len) throws IOException {
    int n = 0;
    do {
      int count = read(b, off + n, len - n);
      if (count < 0)
        throw new EOFException(); 
      n += count;
    } while (n < len);
  }
  
  public long skip(long n) throws IOException {
    return skipBytes((int)n);
  }
  
  public int skipBytes(int n) throws IOException {
    if (n <= 0)
      return 0; 
    int adj = 0;
    if (this.isBack) {
      this.isBack = false;
      if (n == 1)
        return 1; 
      n--;
      adj = 1;
    } 
    int pos = getFilePointer();
    int len = length();
    int newpos = pos + n;
    if (newpos > len)
      newpos = len; 
    seek(newpos);
    return newpos - pos + adj;
  }
  
  public void reOpen() throws IOException {
    if (this.filename != null && this.rf == null && this.trf == null)
      if (this.plainRandomAccess) {
        this.trf = new RandomAccessFile(this.filename, "r");
      } else {
        this.rf = new MappedRandomAccessFile(this.filename, "r");
      }  
    seek(0);
  }
  
  protected void insureOpen() throws IOException {
    if (this.filename != null && this.rf == null && this.trf == null)
      reOpen(); 
  }
  
  public boolean isOpen() {
    return (this.filename == null || this.rf != null || this.trf != null);
  }
  
  public void close() throws IOException {
    this.isBack = false;
    if (this.rf != null) {
      this.rf.close();
      this.rf = null;
      this.plainRandomAccess = true;
    } else if (this.trf != null) {
      this.trf.close();
      this.trf = null;
    } 
  }
  
  public int length() throws IOException {
    if (this.arrayIn == null) {
      insureOpen();
      return (int)(this.plainRandomAccess ? this.trf.length() : this.rf.length()) - this.startOffset;
    } 
    return this.arrayIn.length - this.startOffset;
  }
  
  public void seek(int pos) throws IOException {
    pos += this.startOffset;
    this.isBack = false;
    if (this.arrayIn == null) {
      insureOpen();
      if (this.plainRandomAccess) {
        this.trf.seek(pos);
      } else {
        this.rf.seek(pos);
      } 
    } else {
      this.arrayInPtr = pos;
    } 
  }
  
  public void seek(long pos) throws IOException {
    seek((int)pos);
  }
  
  public int getFilePointer() throws IOException {
    insureOpen();
    int n = this.isBack ? 1 : 0;
    if (this.arrayIn == null)
      return (int)(this.plainRandomAccess ? this.trf.getFilePointer() : this.rf.getFilePointer()) - n - this.startOffset; 
    return this.arrayInPtr - n - this.startOffset;
  }
  
  public boolean readBoolean() throws IOException {
    int ch = read();
    if (ch < 0)
      throw new EOFException(); 
    return (ch != 0);
  }
  
  public byte readByte() throws IOException {
    int ch = read();
    if (ch < 0)
      throw new EOFException(); 
    return (byte)ch;
  }
  
  public int readUnsignedByte() throws IOException {
    int ch = read();
    if (ch < 0)
      throw new EOFException(); 
    return ch;
  }
  
  public short readShort() throws IOException {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException(); 
    return (short)((ch1 << 8) + ch2);
  }
  
  public final short readShortLE() throws IOException {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException(); 
    return (short)((ch2 << 8) + (ch1 << 0));
  }
  
  public int readUnsignedShort() throws IOException {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException(); 
    return (ch1 << 8) + ch2;
  }
  
  public final int readUnsignedShortLE() throws IOException {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException(); 
    return (ch2 << 8) + (ch1 << 0);
  }
  
  public char readChar() throws IOException {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException(); 
    return (char)((ch1 << 8) + ch2);
  }
  
  public final char readCharLE() throws IOException {
    int ch1 = read();
    int ch2 = read();
    if ((ch1 | ch2) < 0)
      throw new EOFException(); 
    return (char)((ch2 << 8) + (ch1 << 0));
  }
  
  public int readInt() throws IOException {
    int ch1 = read();
    int ch2 = read();
    int ch3 = read();
    int ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException(); 
    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
  }
  
  public final int readIntLE() throws IOException {
    int ch1 = read();
    int ch2 = read();
    int ch3 = read();
    int ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException(); 
    return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
  }
  
  public final long readUnsignedInt() throws IOException {
    long ch1 = read();
    long ch2 = read();
    long ch3 = read();
    long ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0L)
      throw new EOFException(); 
    return (ch1 << 24L) + (ch2 << 16L) + (ch3 << 8L) + (ch4 << 0L);
  }
  
  public final long readUnsignedIntLE() throws IOException {
    long ch1 = read();
    long ch2 = read();
    long ch3 = read();
    long ch4 = read();
    if ((ch1 | ch2 | ch3 | ch4) < 0L)
      throw new EOFException(); 
    return (ch4 << 24L) + (ch3 << 16L) + (ch2 << 8L) + (ch1 << 0L);
  }
  
  public long readLong() throws IOException {
    return (readInt() << 32L) + (readInt() & 0xFFFFFFFFL);
  }
  
  public final long readLongLE() throws IOException {
    int i1 = readIntLE();
    int i2 = readIntLE();
    return (i2 << 32L) + (i1 & 0xFFFFFFFFL);
  }
  
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }
  
  public final float readFloatLE() throws IOException {
    return Float.intBitsToFloat(readIntLE());
  }
  
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }
  
  public final double readDoubleLE() throws IOException {
    return Double.longBitsToDouble(readLongLE());
  }
  
  public String readLine() throws IOException {
    StringBuffer input = new StringBuffer();
    int c = -1;
    boolean eol = false;
    while (!eol) {
      int cur;
      switch (c = read()) {
        case -1:
        case 10:
          eol = true;
          continue;
        case 13:
          eol = true;
          cur = getFilePointer();
          if (read() != 10)
            seek(cur); 
          continue;
      } 
      input.append((char)c);
    } 
    if (c == -1 && input.length() == 0)
      return null; 
    return input.toString();
  }
  
  public String readUTF() throws IOException {
    return DataInputStream.readUTF(this);
  }
  
  public int getStartOffset() {
    return this.startOffset;
  }
  
  public void setStartOffset(int startOffset) {
    this.startOffset = startOffset;
  }
  
  public ByteBuffer getNioByteBuffer() throws IOException {
    if (this.filename != null) {
      FileChannel channel;
      if (this.plainRandomAccess) {
        channel = this.trf.getChannel();
      } else {
        channel = this.rf.getChannel();
      } 
      return channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
    } 
    return ByteBuffer.wrap(this.arrayIn);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\RandomAccessFileOrArray.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */