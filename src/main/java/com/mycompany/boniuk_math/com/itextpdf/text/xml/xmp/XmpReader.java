package com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.xml.XmlDomWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmpReader {
  private Document domDocument;
  
  public XmpReader(byte[] bytes) throws SAXException, IOException {
    try {
      DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
      fact.setNamespaceAware(true);
      DocumentBuilder db = fact.newDocumentBuilder();
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      this.domDocument = db.parse(bais);
    } catch (ParserConfigurationException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public boolean replaceNode(String namespaceURI, String localName, String value) {
    NodeList nodes = this.domDocument.getElementsByTagNameNS(namespaceURI, localName);
    if (nodes.getLength() == 0)
      return false; 
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      setNodeText(this.domDocument, node, value);
    } 
    return true;
  }
  
  public boolean replaceDescriptionAttribute(String namespaceURI, String localName, String value) {
    NodeList descNodes = this.domDocument.getElementsByTagNameNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");
    if (descNodes.getLength() == 0)
      return false; 
    for (int i = 0; i < descNodes.getLength(); i++) {
      Node node = descNodes.item(i);
      Node attr = node.getAttributes().getNamedItemNS(namespaceURI, localName);
      if (attr != null) {
        attr.setNodeValue(value);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean add(String parent, String namespaceURI, String localName, String value) {
    NodeList nodes = this.domDocument.getElementsByTagName(parent);
    if (nodes.getLength() == 0)
      return false; 
    for (int i = 0; i < nodes.getLength(); i++) {
      Node pNode = nodes.item(i);
      NamedNodeMap attrs = pNode.getAttributes();
      for (int j = 0; j < attrs.getLength(); j++) {
        Node node = attrs.item(j);
        if (namespaceURI.equals(node.getNodeValue())) {
          node = this.domDocument.createElement(localName);
          node.appendChild(this.domDocument.createTextNode(value));
          pNode.appendChild(node);
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public boolean setNodeText(Document domDocument, Node n, String value) {
    if (n == null)
      return false; 
    Node nc = null;
    while ((nc = n.getFirstChild()) != null)
      n.removeChild(nc); 
    n.appendChild(domDocument.createTextNode(value));
    return true;
  }
  
  public byte[] serializeDoc() throws IOException {
    XmlDomWriter xw = new XmlDomWriter();
    ByteArrayOutputStream fout = new ByteArrayOutputStream();
    xw.setOutput(fout, null);
    fout.write("<?xpacket begin=\"﻿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n".getBytes("UTF-8"));
    fout.flush();
    NodeList xmpmeta = this.domDocument.getElementsByTagName("x:xmpmeta");
    xw.write(xmpmeta.item(0));
    fout.flush();
    for (int i = 0; i < 20; i++)
      fout.write("                                                                                                   \n".getBytes()); 
    fout.write("<?xpacket end=\"w\"?>".getBytes());
    fout.close();
    return fout.toByteArray();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\xmp\XmpReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */