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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;

/**
 * Simple type declaration.
 * 
 * <p>
 * Most of the properties of the simple type declaration component
 * is defined in the {@link XSDatatype} object, which is obtained by the
 * {@link #getType()} method.
 * 
 * <p>
 * Note: XML Schema allows forward reference to simple types.
 * Therefore it must be indirectionalized by ReferenceExp.
 * And this is the only reason this class exists.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeExp extends XMLSchemaTypeExp {
    
    SimpleTypeExp( String typeName ) {
        super(typeName);
    }
    
    public void set( XSDatatypeExp exp ) {
        this.exp = this.type = exp;
    }
    
    protected XSDatatypeExp type;
    /** gets the XSDatatypeExp object that represents this simple type. */
    public XSDatatypeExp getType() { return type; }

    /**
     * Gets the encapsulated Datatype object.
     * <p>
     * This method can be called only after the parsing is finished.
     */
    public XSDatatype getDatatype() { return type.getCreatedType(); }
    
    
    /**
     * gets the value of the block constraint.
     * SimpleTypeExp always returns 0 because it doesn't
     * have the block constraint.
     */
    public int getBlock() { return 0; }
    
    /** clone this object. */
    public RedefinableExp getClone() {
        SimpleTypeExp exp = new SimpleTypeExp(this.name);
        exp.redefine(this);
        return exp;
    }
    
    public void redefine( RedefinableExp _rhs ) {
        super.redefine(_rhs);
        
        SimpleTypeExp rhs = (SimpleTypeExp)_rhs;
        
        if(type==null)
            type = rhs.getType().getClone();
        else {
            // because redefinition only happens by a defined declaration
            if(rhs.getType()==null)
                throw new InternalError();
                
            type.redefine(rhs.getType());
        }
    }
    
    
    // serialization support
    private static final long serialVersionUID = 1;    

}
