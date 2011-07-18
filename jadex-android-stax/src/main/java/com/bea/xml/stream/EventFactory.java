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

import java.util.Iterator;


import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLEventFactory;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.Comment;
import javaxx.xml.stream.events.DTD;
import javaxx.xml.stream.events.EndDocument;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.EntityDeclaration;
import javaxx.xml.stream.events.EntityReference;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.ProcessingInstruction;
import javaxx.xml.stream.events.StartDocument;
import javaxx.xml.stream.events.StartElement;

import com.bea.xml.stream.events.StartElementEvent;
import com.bea.xml.stream.events.EndElementEvent;
import com.bea.xml.stream.events.CharactersEvent;
import com.bea.xml.stream.events.StartDocumentEvent;
import com.bea.xml.stream.events.EndDocumentEvent;
import com.bea.xml.stream.events.ProcessingInstructionEvent;
import com.bea.xml.stream.events.CommentEvent;
import com.bea.xml.stream.events.EntityReferenceEvent;
import com.bea.xml.stream.events.DTDEvent;


/**
 * <p> The default factory for creating events.
 */

public class EventFactory extends XMLEventFactory {
  private Location location;

  public void setLocation(Location l) {
    location = l;
  }
  public Attribute createAttribute(QName name,
                                   String value){ 
    return new AttributeBase(name,value);
  }

  public Attribute createAttribute(String localName, 
                                   String value){ 
    return new AttributeBase("",localName,value);
  }
  public Attribute createAttribute(String prefix,
                                   String namespaceURI,
                                   String localName,
                                   String value) {
    return new AttributeBase(prefix,namespaceURI,localName,value,"CDATA");
  }
  public Namespace createNamespace(String namespaceURI){ 
    return new NamespaceBase(namespaceURI);
  }
  public Namespace createNamespace(String prefix, String namespaceUri){
    if (prefix == null)
      throw new NullPointerException("The prefix of a namespace may "+
                                     "not be set to null");
    return new NamespaceBase(prefix,namespaceUri);
  }
  public StartElement createStartElement(QName name,
                                         Iterator attributes,
                                         Iterator namespaces){ 
    StartElementEvent e=  
      new StartElementEvent(name);
    while(attributes != null && attributes.hasNext()) 
      e.addAttribute((Attribute) attributes.next());
    while(namespaces != null && namespaces.hasNext()) 
      e.addNamespace((Namespace) namespaces.next());
    return e;

  }

  public StartElement createStartElement(String prefix,
                                         String namespaceUri,
                                         String localName){ 
    return new StartElementEvent(new QName(namespaceUri,localName,prefix));
  }
  public static String checkPrefix(String prefix) {
    if (prefix == null) return "";
    return prefix;
  }
  public StartElement createStartElement(String prefix,
                                         String namespaceUri,
                                         String localName,
                                         Iterator attributes,
                                         Iterator namespaces){ 
    prefix=checkPrefix(prefix);
    StartElementEvent e=  
      new StartElementEvent(new QName(namespaceUri,localName,prefix));
    while(attributes != null && attributes.hasNext()) 
      e.addAttribute((Attribute) attributes.next());
    while(namespaces != null && namespaces.hasNext()) 
      e.addNamespace((Namespace) namespaces.next());
    return e;
  }
  public StartElement createStartElement(String prefix,
                                         String namespaceUri,
                                         String localName,
                                         Iterator attributes,
                                         Iterator namespaces,
                                         NamespaceContext context){ 
    prefix=checkPrefix(prefix);
    StartElementEvent e=  
      new StartElementEvent(new QName(namespaceUri,localName,prefix));
    while(attributes != null && attributes.hasNext()) 
      e.addAttribute((Attribute)attributes.next());
    while(namespaces != null && namespaces.hasNext()) 
      e.addNamespace((Namespace)namespaces.next());
    e.setNamespaceContext(context);
    return e;
  }

  public EndElement createEndElement(QName name,
                                     Iterator namespaces){ 
    EndElementEvent e =
      new EndElementEvent(name);
    while(namespaces != null && namespaces.hasNext())
      e.addNamespace((Namespace) namespaces.next());
    return e;

  }

  public EndElement createEndElement(String prefix, 
                                     String namespaceUri,
                                     String localName){ 
    prefix=checkPrefix(prefix);
    return new EndElementEvent(new QName(namespaceUri,localName,prefix));
  }

  public EndElement createEndElement(String prefix, 
                                     String namespaceUri,
                                     String localName,
                                     Iterator namespaces){ 
    prefix=checkPrefix(prefix);
    EndElementEvent e =
      new EndElementEvent(new QName(namespaceUri,localName,prefix));
    while(namespaces.hasNext())
      e.addNamespace((Namespace) namespaces.next());
    return e;
  }



  public Characters createCharacters(String content){ 
    return new CharactersEvent(content);
  }
  public Characters createCData(String content) {
    return new CharactersEvent(content,true);
  }
  public StartDocument createStartDocument(){ 
    return new StartDocumentEvent();
  }
  public StartDocument createStartDocument(String encoding, String version,
                                           boolean standalone){ 
    StartDocumentEvent e = new StartDocumentEvent();
    e.setEncoding(encoding);
    e.setVersion(version);
    e.setStandalone(standalone);
    return e;
  }
  public StartDocument createStartDocument(String encoding, String version){ 
    StartDocumentEvent e = new StartDocumentEvent();
    e.setEncoding(encoding);
    e.setVersion(version);
    return e;
  }

  public StartDocument createStartDocument(String encoding){ 
    StartDocumentEvent e = new StartDocumentEvent();
    e.setEncoding(encoding);
    return e;
  }

  public EndDocument createEndDocument(){ 
    return new EndDocumentEvent();
  }

  /**********
  public AttributeIterator createAttributeIterator(Iterator iterator){ 
    return new AttributeIteratorImpl(iterator);
  }

  public NamespaceIterator createNamespaceIterator(Iterator iterator){ 
    return new NamespaceIteratorImpl(iterator);
  }
  **********/


  public EntityReference createEntityReference(String name,
                                               EntityDeclaration declaration) {
    return new EntityReferenceEvent(name,declaration);
  }

  public Characters createSpace(String content) {
    CharactersEvent c = new CharactersEvent(content);
    c.setSpace(true);
    return c;
  }

  public Characters createIgnorableSpace(String content) {
    CharactersEvent c = new CharactersEvent(content);
    c.setSpace(true);
    c.setIgnorable(true);
    return c;
  }

  public Comment createComment(String text) {
    return new CommentEvent(text);
  }

  public ProcessingInstruction createProcessingInstruction(String target, String data) {
    return new ProcessingInstructionEvent(target,data);
  }

  public DTD createDTD(String dtd) {
    return new DTDEvent(dtd);
  }
}





