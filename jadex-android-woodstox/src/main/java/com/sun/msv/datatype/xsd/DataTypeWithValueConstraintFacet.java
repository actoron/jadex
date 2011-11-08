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
 * base class for facets which constrain value space.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class DataTypeWithValueConstraintFacet extends DataTypeWithFacet {
    
    DataTypeWithValueConstraintFacet(
        String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed )
        throws DatatypeException {
    
        super( nsUri, typeName, baseType, facetName, _isFixed );
    }
    
    final protected boolean needValueCheck() { return true; }
    
    protected final boolean checkFormat( String literal, ValidationContext context ) {
        return _createValue(literal,context)!=null;
    }
    
    // serialization support
    private final static long serialVersionUID =  2497055158497151572L;
}
