/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import com.sun.msv.datatype.xsd.StringType;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.ValidationContext;

/**
 * Dummy <code>DatatypeLibrary</code> implementation which is used
 * to recover from "unknown datatype library" error.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ErrorDatatypeLibrary
        implements DatatypeLibrary, DatatypeBuilder {
    
    /** The sole instance of this class. */
    public static final ErrorDatatypeLibrary theInstance = new ErrorDatatypeLibrary();
    
    private ErrorDatatypeLibrary() {}
    
    public Datatype createDatatype( String name ) {
        return StringType.theInstance;
    }
    public DatatypeBuilder createDatatypeBuilder( String name ) {
        return this;
    }
    
    public Datatype createDatatype() {
        return StringType.theInstance;
    }
    public void addParameter( String name, String value, ValidationContext context ) {
    }
    
    
}
