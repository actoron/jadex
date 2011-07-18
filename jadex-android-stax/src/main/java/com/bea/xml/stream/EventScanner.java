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

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.XMLEvent;

import com.bea.xml.stream.util.ElementTypeNames;
import com.bea.xml.stream.AttributeBase;
import com.bea.xml.stream.NamespaceBase;

import java.io.Reader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileReader;

/**
 * <p> This class replays events from a simple non-xml file format </p>
 *
 */


public class EventScanner {
  protected Reader reader;
  protected char currentChar;
  protected int currentLine=0;
  private boolean readEndDocument = false;
  public EventScanner(){}
  public EventScanner(Reader reader) 
    throws IOException
  {
    setReader(reader);
  }
  public void setReader(Reader reader) 
    throws IOException
  {
    this.reader = reader;
    read();
    skipSpace();
  }

  protected String readString(char delim) 
    throws IOException, XMLStreamException
  {
    StringBuffer buf = new StringBuffer();
    while(getChar()!=delim) {
      if (getChar() == '[' && delim ==']') {
        read();
        buf.append('[');
        if (getChar() != ']')
          buf.append(readString(']'));
        buf.append(']');
        read(']');
      } else {
        buf.append(getChar());
        read();
      }
    }
    return buf.toString();

  }
  protected char getChar() {
    return currentChar;
  }
  protected void skipSpace() 
    throws IOException
  {
    while (currentChar == ' ' | currentChar == '\n' | currentChar == '\t' | currentChar =='\r')
      read();
  }
  protected char read() 
    throws IOException
  {
    currentChar = (char) reader.read();
    if (currentChar == '\n') currentLine++;
    return currentChar;
  }
  protected char read(char c) 
    throws XMLStreamException, IOException
  {
    if (currentChar == c) return read();
    else
      throw new XMLStreamException("Unexpected character '"+currentChar+"' , expected '"+c+"' at line "+currentLine);
  }
  protected void read(String s) 
    throws XMLStreamException, IOException
  {
    for (int i=0; i < s.length(); i++)
      read(s.charAt(i));
  }
  protected int readType() 
    throws XMLStreamException, IOException
  {
 
    read('[');
    String typeName = readString(']');
    int type= ElementTypeNames.getEventType(typeName);
    read(']');
    return type;
  }
  public EventState readStartElement() 
    throws XMLStreamException, IOException
  {
    EventState state = new EventState(XMLEvent.START_ELEMENT);
    read('[');
    state.setName(readName());
    if (getChar()=='[') {
      List atts = readAttributes();
      Iterator i = atts.iterator();
      while(i.hasNext()) {
        Object obj = i.next();
        if (obj instanceof Namespace) 
          state.addNamespace(obj);
        else
          state.addAttribute(obj);
      }

    }
    read(']');
    return state;
  }
  public EventState readEndElement()   
    throws XMLStreamException, IOException
  {
    EventState state = 
      new EventState(XMLEvent.END_ELEMENT);
    read('[');
    state.setName(readName());
    read(']');
    return state;
  }

  public EventState readProcessingInstruction()  
    throws XMLStreamException, IOException
  {
    EventState state =
      new EventState(XMLEvent.PROCESSING_INSTRUCTION);
    read('[');
    String name = readString(']');
    read(']');
    String s = null;
    if (getChar() == ',') {     
      read(",[");
      s = readString(']');
      read(']');
    }
    state.setData(name);
    state.setExtraData(s);
    return state;
  }
  public EventState readCharacterData()   
    throws XMLStreamException, IOException
  {
    EventState state =
      new EventState(XMLEvent.CHARACTERS);
    read('[');
    state.setData(readString(']'));
    read(']');
    return state;
  }
  public EventState readCDATA()   
    throws XMLStreamException, IOException
  {
    EventState state =
      new EventState(XMLEvent.CDATA);
    read('[');
    readString(']');
    read(']');
    return state;
  }


  public EventState readStartDocument()   
    throws XMLStreamException, IOException
  {
    EventState state =
      new EventState(XMLEvent.START_DOCUMENT);
    if (getChar() != ';') {
      read('['); 
      read('[');
      String version = readString(']');
      read(']');
      read(',');
      read('[');
      String encoding = readString(']');
      read(']');
      read(']');
      state.setData(version);
      state.setExtraData(encoding);
    }
    return state;
  }


  public EventState readDTD() 
    throws XMLStreamException, IOException
  {
    EventState state =
      new EventState(XMLEvent.DTD);
    read('[');
    String dtd = readString(']');
    read(']');
    state.setData(dtd);
    return state;
  }
  public EventState readEndDocument()   
    throws XMLStreamException
  {
    EventState state =
      new EventState(XMLEvent.END_DOCUMENT);
    return state;
  }
  public EventState readComment()   
    throws XMLStreamException, IOException
  {
    EventState state = 
      new EventState(XMLEvent.COMMENT);
    read('[');
    state.setData(readString(']'));
    read(']');
    return state;
  }
  public String getPrefix(String name) {
    int index = name.indexOf(':');
    if (index == -1) return null;
    return name.substring(0,index);
  }
  public String getName(String name) {
    int index = name.indexOf(':');
    if (index == -1) return name;
    return name.substring(index+1);
  }
  public QName readName() 
    throws XMLStreamException, IOException
  {
    read('[');
    QName n = readName(']');
    read(']');
    return n;
  }

  public QName readName(char delim)   
    throws XMLStreamException, IOException
  {
    String uri = "";
    String prefix = "";
    if (getChar() == '\'') {
      read('\'');
      uri=readString('\'');
      read('\'');
      read(':');
    }
    String name = readString(delim);
    prefix=getPrefix(name);
    if (prefix == null) prefix = "";
    String localName = getName(name);
    return new QName(uri,localName,prefix);
  }

  public List readAttributes() 
    throws XMLStreamException, IOException
  {
    List attributes = new ArrayList();
    while(getChar() == '[') {
      attributes.add(readAttribute());
    }
    return attributes;
  }

  public Attribute readAttribute()   
    throws XMLStreamException, IOException
  {
    read('[');
    read('[');
    String type=readString(']');
    read(']');
    QName n = readName();
    read("=[");
    String value=readString(']');
    read(']');
    read(']');
    if (type.equals("ATTRIBUTE"))
      return new AttributeBase(n,value);
    if (type.equals("DEFAULT"))
      return new NamespaceBase(value);
    if (type.equals("NAMESPACE"))
      return (new NamespaceBase(n.getLocalPart(),
                              value));
    throw new XMLStreamException("Parser Error expected (ATTRIBUTE|"+
                                 "|DEFAULT|NAMESPACE");
  }

  public EventState readEntityReference ()   
    throws XMLStreamException, IOException
  {    
    EventState state = 
      new EventState(XMLEvent.ENTITY_REFERENCE);
    read('[');
    state.setData(readString(']'));
    read(']');
    return state;
  }
  public EventState readSpace()   
    throws XMLStreamException, IOException
  {
    EventState state = 
      new EventState(XMLEvent.SPACE);
    read('[');
    String content = readString(']');
    read(']');
    state.setData(content);
    return state;
  }

  public EventState readElement()
    throws XMLStreamException, IOException
  {
    int type = readType();
    EventState state;
    switch(type) {
    case XMLEvent.START_ELEMENT: 
      state=readStartElement();break;
    case XMLEvent.END_ELEMENT: 
      state=readEndElement();break; 
    case XMLEvent.PROCESSING_INSTRUCTION:
      state=readProcessingInstruction();break;
    case XMLEvent.CHARACTERS:
      state=readCharacterData();break;
    case XMLEvent.COMMENT:
      state=readComment();break;
    case XMLEvent.START_DOCUMENT:
      state=readStartDocument();break;
    case XMLEvent.END_DOCUMENT:
      readEndDocument = true;
      state=readEndDocument();break;
    case XMLEvent.ENTITY_REFERENCE:
      state=readEntityReference() ;break;
    case XMLEvent.SPACE:
      state=readSpace();break;
    case XMLEvent.DTD:
      state=readDTD();break;
    case XMLEvent.CDATA:
      state=readCDATA();break;
    default:
      throw new XMLStreamException("Attempt to read unknown element ["+type+"]");
    }
    read(';');
    skipSpace();
    return state;
  }

  public boolean endDocumentIsPresent() {
    return readEndDocument;
  }
  
  public boolean hasNext() throws IOException
  {
    return (reader.ready() && !readEndDocument);
  }

  public static void main(String args[]) 
    throws Exception
  {
    EventScanner reader = new EventScanner(new FileReader(args[0]));
    while(reader.hasNext())
      System.out.println(reader.readElement());
    
  }
}

