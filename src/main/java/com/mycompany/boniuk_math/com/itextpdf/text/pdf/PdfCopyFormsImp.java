package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.OutputStream;
import java.util.Map;

class PdfCopyFormsImp extends PdfCopyFieldsImp {
  PdfCopyFormsImp(OutputStream os) throws DocumentException {
    super(os);
  }
  
  public void copyDocumentFields(PdfReader reader) throws DocumentException {
    if (!reader.isOpenedWithFullPermissions())
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password", new Object[0])); 
    if (this.readers2intrefs.containsKey(reader)) {
      reader = new PdfReader(reader);
    } else {
      if (reader.isTampered())
        throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused", new Object[0])); 
      reader.consolidateNamedDestinations();
      reader.setTampered(true);
    } 
    reader.shuffleSubsetNames();
    this.readers2intrefs.put(reader, new IntHashtable());
    this.fields.add(reader.getAcroFields());
    updateCalculationOrder(reader);
  }
  
  void mergeFields() {
    for (int k = 0; k < this.fields.size(); k++) {
      Map<String, AcroFields.Item> fd = ((AcroFields)this.fields.get(k)).getFields();
      mergeWithMaster(fd);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfCopyFormsImp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */