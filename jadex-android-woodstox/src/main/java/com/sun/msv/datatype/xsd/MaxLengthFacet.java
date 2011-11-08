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
 * 'maxLength' facet
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MaxLengthFacet extends DataTypeWithValueConstraintFacet
{
    public final int maxLength;

    protected MaxLengthFacet( String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
        throws DatatypeException {
        this(nsUri,typeName,baseType,
            facets.getNonNegativeInteger(FACET_MAXLENGTH),
            facets.isFixed(FACET_MAXLENGTH));
    }

    protected MaxLengthFacet( String nsUri, String typeName, XSDatatypeImpl baseType, int _maxLength, boolean _isFixed )
        throws DatatypeException {
        super(nsUri,typeName,baseType,FACET_MAXLENGTH,_isFixed);

        this.maxLength = _maxLength;

        // loosened facet check
        DataTypeWithFacet o = baseType.getFacetObject(FACET_MAXLENGTH);
        if(o!=null && ((MaxLengthFacet)o).maxLength < this.maxLength )
            throw new DatatypeException( localize( ERR_LOOSENED_FACET,
                FACET_MAXLENGTH, o.displayName() ) );
        
        // consistency with minLength is checked in XSDatatypeImpl.derive method.
    }
    
    public Object _createValue( String literal, ValidationContext context ) {
        Object o = baseType._createValue(literal,context);
        if(o==null || ((Discrete)concreteType).countLength(o)>maxLength)    return null;
        return o;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        Object o = concreteType._createValue(content,context);
        // base type must have accepted this lexical value, otherwise 
        // this method is never called.
        if(o==null)    throw new IllegalStateException();    // assertion
        
        int cnt = ((Discrete)concreteType).countLength(o);
        if(cnt>maxLength)
            throw new DatatypeException( DatatypeException.UNKNOWN,
                localize(ERR_MAXLENGTH, new Integer(cnt), new Integer(maxLength)) );
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
