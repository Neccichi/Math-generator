package com.mycompany.boniuk_math.org.jdesktop.application;

import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JRootPane;

public class FrameView extends View {
  private static final Logger logger = Logger.getLogger(FrameView.class.getName());
  
  private JFrame frame = null;
  
  public FrameView(Application paramApplication) {
    super(paramApplication);
  }
  
  public JFrame getFrame() {
    if (this.frame == null) {
      String str = getContext().getResourceMap().getString("Application.title", new Object[0]);
      this.frame = new JFrame(str);
      this.frame.setName("mainFrame");
    } 
    return this.frame;
  }
  
  public void setFrame(JFrame paramJFrame) {
    if (paramJFrame == null)
      throw new IllegalArgumentException("null JFrame"); 
    if (this.frame != null)
      throw new IllegalStateException("frame already set"); 
    this.frame = paramJFrame;
    firePropertyChange("frame", null, this.frame);
  }
  
  public JRootPane getRootPane() {
    return getFrame().getRootPane();
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\FrameView.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */