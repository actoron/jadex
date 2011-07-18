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

import com.bea.xml.stream.util.EmptyIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.XMLEvent;

public class EndElementEvent 
  extends NamedEvent 
  implements EndElement 
{
  private List outOfScopeNamespaces;

  public EndElementEvent() {
    super();
    init();
  }
  public EndElementEvent(QName name) { 
    super(name);
    init();
  }
  protected void init() { 
    setEventType(XMLEvent.END_ELEMENT); 
  } 
  public Iterator getNamespaces() {
    if (outOfScopeNamespaces==null)
      return EmptyIterator.emptyIterator;
    return outOfScopeNamespaces.iterator();
  }
  public void addNamespace(Namespace n) {
    if (outOfScopeNamespaces == null) 
      outOfScopeNamespaces = new ArrayList();
    outOfScopeNamespaces.add(n);
  }
  public void reset() {
    if (outOfScopeNamespaces != null) outOfScopeNamespaces.clear();

  }

  public String toString() { 
    String value = "</"+nameAsString();
    Iterator ni = getNamespaces();
    while(ni.hasNext())
      value = value +" "+ ni.next().toString();
    value = value+">";
    return value;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write("</");
      QName name = getName();
      String prefix = name.getPrefix();
      if (prefix != null && prefix.length() > 0) {
          writer.write(prefix);
          writer.write(':');
      }
      writer.write(name.getLocalPart());
      writer.write('>');
  }
}

