package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;

public class ChapterAutoNumber extends Chapter {
  private static final long serialVersionUID = -9217457637987854167L;
  
  protected boolean numberSet = false;
  
  public ChapterAutoNumber(Paragraph para) {
    super(para, 0);
  }
  
  public ChapterAutoNumber(String title) {
    super(title, 0);
  }
  
  public Section addSection(String title) {
    if (isAddedCompletely())
      throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document", new Object[0])); 
    return addSection(title, 2);
  }
  
  public Section addSection(Paragraph title) {
    if (isAddedCompletely())
      throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document", new Object[0])); 
    return addSection(title, 2);
  }
  
  public int setAutomaticNumber(int number) {
    if (!this.numberSet) {
      number++;
      setChapterNumber(number);
      this.numberSet = true;
    } 
    return number;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\ChapterAutoNumber.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */