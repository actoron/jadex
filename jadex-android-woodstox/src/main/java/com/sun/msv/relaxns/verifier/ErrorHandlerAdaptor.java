/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.verifier;

import org.iso_relax.dispatcher.Dispatcher;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * wraps ISORELAX ErrorHandler by VerificationErrorHandler interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ErrorHandlerAdaptor implements ErrorHandler
{
    private final Dispatcher core;
    
    public ErrorHandlerAdaptor( Dispatcher core ) {
        this.core = core;
    }
    
    public void fatalError( SAXParseException error ) throws SAXException {
        core.getErrorHandler().fatalError( error );
    }
    public void error( SAXParseException error ) throws SAXException {
        core.getErrorHandler().error( error );
    }
    public void warning( SAXParseException error ) throws SAXException {
        core.getErrorHandler().warning( error );
    }
}
