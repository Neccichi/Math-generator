package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.RootPaneContainer;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

final class DefaultInputBlocker extends Task.InputBlocker {
  private static final Logger logger = Logger.getLogger(DefaultInputBlocker.class.getName());
  
  private JDialog modalDialog = null;
  
  DefaultInputBlocker(Task paramTask, Task.BlockingScope paramBlockingScope, Object paramObject, ApplicationAction paramApplicationAction) {
    super(paramTask, paramBlockingScope, paramObject, paramApplicationAction);
  }
  
  private void setActionTargetBlocked(boolean paramBoolean) {
    Action action = (Action)getTarget();
    action.setEnabled(!paramBoolean);
  }
  
  private void setComponentTargetBlocked(boolean paramBoolean) {
    Component component = (Component)getTarget();
    component.setEnabled(!paramBoolean);
  }
  
  private void blockingDialogComponents(Component paramComponent, List<Component> paramList) {
    String str = paramComponent.getName();
    if (str != null && str.startsWith("BlockingDialog"))
      paramList.add(paramComponent); 
    if (paramComponent instanceof Container)
      for (Component component : ((Container)paramComponent).getComponents())
        blockingDialogComponents(component, paramList);  
  }
  
  private List<Component> blockingDialogComponents(Component paramComponent) {
    ArrayList<Component> arrayList = new ArrayList();
    blockingDialogComponents(paramComponent, arrayList);
    return arrayList;
  }
  
  private void injectBlockingDialogComponents(Component paramComponent) {
    ResourceMap resourceMap = getTask().getResourceMap();
    if (resourceMap != null)
      resourceMap.injectComponents(paramComponent); 
    ApplicationAction applicationAction = getAction();
    if (applicationAction != null) {
      ResourceMap resourceMap1 = applicationAction.getResourceMap();
      String str = applicationAction.getName();
      for (Component component : blockingDialogComponents(paramComponent))
        component.setName(str + "." + component.getName()); 
      resourceMap1.injectComponents(paramComponent);
    } 
  }
  
  private JDialog createBlockingDialog() {
    JOptionPane jOptionPane = new JOptionPane();
    if (getTask().getUserCanCancel()) {
      JButton jButton = new JButton();
      jButton.setName("BlockingDialog.cancelButton");
      ActionListener actionListener = new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            DefaultInputBlocker.this.getTask().cancel(true);
          }
        };
      jButton.addActionListener(actionListener);
      jOptionPane.setOptions(new Object[] { jButton });
    } else {
      jOptionPane.setOptions(new Object[0]);
    } 
    Component component = (Component)getTarget();
    String str1 = getTask().getTitle();
    String str2 = (str1 == null) ? "BlockingDialog" : str1;
    final JDialog dialog = jOptionPane.createDialog(component, str2);
    jDialog.setModal(true);
    jDialog.setName("BlockingDialog");
    jDialog.setDefaultCloseOperation(0);
    WindowAdapter windowAdapter = new WindowAdapter() {
        public void windowClosing(WindowEvent param1WindowEvent) {
          if (DefaultInputBlocker.this.getTask().getUserCanCancel()) {
            DefaultInputBlocker.this.getTask().cancel(true);
            dialog.setVisible(false);
          } 
        }
      };
    jDialog.addWindowListener(windowAdapter);
    jOptionPane.setName("BlockingDialog.optionPane");
    injectBlockingDialogComponents(jDialog);
    recreateOptionPaneMessage(jOptionPane);
    jDialog.pack();
    return jDialog;
  }
  
  private void recreateOptionPaneMessage(JOptionPane paramJOptionPane) {
    Object object = paramJOptionPane.getMessage();
    if (object instanceof String) {
      Font font = paramJOptionPane.getFont();
      final JTextArea textArea = new JTextArea((String)object);
      jTextArea.setFont(font);
      int i = jTextArea.getFontMetrics(font).getHeight();
      Insets insets = new Insets(0, 0, i, 24);
      jTextArea.setMargin(insets);
      jTextArea.setEditable(false);
      jTextArea.setWrapStyleWord(true);
      jTextArea.setBackground(paramJOptionPane.getBackground());
      JPanel jPanel = new JPanel(new BorderLayout());
      jPanel.add(jTextArea, "Center");
      final JProgressBar progressBar = new JProgressBar();
      jProgressBar.setName("BlockingDialog.progressBar");
      jProgressBar.setIndeterminate(true);
      PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            if ("progress".equals(param1PropertyChangeEvent.getPropertyName())) {
              progressBar.setIndeterminate(false);
              progressBar.setValue(((Integer)param1PropertyChangeEvent.getNewValue()).intValue());
              DefaultInputBlocker.this.updateStatusBarString(progressBar);
            } else if ("message".equals(param1PropertyChangeEvent.getPropertyName())) {
              textArea.setText((String)param1PropertyChangeEvent.getNewValue());
            } 
          }
        };
      getTask().addPropertyChangeListener(propertyChangeListener);
      jPanel.add(jProgressBar, "South");
      injectBlockingDialogComponents(jPanel);
      paramJOptionPane.setMessage(jPanel);
    } 
  }
  
  private void updateStatusBarString(JProgressBar paramJProgressBar) {
    if (!paramJProgressBar.isStringPainted())
      return; 
    String str1 = "progressBarStringFormat";
    if (paramJProgressBar.getClientProperty(str1) == null)
      paramJProgressBar.putClientProperty(str1, paramJProgressBar.getString()); 
    String str2 = (String)paramJProgressBar.getClientProperty(str1);
    if (paramJProgressBar.getValue() <= 0) {
      paramJProgressBar.setString("");
    } else if (str2 == null) {
      paramJProgressBar.setString((String)null);
    } else {
      double d = paramJProgressBar.getValue() / 100.0D;
      long l1 = getTask().getExecutionDuration(TimeUnit.SECONDS);
      long l2 = l1 / 60L;
      long l3 = (long)(0.5D + l1 / d) - l1;
      long l4 = l3 / 60L;
      String str = String.format(str2, new Object[] { Long.valueOf(l2), Long.valueOf(l1 - l2 * 60L), Long.valueOf(l4), Long.valueOf(l3 - l4 * 60L) });
      paramJProgressBar.setString(str);
    } 
  }
  
  private void showBusyGlassPane(boolean paramBoolean) {
    RootPaneContainer rootPaneContainer = null;
    Component component = (Component)getTarget();
    while (component != null) {
      if (component instanceof RootPaneContainer) {
        rootPaneContainer = (RootPaneContainer)component;
        break;
      } 
      component = component.getParent();
    } 
    if (rootPaneContainer != null)
      if (paramBoolean) {
        JMenuBar jMenuBar = rootPaneContainer.getRootPane().getJMenuBar();
        if (jMenuBar != null) {
          jMenuBar.putClientProperty(this, Boolean.valueOf(jMenuBar.isEnabled()));
          jMenuBar.setEnabled(false);
        } 
        BusyGlassPane busyGlassPane = new BusyGlassPane();
        InputVerifier inputVerifier = new InputVerifier() {
            public boolean verify(JComponent param1JComponent) {
              return !param1JComponent.isVisible();
            }
          };
        busyGlassPane.setInputVerifier(inputVerifier);
        Component component1 = rootPaneContainer.getGlassPane();
        rootPaneContainer.getRootPane().putClientProperty(this, component1);
        rootPaneContainer.setGlassPane(busyGlassPane);
        busyGlassPane.setVisible(true);
        busyGlassPane.revalidate();
      } else {
        JMenuBar jMenuBar = rootPaneContainer.getRootPane().getJMenuBar();
        if (jMenuBar != null) {
          boolean bool = ((Boolean)jMenuBar.getClientProperty(this)).booleanValue();
          jMenuBar.putClientProperty(this, (Object)null);
          jMenuBar.setEnabled(bool);
        } 
        Component component1 = (Component)rootPaneContainer.getRootPane().getClientProperty(this);
        rootPaneContainer.getRootPane().putClientProperty(this, (Object)null);
        if (!component1.isVisible())
          rootPaneContainer.getGlassPane().setVisible(false); 
        rootPaneContainer.setGlassPane(component1);
      }  
  }
  
  private static class BusyGlassPane extends JPanel {
    BusyGlassPane() {
      super((LayoutManager)null, false);
      setVisible(false);
      setOpaque(false);
      setCursor(Cursor.getPredefinedCursor(3));
      MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {
        
        };
      addMouseMotionListener(mouseInputAdapter);
      addMouseListener(mouseInputAdapter);
    }
  }
  
  private int blockingDialogDelay() {
    Integer integer = null;
    String str = "BlockingDialogTimer.delay";
    ApplicationAction applicationAction = getAction();
    if (applicationAction != null) {
      ResourceMap resourceMap1 = applicationAction.getResourceMap();
      String str1 = applicationAction.getName();
      integer = resourceMap1.getInteger(str1 + "." + str);
    } 
    ResourceMap resourceMap = getTask().getResourceMap();
    if (integer == null && resourceMap != null)
      integer = resourceMap.getInteger(str); 
    return (integer == null) ? 0 : integer.intValue();
  }
  
  private void showBlockingDialog(boolean paramBoolean) {
    if (paramBoolean) {
      if (this.modalDialog != null) {
        String str = String.format("unexpected InputBlocker state [%s] %s", new Object[] { Boolean.valueOf(paramBoolean), this });
        logger.warning(str);
        this.modalDialog.dispose();
      } 
      this.modalDialog = createBlockingDialog();
      ActionListener actionListener = new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (DefaultInputBlocker.this.modalDialog != null)
              DefaultInputBlocker.this.modalDialog.setVisible(true); 
          }
        };
      Timer timer = new Timer(blockingDialogDelay(), actionListener);
      timer.setRepeats(false);
      timer.start();
    } else if (this.modalDialog != null) {
      this.modalDialog.dispose();
      this.modalDialog = null;
    } else {
      String str = String.format("unexpected InputBlocker state [%s] %s", new Object[] { Boolean.valueOf(paramBoolean), this });
      logger.warning(str);
    } 
  }
  
  protected void block() {
    switch (getScope()) {
      case ACTION:
        setActionTargetBlocked(true);
        break;
      case COMPONENT:
        setComponentTargetBlocked(true);
        break;
      case WINDOW:
      case APPLICATION:
        showBusyGlassPane(true);
        showBlockingDialog(true);
        break;
    } 
  }
  
  protected void unblock() {
    switch (getScope()) {
      case ACTION:
        setActionTargetBlocked(false);
        break;
      case COMPONENT:
        setComponentTargetBlocked(false);
        break;
      case WINDOW:
      case APPLICATION:
        showBusyGlassPane(false);
        showBlockingDialog(false);
        break;
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\DefaultInputBlocker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */