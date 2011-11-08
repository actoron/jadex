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

import org.relaxng.datatype.ValidationContext;

/**
 * "nonPositiveInteger" type.
 * 
 * type of the value object is {@link IntegerValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#nonPositiveInteger for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NonPositiveIntegerType extends IntegerType {
    public static final NonPositiveIntegerType theInstance = new NonPositiveIntegerType();
    private NonPositiveIntegerType() {
        super("nonPositiveInteger",createRangeFacet(
            IntegerType.theInstance,
            null,
            IntegerValueType.create("0")));
    }
    
    final public XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        Object o = super._createValue(lexicalValue,context);
        if(o==null)        return null;
        
        final IntegerValueType v = (IntegerValueType)o;
        if( !v.isNonPositive() )    return null;
        return v;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
