/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

/**
 * attribute group declaration.
 * 
 * the inherited exp field contains the attributes defined in this declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeGroupExp extends RedefinableExp implements AttWildcardExp {
    
    /**
     * Attribute wild card constraint.
     * 
     * <p>
     * Due to the nasty definition of the interaction between attribute wildcards,
     * we cannot add the expression for validating wildcard until the very last moment.
     * 
     * <p>
     * In any way, <code>AttribtueGroupExp</code> will NOT contain the expression
     * corresponding to the wildcard. Only <code>ComplexTypeExp</code> will get 
     * that expression.
     * 
     * <p>
     * Until the wrap-up phase of the schema parsing, this field will contain
     * the "local wildcard definition." In the wrap-up phase, this field is replaced
     * by the "complete wildcard definition." 
     */
    public AttributeWildcard wildcard;
    
    public AttributeWildcard getAttributeWildcard() { return wildcard; }
    public void setAttributeWildcard( AttributeWildcard local ) { wildcard=local; }
    
    /**
     * name of this attribute group declaration.
     * According to the spec, the name must be unique within one schema
     * (in our object model, one XMLSchemaSchema object).
     */
    public AttributeGroupExp( String typeLocalName ) {
        super(typeLocalName);
    }
    
    /** clone this object. */
    public RedefinableExp getClone() {
        RedefinableExp exp = new AttributeGroupExp(super.name);
        exp.redefine(this);
        return exp;
    }

// this class does not have its own member, so no need to override this method.
    public void redefine( RedefinableExp _rhs ) {
        super.redefine(_rhs);
        
        AttributeGroupExp rhs = (AttributeGroupExp)_rhs;
        if(rhs.wildcard==null)    wildcard = null;
        else                    wildcard = rhs.wildcard.copy();
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
