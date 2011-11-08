/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.ReferenceExp;

/**
 * parses &lt;start&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StartState extends DefineState {

    protected ReferenceExp getReference() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        if( startTag.containsAttribute("name") ) {
            // since the name attribute is allowed to the certain point,
            // it is useful to explicitly raise this as an error.
            reader.reportError( RELAXNGReader.ERR_DISALLOWED_ATTRIBUTE, startTag.qName, "name" );
            return null;
        }
            
        return reader.getGrammar();
    }
}
