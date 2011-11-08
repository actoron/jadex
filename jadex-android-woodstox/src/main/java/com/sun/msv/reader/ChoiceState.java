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

/**
 * state that creates ChoiceExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ChoiceState extends ExpressionWithChildState {
    public ChoiceState() {
        this(false);
    }
    public ChoiceState( boolean allowEmptyChoice ) {
        this.allowEmptyChoice = allowEmptyChoice;
    }
    
    protected boolean allowEmptyChoice;
    
    protected Expression initialExpression() {
        return allowEmptyChoice?Expression.nullSet:null;
    }
    
    protected Expression castExpression( Expression exp, Expression child ) {
        // first one.
        if( exp==null )    return child;
        else            return reader.pool.createChoice(exp,child);
    }
}
