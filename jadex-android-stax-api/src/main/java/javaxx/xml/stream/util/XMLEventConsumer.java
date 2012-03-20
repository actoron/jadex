package javaxx.xml.stream.util;

import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.XMLEvent;

/**
 * This interface defines an event consumer interface.  The contract of the 
 * of a consumer is to accept the event.  This interface can be used to 
 * mark an object as able to receive events.  Add may be called several 
 * times in immediate succession so a consumer must be able to cache
 * events it hasn't processed yet.
 *
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 */
public interface XMLEventConsumer {

  /**
   * This method adds an event to the consumer. Calling this method 
   * invalidates the event parameter. The client application should 
   * discard all references to this event upon calling add. 
   * The behavior of an application that continues to use such references 
   * is undefined.
   *
   * @param event the event to add, may not be null
   */
  public void add(XMLEvent event) 
    throws XMLStreamException;
}
