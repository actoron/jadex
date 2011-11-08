/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.util;

import java.util.Set;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;

/**
 * removes all ReferenceExp from AGM.
 * 
 * when named expression is nullSet, it cannot be used.
 * by replacing ReferenceExp by its definition, those unavailable expressions
 * will be properly removed from AGM.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RefExpRemover extends ExpressionCloner {

    /** set of visited ElementExps */
    private final Set<ElementExp> visitedElements = new java.util.HashSet<ElementExp>();

    private final boolean recursive;

    /**
     * @param _recursive
     *        <p>
     *        If true, this object behaves destructively. It recursively
     *        visits all the reachable expressions and removes ReferenceExps.
     *        In this process, this object changes the content model of 
     *        ElementExps.
     *        
     *        <p>
     *        If false, this object doesn't visit the content models of child
     *        elements, therefore, it behaves non-destructively. Nothing in the
     *        original expression will be touched.
     */
    public RefExpRemover(ExpressionPool pool, boolean _recursive) {
        super(pool);
        this.recursive = _recursive;
    }

    public Expression onElement(ElementExp exp) {
        if (!recursive)
            // do not touch child elements.
            return exp;

        if (!visitedElements.contains(exp)) {
            // remove refs from this content model
            visitedElements.add(exp);
            exp.contentModel = exp.contentModel.visit(this);
        }
        if (exp.contentModel == Expression.nullSet) {
            return Expression.nullSet; // this element is not allowed
        }
        return exp;
    }

    public Expression onAttribute(AttributeExp exp) {
        Expression content = exp.exp.visit(this);
        if (content == Expression.nullSet) {
            return Expression.nullSet; // this attribute is not allowed
        }
        return pool.createAttribute(exp.nameClass, content, exp.getDefaultValue());
    }
    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }
    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }
}
