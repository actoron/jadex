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
import com.sun.msv.reader.ExpressionWithChildState;

/**
 * parses &lt;mixed&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MixedState extends ExpressionWithChildState
{
    protected Expression castExpression( Expression current, Expression child )
    {
        if( current!=null )
        {// mixed has more than one child.
            reader.reportError( RELAXCoreReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
            // recover by ignoring previous expression
        }
        return child;
    }
    protected Expression annealExpression( Expression exp )
    {
        return reader.pool.createMixed(exp);
    }
}
