package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

public class LocalStorage extends AbstractBean {
  private static Logger logger = Logger.getLogger(LocalStorage.class.getName());
  
  private final ApplicationContext context;
  
  private long storageLimit = -1L;
  
  private LocalIO localIO = null;
  
  private final File unspecifiedFile = new File("unspecified");
  
  private File directory = this.unspecifiedFile;
  
  protected LocalStorage(ApplicationContext paramApplicationContext) {
    if (paramApplicationContext == null)
      throw new IllegalArgumentException("null context"); 
    this.context = paramApplicationContext;
  }
  
  protected final ApplicationContext getContext() {
    return this.context;
  }
  
  private void checkFileName(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("null fileName"); 
  }
  
  public InputStream openInputFile(String paramString) throws IOException {
    checkFileName(paramString);
    return getLocalIO().openInputFile(paramString);
  }
  
  public OutputStream openOutputFile(String paramString) throws IOException {
    checkFileName(paramString);
    return getLocalIO().openOutputFile(paramString);
  }
  
  public boolean deleteFile(String paramString) throws IOException {
    checkFileName(paramString);
    return getLocalIO().deleteFile(paramString);
  }
  
  private static class AbortExceptionListener implements ExceptionListener {
    public Exception exception = null;
    
    public void exceptionThrown(Exception param1Exception) {
      if (this.exception == null)
        this.exception = param1Exception; 
    }
    
    private AbortExceptionListener() {}
  }
  
  private static boolean persistenceDelegatesInitialized = false;
  
  public void save(Object paramObject, String paramString) throws IOException {
    AbortExceptionListener abortExceptionListener = new AbortExceptionListener();
    XMLEncoder xMLEncoder = null;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      xMLEncoder = new XMLEncoder(byteArrayOutputStream);
      if (!persistenceDelegatesInitialized) {
        xMLEncoder.setPersistenceDelegate(Rectangle.class, new RectanglePD());
        persistenceDelegatesInitialized = true;
      } 
      xMLEncoder.setExceptionListener(abortExceptionListener);
      xMLEncoder.writeObject(paramObject);
    } finally {
      if (xMLEncoder != null)
        xMLEncoder.close(); 
    } 
    if (abortExceptionListener.exception != null)
      throw new LSException("save failed \"" + paramString + "\"", abortExceptionListener.exception); 
    OutputStream outputStream = null;
    try {
      outputStream = openOutputFile(paramString);
      outputStream.write(byteArrayOutputStream.toByteArray());
    } finally {
      if (outputStream != null)
        outputStream.close(); 
    } 
  }
  
  public Object load(String paramString) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = openInputFile(paramString);
    } catch (IOException iOException) {
      return null;
    } 
    AbortExceptionListener abortExceptionListener = new AbortExceptionListener();
    XMLDecoder xMLDecoder = null;
    try {
      xMLDecoder = new XMLDecoder(inputStream);
      xMLDecoder.setExceptionListener(abortExceptionListener);
      Object object = xMLDecoder.readObject();
      if (abortExceptionListener.exception != null)
        throw new LSException("load failed \"" + paramString + "\"", abortExceptionListener.exception); 
      return object;
    } finally {
      if (xMLDecoder != null)
        xMLDecoder.close(); 
    } 
  }
  
  private void closeStream(Closeable paramCloseable, String paramString) throws IOException {
    if (paramCloseable != null)
      try {
        paramCloseable.close();
      } catch (IOException iOException) {
        throw new LSException("close failed \"" + paramString + "\"", iOException);
      }  
  }
  
  public long getStorageLimit() {
    return this.storageLimit;
  }
  
  public void setStorageLimit(long paramLong) {
    if (paramLong < -1L)
      throw new IllegalArgumentException("invalid storageLimit"); 
    long l = this.storageLimit;
    this.storageLimit = paramLong;
    firePropertyChange("storageLimit", Long.valueOf(l), Long.valueOf(this.storageLimit));
  }
  
  private String getId(String paramString1, String paramString2) {
    ResourceMap resourceMap = getContext().getResourceMap();
    String str = resourceMap.getString(paramString1, new Object[0]);
    if (str == null) {
      logger.log(Level.WARNING, "unspecified resource " + paramString1 + " using " + paramString2);
      str = paramString2;
    } else if (str.trim().length() == 0) {
      logger.log(Level.WARNING, "empty resource " + paramString1 + " using " + paramString2);
      str = paramString2;
    } 
    return str;
  }
  
  private String getApplicationId() {
    return getId("Application.id", getContext().getApplicationClass().getSimpleName());
  }
  
  private String getVendorId() {
    return getId("Application.vendorId", "UnknownApplicationVendor");
  }
  
  private enum OSId {
    WINDOWS, OSX, UNIX;
  }
  
  private OSId getOSId() {
    PrivilegedAction<String> privilegedAction = new PrivilegedAction<String>() {
        public String run() {
          return System.getProperty("os.name");
        }
      };
    OSId oSId = OSId.UNIX;
    String str = AccessController.<String>doPrivileged(privilegedAction);
    if (str != null)
      if (str.toLowerCase().startsWith("mac os x")) {
        oSId = OSId.OSX;
      } else if (str.contains("Windows")) {
        oSId = OSId.WINDOWS;
      }  
    return oSId;
  }
  
  public File getDirectory() {
    if (this.directory == this.unspecifiedFile) {
      this.directory = null;
      String str = null;
      try {
        str = System.getProperty("user.home");
      } catch (SecurityException securityException) {}
      if (str != null) {
        String str1 = getApplicationId();
        OSId oSId = getOSId();
        if (oSId == OSId.WINDOWS) {
          File file = null;
          try {
            String str3 = System.getenv("APPDATA");
            if (str3 != null && str3.length() > 0)
              file = new File(str3); 
          } catch (SecurityException securityException) {}
          String str2 = getVendorId();
          if (file != null && file.isDirectory()) {
            String str3 = str2 + "\\" + str1 + "\\";
            this.directory = new File(file, str3);
          } else {
            String str3 = "Application Data\\" + str2 + "\\" + str1 + "\\";
            this.directory = new File(str, str3);
          } 
        } else if (oSId == OSId.OSX) {
          String str2 = "Library/Application Support/" + str1 + "/";
          this.directory = new File(str, str2);
        } else {
          String str2 = "." + str1 + "/";
          this.directory = new File(str, str2);
        } 
      } 
    } 
    return this.directory;
  }
  
  public void setDirectory(File paramFile) {
    File file = this.directory;
    this.directory = paramFile;
    firePropertyChange("directory", file, this.directory);
  }
  
  private static class LSException extends IOException {
    public LSException(String param1String, Throwable param1Throwable) {
      super(param1String);
      initCause(param1Throwable);
    }
    
    public LSException(String param1String) {
      super(param1String);
    }
  }
  
  private static class RectanglePD extends DefaultPersistenceDelegate {
    public RectanglePD() {
      super(new String[] { "x", "y", "width", "height" });
    }
    
    protected Expression instantiate(Object param1Object, Encoder param1Encoder) {
      Rectangle rectangle = (Rectangle)param1Object;
      Object[] arrayOfObject = { Integer.valueOf(rectangle.x), Integer.valueOf(rectangle.y), Integer.valueOf(rectangle.width), Integer.valueOf(rectangle.height) };
      return new Expression(param1Object, param1Object.getClass(), "new", arrayOfObject);
    }
  }
  
  private synchronized LocalIO getLocalIO() {
    if (this.localIO == null) {
      this.localIO = getPersistenceServiceIO();
      if (this.localIO == null)
        this.localIO = new LocalFileIO(); 
    } 
    return this.localIO;
  }
  
  private abstract class LocalIO {
    private LocalIO() {}
    
    public abstract InputStream openInputFile(String param1String) throws IOException;
    
    public abstract OutputStream openOutputFile(String param1String) throws IOException;
    
    public abstract boolean deleteFile(String param1String) throws IOException;
  }
  
  private class LocalFileIO extends LocalIO {
    private LocalFileIO() {}
    
    public InputStream openInputFile(String param1String) throws IOException {
      File file = new File(LocalStorage.this.getDirectory(), param1String);
      try {
        return new BufferedInputStream(new FileInputStream(file));
      } catch (IOException iOException) {
        throw new LocalStorage.LSException("couldn't open input file \"" + param1String + "\"", iOException);
      } 
    }
    
    public OutputStream openOutputFile(String param1String) throws IOException {
      File file1 = LocalStorage.this.getDirectory();
      if (!file1.isDirectory() && 
        !file1.mkdirs())
        throw new LocalStorage.LSException("couldn't create directory " + file1); 
      File file2 = new File(file1, param1String);
      try {
        return new BufferedOutputStream(new FileOutputStream(file2));
      } catch (IOException iOException) {
        throw new LocalStorage.LSException("couldn't open output file \"" + param1String + "\"", iOException);
      } 
    }
    
    public boolean deleteFile(String param1String) throws IOException {
      File file = new File(LocalStorage.this.getDirectory(), param1String);
      return file.delete();
    }
  }
  
  private LocalIO getPersistenceServiceIO() {
    try {
      Class<?> clazz = Class.forName("javax.jnlp.ServiceManager");
      Method method = clazz.getMethod("getServiceNames", new Class[0]);
      String[] arrayOfString = (String[])method.invoke(null, new Object[0]);
      boolean bool1 = false;
      boolean bool2 = false;
      for (String str : arrayOfString) {
        if (str.equals("javax.jnlp.BasicService")) {
          bool2 = true;
        } else if (str.equals("javax.jnlp.PersistenceService")) {
          bool1 = true;
        } 
      } 
      if (bool2 && bool1)
        return new PersistenceServiceIO(); 
    } catch (Exception exception) {}
    return null;
  }
  
  private class PersistenceServiceIO extends LocalIO {
    private BasicService bs;
    
    private PersistenceService ps;
    
    private String initFailedMessage(String param1String) {
      return getClass().getName() + " initialization failed: " + param1String;
    }
    
    PersistenceServiceIO() {
      try {
        this.bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
        this.ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
      } catch (UnavailableServiceException unavailableServiceException) {
        LocalStorage.logger.log(Level.SEVERE, initFailedMessage("ServiceManager.lookup"), (Throwable)unavailableServiceException);
        this.bs = null;
        this.ps = null;
      } 
    }
    
    private void checkBasics(String param1String) throws IOException {
      if (this.bs == null || this.ps == null)
        throw new IOException(initFailedMessage(param1String)); 
    }
    
    private URL fileNameToURL(String param1String) throws IOException {
      try {
        return new URL(this.bs.getCodeBase(), param1String);
      } catch (MalformedURLException malformedURLException) {
        throw new LocalStorage.LSException("invalid filename \"" + param1String + "\"", malformedURLException);
      } 
    }
    
    public InputStream openInputFile(String param1String) throws IOException {
      checkBasics("openInputFile");
      URL uRL = fileNameToURL(param1String);
      try {
        return new BufferedInputStream(this.ps.get(uRL).getInputStream());
      } catch (Exception exception) {
        throw new LocalStorage.LSException("openInputFile \"" + param1String + "\" failed", exception);
      } 
    }
    
    public OutputStream openOutputFile(String param1String) throws IOException {
      checkBasics("openOutputFile");
      URL uRL = fileNameToURL(param1String);
      try {
        FileContents fileContents = null;
        try {
          fileContents = this.ps.get(uRL);
        } catch (FileNotFoundException fileNotFoundException) {
          long l1 = 131072L;
          long l2 = this.ps.create(uRL, l1);
          if (l2 >= l1)
            fileContents = this.ps.get(uRL); 
        } 
        if (fileContents != null && fileContents.canWrite())
          return new BufferedOutputStream(fileContents.getOutputStream(true)); 
        throw new IOException("unable to create FileContents object");
      } catch (Exception exception) {
        throw new LocalStorage.LSException("openOutputFile \"" + param1String + "\" failed", exception);
      } 
    }
    
    public boolean deleteFile(String param1String) throws IOException {
      checkBasics("deleteFile");
      URL uRL = fileNameToURL(param1String);
      try {
        this.ps.delete(uRL);
        return true;
      } catch (Exception exception) {
        throw new LocalStorage.LSException("openInputFile \"" + param1String + "\" failed", exception);
      } 
    }
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\LocalStorage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */