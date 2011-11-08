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
import com.sun.msv.grammar.trex.TREXGrammar;

/**
 * parses &lt;grammar&gt; element.
 * 
 * this state is used to parse top-level grammars and nested grammars.
 * grammars merged by include element are handled by MergeGrammarState.
 * 
 * <p>
 * this class provides a new TREXGrammar object to localize names defined
 * within this grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarState extends DivInGrammarState {
    protected TREXGrammar previousGrammar;
    protected TREXGrammar newGrammar;
    
    protected Expression makeExpression() {
        // start pattern is the grammar-as-a-pattern.
        return newGrammar;
    }

    protected void startSelf() {
        super.startSelf();
        
        previousGrammar = getReader().grammar;
        newGrammar = getReader().sfactory.createGrammar( reader.pool, previousGrammar );
        getReader().grammar = newGrammar;
    }

    public void endSelf() {
        final TREXGrammar grammar = getReader().grammar;
        
        // detect references to undefined pattterns
        reader.detectUndefinedOnes(
            grammar.namedPatterns, TREXBaseReader.ERR_UNDEFINED_PATTERN );

        // is start pattern defined?
        if( grammar.exp==null ) {
            reader.reportError( TREXBaseReader.ERR_MISSING_TOPLEVEL );
            grammar.exp = Expression.nullSet;    // recover by assuming a valid pattern
        }
        
        // this method is called when this State is about to be removed.
        // restore the previous grammar
        if( previousGrammar!=null )
            getReader().grammar = previousGrammar;
        
        // if the previous grammar is null, it means this grammar is the top-level
        // grammar. In that case, leave it there so that GrammarReader can access
        // the loaded grammar.
            
        super.endSelf();
    }
}
