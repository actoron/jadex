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
 * "unsignedByte" type.
 * 
 * type of the value object is <code>java.lang.Short</code>.
 * See http://www.w3.org/TR/xmlschema-2/#unsignedByte for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnsignedByteType extends ShortType {
    public static final UnsignedByteType theInstance = new UnsignedByteType();
    private UnsignedByteType() {
        super("unsignedByte",createRangeFacet(
            UnsignedShortType.theInstance,
            null,
            new Short((short)255)));
    }

    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final short upperBound = 255;
    
    final public XSDatatype getBaseType() {
        return UnsignedShortType.theInstance;
    }

    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // Implementation of JDK1.2.2/JDK1.3 is suitable enough
        try {
            Short v = (Short)super._createValue(lexicalValue,context);
            if( v==null )                        return null;
            if( v.shortValue()<0 )               return null;
            if( v.shortValue()>upperBound )      return null;
            return v;
        } catch( NumberFormatException e ) {
            return null;
        }
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
