package com.mycompany.boniuk_math.com.itextpdf.text.pdf.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class AESCipherCBCnoPad {
  private BlockCipher cbc;
  
  public AESCipherCBCnoPad(boolean forEncryption, byte[] key) {
    AESFastEngine aESFastEngine = new AESFastEngine();
    this.cbc = (BlockCipher)new CBCBlockCipher((BlockCipher)aESFastEngine);
    KeyParameter kp = new KeyParameter(key);
    this.cbc.init(forEncryption, (CipherParameters)kp);
  }
  
  public byte[] processBlock(byte[] inp, int inpOff, int inpLen) {
    if (inpLen % this.cbc.getBlockSize() != 0)
      throw new IllegalArgumentException("Not multiple of block: " + inpLen); 
    byte[] outp = new byte[inpLen];
    int baseOffset = 0;
    while (inpLen > 0) {
      this.cbc.processBlock(inp, inpOff, outp, baseOffset);
      inpLen -= this.cbc.getBlockSize();
      baseOffset += this.cbc.getBlockSize();
      inpOff += this.cbc.getBlockSize();
    } 
    return outp;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\crypto\AESCipherCBCnoPad.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */