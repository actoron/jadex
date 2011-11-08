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
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses the root state of a grammar included as a pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootIncludedPatternState extends SimpleState implements ExpressionOwner {

    protected State createChildState( StartTagInfo tag ) {
        // grammar has to be treated separately so as not to
        // create unnecessary TREXGrammar object.
//        if(tag.localName.equals("grammar"))
//            return new GrammarState();
        
        State s = reader.createExpressionChildState(this,tag);
//        if(s!=null) {
//            // other pattern element is specified.
//            // create wrapper grammar
//            final TREXBaseReader reader = (TREXBaseReader)this.reader;
//            reader.grammar = new TREXGrammar( reader.pool, null );
//            simple = true;
//        }
        
        return s;
    }
    
    
    /**
     * parsed external pattern will be reported to this object.
     * This state parses top-level, so parentState is null.
     */
    private final IncludePatternState grandParent;
    
    protected RootIncludedPatternState( IncludePatternState grandpa ) {
        this.grandParent = grandpa;
    }
        
    public void onEndChild(Expression exp) {
        if( grandParent!=null )
            // this must be from grammar element. pass it to the IncludePatternState.
            grandParent.onEndChild(exp);

    }
}
