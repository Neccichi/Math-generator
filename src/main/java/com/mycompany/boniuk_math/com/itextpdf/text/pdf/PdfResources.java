package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

class PdfResources extends PdfDictionary {
  void add(PdfName key, PdfDictionary resource) {
    if (resource.size() == 0)
      return; 
    PdfDictionary dic = getAsDict(key);
    if (dic == null) {
      put(key, resource);
    } else {
      dic.putAll(resource);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfResources.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */