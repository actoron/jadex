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
 * Base implementation for those states who read tags representing an expression.
 * 
 * <p>
 * Responsibility of derived classes are:
 * 
 * <ol>
 *  <li>if necessary, implement startSelf method to do something.
 *  <li>implement createChildState method, which is mandated by SimpleState.
 *  <li>implement makeExpression method to create Expression object
 *        as the outcome of parsing. This method is called at endElement.
 * </ol>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionState extends SimpleState
{
    protected void endSelf()
    {
        // creates a expression, then calls a hook of reader,
        Expression exp = reader.interceptExpression( this, makeExpression() );
        
        if( parentState!=null )
            // then finally pass it to the parent
            ((ExpressionOwner)parentState).onEndChild(exp);
        
        // interceptExpression is a hook by reader.
        // it is used to implement handling of occurs attribute in RELAX.
        // application-defined reader can also do something useful for them here.

        super.endSelf();
    }
        
    /**
     * This method is called from endElement method.
     * Implementation has to provide Expression object that represents the content of
     * this element.
     */
    protected abstract Expression makeExpression();
}
    
