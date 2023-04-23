package com.mycompany.boniuk_math.org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

public class TaskService extends AbstractBean {
  private final String name;
  
  private final ExecutorService executorService;
  
  private final List<Task> tasks;
  
  private final PropertyChangeListener taskPCL;
  
  public TaskService(String paramString, ExecutorService paramExecutorService) {
    if (paramString == null)
      throw new IllegalArgumentException("null name"); 
    if (paramExecutorService == null)
      throw new IllegalArgumentException("null executorService"); 
    this.name = paramString;
    this.executorService = paramExecutorService;
    this.tasks = new ArrayList<Task>();
    this.taskPCL = new TaskPCL();
  }
  
  public TaskService(String paramString) {
    this(paramString, new ThreadPoolExecutor(3, 10, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
  }
  
  public final String getName() {
    return this.name;
  }
  
  private List<Task> copyTasksList() {
    synchronized (this.tasks) {
      if (this.tasks.isEmpty())
        return Collections.emptyList(); 
      return new ArrayList<Task>(this.tasks);
    } 
  }
  
  private class TaskPCL implements PropertyChangeListener {
    private TaskPCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("done".equals(str)) {
        Task task = (Task)param1PropertyChangeEvent.getSource();
        if (task.isDone()) {
          List list1, list2;
          synchronized (TaskService.this.tasks) {
            list1 = TaskService.this.copyTasksList();
            TaskService.this.tasks.remove(task);
            task.removePropertyChangeListener(TaskService.this.taskPCL);
            list2 = TaskService.this.copyTasksList();
          } 
          TaskService.this.firePropertyChange("tasks", list1, list2);
          Task.InputBlocker inputBlocker = task.getInputBlocker();
          if (inputBlocker != null)
            inputBlocker.unblock(); 
        } 
      } 
    }
  }
  
  private void maybeBlockTask(Task paramTask) {
    final Task.InputBlocker inputBlocker = paramTask.getInputBlocker();
    if (inputBlocker == null)
      return; 
    if (inputBlocker.getScope() != Task.BlockingScope.NONE)
      if (SwingUtilities.isEventDispatchThread()) {
        inputBlocker.block();
      } else {
        Runnable runnable = new Runnable() {
            public void run() {
              inputBlocker.block();
            }
          };
        SwingUtilities.invokeLater(runnable);
      }  
  }
  
  public void execute(Task paramTask) {
    List<Task> list1, list2;
    if (paramTask == null)
      throw new IllegalArgumentException("null task"); 
    if (!paramTask.isPending() || paramTask.getTaskService() != null)
      throw new IllegalArgumentException("task has already been executed"); 
    paramTask.setTaskService(this);
    synchronized (this.tasks) {
      list1 = copyTasksList();
      this.tasks.add(paramTask);
      list2 = copyTasksList();
      paramTask.addPropertyChangeListener(this.taskPCL);
    } 
    firePropertyChange("tasks", list1, list2);
    maybeBlockTask(paramTask);
    this.executorService.execute((Runnable)paramTask);
  }
  
  public List<Task> getTasks() {
    return copyTasksList();
  }
  
  public final void shutdown() {
    this.executorService.shutdown();
  }
  
  public final List<Runnable> shutdownNow() {
    return this.executorService.shutdownNow();
  }
  
  public final boolean isShutdown() {
    return this.executorService.isShutdown();
  }
  
  public final boolean isTerminated() {
    return this.executorService.isTerminated();
  }
  
  public final boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    return this.executorService.awaitTermination(paramLong, paramTimeUnit);
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\TaskService.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */