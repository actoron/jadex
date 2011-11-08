/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * Base implementation for those expression who has one child expresison.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class UnaryExp extends Expression {
    
    /** child expression. */
    public final Expression exp;
    
    protected UnaryExp( Expression exp) {
        super(exp.hashCode());
        this.exp = exp;
    }
    
    protected final int calcHashCode() {
        return exp.hashCode();
    }

    public boolean equals( Object o ) {
        if( !this.getClass().equals(o.getClass()) )        return false;
        
        // every existing children are already unified.
        // therefore, == is enough. (don't need to call equals)
        return ((UnaryExp)o).exp == exp;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
