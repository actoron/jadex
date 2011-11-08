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
import org.relaxng.datatype.DatatypeLibrary;

/**
 * RELAX NG built-in datatypes.
 * 
 * This implementation relies on Sun XML Datatypes Library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BuiltinDatatypeLibrary implements DatatypeLibrary {
    
    /**
     * the sole instance of this class.
     */
    public static final BuiltinDatatypeLibrary theInstance = new BuiltinDatatypeLibrary();
    
    protected BuiltinDatatypeLibrary() {}
    
    public Datatype createDatatype( String name ) throws DatatypeException {
        if( name.equals("string") )
            return com.sun.msv.datatype.xsd.StringType.theInstance;
        if( name.equals("token") )
            return com.sun.msv.datatype.xsd.TokenType.theInstance;
        throw new DatatypeException("undefined built-in type:"+name);
    }
    
    public DatatypeBuilder createDatatypeBuilder( String name ) throws DatatypeException {
        return new DatatypeBuilderImpl( createDatatype(name) );
    }
    
}
