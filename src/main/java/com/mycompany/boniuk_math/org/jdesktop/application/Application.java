package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.PaintEvent;
import java.beans.Beans;
import java.lang.reflect.Constructor;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@ProxyActions({"cut", "copy", "paste", "delete"})
public abstract class Application extends AbstractBean {
  private static final Logger logger = Logger.getLogger(Application.class.getName());
  
  private static Application application = null;
  
  private final List<ExitListener> exitListeners = new CopyOnWriteArrayList<ExitListener>();
  
  private final ApplicationContext context = new ApplicationContext();
  
  public static synchronized <T extends Application> void launch(final Class<T> applicationClass, final String[] args) {
    Runnable runnable = new Runnable() {
        public void run() {
          try {
            Application.application = Application.create(applicationClass);
            Application.application.initialize(args);
            Application.application.startup();
            Application.application.waitForReady();
          } catch (Exception exception) {
            String str = String.format("Application %s failed to launch", new Object[] { this.val$applicationClass });
            Application.logger.log(Level.SEVERE, str, exception);
            throw new Error(str, exception);
          } 
        }
      };
    SwingUtilities.invokeLater(runnable);
  }
  
  static <T extends Application> T create(Class<T> paramClass) throws Exception {
    if (!Beans.isDesignTime())
      try {
        System.setProperty("java.net.useSystemProxies", "true");
      } catch (SecurityException securityException) {} 
    Constructor<T> constructor = paramClass.getDeclaredConstructor(new Class[0]);
    if (!constructor.isAccessible())
      try {
        constructor.setAccessible(true);
      } catch (SecurityException securityException) {} 
    Application application = (Application)constructor.newInstance(new Object[0]);
    ApplicationContext applicationContext = application.getContext();
    applicationContext.setApplicationClass(paramClass);
    applicationContext.setApplication(application);
    ResourceMap resourceMap = applicationContext.getResourceMap();
    resourceMap.putResource("platform", platform());
    if (!Beans.isDesignTime()) {
      String str1 = "Application.lookAndFeel";
      String str2 = resourceMap.getString(str1, new Object[0]);
      String str3 = (str2 == null) ? "system" : str2;
      try {
        if (str3.equalsIgnoreCase("system")) {
          String str = UIManager.getSystemLookAndFeelClassName();
          UIManager.setLookAndFeel(str);
        } else if (!str3.equalsIgnoreCase("default")) {
          UIManager.setLookAndFeel(str3);
        } 
      } catch (Exception exception) {
        String str = "Couldn't set LookandFeel " + str1 + " = \"" + str2 + "\"";
        logger.log(Level.WARNING, str, exception);
      } 
    } 
    return (T)application;
  }
  
  private static String platform() {
    String str = "default";
    try {
      String str1 = System.getProperty("os.name");
      if (str1 != null && str1.toLowerCase().startsWith("mac os x"))
        str = "osx"; 
    } catch (SecurityException securityException) {}
    return str;
  }
  
  void waitForReady() {
    (new DoWaitForEmptyEventQ()).execute();
  }
  
  protected void initialize(String[] paramArrayOfString) {}
  
  protected void ready() {}
  
  protected void shutdown() {}
  
  private static class NotifyingEvent extends PaintEvent implements ActiveEvent {
    private boolean dispatched = false;
    
    private boolean qEmpty = false;
    
    NotifyingEvent(Component param1Component) {
      super(param1Component, 801, null);
    }
    
    synchronized boolean isDispatched() {
      return this.dispatched;
    }
    
    synchronized boolean isEventQEmpty() {
      return this.qEmpty;
    }
    
    public void dispatch() {
      EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
      synchronized (this) {
        this.qEmpty = (eventQueue.peekEvent() == null);
        this.dispatched = true;
        notifyAll();
      } 
    }
  }
  
  private void waitForEmptyEventQ() {
    boolean bool = false;
    JPanel jPanel = new JPanel();
    EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    while (!bool) {
      NotifyingEvent notifyingEvent = new NotifyingEvent(jPanel);
      eventQueue.postEvent(notifyingEvent);
      synchronized (notifyingEvent) {
        while (!notifyingEvent.isDispatched()) {
          try {
            notifyingEvent.wait();
          } catch (InterruptedException interruptedException) {}
        } 
        bool = notifyingEvent.isEventQEmpty();
      } 
    } 
  }
  
  private class DoWaitForEmptyEventQ extends Task<Void, Void> {
    DoWaitForEmptyEventQ() {
      super(Application.this);
    }
    
    protected Void doInBackground() {
      Application.this.waitForEmptyEventQ();
      return null;
    }
    
    protected void finished() {
      Application.this.ready();
    }
  }
  
  public final void exit() {
    exit(null);
  }
  
  public void exit(EventObject paramEventObject) {
    for (ExitListener exitListener : this.exitListeners) {
      if (!exitListener.canExit(paramEventObject))
        return; 
    } 
    try {
      for (ExitListener exitListener : this.exitListeners) {
        try {
          exitListener.willExit(paramEventObject);
        } catch (Exception exception) {
          logger.log(Level.WARNING, "ExitListener.willExit() failed", exception);
        } 
      } 
      shutdown();
    } catch (Exception exception) {
      logger.log(Level.WARNING, "unexpected error in Application.shutdown()", exception);
    } finally {
      end();
    } 
  }
  
  protected void end() {
    Runtime.getRuntime().exit(0);
  }
  
  public void addExitListener(ExitListener paramExitListener) {
    this.exitListeners.add(paramExitListener);
  }
  
  public void removeExitListener(ExitListener paramExitListener) {
    this.exitListeners.remove(paramExitListener);
  }
  
  public ExitListener[] getExitListeners() {
    int i = this.exitListeners.size();
    return this.exitListeners.<ExitListener>toArray(new ExitListener[i]);
  }
  
  @Action
  public void quit(ActionEvent paramActionEvent) {
    exit(paramActionEvent);
  }
  
  public final ApplicationContext getContext() {
    return this.context;
  }
  
  public static synchronized <T extends Application> T getInstance(Class<T> paramClass) {
    if (application == null)
      try {
        application = create(paramClass);
      } catch (Exception exception) {
        String str = String.format("Couldn't construct %s", new Object[] { paramClass });
        throw new Error(str, exception);
      }  
    return paramClass.cast(application);
  }
  
  public static synchronized Application getInstance() {
    if (application == null)
      application = new NoApplication(); 
    return application;
  }
  
  private static class NoApplication extends Application {
    protected NoApplication() {
      ApplicationContext applicationContext = getContext();
      applicationContext.setApplicationClass(getClass());
      applicationContext.setApplication(this);
      ResourceMap resourceMap = applicationContext.getResourceMap();
      resourceMap.putResource("platform", Application.platform());
    }
    
    protected void startup() {}
  }
  
  public void show(View paramView) {
    Window window = (Window)paramView.getRootPane().getParent();
    if (window != null) {
      window.pack();
      window.setVisible(true);
    } 
  }
  
  public void hide(View paramView) {
    paramView.getRootPane().getParent().setVisible(false);
  }
  
  protected abstract void startup();
  
  public static interface ExitListener extends EventListener {
    boolean canExit(EventObject param1EventObject);
    
    void willExit(EventObject param1EventObject);
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\Application.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */