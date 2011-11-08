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
 * State that always returns the same expression.
 * 
 * Typically used for &lt;empty/&gt;, &lt;notAllowed/&gt; and &lt;text/> of RELAX NG.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TerminalState extends ExpressionWithoutChildState {
    
    private final Expression exp;
    
    public TerminalState( Expression exp ) { this.exp = exp; }
    
    protected Expression makeExpression() { return exp; }
}
