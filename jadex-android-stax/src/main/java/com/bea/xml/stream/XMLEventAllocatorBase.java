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

import com.bea.xml.stream.util.ElementTypeNames;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.Comment;
import javaxx.xml.stream.events.DTD;
import javaxx.xml.stream.events.EndDocument;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.EntityReference;
import javaxx.xml.stream.events.ProcessingInstruction;
import javaxx.xml.stream.events.StartDocument;
import javaxx.xml.stream.events.StartElement;
import javaxx.xml.stream.events.XMLEvent;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLEventFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.*;
import javaxx.xml.stream.util.XMLEventAllocator;
import javaxx.xml.stream.util.XMLEventConsumer;

import com.bea.xml.stream.util.EmptyIterator;
import com.bea.xml.stream.events.DTDEvent;
import com.bea.xml.stream.events.EntityDeclarationEvent;

/**
 * <p> An allocator that creates an event per method call. </p>
 */

public class XMLEventAllocatorBase 
  implements XMLEventAllocator 
{
  XMLEventFactory factory;

  public XMLEventAllocatorBase() {
    factory = XMLEventFactory.newInstance();
  }

  public XMLEventAllocator newInstance() {
    return new XMLEventAllocatorBase();
  }

  public static Iterator getAttributes(XMLStreamReader reader) {
    if (reader.getAttributeCount()==0) return EmptyIterator.emptyIterator;
    int attributeCount = reader.getAttributeCount();
    ArrayList atts = new ArrayList();
    for (int i = 0; i < attributeCount; i++){
      atts.add(new AttributeBase(reader.getAttributePrefix(i),
                                 reader.getAttributeNamespace(i),
                                 reader.getAttributeLocalName(i),
                                 reader.getAttributeValue(i),
                                 reader.getAttributeType(i)));
    }
    return atts.iterator();
  }
  
  public static Iterator getNamespaces(XMLStreamReader reader) {
    if (reader.getNamespaceCount()==0) return EmptyIterator.emptyIterator;
    ArrayList ns = new ArrayList();
    for (int i = 0; i < reader.getNamespaceCount(); i++){  
      String prefix = reader.getNamespacePrefix(i);
      if(prefix == null ||
         prefix.equals("")){
        ns.add(new NamespaceBase(reader.getNamespaceURI(i)));
      } else {
        ns.add(new NamespaceBase(prefix,
                                 reader.getNamespaceURI(i)));
      }
    }
    return ns.iterator();
  }

  public StartElement allocateStartElement(XMLStreamReader reader) 
    throws XMLStreamException 
  {
    String prefix = reader.getPrefix();
    String uri = reader.getNamespaceURI();
    if (prefix == null) prefix = "";
    if (uri == null) uri = "";
    return factory.createStartElement(prefix,
                                      uri,
                                      reader.getLocalName(),
                                      getAttributes(reader),
                                      getNamespaces(reader));
  }

  public EndElement allocateEndElement(XMLStreamReader reader) 
    throws XMLStreamException 
  {
    String prefix = reader.getPrefix();
    String uri = reader.getNamespaceURI();
    if (prefix == null) prefix = "";
    if (uri == null) uri = "";
    return factory.createEndElement(prefix,
                                    uri,
                                    reader.getLocalName(),
                                    getNamespaces(reader)
                                    );
  }

  public Characters allocateCharacters(XMLStreamReader reader) 
    throws XMLStreamException
  {
    int start = reader.getTextStart();
    int length = reader.getTextLength();
    String result = new String(reader.getTextCharacters(),
                               start,
                               length);
    if (reader.isWhiteSpace())
      return factory.createSpace(result);
    else
      return factory.createCharacters(result);
  }

  public Characters allocateCData(XMLStreamReader reader) 
    throws XMLStreamException
  {
    return factory.createCData(reader.getText());
  }

  public Characters allocateSpace(XMLStreamReader reader) 
    throws XMLStreamException
  {
    return factory.createSpace(reader.getText());
  }

  public EntityReference allocateEntityReference(XMLStreamReader reader) 
    throws XMLStreamException
  {
      // no factory method for entity declarations... weird.
      String name = reader.getLocalName();

      if (reader instanceof MXParser) {
          /* Should be able to get additional information (public/system id
           * for external entities, declaration)... but not yet implemented.
           */
          // !!! TBI
      }
      EntityDeclarationEvent ed = new EntityDeclarationEvent(name, reader.getText());
      return factory.createEntityReference(name, ed);
  }

  public ProcessingInstruction allocatePI(XMLStreamReader reader) 
    throws XMLStreamException
  {
    return factory.createProcessingInstruction(reader.getPITarget(),
                                               reader.getPIData());
  }

  public Comment allocateComment(XMLStreamReader reader) 
    throws XMLStreamException
  {
    return factory.createComment(reader.getText());
  }

  public StartDocument allocateStartDocument(XMLStreamReader reader)
    throws XMLStreamException
  {
    return allocateXMLDeclaration(reader);
  }

  public EndDocument allocateEndDocument(XMLStreamReader reader)
    throws XMLStreamException
  {
    return factory.createEndDocument();
  }

  public DTD allocateDTD(XMLStreamReader reader)
    throws XMLStreamException
  {
      /* 07-Mar-2006, TSa: Need to be able to specify notations and
       *    (external unparsed?) entities contained in the DTD, so can not
       *    use the constructor that just takes String..
       */
      if (reader instanceof MXParser) {
          MXParser mxp = (MXParser) reader;
          DTDEvent evt = new DTDEvent(reader.getText());
          evt.setNotations((List) mxp.getProperty(MXParser.FEATURE_STAX_NOTATIONS));
          evt.setEntities((List) mxp.getProperty(MXParser.FEATURE_STAX_ENTITIES));
          return evt;
      }

      // Blah. Using some other reader...
      return factory.createDTD(reader.getText());
  }

  public StartDocument allocateXMLDeclaration(XMLStreamReader reader)
    throws XMLStreamException
  {
    String encoding = reader.getCharacterEncodingScheme();
    String version = reader.getVersion();
    boolean standalone = reader.isStandalone();
    if (encoding != null && 
        version != null &&
        !standalone ) {
      return factory.createStartDocument(encoding,
                                         version,
                                         standalone);
    }
    if (version != null && 
        encoding != null)
      return factory.createStartDocument(encoding,
                                         version);

    if (encoding != null)
    return factory.createStartDocument(encoding);

    return factory.createStartDocument();
  }
  

  public XMLEvent allocate(XMLStreamReader reader) 
    throws XMLStreamException
  {
    switch (reader.getEventType()) {
    case XMLEvent.START_ELEMENT: return allocateStartElement(reader);
    case XMLEvent.END_ELEMENT: return allocateEndElement(reader);
    case XMLEvent.CHARACTERS: return allocateCharacters(reader);
    case XMLEvent.SPACE: return allocateCharacters(reader);
    case XMLEvent.CDATA: return allocateCData(reader);
    case XMLEvent.ENTITY_REFERENCE: return allocateEntityReference(reader);
    case XMLEvent.PROCESSING_INSTRUCTION: return allocatePI(reader);
    case XMLEvent.COMMENT: return allocateComment(reader);
      //    case XMLEvent.XML_DECLARATION: return allocateXMLDeclaration(reader);
    case XMLEvent.START_DOCUMENT: return allocateStartDocument(reader);
    case XMLEvent.END_DOCUMENT: return allocateEndDocument(reader);
    case XMLEvent.DTD: return allocateDTD(reader);
    default:
      throw new XMLStreamException("Unable to allocate event["+
                                   reader.getEventType()+" , "+
                                   ElementTypeNames.getEventTypeString(reader.getEventType())+"]");
    }
    //    return new com.bea.xml.stream.events.NullEvent();
  }

  public void allocate(XMLStreamReader reader,
                       XMLEventConsumer consumer) 
    throws XMLStreamException
  {
    consumer.add(allocate(reader));
  }

  public String toString() {
    return "NonStaticAllocator";
  }
}





