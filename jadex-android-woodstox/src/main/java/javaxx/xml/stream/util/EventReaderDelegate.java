package javaxx.xml.stream.util;

import javaxx.xml.stream.util.StreamReaderDelegate;
import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.XMLEvent;

/**
 * This is the base class for deriving an XMLEventReader 
 * filter.
 *
 * This class is designed to sit between an XMLEventReader and an
 * application's XMLEventReader.  By default each method
 * does nothing but call the corresponding method on the
 * parent interface.
 *
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 * @see javaxx.xml.stream.XMLEventReader
 * @see StreamReaderDelegate
 */

public class EventReaderDelegate implements XMLEventReader {
  private XMLEventReader reader;

  /**
   * Construct an empty filter with no parent.
   */
  public EventReaderDelegate(){}

  /**
   * Construct an filter with the specified parent.
   * @param reader the parent
   */
  public EventReaderDelegate(XMLEventReader reader) {
    this.reader = reader;
  }

  /**
   * Set the parent of this instance.
   * @param reader the new parent
   */
  public void setParent(XMLEventReader reader) {
    this.reader = reader;
  }

  /**
   * Get the parent of this instance.
   * @return the parent or null if none is set
   */
  public XMLEventReader getParent() {
    return reader;
  }

  public XMLEvent nextEvent() 
    throws XMLStreamException
  {
    return reader.nextEvent();
  }

  public Object next() {
    return reader.next();
  }

  public boolean hasNext() 
  {
    return reader.hasNext();
  }

  public XMLEvent peek() 
    throws XMLStreamException 
  {
    return reader.peek();
  }

  public void close() 
    throws XMLStreamException
  {
    reader.close();
  }
  
  public String getElementText() 
    throws XMLStreamException
  {
    return reader.getElementText();
  }

  public XMLEvent nextTag() 
    throws XMLStreamException 
  {
    return reader.nextTag();
  }

  public Object getProperty(java.lang.String name)
    throws java.lang.IllegalArgumentException
  {
    return reader.getProperty(name);
  }

  public void remove() {
    reader.remove();
  }
}
