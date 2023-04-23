package com.mycompany.boniuk_math.org.jdesktop.application;

import java.text.StringCharacterIterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JLabel;

class MnemonicText {
  private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
  
  public static void configure(Object paramObject, String paramString) {
    String str = paramString;
    int i = -1;
    int j = 0;
    int k = mnemonicMarkerIndex(paramString, '&');
    if (k == -1)
      k = mnemonicMarkerIndex(paramString, '_'); 
    if (k != -1) {
      str = str.substring(0, k) + str.substring(k + 1);
      i = k;
      StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(paramString, k);
      j = mnemonicKey(stringCharacterIterator.next());
    } 
    if (paramObject instanceof Action) {
      configureAction((Action)paramObject, str, j, i);
    } else if (paramObject instanceof AbstractButton) {
      configureButton((AbstractButton)paramObject, str, j, i);
    } else if (paramObject instanceof JLabel) {
      configureLabel((JLabel)paramObject, str, j, i);
    } else {
      throw new IllegalArgumentException("unrecognized target type " + paramObject);
    } 
  }
  
  private static int mnemonicMarkerIndex(String paramString, char paramChar) {
    if (paramString == null || paramString.length() < 2)
      return -1; 
    StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(paramString);
    int i = 0;
    while (i != -1) {
      i = paramString.indexOf(paramChar, i);
      if (i != -1) {
        stringCharacterIterator.setIndex(i);
        char c1 = stringCharacterIterator.previous();
        stringCharacterIterator.setIndex(i);
        char c2 = stringCharacterIterator.next();
        boolean bool = (c1 == '\'' && c2 == '\'') ? true : false;
        boolean bool1 = Character.isWhitespace(c2);
        if (!bool && !bool1 && c2 != Character.MAX_VALUE)
          return i; 
      } 
      if (i != -1)
        i++; 
    } 
    return -1;
  }
  
  private static int mnemonicKey(char paramChar) {
    char c = paramChar;
    if (c >= 'a' && c <= 'z')
      c -= ' '; 
    return c;
  }
  
  private static void configureAction(Action paramAction, String paramString, int paramInt1, int paramInt2) {
    paramAction.putValue("Name", paramString);
    if (paramInt1 != 0)
      paramAction.putValue("MnemonicKey", Integer.valueOf(paramInt1)); 
    if (paramInt2 != -1)
      paramAction.putValue("SwingDisplayedMnemonicIndexKey", Integer.valueOf(paramInt2)); 
  }
  
  private static void configureButton(AbstractButton paramAbstractButton, String paramString, int paramInt1, int paramInt2) {
    paramAbstractButton.setText(paramString);
    if (paramInt1 != 0)
      paramAbstractButton.setMnemonic(paramInt1); 
    if (paramInt2 != -1)
      paramAbstractButton.setDisplayedMnemonicIndex(paramInt2); 
  }
  
  private static void configureLabel(JLabel paramJLabel, String paramString, int paramInt1, int paramInt2) {
    paramJLabel.setText(paramString);
    if (paramInt1 != 0)
      paramJLabel.setDisplayedMnemonic(paramInt1); 
    if (paramInt2 != -1)
      paramJLabel.setDisplayedMnemonicIndex(paramInt2); 
  }
}


/* Location:              C:\Users\windo\Desktop\appframework-1.0.3.jar!\org\jdesktop\application\MnemonicText.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */