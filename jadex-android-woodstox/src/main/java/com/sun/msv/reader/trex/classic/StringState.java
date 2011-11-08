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

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.TokenType;
import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.util.StringPair;

/**
 * parses &lt;string&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringState extends ExpressionWithoutChildState
{
    protected final StringBuffer text = new StringBuffer();
    
    public void characters( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    
    public void ignorableWhitespace( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    
    protected Expression makeExpression() {
        if("preserve".equals(startTag.getAttribute("whiteSpace")))
            return reader.pool.createValue(
                StringType.theInstance,
                new StringPair("","string"),
                text.toString() );
        else
            return reader.pool.createValue(
                TokenType.theInstance,
                new StringPair("","token"),
                WhiteSpaceProcessor.collapse(text.toString()) );
        
        // masquerade RELAX NG built-in datatypes
    }
}
