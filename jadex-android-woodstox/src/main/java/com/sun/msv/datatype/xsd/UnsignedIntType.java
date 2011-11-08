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
 * "unsignedInt" type.
 * 
 * type of the value object is <code>java.lang.Long</code>.
 * See http://www.w3.org/TR/xmlschema-2/#unsignedInt for the spec
 *
 * We don't have language support for unsigned datatypes, so things are not so easy.
 * UnsignedIntType uses a LongType as a base implementation, for the convenience and
 * faster performance.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnsignedIntType extends LongType {
    
    public static final UnsignedIntType theInstance = new UnsignedIntType();
    private UnsignedIntType() {
        super("unsignedInt",createRangeFacet(
            UnsignedLongType.theInstance,
            null,
            new Long(4294967295L)));
    }
    
    final public XSDatatype getBaseType() {
        return UnsignedLongType.theInstance;
    }

    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final long upperBound = 4294967295L;
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // Implementation of JDK1.2.2/JDK1.3 is suitable enough
        try {
            Long v = (Long)super._createValue(lexicalValue,context);
            if( v==null )                        return null;
            if( v.longValue()<0 )               return null;
            if( v.longValue()>upperBound )      return null;
            return v;
        } catch( NumberFormatException e ) {
            return null;
        }
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
