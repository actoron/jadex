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

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLEventWriter;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;
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
import javaxx.xml.stream.util.XMLEventConsumer;

/**
 * <p> The base writer class </p>
 */

public class XMLEventWriterBase 
  implements XMLEventWriter, XMLEventConsumer
{
  XMLStreamWriter writer;

  public XMLEventWriterBase(XMLStreamWriter writer) {
    this.writer = writer;
  }

  public void flush() 
    throws XMLStreamException 
  {
    writer.flush();
  }

  public void close() 
    throws XMLStreamException 
  {
    writer.close();
  }

  private void addStartElement(StartElement se) 
    throws XMLStreamException 
  {
    String prefix = se.getName().getPrefix();
    String namespace = se.getName().getNamespaceURI();
    String localName = se.getName().getLocalPart();
    writer.writeStartElement(prefix,localName,namespace);
    Iterator ni = se.getNamespaces();
    while (ni.hasNext()) {
      writeNamespace((Namespace)ni.next());
    }

    Iterator ai = se.getAttributes();
    while (ai.hasNext()) {
      writeAttribute((Attribute) ai.next());
    }

  }

  private void addEndElement(EndElement ee) 
    throws XMLStreamException 
  {
    String prefix = ee.getName().getPrefix();
    String namespace = ee.getName().getNamespaceURI();
    String localName = ee.getName().getLocalPart();
    writer.writeEndElement();
  }

  public void addCharacters(Characters cd) 
    throws XMLStreamException
  {
    if (cd.isCData())
      writer.writeCData(cd.getData());
    else
      writer.writeCharacters(cd.getData());
  }

  public void addEntityReference(EntityReference er) 
    throws XMLStreamException
  {
    writer.writeEntityRef(er.getName());
  }

  public void addProcessingInstruction(ProcessingInstruction pi) 
    throws XMLStreamException
  {
    writer.writeProcessingInstruction(pi.getTarget(),
                                      pi.getData());
  }

  public void addComment(Comment c) 
    throws XMLStreamException
  {
    writer.writeComment(c.getText());
  }

  public void addStartDocument(StartDocument sd)
    throws XMLStreamException
  {
    String encoding = sd.getCharacterEncodingScheme();
    String version = sd.getVersion();
    boolean standalone = sd.isStandalone();
    writer.writeStartDocument(encoding,version);
  }

  public void addEndDocument(EndDocument ed)
    throws XMLStreamException
  {

  }

  private void writeAttribute(Attribute a) 
    throws XMLStreamException
  {
    writer.writeAttribute(a.getName().getNamespaceURI(),
                          a.getName().getLocalPart(),
                          a.getValue());
  }
  public void addAttribute(Attribute a)
    throws XMLStreamException
  {
    writeAttribute(a);
  }

  public void writeNamespace(Namespace n) 
    throws XMLStreamException
  {
    if (n.isDefaultNamespaceDeclaration())
      writer.writeDefaultNamespace(n.getNamespaceURI());
    else
      writer.writeNamespace(n.getPrefix(),
                            n.getNamespaceURI());
  }
  public void addNamespace(Namespace ns)
    throws XMLStreamException
  {
    writeNamespace(ns);
  }

  public void addDTD(DTD dtd)
    throws XMLStreamException
  {
    writer.writeDTD(dtd.getDocumentTypeDeclaration());
  }

  public void add(XMLEvent e) 
    throws XMLStreamException 
  {
    switch(e.getEventType()) {
    case XMLEvent.START_ELEMENT: 
      addStartElement((StartElement) e);
      break;
    case XMLEvent.END_ELEMENT:  
      addEndElement((EndElement) e) ;
      break;
    case XMLEvent.CHARACTERS:  
      addCharacters((Characters) e);
      break;
    case XMLEvent.ENTITY_REFERENCE:  
      addEntityReference((EntityReference) e);
      break;
    case XMLEvent.PROCESSING_INSTRUCTION:  
      addProcessingInstruction((ProcessingInstruction) e);
      break;
    case XMLEvent.COMMENT:  
      addComment((Comment) e);
      break;
    case XMLEvent.START_DOCUMENT:  
      addStartDocument((StartDocument) e);
      break;
    case XMLEvent.END_DOCUMENT:  
      addEndDocument((EndDocument) e); 
      break;
    case XMLEvent.ATTRIBUTE:  
      addAttribute((Attribute) e);
      break;
    case XMLEvent.NAMESPACE:  
      addNamespace((Namespace) e);
      break;
    case XMLEvent.DTD:
      addDTD((DTD) e);
      break;
    default:
      throw new XMLStreamException("Unable to add event["+
                                   ElementTypeNames.getEventTypeString(e.getEventType())+"]");
    }
  }

  public void add(XMLEventReader stream) 
    throws XMLStreamException 
  {
    while(stream.hasNext())
      add(stream.nextEvent());
  }

  public String getPrefix(String uri) 
    throws XMLStreamException 
  {
    return writer.getPrefix(uri);
  }
  
  public void setPrefix(String prefix, String uri) 
    throws XMLStreamException 
  {
    writer.setPrefix(prefix,uri);
  }

  public void setDefaultNamespace(String uri) 
    throws XMLStreamException
  {
    writer.setDefaultNamespace(uri);
  }

  public void setNamespaceContext(NamespaceContext context) 
    throws XMLStreamException
  {
    writer.setNamespaceContext(context);
  }

  public NamespaceContext getNamespaceContext() {
    return writer.getNamespaceContext();
  }
  public static void main(String args[]) 
    throws Exception 
  {
    System.setProperty("javax.xml.stream.XMLInputFactory", 
                       "com.bea.xml.stream.MXParserFactory");
    System.setProperty("javax.xml.stream.XMLEventFactory", 
                       "com.bea.xml.stream.EventFactory");

    java.io.Writer w = new java.io.OutputStreamWriter(System.out);
    XMLEventWriterBase writer = 
      new XMLEventWriterBase(new XMLWriterBase(w));
    
    MXParser parser = new MXParser();
    parser.setConfigurationContext(new ConfigurationContextBase());
    parser.setInput(new java.io.FileReader(args[0]));
    
    XMLEventReaderBase reader = new XMLEventReaderBase(parser);
    
    while(reader.hasNext()) {
      XMLEvent e= reader.nextEvent();
      System.out.println("about to add:["+e+"];");
      writer.add(e);
    }
    writer.flush();
  }
}
