package com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode;

import java.util.HashMap;

public final class CharacterSetECI {
  private static HashMap<String, CharacterSetECI> NAME_TO_ECI;
  
  private final String encodingName;
  
  private final int value;
  
  private static void initialize() {
    HashMap<String, CharacterSetECI> n = new HashMap<String, CharacterSetECI>(29);
    addCharacterSet(0, "Cp437", n);
    addCharacterSet(1, new String[] { "ISO8859_1", "ISO-8859-1" }, n);
    addCharacterSet(2, "Cp437", n);
    addCharacterSet(3, new String[] { "ISO8859_1", "ISO-8859-1" }, n);
    addCharacterSet(4, new String[] { "ISO8859_2", "ISO-8859-2" }, n);
    addCharacterSet(5, new String[] { "ISO8859_3", "ISO-8859-3" }, n);
    addCharacterSet(6, new String[] { "ISO8859_4", "ISO-8859-4" }, n);
    addCharacterSet(7, new String[] { "ISO8859_5", "ISO-8859-5" }, n);
    addCharacterSet(8, new String[] { "ISO8859_6", "ISO-8859-6" }, n);
    addCharacterSet(9, new String[] { "ISO8859_7", "ISO-8859-7" }, n);
    addCharacterSet(10, new String[] { "ISO8859_8", "ISO-8859-8" }, n);
    addCharacterSet(11, new String[] { "ISO8859_9", "ISO-8859-9" }, n);
    addCharacterSet(12, new String[] { "ISO8859_10", "ISO-8859-10" }, n);
    addCharacterSet(13, new String[] { "ISO8859_11", "ISO-8859-11" }, n);
    addCharacterSet(15, new String[] { "ISO8859_13", "ISO-8859-13" }, n);
    addCharacterSet(16, new String[] { "ISO8859_14", "ISO-8859-14" }, n);
    addCharacterSet(17, new String[] { "ISO8859_15", "ISO-8859-15" }, n);
    addCharacterSet(18, new String[] { "ISO8859_16", "ISO-8859-16" }, n);
    addCharacterSet(20, new String[] { "SJIS", "Shift_JIS" }, n);
    NAME_TO_ECI = n;
  }
  
  private CharacterSetECI(int value, String encodingName) {
    this.encodingName = encodingName;
    this.value = value;
  }
  
  public String getEncodingName() {
    return this.encodingName;
  }
  
  public int getValue() {
    return this.value;
  }
  
  private static void addCharacterSet(int value, String encodingName, HashMap<String, CharacterSetECI> n) {
    CharacterSetECI eci = new CharacterSetECI(value, encodingName);
    n.put(encodingName, eci);
  }
  
  private static void addCharacterSet(int value, String[] encodingNames, HashMap<String, CharacterSetECI> n) {
    CharacterSetECI eci = new CharacterSetECI(value, encodingNames[0]);
    for (int i = 0; i < encodingNames.length; i++)
      n.put(encodingNames[i], eci); 
  }
  
  public static CharacterSetECI getCharacterSetECIByName(String name) {
    if (NAME_TO_ECI == null)
      initialize(); 
    return NAME_TO_ECI.get(name);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\qrcode\CharacterSetECI.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */