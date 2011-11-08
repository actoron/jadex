/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses Expression which contains other expressions.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionWithChildState
    extends ExpressionState implements ExpressionOwner {
    
    /**
     * expression object that is being created.
     * See {@link #castPattern} and {@link #annealPattern} methods
     * for how will a pattern be created.
     */
    protected Expression exp;

    protected void startSelf() {
        super.startSelf();
        exp = initialExpression();
    }
    
    /** sets initial pattern */
    protected Expression initialExpression() {
        return null;
    }
    
    /**
     * computes default expression.
     * this method is called before annealExpression when no child expression
     * is given during parsing. return null to signal an error.
     */
    protected Expression defaultExpression() {
        return null;
    }
    
    /** receives a Pattern object that is contained in this element. */
    public final void onEndChild( Expression childExpression ) {
        exp = castExpression( exp, childExpression );
    }
    
    protected final Expression makeExpression() {
        if( exp==null )
            exp = defaultExpression();
        
        if( exp==null ) {
            reader.reportError( GrammarReader.ERR_MISSING_CHILD_EXPRESSION );
            exp = Expression.nullSet;
            // recover by assuming some pattern.
        }
        return annealExpression(exp);
    }
    
    protected State createChildState( StartTagInfo tag ) {
        return reader.createExpressionChildState(this,tag);
    }

        
    /**
     * combines half-made expression and newly found child expression into the expression.
     * 
     * <p>
     * Say this container has three child expression exp1,exp2, and exp3.
     * Then, the expression of this state will be made by the following method
     * invocations.
     * 
     * <pre>
     *   annealExpression(
     *     castExpression(
     *       castExpression(
     *         castExpression(null,exp1), exp2), exp3 ) )
     * </pre>
     */
    protected abstract Expression castExpression( Expression halfCastedExpression, Expression newChildExpression );
    
    /**
     * performs final wrap-up and returns a fully created Expression object
     * that represents this element.
     */
    protected Expression annealExpression( Expression exp ) {
        // default implementation do nothing.
        return exp;
    }
}
