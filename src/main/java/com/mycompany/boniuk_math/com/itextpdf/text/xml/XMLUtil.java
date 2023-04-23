package com.mycompany.boniuk_math.com.itextpdf.text.xml;

public class XMLUtil {
  public static String escapeXML(String s, boolean onlyASCII) {
    char[] cc = s.toCharArray();
    int len = cc.length;
    StringBuffer sb = new StringBuffer();
    for (int k = 0; k < len; k++) {
      int c = cc[k];
      switch (c) {
        case 60:
          sb.append("&lt;");
          break;
        case 62:
          sb.append("&gt;");
          break;
        case 38:
          sb.append("&amp;");
          break;
        case 34:
          sb.append("&quot;");
          break;
        case 39:
          sb.append("&apos;");
          break;
        default:
          if (c == 9 || c == 10 || c == 13 || (c >= 32 && c <= 55295) || (c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111)) {
            if (onlyASCII && c > 127) {
              sb.append("&#").append(c).append(';');
              break;
            } 
            sb.append((char)c);
          } 
          break;
      } 
    } 
    return sb.toString();
  }
  
  public static String getEncodingName(byte[] b4) {
    int b0 = b4[0] & 0xFF;
    int b1 = b4[1] & 0xFF;
    if (b0 == 254 && b1 == 255)
      return "UTF-16BE"; 
    if (b0 == 255 && b1 == 254)
      return "UTF-16LE"; 
    int b2 = b4[2] & 0xFF;
    if (b0 == 239 && b1 == 187 && b2 == 191)
      return "UTF-8"; 
    int b3 = b4[3] & 0xFF;
    if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60)
      return "ISO-10646-UCS-4"; 
    if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0)
      return "ISO-10646-UCS-4"; 
    if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0)
      return "ISO-10646-UCS-4"; 
    if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0)
      return "ISO-10646-UCS-4"; 
    if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63)
      return "UTF-16BE"; 
    if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0)
      return "UTF-16LE"; 
    if (b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148)
      return "CP037"; 
    return "UTF-8";
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\XMLUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */