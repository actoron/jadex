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


import com.bea.xml.stream.util.EmptyIterator;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.XMLEvent;

import java.util.List;
import java.io.InputStream;
import java.io.Reader;

/**
 * <p> An implementation of the ReaderDelegate class </p>
 */

public class ReaderDelegate implements XMLStreamReader {
  private XMLStreamReader reader;

  public ReaderDelegate(XMLStreamReader reader) {
    this.reader = reader;
  }
  public void setDelegate(XMLStreamReader reader) {
    this.reader = reader;
  }
  public XMLStreamReader getDelegate() {
    return reader;
  }

  public int next() 
    throws XMLStreamException 
  {
    return reader.next();
  }

  public int nextTag() 
    throws XMLStreamException 
  {
    return reader.nextTag();
  }

  public String getElementText() 
    throws XMLStreamException 
  {
    return reader.getElementText();
  }

  public void require(int type, String namespaceURI, String localName)  
    throws XMLStreamException
  {
    reader.require(type,namespaceURI,localName);
  }

  public boolean hasNext() 
    throws XMLStreamException
  {
    return reader.hasNext();
  }

  //public void skip() 
  // throws XMLStreamException
  //{
  //  reader.skip();
  // }

  public void close() 
    throws XMLStreamException
  {
    reader.close();
  }

  public String getNamespaceURI(String prefix) 
  {
    return reader.getNamespaceURI(prefix);
  }

  public NamespaceContext getNamespaceContext() {
    return reader.getNamespaceContext();
  }

  public boolean isStartElement() {
    return reader.isStartElement();
  }

  public boolean isEndElement() {
    return reader.isEndElement();
  }

  public boolean isCharacters() {
    return reader.isCharacters();
  }

  public boolean isWhiteSpace() {
    return reader.isWhiteSpace();
  }

  public QName getAttributeName(int index) {
    return reader.getAttributeName(index);
  }

  public int getTextCharacters(int sourceStart, 
                               char[] target, 
                               int targetStart, 
                               int length) 
    throws XMLStreamException {
    return reader.getTextCharacters(sourceStart,
                                    target,
                                    targetStart,
                                    length);
  }

  /**********8
  public boolean moveToStartElement() 
    throws XMLStreamException
  {
    return reader.moveToStartElement();
  }

  public boolean moveToStartElement(String localName) 
    throws XMLStreamException
  {
    return reader.moveToStartElement(localName);
  }

  public boolean moveToStartElement(String localName, String namespaceUri) 
    throws XMLStreamException
  {
    return reader.moveToStartElement(localName,namespaceUri);
  }

  public boolean moveToEndElement() 
    throws XMLStreamException
  {
    return reader.moveToEndElement();
  }

  public boolean moveToEndElement(String localName) 
    throws XMLStreamException
  {
    return reader.moveToEndElement(localName);
  }

  public boolean moveToEndElement(String localName, String namespaceUri) 
    throws XMLStreamException
  {
    return reader.moveToEndElement(localName,namespaceUri);
  }

  public boolean hasAttributes() {
    return reader.hasAttributes();
   }

  public boolean hasNamespaces() {
    return reader.hasNamespaces();
  }
  ************/
  public String getAttributeValue(String namespaceUri,
                                  String localName) 
  {
    return reader.getAttributeValue(namespaceUri,localName);
  }
  public int getAttributeCount() {
    return reader.getAttributeCount();
  }
  public String getAttributePrefix(int index) {
    return reader.getAttributePrefix(index);
  }
  public String getAttributeNamespace(int index) {
    return reader.getAttributeNamespace(index);
  }
  public String getAttributeLocalName(int index) {
    return reader.getAttributeLocalName(index);
  }
  public String getAttributeType(int index) {
    return reader.getAttributeType(index);
  }
  public String getAttributeValue(int index) {
    return reader.getAttributeValue(index);
  }
  public boolean isAttributeSpecified(int index) {
    return reader.isAttributeSpecified(index);
  }

  public int getNamespaceCount() {
    return reader.getNamespaceCount();
  }
  public String getNamespacePrefix(int index) {
    return reader.getNamespacePrefix(index);
  }
  public String getNamespaceURI(int index) {
    return reader.getNamespaceURI(index);
  }
    

  // public AttributeIterator getAttributes() {
  //  return reader.getAttributes();
  //

  //  public NamespaceIterator getNamespaces() {
  //  return reader.getNamespaces();
  // }

  //  public XMLStreamReader subReader() 
  //  throws XMLStreamException
  // {
  //  return reader.subReader();
  // }

  //  public void recycle() 
  //  throws XMLStreamException
  // {
  //  reader.recycle();
  // }

  public int getEventType() {
    return reader.getEventType();
  }

  public String getText() {
    return reader.getText();
  }

  public char[] getTextCharacters() {
    return reader.getTextCharacters();
  }

  public int getTextStart() {
    return reader.getTextStart();
  }

  public int getTextLength() {
    return reader.getTextLength();
  }

  public String getEncoding() {
    return reader.getEncoding();
  }

  public boolean hasText() {
    return reader.hasText();
  }

  //  public int getLineNumber() {
  //  return reader.getLineNumber();
  // }

  //public int getColumnNumber() {
  //  return reader.getColumnNumber();
  // }

  //  public int getCharacterOffset() {
  //  return reader.getCharacterOffset();
  // }
  public Location getLocation() {
    return reader.getLocation();
  }

  public QName getName() {
    return reader.getName();
  }


  public String getLocalName() {
    return reader.getLocalName();
  }

  public boolean hasName() {
    return reader.hasName();
  }

  public String getNamespaceURI() {
    return reader.getNamespaceURI();
  }

  public String getPrefix() {
    return reader.getPrefix();
  }

  public String getVersion() {
    return reader.getVersion();
  }

  public boolean isStandalone() {
    return reader.isStandalone();
  }

  public boolean standaloneSet() {
    return reader.standaloneSet();
  }

  public String getCharacterEncodingScheme() {
    return reader.getCharacterEncodingScheme();
  }

  public String getPITarget() {
    return reader.getPITarget();
  }

  public String getPIData() {
    return reader.getPIData();
  }
  //  public NamespaceIterator getOutOfScopeNamespaces() {
  //  return reader.getOutOfScopeNamespaces();
  // }

  //  public ConfigurationContext getConfigurationContext() {
  //  return reader.getConfigurationContext();
  // }

  public Object getProperty(String name) {
    return reader.getProperty(name);
  }
}
