package com.mycompany.boniuk_math.org.jdesktop.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ResourceManager extends AbstractBean {
  private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
  
  private final Map<String, ResourceMap> resourceMaps;
  
  private final ApplicationContext context;
  
  private List<String> applicationBundleNames = null;
  
  private ResourceMap appResourceMap = null;
  
  protected ResourceManager(ApplicationContext paramApplicationContext) {
    if (paramApplicationContext == null)
      throw new IllegalArgumentException("null context"); 
    this.context = paramApplicationContext;
    this.resourceMaps = new ConcurrentHashMap<String, ResourceMap>();
  }
  
  protected final ApplicationContext getContext() {
    return this.context;
  }
  
  private List<String> allBundleNames(Class paramClass1, Class paramClass2) {
    ArrayList<String> arrayList = new ArrayList();
    Class clazz1 = paramClass2.getSuperclass();
    for (Class clazz2 = paramClass1; clazz2 != clazz1; clazz2 = clazz2.getSuperclass())
      arrayList.addAll(getClassBundleNames(clazz2)); 
    return Collections.unmodifiableList(arrayList);
  }
  
  private String bundlePackageName(String paramString) {
    int i = paramString.lastIndexOf(".");
    return (i == -1) ? "" : paramString.substring(0, i);
  }
  
  private ResourceMap createResourceMapChain(ClassLoader paramClassLoader, ResourceMap paramResourceMap, ListIterator<String> paramListIterator) {
    if (!paramListIterator.hasNext())
      return paramResourceMap; 
    String str1 = paramListIterator.next();
    String str2 = bundlePackageName(str1);
    ArrayList<String> arrayList = new ArrayList();
    arrayList.add(str1);
    while (paramListIterator.hasNext()) {
      String str = paramListIterator.next();
      if (str2.equals(bundlePackageName(str))) {
        arrayList.add(str);
        continue;
      } 
      paramListIterator.previous();
    } 
    ResourceMap resourceMap = createResourceMapChain(paramClassLoader, paramResourceMap, paramListIterator);
    return createResourceMap(paramClassLoader, resourceMap, arrayList);
  }
  
  private ResourceMap getApplicationResourceMap() {
    if (this.appResourceMap == null) {
      List<String> list = getApplicationBundleNames();
      Class<Application> clazz = getContext().getApplicationClass();
      if (clazz == null) {
        logger.warning("getApplicationResourceMap(): no Application class");
        clazz = Application.class;
      } 
      ClassLoader classLoader = clazz.getClassLoader();
      this.appResourceMap = createResourceMapChain(classLoader, null, list.listIterator());
    } 
    return this.appResourceMap;
  }
  
  private ResourceMap getClassResourceMap(Class paramClass1, Class paramClass2) {
    String str = paramClass1.getName() + paramClass2.getName();
    ResourceMap resourceMap = this.resourceMaps.get(str);
    if (resourceMap == null) {
      List<String> list = allBundleNames(paramClass1, paramClass2);
      ClassLoader classLoader = paramClass1.getClassLoader();
      ResourceMap resourceMap1 = getResourceMap();
      resourceMap = createResourceMapChain(classLoader, resourceMap1, list.listIterator());
      this.resourceMaps.put(str, resourceMap);
    } 
    return resourceMap;
  }
  
  public ResourceMap getResourceMap(Class<?> paramClass1, Class paramClass2) {
    if (paramClass1 == null)
      throw new IllegalArgumentException("null startClass"); 
    if (paramClass2 == null)
      throw new IllegalArgumentException("null stopClass"); 
    if (!paramClass2.isAssignableFrom(paramClass1))
      throw new IllegalArgumentException("startClass is not a subclass, or the same as, stopClass"); 
    return getClassResourceMap(paramClass1, paramClass2);
  }
  
  public final ResourceMap getResourceMap(Class paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException("null class"); 
    return getResourceMap(paramClass, paramClass);
  }
  
  public ResourceMap getResourceMap() {
    return getApplicationResourceMap();
  }
  
  public List<String> getApplicationBundleNames() {
    if (this.applicationBundleNames == null) {
      Class clazz = getContext().getApplicationClass();
      if (clazz == null)
        return allBundleNames(Application.class, Application.class); 
      this.applicationBundleNames = allBundleNames(clazz, Application.class);
    } 
    return this.applicationBundleNames;
  }
  
  public void setApplicationBundleNames(List<String> paramList) {
    if (paramList != null)
      for (String str : paramList) {
        if (str == null || paramList.size() == 0)
          throw new IllegalArgumentException("invalid bundle name \"" + str + "\""); 
      }  
    List<String> list = this.applicationBundleNames;
    if (paramList != null) {
      this.applicationBundleNames = Collections.unmodifiableList(new ArrayList<String>(paramList));
    } else {
      this.applicationBundleNames = null;
    } 
    this.resourceMaps.clear();
    firePropertyChange("applicationBundleNames", list, this.applicationBundleNames);
  }
  
  private String classBundleBaseName(Class paramClass) {
    String str = paramClass.getName();
    StringBuffer stringBuffer = new StringBuffer();
    int i = str.lastIndexOf('.');
    if (i > 0) {
      stringBuffer.append(str.substring(0, i));
      stringBuffer.append(".resources.");
      stringBuffer.append(paramClass.getSimpleName());
    } else {
      stringBuffer.append("resources.");
      stringBuffer.append(paramClass.getSimpleName());
    } 
    return stringBuffer.toString();
  }
  
  protected List<String> getClassBundleNames(Class paramClass) {
    String str = classBundleBaseName(paramClass);
    return Collections.singletonList(str);
  }
  
  protected ResourceMap createResourceMap(ClassLoader paramClassLoader, ResourceMap paramResourceMap, List<String> paramList) {
    return new ResourceMap(paramResourceMap, paramClassLoader, paramList);
  }
  
  public String getPlatform() {
    return getResourceMap().getString("platform", new Object[0]);
  }
  
  public void setPlatform(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("null platform"); 
    getResourceMap().putResource("platform", paramString);
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\ResourceManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */