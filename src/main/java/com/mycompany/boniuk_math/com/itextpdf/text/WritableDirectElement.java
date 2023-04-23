package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.api.WriterOperation;
import java.util.ArrayList;
import java.util.List;

public abstract class WritableDirectElement implements Element, WriterOperation {
  public boolean process(ElementListener listener) {
    throw new UnsupportedOperationException();
  }
  
  public int type() {
    return 666;
  }
  
  public boolean isContent() {
    return false;
  }
  
  public boolean isNestable() {
    throw new UnsupportedOperationException();
  }
  
  public List<Chunk> getChunks() {
    return new ArrayList<Chunk>(0);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\WritableDirectElement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */