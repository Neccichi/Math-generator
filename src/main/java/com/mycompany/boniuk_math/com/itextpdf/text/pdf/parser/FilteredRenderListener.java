package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

public class FilteredRenderListener implements RenderListener {
  private final RenderListener delegate;
  
  private final RenderFilter[] filters;
  
  public FilteredRenderListener(RenderListener delegate, RenderFilter... filters) {
    this.delegate = delegate;
    this.filters = filters;
  }
  
  public void renderText(TextRenderInfo renderInfo) {
    for (RenderFilter filter : this.filters) {
      if (!filter.allowText(renderInfo))
        return; 
    } 
    this.delegate.renderText(renderInfo);
  }
  
  public void beginTextBlock() {
    this.delegate.beginTextBlock();
  }
  
  public void endTextBlock() {
    this.delegate.endTextBlock();
  }
  
  public void renderImage(ImageRenderInfo renderInfo) {
    for (RenderFilter filter : this.filters) {
      if (!filter.allowImage(renderInfo))
        return; 
    } 
    this.delegate.renderImage(renderInfo);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\FilteredRenderListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */