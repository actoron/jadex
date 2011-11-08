/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Contains information about where and how validity violation was happened.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class ValidityViolation extends SAXParseException {
    
    private ErrorInfo errorInfo;
    
    /**
     * Gets the detailed error information, if any.
     * 
     * If there is no detailed information available, it returns null.
     * Otherwise one of the derived classes of ErrorInfo will be returned.
     */
    public ErrorInfo getErrorInfo() { return errorInfo;    }
    
    public ValidityViolation( Locator loc, String msg, ErrorInfo ei ) {
        super( msg, loc );
        this.errorInfo = ei;
    }
/*
    public ValidityViolation( Locator loc, String msg ) {
        this( msg, loc, null );
    }
*/
}
