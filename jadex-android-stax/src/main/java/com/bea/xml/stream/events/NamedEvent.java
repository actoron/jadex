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

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLStreamException;

public abstract class NamedEvent extends BaseEvent {
  private QName name;

  public NamedEvent() {}

  public NamedEvent(QName name) { 
    this.name = name;
  }
  public NamedEvent(String localName) {
    name = new QName(localName);
  }
  public NamedEvent(String prefix,
                    String namespaceURI,
                    String localName) {
    name = new QName(namespaceURI,localName,prefix);
  }
  public QName getName() {
    return name; 
  }
  public void setName(QName n) {
    name = n;
  }
  public String nameAsString() {

    if ("".equals(name.getNamespaceURI()))
      return name.getLocalPart();
    else if (name.getPrefix() != null && 
             !name.getPrefix().equals(""))
      return "['"+name.getNamespaceURI()+"']:"+
        name.getPrefix()+":"+
        name.getLocalPart();
    else 
      return "['"+name.getNamespaceURI()+"']:"+name.getLocalPart();
  }

  protected abstract void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException, XMLStreamException;
}

