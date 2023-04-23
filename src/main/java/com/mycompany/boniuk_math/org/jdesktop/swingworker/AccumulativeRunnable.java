package com.mycompany.boniuk_math.org.jdesktop.swingworker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

abstract class AccumulativeRunnable<T> implements Runnable {
  private List<T> arguments = null;
  
  protected abstract void run(List<T> paramList);
  
  public final void run() {
    run(flush());
  }
  
  public final synchronized void add(T... paramVarArgs) {
    boolean bool = true;
    if (this.arguments == null) {
      bool = false;
      this.arguments = new ArrayList<T>();
    } 
    Collections.addAll(this.arguments, paramVarArgs);
    if (!bool)
      submit(); 
  }
  
  protected void submit() {
    SwingUtilities.invokeLater(this);
  }
  
  private final synchronized List<T> flush() {
    List<T> list = this.arguments;
    this.arguments = null;
    return list;
  }
}


/* Location:              C:\Users\windo\Desktop\swing-worker-1.1.jar!\org\jdesktop\swingworker\AccumulativeRunnable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */