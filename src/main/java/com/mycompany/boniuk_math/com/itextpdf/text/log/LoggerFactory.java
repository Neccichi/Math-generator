package com.mycompany.boniuk_math.com.itextpdf.text.log;

public class LoggerFactory {
  private static LoggerFactory myself = new LoggerFactory();
  
  public static Logger getLogger(Class<?> klass) {
    return myself.logger.getLogger(klass);
  }
  
  public static Logger getLogger(String name) {
    return myself.logger.getLogger(name);
  }
  
  public static LoggerFactory getInstance() {
    return myself;
  }
  
  private Logger logger = new NoOpLogger();
  
  public void setLogger(Logger logger) {
    this.logger = logger;
  }
  
  public Logger logger() {
    return this.logger;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\log\LoggerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */