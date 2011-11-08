/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.util.StringPair;

/**
 * parses &lt;value&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValueState extends ExpressionWithoutChildState {
    
    protected final StringBuffer text = new StringBuffer();
    
    public void characters( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    public void ignorableWhitespace( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    
    protected Expression makeExpression() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        String typeName = startTag.getCollapsedAttribute("type");
        
        Datatype type;
        
        StringPair typeFullName;
        
        if(typeName==null) {
            try {
                // defaults to built-in token type.
                type = reader.resolveDataTypeLibrary("").createDatatype("token");
                typeFullName = new StringPair("","token");
            } catch( DatatypeException e ) {
                // since token is the built-in datatype,
                // this can't happen
                e.printStackTrace();
                throw new InternalError();
            }
        } else {
            type = reader.resolveDataType(typeName);
            typeFullName = new StringPair(reader.datatypeLibURI,typeName);
        }
        
        Object value = type.createValue(text.toString(),reader);
        if( value==null ) {
            // this is not a good value for this type.
            reader.reportError( RELAXNGReader.ERR_BAD_DATA_VALUE, typeName, text.toString().trim() );
            return Expression.nullSet;    // recover by returning something.
        }
        
        return reader.pool.createValue( type, typeFullName, value );
    }
}
