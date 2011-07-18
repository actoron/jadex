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

import javaxx.xml.stream.EventFilter;
import javaxx.xml.stream.StreamFilter;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.XMLEvent;

public class TypeFilter implements EventFilter, StreamFilter {

  protected boolean[] types= new boolean[20];

  public TypeFilter (){}
  public void addType(int type) {
    types[type]=true;
  }

  public boolean accept(XMLEvent e) {
    return types[e.getEventType()];
  }

  public boolean accept(XMLStreamReader r) {
    return types[r.getEventType()];
  }
}


