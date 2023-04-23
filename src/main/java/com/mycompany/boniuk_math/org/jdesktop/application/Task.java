package com.mycompany.boniuk_math.org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mycompany.boniuk_math.org.jdesktop.swingworker.SwingWorker;

public abstract class Task<T, V> extends SwingWorker<T, V> {
  private static final Logger logger = Logger.getLogger(Task.class.getName());
  
  private final Application application;
  
  private String resourcePrefix;
  
  private ResourceMap resourceMap;
  
  private List<TaskListener<T, V>> taskListeners;
  
  private InputBlocker inputBlocker;
  
  private String name = null;
  
  private String title = null;
  
  private String description = null;
  
  private long messageTime = -1L;
  
  private String message = null;
  
  private long startTime = -1L;
  
  private long doneTime = -1L;
  
  private boolean userCanCancel = true;
  
  private boolean progressPropertyIsValid = false;
  
  private TaskService taskService = null;
  
  public enum BlockingScope {
    NONE, ACTION, COMPONENT, WINDOW, APPLICATION;
  }
  
  private void initTask(ResourceMap paramResourceMap, String paramString) {
    this.resourceMap = paramResourceMap;
    if (paramString == null || paramString.length() == 0) {
      this.resourcePrefix = "";
    } else if (paramString.endsWith(".")) {
      this.resourcePrefix = paramString;
    } else {
      this.resourcePrefix = paramString + ".";
    } 
    if (paramResourceMap != null) {
      this.title = paramResourceMap.getString(resourceName("title"), new Object[0]);
      this.description = paramResourceMap.getString(resourceName("description"), new Object[0]);
      this.message = paramResourceMap.getString(resourceName("message"), new Object[0]);
      if (this.message != null)
        this.messageTime = System.currentTimeMillis(); 
    } 
    addPropertyChangeListener(new StatePCL());
    this.taskListeners = new CopyOnWriteArrayList<TaskListener<T, V>>();
  }
  
  private ResourceMap defaultResourceMap(Application paramApplication) {
    return paramApplication.getContext().getResourceMap(getClass(), Task.class);
  }
  
  @Deprecated
  public Task(Application paramApplication, ResourceMap paramResourceMap, String paramString) {
    this.application = paramApplication;
    initTask(paramResourceMap, paramString);
  }
  
  @Deprecated
  public Task(Application paramApplication, String paramString) {
    this.application = paramApplication;
    initTask(defaultResourceMap(paramApplication), paramString);
  }
  
  public Task(Application paramApplication) {
    this.application = paramApplication;
    initTask(defaultResourceMap(paramApplication), "");
  }
  
  public final Application getApplication() {
    return this.application;
  }
  
  public final ApplicationContext getContext() {
    return getApplication().getContext();
  }
  
  public synchronized TaskService getTaskService() {
    return this.taskService;
  }
  
  synchronized void setTaskService(TaskService paramTaskService) {
    TaskService taskService1, taskService2;
    synchronized (this) {
      taskService1 = this.taskService;
      this.taskService = paramTaskService;
      taskService2 = this.taskService;
    } 
    firePropertyChange("taskService", taskService1, taskService2);
  }
  
  protected final String resourceName(String paramString) {
    return this.resourcePrefix + paramString;
  }
  
  public final ResourceMap getResourceMap() {
    return this.resourceMap;
  }
  
  public synchronized String getTitle() {
    return this.title;
  }
  
  protected void setTitle(String paramString) {
    String str1, str2;
    synchronized (this) {
      str1 = this.title;
      this.title = paramString;
      str2 = this.title;
    } 
    firePropertyChange("title", str1, str2);
  }
  
  public synchronized String getDescription() {
    return this.description;
  }
  
  protected void setDescription(String paramString) {
    String str1, str2;
    synchronized (this) {
      str1 = this.description;
      this.description = paramString;
      str2 = this.description;
    } 
    firePropertyChange("description", str1, str2);
  }
  
  public long getExecutionDuration(TimeUnit paramTimeUnit) {
    long l1;
    long l2;
    long l3;
    synchronized (this) {
      l1 = this.startTime;
      l2 = this.doneTime;
    } 
    if (l1 == -1L) {
      l3 = 0L;
    } else if (l2 == -1L) {
      l3 = System.currentTimeMillis() - l1;
    } else {
      l3 = l2 - l1;
    } 
    return paramTimeUnit.convert(Math.max(0L, l3), TimeUnit.MILLISECONDS);
  }
  
  public String getMessage() {
    return this.message;
  }
  
  protected void setMessage(String paramString) {
    String str1, str2;
    synchronized (this) {
      str1 = this.message;
      this.message = paramString;
      str2 = this.message;
      this.messageTime = System.currentTimeMillis();
    } 
    firePropertyChange("message", str1, str2);
  }
  
  protected final void message(String paramString, Object... paramVarArgs) {
    ResourceMap resourceMap = getResourceMap();
    if (resourceMap != null) {
      setMessage(resourceMap.getString(resourceName(paramString), paramVarArgs));
    } else {
      setMessage(paramString);
    } 
  }
  
  public long getMessageDuration(TimeUnit paramTimeUnit) {
    long l1;
    synchronized (this) {
      l1 = this.messageTime;
    } 
    long l2 = (l1 == -1L) ? 0L : Math.max(0L, System.currentTimeMillis() - l1);
    return paramTimeUnit.convert(l2, TimeUnit.MILLISECONDS);
  }
  
  public synchronized boolean getUserCanCancel() {
    return this.userCanCancel;
  }
  
  protected void setUserCanCancel(boolean paramBoolean) {
    boolean bool1, bool2;
    synchronized (this) {
      bool1 = this.userCanCancel;
      this.userCanCancel = paramBoolean;
      bool2 = this.userCanCancel;
    } 
    firePropertyChange("userCanCancel", Boolean.valueOf(bool1), Boolean.valueOf(bool2));
  }
  
  public synchronized boolean isProgressPropertyValid() {
    return this.progressPropertyIsValid;
  }
  
  protected final void setProgress(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 >= paramInt3)
      throw new IllegalArgumentException("invalid range: min >= max"); 
    if (paramInt1 < paramInt2 || paramInt1 > paramInt3)
      throw new IllegalArgumentException("invalid value"); 
    float f = (paramInt1 - paramInt2) / (paramInt3 - paramInt2);
    setProgress(Math.round(f * 100.0F));
  }
  
  protected final void setProgress(float paramFloat) {
    if (paramFloat < 0.0D || paramFloat > 1.0D)
      throw new IllegalArgumentException("invalid percentage"); 
    setProgress(Math.round(paramFloat * 100.0F));
  }
  
  protected final void setProgress(float paramFloat1, float paramFloat2, float paramFloat3) {
    if (paramFloat2 >= paramFloat3)
      throw new IllegalArgumentException("invalid range: min >= max"); 
    if (paramFloat1 < paramFloat2 || paramFloat1 > paramFloat3)
      throw new IllegalArgumentException("invalid value"); 
    float f = (paramFloat1 - paramFloat2) / (paramFloat3 - paramFloat2);
    setProgress(Math.round(f * 100.0F));
  }
  
  public final boolean isPending() {
    return (getState() == SwingWorker.StateValue.PENDING);
  }
  
  public final boolean isStarted() {
    return (getState() == SwingWorker.StateValue.STARTED);
  }
  
  protected void process(List<V> paramList) {
    fireProcessListeners(paramList);
  }
  
  protected final void done() {
    try {
      if (isCancelled()) {
        cancelled();
      } else {
        try {
          succeeded((T)get());
        } catch (InterruptedException interruptedException) {
          interrupted(interruptedException);
        } catch (ExecutionException executionException) {
          failed(executionException.getCause());
        } 
      } 
    } finally {
      try {
        finished();
      } finally {
        setTaskService((TaskService)null);
      } 
    } 
  }
  
  protected void cancelled() {}
  
  protected void succeeded(T paramT) {}
  
  protected void interrupted(InterruptedException paramInterruptedException) {}
  
  protected void failed(Throwable paramThrowable) {
    String str = String.format("%s failed: %s", new Object[] { this, paramThrowable });
    logger.log(Level.SEVERE, str, paramThrowable);
  }
  
  protected void finished() {}
  
  public void addTaskListener(TaskListener<T, V> paramTaskListener) {
    if (paramTaskListener == null)
      throw new IllegalArgumentException("null listener"); 
    this.taskListeners.add(paramTaskListener);
  }
  
  public void removeTaskListener(TaskListener<T, V> paramTaskListener) {
    if (paramTaskListener == null)
      throw new IllegalArgumentException("null listener"); 
    this.taskListeners.remove(paramTaskListener);
  }
  
  public TaskListener<T, V>[] getTaskListeners() {
    return this.taskListeners.<TaskListener<T, V>>toArray((TaskListener<T, V>[])new TaskListener[this.taskListeners.size()]);
  }
  
  private void fireProcessListeners(List<V> paramList) {
    TaskEvent<List<V>> taskEvent = new TaskEvent<List<V>>(this, paramList);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.process(taskEvent); 
  }
  
  private void fireDoInBackgroundListeners() {
    TaskEvent<Void> taskEvent = new TaskEvent(this, null);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.doInBackground(taskEvent); 
  }
  
  private void fireSucceededListeners(T paramT) {
    TaskEvent<T> taskEvent = new TaskEvent<T>(this, paramT);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.succeeded(taskEvent); 
  }
  
  private void fireCancelledListeners() {
    TaskEvent<Void> taskEvent = new TaskEvent(this, null);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.cancelled(taskEvent); 
  }
  
  private void fireInterruptedListeners(InterruptedException paramInterruptedException) {
    TaskEvent<InterruptedException> taskEvent = new TaskEvent<InterruptedException>(this, paramInterruptedException);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.interrupted(taskEvent); 
  }
  
  private void fireFailedListeners(Throwable paramThrowable) {
    TaskEvent<Throwable> taskEvent = new TaskEvent<Throwable>(this, paramThrowable);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.failed(taskEvent); 
  }
  
  private void fireFinishedListeners() {
    TaskEvent<Void> taskEvent = new TaskEvent(this, null);
    for (TaskListener<T, V> taskListener : this.taskListeners)
      taskListener.finished(taskEvent); 
  }
  
  private void fireCompletionListeners() {
    try {
      if (isCancelled()) {
        fireCancelledListeners();
      } else {
        try {
          fireSucceededListeners((T)get());
        } catch (InterruptedException interruptedException) {
          fireInterruptedListeners(interruptedException);
        } catch (ExecutionException executionException) {
          fireFailedListeners(executionException.getCause());
        } 
      } 
    } finally {
      fireFinishedListeners();
    } 
  }
  
  private class StatePCL implements PropertyChangeListener {
    private StatePCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("state".equals(str)) {
        SwingWorker.StateValue stateValue = (SwingWorker.StateValue)param1PropertyChangeEvent.getNewValue();
        Task task = (Task)param1PropertyChangeEvent.getSource();
        switch (stateValue) {
          case ACTION:
            taskStarted(task);
            break;
          case COMPONENT:
            taskDone(task);
            break;
        } 
      } else if ("progress".equals(str)) {
        synchronized (Task.this) {
          Task.this.progressPropertyIsValid = true;
        } 
      } 
    }
    
    private void taskStarted(Task param1Task) {
      synchronized (Task.this) {
        Task.this.startTime = System.currentTimeMillis();
      } 
      Task.this.firePropertyChange("started", Boolean.valueOf(false), Boolean.valueOf(true));
      Task.this.fireDoInBackgroundListeners();
    }
    
    private void taskDone(Task param1Task) {
      synchronized (Task.this) {
        Task.this.doneTime = System.currentTimeMillis();
      } 
      try {
        param1Task.removePropertyChangeListener(this);
        Task.this.firePropertyChange("done", Boolean.valueOf(false), Boolean.valueOf(true));
        Task.this.fireCompletionListeners();
      } finally {
        Task.this.firePropertyChange("completed", Boolean.valueOf(false), Boolean.valueOf(true));
      } 
    }
  }
  
  public final InputBlocker getInputBlocker() {
    return this.inputBlocker;
  }
  
  public final void setInputBlocker(InputBlocker paramInputBlocker) {
    InputBlocker inputBlocker1, inputBlocker2;
    if (getTaskService() != null)
      throw new IllegalStateException("task already being executed"); 
    synchronized (this) {
      inputBlocker1 = this.inputBlocker;
      this.inputBlocker = paramInputBlocker;
      inputBlocker2 = this.inputBlocker;
    } 
    firePropertyChange("inputBlocker", inputBlocker1, inputBlocker2);
  }
  
  public static abstract class InputBlocker extends AbstractBean {
    private final Task task;
    
    private final Task.BlockingScope scope;
    
    private final Object target;
    
    private final ApplicationAction action;
    
    public InputBlocker(Task param1Task, Task.BlockingScope param1BlockingScope, Object param1Object, ApplicationAction param1ApplicationAction) {
      if (param1Task == null)
        throw new IllegalArgumentException("null task"); 
      if (param1Task.getTaskService() != null)
        throw new IllegalStateException("task already being executed"); 
      switch (param1BlockingScope) {
        case ACTION:
          if (!(param1Object instanceof javax.swing.Action))
            throw new IllegalArgumentException("target not an Action"); 
          break;
        case COMPONENT:
        case WINDOW:
          if (!(param1Object instanceof java.awt.Component))
            throw new IllegalArgumentException("target not a Component"); 
          break;
      } 
      this.task = param1Task;
      this.scope = param1BlockingScope;
      this.target = param1Object;
      this.action = param1ApplicationAction;
    }
    
    public InputBlocker(Task param1Task, Task.BlockingScope param1BlockingScope, Object param1Object) {
      this(param1Task, param1BlockingScope, param1Object, (param1Object instanceof ApplicationAction) ? (ApplicationAction)param1Object : null);
    }
    
    public final Task getTask() {
      return this.task;
    }
    
    public final Task.BlockingScope getScope() {
      return this.scope;
    }
    
    public final Object getTarget() {
      return this.target;
    }
    
    public final ApplicationAction getAction() {
      return this.action;
    }
    
    protected abstract void block();
    
    protected abstract void unblock();
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\Task.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */