/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;

/**
 * &lt;element&gt; pattern of TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementPattern extends ElementExp {
    public final NameClass nameClass;
    public final NameClass getNameClass() { return nameClass; }
    
    public ElementPattern( NameClass nameClass, Expression contentModel ) {
        super(contentModel,false);
        this.nameClass = nameClass;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
