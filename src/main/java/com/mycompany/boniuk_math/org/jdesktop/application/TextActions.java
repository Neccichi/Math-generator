package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

class TextActions extends AbstractBean {
  private final ApplicationContext context;
  
  private final CaretListener textComponentCaretListener;
  
  private final PropertyChangeListener textComponentPCL;
  
  private final String markerActionKey = "TextActions.markerAction";
  
  private final Action markerAction;
  
  private boolean copyEnabled = false;
  
  private boolean cutEnabled = false;
  
  private boolean pasteEnabled = false;
  
  private boolean deleteEnabled = false;
  
  public TextActions(ApplicationContext paramApplicationContext) {
    this.context = paramApplicationContext;
    this.markerAction = new AbstractAction() {
        public void actionPerformed(ActionEvent param1ActionEvent) {}
      };
    this.textComponentCaretListener = new TextComponentCaretListener();
    this.textComponentPCL = new TextComponentPCL();
    getClipboard().addFlavorListener(new ClipboardListener());
  }
  
  private ApplicationContext getContext() {
    return this.context;
  }
  
  private JComponent getFocusOwner() {
    return getContext().getFocusOwner();
  }
  
  private Clipboard getClipboard() {
    return getContext().getClipboard();
  }
  
  void updateFocusOwner(JComponent paramJComponent1, JComponent paramJComponent2) {
    if (paramJComponent1 instanceof JTextComponent) {
      JTextComponent jTextComponent = (JTextComponent)paramJComponent1;
      jTextComponent.removeCaretListener(this.textComponentCaretListener);
      jTextComponent.removePropertyChangeListener(this.textComponentPCL);
    } 
    if (paramJComponent2 instanceof JTextComponent) {
      JTextComponent jTextComponent = (JTextComponent)paramJComponent2;
      maybeInstallTextActions(jTextComponent);
      updateTextActions(jTextComponent);
      jTextComponent.addCaretListener(this.textComponentCaretListener);
      jTextComponent.addPropertyChangeListener(this.textComponentPCL);
    } else if (paramJComponent2 == null) {
      setCopyEnabled(false);
      setCutEnabled(false);
      setPasteEnabled(false);
      setDeleteEnabled(false);
    } 
  }
  
  private final class ClipboardListener implements FlavorListener {
    private ClipboardListener() {}
    
    public void flavorsChanged(FlavorEvent param1FlavorEvent) {
      JComponent jComponent = TextActions.this.getFocusOwner();
      if (jComponent instanceof JTextComponent)
        TextActions.this.updateTextActions((JTextComponent)jComponent); 
    }
  }
  
  private final class TextComponentCaretListener implements CaretListener {
    private TextComponentCaretListener() {}
    
    public void caretUpdate(CaretEvent param1CaretEvent) {
      TextActions.this.updateTextActions((JTextComponent)param1CaretEvent.getSource());
    }
  }
  
  private final class TextComponentPCL implements PropertyChangeListener {
    private TextComponentPCL() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == null || "editable".equals(str))
        TextActions.this.updateTextActions((JTextComponent)param1PropertyChangeEvent.getSource()); 
    }
  }
  
  private void updateTextActions(JTextComponent paramJTextComponent) {
    Caret caret = paramJTextComponent.getCaret();
    boolean bool = (caret.getDot() != caret.getMark()) ? true : false;
    boolean bool1 = paramJTextComponent.isEditable();
    boolean bool2 = getClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
    setCopyEnabled(bool);
    setCutEnabled((bool1 && bool));
    setDeleteEnabled((bool1 && bool));
    setPasteEnabled((bool1 && bool2));
  }
  
  private void maybeInstallTextActions(JTextComponent paramJTextComponent) {
    ActionMap actionMap = paramJTextComponent.getActionMap();
    if (actionMap.get("TextActions.markerAction") == null) {
      actionMap.put("TextActions.markerAction", this.markerAction);
      ApplicationActionMap applicationActionMap = getContext().getActionMap(getClass(), this);
      for (Object object : applicationActionMap.keys())
        actionMap.put(object, applicationActionMap.get(object)); 
    } 
  }
  
  private int getCurrentEventModifiers() {
    int i = 0;
    AWTEvent aWTEvent = EventQueue.getCurrentEvent();
    if (aWTEvent instanceof InputEvent) {
      i = ((InputEvent)aWTEvent).getModifiers();
    } else if (aWTEvent instanceof ActionEvent) {
      i = ((ActionEvent)aWTEvent).getModifiers();
    } 
    return i;
  }
  
  private void invokeTextAction(JTextComponent paramJTextComponent, String paramString) {
    ActionMap actionMap = paramJTextComponent.getActionMap().getParent();
    long l = EventQueue.getMostRecentEventTime();
    int i = getCurrentEventModifiers();
    ActionEvent actionEvent = new ActionEvent(paramJTextComponent, 1001, paramString, l, i);
    actionMap.get(paramString).actionPerformed(actionEvent);
  }
  
  @Action(enabledProperty = "cutEnabled")
  public void cut(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object instanceof JTextComponent)
      invokeTextAction((JTextComponent)object, "cut"); 
  }
  
  public boolean isCutEnabled() {
    return this.cutEnabled;
  }
  
  public void setCutEnabled(boolean paramBoolean) {
    boolean bool = this.cutEnabled;
    this.cutEnabled = paramBoolean;
    firePropertyChange("cutEnabled", Boolean.valueOf(bool), Boolean.valueOf(this.cutEnabled));
  }
  
  @Action(enabledProperty = "copyEnabled")
  public void copy(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object instanceof JTextComponent)
      invokeTextAction((JTextComponent)object, "copy"); 
  }
  
  public boolean isCopyEnabled() {
    return this.copyEnabled;
  }
  
  public void setCopyEnabled(boolean paramBoolean) {
    boolean bool = this.copyEnabled;
    this.copyEnabled = paramBoolean;
    firePropertyChange("copyEnabled", Boolean.valueOf(bool), Boolean.valueOf(this.copyEnabled));
  }
  
  @Action(enabledProperty = "pasteEnabled")
  public void paste(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object instanceof JTextComponent)
      invokeTextAction((JTextComponent)object, "paste"); 
  }
  
  public boolean isPasteEnabled() {
    return this.pasteEnabled;
  }
  
  public void setPasteEnabled(boolean paramBoolean) {
    boolean bool = this.pasteEnabled;
    this.pasteEnabled = paramBoolean;
    firePropertyChange("pasteEnabled", Boolean.valueOf(bool), Boolean.valueOf(this.pasteEnabled));
  }
  
  @Action(enabledProperty = "deleteEnabled")
  public void delete(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object instanceof JTextComponent)
      invokeTextAction((JTextComponent)object, "delete-next"); 
  }
  
  public boolean isDeleteEnabled() {
    return this.deleteEnabled;
  }
  
  public void setDeleteEnabled(boolean paramBoolean) {
    boolean bool = this.deleteEnabled;
    this.deleteEnabled = paramBoolean;
    firePropertyChange("deleteEnabled", Boolean.valueOf(bool), Boolean.valueOf(this.deleteEnabled));
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\TextActions.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */