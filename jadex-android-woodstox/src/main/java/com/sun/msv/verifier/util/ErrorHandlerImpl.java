/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * default implementation of ErrorHandler.
 * 
 * If an error is found, this implementation will throw it 
 * as an exception so that no further processing of the document will be performed.
 * 
 * <p>
 * All warnings are ignored.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ErrorHandlerImpl implements ErrorHandler {
    
    /**
     * the singleton instance of this object. This class doesn't have any
     * internal state so
     */
    public static final ErrorHandler theInstance = new ErrorHandlerImpl();
    
    public void fatalError( SAXParseException error ) throws SAXParseException {
        throw error; }
    public void error( SAXParseException error ) throws SAXParseException {
        throw error; }
    public void warning( SAXParseException warning ) {}
}
