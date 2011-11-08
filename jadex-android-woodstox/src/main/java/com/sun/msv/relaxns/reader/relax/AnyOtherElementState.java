/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader.relax;

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.relaxns.grammar.relax.AnyOtherElementExp;

/**
 * parses &lt;anyOtherElement&gt; state.
 * 
 * To create an expression that implements the semantics of anyOtherElement,
 * the entire grammar must be parsed first.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyOtherElementState extends ExpressionWithoutChildState
{
    protected Expression makeExpression() {
        // when makeExpression is called, return only a skelton.
        // later, after the entire grammar is parsed, we'll provide
        // actual expression.
        
        String in = startTag.getAttribute("includeNamespace");
        String ex = startTag.getAttribute("excludeNamespace");

        if( in!=null && ex!=null ) {
            reader.reportError(
                new Locator[]{this.location},
                RELAXCoreIslandSchemaReader.ERR_CONFLICTING_ATTRIBUTES,
                new Object[]{"includeNamespace", "excludeNamespace"} );
            ex=null;
        }
        
        if( in==null && ex==null )
            ex="";    // this will correctly implement the semantics.
        
        final AnyOtherElementExp exp = new AnyOtherElementExp( this.location, in, ex );
        ((RELAXCoreIslandSchemaReader)reader).pendingAnyOtherElements.add(exp);
        return exp;
    }
    
}
