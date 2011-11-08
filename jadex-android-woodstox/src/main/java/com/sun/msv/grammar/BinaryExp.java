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

import java.util.Iterator;

/**
 * Base implementation for those expression which has two child expressions.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BinaryExp extends Expression
{
    // serialization support
    private static final long serialVersionUID = 1;    
    
    public final Expression exp1;
    public final Expression exp2;
    
    public BinaryExp( Expression left, Expression right ) {
        super( left.hashCode()+right.hashCode() );
        this.exp1 = left;
        this.exp2 = right;
    }
    
    protected final int calcHashCode() {
        return exp1.hashCode()+exp2.hashCode();
    }
    
    public boolean equals( Object o ) {
        if( this.getClass()!=o.getClass() )        return false;
        
        // every existing children are already unified.
        // therefore, == is enough. (don't need to call equals)
        BinaryExp rhs = (BinaryExp)o;
        return rhs.exp1 == exp1
            && rhs.exp2 == exp2;
    }
    
    /**
     * returns all child expressions in one array.
     * 
     * This method is similar to the children method but it returns an array 
     * that contains all children instead of an iterator object.
     */
    public Expression[] getChildren() {
        // count the number of children
        int cnt=1;
        Expression exp = this;
        while( exp.getClass()==this.getClass() ) {
            cnt++;
            exp = ((BinaryExp)exp).exp1;
        }
        
        Expression[] r = new Expression[cnt];
        exp=this;
        while( exp.getClass()==this.getClass() ) {
            r[--cnt] = ((BinaryExp)exp).exp2;
            exp = ((BinaryExp)exp).exp1;
        }
        r[0] = exp;
        
        return r;
    }
    
    /**
     * iterates all child expressions.
     * 
     * Since expressions are binarized, expressions like A|B|C is modeled as
     * A|(B|C).  This is may not be preferable for some applications.
     * 
     * <P>
     * This method returns an iterator that iterates all children
     * (A,B, and C in this example)
     */
    public Iterator<Object> children() {
        final Expression[] items = getChildren();
        return new Iterator<Object>() {
            private int idx =0;
            
            public Object next() {
                return items[idx++];
            }
            public boolean hasNext() { return idx!=items.length; }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
}
