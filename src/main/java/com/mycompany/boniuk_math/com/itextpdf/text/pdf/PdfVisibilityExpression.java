package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;

public class PdfVisibilityExpression extends PdfArray {
  public static final int OR = 0;
  
  public static final int AND = 1;
  
  public static final int NOT = -1;
  
  public PdfVisibilityExpression(int type) {
    switch (type) {
      case 0:
        super.add(PdfName.OR);
        return;
      case 1:
        super.add(PdfName.AND);
        return;
      case -1:
        super.add(PdfName.NOT);
        return;
    } 
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.ve.value", new Object[0]));
  }
  
  public void add(int index, PdfObject element) {
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.ve.value", new Object[0]));
  }
  
  public boolean add(PdfObject object) {
    if (object instanceof PdfLayer)
      return super.add(((PdfLayer)object).getRef()); 
    if (object instanceof PdfVisibilityExpression)
      return super.add(object); 
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.ve.value", new Object[0]));
  }
  
  public void addFirst(PdfObject object) {
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.ve.value", new Object[0]));
  }
  
  public boolean add(float[] values) {
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.ve.value", new Object[0]));
  }
  
  public boolean add(int[] values) {
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.ve.value", new Object[0]));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfVisibilityExpression.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */