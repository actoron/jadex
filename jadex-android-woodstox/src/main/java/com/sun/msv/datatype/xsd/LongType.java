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
 * "long" type.
 * 
 * type of the value object is <code>java.lang.Long</code>.
 * See http://www.w3.org/TR/xmlschema-2/#long for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LongType extends IntegerDerivedType {
    public static final LongType theInstance = new LongType();
    private LongType() {
        super("long",createRangeFacet( IntegerType.theInstance,
            new Long(Long.MIN_VALUE),
            new Long(Long.MAX_VALUE)));
    }
    protected LongType( String typeName, XSDatatypeImpl baseFacets ) {
        super(typeName,baseFacets);
    }
    
    public XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return load(lexicalValue);
    }
    
    public static Long load( String s ) {
        // Implementation of JDK1.2.2/JDK1.3 is suitable enough
        try {
            return new Long(removeOptionalPlus(s));
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    
    public static String save( Long v ) {
        return v.toString();
    }
    
    public Class getJavaObjectType() {
        return Long.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
