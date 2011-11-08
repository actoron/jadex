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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.SequenceState;

/**
 * parses &lt;start&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StartState extends SequenceState {
    
    protected final TREXBaseReader getReader() { return (TREXBaseReader)reader; }
    
    protected Expression annealExpression( Expression exp ) {
        final String name = startTag.getAttribute("name");
        
        if(name!=null) {
            // name attribute is optional.
            ReferenceExp ref = getReader().grammar.namedPatterns.getOrCreate(name);
            ref.exp = exp;
            exp = ref;
        }
        
        getReader().grammar.exp = exp;
        return null;    // return value is meaningless.
    }
}
