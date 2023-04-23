package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

public class ActionManager extends AbstractBean {
  private static final Logger logger = Logger.getLogger(ActionManager.class.getName());
  
  private final ApplicationContext context;
  
  private final WeakHashMap<Object, WeakReference<ApplicationActionMap>> actionMaps;
  
  private ApplicationActionMap globalActionMap = null;
  
  protected ActionManager(ApplicationContext paramApplicationContext) {
    if (paramApplicationContext == null)
      throw new IllegalArgumentException("null context"); 
    this.context = paramApplicationContext;
    this.actionMaps = new WeakHashMap<Object, WeakReference<ApplicationActionMap>>();
  }
  
  protected final ApplicationContext getContext() {
    return this.context;
  }
  
  private ApplicationActionMap createActionMapChain(Class<?> paramClass1, Class paramClass2, Object paramObject, ResourceMap paramResourceMap) {
    ArrayList<Class<?>> arrayList = new ArrayList();
    for (Class<?> clazz = paramClass1;; clazz = clazz.getSuperclass()) {
      arrayList.add(clazz);
      if (clazz.equals(paramClass2))
        break; 
    } 
    Collections.reverse(arrayList);
    ApplicationContext applicationContext = getContext();
    ActionMap actionMap = null;
    for (Class<?> clazz1 : arrayList) {
      ApplicationActionMap applicationActionMap = new ApplicationActionMap(applicationContext, clazz1, paramObject, paramResourceMap);
      applicationActionMap.setParent(actionMap);
      actionMap = applicationActionMap;
    } 
    return (ApplicationActionMap)actionMap;
  }
  
  public ApplicationActionMap getActionMap() {
    if (this.globalActionMap == null) {
      ApplicationContext applicationContext = getContext();
      Application application = applicationContext.getApplication();
      Class clazz = applicationContext.getApplicationClass();
      ResourceMap resourceMap = applicationContext.getResourceMap();
      this.globalActionMap = createActionMapChain(clazz, Application.class, application, resourceMap);
      initProxyActionSupport();
    } 
    return this.globalActionMap;
  }
  
  private void initProxyActionSupport() {
    KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    keyboardFocusManager.addPropertyChangeListener(new KeyboardFocusPCL());
  }
  
  public ApplicationActionMap getActionMap(Class paramClass, Object paramObject) {
    if (paramClass == null)
      throw new IllegalArgumentException("null actionsClass"); 
    if (paramObject == null)
      throw new IllegalArgumentException("null actionsObject"); 
    if (!paramClass.isAssignableFrom(paramObject.getClass()))
      throw new IllegalArgumentException("actionsObject not instanceof actionsClass"); 
    synchronized (this.actionMaps) {
      WeakReference<ApplicationActionMap> weakReference = this.actionMaps.get(paramObject);
      ApplicationActionMap applicationActionMap = (weakReference != null) ? weakReference.get() : null;
      if (applicationActionMap == null || applicationActionMap.getActionsClass() != paramClass) {
        ApplicationContext applicationContext = getContext();
        Class<?> clazz = paramObject.getClass();
        ResourceMap resourceMap = applicationContext.getResourceMap(clazz, paramClass);
        applicationActionMap = createActionMapChain(clazz, paramClass, paramObject, resourceMap);
        ActionMap actionMap = applicationActionMap;
        while (actionMap.getParent() != null)
          actionMap = actionMap.getParent(); 
        actionMap.setParent(getActionMap());
        this.actionMaps.put(paramObject, new WeakReference<ApplicationActionMap>(applicationActionMap));
      } 
      return applicationActionMap;
    } 
  }
  
  private final class KeyboardFocusPCL implements PropertyChangeListener {
    private final TextActions textActions = new TextActions(ActionManager.this.getContext());
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getPropertyName() == "permanentFocusOwner") {
        JComponent jComponent1 = ActionManager.this.getContext().getFocusOwner();
        Object object = param1PropertyChangeEvent.getNewValue();
        JComponent jComponent2 = (object instanceof JComponent) ? (JComponent)object : null;
        this.textActions.updateFocusOwner(jComponent1, jComponent2);
        ActionManager.this.getContext().setFocusOwner(jComponent2);
        ActionManager.this.updateAllProxyActions(jComponent1, jComponent2);
      } 
    }
  }
  
  private void updateAllProxyActions(JComponent paramJComponent1, JComponent paramJComponent2) {
    if (paramJComponent2 != null) {
      ActionMap actionMap = paramJComponent2.getActionMap();
      if (actionMap != null) {
        updateProxyActions(getActionMap(), actionMap, paramJComponent2);
        for (WeakReference<ApplicationActionMap> weakReference : this.actionMaps.values()) {
          ApplicationActionMap applicationActionMap = weakReference.get();
          if (applicationActionMap == null)
            continue; 
          updateProxyActions(applicationActionMap, actionMap, paramJComponent2);
        } 
      } 
    } 
  }
  
  private void updateProxyActions(ApplicationActionMap paramApplicationActionMap, ActionMap paramActionMap, JComponent paramJComponent) {
    for (ApplicationAction applicationAction : paramApplicationActionMap.getProxyActions()) {
      String str = applicationAction.getName();
      Action action = paramActionMap.get(str);
      if (action != null) {
        applicationAction.setProxy(action);
        applicationAction.setProxySource(paramJComponent);
        continue;
      } 
      applicationAction.setProxy(null);
      applicationAction.setProxySource(null);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ActionManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */