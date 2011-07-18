/*   Copyright 2004 BEA Systems, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.bea.xml.stream;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamConstants;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.XMLEvent;

import com.bea.xml.stream.util.NamespaceContextImpl;

/**
 * <p> Creates an XMLStreamReader over a non-xml ascii format </p>
 */

public class XMLStreamPlayer implements XMLStreamReader {
  EventState state;
  EventScanner scanner;
  NamespaceContextImpl context = 
    new NamespaceContextImpl();
  
  public XMLStreamPlayer(){}

  public XMLStreamPlayer(InputStream stream) {
    try {
      scanner = new EventScanner(new InputStreamReader(stream));
      next();
      if (getEventType()==XMLEvent.START_DOCUMENT) {
        String encoding = getCharacterEncodingScheme();
        scanner = new EventScanner(new InputStreamReader(stream,
                                                         encoding));
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to instantiate the XMLStreamPlayer"+e.getMessage());
    }
  }
  public XMLStreamPlayer(Reader reader) {
    try {
      scanner = new EventScanner(reader);
      next();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public Object getProperty(java.lang.String name) 
    throws java.lang.IllegalArgumentException
  {
    return null;
  }  

  public int next() throws XMLStreamException {

    try {
      if (scanner.hasNext() == false) {
        state = null;
        return -1;
      }
      state = scanner.readElement();
      if (isStartElement()) {
        context.openScope();
        for (int i =0; i < getNamespaceCount(); i++) {
          context.bindNamespace(getNamespacePrefix(i),
                                getNamespaceURI(i));
        }
      } else if (isEndElement()) {
        if (context.getDepth() > 0)
          context.closeScope();
      }
      return state.getType();
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
      throw new XMLStreamException(e.getMessage(),e);
    }
  }
  public void require(int type, 
                      String namespaceURI, 
                      String localName)  
    throws XMLStreamException 
  {}

  public String getElementText() 
    throws XMLStreamException 
  {
    StringBuffer buf = new StringBuffer();
    if(getEventType() != START_ELEMENT)
      throw new XMLStreamException(
                                   "Precondition for readText is getEventType() == START_ELEMENT");
    do {
      if(next() == END_DOCUMENT)
        throw new XMLStreamException("Unexpected end of Document");
      if(isStartElement())
        throw new XMLStreamException("Unexpected Element start");
      if(isCharacters())
        buf.append(getText());
   } while(!isEndElement());
   return buf.toString();
  }

  public int nextTag() throws XMLStreamException {
    do {
      if(next() == END_DOCUMENT)
        throw new XMLStreamException("Unexpected end of Document");
      if(isCharacters() && !isWhiteSpace())
        throw new XMLStreamException("Unexpected text");
    } while(!isStartElement() && !isEndElement());
    return getEventType();
  }

  public boolean hasNext() 
    throws XMLStreamException
  {
    try {
      return state != null && state.getType() != XMLStreamConstants.END_DOCUMENT;
    } catch (Exception e) {
      throw new XMLStreamException(e);
    }
  }

  public void close() throws XMLStreamException {}

  public String getNamespaceURI(String prefix) {
    return context.getNamespaceURI(prefix);
  }

  private Attribute getAttributeInternal(int index) {
    return (Attribute) state.getAttributes().get(index);
  }

  private Attribute getNamespaceInternal(int index) {
    return (Attribute) state.getNamespaces().get(index);
  }

  public boolean isStartElement() {
    return ((getEventType() & XMLStreamConstants.START_ELEMENT) != 0);
  }

  public boolean isEndElement() {
    return ((getEventType() & XMLStreamConstants.END_ELEMENT) != 0);
  }

  public boolean isCharacters() {
    return ((getEventType() & XMLStreamConstants.CHARACTERS) != 0);
  }

  public boolean isWhiteSpace() {
    return false;
  }

  public String getAttributeValue(String namespaceUri,
                                  String localName) 
  {
    for (int i=0; i < getAttributeCount(); i++) {
      Attribute a = getAttributeInternal(i);
      if (localName.equals(a.getName().getLocalPart()))
        if (namespaceUri == null)
          return a.getValue();
        else
          if (namespaceUri.equals(a.getName().getNamespaceURI()))
            return a.getValue();
 
    }
    return null;
  }

  public int getAttributeCount() {
    if (isStartElement())
      return state.getAttributes().size();
    else 
      return 0;
  }

  public QName getAttributeName(int index) {
    return new QName(getAttributeNamespace(index),
                     getAttributeLocalName(index),
                     getAttributePrefix(index));

  }

  public String getAttributeNamespace(int index) {
    Attribute a = getAttributeInternal(index);
    if (a == null) return null;
    return a.getName().getNamespaceURI();
  }

  public String getAttributeLocalName(int index) {
    Attribute a = getAttributeInternal(index);
    if (a == null) return null;
    return a.getName().getLocalPart();
  }

  public String getAttributePrefix(int index) {
    Attribute a = getAttributeInternal(index);
    if (a == null) return null;
    return a.getName().getPrefix();
  }

  public String getAttributeType(int index) {
    return "CDATA";
  }
  public String getAttributeValue(int index){
    Attribute a = getAttributeInternal(index);
    if (a == null) return null;
    return a.getValue();
  }
  public boolean isAttributeSpecified(int index) {
    return false;
  }

  // Namespaces

  public int getNamespaceCount() {
    if (isStartElement())
      return state.getNamespaces().size();
    else
      return 0;
  }

  public String getNamespacePrefix(int index) {

    Attribute a = getNamespaceInternal(index);
    if (a == null) return null;
    return a.getName().getLocalPart();
  }

  public String getNamespaceURI(int index) {
    Attribute a = getNamespaceInternal(index);
    if (a == null) return null;
    return a.getValue();
  }

  public NamespaceContext getNamespaceContext() {
    return context;
  }

  public XMLStreamReader subReader() 
    throws XMLStreamException
  {
    return null; 
  }

  public int getEventType() {
    if (state == null) return XMLStreamConstants.END_DOCUMENT;
    return state.getType();
  }

  public String getText() {
    return state.getData();
  }

  public Reader getTextStream() {
    throw new UnsupportedOperationException();
  }

  public char[] getTextCharacters() {
    return state.getData().toCharArray();
  }

  public int getTextCharacters(int src, char[] target, int targetStart, int length)
    throws XMLStreamException {
    throw new UnsupportedOperationException();
  }


  public int getTextStart() {
    return 0;
  }
  public int getTextLength(){
    return state.getData().length();
  }
  public String getEncoding() {
    return state.getData();
  }

  public boolean hasText() {
   return (0 != (getEventType() & (XMLStreamConstants.CHARACTERS |
                               XMLStreamConstants.DTD |
                               XMLStreamConstants.COMMENT |
                               XMLStreamConstants.ENTITY_REFERENCE)));

  }

  public Location getLocation() {
    return null;
  }
  public QName getName() {
    return new QName(getNamespaceURI(),
                     getLocalName(),
                     getPrefix());
  }
  public String getLocalName() {
    return state.getLocalName();
  }
  public boolean hasName() {
    return (0 != (getEventType()  & (XMLEvent.START_ELEMENT 
                            | XMLEvent.END_ELEMENT
                            | XMLEvent.ENTITY_REFERENCE)));
  }
  public String getNamespaceURI() {
    return state.getNamespaceURI();
  }
  public String getPrefix() {
    return state.getPrefix();
  }
  public String getVersion() {
    return "1.0";
  }
  public boolean isStandalone() {
    return true;
  }
  public boolean standaloneSet() {
    return false;
  }
  public String getCharacterEncodingScheme() {
    return null; 
  }
  public String getPITarget() {
    return state.getData();
  }
  public String getPIData() {
    return state.getExtraData();
  }
  public boolean endDocumentIsPresent() {
    return scanner.endDocumentIsPresent();
  }

  public static void main(String args[]) throws Exception {
    XMLStreamReader reader = new XMLStreamPlayer(
                  new java.io.FileReader(args[0]));
    XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
    XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(System.out);
    ReaderToWriter rtow = new ReaderToWriter(xmlw);
    while (reader.hasNext()) {
      rtow.write(reader);
      reader.next();
    }
    xmlw.flush();
  }
}



