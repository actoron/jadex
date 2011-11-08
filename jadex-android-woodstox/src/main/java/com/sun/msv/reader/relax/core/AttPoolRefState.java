/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;

/**
 * parses &lt;ref role="..." /&gt;.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttPoolRefState extends ExpressionWithoutChildState
{
    protected Expression makeExpression()
    {
        final String role = startTag.getAttribute("role");
        if( role==null )
        {
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "ref", "role" );
            return Expression.epsilon;
        }
        
        final String namespace = startTag.getAttribute("namespace");
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        
        return reader.resolveAttPoolRef(namespace,role);
    }
}
