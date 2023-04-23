package com.mycompany.boniuk_math.org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;

public class ApplicationActionMap extends ActionMap {
  private final ApplicationContext context;
  
  private final ResourceMap resourceMap;
  
  private final Class actionsClass;
  
  private final Object actionsObject;
  
  private final List<ApplicationAction> proxyActions;
  
  public ApplicationActionMap(ApplicationContext paramApplicationContext, Class paramClass, Object paramObject, ResourceMap paramResourceMap) {
    if (paramApplicationContext == null)
      throw new IllegalArgumentException("null context"); 
    if (paramClass == null)
      throw new IllegalArgumentException("null actionsClass"); 
    if (paramObject == null)
      throw new IllegalArgumentException("null actionsObject"); 
    if (!paramClass.isInstance(paramObject))
      throw new IllegalArgumentException("actionsObject not an instanceof actionsClass"); 
    this.context = paramApplicationContext;
    this.actionsClass = paramClass;
    this.actionsObject = paramObject;
    this.resourceMap = paramResourceMap;
    this.proxyActions = new ArrayList<ApplicationAction>();
    addAnnotationActions(paramResourceMap);
    maybeAddActionsPCL();
  }
  
  public final ApplicationContext getContext() {
    return this.context;
  }
  
  public final Class getActionsClass() {
    return this.actionsClass;
  }
  
  public final Object getActionsObject() {
    return this.actionsObject;
  }
  
  public List<ApplicationAction> getProxyActions() {
    ArrayList<ApplicationAction> arrayList = new ArrayList<ApplicationAction>(this.proxyActions);
    ActionMap actionMap = getParent();
    while (actionMap != null) {
      if (actionMap instanceof ApplicationActionMap)
        arrayList.addAll(((ApplicationActionMap)actionMap).proxyActions); 
      actionMap = actionMap.getParent();
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  private String aString(String paramString1, String paramString2) {
    return (paramString1.length() == 0) ? paramString2 : paramString1;
  }
  
  private void putAction(String paramString, ApplicationAction paramApplicationAction) {
    if (get(paramString) != null);
    put(paramString, paramApplicationAction);
  }
  
  private void addAnnotationActions(ResourceMap paramResourceMap) {
    Class clazz = getActionsClass();
    for (Method method : clazz.getDeclaredMethods()) {
      Action action = method.<Action>getAnnotation(Action.class);
      if (action != null) {
        String str1 = method.getName();
        String str2 = aString(action.enabledProperty(), (String)null);
        String str3 = aString(action.selectedProperty(), (String)null);
        String str4 = aString(action.name(), str1);
        Task.BlockingScope blockingScope = action.block();
        ApplicationAction applicationAction = new ApplicationAction(this, paramResourceMap, str4, method, str2, str3, blockingScope);
        putAction(str4, applicationAction);
      } 
    } 
    ProxyActions proxyActions = (ProxyActions)clazz.getAnnotation(ProxyActions.class);
    if (proxyActions != null)
      for (String str : proxyActions.value()) {
        ApplicationAction applicationAction = new ApplicationAction(this, paramResourceMap, str);
        applicationAction.setEnabled(false);
        putAction(str, applicationAction);
        this.proxyActions.add(applicationAction);
      }  
  }
  
  private void maybeAddActionsPCL() {
    boolean bool = false;
    Object[] arrayOfObject = keys();
    if (arrayOfObject != null) {
      for (Object object : arrayOfObject) {
        Action action = get(object);
        if (action instanceof ApplicationAction) {
          ApplicationAction applicationAction = (ApplicationAction)action;
          if (applicationAction.getEnabledProperty() != null || applicationAction.getSelectedProperty() != null) {
            bool = true;
            break;
          } 
        } 
      } 
      if (bool)
        try {
          Class clazz = getActionsClass();
          Method method = clazz.getMethod("addPropertyChangeListener", new Class[] { PropertyChangeListener.class });
          method.invoke(getActionsObject(), new Object[] { new ActionsPCL() });
        } catch (Exception exception) {
          String str = "addPropertyChangeListener undefined " + this.actionsClass;
          throw new Error(str, exception);
        }  
    } 
  }
  
  private class ActionsPCL implements PropertyChangeListener {
    private ActionsPCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Object[] arrayOfObject = ApplicationActionMap.this.keys();
      if (arrayOfObject != null)
        for (Object object : arrayOfObject) {
          Action action = ApplicationActionMap.this.get(object);
          if (action instanceof ApplicationAction) {
            ApplicationAction applicationAction = (ApplicationAction)action;
            if (str.equals(applicationAction.getEnabledProperty())) {
              applicationAction.forwardPropertyChangeEvent(param1PropertyChangeEvent, "enabled");
            } else if (str.equals(applicationAction.getSelectedProperty())) {
              applicationAction.forwardPropertyChangeEvent(param1PropertyChangeEvent, "selected");
            } 
          } 
        }  
    }
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ApplicationActionMap.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */