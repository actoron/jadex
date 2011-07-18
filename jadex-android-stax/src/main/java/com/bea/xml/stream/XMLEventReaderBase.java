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

import com.bea.xml.stream.util.CircularQueue;
import com.bea.xml.stream.util.ElementTypeNames;

import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.XMLEvent;
import javaxx.xml.stream.util.XMLEventAllocator;
import javaxx.xml.stream.util.XMLEventConsumer;

/**
 * <p>The base reader class.</p>
 */

public class XMLEventReaderBase 
  implements XMLEventReader, XMLEventConsumer
{
  private CircularQueue elementQ = new CircularQueue();
  private boolean open = true;
  protected XMLStreamReader reader;
  protected XMLEventAllocator allocator;
  private boolean reachedEOF=false;
  private ConfigurationContextBase configurationContext;

  public XMLEventReaderBase(XMLStreamReader reader)

    throws XMLStreamException
  {
    this(reader, new XMLEventAllocatorBase());
  }

  public XMLEventReaderBase(XMLStreamReader reader,
                            XMLEventAllocator alloc) 
    throws XMLStreamException
  {
    if (reader==null) 
      throw new IllegalArgumentException("XMLStreamReader may not be null");
    if (alloc==null) 
      throw new IllegalArgumentException("XMLEventAllocator may not be null");

    this.reader = reader;
    open = true;
    // create the allocator
    this.allocator = alloc;
    //    System.out.println("Allocator->"+allocator);

    // This check fills the information from the XMLDeclaration
    // into the startdocument event
    if (reader.getEventType()==XMLEvent.START_DOCUMENT) {
      XMLEvent e = allocator.allocate(reader);
      reader.next();
      add(e);
    }
  }


  public void setAllocator(XMLEventAllocator allocator) {
    if (allocator == null)
      throw new IllegalArgumentException("XMLEvent Allocator may not be null");

    this.allocator = allocator;
  }

  public String getElementText() throws XMLStreamException {
    StringBuffer buf = new StringBuffer();
    XMLEvent e = nextEvent();
    if (!e.isStartElement())
      throw new XMLStreamException("Precondition for readText is nextEvent().getTypeEventType() == START_ELEMENT (got "+e.getEventType()+")");
    while(hasNext()) {
      e = peek();
      if(e.isStartElement())
        throw new XMLStreamException("Unexpected Element start");
      if(e.isCharacters())
        buf.append(((Characters) e).getData());
      if(e.isEndElement())
        return buf.toString();
      nextEvent();
    } 
    throw new XMLStreamException("Unexpected end of Document");

  }

  public XMLEvent nextTag() throws XMLStreamException {
    while(hasNext()) {
      XMLEvent e = nextEvent();
      if (e.isCharacters() && !((Characters) e).isWhiteSpace())
        throw new XMLStreamException("Unexpected text");
      if (e.isStartElement() || e.isEndElement())
        return e;
    }
    throw new XMLStreamException("Unexpected end of Document");
  }

  public Object next() {
    try {
      return nextEvent();
    } catch (XMLStreamException e) {
      return null;
    }
  }

  public XMLEvent nextEvent()
    throws XMLStreamException
  {
    // FIXME cfry throw error if parseSome fails
    if (needsMore()) {
        if (!parseSome())
            throw new java.util.NoSuchElementException("Attempt to call nextEvent() on a stream with no more elements");
    }
    return get();
  }

  public boolean hasNext() 
  {
    if (!open) return false;
    if (!elementQ.isEmpty()) return true;
    try {
      if (reader.hasNext()) return true;
    } catch (XMLStreamException e) {
      return false;
    }
    open = false;
    return false;
  }

  public XMLEvent peek() 
    throws XMLStreamException
  {
    if (!elementQ.isEmpty()) 
      return (XMLEvent) elementQ.peek();
    if (parseSome()) 
      return (XMLEvent) elementQ.peek();

    // Stax specs indicate null should be returned, if no more stuff:
    return null;
  }

  public void add(XMLEvent event) 
    throws XMLStreamException
  {
    elementQ.add(event);
  }

  protected boolean needsMore() {
    return elementQ.isEmpty();
  }

  protected XMLEvent get() 
    throws XMLStreamException
  {
    return (XMLEvent) elementQ.remove();
  }

  protected boolean isOpen() {
    return !reachedEOF;
  }

  protected void internal_close() {
    reachedEOF = true;
  }

  public void close() 
    throws XMLStreamException
  {
    internal_close();
  }
   
  protected boolean parseSome() 
    throws XMLStreamException
  {
    /* 26-Sep-2005, TSa: Should check if we have hit EOF, and if so,
     *   fail to get any more stuff...
     */
    if (reachedEOF) {
        return false;
    }

    //    System.out.println("Allocator->"+allocator);
    allocator.allocate(reader,this);
    if (reader.hasNext())
      reader.next();
    if (reader.getEventType() == XMLEvent.END_DOCUMENT) {
      allocator.allocate(reader,this);
      reachedEOF = true;
    }
    return !needsMore();
  }

  public void setConfigurationContext(ConfigurationContextBase base) {
    configurationContext = base;
  }
  
  public Object getProperty(String name) {
    return configurationContext.getProperty(name);
  }
  public void remove() {
    throw new java.lang.UnsupportedOperationException();
  }
  public static void main(String args[]) throws Exception {

    System.setProperty("javax.xml.stream.XMLInputFactory", 
                       "com.bea.xml.stream.MXParserFactory");
    System.setProperty("javax.xml.stream.XMLEventFactory", 
                       "com.bea.xml.stream.EventFactory");

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader xmlr = factory.createXMLEventReader(new java.io.FileReader(args[0]));
    
    while(xmlr.hasNext()) {
      XMLEvent e = xmlr.nextEvent();
      System.out.println("["+
                         ElementTypeNames.getEventTypeString(e.getEventType())
                         +"]["+
                         e+"]");
    }
  }
  //  public ConfigurationContext getConfigurationContext() {
  //  return new ConfigurationContextBase();
  // }

}
