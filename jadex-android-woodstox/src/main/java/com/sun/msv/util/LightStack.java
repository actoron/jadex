/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util;

/**
 * light-weight stack implementation.
 * 
 * This one is unsynchronized, and never shrink its memory footprint, but fast.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class LightStack {
    
    private Object[] buf = new Object[8];
    private int len = 0;
    
    public void push( Object o ) {
        try {
            buf[len] = o;
            len++;
        } catch( ArrayIndexOutOfBoundsException e ) {
            Object[] nbuf = new Object[buf.length*2];
            System.arraycopy( buf, 0, nbuf, 0, buf.length );
            buf = nbuf;
            buf[len++] = o;
        }
    }
    
    public Object pop() {
        return buf[--len];
    }
    
    public Object top() {
        return buf[len-1];
    }
    
    public int size() {
        return len;
    }
    
    public boolean contains( Object o ) {
        for( int i=0; i<len; i++ )
            if( buf[i]==o )
                return true;
        return false;
    }
}
