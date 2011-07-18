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
package com.bea.xml.stream.events;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.StartElement;
import javaxx.xml.stream.events.XMLEvent;

import com.bea.xml.stream.util.ElementTypeNames;

/**
 * <p> Base event class for events to extend from </p>
 */


public abstract class BaseEvent implements XMLEvent, Location {
  private int eventType = -1;
  private int line = -1;
  private int column = -1;
  private int characterOffset = 0;
  private String locationURI;
  public BaseEvent(){}
  public BaseEvent(int type) {
    eventType = type;
  }
  public int getEventType() { 
    return eventType; 
  }
  protected void setEventType(int type) { 
    this.eventType = type; 
  }
  public String getTypeAsString() { 
    return ElementTypeNames.getEventTypeString(eventType); 
  }
  public boolean isStartElement() { 
    return (eventType ==  XMLEvent.START_ELEMENT);
  }
  public boolean isEndElement() { 
    return (eventType == XMLEvent.END_ELEMENT);
  }
  public boolean isEntityReference(){ 
    return (eventType == XMLEvent.ENTITY_REFERENCE);
  }
  public boolean isProcessingInstruction(){ 
    return (eventType == XMLEvent.PROCESSING_INSTRUCTION);
  }
  public boolean isCharacters(){ 
    return (eventType == XMLEvent.CHARACTERS);
  }
  public boolean isStartDocument(){ 
    return (eventType == XMLEvent.START_DOCUMENT);
  }
  public boolean isEndDocument(){ 
    return (eventType == XMLEvent.END_DOCUMENT);
  }
  public boolean isAttribute(){ 
    return (eventType == XMLEvent.ATTRIBUTE);
  }
  public boolean isNamespace(){ 
    return (eventType == XMLEvent.NAMESPACE);
  }

  public Location getLocation() {
    return this;
  }
  public String getPublicId() {
    return null;
  }
  public String getSystemId() {
    return null;
  }
  public String getSourceName() { return null; }
  public int getLineNumber() { return line; }
  public void setLineNumber(int line) { this.line = line; }
  public int getColumnNumber() { return column; }
  public void setColumnNumber(int col) { this.column = col; }
  public int getCharacterOffset() { return characterOffset; }
  public void setCharacterOffset(int c) { characterOffset = c; }
  public String getLocationURI() { return locationURI; }
  public void setLocationURI(String uri) { locationURI = uri; }
  public StartElement asStartElement() {
    return (StartElement) this;
  }
  public EndElement asEndElement() {
    return (EndElement) this;
  }
  public Characters asCharacters() {
    return (Characters) this;
  }
  public void recycle() {
  }
  public QName getSchemaType() { return null; }

  public final void writeAsEncodedUnicode(Writer writer) 
      throws XMLStreamException
  {
      try {
          doWriteAsEncodedUnicode(writer);
      } catch (IOException e) {
          throw new XMLStreamException(e);
      }
  }

  /**
   * Template method to be implemented by sub-classes. 
   */
  protected abstract void doWriteAsEncodedUnicode(Writer writer) 
      throws IOException, XMLStreamException;

  public String toString()
  {
      StringWriter sw = new StringWriter(64);
      try {
          writeAsEncodedUnicode(sw);
      } catch (XMLStreamException e) {
          sw.write("[ERROR: ");
          sw.write(e.toString());
          sw.write("]");
      }
      return sw.toString();
  }
}

