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

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ReferenceExp;

/**
 * global attribute declaration.
 * 
 * The exp field of this object is not much useful.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeDeclExp extends ReferenceExp {
    
    /**
     * name of this attribute declaration.
     * According to the spec, the name must be unique within one schema
     * (in our object model, one XMLSchemaSchema object).
     */
    public AttributeDeclExp( String typeLocalName ) {
        super(typeLocalName);
    }
    
    /**
     * actual definition. This expression contains meaningful information.
     */
    public AttributeExp self;
    
    public void set( AttributeExp exp ) {
        self = exp;
        this.exp = self;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
