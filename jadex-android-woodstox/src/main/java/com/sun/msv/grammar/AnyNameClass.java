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
 * a NameClass that matches any name.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class AnyNameClass extends NameClass {
    
    /** AnyNameClass accpets any name, as its name suggests. */
    public boolean accepts( String namespaceURI, String localName ) {
        return true;
    }
    
    public Object visit( NameClassVisitor visitor ) { return visitor.onAnyName(this); }
    
    /**
     * Singleton instance.
     * 
     * @deprecated Use {@link NameClass#ALL} instead.
     */
    public static final NameClass theInstance = new AnyNameClass();
    
    protected AnyNameClass() {}
    
    public String toString()    { return "*:*"; }
    
    // serialization support
    private static final long serialVersionUID = 1;    
    private Object readResolve() { return theInstance; }
}
