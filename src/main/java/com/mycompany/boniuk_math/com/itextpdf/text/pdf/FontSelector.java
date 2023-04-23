package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.Font;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.Utilities;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.util.ArrayList;

public class FontSelector {
  protected ArrayList<Font> fonts = new ArrayList<Font>();
  
  public void addFont(Font font) {
    if (font.getBaseFont() != null) {
      this.fonts.add(font);
      return;
    } 
    BaseFont bf = font.getCalculatedBaseFont(true);
    Font f2 = new Font(bf, font.getSize(), font.getCalculatedStyle(), font.getColor());
    this.fonts.add(f2);
  }
  
  public Phrase process(String text) {
    int fsize = this.fonts.size();
    if (fsize == 0)
      throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("no.font.is.defined", new Object[0])); 
    char[] cc = text.toCharArray();
    int len = cc.length;
    StringBuffer sb = new StringBuffer();
    Font font = null;
    int lastidx = -1;
    Phrase ret = new Phrase();
    for (int k = 0; k < len; k++) {
      char c = cc[k];
      if (c == '\n' || c == '\r') {
        sb.append(c);
      } else if (Utilities.isSurrogatePair(cc, k)) {
        int u = Utilities.convertToUtf32(cc, k);
        for (int f = 0; f < fsize; f++) {
          font = this.fonts.get(f);
          if (font.getBaseFont().charExists(u)) {
            if (lastidx != f) {
              if (sb.length() > 0 && lastidx != -1) {
                Chunk ck = new Chunk(sb.toString(), this.fonts.get(lastidx));
                ret.add((Element)ck);
                sb.setLength(0);
              } 
              lastidx = f;
            } 
            sb.append(c);
            sb.append(cc[++k]);
            break;
          } 
        } 
      } else {
        for (int f = 0; f < fsize; f++) {
          font = this.fonts.get(f);
          if (font.getBaseFont().charExists(c)) {
            if (lastidx != f) {
              if (sb.length() > 0 && lastidx != -1) {
                Chunk ck = new Chunk(sb.toString(), this.fonts.get(lastidx));
                ret.add((Element)ck);
                sb.setLength(0);
              } 
              lastidx = f;
            } 
            sb.append(c);
            break;
          } 
        } 
      } 
    } 
    if (sb.length() > 0) {
      Chunk ck = new Chunk(sb.toString(), this.fonts.get((lastidx == -1) ? 0 : lastidx));
      ret.add((Element)ck);
    } 
    return ret;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\FontSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */