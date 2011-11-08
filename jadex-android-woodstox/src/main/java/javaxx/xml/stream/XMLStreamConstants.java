package javaxx.xml.stream;

/**
 * This interface declares the constants used in this API.
 * Numbers in the range 0 to 256 are reserved for the specification,
 * user defined events must use event codes outside that range.
 */

public interface XMLStreamConstants {
  /**
   * Indicates an event is a start element
   * @see javaxx.xml.stream.events.StartElement
   */
  public static final int START_ELEMENT=1;
  /**
   * Indicates an event is an end element
   * @see javaxx.xml.stream.events.EndElement
   */
  public static final int END_ELEMENT=2;
  /**
   * Indicates an event is a processing instruction
   * @see javaxx.xml.stream.events.ProcessingInstruction
   */
  public static final int PROCESSING_INSTRUCTION=3;

  /**
   * Indicates an event is characters
   * @see javaxx.xml.stream.events.Characters
   */
  public static final int CHARACTERS=4;

  /**
   * Indicates an event is a comment
   * @see javaxx.xml.stream.events.Comment
   */
  public static final int COMMENT=5;

  /**
   * The characters are white space 
   * (see [XML], 2.10 "White Space Handling").
   * Events are only reported as SPACE if they are ignorable white
   * space.  Otherwise they are reported as CHARACTERS.
   * @see javaxx.xml.stream.events.Characters
   */
  public static final int SPACE=6;

  /**
   * Indicates an event is a start document
   * @see javaxx.xml.stream.events.StartDocument
   */
  public static final int START_DOCUMENT=7;

  /**
   * Indicates an event is an end document
   * @see javaxx.xml.stream.events.EndDocument
   */
  public static final int END_DOCUMENT=8;

  /**
   * Indicates an event is an entity reference
   * @see javaxx.xml.stream.events.EntityReference
   */
  public static final int ENTITY_REFERENCE=9;

  /**
   * Indicates an event is an attribute
   * @see javaxx.xml.stream.events.Attribute
   */
  public static final int ATTRIBUTE=10;

  /**
   * Indicates an event is a DTD
   * @see javaxx.xml.stream.events.DTD
   */
  public static final int DTD=11;

  /**
   * Indicates an event is a CDATA section
   * @see javaxx.xml.stream.events.Characters
   */
  public static final int CDATA=12;

  /**
   * Indicates the event is a namespace declaration
   * 
   * @see javaxx.xml.stream.events.Namespace
   */
  public static final int NAMESPACE=13;

  /**
   * Indicates a Notation 
   * @see javaxx.xml.stream.events.NotationDeclaration
   */
  public static final int NOTATION_DECLARATION=14;

  /**
   * Indicates a Entity Declaration
   * @see javaxx.xml.stream.events.NotationDeclaration
   */
  public static final int ENTITY_DECLARATION=15;
}




