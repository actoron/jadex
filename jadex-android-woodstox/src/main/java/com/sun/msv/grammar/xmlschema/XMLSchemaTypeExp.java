/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

/**
 * Base class of {@link ComplexTypeExp} and {@link SimpleTypeExp}.
 * 
 * This class represents "type" of W3C XML Schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class XMLSchemaTypeExp extends RedefinableExp {
    
    XMLSchemaTypeExp( String typeLocalName ) {
        super(typeLocalName);
    }
    
    /**
     * gets the value of the block constraint.
     */
    public abstract int getBlock();

    
    // actual values for these constants must keep in line with those values
    // defined in the ElementDeclExp.
    public static final int RESTRICTION    = 0x1;
    public static final int EXTENSION    = 0x2;
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
