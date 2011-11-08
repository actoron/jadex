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
 * "nonNegativeInteger" type.
 * 
 * type of the value object is {@link IntegerValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#nonNegativeInteger for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NonNegativeIntegerType extends IntegerType {
    public static final NonNegativeIntegerType theInstance = new NonNegativeIntegerType();
    private NonNegativeIntegerType() {
        super("nonNegativeInteger",
            createRangeFacet(IntegerType.theInstance,
                IntegerValueType.create("0"),null));
    }
    
    final public XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        Object o = super._createValue(lexicalValue,context);
        if(o==null)        return null;
        
        final IntegerValueType v = (IntegerValueType)o;
        if( !v.isNonNegative() )    return null;
        return v;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
