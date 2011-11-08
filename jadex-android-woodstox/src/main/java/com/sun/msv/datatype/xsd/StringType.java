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
 * "string" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#string for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringType extends BuiltinAtomicType implements Discrete {
    
    public static final StringType theInstance
        = new StringType("string",WhiteSpaceProcessor.thePreserve,true);
    
    /**
     * Value returned from the isAlwaysValid method.
     */
    private final boolean isAlwaysValid;

    protected StringType( String typeName, WhiteSpaceProcessor whiteSpace ) {
        this( typeName, whiteSpace, false );
    }
    
    protected StringType( String typeName, WhiteSpaceProcessor whiteSpace, boolean _isAlwaysValid ) {
        super(typeName,whiteSpace);
        this.isAlwaysValid = _isAlwaysValid;
    }
    
    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    protected final boolean checkFormat( String content, ValidationContext context ) {
        // string derived types should use _createValue method to check its validity
        return _createValue(content,context)!=null;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // for string, lexical space is value space by itself
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
    
    public final int countLength( Object value ) {
        // for string-derived types, length means number of XML characters.
        return UnicodeUtil.countLength( (String)value );
    }
    
    public final int isFacetApplicable( String facetName ) {
        if( facetName.equals(FACET_PATTERN)
        ||    facetName.equals(FACET_ENUMERATION)
        ||    facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_LENGTH)
        ||    facetName.equals(FACET_MAXLENGTH)
        ||    facetName.equals(FACET_MINLENGTH) )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }

    public boolean isAlwaysValid() {
        return isAlwaysValid;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
