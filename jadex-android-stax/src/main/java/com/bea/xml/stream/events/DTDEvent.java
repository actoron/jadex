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

import java.util.List;

import javaxx.xml.stream.XMLEventReader;
import javaxx.xml.stream.events.DTD;
import javaxx.xml.stream.events.EntityDeclaration;
import javaxx.xml.stream.events.NotationDeclaration;
import javaxx.xml.stream.events.XMLEvent;

import com.wutka.dtd.DTDEntity;
import com.wutka.dtd.DTDExternalID;
import com.wutka.dtd.DTDNotation;
import com.wutka.dtd.DTDPublic;
import com.wutka.dtd.DTDSystem;

public class DTDEvent 
  extends BaseEvent
  implements DTD
{
  private String dtd;

  private List notations;
  private List entities;

  public DTDEvent() { init(); }

  public DTDEvent(String dtd) {
    init();
    setDTD(dtd);
  }

  protected void init() {setEventType(XMLEvent.DTD); }

  public static EntityDeclaration createEntityDeclaration(DTDEntity dtdEntity)
  {
      return new EntityDeclarationEvent(dtdEntity.getName(), dtdEntity.getValue());
  }

  public static NotationDeclaration createNotationDeclaration(DTDNotation dtdNotation)
  {
      DTDExternalID extId = dtdNotation.getExternalID();
      String systemId = extId.getSystem();
      String publicId = (extId instanceof DTDPublic) ?
          ((DTDPublic) extId).getPub() : null;

      return new NotationDeclarationEvent(dtdNotation.getName(), publicId, systemId);
  }
                                                               
  public void setDTD(String dtd) {
    this.dtd=dtd;
  }

  public void setNotations(List l) {
      notations = l;
  }

  public void setEntities(List l) {
      entities = l;
  }

  public Object getProcessedDTD() {
    return null;
  }
  public String getDocumentTypeDeclaration() {
    return dtd;
  }
  public List getEntities() {
    return entities;
  }
  public List getNotations() {
    return notations;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write("<!DOCTYPE ");
      // !!! TBI: Should get the root element name here...
      if (dtd != null && dtd.length() > 0) {
          writer.write('[');
          writer.write(dtd);
          writer.write(']');
      }
      writer.write('>');
  }
}
