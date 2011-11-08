package javaxx.xml.stream.events;

import javaxx.xml.stream.events.EntityDeclaration;
import javaxx.xml.stream.events.XMLEvent;

/**
 * An interface for handling Entity events.
 * 
 * This event reports entities that have not been resolved
 * and reports their replacement text unprocessed (if
 * available).  This event will be reported if javaxx.xml.stream.isReplacingEntityReferences 
 * is set to false.  If javaxx.xml.stream.isReplacingEntityReferences is set to true
 * entity references will be resolved transparently.
 *
 * Entities are handled in two possible ways:
 *
 * (1) If javaxx.xml.stream.isReplacingEntityReferences is set to true
 * all entity references are resolved and reported as markup transparently.
 * (2) If javaxx.xml.stream.isReplacingEntityReferences is set to false
 * Entity references are reported as an EntityReference Event.
 *
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 */
public interface EntityReference extends XMLEvent {

  /**
   * Return the declaration of this entity.
   */
  EntityDeclaration getDeclaration();

  /**
   * The name of the entity
   * @return the entity's name, may not be null
   */
  String getName();
}
