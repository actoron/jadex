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

import com.sun.msv.datatype.SerializationContext;
import org.relaxng.datatype.ValidationContext;

/**
 * simple "ur-type" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-1/#simple-ur-type-itself for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleURType extends BuiltinAtomicType {
    
    public static final SimpleURType theInstance = new SimpleURType();
    
    protected SimpleURType() {
        super("anySimpleType",WhiteSpaceProcessor.thePreserve);
    }
    
    /**
     * SimpleURType always returns null to indicate that 
     * there is no base type for this type.
     */
    final public XSDatatype getBaseType() {
        return null;
    }
    
    /**
     * simple ur-type accepts anything.
     */
    protected final boolean checkFormat( String content, ValidationContext context ) {
        return true;
    }
    
    /**
     * the value object of the simple ur-type is the lexical value itself.
     */
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return lexicalValue;
    }
    public Class getJavaObjectType() {
        return String.class;
    }

    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if( value instanceof String )
            return (String)value;
        else
            throw new IllegalArgumentException();
    }
    
    /**
     * no facet is applicable to the simple ur-type.
     */
    public final int isFacetApplicable( String facetName ) {
        return APPLICABLE;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
