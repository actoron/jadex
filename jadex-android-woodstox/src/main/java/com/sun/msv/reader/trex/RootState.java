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
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * invokes State object that parses the document element.
 * 
 * This class is used to parse the first grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootState extends RootIncludedPatternState {
    
    public RootState() { super(null); }

    protected State createChildState( StartTagInfo tag ) {
        final TREXBaseReader reader = (TREXBaseReader)this.reader;

        // grammar has to be treated separately so as not to
        // create unnecessary TREXGrammar object.
        if(tag.localName.equals("grammar"))
            return reader.sfactory.grammar(null,tag);
        
        State s = super.createChildState(tag);
        if(s!=null) {
            // other pattern element is specified.
            // create wrapper grammar
            reader.grammar = reader.sfactory.createGrammar( reader.pool, null );
            simple = true;
        }
        
        return s;
    }
    
    /**
     * a flag that indicates 'grammar' element was not used.
     * In that case, this object is responsible to set start pattern.
     */
    private boolean simple = false;
        
    // GrammarState implements ExpressionState,
    // so RootState has to implement ExpressionOwner.
    public void onEndChild(Expression exp) {
        super.onEndChild(exp);

        final TREXBaseReader reader = (TREXBaseReader)this.reader;

        if( simple )
            // set the top-level expression if that is necessary.
            reader.grammar.exp = exp;
        
        // perform final wrap-up.
        reader.wrapUp();
    }
}
