package javaxx.xml.stream;

import javaxx.xml.stream.FactoryConfigurationError;
import javaxx.xml.stream.FactoryFinder;
import javaxx.xml.stream.XMLEventWriter;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.transform.Result;

/**
 * Defines an abstract implementation of a factory for 
 * getting XMLEventWriters and XMLStreamWriters.
 *
 * The following table defines the standard properties of this specification.  
 * Each property varies in the level of support required by each implementation.
 * The level of support required is described in the 'Required' column.
 *
 *     <table border="2" rules="all" cellpadding="4">
 *     <thead>
 *      <tr>
 *        <th align="center" colspan="2">
 *          Configuration parameters
 *        </th>
 *      </tr>
 *    </thead>
 *    <tbody>
 *      <tr>
 *        <th>Property Name</th>
 *        <th>Behavior</th>
 *        <th>Return type</th>
 *        <th>Default Value</th>
 *        <th>Required</th>
 *              </tr>
 *         <tr><td>javaxx.xml.stream.isRepairingNamespaces</td><td>defaults prefixes on the output side</td><td>Boolean</td><td>False</td><td>Yes</td></tr>
 *      </tbody>
 *   </table>
 *
 * <p>The following paragraphs describe the namespace and prefix repair algorithm:</p>
 *
 * <p>The property can be set with the following code line:
 * <code>setProperty("javaxx.xml.stream.isRepairingNamespaces",new Boolean(true|false));</code></p>
 * 
 * <p>This property specifies that the writer default namespace prefix declarations. 
 * The default value is false. </p>
 *
 * <p>If a writer isRepairingNamespaces it will create a namespace declaration
 * on the current StartElement for
 * any attribute that does not 
 * currently have a namespace declaration in scope.  If the StartElement
 * has a uri but no prefix specified a prefix will be assigned, if the prefix
 * has not been declared in a parent of the current StartElement it will be declared
 * on the current StartElement.  If the defaultNamespace is bound and in scope
 * and the default namespace matches the URI of the attribute or StartElement
 * QName no prefix will be assigned.</p>
 *
 * <p>If an element or attribute name has a prefix, but is not 
 * bound to any namespace URI, then the prefix will be removed 
 * during serialization.</p> 
 *
 * <p>If element and/or attribute names in the same start or 
 * empty-element tag are bound to different namespace URIs and 
 * are using the same prefix then the element or the first 
 * occurring attribute retains the original prefix and the 
 * following attributes have their prefixes replaced with a 
 * new prefix that is bound to the namespace URIs of those 
 * attributes. </p>
 *
 * <p>If an element or attribute name uses a prefix that is 
 * bound to a different URI than that inherited from the 
 * namespace context of the parent of that element and there 
 * is no namespace declaration in the context of the current 
 * element then such a namespace declaration is added. </p>
 *
 * <p>If an element or attribute name is bound to a prefix and 
 * there is a namespace declaration that binds that prefix 
 * to a different URI then that namespace declaration is 
 * either removed if the correct mapping is inherited from 
 * the parent context of that element, or changed to the 
 * namespace URI of the element or attribute using that prefix.</p> 
 *
 * @version 1.0 
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 * @see XMLInputFactory
 * @see XMLEventWriter
 * @see XMLStreamWriter
 */
public abstract class XMLOutputFactory {
  /** 
   * Property used to set prefix defaulting on the output side 
   */
  public static final String IS_REPAIRING_NAMESPACES=
    "javaxx.xml.stream.isRepairingNamespaces";

  protected XMLOutputFactory(){}

  /**
   * Create a new instance of the factory using the default factory location
   * mechanism (check env. variable, jaxp.properties, jar services manifest,
   * in this order)
   * 
   * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
   */
  public static XMLOutputFactory newInstance() 
    throws FactoryConfigurationError
  {
    Object find = FactoryFinder.find("javaxx.xml.stream.XMLOutputFactory",
                                                 "com.ctc.wstx.stax.WstxOutputFactory");
	return (XMLOutputFactory) find;
  }

  /**
   * Create a new instance of the factory 
   *
   * @param factoryId Name of the factory to find; not the class name but the
   *    property name to use
   * @param classLoader classLoader to use
   * @return the factory implementation
   * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
   */
  public static XMLOutputFactory newInstance(String factoryId,
					     ClassLoader classLoader)
    throws FactoryConfigurationError
  {
    return (XMLOutputFactory) FactoryFinder.find(
      factoryId,
      "com.bea.xml.stream.XMLOutputFactoryBase",
      classLoader);
  }

  /**
   * Create a new XMLStreamWriter that writes to a writer
   * @param stream the writer to write to
   * @throws XMLStreamException
   */
  public abstract XMLStreamWriter createXMLStreamWriter(java.io.Writer stream) throws XMLStreamException;

  /**
   * Create a new XMLStreamWriter that writes to a stream
   * @param stream the stream to write to
   * @throws XMLStreamException
   */
  public abstract XMLStreamWriter createXMLStreamWriter(java.io.OutputStream stream) throws XMLStreamException;

  /**
   * Create a new XMLStreamWriter that writes to a stream
   * @param stream the stream to write to
   * @param encoding the encoding to use
   * @throws XMLStreamException
   */
  public abstract XMLStreamWriter createXMLStreamWriter(java.io.OutputStream stream,
                                         String encoding) throws XMLStreamException;

  /**
   * Create a new XMLStreamWriter that writes to a JAXP result.  This method is optional.
   * @param result the result to write to
   * @throws UnsupportedOperationException if this method is not 
   * supported by this XMLOutputFactory
   * @throws XMLStreamException 
   */
  public abstract XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException;


  /**
   * Create a new XMLEventWriter that writes to a JAXP result.  This method is optional.
   * @param result the result to write to
   * @throws UnsupportedOperationException if this method is not 
   * supported by this XMLOutputFactory
   * @throws XMLStreamException 
   */
  public abstract XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException;

  /**
   * Create a new XMLEventWriter that writes to a stream
   * @param stream the stream to write to
   * @throws XMLStreamException
   */
  public abstract XMLEventWriter createXMLEventWriter(java.io.OutputStream stream) throws XMLStreamException;



  /**
   * Create a new XMLEventWriter that writes to a stream
   * @param stream the stream to write to
   * @param encoding the encoding to use
   * @throws XMLStreamException
   */
  public abstract XMLEventWriter createXMLEventWriter(java.io.OutputStream stream,
                                                     String encoding) throws XMLStreamException;

  /**
   * Create a new XMLEventWriter that writes to a writer
   * @param stream the stream to write to
   * @throws XMLStreamException
   */
  public abstract XMLEventWriter createXMLEventWriter(java.io.Writer stream) throws XMLStreamException;

  /**
   * Allows the user to set specific features/properties on the underlying implementation. 
   * @param name The name of the property
   * @param value The value of the property
   * @throws java.lang.IllegalArgumentException if the property is not supported
   */
  public abstract void setProperty(java.lang.String name, 
                                    Object value) 
    throws IllegalArgumentException;

  /**
   * Get a feature/property on the underlying implementation
   * @param name The name of the property
   * @return The value of the property
   * @throws java.lang.IllegalArgumentException if the property is not supported
   */
  public abstract Object getProperty(java.lang.String name)
    throws IllegalArgumentException;

  /**
   * Query the set of properties that this factory supports.
   *
   * @param name The name of the property (may not be null)
   * @return true if the property is supported and false otherwise
   */
  public abstract boolean isPropertySupported(String name);
}








