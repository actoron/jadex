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
import com.bea.xml.stream.util.ElementTypeNames;
import com.bea.xml.stream.filters.TypeFilter;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.StartElement;
import javaxx.xml.stream.events.XMLEvent;

public class EventReaderFilter implements XMLEventReader {
  private XMLEventReader parent;
  private EventFilter filter;
  public EventReaderFilter (XMLEventReader reader) 
    throws XMLStreamException
  {
    this.parent = reader;
  }
  public EventReaderFilter (XMLEventReader reader, 
                            EventFilter filter) 
    throws XMLStreamException
  {
    this.parent = reader;
    this.filter = filter;
  }

  public void setFilter(EventFilter filter) {
    this.filter = filter;
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
    if (hasNext())
      return parent.nextEvent();
    return null;
  }

  public String getElementText() 
    throws XMLStreamException
  {
    StringBuffer buf = new StringBuffer();
    XMLEvent e = nextEvent();
    if (!e.isStartElement())
      throw new XMLStreamException(
                                   "Precondition for readText is"+
                                   " nextEvent().getTypeEventType() == START_ELEMENT");
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

  
  public boolean hasNext() 
  {
    try { 
      while(parent.hasNext()) {
        if (filter.accept(parent.peek())) return true;
        parent.nextEvent();
      }
      return false;
    } catch (XMLStreamException e) {
      return false;
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public XMLEvent peek() 
    throws XMLStreamException
  {
    if (hasNext())
      return parent.peek();
    return null;
  }

  public void close() 
    throws XMLStreamException
  {
    parent.close();
  }

  public Object getProperty(String name) {
    return parent.getProperty(name);
  }
  
  public static void main(String args[]) throws Exception {
    System.setProperty("javax.xml.stream.XMLInputFactory", 
                       "com.bea.xml.stream.MXParserFactory");
    System.setProperty("javax.xml.stream.XMLEventFactory", 
                       "com.bea.xml.stream.EventFactory");


    /**
    MXParser r = new MXParser();
    r.setInput(new java.io.FileReader(args[0]));
    XMLEventReaderBase b = new XMLEventReaderBase(r);
    EventFilter f = new com.bea.xml.stream.filters.TypeFilter(XMLEvent.START_ELEMENT |
                                                              XMLEvent.END_ELEMENT);
    EventReaderFilter filteredReader = new EventReaderFilter(b,f);

    while (filteredReader.hasNext())
      System.out.println(filteredReader.next());
    **/

    XMLInputFactory factory = XMLInputFactory.newInstance();

    TypeFilter f = new com.bea.xml.stream.filters.TypeFilter();
    f.addType(XMLEvent.START_ELEMENT);
    f.addType(XMLEvent.END_ELEMENT);
    
 
    XMLEventReader reader = factory.createFilteredReader(
      factory.createXMLEventReader(new java.io.FileReader(args[0])),
      f);
    
    while(reader.hasNext())
      System.out.println(reader.nextEvent());

  }
}







