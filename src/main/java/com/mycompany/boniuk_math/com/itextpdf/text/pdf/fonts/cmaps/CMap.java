package com.mycompany.boniuk_math.com.itextpdf.text.pdf.fonts.cmaps;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMap {
  private List<CodespaceRange> codeSpaceRanges = new ArrayList<CodespaceRange>();
  
  private Map<Integer, String> singleByteMappings = new HashMap<Integer, String>();
  
  private Map<Integer, String> doubleByteMappings = new HashMap<Integer, String>();
  
  public boolean hasOneByteMappings() {
    return !this.singleByteMappings.isEmpty();
  }
  
  public boolean hasTwoByteMappings() {
    return !this.doubleByteMappings.isEmpty();
  }
  
  public String lookup(byte[] code, int offset, int length) {
    String result = null;
    Integer key = null;
    if (length == 1) {
      key = Integer.valueOf(code[offset] & 0xFF);
      result = this.singleByteMappings.get(key);
    } else if (length == 2) {
      int intKey = code[offset] & 0xFF;
      intKey <<= 8;
      intKey += code[offset + 1] & 0xFF;
      key = Integer.valueOf(intKey);
      result = this.doubleByteMappings.get(key);
    } 
    return result;
  }
  
  public void addMapping(byte[] src, String dest) throws IOException {
    if (src.length == 1) {
      this.singleByteMappings.put(Integer.valueOf(src[0] & 0xFF), dest);
    } else if (src.length == 2) {
      int intSrc = src[0] & 0xFF;
      intSrc <<= 8;
      intSrc |= src[1] & 0xFF;
      this.doubleByteMappings.put(Integer.valueOf(intSrc), dest);
    } else {
      throw new IOException(MessageLocalization.getComposedMessage("mapping.code.should.be.1.or.two.bytes.and.not.1", src.length));
    } 
  }
  
  public void addCodespaceRange(CodespaceRange range) {
    this.codeSpaceRanges.add(range);
  }
  
  public List<CodespaceRange> getCodeSpaceRanges() {
    return this.codeSpaceRanges;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\fonts\cmaps\CMap.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */