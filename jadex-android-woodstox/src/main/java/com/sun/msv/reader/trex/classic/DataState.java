/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.classic;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.util.StringPair;

/**
 * parses &lt;data&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataState extends ExpressionWithoutChildState {
    
    protected Expression makeExpression() {
        final String typeName = startTag.getAttribute("type");
        if( typeName==null ) {
            // type attribute is required
            reader.reportError( TREXGrammarReader.ERR_MISSING_ATTRIBUTE,
                startTag.qName, "type" );
            
            // recover from error by assuming anyString.
            return Expression.anyString;
        } else {
            return reader.pool.createData(
                ((TREXGrammarReader)reader).resolveDatatype(typeName),
                new StringPair("",typeName) );
        }
    }
}
