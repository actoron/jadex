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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceExp;

/**
 * hedgeRule of RELAX module.
 * 
 * ReferenceExp.exp holds a choice of the content models of all hedgeRules that
 * share the same label name.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class HedgeRules extends ReferenceExp implements Exportable {
    
    protected HedgeRules( String label, RELAXModule ownerModule ) {
        super(label);
        this.ownerModule = ownerModule;
    }
    
    public void addHedge( Expression exp, ExpressionPool pool ) {
        if( this.exp==null )        this.exp=exp;    // first time
        else                        this.exp=pool.createChoice(this.exp,exp);
    }
    
    public boolean equals( Object o )    { return this==o; }

    public Object visit( RELAXExpressionVisitor visitor )
    { return visitor.onHedgeRules(this); }

    public Expression visit( RELAXExpressionVisitorExpression visitor )
    { return visitor.onHedgeRules(this); }
    
    public boolean visit( RELAXExpressionVisitorBoolean visitor )
    { return visitor.onHedgeRules(this); }

    public void visit( RELAXExpressionVisitorVoid visitor )
    { visitor.onHedgeRules(this); }

    /** a flag that indicates this hedgeRule is exported and
     * therefore accessible from other modules.
     */
    public boolean exported = false;
    public boolean isExported() { return exported; }
    
    /** RELAXModule object to which this object belongs */
    public final RELAXModule ownerModule;

    
    // serialization support
    private static final long serialVersionUID = 1;    
}
