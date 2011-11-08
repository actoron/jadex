/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends NameClassAndExpressionState
{
    protected boolean firstChild=true;
    
    protected Expression initialExpression() {
        // <attribute> defaults to <anyString />
        return Expression.anyString;
    }

    protected String getNamespace() {
        final String ns = startTag.getAttribute("ns");
        final boolean global = "true".equals(startTag.getAttribute("global"));
        
        if( ns!=null )    return ns;    // "ns" attribute always has precedence.
        
        // if global="true" is specified, it defaults to propagated ns attribute.
        if( global )    return ((TREXBaseReader)reader).targetNamespace;
        
        // otherwise, it defaults to ""
        return "";
    }
            

    protected Expression castExpression( Expression initialExpression, Expression newChild ) {
        // <attribute> is allowed to have only one pattern
        if(!firstChild)
            reader.reportError( TREXBaseReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
            // recover by ignore the error
        firstChild = false;
        return newChild;
    }

    protected Expression annealExpression( Expression contentModel ) {
        Expression e = reader.pool.createAttribute( nameClass, contentModel, null );
        if(e instanceof AttributeExp) {
            reader.setDeclaredLocationOf(e);
        }
        return e;
    }
}
