package javaxx.xml.stream.events;

import javaxx.xml.stream.events.XMLEvent;

/**
 * An interface for comment events
 * 
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 */
public interface Comment extends XMLEvent {

  /**
   * Return the string data of the comment, returns empty string if it
   * does not exist
   */
  public String getText();
}
