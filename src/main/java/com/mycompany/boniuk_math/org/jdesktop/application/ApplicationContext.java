package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import javax.swing.JComponent;

public class ApplicationContext extends AbstractBean {
  private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
  
  private final List<TaskService> taskServices;
  
  private final List<TaskService> taskServicesReadOnly;
  
  private ResourceManager resourceManager;
  
  private ActionManager actionManager;
  
  private LocalStorage localStorage;
  
  private SessionStorage sessionStorage;
  
  private Application application = null;
  
  private Class applicationClass = null;
  
  private JComponent focusOwner = null;
  
  private Clipboard clipboard = null;
  
  private Throwable uncaughtException = null;
  
  private TaskMonitor taskMonitor = null;
  
  protected ApplicationContext() {
    this.resourceManager = new ResourceManager(this);
    this.actionManager = new ActionManager(this);
    this.localStorage = new LocalStorage(this);
    this.sessionStorage = new SessionStorage(this);
    this.taskServices = new CopyOnWriteArrayList<TaskService>();
    this.taskServices.add(new TaskService("default"));
    this.taskServicesReadOnly = Collections.unmodifiableList(this.taskServices);
  }
  
  public final synchronized Class getApplicationClass() {
    return this.applicationClass;
  }
  
  public final synchronized void setApplicationClass(Class paramClass) {
    if (this.application != null)
      throw new IllegalStateException("application has been launched"); 
    this.applicationClass = paramClass;
  }
  
  public final synchronized Application getApplication() {
    return this.application;
  }
  
  synchronized void setApplication(Application paramApplication) {
    if (this.application != null)
      throw new IllegalStateException("application has already been launched"); 
    this.application = paramApplication;
  }
  
  public final ResourceManager getResourceManager() {
    return this.resourceManager;
  }
  
  protected void setResourceManager(ResourceManager paramResourceManager) {
    if (paramResourceManager == null)
      throw new IllegalArgumentException("null resourceManager"); 
    ResourceManager resourceManager = this.resourceManager;
    this.resourceManager = paramResourceManager;
    firePropertyChange("resourceManager", resourceManager, this.resourceManager);
  }
  
  public final ResourceMap getResourceMap(Class paramClass) {
    return getResourceManager().getResourceMap(paramClass, paramClass);
  }
  
  public final ResourceMap getResourceMap(Class paramClass1, Class paramClass2) {
    return getResourceManager().getResourceMap(paramClass1, paramClass2);
  }
  
  public final ResourceMap getResourceMap() {
    return getResourceManager().getResourceMap();
  }
  
  public final ActionManager getActionManager() {
    return this.actionManager;
  }
  
  protected void setActionManager(ActionManager paramActionManager) {
    if (paramActionManager == null)
      throw new IllegalArgumentException("null actionManager"); 
    ActionManager actionManager = this.actionManager;
    this.actionManager = paramActionManager;
    firePropertyChange("actionManager", actionManager, this.actionManager);
  }
  
  public final ApplicationActionMap getActionMap() {
    return getActionManager().getActionMap();
  }
  
  public final ApplicationActionMap getActionMap(Class paramClass, Object paramObject) {
    return getActionManager().getActionMap(paramClass, paramObject);
  }
  
  public final ApplicationActionMap getActionMap(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("null actionsObject"); 
    return getActionManager().getActionMap(paramObject.getClass(), paramObject);
  }
  
  public final LocalStorage getLocalStorage() {
    return this.localStorage;
  }
  
  protected void setLocalStorage(LocalStorage paramLocalStorage) {
    if (paramLocalStorage == null)
      throw new IllegalArgumentException("null localStorage"); 
    LocalStorage localStorage = this.localStorage;
    this.localStorage = paramLocalStorage;
    firePropertyChange("localStorage", localStorage, this.localStorage);
  }
  
  public final SessionStorage getSessionStorage() {
    return this.sessionStorage;
  }
  
  protected void setSessionStorage(SessionStorage paramSessionStorage) {
    if (paramSessionStorage == null)
      throw new IllegalArgumentException("null sessionStorage"); 
    SessionStorage sessionStorage = this.sessionStorage;
    this.sessionStorage = paramSessionStorage;
    firePropertyChange("sessionStorage", sessionStorage, this.sessionStorage);
  }
  
  public Clipboard getClipboard() {
    if (this.clipboard == null)
      try {
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      } catch (SecurityException securityException) {
        this.clipboard = new Clipboard("sandbox");
      }  
    return this.clipboard;
  }
  
  public JComponent getFocusOwner() {
    return this.focusOwner;
  }
  
  void setFocusOwner(JComponent paramJComponent) {
    JComponent jComponent = this.focusOwner;
    this.focusOwner = paramJComponent;
    firePropertyChange("focusOwner", jComponent, this.focusOwner);
  }
  
  private List<TaskService> copyTaskServices() {
    return new ArrayList<TaskService>(this.taskServices);
  }
  
  public void addTaskService(TaskService paramTaskService) {
    if (paramTaskService == null)
      throw new IllegalArgumentException("null taskService"); 
    List<TaskService> list1 = null, list2 = null;
    boolean bool = false;
    synchronized (this.taskServices) {
      if (!this.taskServices.contains(paramTaskService)) {
        list1 = copyTaskServices();
        this.taskServices.add(paramTaskService);
        list2 = copyTaskServices();
        bool = true;
      } 
    } 
    if (bool)
      firePropertyChange("taskServices", list1, list2); 
  }
  
  public void removeTaskService(TaskService paramTaskService) {
    if (paramTaskService == null)
      throw new IllegalArgumentException("null taskService"); 
    List<TaskService> list1 = null, list2 = null;
    boolean bool = false;
    synchronized (this.taskServices) {
      if (this.taskServices.contains(paramTaskService)) {
        list1 = copyTaskServices();
        this.taskServices.remove(paramTaskService);
        list2 = copyTaskServices();
        bool = true;
      } 
    } 
    if (bool)
      firePropertyChange("taskServices", list1, list2); 
  }
  
  public TaskService getTaskService(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("null name"); 
    for (TaskService taskService : this.taskServices) {
      if (paramString.equals(taskService.getName()))
        return taskService; 
    } 
    return null;
  }
  
  public final TaskService getTaskService() {
    return getTaskService("default");
  }
  
  public List<TaskService> getTaskServices() {
    return this.taskServicesReadOnly;
  }
  
  public final TaskMonitor getTaskMonitor() {
    if (this.taskMonitor == null)
      this.taskMonitor = new TaskMonitor(this); 
    return this.taskMonitor;
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ApplicationContext.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */