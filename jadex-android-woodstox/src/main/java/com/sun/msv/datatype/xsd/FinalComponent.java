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


/**
 * "final" component.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class FinalComponent extends Proxy {
    
    private final int finalValue;
    
    public FinalComponent( XSDatatypeImpl baseType, int finalValue ) {
        this( baseType.getNamespaceUri(),  baseType.getName(), baseType, finalValue );
    }
    
    public FinalComponent( String nsUri, String newTypeName, XSDatatypeImpl baseType, int finalValue ) {
        super( nsUri, newTypeName, baseType );
        this.finalValue = finalValue;
    }
    
    public boolean isFinal( int derivationType ) {
        if( (finalValue&derivationType) != 0 )    return true;
        return super.isFinal(derivationType);
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
