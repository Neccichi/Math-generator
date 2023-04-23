package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.CMapAwareDocumentFont;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PRIndirectReference;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PRTokeniser;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfContentParser;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfIndirectReference;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfLiteral;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfStream;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PdfContentStreamProcessor {
  public static final String DEFAULTOPERATOR = "DefaultOperator";
  
  private final Map<String, ContentOperator> operators;
  
  private ResourceDictionary resources;
  
  private final Stack<GraphicsState> gsStack = new Stack<GraphicsState>();
  
  private Matrix textMatrix;
  
  private Matrix textLineMatrix;
  
  private final RenderListener renderListener;
  
  private final Map<PdfName, XObjectDoHandler> xobjectDoHandlers;
  
  private final Map<Integer, CMapAwareDocumentFont> cachedFonts = new HashMap<Integer, CMapAwareDocumentFont>();
  
  private final Stack<MarkedContentInfo> markedContentStack = new Stack<MarkedContentInfo>();
  
  public PdfContentStreamProcessor(RenderListener renderListener) {
    this.renderListener = renderListener;
    this.operators = new HashMap<String, ContentOperator>();
    populateOperators();
    this.xobjectDoHandlers = new HashMap<PdfName, XObjectDoHandler>();
    populateXObjectDoHandlers();
    reset();
  }
  
  private void populateXObjectDoHandlers() {
    registerXObjectDoHandler(PdfName.DEFAULT, new IgnoreXObjectDoHandler());
    registerXObjectDoHandler(PdfName.FORM, new FormXObjectDoHandler());
    registerXObjectDoHandler(PdfName.IMAGE, new ImageXObjectDoHandler());
  }
  
  public XObjectDoHandler registerXObjectDoHandler(PdfName xobjectSubType, XObjectDoHandler handler) {
    return this.xobjectDoHandlers.put(xobjectSubType, handler);
  }
  
  public CMapAwareDocumentFont getFont(PRIndirectReference ind) {
    Integer n = Integer.valueOf(ind.getNumber());
    CMapAwareDocumentFont font = this.cachedFonts.get(n);
    if (font == null) {
      font = new CMapAwareDocumentFont(ind);
      this.cachedFonts.put(n, font);
    } 
    return font;
  }
  
  private void populateOperators() {
    registerContentOperator("DefaultOperator", new IgnoreOperatorContentOperator());
    registerContentOperator("q", new PushGraphicsState());
    registerContentOperator("Q", new PopGraphicsState());
    registerContentOperator("cm", new ModifyCurrentTransformationMatrix());
    registerContentOperator("gs", new ProcessGraphicsStateResource());
    SetTextCharacterSpacing tcOperator = new SetTextCharacterSpacing();
    registerContentOperator("Tc", tcOperator);
    SetTextWordSpacing twOperator = new SetTextWordSpacing();
    registerContentOperator("Tw", twOperator);
    registerContentOperator("Tz", new SetTextHorizontalScaling());
    SetTextLeading tlOperator = new SetTextLeading();
    registerContentOperator("TL", tlOperator);
    registerContentOperator("Tf", new SetTextFont());
    registerContentOperator("Tr", new SetTextRenderMode());
    registerContentOperator("Ts", new SetTextRise());
    registerContentOperator("BT", new BeginText());
    registerContentOperator("ET", new EndText());
    registerContentOperator("BMC", new BeginMarkedContent());
    registerContentOperator("BDC", new BeginMarkedContentDictionary());
    registerContentOperator("EMC", new EndMarkedContent());
    TextMoveStartNextLine tdOperator = new TextMoveStartNextLine();
    registerContentOperator("Td", tdOperator);
    registerContentOperator("TD", new TextMoveStartNextLineWithLeading(tdOperator, tlOperator));
    registerContentOperator("Tm", new TextSetTextMatrix());
    TextMoveNextLine tstarOperator = new TextMoveNextLine(tdOperator);
    registerContentOperator("T*", tstarOperator);
    ShowText tjOperator = new ShowText();
    registerContentOperator("Tj", new ShowText());
    MoveNextLineAndShowText tickOperator = new MoveNextLineAndShowText(tstarOperator, tjOperator);
    registerContentOperator("'", tickOperator);
    registerContentOperator("\"", new MoveNextLineAndShowTextWithSpacing(twOperator, tcOperator, tickOperator));
    registerContentOperator("TJ", new ShowTextArray());
    registerContentOperator("Do", new Do());
  }
  
  public ContentOperator registerContentOperator(String operatorString, ContentOperator operator) {
    return this.operators.put(operatorString, operator);
  }
  
  public void reset() {
    this.gsStack.removeAllElements();
    this.gsStack.add(new GraphicsState());
    this.textMatrix = null;
    this.textLineMatrix = null;
    this.resources = new ResourceDictionary();
  }
  
  private GraphicsState gs() {
    return this.gsStack.peek();
  }
  
  private void invokeOperator(PdfLiteral operator, ArrayList<PdfObject> operands) throws Exception {
    ContentOperator op = this.operators.get(operator.toString());
    if (op == null)
      op = this.operators.get("DefaultOperator"); 
    op.invoke(this, operator, operands);
  }
  
  private void beginMarkedContent(PdfName tag, PdfDictionary dict) {
    this.markedContentStack.push(new MarkedContentInfo(tag, dict));
  }
  
  private void endMarkedContent() {
    this.markedContentStack.pop();
  }
  
  private String decode(PdfString in) {
    byte[] bytes = in.getBytes();
    return (gs()).font.decode(bytes, 0, bytes.length);
  }
  
  private void beginText() {
    this.renderListener.beginTextBlock();
  }
  
  private void endText() {
    this.renderListener.endTextBlock();
  }
  
  private void displayPdfString(PdfString string) {
    String unicode = decode(string);
    TextRenderInfo renderInfo = new TextRenderInfo(unicode, gs(), this.textMatrix, this.markedContentStack);
    this.renderListener.renderText(renderInfo);
    this.textMatrix = (new Matrix(renderInfo.getUnscaledWidth(), 0.0F)).multiply(this.textMatrix);
  }
  
  private void displayXObject(PdfName xobjectName) throws IOException {
    PdfDictionary xobjects = this.resources.getAsDict(PdfName.XOBJECT);
    PdfObject xobject = xobjects.getDirectObject(xobjectName);
    PdfStream xobjectStream = (PdfStream)xobject;
    PdfName subType = xobjectStream.getAsName(PdfName.SUBTYPE);
    if (xobject.isStream()) {
      XObjectDoHandler handler = this.xobjectDoHandlers.get(subType);
      if (handler == null)
        handler = this.xobjectDoHandlers.get(PdfName.DEFAULT); 
      handler.handleXObject(this, xobjectStream, xobjects.getAsIndirectObject(xobjectName));
    } else {
      throw new IllegalStateException(MessageLocalization.getComposedMessage("XObject.1.is.not.a.stream", new Object[] { xobjectName }));
    } 
  }
  
  private void applyTextAdjust(float tj) {
    float adjustBy = -tj / 1000.0F * (gs()).fontSize * (gs()).horizontalScaling;
    this.textMatrix = (new Matrix(adjustBy, 0.0F)).multiply(this.textMatrix);
  }
  
  public void processContent(byte[] contentBytes, PdfDictionary resources) {
    this.resources.push(resources);
    try {
      PRTokeniser tokeniser = new PRTokeniser(contentBytes);
      PdfContentParser ps = new PdfContentParser(tokeniser);
      ArrayList<PdfObject> operands = new ArrayList<PdfObject>();
      while (ps.parse(operands).size() > 0) {
        PdfLiteral operator = (PdfLiteral)operands.get(operands.size() - 1);
        if ("BI".equals(operator.toString())) {
          PdfDictionary colorSpaceDic = resources.getAsDict(PdfName.COLORSPACE);
          ImageRenderInfo renderInfo = ImageRenderInfo.createdForEmbeddedImage((gs()).ctm, InlineImageUtils.parseInlineImage(ps, colorSpaceDic));
          this.renderListener.renderImage(renderInfo);
          continue;
        } 
        invokeOperator(operator, operands);
      } 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
    this.resources.pop();
  }
  
  private static class ResourceDictionary extends PdfDictionary {
    private final List<PdfDictionary> resourcesStack = new ArrayList<PdfDictionary>();
    
    public void push(PdfDictionary resources) {
      this.resourcesStack.add(resources);
    }
    
    public void pop() {
      this.resourcesStack.remove(this.resourcesStack.size() - 1);
    }
    
    public PdfObject getDirectObject(PdfName key) {
      for (int i = this.resourcesStack.size() - 1; i >= 0; i--) {
        PdfDictionary subResource = this.resourcesStack.get(i);
        if (subResource != null) {
          PdfObject obj = subResource.getDirectObject(key);
          if (obj != null)
            return obj; 
        } 
      } 
      return super.getDirectObject(key);
    }
  }
  
  private static class IgnoreOperatorContentOperator implements ContentOperator {
    private IgnoreOperatorContentOperator() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {}
  }
  
  private static class ShowTextArray implements ContentOperator {
    private ShowTextArray() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfArray array = (PdfArray)operands.get(0);
      float tj = 0.0F;
      for (Iterator<PdfObject> i = array.listIterator(); i.hasNext(); ) {
        PdfObject entryObj = i.next();
        if (entryObj instanceof PdfString) {
          processor.displayPdfString((PdfString)entryObj);
          tj = 0.0F;
          continue;
        } 
        tj = ((PdfNumber)entryObj).floatValue();
        processor.applyTextAdjust(tj);
      } 
    }
  }
  
  private static class MoveNextLineAndShowTextWithSpacing implements ContentOperator {
    private final PdfContentStreamProcessor.SetTextWordSpacing setTextWordSpacing;
    
    private final PdfContentStreamProcessor.SetTextCharacterSpacing setTextCharacterSpacing;
    
    private final PdfContentStreamProcessor.MoveNextLineAndShowText moveNextLineAndShowText;
    
    public MoveNextLineAndShowTextWithSpacing(PdfContentStreamProcessor.SetTextWordSpacing setTextWordSpacing, PdfContentStreamProcessor.SetTextCharacterSpacing setTextCharacterSpacing, PdfContentStreamProcessor.MoveNextLineAndShowText moveNextLineAndShowText) {
      this.setTextWordSpacing = setTextWordSpacing;
      this.setTextCharacterSpacing = setTextCharacterSpacing;
      this.moveNextLineAndShowText = moveNextLineAndShowText;
    }
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber aw = (PdfNumber)operands.get(0);
      PdfNumber ac = (PdfNumber)operands.get(1);
      PdfString string = (PdfString)operands.get(2);
      ArrayList<PdfObject> twOperands = new ArrayList<PdfObject>(1);
      twOperands.add(0, aw);
      this.setTextWordSpacing.invoke(processor, null, twOperands);
      ArrayList<PdfObject> tcOperands = new ArrayList<PdfObject>(1);
      tcOperands.add(0, ac);
      this.setTextCharacterSpacing.invoke(processor, null, tcOperands);
      ArrayList<PdfObject> tickOperands = new ArrayList<PdfObject>(1);
      tickOperands.add(0, string);
      this.moveNextLineAndShowText.invoke(processor, null, tickOperands);
    }
  }
  
  private static class MoveNextLineAndShowText implements ContentOperator {
    private final PdfContentStreamProcessor.TextMoveNextLine textMoveNextLine;
    
    private final PdfContentStreamProcessor.ShowText showText;
    
    public MoveNextLineAndShowText(PdfContentStreamProcessor.TextMoveNextLine textMoveNextLine, PdfContentStreamProcessor.ShowText showText) {
      this.textMoveNextLine = textMoveNextLine;
      this.showText = showText;
    }
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      this.textMoveNextLine.invoke(processor, null, new ArrayList<PdfObject>(0));
      this.showText.invoke(processor, null, operands);
    }
  }
  
  private static class ShowText implements ContentOperator {
    private ShowText() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfString string = (PdfString)operands.get(0);
      processor.displayPdfString(string);
    }
  }
  
  private static class TextMoveNextLine implements ContentOperator {
    private final PdfContentStreamProcessor.TextMoveStartNextLine moveStartNextLine;
    
    public TextMoveNextLine(PdfContentStreamProcessor.TextMoveStartNextLine moveStartNextLine) {
      this.moveStartNextLine = moveStartNextLine;
    }
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      ArrayList<PdfObject> tdoperands = new ArrayList<PdfObject>(2);
      tdoperands.add(0, new PdfNumber(0));
      tdoperands.add(1, new PdfNumber(-(processor.gs()).leading));
      this.moveStartNextLine.invoke(processor, null, tdoperands);
    }
  }
  
  private static class TextSetTextMatrix implements ContentOperator {
    private TextSetTextMatrix() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      float a = ((PdfNumber)operands.get(0)).floatValue();
      float b = ((PdfNumber)operands.get(1)).floatValue();
      float c = ((PdfNumber)operands.get(2)).floatValue();
      float d = ((PdfNumber)operands.get(3)).floatValue();
      float e = ((PdfNumber)operands.get(4)).floatValue();
      float f = ((PdfNumber)operands.get(5)).floatValue();
      processor.textLineMatrix = new Matrix(a, b, c, d, e, f);
      processor.textMatrix = processor.textLineMatrix;
    }
  }
  
  private static class TextMoveStartNextLineWithLeading implements ContentOperator {
    private final PdfContentStreamProcessor.TextMoveStartNextLine moveStartNextLine;
    
    private final PdfContentStreamProcessor.SetTextLeading setTextLeading;
    
    public TextMoveStartNextLineWithLeading(PdfContentStreamProcessor.TextMoveStartNextLine moveStartNextLine, PdfContentStreamProcessor.SetTextLeading setTextLeading) {
      this.moveStartNextLine = moveStartNextLine;
      this.setTextLeading = setTextLeading;
    }
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      float ty = ((PdfNumber)operands.get(1)).floatValue();
      ArrayList<PdfObject> tlOperands = new ArrayList<PdfObject>(1);
      tlOperands.add(0, new PdfNumber(-ty));
      this.setTextLeading.invoke(processor, null, tlOperands);
      this.moveStartNextLine.invoke(processor, null, operands);
    }
  }
  
  private static class TextMoveStartNextLine implements ContentOperator {
    private TextMoveStartNextLine() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      float tx = ((PdfNumber)operands.get(0)).floatValue();
      float ty = ((PdfNumber)operands.get(1)).floatValue();
      Matrix translationMatrix = new Matrix(tx, ty);
      processor.textMatrix = translationMatrix.multiply(processor.textLineMatrix);
      processor.textLineMatrix = processor.textMatrix;
    }
  }
  
  private static class SetTextFont implements ContentOperator {
    private SetTextFont() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfName fontResourceName = (PdfName)operands.get(0);
      float size = ((PdfNumber)operands.get(1)).floatValue();
      PdfDictionary fontsDictionary = processor.resources.getAsDict(PdfName.FONT);
      CMapAwareDocumentFont font = processor.getFont((PRIndirectReference)fontsDictionary.get(fontResourceName));
      (processor.gs()).font = font;
      (processor.gs()).fontSize = size;
    }
  }
  
  private static class SetTextRenderMode implements ContentOperator {
    private SetTextRenderMode() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber render = (PdfNumber)operands.get(0);
      (processor.gs()).renderMode = render.intValue();
    }
  }
  
  private static class SetTextRise implements ContentOperator {
    private SetTextRise() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber rise = (PdfNumber)operands.get(0);
      (processor.gs()).rise = rise.floatValue();
    }
  }
  
  private static class SetTextLeading implements ContentOperator {
    private SetTextLeading() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber leading = (PdfNumber)operands.get(0);
      (processor.gs()).leading = leading.floatValue();
    }
  }
  
  private static class SetTextHorizontalScaling implements ContentOperator {
    private SetTextHorizontalScaling() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber scale = (PdfNumber)operands.get(0);
      (processor.gs()).horizontalScaling = scale.floatValue() / 100.0F;
    }
  }
  
  private static class SetTextCharacterSpacing implements ContentOperator {
    private SetTextCharacterSpacing() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber charSpace = (PdfNumber)operands.get(0);
      (processor.gs()).characterSpacing = charSpace.floatValue();
    }
  }
  
  private static class SetTextWordSpacing implements ContentOperator {
    private SetTextWordSpacing() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfNumber wordSpace = (PdfNumber)operands.get(0);
      (processor.gs()).wordSpacing = wordSpace.floatValue();
    }
  }
  
  private static class ProcessGraphicsStateResource implements ContentOperator {
    private ProcessGraphicsStateResource() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      PdfName dictionaryName = (PdfName)operands.get(0);
      PdfDictionary extGState = processor.resources.getAsDict(PdfName.EXTGSTATE);
      if (extGState == null)
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("resources.do.not.contain.extgstate.entry.unable.to.process.operator.1", new Object[] { operator })); 
      PdfDictionary gsDic = extGState.getAsDict(dictionaryName);
      if (gsDic == null)
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("1.is.an.unknown.graphics.state.dictionary", new Object[] { dictionaryName })); 
      PdfArray fontParameter = gsDic.getAsArray(PdfName.FONT);
      if (fontParameter != null) {
        CMapAwareDocumentFont font = processor.getFont((PRIndirectReference)fontParameter.getPdfObject(0));
        float size = fontParameter.getAsNumber(1).floatValue();
        (processor.gs()).font = font;
        (processor.gs()).fontSize = size;
      } 
    }
  }
  
  private static class PushGraphicsState implements ContentOperator {
    private PushGraphicsState() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      GraphicsState gs = processor.gsStack.peek();
      GraphicsState copy = new GraphicsState(gs);
      processor.gsStack.push(copy);
    }
  }
  
  private static class ModifyCurrentTransformationMatrix implements ContentOperator {
    private ModifyCurrentTransformationMatrix() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      float a = ((PdfNumber)operands.get(0)).floatValue();
      float b = ((PdfNumber)operands.get(1)).floatValue();
      float c = ((PdfNumber)operands.get(2)).floatValue();
      float d = ((PdfNumber)operands.get(3)).floatValue();
      float e = ((PdfNumber)operands.get(4)).floatValue();
      float f = ((PdfNumber)operands.get(5)).floatValue();
      Matrix matrix = new Matrix(a, b, c, d, e, f);
      GraphicsState gs = processor.gsStack.peek();
      gs.ctm = matrix.multiply(gs.ctm);
    }
  }
  
  private static class PopGraphicsState implements ContentOperator {
    private PopGraphicsState() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      processor.gsStack.pop();
    }
  }
  
  private static class BeginText implements ContentOperator {
    private BeginText() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      processor.textMatrix = new Matrix();
      processor.textLineMatrix = processor.textMatrix;
      processor.beginText();
    }
  }
  
  private static class EndText implements ContentOperator {
    private EndText() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) {
      processor.textMatrix = null;
      processor.textLineMatrix = null;
      processor.endText();
    }
  }
  
  private static class BeginMarkedContent implements ContentOperator {
    private BeginMarkedContent() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) throws Exception {
      processor.beginMarkedContent((PdfName)operands.get(0), new PdfDictionary());
    }
  }
  
  private static class BeginMarkedContentDictionary implements ContentOperator {
    private BeginMarkedContentDictionary() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) throws Exception {
      PdfObject properties = operands.get(1);
      processor.beginMarkedContent((PdfName)operands.get(0), getPropertiesDictionary(properties, processor.resources));
    }
    
    private PdfDictionary getPropertiesDictionary(PdfObject operand1, PdfContentStreamProcessor.ResourceDictionary resources) {
      if (operand1.isDictionary())
        return (PdfDictionary)operand1; 
      PdfName dictionaryName = (PdfName)operand1;
      return resources.getAsDict(dictionaryName);
    }
  }
  
  private static class EndMarkedContent implements ContentOperator {
    private EndMarkedContent() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) throws Exception {
      processor.endMarkedContent();
    }
  }
  
  private static class Do implements ContentOperator {
    private Do() {}
    
    public void invoke(PdfContentStreamProcessor processor, PdfLiteral operator, ArrayList<PdfObject> operands) throws IOException {
      PdfName xobjectName = (PdfName)operands.get(0);
      processor.displayXObject(xobjectName);
    }
  }
  
  private static class FormXObjectDoHandler implements XObjectDoHandler {
    private FormXObjectDoHandler() {}
    
    public void handleXObject(PdfContentStreamProcessor processor, PdfStream stream, PdfIndirectReference ref) {
      byte[] contentBytes;
      PdfDictionary resources = stream.getAsDict(PdfName.RESOURCES);
      try {
        contentBytes = ContentByteUtils.getContentBytesFromContentObject((PdfObject)stream);
      } catch (IOException e1) {
        throw new ExceptionConverter(e1);
      } 
      PdfArray matrix = stream.getAsArray(PdfName.MATRIX);
      (new PdfContentStreamProcessor.PushGraphicsState()).invoke(processor, null, null);
      if (matrix != null) {
        float a = matrix.getAsNumber(0).floatValue();
        float b = matrix.getAsNumber(1).floatValue();
        float c = matrix.getAsNumber(2).floatValue();
        float d = matrix.getAsNumber(3).floatValue();
        float e = matrix.getAsNumber(4).floatValue();
        float f = matrix.getAsNumber(5).floatValue();
        Matrix formMatrix = new Matrix(a, b, c, d, e, f);
        (processor.gs()).ctm = formMatrix.multiply((processor.gs()).ctm);
      } 
      processor.processContent(contentBytes, resources);
      (new PdfContentStreamProcessor.PopGraphicsState()).invoke(processor, null, null);
    }
  }
  
  private static class ImageXObjectDoHandler implements XObjectDoHandler {
    private ImageXObjectDoHandler() {}
    
    public void handleXObject(PdfContentStreamProcessor processor, PdfStream xobjectStream, PdfIndirectReference ref) {
      ImageRenderInfo renderInfo = ImageRenderInfo.createForXObject((processor.gs()).ctm, ref);
      processor.renderListener.renderImage(renderInfo);
    }
  }
  
  private static class IgnoreXObjectDoHandler implements XObjectDoHandler {
    private IgnoreXObjectDoHandler() {}
    
    public void handleXObject(PdfContentStreamProcessor processor, PdfStream xobjectStream, PdfIndirectReference ref) {}
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\PdfContentStreamProcessor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */