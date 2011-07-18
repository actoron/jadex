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

import java.io.*;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.StreamFilter;
import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLReporter;
import javaxx.xml.stream.XMLResolver;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.util.XMLEventAllocator;

import org.xml.sax.InputSource;

public class MXParserFactory extends XMLInputFactory {

  ConfigurationContextBase config = 
    new ConfigurationContextBase();

  public static XMLInputFactory newInstance() {
    return XMLInputFactory.newInstance();
  }

  public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException
  {
      /* 13-Mar-2006, TSa: Let's add minimal support, to make life bit
       *   easier when interacting with SAX tools...
       */
        if (source instanceof SAXSource) {
            SAXSource ss = (SAXSource) source;
            InputSource isource = ss.getInputSource();
            if (isource != null) {
                String sysId = isource.getSystemId();
                Reader r = isource.getCharacterStream();
                if (r != null) {
                    return createXMLStreamReader(sysId, r);
                }
                InputStream in = isource.getByteStream();
                if (in != null) {
                    return createXMLStreamReader(sysId, in);
                }
            }
            throw new XMLStreamException("Can only create STaX reader for a SAXSource if Reader or InputStream exposed via getSource(); can not use -- not implemented.");
        }

        if (source instanceof DOMSource) { 
            // !!! TBI?
            //DOMSource sr = (DOMSource) source;
        }
        throw new UnsupportedOperationException("XMLInputFactory.createXMLStreamReader("+source.getClass().getName()+") not yet implemented");
  }

  /**
   * Create a new XMLStreamReader from a java.io.stream
   * @param stream the InputStream to read from
   */
  public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
    MXParser pp = new MXParser();
    pp.setInput(stream);
    pp.setConfigurationContext(config);
    return pp;
  }

  /**
   * Create a new XMLStreamReader from a java.io.stream
   * @param stream the InputStream to read from
   * @param encoding the character encoding of the stream
   */
  public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
    MXParser pp = new MXParser();
    pp.setInput(stream, encoding);
    pp.setConfigurationContext(config);
    return pp;

  }

  public XMLStreamReader createXMLStreamReader(String systemId, java.io.InputStream stream) throws XMLStreamException {
    return createXMLStreamReader(stream);
  }

  public XMLStreamReader createXMLStreamReader(String systemId, java.io.Reader reader) throws XMLStreamException {
    return createXMLStreamReader(reader);
  }

  public XMLEventReader createXMLEventReader(String systemId, java.io.Reader reader) throws XMLStreamException {
    return createXMLEventReader(reader);
  }

  public XMLEventReader createXMLEventReader(String systemId, java.io.InputStream stream) throws XMLStreamException {
    return createXMLEventReader(stream);
  }

  /**
   * Create a new XMLEventReader from a reader
   * @param reader the XML data to read from
   */
  public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
    return createXMLEventReader(createXMLStreamReader(reader));
  }

  /**
   * Create a new XMLEventReader from an XMLStreamReader
   * @param reader the XMLEventReader to read from
   */
  public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {


    XMLEventReaderBase base;
    if (config.getEventAllocator() == null) {
      base = new XMLEventReaderBase(reader);
    } else {
      base = new XMLEventReaderBase(reader,
                                    (config.getEventAllocator()).newInstance());
    }
    return base;
  }
  
  /**
   * Create a new XMLEventReader from a JAXP source
   * @param source the source to read from
   */
  public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
    return createXMLEventReader(createXMLStreamReader(source));
  }

  /**
   * Create a new XMLEventReader from an input stream
   * @param stream the InputStream to read from
   */
  public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
    return createXMLEventReader(createXMLStreamReader(stream));
  }

  /**
   * Create a new XMLEventReader from an input stream
   * @param stream the InputStream to read from
   * @param encoding the character encoding of the stream
   */
  public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
    return createXMLEventReader(createXMLStreamReader(stream,encoding));
  }


  /**
   * The resolver that will be set on any XMLStreamReader or XMLEventReader created by this factory instance.
   */
  public XMLResolver getXMLResolver() {
    return config.getXMLResolver();
  }

  /**
   * The resolver that will be set on any XMLStreamReader or XMLEventReader created by this factory instance.
   * @param resolver the resolver to use to resolve references
   */
  public void  setXMLResolver(XMLResolver resolver) {
    config.setXMLResolver(resolver);
  }

  /**
   * Create a filtered reader that wraps the filter around the reader
   * @param reader the reader to filter
   * @param filter the filter to apply to the reader
   */
  public XMLStreamReader createFilteredReader(XMLStreamReader reader, 
                                              StreamFilter filter) 
    throws XMLStreamException 
  {
    return new StreamReaderFilter(reader,filter);
  }

  /**
   * Create a filtered event reader that wraps the filter around the event reader
   * @param reader the event reader to wrap
   * @param filter the filter to apply to the event reader
   */
  public XMLEventReader createFilteredReader(XMLEventReader reader, 
                                             EventFilter filter) 
    throws XMLStreamException
  {
    return new EventReaderFilter(reader,filter);
  }
  
  /**
   * The reporter that will be set on any XMLStreamReader or XMLEventReader created by this factory instance.
   */
  public XMLReporter getXMLReporter() {
    return config.getXMLReporter();
  }

  /**
   * The reporter that will be set on any XMLStreamReader or XMLEventReader created by this factory instance.
   * @param reporter the resolver to use to report non fatal errors
   */
  public void setXMLReporter(XMLReporter reporter) {
    config.setXMLReporter(reporter);
  }


  /**
   * Set a user defined event allocator for events
   * @param allocator the user defined allocator
   */
  public void setEventAllocator(XMLEventAllocator allocator) { 
    config.setEventAllocator(allocator);
  }

  /**
   * Gets the allocator used by streams created with this factory
   */
  public XMLEventAllocator getEventAllocator() {
    return config.getEventAllocator();
  }

  /**
   * Specifies that the stream produced by this code will append all adjacent text nodes. 
   */  
  public void setCoalescing(boolean coalescing){
    config.setCoalescing(coalescing);
  }

  /**
   * Indicates whether or not the factory is configured to produced streams that coalesce adjacent text nodes.
   */
  public boolean isCoalescing(){
    return config.isCoalescing();
  }

  public void setProperty(String name, Object value) throws IllegalArgumentException {
    // TODO - cwitt : check against supported feature list
    config.setProperty(name,value);
  }

  public Object getProperty(String name) throws IllegalArgumentException {
    return config.getProperty(name);
  }

  public XMLStreamReader createXMLStreamReader(Reader in) 
    throws XMLStreamException
  {
    MXParser pp = new MXParser();
    pp.setInput(in);
    pp.setConfigurationContext(config);
    return pp;
  }

  public boolean isPropertySupported(String name) {
    return config.isPropertySupported(name);
  }

}


