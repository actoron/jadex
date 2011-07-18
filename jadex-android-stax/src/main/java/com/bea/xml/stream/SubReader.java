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

import com.bea.xml.stream.util.EmptyIterator;
import com.bea.xml.stream.util.ElementTypeNames;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.XMLEvent;

import java.util.List;

/**
 * <p> Creates a SubReader over a node of a document </p>
 */

public class SubReader extends ReaderDelegate {
  private int depth=0;
  private boolean open=true;
  public SubReader(XMLStreamReader reader) 
    throws XMLStreamException
  {
    super(reader);
    if (!reader.isStartElement())
      throw new XMLStreamException("Unable to instantiate a subReader "+
                                   "because the underlying reader "+
                                   "was not on a start element.");
    open = true;
    depth++;
  }

  public int next() 
    throws XMLStreamException 
  {
    if (depth <= 0) open = false;
    int type = super.next();
    if (isStartElement()) depth++;
    if (isEndElement()) {
      depth--;
    }
    return type;
  }

  public int nextElement() 
    throws XMLStreamException 
  {
    next();
    while (hasNext() && !isStartElement() && !isEndElement()) next(); 
    return super.getEventType();
  }

  public boolean hasNext() 
    throws XMLStreamException
  {
    if (!open) return false;
    return super.hasNext();
  }

  public boolean moveToStartElement() 
    throws XMLStreamException
  {
    if (isStartElement()) return true;
    while(hasNext()) {
      if (isStartElement()) return true;
      else
        next();
    }
    return false;
  }

  public boolean moveToStartElement(String localName) 
    throws XMLStreamException
  {
    if (localName == null) return false;
    while( moveToStartElement() ) {
      if (localName.equals(getLocalName())) return true;
      if (!hasNext()) return false;
      next();
    }
    return false;
  }

  public boolean moveToStartElement(String localName, String namespaceUri) 
    throws XMLStreamException
  {
    if (localName == null || namespaceUri == null) return false;
    while(moveToStartElement(localName)) {
      if(namespaceUri.equals(getNamespaceURI())) return true;
      if (!hasNext()) return false;
      next();
    }
    return false;
  }

  public boolean moveToEndElement() 
    throws XMLStreamException
  {
    if (isEndElement()) return true;
    while (hasNext()) {
      if (isEndElement()) return true;
      else
        next();
    }
    return false;
  }

  public boolean moveToEndElement(String localName) 
    throws XMLStreamException
  {
    if (localName == null) return false;
    while( moveToEndElement() ) {
      if (localName.equals(getLocalName())) return true;
      if (!hasNext()) return false;
      next();
    }
    return false;
  }

  public boolean moveToEndElement(String localName, String namespaceUri) 
    throws XMLStreamException
  {
    if (localName == null || namespaceUri == null) return false;
    while(moveToEndElement(localName)) {
      if(namespaceUri.equals(getNamespaceURI())) return true;
      if (!hasNext()) return false;
      next();
    }
    return false;
  }
  
  public static void print(XMLStreamReader r, int depth) throws XMLStreamException {
    System.out.print("["+depth+"]Sub: "+ElementTypeNames.getEventTypeString(r.getEventType()));
    if(r.hasName()) System.out.println("->"+r.getLocalName());
    else if(r.hasText()) System.out.println("->["+r.getText()+"]");
    else System.out.println();
  }

  public static void sub(XMLStreamReader r, int depth) throws Exception {
    while (r.hasNext()) {
      print(r,depth);
      r.next();
    }
  } 

  public static void main(String args[]) throws Exception {
    MXParser r = new MXParser();
    r.setInput(new java.io.FileReader(args[0]));
    r.moveToStartElement(); r.next();
    while(r.moveToStartElement()) {
      System.out.println("SE->"+r.getName());
      XMLStreamReader subr = r.subReader();
      sub(subr,1);
    }
  }
}







