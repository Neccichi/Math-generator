package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.exceptions.InvalidPdfException;
import java.io.IOException;

public class PRTokeniser {
  public enum TokenType {
    NUMBER, STRING, NAME, COMMENT, START_ARRAY, END_ARRAY, START_DIC, END_DIC, REF, OTHER, ENDOFFILE;
  }
  
  public static final boolean[] delims = new boolean[] { 
      true, true, false, false, false, false, false, false, false, false, 
      true, true, false, true, true, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, true, false, false, false, false, true, false, 
      false, true, true, false, false, false, false, false, true, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, true, false, true, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, true, false, true, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false };
  
  static final String EMPTY = "";
  
  protected RandomAccessFileOrArray file;
  
  protected TokenType type;
  
  protected String stringValue;
  
  protected int reference;
  
  protected int generation;
  
  protected boolean hexString;
  
  public PRTokeniser(String filename) throws IOException {
    this.file = new RandomAccessFileOrArray(filename);
  }
  
  public PRTokeniser(byte[] pdfIn) {
    this.file = new RandomAccessFileOrArray(pdfIn);
  }
  
  public PRTokeniser(RandomAccessFileOrArray file) {
    this.file = file;
  }
  
  public void seek(int pos) throws IOException {
    this.file.seek(pos);
  }
  
  public int getFilePointer() throws IOException {
    return this.file.getFilePointer();
  }
  
  public void close() throws IOException {
    this.file.close();
  }
  
  public int length() throws IOException {
    return this.file.length();
  }
  
  public int read() throws IOException {
    return this.file.read();
  }
  
  public RandomAccessFileOrArray getSafeFile() {
    return new RandomAccessFileOrArray(this.file);
  }
  
  public RandomAccessFileOrArray getFile() {
    return this.file;
  }
  
  public String readString(int size) throws IOException {
    StringBuffer buf = new StringBuffer();
    while (size-- > 0) {
      int ch = this.file.read();
      if (ch == -1)
        break; 
      buf.append((char)ch);
    } 
    return buf.toString();
  }
  
  public static final boolean isWhitespace(int ch) {
    return (ch == 0 || ch == 9 || ch == 10 || ch == 12 || ch == 13 || ch == 32);
  }
  
  public static final boolean isDelimiter(int ch) {
    return (ch == 40 || ch == 41 || ch == 60 || ch == 62 || ch == 91 || ch == 93 || ch == 47 || ch == 37);
  }
  
  public static final boolean isDelimiterWhitespace(int ch) {
    return delims[ch + 1];
  }
  
  public TokenType getTokenType() {
    return this.type;
  }
  
  public String getStringValue() {
    return this.stringValue;
  }
  
  public int getReference() {
    return this.reference;
  }
  
  public int getGeneration() {
    return this.generation;
  }
  
  public void backOnePosition(int ch) {
    if (ch != -1)
      this.file.pushBack((byte)ch); 
  }
  
  public void throwError(String error) throws IOException {
    throw new InvalidPdfException(MessageLocalization.getComposedMessage("1.at.file.pointer.2", new Object[] { error, String.valueOf(this.file.getFilePointer()) }));
  }
  
  public char checkPdfHeader() throws IOException {
    this.file.setStartOffset(0);
    String str = readString(1024);
    int idx = str.indexOf("%PDF-");
    if (idx < 0)
      throw new InvalidPdfException(MessageLocalization.getComposedMessage("pdf.header.not.found", new Object[0])); 
    this.file.setStartOffset(idx);
    return str.charAt(idx + 7);
  }
  
  public void checkFdfHeader() throws IOException {
    this.file.setStartOffset(0);
    String str = readString(1024);
    int idx = str.indexOf("%FDF-");
    if (idx < 0)
      throw new InvalidPdfException(MessageLocalization.getComposedMessage("fdf.header.not.found", new Object[0])); 
    this.file.setStartOffset(idx);
  }
  
  public int getStartxref(int arrLength) throws IOException {
    int fileLength = this.file.length();
    int size = Math.min(arrLength, fileLength);
    int pos = this.file.length() - size;
    this.file.seek(pos);
    String str = readString(arrLength);
    int idx = str.lastIndexOf("startxref");
    if (idx < 0 && size == fileLength)
      throw new InvalidPdfException(MessageLocalization.getComposedMessage("pdf.startxref.not.found", new Object[0])); 
    if (idx < 0)
      return getStartxref(arrLength + 1024); 
    return pos + idx;
  }
  
  public static int getHex(int v) {
    if (v >= 48 && v <= 57)
      return v - 48; 
    if (v >= 65 && v <= 70)
      return v - 65 + 10; 
    if (v >= 97 && v <= 102)
      return v - 97 + 10; 
    return -1;
  }
  
  public void nextValidToken() throws IOException {
    int level = 0;
    String n1 = null;
    String n2 = null;
    int ptr = 0;
    while (nextToken()) {
      if (this.type == TokenType.COMMENT)
        continue; 
      switch (level) {
        case 0:
          if (this.type != TokenType.NUMBER)
            return; 
          ptr = this.file.getFilePointer();
          n1 = this.stringValue;
          level++;
          continue;
        case 1:
          if (this.type != TokenType.NUMBER) {
            this.file.seek(ptr);
            this.type = TokenType.NUMBER;
            this.stringValue = n1;
            return;
          } 
          n2 = this.stringValue;
          level++;
          continue;
      } 
      if (this.type != TokenType.OTHER || !this.stringValue.equals("R")) {
        this.file.seek(ptr);
        this.type = TokenType.NUMBER;
        this.stringValue = n1;
        return;
      } 
      this.type = TokenType.REF;
      this.reference = Integer.parseInt(n1);
      this.generation = Integer.parseInt(n2);
      return;
    } 
  }
  
  public boolean nextToken() throws IOException {
    int v1, nesting, v2, ch = 0;
    do {
      ch = this.file.read();
    } while (ch != -1 && isWhitespace(ch));
    if (ch == -1) {
      this.type = TokenType.ENDOFFILE;
      return false;
    } 
    StringBuffer outBuf = null;
    this.stringValue = "";
    switch (ch) {
      case 91:
        this.type = TokenType.START_ARRAY;
        break;
      case 93:
        this.type = TokenType.END_ARRAY;
        break;
      case 47:
        outBuf = new StringBuffer();
        this.type = TokenType.NAME;
        while (true) {
          ch = this.file.read();
          if (delims[ch + 1])
            break; 
          if (ch == 35)
            ch = (getHex(this.file.read()) << 4) + getHex(this.file.read()); 
          outBuf.append((char)ch);
        } 
        backOnePosition(ch);
        break;
      case 62:
        ch = this.file.read();
        if (ch != 62)
          throwError(MessageLocalization.getComposedMessage("greaterthan.not.expected", new Object[0])); 
        this.type = TokenType.END_DIC;
        break;
      case 60:
        v1 = this.file.read();
        if (v1 == 60) {
          this.type = TokenType.START_DIC;
          break;
        } 
        outBuf = new StringBuffer();
        this.type = TokenType.STRING;
        this.hexString = true;
        v2 = 0;
        while (true) {
          while (isWhitespace(v1))
            v1 = this.file.read(); 
          if (v1 == 62)
            break; 
          v1 = getHex(v1);
          if (v1 < 0)
            break; 
          v2 = this.file.read();
          while (isWhitespace(v2))
            v2 = this.file.read(); 
          if (v2 == 62) {
            ch = v1 << 4;
            outBuf.append((char)ch);
            break;
          } 
          v2 = getHex(v2);
          if (v2 < 0)
            break; 
          ch = (v1 << 4) + v2;
          outBuf.append((char)ch);
          v1 = this.file.read();
        } 
        if (v1 < 0 || v2 < 0)
          throwError(MessageLocalization.getComposedMessage("error.reading.string", new Object[0])); 
        break;
      case 37:
        this.type = TokenType.COMMENT;
        do {
          ch = this.file.read();
        } while (ch != -1 && ch != 13 && ch != 10);
        break;
      case 40:
        outBuf = new StringBuffer();
        this.type = TokenType.STRING;
        this.hexString = false;
        nesting = 0;
        while (true) {
          ch = this.file.read();
          if (ch == -1)
            break; 
          if (ch == 40) {
            nesting++;
          } else if (ch == 41) {
            nesting--;
          } else if (ch == 92) {
            int octal;
            boolean lineBreak = false;
            ch = this.file.read();
            switch (ch) {
              case 110:
                ch = 10;
                break;
              case 114:
                ch = 13;
                break;
              case 116:
                ch = 9;
                break;
              case 98:
                ch = 8;
                break;
              case 102:
                ch = 12;
                break;
              case 40:
              case 41:
              case 92:
                break;
              case 13:
                lineBreak = true;
                ch = this.file.read();
                if (ch != 10)
                  backOnePosition(ch); 
                break;
              case 10:
                lineBreak = true;
                break;
              default:
                if (ch < 48 || ch > 55)
                  break; 
                octal = ch - 48;
                ch = this.file.read();
                if (ch < 48 || ch > 55) {
                  backOnePosition(ch);
                  ch = octal;
                  break;
                } 
                octal = (octal << 3) + ch - 48;
                ch = this.file.read();
                if (ch < 48 || ch > 55) {
                  backOnePosition(ch);
                  ch = octal;
                  break;
                } 
                octal = (octal << 3) + ch - 48;
                ch = octal & 0xFF;
                break;
            } 
            if (lineBreak)
              continue; 
            if (ch < 0)
              break; 
          } else if (ch == 13) {
            ch = this.file.read();
            if (ch < 0)
              break; 
            if (ch != 10) {
              backOnePosition(ch);
              ch = 10;
            } 
          } 
          if (nesting == -1)
            break; 
          outBuf.append((char)ch);
        } 
        if (ch == -1)
          throwError(MessageLocalization.getComposedMessage("error.reading.string", new Object[0])); 
        break;
      default:
        outBuf = new StringBuffer();
        if (ch == 45 || ch == 43 || ch == 46 || (ch >= 48 && ch <= 57)) {
          this.type = TokenType.NUMBER;
          do {
            outBuf.append((char)ch);
            ch = this.file.read();
          } while (ch != -1 && ((ch >= 48 && ch <= 57) || ch == 46));
        } else {
          this.type = TokenType.OTHER;
          do {
            outBuf.append((char)ch);
            ch = this.file.read();
          } while (!delims[ch + 1]);
        } 
        backOnePosition(ch);
        break;
    } 
    if (outBuf != null)
      this.stringValue = outBuf.toString(); 
    return true;
  }
  
  public int intValue() {
    return Integer.parseInt(this.stringValue);
  }
  
  public boolean readLineSegment(byte[] input) throws IOException {
    int c = -1;
    boolean eol = false;
    int ptr = 0;
    int len = input.length;
    if (ptr < len)
      while (isWhitespace(c = read())); 
    while (!eol && ptr < len) {
      int cur;
      switch (c) {
        case -1:
        case 10:
          eol = true;
          break;
        case 13:
          eol = true;
          cur = getFilePointer();
          if (read() != 10)
            seek(cur); 
          break;
        default:
          input[ptr++] = (byte)c;
          break;
      } 
      if (eol || len <= ptr)
        break; 
      c = read();
    } 
    if (ptr >= len) {
      eol = false;
      while (!eol) {
        int cur;
        switch (c = read()) {
          case -1:
          case 10:
            eol = true;
          case 13:
            eol = true;
            cur = getFilePointer();
            if (read() != 10)
              seek(cur); 
        } 
      } 
    } 
    if (c == -1 && ptr == 0)
      return false; 
    if (ptr + 2 <= len) {
      input[ptr++] = 32;
      input[ptr] = 88;
    } 
    return true;
  }
  
  public static int[] checkObjectStart(byte[] line) {
    try {
      PRTokeniser tk = new PRTokeniser(line);
      int num = 0;
      int gen = 0;
      if (!tk.nextToken() || tk.getTokenType() != TokenType.NUMBER)
        return null; 
      num = tk.intValue();
      if (!tk.nextToken() || tk.getTokenType() != TokenType.NUMBER)
        return null; 
      gen = tk.intValue();
      if (!tk.nextToken())
        return null; 
      if (!tk.getStringValue().equals("obj"))
        return null; 
      return new int[] { num, gen };
    } catch (Exception ioe) {
      return null;
    } 
  }
  
  public boolean isHexString() {
    return this.hexString;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PRTokeniser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */