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
 * base class for facets which constrains lexical space of data
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class DataTypeWithLexicalConstraintFacet extends DataTypeWithFacet {
    
    DataTypeWithLexicalConstraintFacet(
        String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed )
        throws DatatypeException {
        super( nsUri, typeName, baseType, facetName, _isFixed );
    }
    
    // this class does not perform any lexical check.
    protected final boolean checkFormat( String literal, ValidationContext context ) {
        if(!baseType.checkFormat(literal,context))    return false;
        return checkLexicalConstraint(literal);
    }
    
    public final Object _createValue( String literal, ValidationContext context ) {
        Object o = baseType._createValue(literal,context);
        if(o!=null && !checkLexicalConstraint(literal) )    return null;
        return o;
    }

    protected abstract boolean checkLexicalConstraint( String literal );
    
    private static final long serialVersionUID = 6093401348890059498L;
}
