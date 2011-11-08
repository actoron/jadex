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
 * do-nothing implementation of ErrorHandler.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IgnoreErrorHandler implements ErrorHandler
{
    public void fatalError( SAXParseException e ) throws SAXParseException { throw e; }
    public void error( SAXParseException error ) {}
    public void warning( SAXParseException warning ) {}
}
