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
 * Union of two NameClasses.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ChoiceNameClass extends NameClass {
    public final NameClass nc1;
    public final NameClass nc2;
    
    /** accepts a name if it is accepted by nc1 or nc2. */
    public boolean accepts( String namespaceURI, String localPart ) {
        return nc1.accepts(namespaceURI,localPart)
            || nc2.accepts(namespaceURI,localPart);
    }
    
    public Object visit( NameClassVisitor visitor ) { return visitor.onChoice(this); }
    
    public ChoiceNameClass( NameClass nc1, NameClass nc2 ) {
        this.nc1 = nc1;
        this.nc2 = nc2;
    }
    
    public String toString() {
        return '('+nc1.toString()+'|'+nc2.toString()+')';
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
