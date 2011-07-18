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

import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.XMLEvent;

/**
 * <p> Create events from a file format.</p>
 */

public class XMLEventPlayer
  extends XMLEventReaderBase
{
  private XMLStreamPlayer player;
  public XMLEventPlayer(XMLStreamPlayer reader) 
    throws XMLStreamException
  {
    super(reader);
    player = reader;
  }

  protected boolean parseSome() 
    throws XMLStreamException
  {
    allocator.allocate(reader,this);
    if (reader.hasNext())
      reader.next();
    if (isOpen() && reader.getEventType() == XMLEvent.END_DOCUMENT) {
      if (player.endDocumentIsPresent())
        allocator.allocate(reader,this);
      internal_close();
    }
    return !needsMore();
  }
}



