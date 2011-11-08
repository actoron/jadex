/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex.typed;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.trex.ElementPattern;

/**
 * ElementPattern with type.
 * 
 * Proprietary extension by MSV to support type-assignment in TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypedElementPattern extends ElementPattern {
    /** label of this element. */
    public final String label;
    
    public TypedElementPattern( NameClass nameClass, Expression contentModel, String label ) {
        super(nameClass,contentModel);
        this.label = label;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
