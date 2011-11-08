/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.classic;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithChildState;

/**
 * parses &lt;concur&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ConcurState extends ExpressionWithChildState {
    
    protected Expression castExpression( Expression exp, Expression child ) {
        // first one.
        if( exp==null )        return child;
        return reader.pool.createConcur(exp,child);
    }
}
