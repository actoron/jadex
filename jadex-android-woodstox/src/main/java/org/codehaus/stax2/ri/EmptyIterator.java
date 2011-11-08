package org.codehaus.stax2.ri;

import java.util.Iterator;

/**
 * Simple implementation of "null iterator", iterator that has nothing to
 * iterate over.
 */
public final class EmptyIterator
    implements Iterator
{
    final static EmptyIterator sInstance = new EmptyIterator();
    
    private EmptyIterator() { }
    
    public static EmptyIterator getInstance() { return sInstance; }
    
    public boolean hasNext() { return false; }
    
    public Object next() {
        throw new java.util.NoSuchElementException();
    }
    
    public void remove()
    {
        /* The reason we do this is that we know for a fact that
         * it can not have been moved
         */
        throw new IllegalStateException();
    }
}
