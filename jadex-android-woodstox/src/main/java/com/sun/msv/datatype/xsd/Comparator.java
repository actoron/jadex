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
 * Comparable datatype.
 *
 * Those datatypes which has order relation must implement this interface.
 * RangeFacet uses this interface to do its job.
 * It differs from {@link java.util.Comparator} in return value.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface Comparator {
    static final int LESS            = -1;    // lhs < rhs
    static final int EQUAL            = 0;    // lhs = rhs
    static final int GREATER        = 1;    // lhs > rhs
    static final int UNDECIDABLE    = 999;    // lhs ? rhs
    
    /**
     * compare to value types and decides its order relation
     */
    int compare( Object o1, Object o2 );
}
