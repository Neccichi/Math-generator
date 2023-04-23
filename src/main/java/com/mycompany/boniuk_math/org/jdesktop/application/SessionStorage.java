package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class SessionStorage {
  private static Logger logger = Logger.getLogger(SessionStorage.class.getName());
  
  private final Map<Class, Property> propertyMap;
  
  private final ApplicationContext context;
  
  protected SessionStorage(ApplicationContext paramApplicationContext) {
    if (paramApplicationContext == null)
      throw new IllegalArgumentException("null context"); 
    this.context = paramApplicationContext;
    this.propertyMap = (Map)new HashMap<Class<?>, Property>();
    this.propertyMap.put(Window.class, new WindowProperty());
    this.propertyMap.put(JTabbedPane.class, new TabbedPaneProperty());
    this.propertyMap.put(JSplitPane.class, new SplitPaneProperty());
    this.propertyMap.put(JTable.class, new TableProperty());
  }
  
  protected final ApplicationContext getContext() {
    return this.context;
  }
  
  private void checkSaveRestoreArgs(Component paramComponent, String paramString) {
    if (paramComponent == null)
      throw new IllegalArgumentException("null root"); 
    if (paramString == null)
      throw new IllegalArgumentException("null fileName"); 
  }
  
  private String getComponentName(Component paramComponent) {
    return paramComponent.getName();
  }
  
  private String getComponentPathname(Component paramComponent) {
    String str = getComponentName(paramComponent);
    if (str == null)
      return null; 
    StringBuilder stringBuilder = new StringBuilder(str);
    while (paramComponent.getParent() != null && !(paramComponent instanceof Window) && !(paramComponent instanceof java.applet.Applet)) {
      paramComponent = paramComponent.getParent();
      str = getComponentName(paramComponent);
      if (str == null) {
        int i = paramComponent.getParent().getComponentZOrder(paramComponent);
        if (i >= 0) {
          Class<?> clazz = paramComponent.getClass();
          str = clazz.getSimpleName();
          if (str.length() == 0)
            str = "Anonymous" + clazz.getSuperclass().getSimpleName(); 
          str = str + i;
        } else {
          logger.warning("Couldn't compute pathname for " + paramComponent);
          return null;
        } 
      } 
      stringBuilder.append("/").append(str);
    } 
    return stringBuilder.toString();
  }
  
  private void saveTree(List<Component> paramList, Map<String, Object> paramMap) {
    ArrayList<? super Component> arrayList = new ArrayList();
    for (Component component : paramList) {
      if (component != null) {
        Property property = getProperty(component);
        if (property != null) {
          String str = getComponentPathname(component);
          if (str != null) {
            Object object = property.getSessionState(component);
            if (object != null)
              paramMap.put(str, object); 
          } 
        } 
      } 
      if (component instanceof Container) {
        Component[] arrayOfComponent = ((Container)component).getComponents();
        if (arrayOfComponent != null && arrayOfComponent.length > 0)
          Collections.addAll(arrayList, arrayOfComponent); 
      } 
    } 
    if (arrayList.size() > 0)
      saveTree((List)arrayList, paramMap); 
  }
  
  public void save(Component paramComponent, String paramString) throws IOException {
    checkSaveRestoreArgs(paramComponent, paramString);
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    saveTree(Collections.singletonList(paramComponent), (Map)hashMap);
    LocalStorage localStorage = getContext().getLocalStorage();
    localStorage.save(hashMap, paramString);
  }
  
  private void restoreTree(List<Component> paramList, Map<String, Object> paramMap) {
    ArrayList<? super Component> arrayList = new ArrayList();
    for (Component component : paramList) {
      if (component != null) {
        Property property = getProperty(component);
        if (property != null) {
          String str = getComponentPathname(component);
          if (str != null) {
            Object object = paramMap.get(str);
            if (object != null) {
              property.setSessionState(component, object);
            } else {
              logger.warning("No saved state for " + component);
            } 
          } 
        } 
      } 
      if (component instanceof Container) {
        Component[] arrayOfComponent = ((Container)component).getComponents();
        if (arrayOfComponent != null && arrayOfComponent.length > 0)
          Collections.addAll(arrayList, arrayOfComponent); 
      } 
    } 
    if (arrayList.size() > 0)
      restoreTree((List)arrayList, paramMap); 
  }
  
  public void restore(Component paramComponent, String paramString) throws IOException {
    checkSaveRestoreArgs(paramComponent, paramString);
    LocalStorage localStorage = getContext().getLocalStorage();
    Map<String, Object> map = (Map)localStorage.load(paramString);
    if (map != null)
      restoreTree(Collections.singletonList(paramComponent), map); 
  }
  
  public static class WindowState {
    private final Rectangle bounds;
    
    private Rectangle gcBounds = null;
    
    private int screenCount;
    
    private int frameState = 0;
    
    public WindowState() {
      this.bounds = new Rectangle();
    }
    
    public WindowState(Rectangle param1Rectangle1, Rectangle param1Rectangle2, int param1Int1, int param1Int2) {
      if (param1Rectangle1 == null)
        throw new IllegalArgumentException("null bounds"); 
      if (param1Int1 < 1)
        throw new IllegalArgumentException("invalid screenCount"); 
      this.bounds = param1Rectangle1;
      this.gcBounds = param1Rectangle2;
      this.screenCount = param1Int1;
      this.frameState = param1Int2;
    }
    
    public Rectangle getBounds() {
      return new Rectangle(this.bounds);
    }
    
    public void setBounds(Rectangle param1Rectangle) {
      this.bounds.setBounds(param1Rectangle);
    }
    
    public int getScreenCount() {
      return this.screenCount;
    }
    
    public void setScreenCount(int param1Int) {
      this.screenCount = param1Int;
    }
    
    public int getFrameState() {
      return this.frameState;
    }
    
    public void setFrameState(int param1Int) {
      this.frameState = param1Int;
    }
    
    public Rectangle getGraphicsConfigurationBounds() {
      return (this.gcBounds == null) ? null : new Rectangle(this.gcBounds);
    }
    
    public void setGraphicsConfigurationBounds(Rectangle param1Rectangle) {
      this.gcBounds = (param1Rectangle == null) ? null : new Rectangle(param1Rectangle);
    }
  }
  
  public static class WindowProperty implements Property {
    private void checkComponent(Component param1Component) {
      if (param1Component == null)
        throw new IllegalArgumentException("null component"); 
      if (!(param1Component instanceof Window))
        throw new IllegalArgumentException("invalid component"); 
    }
    
    private int getScreenCount() {
      return (GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()).length;
    }
    
    public Object getSessionState(Component param1Component) {
      checkComponent(param1Component);
      int i = 0;
      if (param1Component instanceof Frame)
        i = ((Frame)param1Component).getExtendedState(); 
      GraphicsConfiguration graphicsConfiguration = param1Component.getGraphicsConfiguration();
      Rectangle rectangle1 = (graphicsConfiguration == null) ? null : graphicsConfiguration.getBounds();
      Rectangle rectangle2 = param1Component.getBounds();
      if (param1Component instanceof JFrame && 0 != (i & 0x6)) {
        String str = "WindowState.normalBounds";
        Object object = ((JFrame)param1Component).getRootPane().getClientProperty(str);
        if (object instanceof Rectangle)
          rectangle2 = (Rectangle)object; 
      } 
      return new SessionStorage.WindowState(rectangle2, rectangle1, getScreenCount(), i);
    }
    
    public void setSessionState(Component param1Component, Object param1Object) {
      checkComponent(param1Component);
      if (param1Object != null && !(param1Object instanceof SessionStorage.WindowState))
        throw new IllegalArgumentException("invalid state"); 
      Window window = (Window)param1Component;
      if (!window.isLocationByPlatform() && param1Object != null) {
        SessionStorage.WindowState windowState = (SessionStorage.WindowState)param1Object;
        Rectangle rectangle1 = windowState.getGraphicsConfigurationBounds();
        int i = windowState.getScreenCount();
        GraphicsConfiguration graphicsConfiguration = param1Component.getGraphicsConfiguration();
        Rectangle rectangle2 = (graphicsConfiguration == null) ? null : graphicsConfiguration.getBounds();
        int j = getScreenCount();
        if (rectangle1 != null && rectangle1.equals(rectangle2) && i == j) {
          boolean bool = true;
          if (window instanceof Frame) {
            bool = ((Frame)window).isResizable();
          } else if (window instanceof Dialog) {
            bool = ((Dialog)window).isResizable();
          } 
          if (bool)
            window.setBounds(windowState.getBounds()); 
        } 
        if (window instanceof Frame)
          ((Frame)window).setExtendedState(windowState.getFrameState()); 
      } 
    }
  }
  
  public static class TabbedPaneState {
    private int selectedIndex;
    
    private int tabCount;
    
    public TabbedPaneState() {
      this.selectedIndex = -1;
      this.tabCount = 0;
    }
    
    public TabbedPaneState(int param1Int1, int param1Int2) {
      if (param1Int2 < 0)
        throw new IllegalArgumentException("invalid tabCount"); 
      if (param1Int1 < -1 || param1Int1 > param1Int2)
        throw new IllegalArgumentException("invalid selectedIndex"); 
      this.selectedIndex = param1Int1;
      this.tabCount = param1Int2;
    }
    
    public int getSelectedIndex() {
      return this.selectedIndex;
    }
    
    public void setSelectedIndex(int param1Int) {
      if (param1Int < -1)
        throw new IllegalArgumentException("invalid selectedIndex"); 
      this.selectedIndex = param1Int;
    }
    
    public int getTabCount() {
      return this.tabCount;
    }
    
    public void setTabCount(int param1Int) {
      if (param1Int < 0)
        throw new IllegalArgumentException("invalid tabCount"); 
      this.tabCount = param1Int;
    }
  }
  
  public static class TabbedPaneProperty implements Property {
    private void checkComponent(Component param1Component) {
      if (param1Component == null)
        throw new IllegalArgumentException("null component"); 
      if (!(param1Component instanceof JTabbedPane))
        throw new IllegalArgumentException("invalid component"); 
    }
    
    public Object getSessionState(Component param1Component) {
      checkComponent(param1Component);
      JTabbedPane jTabbedPane = (JTabbedPane)param1Component;
      return new SessionStorage.TabbedPaneState(jTabbedPane.getSelectedIndex(), jTabbedPane.getTabCount());
    }
    
    public void setSessionState(Component param1Component, Object param1Object) {
      checkComponent(param1Component);
      if (param1Object != null && !(param1Object instanceof SessionStorage.TabbedPaneState))
        throw new IllegalArgumentException("invalid state"); 
      JTabbedPane jTabbedPane = (JTabbedPane)param1Component;
      SessionStorage.TabbedPaneState tabbedPaneState = (SessionStorage.TabbedPaneState)param1Object;
      if (jTabbedPane.getTabCount() == tabbedPaneState.getTabCount())
        jTabbedPane.setSelectedIndex(tabbedPaneState.getSelectedIndex()); 
    }
  }
  
  public static class SplitPaneState {
    private int dividerLocation = -1;
    
    private int orientation = 1;
    
    private void checkOrientation(int param1Int) {
      if (param1Int != 1 && param1Int != 0)
        throw new IllegalArgumentException("invalid orientation"); 
    }
    
    public SplitPaneState() {}
    
    public SplitPaneState(int param1Int1, int param1Int2) {
      checkOrientation(param1Int2);
      if (param1Int1 < -1)
        throw new IllegalArgumentException("invalid dividerLocation"); 
      this.dividerLocation = param1Int1;
      this.orientation = param1Int2;
    }
    
    public int getDividerLocation() {
      return this.dividerLocation;
    }
    
    public void setDividerLocation(int param1Int) {
      if (param1Int < -1)
        throw new IllegalArgumentException("invalid dividerLocation"); 
      this.dividerLocation = param1Int;
    }
    
    public int getOrientation() {
      return this.orientation;
    }
    
    public void setOrientation(int param1Int) {
      checkOrientation(param1Int);
      this.orientation = param1Int;
    }
  }
  
  public static class SplitPaneProperty implements Property {
    private void checkComponent(Component param1Component) {
      if (param1Component == null)
        throw new IllegalArgumentException("null component"); 
      if (!(param1Component instanceof JSplitPane))
        throw new IllegalArgumentException("invalid component"); 
    }
    
    public Object getSessionState(Component param1Component) {
      checkComponent(param1Component);
      JSplitPane jSplitPane = (JSplitPane)param1Component;
      return new SessionStorage.SplitPaneState(jSplitPane.getUI().getDividerLocation(jSplitPane), jSplitPane.getOrientation());
    }
    
    public void setSessionState(Component param1Component, Object param1Object) {
      checkComponent(param1Component);
      if (param1Object != null && !(param1Object instanceof SessionStorage.SplitPaneState))
        throw new IllegalArgumentException("invalid state"); 
      JSplitPane jSplitPane = (JSplitPane)param1Component;
      SessionStorage.SplitPaneState splitPaneState = (SessionStorage.SplitPaneState)param1Object;
      if (jSplitPane.getOrientation() == splitPaneState.getOrientation())
        jSplitPane.setDividerLocation(splitPaneState.getDividerLocation()); 
    }
  }
  
  public static class TableState {
    private int[] columnWidths = new int[0];
    
    private int[] copyColumnWidths(int[] param1ArrayOfint) {
      if (param1ArrayOfint == null)
        throw new IllegalArgumentException("invalid columnWidths"); 
      int[] arrayOfInt = new int[param1ArrayOfint.length];
      System.arraycopy(param1ArrayOfint, 0, arrayOfInt, 0, param1ArrayOfint.length);
      return arrayOfInt;
    }
    
    public TableState() {}
    
    public TableState(int[] param1ArrayOfint) {
      this.columnWidths = copyColumnWidths(param1ArrayOfint);
    }
    
    public int[] getColumnWidths() {
      return copyColumnWidths(this.columnWidths);
    }
    
    public void setColumnWidths(int[] param1ArrayOfint) {
      this.columnWidths = copyColumnWidths(param1ArrayOfint);
    }
  }
  
  public static class TableProperty implements Property {
    private void checkComponent(Component param1Component) {
      if (param1Component == null)
        throw new IllegalArgumentException("null component"); 
      if (!(param1Component instanceof JTable))
        throw new IllegalArgumentException("invalid component"); 
    }
    
    public Object getSessionState(Component param1Component) {
      checkComponent(param1Component);
      JTable jTable = (JTable)param1Component;
      int[] arrayOfInt = new int[jTable.getColumnCount()];
      boolean bool = false;
      for (byte b = 0; b < arrayOfInt.length; b++) {
        TableColumn tableColumn = jTable.getColumnModel().getColumn(b);
        arrayOfInt[b] = tableColumn.getResizable() ? tableColumn.getWidth() : -1;
        if (tableColumn.getResizable())
          bool = true; 
      } 
      return bool ? new SessionStorage.TableState(arrayOfInt) : null;
    }
    
    public void setSessionState(Component param1Component, Object param1Object) {
      checkComponent(param1Component);
      if (!(param1Object instanceof SessionStorage.TableState))
        throw new IllegalArgumentException("invalid state"); 
      JTable jTable = (JTable)param1Component;
      int[] arrayOfInt = ((SessionStorage.TableState)param1Object).getColumnWidths();
      if (jTable.getColumnCount() == arrayOfInt.length)
        for (byte b = 0; b < arrayOfInt.length; b++) {
          if (arrayOfInt[b] != -1) {
            TableColumn tableColumn = jTable.getColumnModel().getColumn(b);
            if (tableColumn.getResizable())
              tableColumn.setPreferredWidth(arrayOfInt[b]); 
          } 
        }  
    }
  }
  
  private void checkClassArg(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("null class"); 
  }
  
  public Property getProperty(Class paramClass) {
    checkClassArg(paramClass);
    while (paramClass != null) {
      Property property = this.propertyMap.get(paramClass);
      if (property != null)
        return property; 
      paramClass = paramClass.getSuperclass();
    } 
    return null;
  }
  
  public void putProperty(Class paramClass, Property paramProperty) {
    checkClassArg(paramClass);
    this.propertyMap.put(paramClass, paramProperty);
  }
  
  public final Property getProperty(Component paramComponent) {
    if (paramComponent == null)
      throw new IllegalArgumentException("null component"); 
    if (paramComponent instanceof Property)
      return (Property)paramComponent; 
    Property property = null;
    if (paramComponent instanceof JComponent) {
      Object object = ((JComponent)paramComponent).getClientProperty(Property.class);
      property = (object instanceof Property) ? (Property)object : null;
    } 
    return (property != null) ? property : getProperty(paramComponent.getClass());
  }
  
  public static interface Property {
    Object getSessionState(Component param1Component);
    
    void setSessionState(Component param1Component, Object param1Object);
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\SessionStorage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */