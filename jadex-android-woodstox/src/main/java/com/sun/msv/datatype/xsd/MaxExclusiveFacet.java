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

import org.relaxng.datatype.DatatypeException;

/**
 * 'maxExclusive' facet
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MaxExclusiveFacet extends RangeFacet {
    protected MaxExclusiveFacet( String nsUri, String typeName, XSDatatypeImpl baseType, Object limit, boolean _isFixed )
        throws DatatypeException {
        super( nsUri, typeName, baseType, FACET_MAXEXCLUSIVE, limit, _isFixed );
    }
    
    protected final boolean rangeCheck( int r ) {
        return r==Comparator.GREATER;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
