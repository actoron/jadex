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

import org.relaxng.datatype.ValidationContext;

/**
 * "NMTOKEN" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#NMTOKEN for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NmtokenType extends TokenType {
    public static final NmtokenType theInstance = new NmtokenType("NMTOKEN");
    
    protected NmtokenType(String typeName) { super(typeName,false); }
    
    final public XSDatatype getBaseType() {
        return TokenType.theInstance;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        if(XmlNames.isNmtoken(content))        return content;
        else                                return null;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
