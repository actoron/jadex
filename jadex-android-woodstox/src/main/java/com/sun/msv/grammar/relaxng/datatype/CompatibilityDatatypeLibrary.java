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

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.IDREFType;
import com.sun.msv.datatype.xsd.IDType;

/**
 * RELAX NG DTD compatibility datatype library.
 * 
 * This implementation relies on Sun XML Datatypes Library.
 * Compatibility datatypes library available through
 * <code>http://relaxng.org/ns/compatibility/datatypes/1.0</code>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CompatibilityDatatypeLibrary implements DatatypeLibrary {
    
    /** namespace URI of the compatibility datatypes library. */
    public static final String namespaceURI = "http://relaxng.org/ns/compatibility/datatypes/1.0";
    
    public Datatype createDatatype( String name ) throws DatatypeException {
        if( name.equals("ID") )
            return IDType.theInstance;
        if( name.equals("IDREF") )
            return IDREFType.theInstance;
        if( name.equals("IDREFS") )
            return DatatypeFactory.getTypeByName("IDREFS");
        
        throw new DatatypeException("undefined built-in type:"+name);
    }
    
    public DatatypeBuilder createDatatypeBuilder( String name ) throws DatatypeException {
        return new DatatypeBuilderImpl( createDatatype(name) );
    }
}
