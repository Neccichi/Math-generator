package com.mycompany.boniuk_math.org.jdesktop.application;

import java.util.EventObject;

public class TaskEvent<T> extends EventObject {
  private final T value;
  
  public final T getValue() {
    return this.value;
  }
  
  public TaskEvent(Task paramTask, T paramT) {
    super(paramTask);
    this.value = paramT;
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\TaskEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */