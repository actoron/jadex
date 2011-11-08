/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * parses &lt;name&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassNameState extends NameClassWithoutChildState {
    protected final StringBuffer text = new StringBuffer();
    
    public void characters( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    public void ignorableWhitespace( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }

    protected NameClass makeNameClass() {
        
        String name = WhiteSpaceProcessor.collapse(new String(text));
        
        int idx = name.indexOf(':');
        if(idx<0)
            // if the name is NCName
            return new SimpleNameClass( getPropagatedNamespace(), name );
        
        // if it's a QName, resolve it.
        String[] qname 
            = reader.splitQName(name);
        
        return new SimpleNameClass( qname[0], qname[1] );
    }
}
