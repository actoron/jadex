/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;


/**
 * "token" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#token for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TokenType extends StringType {
    public static final TokenType theInstance = new TokenType("token",true);
    
    protected TokenType( String typeName, boolean isAlwaysValid ) {
        super(typeName,WhiteSpaceProcessor.theCollapse,isAlwaysValid);
    }
    
    public XSDatatype getBaseType() {
        return NormalizedStringType.theInstance;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
