/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relax;

import com.sun.msv.grammar.ExpressionVisitorVoid;

/**
 * RELAX Version of {@link ExpressionVisitorVoid}
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface RELAXExpressionVisitorVoid extends ExpressionVisitorVoid {
    
    // RELAX visitor can ignore onRef callback.
    void onAttPool( AttPoolClause exp );
    void onTag( TagClause exp );
    void onElementRules( ElementRules exp );
    void onHedgeRules( HedgeRules exp );
}
