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

import java.io.IOException;
import java.io.Writer;

import javaxx.xml.stream.events.Characters;
import javaxx.xml.stream.events.XMLEvent;

public class CharactersEvent 
  extends BaseEvent
  implements Characters
{
  private String data;
  private boolean isCData=false;
  private boolean isSpace=false;
  private boolean isIgnorable=false;
  public CharactersEvent() {
    super();
    init();
  }
  public CharactersEvent(String data) {
    super();
    init();
    setData(data);
  }
  public CharactersEvent(String data, boolean isCData) {
    super();
    init();
    setData(data);
    this.isCData = isCData;
  }
  public void setSpace(boolean space) {
    isSpace = space;
  }
  public boolean isWhiteSpace() {
    return isSpace;
  }
  public boolean isIgnorableWhiteSpace() {
    return isIgnorable;
  }
  public void setIgnorable(boolean ignorable) {
    isIgnorable = ignorable;
  }
  protected void init() {setEventType(XMLEvent.CHARACTERS); }
  public String getData() { return data; }
  public void setData(String data) { this.data = data; }
  public boolean hasData() {return data != null;}
  public boolean isCData() { return isCData; }
  public char[] getDataAsArray() {
    return data.toCharArray();
  }

  protected void doWriteAsEncodedUnicode(Writer writer) 
      throws IOException
  {
      if (isCData) {
          writer.write("<![CDATA[");
          writer.write(getData());
          writer.write("]]>");
      } else {
          String data = getData();
          int len = data.length();

          if (len > 0) {
              int i = 0;
              
              // Let's see how much we can output without encoding:
              loop:
              for (; i < len; ++i) {
                  switch (data.charAt(i)) {
                  case '&':
                  case '<':
                  case '>': // only mandatory as part of ']]>' but let's play it safe
                      break loop;
                  }
              }
              // got it all?
              if (i == len) {
                  writer.write(data);
              } else { // nope...
                  if (i > 0) {
                      writer.write(data, 0, i);
                  }
                  for (; i < len; ++i) {
                      final char c = data.charAt(i);
                      switch (c) {
                      case '&':
                          writer.write("&amp;");
                          break;
                      case '<':
                          writer.write("&lt;");
                          break;
                      case '>':
                          writer.write("&gt;");
                          break;
                      default:
                          writer.write(c);
                      }
                  }
              }
          }
      }
  }
}
