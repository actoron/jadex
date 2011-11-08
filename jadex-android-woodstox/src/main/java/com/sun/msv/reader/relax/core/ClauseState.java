/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * common part of &lt;tag&gt; and &lt;attPool&gt;.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class ClauseState extends SimpleState implements ExpressionOwner
{
    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("ref"))            return getReader().getStateFactory().refRole(this,tag);
        if(tag.localName.equals("attribute"))    return getReader().getStateFactory().attribute(this,tag);
        
        return null;    // unrecognized
    }
    
    protected Expression initialExpression()    { return Expression.epsilon; }
    
    protected Expression castExpression( Expression exp, Expression child ) {
        // attributes and references are combined in one sequence
        return reader.pool.createSequence(exp,child);
    }
    

    /** gets reader in type-safe fashion */
    protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }



    /**
     * expression object that is being created.
     * See {@link #castPattern} and {@link #annealPattern} methods
     * for how will a pattern be created.
     */
    protected Expression exp = initialExpression();
    
    /** receives a Pattern object that is contained in this element. */
    public final void onEndChild( Expression childExpression ) {
        exp = castExpression( exp, childExpression );
    }
}
