package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

public abstract class SingleFrameApplication extends Application {
  private static final Logger logger = Logger.getLogger(SingleFrameApplication.class.getName());
  
  private ResourceMap appResources = null;
  
  public final JFrame getMainFrame() {
    return getMainView().getFrame();
  }
  
  protected final void setMainFrame(JFrame paramJFrame) {
    getMainView().setFrame(paramJFrame);
  }
  
  private String sessionFilename(Window paramWindow) {
    if (paramWindow == null)
      return null; 
    String str = paramWindow.getName();
    return (str == null) ? null : (str + ".session.xml");
  }
  
  protected void configureWindow(Window paramWindow) {
    getContext().getResourceMap().injectComponents(paramWindow);
  }
  
  private void initRootPaneContainer(RootPaneContainer paramRootPaneContainer) {
    JRootPane jRootPane = paramRootPaneContainer.getRootPane();
    String str = "SingleFrameApplication.initRootPaneContainer";
    if (jRootPane.getClientProperty(str) != null)
      return; 
    jRootPane.putClientProperty(str, Boolean.TRUE);
    Container container = jRootPane.getParent();
    if (container instanceof Window)
      configureWindow((Window)container); 
    JFrame jFrame = getMainFrame();
    if (paramRootPaneContainer == jFrame) {
      jFrame.addWindowListener(new MainFrameListener());
      jFrame.setDefaultCloseOperation(0);
    } else if (container instanceof Window) {
      Window window = (Window)container;
      window.addHierarchyListener(new SecondaryWindowListener());
    } 
    if (container instanceof JFrame)
      container.addComponentListener(new FrameBoundsListener()); 
    if (container instanceof Window) {
      Window window = (Window)container;
      if (!container.isValid() || container.getWidth() == 0 || container.getHeight() == 0)
        window.pack(); 
      if (!window.isLocationByPlatform() && container.getX() == 0 && container.getY() == 0) {
        Window window1 = window.getOwner();
        if (window1 == null)
          window1 = (window != jFrame) ? jFrame : null; 
        window.setLocationRelativeTo(window1);
      } 
    } 
    if (container instanceof Window) {
      String str1 = sessionFilename((Window)container);
      if (str1 != null)
        try {
          getContext().getSessionStorage().restore(container, str1);
        } catch (Exception exception) {
          String str2 = String.format("couldn't restore sesssion [%s]", new Object[] { str1 });
          logger.log(Level.WARNING, str2, exception);
        }  
    } 
  }
  
  protected void show(JComponent paramJComponent) {
    if (paramJComponent == null)
      throw new IllegalArgumentException("null JComponent"); 
    JFrame jFrame = getMainFrame();
    jFrame.getContentPane().add(paramJComponent, "Center");
    initRootPaneContainer(jFrame);
    jFrame.setVisible(true);
  }
  
  public void show(JDialog paramJDialog) {
    if (paramJDialog == null)
      throw new IllegalArgumentException("null JDialog"); 
    initRootPaneContainer(paramJDialog);
    paramJDialog.setVisible(true);
  }
  
  public void show(JFrame paramJFrame) {
    if (paramJFrame == null)
      throw new IllegalArgumentException("null JFrame"); 
    initRootPaneContainer(paramJFrame);
    paramJFrame.setVisible(true);
  }
  
  private void saveSession(Window paramWindow) {
    String str = sessionFilename(paramWindow);
    if (str != null)
      try {
        getContext().getSessionStorage().save(paramWindow, str);
      } catch (IOException iOException) {
        logger.log(Level.WARNING, "couldn't save sesssion", iOException);
      }  
  }
  
  private boolean isVisibleWindow(Window paramWindow) {
    return (paramWindow.isVisible() && (paramWindow instanceof JFrame || paramWindow instanceof JDialog || paramWindow instanceof javax.swing.JWindow));
  }
  
  private List<Window> getVisibleSecondaryWindows() {
    ArrayList<Window> arrayList = new ArrayList();
    Method method = null;
    try {
      method = Window.class.getMethod("getWindows", new Class[0]);
    } catch (Exception exception) {}
    if (method != null) {
      Window[] arrayOfWindow = null;
      try {
        arrayOfWindow = (Window[])method.invoke(null, new Object[0]);
      } catch (Exception exception) {
        throw new Error("HCTB - can't get top level windows list", exception);
      } 
      if (arrayOfWindow != null)
        for (Window window : arrayOfWindow) {
          if (isVisibleWindow(window))
            arrayList.add(window); 
        }  
    } else {
      Frame[] arrayOfFrame = Frame.getFrames();
      if (arrayOfFrame != null)
        for (Frame frame : arrayOfFrame) {
          if (isVisibleWindow(frame))
            arrayList.add(frame); 
        }  
    } 
    return arrayList;
  }
  
  protected void shutdown() {
    saveSession(getMainFrame());
    for (Window window : getVisibleSecondaryWindows())
      saveSession(window); 
  }
  
  private class MainFrameListener extends WindowAdapter {
    private MainFrameListener() {}
    
    public void windowClosing(WindowEvent param1WindowEvent) {
      SingleFrameApplication.this.exit(param1WindowEvent);
    }
  }
  
  private class SecondaryWindowListener implements HierarchyListener {
    private SecondaryWindowListener() {}
    
    public void hierarchyChanged(HierarchyEvent param1HierarchyEvent) {
      if ((param1HierarchyEvent.getChangeFlags() & 0x4L) != 0L && 
        param1HierarchyEvent.getSource() instanceof Window) {
        Window window = (Window)param1HierarchyEvent.getSource();
        if (!window.isShowing())
          SingleFrameApplication.this.saveSession(window); 
      } 
    }
  }
  
  private static class FrameBoundsListener implements ComponentListener {
    private FrameBoundsListener() {}
    
    private void maybeSaveFrameSize(ComponentEvent param1ComponentEvent) {
      if (param1ComponentEvent.getComponent() instanceof JFrame) {
        JFrame jFrame = (JFrame)param1ComponentEvent.getComponent();
        if ((jFrame.getExtendedState() & 0x6) == 0) {
          String str = "WindowState.normalBounds";
          jFrame.getRootPane().putClientProperty(str, jFrame.getBounds());
        } 
      } 
    }
    
    public void componentResized(ComponentEvent param1ComponentEvent) {
      maybeSaveFrameSize(param1ComponentEvent);
    }
    
    public void componentMoved(ComponentEvent param1ComponentEvent) {}
    
    public void componentHidden(ComponentEvent param1ComponentEvent) {}
    
    public void componentShown(ComponentEvent param1ComponentEvent) {}
  }
  
  private FrameView mainView = null;
  
  public FrameView getMainView() {
    if (this.mainView == null)
      this.mainView = new FrameView(this); 
    return this.mainView;
  }
  
  public void show(View paramView) {
    if (this.mainView == null && paramView instanceof FrameView)
      this.mainView = (FrameView)paramView; 
    RootPaneContainer rootPaneContainer = (RootPaneContainer)paramView.getRootPane().getParent();
    initRootPaneContainer(rootPaneContainer);
    ((Window)rootPaneContainer).setVisible(true);
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\SingleFrameApplication.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */