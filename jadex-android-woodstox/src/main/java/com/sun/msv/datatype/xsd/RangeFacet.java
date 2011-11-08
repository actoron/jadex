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
import org.relaxng.datatype.ValidationContext;

/**
 * Base class of "(max|min)(In|Ex)clusive" facet validator
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class RangeFacet extends DataTypeWithValueConstraintFacet {
    public final Object limitValue;

    protected RangeFacet( String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, Object limit, boolean _isFixed )
        throws DatatypeException {
        super(nsUri,typeName,baseType,facetName,_isFixed);
        limitValue = limit;
    }
    
    public final Object _createValue( String literal, ValidationContext context ) {
        Object o = baseType._createValue(literal,context);
        if(o==null)    return null;
        
        int r = ((Comparator)concreteType).compare(limitValue,o);
        if(!rangeCheck(r))        return null;
        return o;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if( _createValue(content,context)!=null )        return;
            
        throw new DatatypeException( DatatypeException.UNKNOWN,
            localize(ERR_OUT_OF_RANGE, facetName, limitValue) );
    }
    
    protected abstract boolean rangeCheck( int compareResult );

    // serialization support
    private static final long serialVersionUID = 1;    
}
