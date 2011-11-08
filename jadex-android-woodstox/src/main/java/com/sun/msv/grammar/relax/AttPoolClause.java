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
import com.sun.msv.grammar.ReferenceExp;

/**
 * 'attPool'  of RELAX module.
 * 
 * ReferenceExp.exp contains a sequence of AttributeExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttPoolClause extends ReferenceExp implements Exportable {
    
    protected AttPoolClause( String role )    { super(role); }

    /**
     * a flag that indicates this elementRule is exported and
     * therefore accessible from other modules.
     */
    public boolean exported = false;
    public boolean isExported() { return exported; }
    
    public Object visit( RELAXExpressionVisitor visitor ) {
        return visitor.onAttPool(this);
    }

    public Expression visit( RELAXExpressionVisitorExpression visitor ) {
        return visitor.onAttPool(this);
    }
    
    public boolean visit( RELAXExpressionVisitorBoolean visitor ) {
        return visitor.onAttPool(this);
    }

    public void visit( RELAXExpressionVisitorVoid visitor ) {
        visitor.onAttPool(this);
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
