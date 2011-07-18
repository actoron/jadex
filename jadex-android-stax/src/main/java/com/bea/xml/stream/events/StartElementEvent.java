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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.StartElement;
import javaxx.xml.stream.events.XMLEvent;

public class StartElementEvent 
  extends NamedEvent
  implements StartElement 
{
  private List attributes;
  private List namespaces;
  private NamespaceContext context;

  public StartElementEvent() { super();}

  public StartElementEvent(QName name) {
    super(name);
    init();
  }

  public void reset() {
    if (attributes != null) attributes.clear();
    if (namespaces != null) namespaces.clear();
    if (context != null) context = null;
  }
  public StartElementEvent(StartElement element) {
    super(element.getName());
    init();
    setName(element.getName());

    Iterator ai = element.getAttributes();
    while(ai.hasNext()) 
      addAttribute((Attribute) ai.next());

    Iterator ni = element.getNamespaces();
    ni = element.getNamespaces();
    while(ni.hasNext())
      addNamespace((Namespace) ni.next());
  }
  protected void init() {setEventType(XMLEvent.START_ELEMENT); }
  public Iterator getAttributes() { 
    if (attributes == null) return EmptyIterator.emptyIterator;
    return attributes.iterator(); 
  }
  public Iterator getNamespaces() { 
    if (namespaces == null) return EmptyIterator.emptyIterator;
    return namespaces.iterator(); 
  }

  public Attribute getAttributeByName(QName name) {
    if (name == null) return null;
    Iterator i = getAttributes();
    while (i.hasNext()) {
      Attribute a = (Attribute) i.next();
      if (a.getName().equals(name))
        return a;
    }
    return null;
  }
  public void setAttributes(List attributes) {
    this.attributes = attributes;
  }
  public void addAttribute(Attribute attribute) {
    if (attributes == null)
      attributes = new ArrayList();
    attributes.add(attribute);
  }
  public void addNamespace(Namespace attribute) {
    if (namespaces == null)
      namespaces = new ArrayList();
    namespaces.add(attribute);
  }
  public String getNamespaceURI(String prefix) {
    if (context == null) return null;
    return (String) context.getNamespaceURI(prefix);
  }

  public void setNamespaceContext(NamespaceContext c) {
    this.context = c;
  }

  public NamespaceContext getNamespaceContext() {
    return context;
  }
  public String toString() {
    String value = "<"+nameAsString();
    Iterator ai = getAttributes();
    while (ai.hasNext()) 
      value = value +" "+ ai.next().toString();
    Iterator ni = getNamespaces();
    while (ni.hasNext()) 
      value = value +" "+ ni.next().toString();

    value = value + ">";
    return value;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException, XMLStreamException
  {
      writer.write('<');
      QName name = getName();
      String prefix = name.getPrefix();
      if (prefix != null && prefix.length() > 0) {
          writer.write(prefix);
          writer.write(':');
      }
      writer.write(name.getLocalPart());

      // Any namespace declarations?
      Iterator ni = getNamespaces();
      while (ni.hasNext()) {
          writer.write(' ');
          // Ouch: neither ns nor attr are based on BaseEvent... doh!
          XMLEvent evt = (XMLEvent) ni.next();
          evt.writeAsEncodedUnicode(writer);
      }

      // Any attributes?
      Iterator ai = getAttributes();
      while (ai.hasNext()) {
          writer.write(' ');
          XMLEvent evt = (XMLEvent) ai.next();
          evt.writeAsEncodedUnicode(writer);
      }

      writer.write('>');
  }
}
