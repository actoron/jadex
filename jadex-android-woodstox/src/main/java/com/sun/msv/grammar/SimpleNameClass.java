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

import com.sun.msv.util.StringPair;

/**
 * a NameClass that accepts only one fixed name.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class SimpleNameClass extends NameClass {
    public final String    namespaceURI;
    public final String localName;
    
    public boolean accepts( String namespaceURI, String localName ) {
        // wild cards are treated as symbols, rather than strings.
        return    ( this.namespaceURI.equals(namespaceURI) || NAMESPACE_WILDCARD==namespaceURI )
            &&  ( this.localName.equals(localName) || LOCALNAME_WILDCARD==localName );
    }
    
    public Object visit( NameClassVisitor visitor ) { return visitor.onSimple(this); }

    public SimpleNameClass( StringPair name ) {
        this( name.namespaceURI, name.localName );
    }
    
    public SimpleNameClass( String namespaceURI, String localName ) {
        this.namespaceURI    = namespaceURI;
        this.localName        = localName;
    }
    
    public StringPair toStringPair() {
        return new StringPair(namespaceURI,localName);
    }
    
    public String toString() {
        if( namespaceURI.length()==0 )    return localName;
        else                            return /*namespaceURI+":"+*/localName;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
