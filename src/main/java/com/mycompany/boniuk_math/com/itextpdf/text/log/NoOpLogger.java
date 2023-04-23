package com.mycompany.boniuk_math.com.itextpdf.text.log;

public final class NoOpLogger implements Logger {
  public Logger getLogger(Class<?> name) {
    return this;
  }
  
  public void warn(String message) {}
  
  public void trace(String message) {}
  
  public void debug(String message) {}
  
  public void info(String message) {}
  
  public void error(String message, Exception e) {}
  
  public boolean isLogging(Level level) {
    return false;
  }
  
  public void error(String message) {}
  
  public Logger getLogger(String name) {
    return this;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\log\NoOpLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */