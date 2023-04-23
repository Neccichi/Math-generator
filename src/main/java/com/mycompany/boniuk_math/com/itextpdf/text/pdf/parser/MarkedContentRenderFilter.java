package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

public class MarkedContentRenderFilter extends RenderFilter {
  private int mcid;
  
  public MarkedContentRenderFilter(int mcid) {
    this.mcid = mcid;
  }
  
  public boolean allowText(TextRenderInfo renderInfo) {
    return renderInfo.hasMcid(this.mcid);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\MarkedContentRenderFilter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */