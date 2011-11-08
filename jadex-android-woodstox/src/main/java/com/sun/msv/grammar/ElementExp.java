/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * Element declaration.
 * 
 * For RELAX, this is a base implementation of 'elementRule' declaration.
 * For TREX, this is a base implementation of 'element' pattern.
 * 
 * Each grammar must/can provide only one concrete implementation.
 * Therefore, they cannot override visit method.
 * 
 * <p>
 * This class can be extended.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ElementExp extends Expression implements NameClassAndExpression {
    /** content model of this element declaration. */
    public Expression contentModel;
    public final Expression getContentModel() { return contentModel; }
    
    /** a flag that indicates undeclared attributes should be ignored. */
    public boolean ignoreUndeclaredAttributes;
    
    /** obtains a constraint over tag name.
     * 
     * ElementExp is cannot be shared because NameClass has to be mutable
     * to absorb the difference of RELAX and TREX.
     * 
     * In case of TREX, name class will be determined when parsing ElementExp itself.
     * Thus effectively it's immutable.
     * 
     * In case of RELAX, name class will be determined when its corresponding Clause
     * object is parsed. 
     */
    abstract public NameClass getNameClass();
    
    public ElementExp( Expression contentModel, boolean ignoreUndeclaredAttributes ) {
        // since ElementExp is not unified, no two ElementExp objects are considered equal.
        // therefore essentially any value can be used as hash code.
        // that's why this code works even when content model may be changed later.
        super( contentModel.hashCode() );
        this.contentModel = contentModel;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }
    
    protected final int calcHashCode() {
        return contentModel.hashCode();
    }

    public final boolean equals( Object o ) {
        return this==o;
    }

    public final Object visit( ExpressionVisitor visitor )                    { return visitor.onElement(this); }
    public final Expression visit( ExpressionVisitorExpression visitor )    { return visitor.onElement(this); }
    public final boolean visit( ExpressionVisitorBoolean visitor )            { return visitor.onElement(this); }
    public final void visit( ExpressionVisitorVoid visitor )                { visitor.onElement(this); }
    
    protected final boolean calcEpsilonReducibility()
    { return false; }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
