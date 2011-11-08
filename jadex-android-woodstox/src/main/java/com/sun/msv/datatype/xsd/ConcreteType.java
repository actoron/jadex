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
 * base class for types that union/list/atomic.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ConcreteType extends XSDatatypeImpl {
    
    protected ConcreteType( String nsUri, String typeName, WhiteSpaceProcessor whiteSpace ) {
        super( nsUri, typeName, whiteSpace );
    }
    
    protected ConcreteType( String nsUri, String typeName ) {
        this( nsUri, typeName, WhiteSpaceProcessor.theCollapse );
    }
    
    final public ConcreteType getConcreteType() {
        return this;
    }
    
    public boolean isFinal( int derivationType ) {
        // allow derivation by default.
        return false;
    }

    // default implementation for concrete type. somewhat shabby.
    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        if(checkFormat(content,context))    return;
        
        throw new DatatypeException(DatatypeException.UNKNOWN,
            localize(ERR_INAPPROPRIATE_FOR_TYPE, content, getName()) );
    }
    
//
// DatabindableDatatype implementation
//===========================================
// The default implementation yields to the createValue method and
// the convertToLexicalValue method. If a derived class overrides the
// createJavaObject method, then it must also override the serializeJavaObject method.
//
    public Object _createJavaObject( String literal, ValidationContext context ) {
        return _createValue(literal,context);
    }
    public String serializeJavaObject( Object value, SerializationContext context ) {
        String literal = convertToLexicalValue( value, context );
        if(!isValid( literal, serializedValueChecker ))
            return null;
        else
            return literal;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
