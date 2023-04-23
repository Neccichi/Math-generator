package com.mycompany.boniuk_math.com.itextpdf.text.pdf.qrcode;

final class GF256Poly {
  private final GF256 field;
  
  private final int[] coefficients;
  
  GF256Poly(GF256 field, int[] coefficients) {
    if (coefficients == null || coefficients.length == 0)
      throw new IllegalArgumentException(); 
    this.field = field;
    int coefficientsLength = coefficients.length;
    if (coefficientsLength > 1 && coefficients[0] == 0) {
      int firstNonZero = 1;
      while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0)
        firstNonZero++; 
      if (firstNonZero == coefficientsLength) {
        this.coefficients = (field.getZero()).coefficients;
      } else {
        this.coefficients = new int[coefficientsLength - firstNonZero];
        System.arraycopy(coefficients, firstNonZero, this.coefficients, 0, this.coefficients.length);
      } 
    } else {
      this.coefficients = coefficients;
    } 
  }
  
  int[] getCoefficients() {
    return this.coefficients;
  }
  
  int getDegree() {
    return this.coefficients.length - 1;
  }
  
  boolean isZero() {
    return (this.coefficients[0] == 0);
  }
  
  int getCoefficient(int degree) {
    return this.coefficients[this.coefficients.length - 1 - degree];
  }
  
  int evaluateAt(int a) {
    if (a == 0)
      return getCoefficient(0); 
    int size = this.coefficients.length;
    if (a == 1) {
      int j = 0;
      for (int k = 0; k < size; k++)
        j = GF256.addOrSubtract(j, this.coefficients[k]); 
      return j;
    } 
    int result = this.coefficients[0];
    for (int i = 1; i < size; i++)
      result = GF256.addOrSubtract(this.field.multiply(a, result), this.coefficients[i]); 
    return result;
  }
  
  GF256Poly addOrSubtract(GF256Poly other) {
    if (!this.field.equals(other.field))
      throw new IllegalArgumentException("GF256Polys do not have same GF256 field"); 
    if (isZero())
      return other; 
    if (other.isZero())
      return this; 
    int[] smallerCoefficients = this.coefficients;
    int[] largerCoefficients = other.coefficients;
    if (smallerCoefficients.length > largerCoefficients.length) {
      int[] temp = smallerCoefficients;
      smallerCoefficients = largerCoefficients;
      largerCoefficients = temp;
    } 
    int[] sumDiff = new int[largerCoefficients.length];
    int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
    System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);
    for (int i = lengthDiff; i < largerCoefficients.length; i++)
      sumDiff[i] = GF256.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]); 
    return new GF256Poly(this.field, sumDiff);
  }
  
  GF256Poly multiply(GF256Poly other) {
    if (!this.field.equals(other.field))
      throw new IllegalArgumentException("GF256Polys do not have same GF256 field"); 
    if (isZero() || other.isZero())
      return this.field.getZero(); 
    int[] aCoefficients = this.coefficients;
    int aLength = aCoefficients.length;
    int[] bCoefficients = other.coefficients;
    int bLength = bCoefficients.length;
    int[] product = new int[aLength + bLength - 1];
    for (int i = 0; i < aLength; i++) {
      int aCoeff = aCoefficients[i];
      for (int j = 0; j < bLength; j++)
        product[i + j] = GF256.addOrSubtract(product[i + j], this.field.multiply(aCoeff, bCoefficients[j])); 
    } 
    return new GF256Poly(this.field, product);
  }
  
  GF256Poly multiply(int scalar) {
    if (scalar == 0)
      return this.field.getZero(); 
    if (scalar == 1)
      return this; 
    int size = this.coefficients.length;
    int[] product = new int[size];
    for (int i = 0; i < size; i++)
      product[i] = this.field.multiply(this.coefficients[i], scalar); 
    return new GF256Poly(this.field, product);
  }
  
  GF256Poly multiplyByMonomial(int degree, int coefficient) {
    if (degree < 0)
      throw new IllegalArgumentException(); 
    if (coefficient == 0)
      return this.field.getZero(); 
    int size = this.coefficients.length;
    int[] product = new int[size + degree];
    for (int i = 0; i < size; i++)
      product[i] = this.field.multiply(this.coefficients[i], coefficient); 
    return new GF256Poly(this.field, product);
  }
  
  GF256Poly[] divide(GF256Poly other) {
    if (!this.field.equals(other.field))
      throw new IllegalArgumentException("GF256Polys do not have same GF256 field"); 
    if (other.isZero())
      throw new IllegalArgumentException("Divide by 0"); 
    GF256Poly quotient = this.field.getZero();
    GF256Poly remainder = this;
    int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
    int inverseDenominatorLeadingTerm = this.field.inverse(denominatorLeadingTerm);
    while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
      int degreeDifference = remainder.getDegree() - other.getDegree();
      int scale = this.field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
      GF256Poly term = other.multiplyByMonomial(degreeDifference, scale);
      GF256Poly iterationQuotient = this.field.buildMonomial(degreeDifference, scale);
      quotient = quotient.addOrSubtract(iterationQuotient);
      remainder = remainder.addOrSubtract(term);
    } 
    return new GF256Poly[] { quotient, remainder };
  }
  
  public String toString() {
    StringBuffer result = new StringBuffer(8 * getDegree());
    for (int degree = getDegree(); degree >= 0; degree--) {
      int coefficient = getCoefficient(degree);
      if (coefficient != 0) {
        if (coefficient < 0) {
          result.append(" - ");
          coefficient = -coefficient;
        } else if (result.length() > 0) {
          result.append(" + ");
        } 
        if (degree == 0 || coefficient != 1) {
          int alphaPower = this.field.log(coefficient);
          if (alphaPower == 0) {
            result.append('1');
          } else if (alphaPower == 1) {
            result.append('a');
          } else {
            result.append("a^");
            result.append(alphaPower);
          } 
        } 
        if (degree != 0)
          if (degree == 1) {
            result.append('x');
          } else {
            result.append("x^");
            result.append(degree);
          }  
      } 
    } 
    return result.toString();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\qrcode\GF256Poly.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */