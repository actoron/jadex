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
 * "boolean" type.
 * 
 * type of the value object is <code>java.lang.Boolean</code>.
 * See http://www.w3.org/TR/xmlschema-2/#boolean for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BooleanType extends BuiltinAtomicType {
    public static final BooleanType theInstance = new BooleanType();
    
    private BooleanType()    { super("boolean"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    protected boolean checkFormat( String content, ValidationContext context ) {
        return "true".equals(content) || "false".equals(content)
            || "0".equals(content) || "1".equals(content);
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // for string, lexical space is value space by itself
        return load(lexicalValue);
    }
    
    public static Boolean load( String s ) {
        if( s.equals("true") )        return Boolean.TRUE;
        if( s.equals("1") )            return Boolean.TRUE;
        if( s.equals("0") )            return Boolean.FALSE;
        if( s.equals("false") )        return Boolean.FALSE;
        return null;
    }

    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if( value instanceof Boolean )
            return save( (Boolean)value );
        else
            throw new IllegalArgumentException();
    }

    public static String save( Boolean b ) {
        if( b.booleanValue()==true )    return "true";
        else                            return "false";
    }
    
    public int isFacetApplicable( String facetName ) {
        if(facetName.equals(FACET_PATTERN)
        || facetName.equals(FACET_ENUMERATION)
        || facetName.equals(FACET_WHITESPACE))
            return APPLICABLE;
        return NOT_ALLOWED;
    }
    public Class getJavaObjectType() {
        return Boolean.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
