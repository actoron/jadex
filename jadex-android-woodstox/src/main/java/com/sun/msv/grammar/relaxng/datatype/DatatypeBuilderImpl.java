/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relaxng.datatype;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * DataTypeBuilder implementation.
 * 
 * There is no paramater for any built-in and compatibility types.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class DatatypeBuilderImpl implements DatatypeBuilder {
    
    private final Datatype baseType;
    DatatypeBuilderImpl( Datatype baseType ) {
        this.baseType = baseType;
    }
    
    public Datatype createDatatype() {
        return baseType;
    }
    
    public void addParameter( String name, String value, ValidationContext context ) 
            throws DatatypeException {
        throw new DatatypeException(
            localize(ERR_PARAMETER_UNSUPPORTED,null));
    }


    protected String localize( String propertyName, Object[] args ) {
        String format = java.util.ResourceBundle.getBundle(
            "com.sun.msv.grammar.relaxng.Messages").getString(propertyName);
        
        return java.text.MessageFormat.format(format, args );
    }
    
    protected final static String ERR_PARAMETER_UNSUPPORTED = // arg:0
        "DataTypeBuilderImpl.ParameterUnsupported";
}
