package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;

public class View extends AbstractBean {
  private static final Logger logger = Logger.getLogger(View.class.getName());
  
  private final Application application;
  
  private ResourceMap resourceMap = null;
  
  private JRootPane rootPane = null;
  
  private JComponent component = null;
  
  private JMenuBar menuBar = null;
  
  private List<JToolBar> toolBars = Collections.emptyList();
  
  private JComponent toolBarsPanel = null;
  
  private JComponent statusBar = null;
  
  public View(Application paramApplication) {
    if (paramApplication == null)
      throw new IllegalArgumentException("null application"); 
    this.application = paramApplication;
  }
  
  public final Application getApplication() {
    return this.application;
  }
  
  public final ApplicationContext getContext() {
    return getApplication().getContext();
  }
  
  public ResourceMap getResourceMap() {
    if (this.resourceMap == null)
      this.resourceMap = getContext().getResourceMap(getClass(), View.class); 
    return this.resourceMap;
  }
  
  public JRootPane getRootPane() {
    if (this.rootPane == null) {
      this.rootPane = new JRootPane();
      this.rootPane.setOpaque(true);
    } 
    return this.rootPane;
  }
  
  private void replaceContentPaneChild(JComponent paramJComponent1, JComponent paramJComponent2, String paramString) {
    Container container = getRootPane().getContentPane();
    if (paramJComponent1 != null)
      container.remove(paramJComponent1); 
    if (paramJComponent2 != null)
      container.add(paramJComponent2, paramString); 
  }
  
  public JComponent getComponent() {
    return this.component;
  }
  
  public void setComponent(JComponent paramJComponent) {
    JComponent jComponent = this.component;
    this.component = paramJComponent;
    replaceContentPaneChild(jComponent, this.component, "Center");
    firePropertyChange("component", jComponent, this.component);
  }
  
  public JMenuBar getMenuBar() {
    return this.menuBar;
  }
  
  public void setMenuBar(JMenuBar paramJMenuBar) {
    JMenuBar jMenuBar = getMenuBar();
    this.menuBar = paramJMenuBar;
    getRootPane().setJMenuBar(paramJMenuBar);
    firePropertyChange("menuBar", jMenuBar, paramJMenuBar);
  }
  
  public List<JToolBar> getToolBars() {
    return this.toolBars;
  }
  
  public void setToolBars(List<JToolBar> paramList) {
    if (paramList == null)
      throw new IllegalArgumentException("null toolbars"); 
    List<JToolBar> list = getToolBars();
    this.toolBars = Collections.unmodifiableList(new ArrayList<JToolBar>(paramList));
    JComponent jComponent1 = this.toolBarsPanel;
    JComponent jComponent2 = null;
    if (this.toolBars.size() == 1) {
      jComponent2 = paramList.get(0);
    } else if (this.toolBars.size() > 1) {
      jComponent2 = new JPanel();
      for (JToolBar jToolBar : this.toolBars)
        jComponent2.add(jToolBar); 
    } 
    replaceContentPaneChild(jComponent1, jComponent2, "North");
    firePropertyChange("toolBars", list, this.toolBars);
  }
  
  public final JToolBar getToolBar() {
    List<JToolBar> list = getToolBars();
    return (list.size() == 0) ? null : list.get(0);
  }
  
  public final void setToolBar(JToolBar paramJToolBar) {
    JToolBar jToolBar = getToolBar();
    List<?> list = Collections.emptyList();
    if (paramJToolBar != null)
      list = Collections.singletonList(paramJToolBar); 
    setToolBars((List)list);
    firePropertyChange("toolBar", jToolBar, paramJToolBar);
  }
  
  public JComponent getStatusBar() {
    return this.statusBar;
  }
  
  public void setStatusBar(JComponent paramJComponent) {
    JComponent jComponent = this.statusBar;
    this.statusBar = paramJComponent;
    replaceContentPaneChild(jComponent, this.statusBar, "South");
    firePropertyChange("statusBar", jComponent, this.statusBar);
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\View.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */