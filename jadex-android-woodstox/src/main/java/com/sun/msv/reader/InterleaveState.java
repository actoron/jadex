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
 * state that creates an InterleaveExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InterleaveState extends ExpressionWithChildState {
    
    protected Expression castExpression( Expression exp, Expression child ) {
        // first one.
        if( exp==null )        return child;
        return reader.pool.createInterleave(exp,child);
    }
}
