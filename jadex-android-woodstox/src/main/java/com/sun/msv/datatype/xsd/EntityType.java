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
 * "ENTITY" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#ENTITY for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class EntityType extends BuiltinAtomicType {
    public static final EntityType theInstance = new EntityType();
    private EntityType() { super("ENTITY"); }
    
    final public XSDatatype getBaseType() {
        return NcnameType.theInstance;
    }

    /**
     * ENTITY type always returns true. That is, ENTITY is a context-dependent type.
     */
    public boolean isContextDependent() {
        return true;
    }
    
    public final int isFacetApplicable( String facetName ) {
        if( facetName.equals(FACET_LENGTH)
        ||    facetName.equals(FACET_MINLENGTH)
        ||    facetName.equals(FACET_MAXLENGTH)
        ||    facetName.equals(FACET_PATTERN)
        ||  facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_ENUMERATION) )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }
    
    protected boolean checkFormat( String content, ValidationContext context ) {
        return context.isUnparsedEntity(content);
    }

    public Object _createValue( String content, ValidationContext context ) {
        if(context.isUnparsedEntity(content))    return content;
        else                                    return null;
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

    // serialization support
    private static final long serialVersionUID = 1;    
}
