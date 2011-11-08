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
 * "length", "minLength", and "maxLength" facet validator.
 * 
 * this class also detects inconsistent facet setting
 * (for example, minLength=100 and maxLength=0)
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LengthFacet extends DataTypeWithValueConstraintFacet {
    public final int length;
    
    protected LengthFacet( String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
        throws DatatypeException {
        this(nsUri,typeName,baseType,
            facets.getNonNegativeInteger(FACET_LENGTH),
            facets.isFixed(FACET_LENGTH));
    }
                
    protected LengthFacet( String nsUri, String typeName, XSDatatypeImpl baseType, int _length, boolean _isFixed )
        throws DatatypeException {
        super(nsUri,typeName,baseType,FACET_LENGTH,_isFixed);
    
        length = _length;
        
        // loosened facet check
        DataTypeWithFacet o = baseType.getFacetObject(FACET_LENGTH);
        if(o!=null && ((LengthFacet)o).length != this.length )
            throw new DatatypeException(
                localize(ERR_LOOSENED_FACET, FACET_LENGTH, o.displayName() ) );
        
        // consistency with minLength/maxLength is checked in XSDatatypeImpl.derive method.
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        Object o = baseType._createValue(content,context);
        if(o==null || ((Discrete)concreteType).countLength(o)!=length)    return null;
        return o;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        Object o = concreteType._createValue(content,context);
        // base type must have accepted this lexical value, otherwise 
        // this method is never called.
        if(o==null)    throw new IllegalStateException();    // assertion
        
        int cnt = ((Discrete)concreteType).countLength(o);
        if(cnt!=length)
            throw new DatatypeException( DatatypeException.UNKNOWN,
                localize(ERR_LENGTH, new Integer(cnt), new Integer(length)) );
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
