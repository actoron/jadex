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
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * Delegates all methods to the base type.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Proxy extends XSDatatypeImpl {
    /** immediate base type, which may be a concrete type or DataTypeWithFacet */
    public final XSDatatypeImpl baseType;
    final public XSDatatype getBaseType() { return baseType; }
    
    public Proxy( String nsUri, String newTypeName, XSDatatypeImpl baseType ) {
        super( nsUri, newTypeName, baseType.whiteSpace );
        this.baseType = baseType;
    }
    
    public boolean isContextDependent() {
        return baseType.isContextDependent();
    }
    
    public int getIdType() {
        return baseType.getIdType();
    }
    
    public boolean isFinal( int derivationType ) {
        return baseType.isFinal(derivationType);
    }
    
    public ConcreteType getConcreteType() {
        return baseType.getConcreteType();
    }
    
    public String displayName() {
        return baseType.displayName();
    }
    
    public int getVariety() {
        return baseType.getVariety();
    }
    
    public int isFacetApplicable( String facetName ) {
        return baseType.isFacetApplicable(facetName);
    }
    
    public boolean checkFormat( String content, ValidationContext context ) {
        return baseType.checkFormat(content,context);
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        return baseType._createValue(content,context);
    }

    public DataTypeWithFacet getFacetObject( String facetName ) {
        return baseType.getFacetObject(facetName);
    }
    
    public Class getJavaObjectType() {
        return baseType.getJavaObjectType();
    }
    
    public Object _createJavaObject( String literal, ValidationContext context ) {
        return baseType._createJavaObject(literal,context);
    }
    
    public String serializeJavaObject( Object value, SerializationContext context ) {
        return baseType.serializeJavaObject(value,context);
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        return baseType.convertToLexicalValue(value,context);
    }
    
    public void _checkValid( String content, ValidationContext context ) throws DatatypeException {
        baseType._checkValid(content,context);
    }
    

    // serialization support
    private static final long serialVersionUID = 1;    
}
