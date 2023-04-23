package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

public class FilteredTextRenderListener extends FilteredRenderListener implements TextExtractionStrategy {
  private final TextExtractionStrategy delegate;
  
  public FilteredTextRenderListener(TextExtractionStrategy delegate, RenderFilter... filters) {
    super(delegate, filters);
    this.delegate = delegate;
  }
  
  public String getResultantText() {
    return this.delegate.getResultantText();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\FilteredTextRenderListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */