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
 * Attribute declaration.
 * 
 * <p>
 * Attribute declaration consists of a NameClass that verifies attribute name
 * and an Expression that verifies the value of the attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeExp extends Expression implements NameClassAndExpression
{
    // serialization support
    private static final long serialVersionUID = 1;    

    /** constraint over attribute name */
    public final NameClass nameClass;
    public final NameClass getNameClass() { return nameClass; }
    
    /** child expression */
    public final Expression exp;
    public final Expression getContentModel() { return exp; }

    protected String defaultValue;
    
    public AttributeExp( NameClass nameClass, Expression exp ) {
        super( nameClass.hashCode()+exp.hashCode() );
        this.nameClass    = nameClass;
        this.exp        = exp;
    }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String v) { defaultValue = v; }
    
    protected final int calcHashCode() {
        int hash = nameClass.hashCode()+exp.hashCode();
        if (defaultValue != null) {
            hash += defaultValue.hashCode();
        }
        return hash;
    }
    
    public boolean equals( Object o ) {
        if (o == this) return true;
        if (o == null) return false;
        // reject derived classes
        if(o.getClass() != getClass()) return false;
        
        AttributeExp rhs = (AttributeExp)o;
        if (rhs.nameClass.equals(nameClass) && rhs.exp.equals(exp)) {
            if (defaultValue != null) {
                return defaultValue.equals(rhs.defaultValue);
            }
            return (rhs.defaultValue == null);
        }
        return false;
    }
    
    public Object visit( ExpressionVisitor visitor )                { return visitor.onAttribute(this);    }
    public Expression visit( ExpressionVisitorExpression visitor )    { return visitor.onAttribute(this); }
    public boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onAttribute(this);    }
    public void visit( ExpressionVisitorVoid visitor )                { visitor.onAttribute(this);    }
    
    protected boolean calcEpsilonReducibility() {
        return false;
    }
}
