/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.dtd;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * a NameClass that accepts any tag name as long as its local part is specified name.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class LocalNameClass extends NameClass {
    public final String localName;
    
    public boolean accepts( String namespaceURI, String localName ) {
        return    this.localName.equals(localName) || LOCALNAME_WILDCARD.equals(localName);
    }
    
    public Object visit( NameClassVisitor visitor ) {
        // use the approximation.
        // FIXME
        return new SimpleNameClass("",localName).visit(visitor);
    }
    
    public LocalNameClass( String localName ) {
        this.localName        = localName;
    }
    
    public String toString() {
        return localName;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
