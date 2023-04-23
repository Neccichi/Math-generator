package com.mycompany.boniuk_math.org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.mycompany.boniuk_math.org.jdesktop.swingworker.SwingWorker;

public class TaskMonitor extends AbstractBean {
  private final PropertyChangeListener applicationPCL;
  
  private final PropertyChangeListener taskServicePCL;
  
  private final PropertyChangeListener taskPCL;
  
  private final LinkedList<Task> taskQueue;
  
  private boolean autoUpdateForegroundTask = true;
  
  private Task foregroundTask = null;
  
  public TaskMonitor(ApplicationContext paramApplicationContext) {
    this.applicationPCL = new ApplicationPCL();
    this.taskServicePCL = new TaskServicePCL();
    this.taskPCL = new TaskPCL();
    this.taskQueue = new LinkedList<Task>();
    paramApplicationContext.addPropertyChangeListener(this.applicationPCL);
    for (TaskService taskService : paramApplicationContext.getTaskServices())
      taskService.addPropertyChangeListener(this.taskServicePCL); 
  }
  
  public void setForegroundTask(Task paramTask) {
    Task task1 = this.foregroundTask;
    if (task1 != null)
      task1.removePropertyChangeListener(this.taskPCL); 
    this.foregroundTask = paramTask;
    Task task2 = this.foregroundTask;
    if (task2 != null)
      task2.addPropertyChangeListener(this.taskPCL); 
    firePropertyChange("foregroundTask", task1, task2);
  }
  
  public Task getForegroundTask() {
    return this.foregroundTask;
  }
  
  public boolean getAutoUpdateForegroundTask() {
    return this.autoUpdateForegroundTask;
  }
  
  public void setAutoUpdateForegroundTask(boolean paramBoolean) {
    boolean bool = this.autoUpdateForegroundTask;
    this.autoUpdateForegroundTask = paramBoolean;
    firePropertyChange("autoUpdateForegroundTask", Boolean.valueOf(bool), Boolean.valueOf(this.autoUpdateForegroundTask));
  }
  
  private List<Task> copyTaskQueue() {
    synchronized (this.taskQueue) {
      if (this.taskQueue.isEmpty())
        return Collections.emptyList(); 
      return new ArrayList<Task>(this.taskQueue);
    } 
  }
  
  public List<Task> getTasks() {
    return copyTaskQueue();
  }
  
  private void updateTasks(List<Task> paramList1, List<Task> paramList2) {
    boolean bool = false;
    List<Task> list = copyTaskQueue();
    for (Task task : paramList1) {
      if (!paramList2.contains(task) && 
        this.taskQueue.remove(task))
        bool = true; 
    } 
    for (Task task : paramList2) {
      if (!this.taskQueue.contains(task)) {
        this.taskQueue.addLast(task);
        bool = true;
      } 
    } 
    Iterator<Task> iterator = this.taskQueue.iterator();
    while (iterator.hasNext()) {
      Task task = iterator.next();
      if (task.isDone()) {
        iterator.remove();
        bool = true;
      } 
    } 
    if (bool) {
      List<Task> list1 = copyTaskQueue();
      firePropertyChange("tasks", list, list1);
    } 
    if (this.autoUpdateForegroundTask && getForegroundTask() == null)
      setForegroundTask(this.taskQueue.isEmpty() ? null : this.taskQueue.getLast()); 
  }
  
  private class ApplicationPCL implements PropertyChangeListener {
    private ApplicationPCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("taskServices".equals(str)) {
        List list1 = (List)param1PropertyChangeEvent.getOldValue();
        List list2 = (List)param1PropertyChangeEvent.getNewValue();
        for (TaskService taskService : list1)
          taskService.removePropertyChangeListener(TaskMonitor.this.taskServicePCL); 
        for (TaskService taskService : list2)
          taskService.addPropertyChangeListener(TaskMonitor.this.taskServicePCL); 
      } 
    }
  }
  
  private class TaskServicePCL implements PropertyChangeListener {
    private TaskServicePCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("tasks".equals(str)) {
        List list1 = (List)param1PropertyChangeEvent.getOldValue();
        List list2 = (List)param1PropertyChangeEvent.getNewValue();
        TaskMonitor.this.updateTasks(list1, list2);
      } 
    }
  }
  
  private class TaskPCL implements PropertyChangeListener {
    private TaskPCL() {}
    
    private void fireStateChange(Task param1Task, String param1String) {
      TaskMonitor.this.firePropertyChange(new PropertyChangeEvent(param1Task, param1String, Boolean.valueOf(false), Boolean.valueOf(true)));
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Task task = (Task)param1PropertyChangeEvent.getSource();
      Object object = param1PropertyChangeEvent.getNewValue();
      if (task != null && task == TaskMonitor.this.getForegroundTask()) {
        TaskMonitor.this.firePropertyChange(param1PropertyChangeEvent);
        if ("state".equals(str)) {
          SwingWorker.StateValue stateValue = (SwingWorker.StateValue)param1PropertyChangeEvent.getNewValue();
          switch (stateValue) {
            case PENDING:
              fireStateChange(task, "pending");
              break;
            case STARTED:
              fireStateChange(task, "started");
              break;
            case DONE:
              fireStateChange(task, "done");
              TaskMonitor.this.setForegroundTask((Task)null);
              break;
          } 
        } 
      } 
    }
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\TaskMonitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */