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
 * state that creates SequenceExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SequenceState extends ExpressionWithChildState {
    public SequenceState() {
        this(false);
    }
    public SequenceState( boolean allowEmptySequence ) {
        this.allowEmptySequence = allowEmptySequence;
    }
    
    protected boolean allowEmptySequence;
    
    protected Expression initialExpression() {
        return allowEmptySequence?Expression.epsilon:null;
    }
    
    protected Expression castExpression( Expression exp, Expression child ) {
        // first one.
        if( exp==null )    return child;
        return reader.pool.createSequence(exp,child);
    }
    
}
