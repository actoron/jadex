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

import javaxx.xml.stream.XMLInputFactory;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.stream.events.XMLEvent;

/**
 * <p> Automatically write a reader.</p>
 */

public class ReaderToWriter {

  private XMLStreamWriter writer;

  public ReaderToWriter(){}
  public ReaderToWriter(XMLStreamWriter xmlw) {
    writer = xmlw;
  }

  public void setStreamWriter(XMLStreamWriter xmlw) {
    writer = xmlw;
  }
  public void write(XMLStreamReader xmlr) 
    throws XMLStreamException
  {
    System.out.println("wrote event");
    switch (xmlr.getEventType()) {
    case XMLEvent.START_ELEMENT:
      String prefix = xmlr.getPrefix();
      String namespaceURI = xmlr.getNamespaceURI();
      if (namespaceURI != null) {
        if(prefix != null)
          writer.writeStartElement(xmlr.getPrefix(),
                                   xmlr.getLocalName(),
                                   xmlr.getNamespaceURI());
        else
          writer.writeStartElement(xmlr.getNamespaceURI(),
                                   xmlr.getLocalName());
      } else {
        writer.writeStartElement(xmlr.getLocalName());
      }

      for (int i =0; i < xmlr.getNamespaceCount(); i++) {
        writer.writeNamespace(xmlr.getNamespacePrefix(i),
                              xmlr.getNamespaceURI(i));
      }
      break;
    case XMLEvent.END_ELEMENT:
      writer.writeEndElement();
      break;
    case XMLEvent.SPACE:
    case XMLEvent.CHARACTERS:
      writer.writeCharacters(xmlr.getTextCharacters(),
                             xmlr.getTextStart(),
                             xmlr.getTextLength());
      break;
    case XMLEvent.PROCESSING_INSTRUCTION:
      writer.writeProcessingInstruction(xmlr.getPITarget(),
                                        xmlr.getPIData());
      break;
    case XMLEvent.CDATA:
      writer.writeCData(xmlr.getText());
      break;

    case XMLEvent.COMMENT:
      writer.writeComment(xmlr.getText());
      break;
    case XMLEvent.ENTITY_REFERENCE:
      writer.writeEntityRef(xmlr.getLocalName());
      break;
    case XMLEvent.START_DOCUMENT:
      String encoding = xmlr.getCharacterEncodingScheme();
      String version = xmlr.getVersion();
     
      if (encoding != null && version != null) 
        writer.writeStartDocument(encoding,
                                  version);
      else if (version != null)
        writer.writeStartDocument(xmlr.getVersion());
      break;
    case XMLEvent.END_DOCUMENT:
      writer.writeEndDocument();
      break;
    case XMLEvent.DTD:
      writer.writeDTD(xmlr.getText());
      break;

    }
  }

  public XMLStreamWriter writeAll(XMLStreamReader xmlr) 
    throws XMLStreamException
  {
    while (xmlr.hasNext()) {
      write(xmlr);
      xmlr.next();
    }
    writer.flush();
    return writer;
  }

  public static void main(String args[]) throws Exception {
    XMLInputFactory xmlif = XMLInputFactory.newInstance();
    XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
    XMLStreamReader xmlr = xmlif.createXMLStreamReader(new java.io.FileReader(args[0]));
    XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(System.out);

    ReaderToWriter rtow = new ReaderToWriter(xmlw);
    while (xmlr.hasNext()) {
      rtow.write(xmlr);
      xmlr.next();
    }
    xmlw.flush();
  }
}
