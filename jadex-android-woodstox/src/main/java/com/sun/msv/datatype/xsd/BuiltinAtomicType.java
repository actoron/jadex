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

import org.relaxng.datatype.DatatypeException;

/**
 * base class for atomic built-in types; those types which can be used by itself
 * (int,uriReference,string, etc) .
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BuiltinAtomicType extends ConcreteType
{
    protected BuiltinAtomicType( String typeName, WhiteSpaceProcessor whiteSpace ) {
        super( XMLSCHEMA_NSURI, typeName, whiteSpace );
    }
    
    protected BuiltinAtomicType( String typeName ) {
        this( typeName, WhiteSpaceProcessor.theCollapse );
    }
    
    public final int getVariety() { return VARIETY_ATOMIC; }

    
    public final String displayName() {
        // built-in types always have fixed names.
        return getName();
    }
    
    protected Object readResolve() throws java.io.ObjectStreamException {
        // return the sigleton object, if any.
        String name = getName();
        if(name!=null) {
            try {
                return DatatypeFactory.getTypeByName(name);
            } catch( DatatypeException e ) {
                ;
            }
        }
        
        return this;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
