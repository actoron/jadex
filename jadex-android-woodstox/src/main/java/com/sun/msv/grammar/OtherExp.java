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
 * Base class for application-specific AGM annotation.
 * 
 * <p>
 * This primitive has no meaning to MSV. For example, the following expression
 * <pre>
 * Expression exp = new OtherExp( pool.createSequence(a,b) );
 * </pre>
 * is treated as if MSV sees the following, OtherExp-less expression:
 * <pre>
 * Expression exp = pool.createSequence(a,b);
 * </pre>
 * 
 * <p>
 * By using this "transparency", application can implement derived classes
 * of <code>OtherExp</code> and add application-specific information to AGM.
 * 
 * <p>
 * For example, you can implement <code>AnnotationInfoExp</code> class that derives
 * <code>OtherExp</code> and introduces "documentation" field.
 * Then you'll write a customized <code>XMLSchemaReader</code> that
 * parses &lt;annotation&gt; tag and mix <code>AnnotationInfoExp</code> into an AGM.
 * Your application can then examine it and do some useful things.
 * 
 * <p>
 * Those application-specific information added through OtherExp are completely
 * ignored by MSV. So the annotated AGM can still be used just like anormal AGM.
 * 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class OtherExp extends Expression {
    
    /**
     * returns the string which will be used by ExpressionPrinter
     * to print this expression.
     */
    public String printName() {
        String className = this.getClass().getName();
        int idx = className.lastIndexOf('.');
        if(idx>=0)    className = className.substring(idx+1);
        return className;
    }
    
    /**
     * child expression.
     */
    public Expression exp;
    
    public OtherExp() {
    }
    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }

    public OtherExp( Expression exp ) {
        this();
        this.exp = exp;
    }
    
    public boolean equals( Object o ) {
        return this==o;
    }
    
    protected boolean calcEpsilonReducibility() {
        if(exp==null)
//            // actual expression is not supplied yet.
//            // actual definition of the referenced expression must be supplied
//            // before any computation over the grammar.
//            throw new Error();    // assertion failed.
            return false;
        // this method can be called while parsing a grammar.
        // in that case, epsilon reducibility is just used for approximation.
        // therefore we can safely return false.
        
        return exp.isEpsilonReducible();
    }
    
    // derived class must be able to behave as a ReferenceExp
    public final Object visit( ExpressionVisitor visitor )                { return visitor.onOther(this); }
    public final Expression visit( ExpressionVisitorExpression visitor ){ return visitor.onOther(this); }
    public final boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onOther(this); }
    public final void visit( ExpressionVisitorVoid visitor )            { visitor.onOther(this); }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
