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

import java.util.AbstractCollection;
import java.util.EmptyStackException;
import java.util.Iterator;

public final class Stack extends AbstractCollection {
  private Object[] values;
  private int pointer;

  public Stack() {
    this(15);
  }

  public Stack(int size) {
    if (size < 0) throw new IllegalArgumentException();
    values = new Object[size];
    pointer = 0;
  }

  private Stack(Object[] values, int pointer) {
    this.values = values;
    this.pointer = pointer;
  }

  private void resize() {
    if (pointer == 0) {
      values = new Object[1];
      return;
    }
    Object[] o = new Object[pointer * 2];
    System.arraycopy(values, 0, o, 0, pointer);
    values = o;
  }

  public boolean add(Object o) {
    push(o);
    return true;
  }

  public void clear() {
    Object[] v = values;
    while (pointer > 0 ) {
      v[--pointer] = null;
    }
  }

  public boolean isEmpty() { return pointer == 0; }

  public Iterator iterator() {
    Object[] o = new Object[pointer];
    System.arraycopy(values, 0, o, 0, pointer);
    return new ArrayIterator(o);
  }

  public Object clone() {
    Object[] newValues = new Object[pointer];
    System.arraycopy(values, 0, newValues, 0, pointer);
    return new Stack(newValues, pointer); 
  }

  public int size() { return pointer; }

  public void push(Object o) {
    if (pointer == values.length) resize();
    values[pointer++] = o;
  }

  public Object pop() {
    try {
      Object o = values[--pointer];
      values[pointer] = null;
      return o;
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      // Make sure the stack continues to be useable even if we popped
      // too many.
      if (pointer < 0) pointer = 0;
      throw new EmptyStackException();
    }
  }

  public Object peek() {
    try {
      return values[pointer - 1];
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      throw new EmptyStackException();
    }
  }
}
