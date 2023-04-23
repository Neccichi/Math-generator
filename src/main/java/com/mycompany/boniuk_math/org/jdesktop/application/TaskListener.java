package com.mycompany.boniuk_math.org.jdesktop.application;

import java.util.List;

public interface TaskListener<T, V> {
  void doInBackground(TaskEvent<Void> paramTaskEvent);
  
  void process(TaskEvent<List<V>> paramTaskEvent);
  
  void succeeded(TaskEvent<T> paramTaskEvent);
  
  void failed(TaskEvent<Throwable> paramTaskEvent);
  
  void cancelled(TaskEvent<Void> paramTaskEvent);
  
  void interrupted(TaskEvent<InterruptedException> paramTaskEvent);
  
  void finished(TaskEvent<Void> paramTaskEvent);
  
  public static class Adapter<T, V> implements TaskListener<T, V> {
    public void doInBackground(TaskEvent<Void> param1TaskEvent) {}
    
    public void process(TaskEvent<List<V>> param1TaskEvent) {}
    
    public void succeeded(TaskEvent<T> param1TaskEvent) {}
    
    public void failed(TaskEvent<Throwable> param1TaskEvent) {}
    
    public void cancelled(TaskEvent<Void> param1TaskEvent) {}
    
    public void interrupted(TaskEvent<InterruptedException> param1TaskEvent) {}
    
    public void finished(TaskEvent<Void> param1TaskEvent) {}
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\TaskListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */