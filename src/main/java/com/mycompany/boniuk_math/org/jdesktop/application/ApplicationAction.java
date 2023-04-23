package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.KeyStroke;

public class ApplicationAction extends AbstractAction {
  private static final Logger logger = Logger.getLogger(ApplicationAction.class.getName());
  
  private final ApplicationActionMap appAM;
  
  private final ResourceMap resourceMap;
  
  private final String actionName;
  
  private final Method actionMethod;
  
  private final String enabledProperty;
  
  private final Method isEnabledMethod;
  
  private final Method setEnabledMethod;
  
  private final String selectedProperty;
  
  private final Method isSelectedMethod;
  
  private final Method setSelectedMethod;
  
  private final Task.BlockingScope block;
  
  private Action proxy = null;
  
  private Object proxySource = null;
  
  private PropertyChangeListener proxyPCL = null;
  
  private static final String SELECTED_KEY = "SwingSelectedKey";
  
  private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
  
  private static final String LARGE_ICON_KEY = "SwingLargeIconKey";
  
  public ApplicationAction(ApplicationActionMap paramApplicationActionMap, ResourceMap paramResourceMap, String paramString1, Method paramMethod, String paramString2, String paramString3, Task.BlockingScope paramBlockingScope) {
    if (paramApplicationActionMap == null)
      throw new IllegalArgumentException("null appAM"); 
    if (paramString1 == null)
      throw new IllegalArgumentException("null baseName"); 
    this.appAM = paramApplicationActionMap;
    this.resourceMap = paramResourceMap;
    this.actionName = paramString1;
    this.actionMethod = paramMethod;
    this.enabledProperty = paramString2;
    this.selectedProperty = paramString3;
    this.block = paramBlockingScope;
    if (paramString2 != null) {
      this.setEnabledMethod = propertySetMethod(paramString2, boolean.class);
      this.isEnabledMethod = propertyGetMethod(paramString2);
      if (this.isEnabledMethod == null)
        throw newNoSuchPropertyException(paramString2); 
    } else {
      this.isEnabledMethod = null;
      this.setEnabledMethod = null;
    } 
    if (paramString3 != null) {
      this.setSelectedMethod = propertySetMethod(paramString3, boolean.class);
      this.isSelectedMethod = propertyGetMethod(paramString3);
      if (this.isSelectedMethod == null)
        throw newNoSuchPropertyException(paramString3); 
      super.putValue("SwingSelectedKey", Boolean.FALSE);
    } else {
      this.isSelectedMethod = null;
      this.setSelectedMethod = null;
    } 
    if (paramResourceMap != null)
      initActionProperties(paramResourceMap, paramString1); 
  }
  
  ApplicationAction(ApplicationActionMap paramApplicationActionMap, ResourceMap paramResourceMap, String paramString) {
    this(paramApplicationActionMap, paramResourceMap, paramString, (Method)null, (String)null, (String)null, Task.BlockingScope.NONE);
  }
  
  private IllegalArgumentException newNoSuchPropertyException(String paramString) {
    String str1 = this.appAM.getActionsClass().getName();
    String str2 = String.format("no property named %s in %s", new Object[] { paramString, str1 });
    return new IllegalArgumentException(str2);
  }
  
  String getEnabledProperty() {
    return this.enabledProperty;
  }
  
  String getSelectedProperty() {
    return this.selectedProperty;
  }
  
  public Action getProxy() {
    return this.proxy;
  }
  
  public void setProxy(Action paramAction) {
    Action action = this.proxy;
    this.proxy = paramAction;
    if (action != null) {
      action.removePropertyChangeListener(this.proxyPCL);
      this.proxyPCL = null;
    } 
    if (this.proxy != null) {
      updateProxyProperties();
      this.proxyPCL = new ProxyPCL();
      paramAction.addPropertyChangeListener(this.proxyPCL);
    } else if (action != null) {
      setEnabled(false);
      setSelected(false);
    } 
    firePropertyChange("proxy", action, this.proxy);
  }
  
  public Object getProxySource() {
    return this.proxySource;
  }
  
  public void setProxySource(Object paramObject) {
    Object object = this.proxySource;
    this.proxySource = paramObject;
    firePropertyChange("proxySource", object, this.proxySource);
  }
  
  private void maybePutDescriptionValue(String paramString, Action paramAction) {
    Object object = paramAction.getValue(paramString);
    if (object instanceof String)
      putValue(paramString, object); 
  }
  
  private void updateProxyProperties() {
    Action action = getProxy();
    if (action != null) {
      setEnabled(action.isEnabled());
      Object object = action.getValue("SwingSelectedKey");
      setSelected((object instanceof Boolean && ((Boolean)object).booleanValue()));
      maybePutDescriptionValue("ShortDescription", action);
      maybePutDescriptionValue("LongDescription", action);
    } 
  }
  
  private class ProxyPCL implements PropertyChangeListener {
    private ProxyPCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == null || "enabled".equals(str) || "selected".equals(str) || "ShortDescription".equals(str) || "LongDescription".equals(str))
        ApplicationAction.this.updateProxyProperties(); 
    }
  }
  
  private void initActionProperties(ResourceMap paramResourceMap, String paramString) {
    boolean bool = false;
    Object object = null;
    String str = paramResourceMap.getString(paramString + ".Action.text", new Object[0]);
    if (str != null) {
      MnemonicText.configure(this, str);
      bool = true;
    } 
    Integer integer1 = paramResourceMap.getKeyCode(paramString + ".Action.mnemonic");
    if (integer1 != null)
      putValue("MnemonicKey", integer1); 
    Integer integer2 = paramResourceMap.getInteger(paramString + ".Action.displayedMnemonicIndex");
    if (integer2 != null)
      putValue("SwingDisplayedMnemonicIndexKey", integer2); 
    KeyStroke keyStroke = paramResourceMap.getKeyStroke(paramString + ".Action.accelerator");
    if (keyStroke != null)
      putValue("AcceleratorKey", keyStroke); 
    Icon icon1 = paramResourceMap.getIcon(paramString + ".Action.icon");
    if (icon1 != null) {
      putValue("SmallIcon", icon1);
      putValue("SwingLargeIconKey", icon1);
      bool = true;
    } 
    Icon icon2 = paramResourceMap.getIcon(paramString + ".Action.smallIcon");
    if (icon2 != null) {
      putValue("SmallIcon", icon2);
      bool = true;
    } 
    Icon icon3 = paramResourceMap.getIcon(paramString + ".Action.largeIcon");
    if (icon3 != null) {
      putValue("SwingLargeIconKey", icon3);
      bool = true;
    } 
    putValue("ShortDescription", paramResourceMap.getString(paramString + ".Action.shortDescription", new Object[0]));
    putValue("LongDescription", paramResourceMap.getString(paramString + ".Action.longDescription", new Object[0]));
    putValue("ActionCommandKey", paramResourceMap.getString(paramString + ".Action.command", new Object[0]));
    if (!bool)
      putValue("Name", this.actionName); 
  }
  
  private String propertyMethodName(String paramString1, String paramString2) {
    return paramString1 + paramString2.substring(0, 1).toUpperCase() + paramString2.substring(1);
  }
  
  private Method propertyGetMethod(String paramString) {
    String[] arrayOfString = { propertyMethodName("is", paramString), propertyMethodName("get", paramString) };
    Class clazz = this.appAM.getActionsClass();
    for (String str : arrayOfString) {
      try {
        return clazz.getMethod(str, new Class[0]);
      } catch (NoSuchMethodException noSuchMethodException) {}
    } 
    return null;
  }
  
  private Method propertySetMethod(String paramString, Class paramClass) {
    Class clazz = this.appAM.getActionsClass();
    try {
      return clazz.getMethod(propertyMethodName("set", paramString), new Class[] { paramClass });
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  public String getName() {
    return this.actionName;
  }
  
  public ResourceMap getResourceMap() {
    return this.resourceMap;
  }
  
  protected Object getActionArgument(Class<ActionEvent> paramClass, String paramString, ActionEvent paramActionEvent) {
    Application application;
    ActionEvent actionEvent = null;
    if (paramClass == ActionEvent.class) {
      actionEvent = paramActionEvent;
    } else if (paramClass == Action.class) {
      ApplicationAction applicationAction = this;
    } else if (paramClass == ActionMap.class) {
      ApplicationActionMap applicationActionMap = this.appAM;
    } else if (paramClass == ResourceMap.class) {
      ResourceMap resourceMap = this.resourceMap;
    } else if (paramClass == ApplicationContext.class) {
      ApplicationContext applicationContext = this.appAM.getContext();
    } else if (paramClass == Application.class) {
      application = this.appAM.getContext().getApplication();
    } else {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("unrecognized @Action method parameter");
      actionFailed(paramActionEvent, illegalArgumentException);
    } 
    return application;
  }
  
  private Task.InputBlocker createInputBlocker(Task paramTask, ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (this.block == Task.BlockingScope.ACTION)
      object = this; 
    return new DefaultInputBlocker(paramTask, this.block, object, this);
  }
  
  private void noProxyActionPerformed(ActionEvent paramActionEvent) {
    Object object = null;
    Annotation[][] arrayOfAnnotation = this.actionMethod.getParameterAnnotations();
    Class[] arrayOfClass = this.actionMethod.getParameterTypes();
    Object[] arrayOfObject = new Object[arrayOfClass.length];
    for (byte b = 0; b < arrayOfClass.length; b++) {
      String str = null;
      for (Annotation annotation : arrayOfAnnotation[b]) {
        if (annotation instanceof Action.Parameter) {
          str = ((Action.Parameter)annotation).value();
          break;
        } 
      } 
      arrayOfObject[b] = getActionArgument(arrayOfClass[b], str, paramActionEvent);
    } 
    try {
      Object object1 = this.appAM.getActionsObject();
      object = this.actionMethod.invoke(object1, arrayOfObject);
    } catch (Exception exception) {
      actionFailed(paramActionEvent, exception);
    } 
    if (object instanceof Task) {
      Task task = (Task)object;
      if (task.getInputBlocker() == null)
        task.setInputBlocker(createInputBlocker(task, paramActionEvent)); 
      ApplicationContext applicationContext = this.appAM.getContext();
      applicationContext.getTaskService().execute(task);
    } 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Action action = getProxy();
    if (action != null) {
      paramActionEvent.setSource(getProxySource());
      action.actionPerformed(paramActionEvent);
    } else if (this.actionMethod != null) {
      noProxyActionPerformed(paramActionEvent);
    } 
  }
  
  public boolean isEnabled() {
    if (getProxy() != null || this.isEnabledMethod == null)
      return super.isEnabled(); 
    try {
      Object object = this.isEnabledMethod.invoke(this.appAM.getActionsObject(), new Object[0]);
      return ((Boolean)object).booleanValue();
    } catch (Exception exception) {
      throw newInvokeError(this.isEnabledMethod, exception, new Object[0]);
    } 
  }
  
  public void setEnabled(boolean paramBoolean) {
    if (getProxy() != null || this.setEnabledMethod == null) {
      super.setEnabled(paramBoolean);
    } else {
      try {
        this.setEnabledMethod.invoke(this.appAM.getActionsObject(), new Object[] { Boolean.valueOf(paramBoolean) });
      } catch (Exception exception) {
        throw newInvokeError(this.setEnabledMethod, exception, new Object[] { Boolean.valueOf(paramBoolean) });
      } 
    } 
  }
  
  public boolean isSelected() {
    if (getProxy() != null || this.isSelectedMethod == null) {
      Object object = getValue("SwingSelectedKey");
      return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : false;
    } 
    try {
      Object object = this.isSelectedMethod.invoke(this.appAM.getActionsObject(), new Object[0]);
      return ((Boolean)object).booleanValue();
    } catch (Exception exception) {
      throw newInvokeError(this.isSelectedMethod, exception, new Object[0]);
    } 
  }
  
  public void setSelected(boolean paramBoolean) {
    if (getProxy() != null || this.setSelectedMethod == null) {
      super.putValue("SwingSelectedKey", Boolean.valueOf(paramBoolean));
    } else {
      try {
        super.putValue("SwingSelectedKey", Boolean.valueOf(paramBoolean));
        if (paramBoolean != isSelected())
          this.setSelectedMethod.invoke(this.appAM.getActionsObject(), new Object[] { Boolean.valueOf(paramBoolean) }); 
      } catch (Exception exception) {
        throw newInvokeError(this.setSelectedMethod, exception, new Object[] { Boolean.valueOf(paramBoolean) });
      } 
    } 
  }
  
  public void putValue(String paramString, Object paramObject) {
    if ("SwingSelectedKey".equals(paramString) && paramObject instanceof Boolean) {
      setSelected(((Boolean)paramObject).booleanValue());
    } else {
      super.putValue(paramString, paramObject);
    } 
  }
  
  private Error newInvokeError(Method paramMethod, Exception paramException, Object... paramVarArgs) {
    String str1 = (paramVarArgs.length == 0) ? "" : paramVarArgs[0].toString();
    for (byte b = 1; b < paramVarArgs.length; b++)
      str1 = str1 + ", " + paramVarArgs[b]; 
    String str2 = this.appAM.getActionsObject().getClass().getName();
    String str3 = String.format("%s.%s(%s) failed", new Object[] { str2, paramMethod, str1 });
    return new Error(str3, paramException);
  }
  
  void forwardPropertyChangeEvent(PropertyChangeEvent paramPropertyChangeEvent, String paramString) {
    if ("selected".equals(paramString) && paramPropertyChangeEvent.getNewValue() instanceof Boolean)
      putValue("SwingSelectedKey", paramPropertyChangeEvent.getNewValue()); 
    firePropertyChange(paramString, paramPropertyChangeEvent.getOldValue(), paramPropertyChangeEvent.getNewValue());
  }
  
  private void actionFailed(ActionEvent paramActionEvent, Exception paramException) {
    throw new Error(paramException);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getClass().getName());
    stringBuilder.append(" ");
    boolean bool = isEnabled();
    if (!bool)
      stringBuilder.append("("); 
    stringBuilder.append(getName());
    Object object1 = getValue("SwingSelectedKey");
    if (object1 instanceof Boolean && (
      (Boolean)object1).booleanValue())
      stringBuilder.append("+"); 
    if (!bool)
      stringBuilder.append(")"); 
    Object object2 = getValue("Name");
    if (object2 instanceof String) {
      stringBuilder.append(" \"");
      stringBuilder.append((String)object2);
      stringBuilder.append("\"");
    } 
    this.proxy = getProxy();
    if (this.proxy != null) {
      stringBuilder.append(" Proxy for: ");
      stringBuilder.append(this.proxy.toString());
    } 
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ApplicationAction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */