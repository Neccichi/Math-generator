package com.mycompany.boniuk_math.com.itextpdf.text.pdf.fonts.cmaps;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMapParser {
  private static final String BEGIN_CODESPACE_RANGE = "begincodespacerange";
  
  private static final String BEGIN_BASE_FONT_CHAR = "beginbfchar";
  
  private static final String BEGIN_BASE_FONT_RANGE = "beginbfrange";
  
  private static final String MARK_END_OF_DICTIONARY = ">>";
  
  private static final String MARK_END_OF_ARRAY = "]";
  
  private byte[] tokenParserByteBuffer = new byte[512];
  
  public CMap parse(InputStream input) throws IOException {
    PushbackInputStream cmapStream = new PushbackInputStream(input);
    CMap result = new CMap();
    Object token = null;
    label58: while ((token = parseNextToken(cmapStream)) != null) {
      if (token instanceof Operator) {
        Operator op = (Operator)token;
        if (op.op.equals("begincodespacerange"))
          while (true) {
            Object nx = parseNextToken(cmapStream);
            if (nx instanceof Operator && ((Operator)nx).op.equals("endcodespacerange"))
              continue label58; 
            byte[] startRange = (byte[])nx;
            byte[] endRange = (byte[])parseNextToken(cmapStream);
            CodespaceRange range = new CodespaceRange();
            range.setStart(startRange);
            range.setEnd(endRange);
            result.addCodespaceRange(range);
          }  
        if (op.op.equals("beginbfchar")) {
          Object nextToken;
          while (true) {
            Object nx = parseNextToken(cmapStream);
            if (nx instanceof Operator && ((Operator)nx).op.equals("endbfchar"))
              continue label58; 
            byte[] inputCode = (byte[])nx;
            nextToken = parseNextToken(cmapStream);
            if (nextToken instanceof byte[]) {
              byte[] bytes = (byte[])nextToken;
              String value = createStringFromBytes(bytes);
              result.addMapping(inputCode, value);
              continue;
            } 
            if (nextToken instanceof LiteralName) {
              result.addMapping(inputCode, ((LiteralName)nextToken).name);
              continue;
            } 
            break;
          } 
          throw new IOException(MessageLocalization.getComposedMessage("error.parsing.cmap.beginbfchar.expected.cosstring.or.cosname.and.not.1", new Object[] { nextToken }));
        } 
        if (op.op.equals("beginbfrange"))
          while (true) {
            Object nx = parseNextToken(cmapStream);
            if (nx instanceof Operator && ((Operator)nx).op.equals("endbfrange"))
              continue label58; 
            byte[] startCode = (byte[])nx;
            byte[] endCode = (byte[])parseNextToken(cmapStream);
            Object nextToken = parseNextToken(cmapStream);
            List<byte[]> array = null;
            byte[] tokenBytes = null;
            if (nextToken instanceof List) {
              array = (List<byte[]>)nextToken;
              tokenBytes = array.get(0);
            } else {
              tokenBytes = (byte[])nextToken;
            } 
            String value = null;
            int arrayIndex = 0;
            boolean done = false;
            while (!done) {
              if (compare(startCode, endCode) >= 0)
                done = true; 
              value = createStringFromBytes(tokenBytes);
              result.addMapping(startCode, value);
              increment(startCode);
              if (array == null) {
                increment(tokenBytes);
                continue;
              } 
              arrayIndex++;
              if (arrayIndex < array.size())
                tokenBytes = array.get(arrayIndex); 
            } 
          }  
      } 
    } 
    return result;
  }
  
  private Object parseNextToken(PushbackInputStream is) throws IOException {
    StringBuffer stringBuffer1;
    int secondCloseBrace;
    List<Object> list;
    int theNextByte, i;
    Object nextToken;
    int stringByte;
    String value;
    Object<Object> retval = null;
    int nextByte = is.read();
    while (nextByte == 9 || nextByte == 32 || nextByte == 13 || nextByte == 10)
      nextByte = is.read(); 
    switch (nextByte) {
      case 37:
        stringBuffer1 = new StringBuffer();
        stringBuffer1.append((char)nextByte);
        readUntilEndOfLine(is, stringBuffer1);
        retval = (Object<Object>)stringBuffer1.toString();
      case 40:
        stringBuffer1 = new StringBuffer();
        i = is.read();
        while (i != -1 && i != 41) {
          stringBuffer1.append((char)i);
          i = is.read();
        } 
        retval = (Object<Object>)stringBuffer1.toString();
      case 62:
        secondCloseBrace = is.read();
        if (secondCloseBrace == 62) {
          retval = (Object<Object>)">>";
        } else {
          throw new IOException(MessageLocalization.getComposedMessage("error.expected.the.end.of.a.dictionary", new Object[0]));
        } 
      case 93:
        retval = (Object<Object>)"]";
      case 91:
        list = new ArrayList();
        nextToken = parseNextToken(is);
        while (nextToken != "]") {
          list.add(nextToken);
          nextToken = parseNextToken(is);
        } 
        retval = (Object<Object>)list;
      case 60:
        theNextByte = is.read();
        if (theNextByte == 60) {
          Map<String, Object> result = new HashMap<String, Object>();
          Object key = parseNextToken(is);
          while (key instanceof LiteralName && key != ">>") {
            Object object = parseNextToken(is);
            result.put(((LiteralName)key).name, object);
            key = parseNextToken(is);
          } 
          Map<String, Object> map1 = result;
        } else {
          int multiplyer = 16;
          int bufferIndex = -1;
          while (theNextByte != -1 && theNextByte != 62) {
            int intValue = 0;
            if (theNextByte >= 48 && theNextByte <= 57) {
              intValue = theNextByte - 48;
            } else if (theNextByte >= 65 && theNextByte <= 70) {
              intValue = 10 + theNextByte - 65;
            } else if (theNextByte >= 97 && theNextByte <= 102) {
              intValue = 10 + theNextByte - 97;
            } else {
              if (theNextByte == 32 || theNextByte == 9) {
                theNextByte = is.read();
                continue;
              } 
              throw new IOException(MessageLocalization.getComposedMessage("error.expected.hex.character.and.not.char.thenextbyte.1", theNextByte));
            } 
            intValue *= multiplyer;
            if (multiplyer == 16) {
              bufferIndex++;
              this.tokenParserByteBuffer[bufferIndex] = 0;
              multiplyer = 1;
            } else {
              multiplyer = 16;
            } 
            this.tokenParserByteBuffer[bufferIndex] = (byte)(this.tokenParserByteBuffer[bufferIndex] + intValue);
            theNextByte = is.read();
          } 
          byte[] finalResult = new byte[bufferIndex + 1];
          System.arraycopy(this.tokenParserByteBuffer, 0, finalResult, 0, bufferIndex + 1);
          byte[] arrayOfByte1 = finalResult;
        } 
      case 47:
        buffer = new StringBuffer();
        stringByte = is.read();
        while (!isWhitespaceOrEOF(stringByte)) {
          buffer.append((char)stringByte);
          stringByte = is.read();
        } 
        retval = (Object<Object>)new LiteralName(buffer.toString());
      case -1:
        return retval;
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        buffer = new StringBuffer();
        buffer.append((char)nextByte);
        nextByte = is.read();
        while (!isWhitespaceOrEOF(nextByte) && (Character.isDigit((char)nextByte) || nextByte == 46)) {
          buffer.append((char)nextByte);
          nextByte = is.read();
        } 
        is.unread(nextByte);
        value = buffer.toString();
        if (value.indexOf('.') >= 0) {
          retval = (Object<Object>)new Double(value);
        } else {
          retval = (Object<Object>)Integer.valueOf(buffer.toString());
        } 
    } 
    StringBuffer buffer = new StringBuffer();
    buffer.append((char)nextByte);
    nextByte = is.read();
    while (!isWhitespaceOrEOF(nextByte)) {
      buffer.append((char)nextByte);
      nextByte = is.read();
    } 
    retval = (Object<Object>)new Operator(buffer.toString());
  }
  
  private void readUntilEndOfLine(InputStream is, StringBuffer buf) throws IOException {
    int nextByte = is.read();
    while (nextByte != -1 && nextByte != 13 && nextByte != 10) {
      buf.append((char)nextByte);
      nextByte = is.read();
    } 
  }
  
  private boolean isWhitespaceOrEOF(int aByte) {
    return (aByte == -1 || aByte == 32 || aByte == 13 || aByte == 10);
  }
  
  private void increment(byte[] data) {
    increment(data, data.length - 1);
  }
  
  private void increment(byte[] data, int position) {
    if (position > 0 && (data[position] + 256) % 256 == 255) {
      data[position] = 0;
      increment(data, position - 1);
    } else {
      data[position] = (byte)(data[position] + 1);
    } 
  }
  
  private String createStringFromBytes(byte[] bytes) throws IOException {
    String retval = null;
    if (bytes.length == 1) {
      retval = new String(bytes);
    } else {
      retval = new String(bytes, "UTF-16BE");
    } 
    return retval;
  }
  
  private int compare(byte[] first, byte[] second) {
    int retval = 1;
    boolean done = false;
    for (int i = 0; i < first.length && !done; i++) {
      if (first[i] != second[i])
        if ((first[i] + 256) % 256 < (second[i] + 256) % 256) {
          done = true;
          retval = -1;
        } else {
          done = true;
          retval = 1;
        }  
    } 
    return retval;
  }
  
  private class LiteralName {
    private String name;
    
    private LiteralName(String theName) {
      this.name = theName;
    }
  }
  
  private class Operator {
    private String op;
    
    private Operator(String theOp) {
      this.op = theOp;
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\fonts\cmaps\CMapParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */