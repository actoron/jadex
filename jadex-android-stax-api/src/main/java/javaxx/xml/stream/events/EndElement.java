package javaxx.xml.stream.events;

import java.util.Iterator;

import javaxx.xml.stream.events.XMLEvent;
import javaxx.xml.namespace.QName;
/**
 * An interface for the end element event.  An EndElement is reported
 * for each End Tag in the document.
 *
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 * @see XMLEvent
 */
public interface EndElement extends XMLEvent {

  /**
   * Get the name of this event
   * @return the qualified name of this event
   */
  public QName getName();

  /**
   * Returns an Iterator of namespaces that have gone out
   * of scope.  Returns an empty iterator if no namespaces have gone
   * out of scope.
   * @return an Iterator over Namespace interfaces, or an
   * empty iterator
   */
  public Iterator getNamespaces();

}
