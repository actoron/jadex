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

import com.sun.msv.grammar.ExpressionVisitor;

/**
 * Visitor interface for RELAX expressions.
 * 
 * By implementing this interface, your visitor can distinguish
 * four subclass of ReferenceExp introduced as RELAX stub.
 * 
 * <p>
 * Note that onRef method may still be called if you visit AGM created from
 * TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface RELAXExpressionVisitor extends ExpressionVisitor {
    
    Object onAttPool( AttPoolClause exp );
    Object onTag( TagClause exp );
    Object onElementRules( ElementRules exp );
    Object onHedgeRules( HedgeRules exp );
}
