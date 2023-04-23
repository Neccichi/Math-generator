package com.mycompany.boniuk_math.org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

public class AbstractBean {
  private final PropertyChangeSupport pcs = new EDTPropertyChangeSupport(this);
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.pcs.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.pcs.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.pcs.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.pcs.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() {
    return this.pcs.getPropertyChangeListeners();
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject1 != null && paramObject2 != null && paramObject1.equals(paramObject2))
      return; 
    this.pcs.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  protected void firePropertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    this.pcs.firePropertyChange(paramPropertyChangeEvent);
  }
  
  private static class EDTPropertyChangeSupport extends PropertyChangeSupport {
    EDTPropertyChangeSupport(Object param1Object) {
      super(param1Object);
    }
    
    public void firePropertyChange(final PropertyChangeEvent e) {
      if (SwingUtilities.isEventDispatchThread()) {
        super.firePropertyChange(e);
      } else {
        Runnable runnable = new Runnable() {
            public void run() {
              AbstractBean.EDTPropertyChangeSupport.this.firePropertyChange(e);
            }
          };
        SwingUtilities.invokeLater(runnable);
      } 
    }
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\AbstractBean.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */