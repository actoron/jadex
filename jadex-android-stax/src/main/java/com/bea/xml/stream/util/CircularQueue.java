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
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class CircularQueue extends AbstractCollection {

  // This is the largest capacity allowed by this implementation
  private static final int MAX_CAPACITY = 1 << 30;
  private static final int DEFAULT_CAPACITY = 1 << 8;

  private int size          = 0;
  private int producerIndex = 0;
  private int consumerIndex = 0;

  // capacity must be a power of 2 at all times
  private int capacity;
  private int maxCapacity;

  // we mask with capacity -1.  This variable caches that values
  private int bitmask; 

  private Object[] q;

  public CircularQueue() {
    this(DEFAULT_CAPACITY);
  }

  // Construct a queue which has at least the specified capacity.  If
  // the value specified is a power of two then the queue will be
  // exactly the specified size.  Otherwise the queue will be the
  // first power of two which is greater than the specified value.
  public CircularQueue(int c) {
    this(c, MAX_CAPACITY);
  }

  public CircularQueue(int c, int mc) {
    if (c > mc) {
      throw new IllegalArgumentException("Capacity greater than maximum");
    }

    if (mc > MAX_CAPACITY) {
      throw new IllegalArgumentException("Maximum capacity greater than " +
        "allowed");
    }

    for (capacity = 1; capacity < c; capacity <<= 1) ;
    for (maxCapacity = 1; maxCapacity < mc; maxCapacity <<= 1) ;

    bitmask = capacity - 1;
    q = new Object[capacity];
  }

  // Constructor used by clone()
  private CircularQueue(CircularQueue oldQueue) {
    size = oldQueue.size;
    producerIndex = oldQueue.producerIndex;
    consumerIndex = oldQueue.consumerIndex;
    capacity = oldQueue.capacity;
    maxCapacity = oldQueue.maxCapacity;
    bitmask = oldQueue.bitmask;
    q = new Object[oldQueue.q.length];
    System.arraycopy(oldQueue.q, 0, q, 0, q.length);
  }

  private boolean expandQueue() {
    // double the size of the queue
    // This design assumes that this is a rare case

    if (capacity == maxCapacity) {
      return false;
    }

    int old_capacity = capacity;
    Object[] old_q    = q;

    capacity += capacity;
    bitmask   = capacity - 1;
    q         = new Object[capacity];

    System.arraycopy(old_q, consumerIndex, q, 0, old_capacity - consumerIndex);

    if (consumerIndex != 0) {
      System.arraycopy(old_q, 0, q, old_capacity - consumerIndex, 
        consumerIndex);
    }

    consumerIndex = 0;
    producerIndex = size;

    return true;
  }

  public boolean add(Object obj) {
    if (size == capacity) {
      // no room
      if (!expandQueue()) return false;
    }

    size++;
    q[producerIndex] = obj;

    producerIndex = (producerIndex + 1) & bitmask;

    return true;
  }

  public Object remove() {
    Object obj;
    
    if (size == 0) return null;
    
    size--;
    obj = q[consumerIndex];
    q[consumerIndex] = null; // allow gc to collect
    
    consumerIndex = (consumerIndex + 1) & bitmask;

    return obj;
  }

  public boolean isEmpty() { return size == 0; }

  public int size() { return size; }

  public int capacity() { return capacity; }

  public Object peek() {
    if (size == 0) return null;
    return q[consumerIndex];
  }

  public void clear() {
    Arrays.fill(q, null);
    size = 0;
    producerIndex = 0;
    consumerIndex = 0;
  }

  public Object clone() {
    return new CircularQueue(this);
  }

  public String toString() {
    StringBuffer s = new StringBuffer(super.toString() + " - capacity: '" +
      capacity() + "' size: '" + size() + "'");

    if (size > 0) {
      s.append(" elements:");
      for (int i = 0; i < size; ++i) {
        s.append('\n');
        s.append('\t');
        s.append(q[consumerIndex + i & bitmask].toString());
      }      
    }

    return s.toString();
  }

  public Iterator iterator() {
    return new Iterator() {
      private final int ci = consumerIndex;
      private final int pi = producerIndex;
      private int s = size;
      private int i = ci;

      public boolean hasNext() {
        checkForModification();
        return s > 0;
      }

      public Object next() {
        checkForModification();
        if (s == 0) throw new NoSuchElementException();
    
        s--;
        Object r = q[i];
        i = (i + 1) & bitmask;

        return r;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      private void checkForModification() {
        if (ci != consumerIndex) throw new ConcurrentModificationException();
        if (pi != producerIndex) throw new ConcurrentModificationException();
      }
    };
  }
}
