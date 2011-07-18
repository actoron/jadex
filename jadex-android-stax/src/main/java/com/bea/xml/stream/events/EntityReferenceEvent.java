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

import javaxx.xml.stream.events.EntityDeclaration;
import javaxx.xml.stream.events.EntityReference;
import javaxx.xml.stream.events.XMLEvent;
public class EntityReferenceEvent 
  extends BaseEvent 
  implements EntityReference
{
  private String name;
  private String replacementText;
  private EntityDeclaration ed;
  public EntityReferenceEvent() {super();init();}
  public EntityReferenceEvent(String name,
                              EntityDeclaration ed) {
    super();
    init();
    this.name = name;
    this.ed = ed;
  }
  public String getReplacementText() {
    return ed.getReplacementText();
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setReplacementText(String text) {
    this.replacementText = text;
  }
  public String getBaseURI() {
    return null;
  }
  public String getPublicId() {
    return null;
  }
  public String getSystemId() {
    return null;
  }
  public EntityDeclaration getDeclaration() {
    return ed;
  }
  protected void init() {setEventType(XMLEvent.ENTITY_REFERENCE); }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write('&');
      writer.write(getName());
      writer.write(';');
  }

  /**
   * toString() overridden to output more information than what the
   * default implementation from base event class outputs.
   */
  public String toString() {
    String replacement = getReplacementText();
    if (replacement == null) replacement="";
    return "&"+
      getName()+
      ":='"+
      replacement+
      "'"; 
  }
}
