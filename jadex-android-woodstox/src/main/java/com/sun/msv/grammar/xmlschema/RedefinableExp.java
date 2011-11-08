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

import com.sun.msv.grammar.ReferenceExp;

/**
 * declaration that can be redefined by using &lt;redefine&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class RedefinableExp extends ReferenceExp {
    
    public RedefinableExp( String typeLocalName ) {
        super(typeLocalName);
    }
    
    /** clones this object. */
    public abstract RedefinableExp getClone();
    
    /**
     * assigns contents of rhs to this object.
     * 
     * rhs and this object must be the same runtime type, and
     * they must have the same name.
     * this method redefines this object by the given component.
     * 
     * derived class should override this method and copy
     * necessary fields, should it necessary.
     */
    public void redefine( RedefinableExp rhs ) {
        if( this.getClass()!=rhs.getClass()
        || !this.name.equals(rhs.name) )
            // two must be the same class.
            throw new IllegalArgumentException();
        
        this.exp = rhs.exp;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
