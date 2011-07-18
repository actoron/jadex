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

package com.bea.xml.stream.filters;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.StreamFilter;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.StartElement;
import javaxx.xml.stream.events.XMLEvent;

public class NameFilter implements EventFilter, StreamFilter {
  private QName name;
  
  public NameFilter(QName name) 
  { 
    this.name = name;
  }

  public boolean accept(XMLEvent e) {
    if (!e.isStartElement() && !e.isEndElement()) return false;
    QName eName = null;
    if (e.isStartElement())
      eName = ((StartElement)e).getName();
    else
      eName = ((EndElement)e).getName();
    if (name.equals(eName))
      return true;
    return false;
  }

  public boolean accept(XMLStreamReader r) {
    if (!r.isStartElement() && !r.isEndElement()) return false;
    QName eName = new QName(r.getNamespaceURI(),
                            r.getLocalName());
    if (name.equals(eName))
      return true;
    return false;
  }
}


