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

package com.bea.xml.stream.util;

import javaxx.xml.stream.events.XMLEvent;

public class ElementTypeNames {
  public final static String getEventTypeString(int eventType) {
    switch (eventType){
    case XMLEvent.START_ELEMENT:
      return "START_ELEMENT";
    case XMLEvent.END_ELEMENT:
      return "END_ELEMENT";
    case XMLEvent.PROCESSING_INSTRUCTION:
      return "PROCESSING_INSTRUCTION";
    case XMLEvent.CHARACTERS:
      return "CHARACTERS";
   case XMLEvent.SPACE:
      return "SPACE";
     case XMLEvent.COMMENT:
      return "COMMENT";
    case XMLEvent.START_DOCUMENT:
      return "START_DOCUMENT";
    case XMLEvent.END_DOCUMENT:
      return "END_DOCUMENT";
    case XMLEvent.ENTITY_REFERENCE:
      return "ENTITY_REFERENCE";
    case XMLEvent.ATTRIBUTE:
      return "ATTRIBUTE";
    case XMLEvent.DTD:
      return "DTD";
    case XMLEvent.CDATA:
      return "CDATA";
    case XMLEvent.NAMESPACE:
      return "NAMESPACE";
    }
    return "UNKNOWN_EVENT_TYPE";
  }

  public static int getEventType(String val) {
    if (val.equals ("START_ELEMENT")) 
      return XMLEvent.START_ELEMENT;
    if (val.equals ("SPACE")) 
      return XMLEvent.SPACE; 
    if (val.equals ("END_ELEMENT")) 
      return XMLEvent.END_ELEMENT;
    if (val.equals ("PROCESSING_INSTRUCTION"))
      return XMLEvent.PROCESSING_INSTRUCTION; 
    if (val.equals ("CHARACTERS"))
      return XMLEvent.CHARACTERS; 
    if (val.equals ("COMMENT"))
      return XMLEvent.COMMENT; 
    if (val.equals ("START_DOCUMENT"))
      return XMLEvent.START_DOCUMENT; 
    if (val.equals ("END_DOCUMENT"))
      return XMLEvent.END_DOCUMENT; 
    if (val.equals("ATTRIBUTE")) 
      return XMLEvent.ATTRIBUTE;
    if (val.equals("DTD"))
      return XMLEvent.DTD;
    if (val.equals("CDATA"))
      return XMLEvent.CDATA;
    if (val.equals("NAMESPACE"))
      return XMLEvent.NAMESPACE;
    return -1;
  }
}
