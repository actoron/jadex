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

import org.relaxng.datatype.Datatype;

import com.sun.msv.util.StringPair;

/**
 * Expression that matchs a particular value of a {@link Datatype}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ValueExp extends Expression implements DataOrValueExp {
    
    /** Datatype object that is used to test the equality. */
    public final Datatype dt;
    public Datatype getType() { return dt; }

    /** This expression matches this value only. */
    public final Object value;
    
    /**
     * name of this datatype.
     * 
     * The value of this field is not considered as significant.
     * When two TypedStringExps share the same Datatype object,
     * then they are unified even if they have different names.
     */
    public final StringPair name;
    public StringPair getName() { return name; }
    
    protected ValueExp( Datatype dt, StringPair typeName, Object value ) {
        super(dt.hashCode()+dt.valueHashCode(value));
        this.dt=dt;
        this.name = typeName;
        this.value = value;
    }

    protected final int calcHashCode() {
        return dt.hashCode()+dt.valueHashCode(value);
    }
    
    public boolean equals( Object o ) {
        // Note that equals method of this class *can* be sloppy, 
        // since this class does not have a pattern as its child.
        
        // Therefore datatype vocaburary does not necessarily provide
        // strict equals method.
        if(o.getClass()!=this.getClass())    return false;
        
        ValueExp rhs = (ValueExp)o;
        
        if(!rhs.dt.equals(dt))                return false;
        
        return dt.sameValue(value,rhs.value);
    }
    
    public Object visit( ExpressionVisitor visitor )                { return visitor.onValue(this); }
    public Expression visit( ExpressionVisitorExpression visitor )    { return visitor.onValue(this); }
    public boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onValue(this); }
    public void visit( ExpressionVisitorVoid visitor )                { visitor.onValue(this); }

    protected boolean calcEpsilonReducibility() {
        return false;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
