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
 * "NCName" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#NCName for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NcnameType extends TokenType {
    public static final NcnameType theInstance = new NcnameType("NCName");
    
    protected NcnameType( String typeName ) { super(typeName,false); }
    
    final public XSDatatype getBaseType() {
        return NameType.theInstance;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        if(XmlNames.isNCName(content))        return content;
        else                                return null;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
