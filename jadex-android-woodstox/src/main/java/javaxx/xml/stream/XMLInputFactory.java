package javaxx.xml.stream;

import javaxx.xml.transform.Source;
import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.FactoryConfigurationError;
import javaxx.xml.stream.FactoryFinder;
import javaxx.xml.stream.StreamFilter;
import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLResolver;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.util.XMLEventAllocator;

/**
 * Defines an abstract implementation of a factory for getting streams.
 * 
 * The following table defines the standard properties of this specification.  
 * Each property varies in the level of support required by each implementation.
 * The level of support required is described in the 'Required' column.
 *
 *   <table border="2" rules="all" cellpadding="4">
 *    <thead>
 *      <tr>
 *        <th align="center" colspan="5">
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
 *      </tr>
 * <tr><td>javaxx.xml.stream.isValidating</td><td>Turns on/off implementation specific DTD validation</td><td>Boolean</td><td>False</td><td>No</td></tr>
 * <tr><td>javaxx.xml.stream.isNamespaceAware</td><td>Turns on/off namespace processing for XML 1.0 support</td><td>Boolean</td><td>True</td><td>True (required) / False (optional)</td></tr>
 * <tr><td>javaxx.xml.stream.isCoalescing</td><td>Requires the processor to coalesce adjacent character data</td><td>Boolean</td><td>False</td><td>Yes</td></tr>
 * <tr><td>javaxx.xml.stream.isReplacingEntityReferences</td><td>replace internal entity references with their replacement text and report them as characters</td><td>Boolean</td><td>True</td><td>Yes</td></tr>
 *<tr><td>javaxx.xml.stream.isSupportingExternalEntities</td><td>Resolve external parsed entities</td><td>Boolean</td><td>Unspecified</td><td>Yes</td></tr>
 *<tr><td>javaxx.xml.stream.supportDTD</td><td>Use this property to request processors that do not support DTDs</td><td>Boolean</td><td>True</td><td>Yes</td></tr>
 *<tr><td>javaxx.xml.stream.reporter</td><td>sets/gets the impl of the XMLReporter </td><td>javaxx.xml.stream.XMLReporter</td><td>Null</td><td>Yes</td></tr>
 *<tr><td>javaxx.xml.stream.resolver</td><td>sets/gets the impl of the XMLResolver interface</td><td>javaxx.xml.stream.XMLResolver</td><td>Null</td><td>Yes</td></tr>
 *<tr><td>javaxx.xml.stream.allocator</td><td>sets/gets the impl of the XMLEventAllocator interface</td><td>javaxx.xml.stream.util.XMLEventAllocator</td><td>Null</td><td>Yes</td></tr>
 *    </tbody>
 *  </table>
 *
 *
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 * @see XMLOutputFactory
 * @see XMLEventReader
 * @see XMLStreamReader
 * @see EventFilter
 * @see XMLReporter
 * @see XMLResolver
 * @see javaxx.xml.stream.util.XMLEventAllocator
 */

public abstract class XMLInputFactory {
  /** 
   * The property used to turn on/off namespace support, 
   * this is to support XML 1.0 documents,
   * only the true setting must be supported
   */
  public static final String IS_NAMESPACE_AWARE=
    "javaxx.xml.stream.isNamespaceAware";

  /** 
   * The property used to turn on/off implementation specific validation 
   */
  public static final String IS_VALIDATING=
    "javaxx.xml.stream.isValidating";
  
  /** 
   * The property that requires the parser to coalesce adjacent character data sections 
   */
  public static final String IS_COALESCING=
    "javaxx.xml.stream.isCoalescing";
  
  /** 
   * Requires the parser to replace internal 
   * entity references with their replacement 
   * text and report them as characters
   */
  public static final String IS_REPLACING_ENTITY_REFERENCES=
    "javaxx.xml.stream.isReplacingEntityReferences";
  
  /** 
   *  The property that requires the parser to resolve external parsed entities
   */
  public static final String IS_SUPPORTING_EXTERNAL_ENTITIES=
    "javaxx.xml.stream.isSupportingExternalEntities";

  /** 
   *  The property that requires the parser to support DTDs
   */
  public static final String SUPPORT_DTD=
    "javaxx.xml.stream.supportDTD";

  /**
   * The property used to
   * set/get the implementation of the XMLReporter interface 
   */
  public static final String REPORTER=
    "javaxx.xml.stream.reporter";

  /**
   * The property used to set/get the implementation of the XMLResolver
   */
  public static final String RESOLVER=
    "javaxx.xml.stream.resolver";
  
  /**
   * The property used to set/get the implementation of the allocator
   */
  public static final String ALLOCATOR=
    "javaxx.xml.stream.allocator";

  protected XMLInputFactory(){}

  /**
   * Create a new instance of the factory.
   * This static method creates a new factory instance. 
   * This method uses the following ordered lookup procedure to determine 
   * the XMLInputFactory implementation class to load: 
   * Use the javaxx.xml.stream.XMLInputFactory system property. 
   * Use the properties file "lib/stax.properties" in the JRE directory. 
   * This configuration file is in standard java.util.Properties format and contains 
   * the fully qualified name of the implementation class with the key being the system property defined above. 
   * Use the Services API (as detailed in the JAR specification), if available, to determine the classname. 
   * The Services API will look for a classname in the file META-INF/services/javaxx.xml.stream.XMLInputFactory 
   * in jars available to the runtime. 
   * Platform default XMLInputFactory instance. 
   * Once an application has obtained a reference to a XMLInputFactory 
   * it can use the factory to configure and obtain stream instances. 
   *
   * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
   */
  public static XMLInputFactory newInstance()
    throws FactoryConfigurationError
  {
    return (XMLInputFactory) FactoryFinder.find(
      "javaxx.xml.stream.XMLInputFactory",
      "com.ctc.wstx.stax.WstxInputFactory");
  }

  /**
   * Create a new instance of the factory 
   *
   * @param factoryId             Name of the factory to find, same as
   *                              a property name
   * @param classLoader           classLoader to use
   * @return the factory implementation
   * @throws FactoryConfigurationError if an instance of this factory cannot be loaded
   */

  public static XMLInputFactory newInstance(String factoryId,
                                            ClassLoader classLoader)
    throws FactoryConfigurationError
  {
    return (XMLInputFactory) FactoryFinder.find(
      factoryId,
      "com.bea.xml.stream.MXParserFactory",
      classLoader);
  }

  /**
   * Create a new XMLStreamReader from a reader
   * @param reader the XML data to read from
   * @throws XMLStreamException 
   */
  public abstract XMLStreamReader createXMLStreamReader(java.io.Reader reader) 
    throws XMLStreamException;

  /**
   * Create a new XMLStreamReader from a JAXP source.  This method is optional.
   * @param source the source to read from
   * @throws UnsupportedOperationException if this method is not 
   * supported by this XMLInputFactory
   * @throws XMLStreamException 
   */
  public abstract XMLStreamReader createXMLStreamReader(Source source) 
    throws XMLStreamException;

  /**
   * Create a new XMLStreamReader from a java.io.InputStream
   * @param stream the InputStream to read from
   * @throws XMLStreamException 
   */
  public abstract XMLStreamReader createXMLStreamReader(java.io.InputStream stream) 
    throws XMLStreamException;

  /**
   * Create a new XMLStreamReader from a java.io.InputStream
   * @param stream the InputStream to read from
   * @param encoding the character encoding of the stream
   * @throws XMLStreamException 
   */
  public abstract XMLStreamReader createXMLStreamReader(java.io.InputStream stream, String encoding)
    throws XMLStreamException;

  /**
   * Create a new XMLStreamReader from a java.io.InputStream
   * @param systemId the system ID of the stream
   * @param stream the InputStream to read from
   */
  public abstract XMLStreamReader createXMLStreamReader(String systemId, java.io.InputStream stream)
    throws XMLStreamException;

  /**
   * Create a new XMLStreamReader from a java.io.InputStream
   * @param systemId the system ID of the stream
   * @param reader the InputStream to read from
   */
  public abstract XMLStreamReader createXMLStreamReader(String systemId, java.io.Reader reader)
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from a reader
   * @param reader the XML data to read from
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createXMLEventReader(java.io.Reader reader) 
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from a reader
   * @param systemId the system ID of the input 
   * @param reader the XML data to read from
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createXMLEventReader(String systemId, java.io.Reader reader)
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from an XMLStreamReader.  After being used
   * to construct the XMLEventReader instance returned from this method
   * the XMLStreamReader must not be used.  
   * @param reader the XMLStreamReader to read from (may not be modified)
   * @return a new XMLEventReader
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createXMLEventReader(XMLStreamReader reader) 
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from a JAXP source.
   * Support of this method is optional.
   * @param source the source to read from
   * @throws UnsupportedOperationException if this method is not 
   * supported by this XMLInputFactory
   */
  public abstract XMLEventReader createXMLEventReader(Source source) 
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from a java.io.InputStream
   * @param stream the InputStream to read from
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createXMLEventReader(java.io.InputStream stream) 
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from a java.io.InputStream
   * @param stream the InputStream to read from
   * @param encoding the character encoding of the stream
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createXMLEventReader(java.io.InputStream stream, String encoding) 
    throws XMLStreamException;

  /**
   * Create a new XMLEventReader from a java.io.InputStream
   * @param systemId the system ID of the stream
   * @param stream the InputStream to read from
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createXMLEventReader(String systemId, java.io.InputStream stream)
    throws XMLStreamException;

  /**
   * Create a filtered reader that wraps the filter around the reader
   * @param reader the reader to filter
   * @param filter the filter to apply to the reader
   * @throws XMLStreamException 
   */
  public abstract XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) 
    throws XMLStreamException;

  /**
   * Create a filtered event reader that wraps the filter around the event reader
   * @param reader the event reader to wrap
   * @param filter the filter to apply to the event reader
   * @throws XMLStreamException 
   */
  public abstract XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) 
    throws XMLStreamException;

  /**
   * The resolver that will be set on any XMLStreamReader or XMLEventReader created 
   * by this factory instance.
   */
  public abstract XMLResolver getXMLResolver();

  /**
   * The resolver that will be set on any XMLStreamReader or XMLEventReader created 
   * by this factory instance.
   * @param resolver the resolver to use to resolve references
   */
  public abstract void  setXMLResolver(XMLResolver resolver);

  /**
   * The reporter that will be set on any XMLStreamReader or XMLEventReader created 
   * by this factory instance.
   */
  public abstract XMLReporter getXMLReporter();

  /**
   * The reporter that will be set on any XMLStreamReader or XMLEventReader created 
   * by this factory instance.
   * @param reporter the resolver to use to report non fatal errors
   */
  public abstract void setXMLReporter(XMLReporter reporter);

  /**
   * Allows the user to set specific feature/property on the underlying implementation. The underlying implementation
   * is not required to support every setting of every property in the specification and may use IllegalArgumentException
   * to signal that an unsupported property may not be set with the specified value.
   * @param name The name of the property (may not be null)
   * @param value The value of the property
   * @throws java.lang.IllegalArgumentException if the property is not supported
   */
  public abstract void setProperty(java.lang.String name, Object value) 
    throws java.lang.IllegalArgumentException;  

  /**
   * Get the value of a feature/property from the underlying implementation
   * @param name The name of the property (may not be null)
   * @return The value of the property
   * @throws IllegalArgumentException if the property is not supported
   */
  public abstract Object getProperty(java.lang.String name) 
    throws java.lang.IllegalArgumentException;  


  /**
   * Query the set of properties that this factory supports.
   *
   * @param name The name of the property (may not be null)
   * @return true if the property is supported and false otherwise
   */
  public abstract boolean isPropertySupported(String name);

  /**
   * Set a user defined event allocator for events
   * @param allocator the user defined allocator
   */
  public abstract void setEventAllocator(XMLEventAllocator allocator);

  /**
   * Gets the allocator used by streams created with this factory
   */
  public abstract XMLEventAllocator getEventAllocator();

}

