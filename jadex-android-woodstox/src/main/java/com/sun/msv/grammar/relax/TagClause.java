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
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;

/**
 * 'tag'  of RELAX module.
 * 
 * exp field contains a sequence of AttributeExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TagClause extends ReferenceExp {
    /**
     * tag name constraint.
     * This should be SimpleNameClass. The only exception is for stub module.
     */
    public NameClass nameClass;
    
    /** RefContainer-controlled creation. should be created via RefContainer.getOrCreate */
    protected TagClause( String role )    { super(role); }
    
    /** constructor for inline tag. creatable directly from outside */
    public TagClause() { super(null); }
    
    public Object visit( RELAXExpressionVisitor visitor )
    { return visitor.onTag(this); }

    public Expression visit( RELAXExpressionVisitorExpression visitor )
    { return visitor.onTag(this); }
    
    public boolean visit( RELAXExpressionVisitorBoolean visitor )
    { return visitor.onTag(this); }

    public void visit( RELAXExpressionVisitorVoid visitor )
    { visitor.onTag(this); }

    
    // serialization support
    private static final long serialVersionUID = 1;    
}
