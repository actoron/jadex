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

import java.io.Writer;
import java.io.IOException;

import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.stream.events.XMLEvent;

/**
 * <p> Writes XML in a non-xml format to create XML tests. </p>
 */

public class XMLStreamRecorder extends XMLWriterBase {

  public XMLStreamRecorder(){}
  public XMLStreamRecorder(Writer writer) {
    super(writer);
  }

  protected String writeName(String prefix,String namespaceURI, String localName) 
    throws XMLStreamException
  {
    if (!"".equals(namespaceURI))
      write("['"+namespaceURI+"':");
    else
      write("[");
    prefix = super.writeName(prefix,namespaceURI,localName);
    write(']');
    return prefix;
  }

  protected void writeType(int type) 
    throws XMLStreamException
  {
    closeStartElement();
    write('[');
    write(com.bea.xml.stream.util.ElementTypeNames.getEventTypeString(type));
    write(']');

  }
  
  protected void openStartTag() 
    throws XMLStreamException
  {
    write('[');
  }

  protected void closeStartTag() throws XMLStreamException{
    write("];\n");
  }

  protected void openEndTag() 
    throws XMLStreamException
  {
    write('[');
  }
  
  protected void closeEndTag() 
    throws XMLStreamException
  {
    write(']');
  }

  public void writeAttribute(String namespaceURI,
                             String localName,
                             String value) 
    throws XMLStreamException
  {
    write("[[ATTRIBUTE]");
    writeName("",namespaceURI,localName);
    write("=");
    writeCharactersInternal(value.toCharArray(),0,value.length(),true);
    write("]");
  }
  public void writeNamespace(String prefix, String namespaceURI) 
    throws XMLStreamException 
  {

    if(!isOpen())
     throw new XMLStreamException("A start element must be written before a namespace");
    if (prefix == null || "".equals(prefix) || "xmlns".equals(prefix)) {
      writeDefaultNamespace(namespaceURI);
      return;
    }
    write("[[NAMESPACE][");
    write("xmlns:");
    write(prefix);
    write("]=[");
    write(namespaceURI);
    write("]");
    setPrefix(prefix,namespaceURI);
    write(']');
  }

  public void writeDefaultNamespace(String namespaceURI)
    throws XMLStreamException 
  {
    write("[[DEFAULT][");
    if(!isOpen())
     throw new XMLStreamException("A start element must be written before the default namespace");
    write("xmlns]");
    write("=[");
    write(namespaceURI);
    write("]");
    setPrefix(DEFAULTNS,namespaceURI);
    write(']');
  }

  public void writeComment(String data) 
      throws XMLStreamException
  {
    closeStartElement();
    write("[");
    if (data != null)
      write(data);
    write("]");
  }

  public void writeProcessingInstruction(String target,
                                         String text) 
    throws XMLStreamException
  {
    closeStartElement();
    write("[");
    if (target != null)
      write("["+target+"]");
    if (text != null) {
      write(",["+text+"]");
    }
    write("]");
  }

  public void writeDTD(String dtd) 
    throws XMLStreamException
  {
    write("[");
    super.write(dtd);
    write("]");
  }

  public void writeCData(String data) 
    throws XMLStreamException
  {
    write("[");
    if (data != null)
      write(data);
    write("]");
  }

  public void writeEntityRef(String name) 
    throws XMLStreamException
  {
    write("[");
    super.writeEntityRef(name);
    write("]");
  }

  public void writeStartDocument() 
    throws XMLStreamException
  {
    write("[[1.0],[utf-8]]");
  }

  public void writeStartDocument(String version) 
    throws XMLStreamException
  {
    write("[[");
    write(version);
    write("],[utf-8]]");
  }

  public void writeStartDocument(String encoding,
                                 String version) 
    throws XMLStreamException
  {
    write("[[");
    write(version);
    write("],[");
    write(encoding);
    write("]]");
  }
  protected void writeCharactersInternal(char characters[],
                                         int start,
                                         int length,
                                         boolean isAttributeValue) 
    throws XMLStreamException
  {
    if(length == 0) write("[]");
    else { 
      write("[");
      write(characters,start,length);
      write("]");
    }
  }
  
  public void write(XMLStreamReader xmlr) 
    throws XMLStreamException
  {
    writeType(xmlr.getEventType());
    super.write(xmlr);
    if (!isOpen()) write(";\n");
  }

  public static void main(String args[]) throws Exception {
    XMLInputFactory xmlif = XMLInputFactory.newInstance();
    XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
    XMLStreamReader xmlr = xmlif.createXMLStreamReader(new java.io.FileReader(args[0]));

    XMLStreamRecorder r = new XMLStreamRecorder(new java.io.OutputStreamWriter(new java.io.FileOutputStream("out.stream")));

    while (xmlr.hasNext()) {
      r.write(xmlr);
      xmlr.next();
    }
    r.write(xmlr);
    r.flush();
  }

  
}






