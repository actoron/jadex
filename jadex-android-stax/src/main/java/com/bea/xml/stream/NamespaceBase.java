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
import javaxx.xml.stream.events.XMLEvent;

/**
 * <p> The default implementation of the namespace class </p>
 */

public class NamespaceBase extends AttributeBase 
  implements javaxx.xml.stream.events.Namespace
{
  boolean declaresDefaultNamespace=false;
  public NamespaceBase(String prefix,
                       String namespaceURI) 
  {
    super("xmlns",
          prefix,
          namespaceURI);
    declaresDefaultNamespace=false;
  }

  public NamespaceBase(String namespaceURI)
  {
    super("xmlns",
          "",
          namespaceURI);
    declaresDefaultNamespace=true;
  }

  public int getEventType() { return XMLEvent.NAMESPACE; }
  public boolean isAttribute() { return false; }
  public boolean isNamespace() { return true; }
  public String getPrefix() {
    if (declaresDefaultNamespace) return "";
    return super.getLocalName();
  }
  public String getNamespaceURI() {
    return super.getValue();
  }
  public boolean isDefaultNamespaceDeclaration() {
    return declaresDefaultNamespace;
  }
  public String toString() {
    if (declaresDefaultNamespace) 
      return "xmlns='"+getNamespaceURI()+"'";
    else
      return "xmlns:"+getPrefix()+"='"+getNamespaceURI()+"'";
  }

}
