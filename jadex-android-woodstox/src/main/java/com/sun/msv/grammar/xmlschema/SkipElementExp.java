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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;

/**
 * ElementExp that is used for &lt;any processContents="skip"/&gt;.
 * 
 * This is kept in the separate class so that the wildcard element
 * can be easily distinguished by the application program.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SkipElementExp extends com.sun.msv.grammar.trex.ElementPattern {
    
    public SkipElementExp( NameClass nameClass, Expression contentModel ) {
        super(nameClass,contentModel);
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
