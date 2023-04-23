package com.mycompany.boniuk_math.org.jdesktop.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

public class ResourceMap {
  private static Logger logger = Logger.getLogger(ResourceMap.class.getName());
  
  private static final Object nullResource = new String("null resource");
  
  private final ClassLoader classLoader;
  
  private final ResourceMap parent;
  
  private final List<String> bundleNames;
  
  private final String resourcesDir;
  
  private Map<String, Object> bundlesMapP = null;
  
  private Locale locale = Locale.getDefault();
  
  private Set<String> bundlesMapKeysP = null;
  
  private boolean bundlesLoaded = false;
  
  public ResourceMap(ResourceMap paramResourceMap, ClassLoader paramClassLoader, List<String> paramList) {
    if (paramClassLoader == null)
      throw new IllegalArgumentException("null ClassLoader"); 
    if (paramList == null || paramList.size() == 0)
      throw new IllegalArgumentException("no bundle specified"); 
    for (String str1 : paramList) {
      if (str1 == null || str1.length() == 0)
        throw new IllegalArgumentException("invalid bundleName: \"" + str1 + "\""); 
    } 
    String str = bundlePackageName(paramList.get(0));
    for (String str1 : paramList) {
      if (!str.equals(bundlePackageName(str1)))
        throw new IllegalArgumentException("bundles not colocated: \"" + str1 + "\" != \"" + str + "\""); 
    } 
    this.parent = paramResourceMap;
    this.classLoader = paramClassLoader;
    this.bundleNames = Collections.unmodifiableList(new ArrayList<String>(paramList));
    this.resourcesDir = str.replace(".", "/") + "/";
  }
  
  private String bundlePackageName(String paramString) {
    int i = paramString.lastIndexOf(".");
    return (i == -1) ? "" : paramString.substring(0, i);
  }
  
  public ResourceMap(ResourceMap paramResourceMap, ClassLoader paramClassLoader, String... paramVarArgs) {
    this(paramResourceMap, paramClassLoader, Arrays.asList(paramVarArgs));
  }
  
  public ResourceMap getParent() {
    return this.parent;
  }
  
  public List<String> getBundleNames() {
    return this.bundleNames;
  }
  
  public ClassLoader getClassLoader() {
    return this.classLoader;
  }
  
  public String getResourcesDir() {
    return this.resourcesDir;
  }
  
  private synchronized Map<String, Object> getBundlesMap() {
    Locale locale = Locale.getDefault();
    if (this.locale != locale) {
      this.bundlesLoaded = false;
      this.locale = locale;
    } 
    if (!this.bundlesLoaded) {
      ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<Object, Object>();
      for (int i = this.bundleNames.size() - 1; i >= 0; i--) {
        try {
          String str = this.bundleNames.get(i);
          ResourceBundle resourceBundle = ResourceBundle.getBundle(str, this.locale, this.classLoader);
          Enumeration<String> enumeration = resourceBundle.getKeys();
          while (enumeration.hasMoreElements()) {
            String str1 = enumeration.nextElement();
            concurrentHashMap.put(str1, resourceBundle.getObject(str1));
          } 
        } catch (MissingResourceException missingResourceException) {}
      } 
      this.bundlesMapP = (Map)concurrentHashMap;
      this.bundlesLoaded = true;
    } 
    return this.bundlesMapP;
  }
  
  private void checkNullKey(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("null key"); 
  }
  
  private synchronized Set<String> getBundlesMapKeys() {
    if (this.bundlesMapKeysP == null) {
      HashSet<String> hashSet = new HashSet<String>(getResourceKeySet());
      ResourceMap resourceMap = getParent();
      if (resourceMap != null)
        hashSet.addAll(resourceMap.keySet()); 
      this.bundlesMapKeysP = Collections.unmodifiableSet(hashSet);
    } 
    return this.bundlesMapKeysP;
  }
  
  public Set<String> keySet() {
    return getBundlesMapKeys();
  }
  
  public boolean containsKey(String paramString) {
    checkNullKey(paramString);
    if (containsResourceKey(paramString))
      return true; 
    ResourceMap resourceMap = getParent();
    return (resourceMap != null) ? resourceMap.containsKey(paramString) : false;
  }
  
  public static class LookupException extends RuntimeException {
    private final Class type;
    
    private final String key;
    
    public LookupException(String param1String1, String param1String2, Class param1Class) {
      super(String.format("%s: resource %s, type %s", new Object[] { param1String1, param1String2, param1Class }));
      this.key = param1String2;
      this.type = param1Class;
    }
    
    public Class getType() {
      return this.type;
    }
    
    public String getKey() {
      return this.key;
    }
  }
  
  protected Set<String> getResourceKeySet() {
    Map<String, Object> map = getBundlesMap();
    if (map == null)
      return Collections.emptySet(); 
    return map.keySet();
  }
  
  protected boolean containsResourceKey(String paramString) {
    checkNullKey(paramString);
    Map<String, Object> map = getBundlesMap();
    return (map != null && map.containsKey(paramString));
  }
  
  protected Object getResource(String paramString) {
    checkNullKey(paramString);
    Map<String, Object> map = getBundlesMap();
    V v = (map != null) ? (V)map.get(paramString) : null;
    return (v == nullResource) ? null : v;
  }
  
  protected void putResource(String paramString, Object paramObject) {
    checkNullKey(paramString);
    Map<String, Object> map = getBundlesMap();
    if (map != null)
      map.put(paramString, (paramObject == null) ? nullResource : paramObject); 
  }
  
  public Object getObject(String paramString, Class<boolean> paramClass) {
    Class<Double> clazz;
    checkNullKey(paramString);
    if (paramClass == null)
      throw new IllegalArgumentException("null type"); 
    if (paramClass.isPrimitive()) {
      Class<Boolean> clazz1;
      if (paramClass == boolean.class) {
        clazz1 = Boolean.class;
      } else {
        Class<Character> clazz2;
        if (clazz1 == char.class) {
          clazz2 = Character.class;
        } else {
          Class<Byte> clazz3;
          if (clazz2 == byte.class) {
            clazz3 = Byte.class;
          } else {
            Class<Short> clazz4;
            if (clazz3 == short.class) {
              clazz4 = Short.class;
            } else {
              Class<Integer> clazz5;
              if (clazz4 == int.class) {
                clazz5 = Integer.class;
              } else {
                Class<Long> clazz6;
                if (clazz5 == long.class) {
                  clazz6 = Long.class;
                } else {
                  Class<Float> clazz7;
                  if (clazz6 == float.class) {
                    clazz7 = Float.class;
                  } else if (clazz7 == double.class) {
                    clazz = Double.class;
                  } 
                } 
              } 
            } 
          } 
        } 
      } 
    } 
    Object object = null;
    ResourceMap resourceMap = this;
    while (resourceMap != null) {
      if (resourceMap.containsResourceKey(paramString)) {
        object = resourceMap.getResource(paramString);
        break;
      } 
      resourceMap = resourceMap.getParent();
    } 
    if (object instanceof String && ((String)object).contains("${")) {
      object = evaluateStringExpression((String)object);
      resourceMap.putResource(paramString, object);
    } 
    if (object != null) {
      Class<?> clazz1 = object.getClass();
      if (!clazz.isAssignableFrom(clazz1))
        if (object instanceof String) {
          ResourceConverter resourceConverter = ResourceConverter.forType(clazz);
          if (resourceConverter != null) {
            String str = (String)object;
            try {
              object = resourceConverter.parseString(str, resourceMap);
              resourceMap.putResource(paramString, object);
            } catch (ResourceConverterException resourceConverterException) {
              String str1 = "string conversion failed";
              LookupException lookupException = new LookupException(str1, paramString, clazz);
              lookupException.initCause(resourceConverterException);
              throw lookupException;
            } 
          } else {
            String str = "no StringConverter for required type";
            throw new LookupException(str, paramString, clazz);
          } 
        } else {
          String str = "named resource has wrong type";
          throw new LookupException(str, paramString, clazz);
        }  
    } 
    return object;
  }
  
  private String evaluateStringExpression(String paramString) {
    if (paramString.trim().equals("${null}"))
      return null; 
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0, j = 0;
    while ((j = paramString.indexOf("${", i)) != -1) {
      if (j == 0 || (j > 0 && paramString.charAt(j - 1) != '\\')) {
        int k = paramString.indexOf("}", j);
        if (k != -1 && k > j + 2) {
          String str1 = paramString.substring(j + 2, k);
          String str2 = getString(str1, new Object[0]);
          stringBuffer.append(paramString.substring(i, j));
          if (str2 != null) {
            stringBuffer.append(str2);
          } else {
            String str3 = String.format("no value for \"%s\" in \"%s\"", new Object[] { str1, paramString });
            throw new LookupException(str3, str1, String.class);
          } 
          i = k + 1;
          continue;
        } 
        String str = String.format("no closing brace in \"%s\"", new Object[] { paramString });
        throw new LookupException(str, "<not found>", String.class);
      } 
      stringBuffer.append(paramString.substring(i, j - 1));
      stringBuffer.append("${");
      i = j + 2;
    } 
    stringBuffer.append(paramString.substring(i));
    return stringBuffer.toString();
  }
  
  public String getString(String paramString, Object... paramVarArgs) {
    if (paramVarArgs.length == 0)
      return (String)getObject(paramString, String.class); 
    String str = (String)getObject(paramString, String.class);
    return (str == null) ? null : String.format(str, paramVarArgs);
  }
  
  public final Boolean getBoolean(String paramString) {
    return (Boolean)getObject(paramString, Boolean.class);
  }
  
  public final Integer getInteger(String paramString) {
    return (Integer)getObject(paramString, Integer.class);
  }
  
  public final Long getLong(String paramString) {
    return (Long)getObject(paramString, Long.class);
  }
  
  public final Short getShort(String paramString) {
    return (Short)getObject(paramString, Short.class);
  }
  
  public final Byte getByte(String paramString) {
    return (Byte)getObject(paramString, Byte.class);
  }
  
  public final Float getFloat(String paramString) {
    return (Float)getObject(paramString, Float.class);
  }
  
  public final Double getDouble(String paramString) {
    return (Double)getObject(paramString, Double.class);
  }
  
  public final Icon getIcon(String paramString) {
    return (Icon)getObject(paramString, Icon.class);
  }
  
  public final ImageIcon getImageIcon(String paramString) {
    return (ImageIcon)getObject(paramString, ImageIcon.class);
  }
  
  public final Font getFont(String paramString) {
    return (Font)getObject(paramString, Font.class);
  }
  
  public final Color getColor(String paramString) {
    return (Color)getObject(paramString, Color.class);
  }
  
  public final KeyStroke getKeyStroke(String paramString) {
    return (KeyStroke)getObject(paramString, KeyStroke.class);
  }
  
  public Integer getKeyCode(String paramString) {
    KeyStroke keyStroke = getKeyStroke(paramString);
    return (keyStroke != null) ? new Integer(keyStroke.getKeyCode()) : null;
  }
  
  public static class PropertyInjectionException extends RuntimeException {
    private final String key;
    
    private final Component component;
    
    private final String propertyName;
    
    public PropertyInjectionException(String param1String1, String param1String2, Component param1Component, String param1String3) {
      super(String.format("%s: resource %s, property %s, component %s", new Object[] { param1String1, param1String2, param1String3, param1Component }));
      this.key = param1String2;
      this.component = param1Component;
      this.propertyName = param1String3;
    }
    
    public String getKey() {
      return this.key;
    }
    
    public Component getComponent() {
      return this.component;
    }
    
    public String getPropertyName() {
      return this.propertyName;
    }
  }
  
  private void injectComponentProperty(Component paramComponent, PropertyDescriptor paramPropertyDescriptor, String paramString) {
    Method method = paramPropertyDescriptor.getWriteMethod();
    Class<?> clazz = paramPropertyDescriptor.getPropertyType();
    if (method != null && clazz != null && containsKey(paramString)) {
      Object object = getObject(paramString, clazz);
      String str = paramPropertyDescriptor.getName();
      try {
        if ("text".equals(str) && paramComponent instanceof javax.swing.AbstractButton) {
          MnemonicText.configure(paramComponent, (String)object);
        } else if ("text".equals(str) && paramComponent instanceof javax.swing.JLabel) {
          MnemonicText.configure(paramComponent, (String)object);
        } else {
          method.invoke(paramComponent, new Object[] { object });
        } 
      } catch (Exception exception) {
        String str1 = paramPropertyDescriptor.getName();
        String str2 = "property setter failed";
        PropertyInjectionException propertyInjectionException = new PropertyInjectionException(str2, paramString, paramComponent, str1);
        propertyInjectionException.initCause(exception);
        throw propertyInjectionException;
      } 
    } else {
      if (clazz != null) {
        String str1 = paramPropertyDescriptor.getName();
        String str2 = "no value specified for resource";
        throw new PropertyInjectionException(str2, paramString, paramComponent, str1);
      } 
      if (method == null) {
        String str1 = paramPropertyDescriptor.getName();
        String str2 = "can't set read-only property";
        throw new PropertyInjectionException(str2, paramString, paramComponent, str1);
      } 
    } 
  }
  
  private void injectComponentProperties(Component paramComponent) {
    String str = paramComponent.getName();
    if (str != null) {
      boolean bool = false;
      for (String str1 : keySet()) {
        int i = str1.lastIndexOf(".");
        if (i != -1 && str.equals(str1.substring(0, i))) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return; 
      BeanInfo beanInfo = null;
      try {
        beanInfo = Introspector.getBeanInfo(paramComponent.getClass());
      } catch (IntrospectionException introspectionException) {
        String str1 = "introspection failed";
        PropertyInjectionException propertyInjectionException = new PropertyInjectionException(str1, null, paramComponent, null);
        propertyInjectionException.initCause(introspectionException);
        throw propertyInjectionException;
      } 
      PropertyDescriptor[] arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
      if (arrayOfPropertyDescriptor != null && arrayOfPropertyDescriptor.length > 0)
        for (String str1 : keySet()) {
          int i = str1.lastIndexOf(".");
          String str2 = (i == -1) ? null : str1.substring(0, i);
          if (str.equals(str2)) {
            if (i + 1 == str1.length()) {
              String str4 = "component resource lacks property name suffix";
              logger.warning(str4);
              break;
            } 
            String str3 = str1.substring(i + 1);
            boolean bool1 = false;
            for (PropertyDescriptor propertyDescriptor : arrayOfPropertyDescriptor) {
              if (propertyDescriptor.getName().equals(str3)) {
                injectComponentProperty(paramComponent, propertyDescriptor, str1);
                bool1 = true;
                break;
              } 
            } 
            if (!bool1) {
              String str4 = String.format("[resource %s] component named %s doesn't have a property named %s", new Object[] { str1, str, str3 });
              logger.warning(str4);
            } 
          } 
        }  
    } 
  }
  
  public void injectComponent(Component paramComponent) {
    if (paramComponent == null)
      throw new IllegalArgumentException("null target"); 
    injectComponentProperties(paramComponent);
  }
  
  public void injectComponents(Component paramComponent) {
    injectComponent(paramComponent);
    if (paramComponent instanceof JMenu) {
      JMenu jMenu = (JMenu)paramComponent;
      for (Component component : jMenu.getMenuComponents())
        injectComponents(component); 
    } else if (paramComponent instanceof Container) {
      Container container = (Container)paramComponent;
      for (Component component : container.getComponents())
        injectComponents(component); 
    } 
  }
  
  public static class InjectFieldException extends RuntimeException {
    private final Field field;
    
    private final Object target;
    
    private final String key;
    
    public InjectFieldException(String param1String1, Field param1Field, Object param1Object, String param1String2) {
      super(String.format("%s: resource %s, field %s, target %s", new Object[] { param1String1, param1String2, param1Field, param1Object }));
      this.field = param1Field;
      this.target = param1Object;
      this.key = param1String2;
    }
    
    public Field getField() {
      return this.field;
    }
    
    public Object getTarget() {
      return this.target;
    }
    
    public String getKey() {
      return this.key;
    }
  }
  
  private void injectField(Field paramField, Object paramObject, String paramString) {
    Class<?> clazz = paramField.getType();
    if (clazz.isArray()) {
      clazz = clazz.getComponentType();
      Pattern pattern = Pattern.compile(paramString + "\\[([\\d]+)\\]");
      ArrayList arrayList = new ArrayList();
      for (String str : keySet()) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
          Object object = getObject(str, clazz);
          if (!paramField.isAccessible())
            paramField.setAccessible(true); 
          try {
            int i = Integer.parseInt(matcher.group(1));
            Array.set(paramField.get(paramObject), i, object);
          } catch (Exception exception) {
            String str1 = "unable to set array element";
            InjectFieldException injectFieldException = new InjectFieldException(str1, paramField, paramObject, paramString);
            injectFieldException.initCause(exception);
            throw injectFieldException;
          } 
        } 
      } 
    } else {
      Object object = getObject(paramString, clazz);
      if (object != null) {
        if (!paramField.isAccessible())
          paramField.setAccessible(true); 
        try {
          paramField.set(paramObject, object);
        } catch (Exception exception) {
          String str = "unable to set field's value";
          InjectFieldException injectFieldException = new InjectFieldException(str, paramField, paramObject, paramString);
          injectFieldException.initCause(exception);
          throw injectFieldException;
        } 
      } 
    } 
  }
  
  public void injectFields(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("null target"); 
    Class<?> clazz = paramObject.getClass();
    if (clazz.isArray())
      throw new IllegalArgumentException("array target"); 
    String str = clazz.getSimpleName() + ".";
    for (Field field : clazz.getDeclaredFields()) {
      Resource resource = field.<Resource>getAnnotation(Resource.class);
      if (resource != null) {
        String str1 = resource.key();
        String str2 = (str1.length() > 0) ? str1 : (str + field.getName());
        injectField(field, paramObject, str2);
      } 
    } 
  }
  
  static {
    ResourceConverter[] arrayOfResourceConverter = { new ColorStringConverter(), new IconStringConverter(), new ImageStringConverter(), new FontStringConverter(), new KeyStrokeStringConverter(), new DimensionStringConverter(), new PointStringConverter(), new RectangleStringConverter(), new InsetsStringConverter(), new EmptyBorderStringConverter() };
    for (ResourceConverter resourceConverter : arrayOfResourceConverter)
      ResourceConverter.register(resourceConverter); 
  }
  
  private static String resourcePath(String paramString, ResourceMap paramResourceMap) {
    String str = paramString;
    if (paramString == null) {
      str = null;
    } else if (paramString.startsWith("/")) {
      str = (paramString.length() > 1) ? paramString.substring(1) : null;
    } else {
      str = paramResourceMap.getResourcesDir() + paramString;
    } 
    return str;
  }
  
  private static ImageIcon loadImageIcon(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
    String str1 = resourcePath(paramString, paramResourceMap);
    if (str1 == null) {
      String str = String.format("invalid image/icon path \"%s\"", new Object[] { paramString });
      throw new ResourceConverter.ResourceConverterException(str, paramString);
    } 
    URL uRL = paramResourceMap.getClassLoader().getResource(str1);
    if (uRL != null)
      return new ImageIcon(uRL); 
    String str2 = String.format("couldn't find Icon resource \"%s\"", new Object[] { paramString });
    throw new ResourceConverter.ResourceConverterException(str2, paramString);
  }
  
  private static class FontStringConverter extends ResourceConverter {
    FontStringConverter() {
      super(Font.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      return Font.decode(param1String);
    }
  }
  
  private static class ColorStringConverter extends ResourceConverter {
    ColorStringConverter() {
      super(Color.class);
    }
    
    private void error(String param1String1, String param1String2, Exception param1Exception) throws ResourceConverter.ResourceConverterException {
      throw new ResourceConverter.ResourceConverterException(param1String1, param1String2, param1Exception);
    }
    
    private void error(String param1String1, String param1String2) throws ResourceConverter.ResourceConverterException {
      error(param1String1, param1String2, null);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      Color color = null;
      if (param1String.startsWith("#")) {
        int i;
        int j;
        switch (param1String.length()) {
          case 7:
            color = Color.decode(param1String);
            return color;
          case 9:
            i = Integer.decode(param1String.substring(0, 3)).intValue();
            j = Integer.decode("#" + param1String.substring(3)).intValue();
            color = new Color(i << 24 | j, true);
            return color;
        } 
        throw new ResourceConverter.ResourceConverterException("invalid #RRGGBB or #AARRGGBB color string", param1String);
      } 
      String[] arrayOfString = param1String.split(",");
      if (arrayOfString.length < 3 || arrayOfString.length > 4)
        throw new ResourceConverter.ResourceConverterException("invalid R, G, B[, A] color string", param1String); 
      try {
        if (arrayOfString.length == 4) {
          int i = Integer.parseInt(arrayOfString[0].trim());
          int j = Integer.parseInt(arrayOfString[1].trim());
          int k = Integer.parseInt(arrayOfString[2].trim());
          int m = Integer.parseInt(arrayOfString[3].trim());
          color = new Color(i, j, k, m);
        } else {
          int i = Integer.parseInt(arrayOfString[0].trim());
          int j = Integer.parseInt(arrayOfString[1].trim());
          int k = Integer.parseInt(arrayOfString[2].trim());
          color = new Color(i, j, k);
        } 
      } catch (NumberFormatException numberFormatException) {
        throw new ResourceConverter.ResourceConverterException("invalid R, G, B[, A] color string", param1String, numberFormatException);
      } 
      return color;
    }
  }
  
  private static class IconStringConverter extends ResourceConverter {
    IconStringConverter() {
      super(Icon.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      return ResourceMap.loadImageIcon(param1String, param1ResourceMap);
    }
    
    public boolean supportsType(Class param1Class) {
      return (param1Class.equals(Icon.class) || param1Class.equals(ImageIcon.class));
    }
  }
  
  private static class ImageStringConverter extends ResourceConverter {
    ImageStringConverter() {
      super(Image.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      return ResourceMap.loadImageIcon(param1String, param1ResourceMap).getImage();
    }
  }
  
  private static class KeyStrokeStringConverter extends ResourceConverter {
    KeyStrokeStringConverter() {
      super(KeyStroke.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) {
      if (param1String.contains("shortcut")) {
        int i = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        param1String = param1String.replaceAll("shortcut", (i == 4) ? "meta" : "control");
      } 
      return KeyStroke.getKeyStroke(param1String);
    }
  }
  
  private static List<Double> parseDoubles(String paramString1, int paramInt, String paramString2) throws ResourceConverter.ResourceConverterException {
    String[] arrayOfString = paramString1.split(",", paramInt + 1);
    if (arrayOfString.length != paramInt)
      throw new ResourceConverter.ResourceConverterException(paramString2, paramString1); 
    ArrayList<Double> arrayList = new ArrayList(paramInt);
    for (String str : arrayOfString) {
      try {
        arrayList.add(Double.valueOf(str));
      } catch (NumberFormatException numberFormatException) {
        throw new ResourceConverter.ResourceConverterException(paramString2, paramString1, numberFormatException);
      } 
    } 
    return arrayList;
  }
  
  private static class DimensionStringConverter extends ResourceConverter {
    DimensionStringConverter() {
      super(Dimension.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      List<Double> list = ResourceMap.parseDoubles(param1String, 2, "invalid x,y Dimension string");
      Dimension dimension = new Dimension();
      dimension.setSize(((Double)list.get(0)).doubleValue(), ((Double)list.get(1)).doubleValue());
      return dimension;
    }
  }
  
  private static class PointStringConverter extends ResourceConverter {
    PointStringConverter() {
      super(Point.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      List<Double> list = ResourceMap.parseDoubles(param1String, 2, "invalid x,y Point string");
      Point point = new Point();
      point.setLocation(((Double)list.get(0)).doubleValue(), ((Double)list.get(1)).doubleValue());
      return point;
    }
  }
  
  private static class RectangleStringConverter extends ResourceConverter {
    RectangleStringConverter() {
      super(Rectangle.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      List<Double> list = ResourceMap.parseDoubles(param1String, 4, "invalid x,y,width,height Rectangle string");
      Rectangle rectangle = new Rectangle();
      rectangle.setFrame(((Double)list.get(0)).doubleValue(), ((Double)list.get(1)).doubleValue(), ((Double)list.get(2)).doubleValue(), ((Double)list.get(3)).doubleValue());
      return rectangle;
    }
  }
  
  private static class InsetsStringConverter extends ResourceConverter {
    InsetsStringConverter() {
      super(Insets.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      List<Double> list = ResourceMap.parseDoubles(param1String, 4, "invalid top,left,bottom,right Insets string");
      return new Insets(((Double)list.get(0)).intValue(), ((Double)list.get(1)).intValue(), ((Double)list.get(2)).intValue(), ((Double)list.get(3)).intValue());
    }
  }
  
  private static class EmptyBorderStringConverter extends ResourceConverter {
    EmptyBorderStringConverter() {
      super(EmptyBorder.class);
    }
    
    public Object parseString(String param1String, ResourceMap param1ResourceMap) throws ResourceConverter.ResourceConverterException {
      List<Double> list = ResourceMap.parseDoubles(param1String, 4, "invalid top,left,bottom,right EmptyBorder string");
      return new EmptyBorder(((Double)list.get(0)).intValue(), ((Double)list.get(1)).intValue(), ((Double)list.get(2)).intValue(), ((Double)list.get(3)).intValue());
    }
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ResourceMap.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */