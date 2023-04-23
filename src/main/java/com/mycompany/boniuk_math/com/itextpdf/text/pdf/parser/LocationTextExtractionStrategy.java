package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LocationTextExtractionStrategy implements TextExtractionStrategy {
  static boolean DUMP_STATE = false;
  
  private final List<TextChunk> locationalResult = new ArrayList<TextChunk>();
  
  public void beginTextBlock() {}
  
  public void endTextBlock() {}
  
  public String getResultantText() {
    if (DUMP_STATE)
      dumpState(); 
    Collections.sort(this.locationalResult);
    StringBuffer sb = new StringBuffer();
    TextChunk lastChunk = null;
    for (TextChunk chunk : this.locationalResult) {
      if (lastChunk == null) {
        sb.append(chunk.text);
      } else if (chunk.sameLine(lastChunk)) {
        float dist = chunk.distanceFromEndOf(lastChunk);
        if (dist < -chunk.charSpaceWidth) {
          sb.append(' ');
        } else if (dist > chunk.charSpaceWidth / 2.0F && chunk.text.charAt(0) != ' ' && lastChunk.text.charAt(lastChunk.text.length() - 1) != ' ') {
          sb.append(' ');
        } 
        sb.append(chunk.text);
      } else {
        sb.append('\n');
        sb.append(chunk.text);
      } 
      lastChunk = chunk;
    } 
    return sb.toString();
  }
  
  private void dumpState() {
    for (Iterator<TextChunk> iterator = this.locationalResult.iterator(); iterator.hasNext(); ) {
      TextChunk location = iterator.next();
      location.printDiagnostics();
      System.out.println();
    } 
  }
  
  public void renderText(TextRenderInfo renderInfo) {
    LineSegment segment = renderInfo.getBaseline();
    TextChunk location = new TextChunk(renderInfo.getText(), segment.getStartPoint(), segment.getEndPoint(), renderInfo.getSingleSpaceWidth());
    this.locationalResult.add(location);
  }
  
  private static class TextChunk implements Comparable<TextChunk> {
    final String text;
    
    final Vector startLocation;
    
    final Vector endLocation;
    
    final Vector orientationVector;
    
    final int orientationMagnitude;
    
    final int distPerpendicular;
    
    final float distParallelStart;
    
    final float distParallelEnd;
    
    final float charSpaceWidth;
    
    public TextChunk(String string, Vector startLocation, Vector endLocation, float charSpaceWidth) {
      this.text = string;
      this.startLocation = startLocation;
      this.endLocation = endLocation;
      this.charSpaceWidth = charSpaceWidth;
      this.orientationVector = endLocation.subtract(startLocation).normalize();
      this.orientationMagnitude = (int)(Math.atan2(this.orientationVector.get(1), this.orientationVector.get(0)) * 1000.0D);
      Vector origin = new Vector(0.0F, 0.0F, 1.0F);
      this.distPerpendicular = (int)startLocation.subtract(origin).cross(this.orientationVector).get(2);
      this.distParallelStart = this.orientationVector.dot(startLocation);
      this.distParallelEnd = this.orientationVector.dot(endLocation);
    }
    
    private void printDiagnostics() {
      System.out.println("Text (@" + this.startLocation + " -> " + this.endLocation + "): " + this.text);
      System.out.println("orientationMagnitude: " + this.orientationMagnitude);
      System.out.println("distPerpendicular: " + this.distPerpendicular);
      System.out.println("distParallel: " + this.distParallelStart);
    }
    
    public boolean sameLine(TextChunk as) {
      if (this.orientationMagnitude != as.orientationMagnitude)
        return false; 
      if (this.distPerpendicular != as.distPerpendicular)
        return false; 
      return true;
    }
    
    public float distanceFromEndOf(TextChunk other) {
      float distance = this.distParallelStart - other.distParallelEnd;
      return distance;
    }
    
    public int compareTo(TextChunk rhs) {
      if (this == rhs)
        return 0; 
      int rslt = compareInts(this.orientationMagnitude, rhs.orientationMagnitude);
      if (rslt != 0)
        return rslt; 
      rslt = compareInts(this.distPerpendicular, rhs.distPerpendicular);
      if (rslt != 0)
        return rslt; 
      rslt = (this.distParallelStart < rhs.distParallelStart) ? -1 : 1;
      return rslt;
    }
    
    private static int compareInts(int int1, int int2) {
      return (int1 == int2) ? 0 : ((int1 < int2) ? -1 : 1);
    }
  }
  
  public void renderImage(ImageRenderInfo renderInfo) {}
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\LocationTextExtractionStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */