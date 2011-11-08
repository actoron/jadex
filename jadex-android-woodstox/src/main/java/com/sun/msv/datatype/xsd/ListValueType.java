/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;


/**
 * value object of ListType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ListValueType implements java.io.Serializable
{
    public final Object[] values;
    
    public ListValueType( Object[] values ) { this.values=values; }
    
    /**
     * Two ListValueType are equal if and only if all the array members
     * are equal respectively.
     */
    public boolean equals( Object o ) {
        if(o==null || !(o instanceof ListValueType))     return false;

        ListValueType rhs = (ListValueType)o;
        final int len = values.length;
        if( len!=rhs.values.length )    return false;
        for( int i=0; i<len; i++ )
            if(!values[i].equals(rhs.values[i]))    return false;
        
        return true;
    }
    
    public int hashCode() {
        int h=1;
        final int len = values.length;
        for( int i=0; i<len; i++ )
            h += values[i].hashCode();
        
        return h;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
