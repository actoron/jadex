/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.ElementExp;

/**
 * a token that represents an XML element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementToken extends Token {
    
    final ElementExp[] acceptedPatterns;
    
    public ElementToken( ElementExp[] acceptedPatterns ) {
        this.acceptedPatterns = acceptedPatterns;
    }
    
    public boolean match( ElementExp exp ) {
        // since every subpatterns are reused, object identity is enough
        // to judge the equality of patterns
        for( int i=0; i<acceptedPatterns.length; i++ )
            if( acceptedPatterns[i]==exp )    return true;
        return false;
    }
    
    public String toString() {
        String s = "ElementToken";
        for( int i=0; i<acceptedPatterns.length; i++ )
            s += "/"+acceptedPatterns[i].getNameClass().toString();
        return s;
    }
}
