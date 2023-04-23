package com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec;

import java.io.IOException;
import java.io.OutputStream;

public class BitFile {
  OutputStream output_;
  
  byte[] buffer_;
  
  int index_;
  
  int bitsLeft_;
  
  boolean blocks_ = false;
  
  public BitFile(OutputStream output, boolean blocks) {
    this.output_ = output;
    this.blocks_ = blocks;
    this.buffer_ = new byte[256];
    this.index_ = 0;
    this.bitsLeft_ = 8;
  }
  
  public void flush() throws IOException {
    int numBytes = this.index_ + ((this.bitsLeft_ == 8) ? 0 : 1);
    if (numBytes > 0) {
      if (this.blocks_)
        this.output_.write(numBytes); 
      this.output_.write(this.buffer_, 0, numBytes);
      this.buffer_[0] = 0;
      this.index_ = 0;
      this.bitsLeft_ = 8;
    } 
  }
  
  public void writeBits(int bits, int numbits) throws IOException {
    int bitsWritten = 0;
    int numBytes = 255;
    do {
      if ((this.index_ == 254 && this.bitsLeft_ == 0) || this.index_ > 254) {
        if (this.blocks_)
          this.output_.write(numBytes); 
        this.output_.write(this.buffer_, 0, numBytes);
        this.buffer_[0] = 0;
        this.index_ = 0;
        this.bitsLeft_ = 8;
      } 
      if (numbits <= this.bitsLeft_) {
        if (this.blocks_) {
          this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << numbits) - 1) << 8 - this.bitsLeft_);
          bitsWritten += numbits;
          this.bitsLeft_ -= numbits;
          numbits = 0;
        } else {
          this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << numbits) - 1) << this.bitsLeft_ - numbits);
          bitsWritten += numbits;
          this.bitsLeft_ -= numbits;
          numbits = 0;
        } 
      } else if (this.blocks_) {
        this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << this.bitsLeft_) - 1) << 8 - this.bitsLeft_);
        bitsWritten += this.bitsLeft_;
        bits >>= this.bitsLeft_;
        numbits -= this.bitsLeft_;
        this.buffer_[++this.index_] = 0;
        this.bitsLeft_ = 8;
      } else {
        int topbits = bits >>> numbits - this.bitsLeft_ & (1 << this.bitsLeft_) - 1;
        this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | topbits);
        numbits -= this.bitsLeft_;
        bitsWritten += this.bitsLeft_;
        this.buffer_[++this.index_] = 0;
        this.bitsLeft_ = 8;
      } 
    } while (numbits != 0);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\codec\BitFile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */