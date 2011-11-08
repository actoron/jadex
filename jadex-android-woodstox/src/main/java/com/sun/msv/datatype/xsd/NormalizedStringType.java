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
 * "normalizedString" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#normalizedString for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NormalizedStringType extends StringType {
    public static final NormalizedStringType theInstance =
        new NormalizedStringType("normalizedString",true);
    
    protected NormalizedStringType( String typeName, boolean isAlwaysValid ) {
        super(typeName, WhiteSpaceProcessor.theReplace, isAlwaysValid);
    }
    
    public XSDatatype getBaseType() {
        return StringType.theInstance;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
