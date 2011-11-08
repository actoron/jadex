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

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.NameClass;

/**
 * ElementRule declaration of RELAX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRule extends ElementExp {
    
    /** constraints over start tag of the element */
    public final TagClause clause;
    
    /** Attribute-free content model */
    public final Expression attributeFreeContentModel;
    
    protected ElementRules parent;

    /** gets the parent ElementRules object.
     * 
     * when this object is used as a named, no-inline elementRule,
     * this variable holds a reference to the parent ElementRules object.
     * otherwise, null
     */
    public ElementRules getParent() { return parent; }
    
    public final NameClass getNameClass()    { return clause.nameClass; }
    
    public ElementRule( ExpressionPool pool, TagClause clause, Expression contentModel ) {
        super( pool.createSequence(clause,contentModel), true );
        this.clause = clause;
        this.attributeFreeContentModel = contentModel;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
