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

import java.util.Hashtable;
import javax.xml.transform.Result;
import javaxx.xml.stream.XMLEventWriter;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;

/**
 * <p> Creates instances of the various interfaces for XML output </p>
 */

public class XMLOutputFactoryBase 
  extends XMLOutputFactory
{
  ConfigurationContextBase config = new ConfigurationContextBase();

  public XMLStreamWriter createXMLStreamWriter(java.io.Writer stream) 
    throws XMLStreamException
  {
    XMLWriterBase b = new XMLWriterBase(stream); 
    b.setConfigurationContext(config);
    return b;
  }

  public  XMLStreamWriter createXMLStreamWriter(java.io.OutputStream stream) 
    throws XMLStreamException
  {
      return createXMLStreamWriter(new BufferedWriter(new OutputStreamWriter(stream), 500)); 
  }

  public  XMLStreamWriter createXMLStreamWriter(java.io.OutputStream stream,
                                 String encoding) 
    throws XMLStreamException
  {
    try {
        return createXMLStreamWriter(new BufferedWriter(new OutputStreamWriter(stream,encoding), 500)); 
    } catch (java.io.UnsupportedEncodingException uee) {
      throw new XMLStreamException("Unsupported encoding "+encoding,uee);
    }
  }
  public  XMLEventWriter createXMLEventWriter(java.io.OutputStream stream) 
    throws XMLStreamException
  {
    return new XMLEventWriterBase(createXMLStreamWriter(stream));
  }

  public  XMLEventWriter createXMLEventWriter(java.io.Writer stream) 
    throws XMLStreamException
  {
    return new XMLEventWriterBase(createXMLStreamWriter(stream));
  }

  public  XMLEventWriter createXMLEventWriter(java.io.OutputStream stream,
                                           String encoding) 
    throws XMLStreamException
  {
    return new XMLEventWriterBase(createXMLStreamWriter(stream,encoding));
  }
  public  void setProperty(java.lang.String name, 
                          Object value){
    config.setProperty(name,value);
  }
  public  Object getProperty(java.lang.String name) {
    return config.getProperty(name);
  }
  public  boolean isPrefixDefaulting(){ 
    return config.isPrefixDefaulting();
  }
  public  void setPrefixDefaulting(boolean value){
    config.setPrefixDefaulting(value);
  }
  public boolean isPropertySupported(String name) {
    return config.isPropertySupported(name);
  }
  public XMLStreamWriter createXMLStreamWriter(Result result) 
    throws XMLStreamException 
  {
    throw new UnsupportedOperationException();
  }

  public XMLEventWriter createXMLEventWriter(Result result) 
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }

}
 
