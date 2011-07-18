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
import java.util.Iterator;




import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLEventFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.Comment;
import javaxx.xml.stream.events.DTD;
import javaxx.xml.stream.events.EndDocument;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.EntityReference;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.ProcessingInstruction;
import javaxx.xml.stream.events.StartDocument;
import javaxx.xml.stream.events.StartElement;
import javaxx.xml.stream.events.XMLEvent;
import javaxx.xml.stream.util.XMLEventAllocator;
import javaxx.xml.stream.util.XMLEventConsumer;

import com.bea.xml.stream.events.CharactersEvent;
import com.bea.xml.stream.events.CommentEvent;
import com.bea.xml.stream.events.EndDocumentEvent;
import com.bea.xml.stream.events.EndElementEvent;
import com.bea.xml.stream.events.EntityReferenceEvent;
import com.bea.xml.stream.events.ProcessingInstructionEvent;
import com.bea.xml.stream.events.StartDocumentEvent;
import com.bea.xml.stream.events.StartElementEvent;
import com.bea.xml.stream.events.DTDEvent;
 
/**
 * <p> Return a single event for each allocate call </p>
 */

public class StaticAllocator 
  implements XMLEventAllocator 
{
    public static final String FEATURE_STAX_NOTATIONS = "javax.xml.stream.notations";
    public static final String FEATURE_STAX_ENTITIES = "javax.xml.stream.entities";

  StartElementEvent startElement = new StartElementEvent();
  EndElementEvent endElement = new EndElementEvent();
  CharactersEvent characters = new CharactersEvent();
  CharactersEvent cData = new CharactersEvent("",true);
  CharactersEvent space = new CharactersEvent();
  CommentEvent comment = new CommentEvent();
  EntityReferenceEvent entity = new EntityReferenceEvent();
  ProcessingInstructionEvent pi = new ProcessingInstructionEvent();
  StartDocumentEvent startDoc = new StartDocumentEvent();
  EndDocumentEvent endDoc = new EndDocumentEvent();
  DTDEvent dtd = new DTDEvent();

  public StaticAllocator() { }
  public XMLEventAllocator newInstance() {
    return new StaticAllocator();
  }
  
  public StartElement allocateStartElement(XMLStreamReader reader) 
    throws XMLStreamException 
  {
    startElement.reset();
    String prefix = EventFactory.checkPrefix(reader.getPrefix());
    startElement.setName(new QName(reader.getNamespaceURI(),
                                   reader.getLocalName(),
                                   prefix));
    Iterator ai = XMLEventAllocatorBase.getAttributes(reader);
    while (ai.hasNext())
      startElement.addAttribute((Attribute)ai.next());
    
    Iterator ni = XMLEventAllocatorBase.getNamespaces(reader);
    while (ni.hasNext())
      startElement.addAttribute((Namespace)ni.next());
    return startElement;
  }

  public EndElement allocateEndElement(XMLStreamReader reader) 
    throws XMLStreamException 
  {
    endElement.reset();
    String prefix = EventFactory.checkPrefix(reader.getPrefix());
    endElement.setName(new QName(reader.getNamespaceURI(),
                                 reader.getLocalName(),
                                 prefix
                                 ));
    Iterator ni = XMLEventAllocatorBase.getNamespaces(reader);
    while (ni.hasNext())
      endElement.addNamespace((Namespace) ni.next());
    return endElement;
  }

  public Characters allocateCharacters(XMLStreamReader reader) 
    throws XMLStreamException
  {
    characters.setData(reader.getText()); 
    return characters;
  }

  public Characters allocateCData(XMLStreamReader reader) 
    throws XMLStreamException
  {
    cData.setData(reader.getText()); 
    return cData;
  }

  public Characters allocateSpace(XMLStreamReader reader) 
    throws XMLStreamException
  {
    space.setSpace(true);
    space.setData(reader.getText()); 
    return space;
  }


  public EntityReference allocateEntityReference(XMLStreamReader reader) 
    throws XMLStreamException
  {
    entity.setName(reader.getLocalName());
    entity.setReplacementText(reader.getText());
    return entity;
  }

  public ProcessingInstruction allocatePI(XMLStreamReader reader) 
    throws XMLStreamException
  {
    pi.setTarget(reader.getPITarget());
    pi.setData(reader.getPIData());
    return pi;
  }

  public Comment allocateComment(XMLStreamReader reader) 
    throws XMLStreamException
  {
    comment.setData(reader.getText());
    return comment;
  }

  public StartDocument allocateStartDocument(XMLStreamReader reader)
    throws XMLStreamException
  {
    allocateXMLDeclaration(reader);
    return startDoc;
  }

  public EndDocument allocateEndDocument(XMLStreamReader reader)
    throws XMLStreamException
  {
    return endDoc;
  }

  public DTD allocateDTD(XMLStreamReader reader)
    throws XMLStreamException
  {
    dtd.setDTD(reader.getText());
    return dtd;
  }

  public StartDocument allocateXMLDeclaration(XMLStreamReader reader)
    throws XMLStreamException
  {
    startDoc.clear();
    String encoding = reader.getCharacterEncodingScheme();
    String version = reader.getVersion();
    boolean standalone = reader.isStandalone();
    if (encoding != null && 
        version != null &&
        !standalone ) {
      startDoc.setEncoding(encoding);
      startDoc.setVersion(version);
      startDoc.setStandalone(standalone);
      return startDoc;
    }
    if (version != null && 
        encoding != null) {
      startDoc.setEncoding(encoding);
      startDoc.setVersion(version);
      return startDoc;
    }

    if (encoding != null)
      startDoc.setEncoding(encoding);
    return startDoc;
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
      //case XMLEvent.XML_DECLARATION: return allocateXMLDeclaration(reader);
    case XMLEvent.START_DOCUMENT: return allocateStartDocument(reader);
    case XMLEvent.END_DOCUMENT: return allocateEndDocument(reader);
    case XMLEvent.DTD: return allocateDTD(reader);
    default:
      throw new XMLStreamException("Unable to allocate event["+
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
    return "Static Allocator";
  }

}






