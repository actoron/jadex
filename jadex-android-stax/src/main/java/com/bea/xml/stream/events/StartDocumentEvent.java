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
import javaxx.xml.stream.events.StartDocument;
import javaxx.xml.stream.events.XMLEvent;
public class StartDocumentEvent 
  extends BaseEvent 
  implements StartDocument 
{
  protected String systemId="";
  protected String publicId="";
  protected String encodingScheme="UTF-8";
  protected boolean standalone=false;
  protected String version="1.0";
  private boolean encodingSchemeSet = false;
  private boolean standaloneSet = false;

  public StartDocumentEvent(){super();init();}
  protected void init() {setEventType(XMLEvent.START_DOCUMENT); }
  public String getSystemId() { return systemId; }
  //  public String getPublicId() { return publicId; }
  public String getCharacterEncodingScheme() { return encodingScheme; }
  public boolean isStandalone(){ return standalone; }
  public String getVersion() { return version; }
  public void setStandalone(boolean standalone) {
    standaloneSet = true;
    this.standalone = standalone;
  }
  public void setStandalone(String standalone) {
    standaloneSet = true;
    if (standalone == null) {this.standalone = true; return;}
    if (standalone.equals("yes")) this.standalone = true;
    else
      this.standalone = false;
  }
  public boolean encodingSet() { return encodingSchemeSet; }
  public boolean standaloneSet() { return standaloneSet; }
  public void setEncoding(String encoding) {
    encodingScheme = encoding;
    encodingSchemeSet = true;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public void clear() {
    encodingScheme = "UTF-8";
    standalone = true;
    version = "1.0";
    encodingSchemeSet = false;
    standaloneSet=false;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write("<?xml version=\"");
      writer.write(version);
      writer.write("\" encoding='");
      writer.write(encodingScheme);
      writer.write('\'');
      if (standaloneSet) {
          writer.write(" standalone='");
          writer.write(standalone ? "yes'" : "no'");
      }
      writer.write("?>");
  }
}



