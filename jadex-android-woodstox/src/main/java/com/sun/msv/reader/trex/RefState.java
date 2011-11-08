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
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.ExpressionWithoutChildState;
                                                           
/**
 * parses &lt;ref&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RefState extends ExpressionWithoutChildState {
    
    public RefState( boolean parentRef ) {
        this.parentRef = parentRef;
    }
    
    protected boolean parentRef;
    
    protected Expression makeExpression() {
        final String name = startTag.getCollapsedAttribute("name");
        
        if(name==null) {
            // name attribute is required.
            reader.reportError( TREXBaseReader.ERR_MISSING_ATTRIBUTE,
                "ref","name");
            // recover by returning something that can be interpreted as Pattern
            return Expression.nullSet;
        }
        
        TREXGrammar grammar = ((TREXBaseReader)this.reader).grammar;
        
        if( parentRef ) {
            grammar = grammar.getParentGrammar();
            
            if( grammar==null ) {
                reader.reportError( TREXBaseReader.ERR_NONEXISTENT_PARENT_GRAMMAR );
                return Expression.nullSet;
                // recover by returning something that can be interpreted as Pattern
            }
        }
        
        ReferenceExp r = grammar.namedPatterns.getOrCreate(name);
        wrapUp(r);
        return r;
    }
    
    /**
     * Performs the final wrap-up.
     */
    protected void wrapUp( ReferenceExp r ) {
        reader.backwardReference.memorizeLink(r);
    }

}
