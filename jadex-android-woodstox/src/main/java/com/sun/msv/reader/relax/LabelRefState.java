/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.reader.GrammarReader;

/**
 * base implementation of HedgeRefState and ElementRefState.
 * 
 * this class resolves namespace attribute and label attribute into the actual
 * ReferenceExp object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class LabelRefState extends ExpressionWithoutChildState
{
    protected Expression makeExpression()
    {
        final String label = startTag.getAttribute("label");
        final String namespace = startTag.getAttribute("namespace");
        final RELAXReader reader = (RELAXReader)this.reader;
        
        if(label==null)
        {// label attribute is required.
            reader.reportError( GrammarReader.ERR_MISSING_ATTRIBUTE,
                startTag.localName,"label");
            // recover by returning something that can be interpreted as Pattern
            return Expression.nullSet;
        }
        
        return resolve(namespace,label);
    }
    
    /** gets or creates appropriate reference */
    protected abstract Expression resolve( String namespace, String label );
}
