package com.mycompany.boniuk_math.org.jdesktop.application;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ResourceConverter {
  protected final Class type;
  
  public abstract Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverterException;
  
  public String toString(Object paramObject) {
    return (paramObject == null) ? "null" : paramObject.toString();
  }
  
  protected ResourceConverter(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("null type"); 
    this.type = paramClass;
  }
  
  private ResourceConverter() {
    this.type = null;
  }
  
  public boolean supportsType(Class paramClass) {
    return this.type.equals(paramClass);
  }
  
  public static class ResourceConverterException extends Exception {
    private final String badString;
    
    private String maybeShorten(String param1String) {
      int i = param1String.length();
      return (i < 128) ? param1String : (param1String.substring(0, 128) + "...[" + (i - 128) + " more characters]");
    }
    
    public ResourceConverterException(String param1String1, String param1String2, Throwable param1Throwable) {
      super(param1String1, param1Throwable);
      this.badString = maybeShorten(param1String2);
    }
    
    public ResourceConverterException(String param1String1, String param1String2) {
      super(param1String1);
      this.badString = maybeShorten(param1String2);
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer(super.toString());
      stringBuffer.append(" string: \"");
      stringBuffer.append(this.badString);
      stringBuffer.append("\"");
      return stringBuffer.toString();
    }
  }
  
  public static void register(ResourceConverter paramResourceConverter) {
    if (paramResourceConverter == null)
      throw new IllegalArgumentException("null resourceConverter"); 
    resourceConverters.add(paramResourceConverter);
  }
  
  public static ResourceConverter forType(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("null type"); 
    for (ResourceConverter resourceConverter : resourceConverters) {
      if (resourceConverter.supportsType(paramClass))
        return resourceConverter; 
    } 
    return null;
  }
  
  private static ResourceConverter[] resourceConvertersArray = new ResourceConverter[] { new BooleanResourceConverter(new String[] { "true", "on", "yes" }), new IntegerResourceConverter(), new MessageFormatResourceConverter(), new FloatResourceConverter(), new DoubleResourceConverter(), new LongResourceConverter(), new ShortResourceConverter(), new ByteResourceConverter(), new URLResourceConverter(), new URIResourceConverter() };
  
  private static List<ResourceConverter> resourceConverters = new ArrayList<ResourceConverter>(Arrays.asList(resourceConvertersArray));
  
  private static class BooleanResourceConverter extends ResourceConverter {
    private final String[] trueStrings;
    
    BooleanResourceConverter(String... param1VarArgs) {
      super(Boolean.class);
      this.trueStrings = param1VarArgs;
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) {
      param1String = param1String.trim();
      for (String str : this.trueStrings) {
        if (param1String.equalsIgnoreCase(str))
          return Boolean.TRUE; 
      } 
      return Boolean.FALSE;
    }
    
    public boolean supportsType(Class param1Class) {
      return (param1Class.equals(Boolean.class) || param1Class.equals(boolean.class));
    }
  }
  
  private static abstract class NumberResourceConverter extends ResourceConverter {
    private final Class primitiveType;
    
    NumberResourceConverter(Class param1Class1, Class param1Class2) {
      super(param1Class1);
      this.primitiveType = param1Class2;
    }
    
    protected abstract Number parseString(String param1String) throws NumberFormatException;
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        return parseString(param1String);
      } catch (NumberFormatException numberFormatException) {
        throw new ResourceConverter.ResourceConverterException("invalid " + this.type.getSimpleName(), param1String, numberFormatException);
      } 
    }
    
    public boolean supportsType(Class param1Class) {
      return (param1Class.equals(this.type) || param1Class.equals(this.primitiveType));
    }
  }
  
  private static class FloatResourceConverter extends NumberResourceConverter {
    FloatResourceConverter() {
      super(Float.class, float.class);
    }
    
    protected Number parseString(String param1String) throws NumberFormatException {
      return Float.valueOf(Float.parseFloat(param1String));
    }
  }
  
  private static class DoubleResourceConverter extends NumberResourceConverter {
    DoubleResourceConverter() {
      super(Double.class, double.class);
    }
    
    protected Number parseString(String param1String) throws NumberFormatException {
      return Double.valueOf(Double.parseDouble(param1String));
    }
  }
  
  private static abstract class INumberResourceConverter extends ResourceConverter {
    private final Class primitiveType;
    
    INumberResourceConverter(Class param1Class1, Class param1Class2) {
      super(param1Class1);
      this.primitiveType = param1Class2;
    }
    
    protected abstract Number parseString(String param1String, int param1Int) throws NumberFormatException;
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        String[] arrayOfString = param1String.split("&");
        boolean bool = (arrayOfString.length == 2) ? Integer.parseInt(arrayOfString[1]) : true;
        return parseString(arrayOfString[0], bool);
      } catch (NumberFormatException numberFormatException) {
        throw new ResourceConverter.ResourceConverterException("invalid " + this.type.getSimpleName(), param1String, numberFormatException);
      } 
    }
    
    public boolean supportsType(Class param1Class) {
      return (param1Class.equals(this.type) || param1Class.equals(this.primitiveType));
    }
  }
  
  private static class ByteResourceConverter extends INumberResourceConverter {
    ByteResourceConverter() {
      super(Byte.class, byte.class);
    }
    
    protected Number parseString(String param1String, int param1Int) throws NumberFormatException {
      return Byte.valueOf((param1Int == -1) ? Byte.decode(param1String).byteValue() : Byte.parseByte(param1String, param1Int));
    }
  }
  
  private static class IntegerResourceConverter extends INumberResourceConverter {
    IntegerResourceConverter() {
      super(Integer.class, int.class);
    }
    
    protected Number parseString(String param1String, int param1Int) throws NumberFormatException {
      return Integer.valueOf((param1Int == -1) ? Integer.decode(param1String).intValue() : Integer.parseInt(param1String, param1Int));
    }
  }
  
  private static class LongResourceConverter extends INumberResourceConverter {
    LongResourceConverter() {
      super(Long.class, long.class);
    }
    
    protected Number parseString(String param1String, int param1Int) throws NumberFormatException {
      return Long.valueOf((param1Int == -1) ? Long.decode(param1String).longValue() : Long.parseLong(param1String, param1Int));
    }
  }
  
  private static class ShortResourceConverter extends INumberResourceConverter {
    ShortResourceConverter() {
      super(Short.class, short.class);
    }
    
    protected Number parseString(String param1String, int param1Int) throws NumberFormatException {
      return Short.valueOf((param1Int == -1) ? Short.decode(param1String).shortValue() : Short.parseShort(param1String, param1Int));
    }
  }
  
  private static class MessageFormatResourceConverter extends ResourceConverter {
    MessageFormatResourceConverter() {
      super(MessageFormat.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) {
      return new MessageFormat(param1String);
    }
  }
  
  private static class URLResourceConverter extends ResourceConverter {
    URLResourceConverter() {
      super(URL.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        return new URL(param1String);
      } catch (MalformedURLException malformedURLException) {
        throw new ResourceConverter.ResourceConverterException("invalid URL", param1String, malformedURLException);
      } 
    }
  }
  
  private static class URIResourceConverter extends ResourceConverter {
    URIResourceConverter() {
      super(URI.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        return new URI(param1String);
      } catch (URISyntaxException uRISyntaxException) {
        throw new ResourceConverter.ResourceConverterException("invalid URI", param1String, uRISyntaxException);
      } 
    }
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ResourceConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */