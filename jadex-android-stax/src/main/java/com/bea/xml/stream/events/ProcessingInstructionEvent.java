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
import javaxx.xml.stream.events.ProcessingInstruction;
import javaxx.xml.stream.events.XMLEvent;
public class ProcessingInstructionEvent 
  extends BaseEvent
  implements ProcessingInstruction 
{
  String name;
  String content;
  public ProcessingInstructionEvent() {super();init();}
  public ProcessingInstructionEvent(String name, String content) {
    super(); init();
    this.name = name;
    this.content=content;
  }
  protected void init() {setEventType(XMLEvent.PROCESSING_INSTRUCTION); }
  public String getTarget() {
    return name;
  }
  public void setTarget(String target) {
    name= target;
  }
  public void setData(String data) {
    this.content = data;
  }
  public String getData() {
    return content;
  }

  protected void doWriteAsEncodedUnicode(java.io.Writer writer) 
      throws java.io.IOException
  {
      writer.write("<?");
      if (name != null) {
          writer.write(name);
      }
      if (content != null) {
          writer.write(' ');
          writer.write(content);
      }
      writer.write("?>");
  }
}
