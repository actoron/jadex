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
 * "byte" type.
 * 
 * type of the value object is <code>java.lang.Byte</code>.
 * See http://www.w3.org/TR/xmlschema-2/#byte for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ByteType extends IntegerDerivedType {
    public final static ByteType theInstance = new ByteType();
    private ByteType() {
        super("byte",createRangeFacet(ShortType.theInstance,
            new Byte(Byte.MIN_VALUE), new Byte(Byte.MAX_VALUE)));
    }
    
    final public XSDatatype getBaseType() {
        return ShortType.theInstance;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        return load(content);
    }
    
    public static Byte load( String s ) {
        // Implementation of JDK1.2.2/JDK1.3 is suitable enough
        try {
            return new Byte(removeOptionalPlus(s));
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    public static String save( Byte v ) {
        return v.toString();
    }
    public Class getJavaObjectType() {
        return Byte.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
