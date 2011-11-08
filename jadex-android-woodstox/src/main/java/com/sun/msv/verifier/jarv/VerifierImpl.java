/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.jarv;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;

import com.sun.msv.verifier.IVerifier;

/**
 * Verifier implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class VerifierImpl extends org.iso_relax.verifier.impl.VerifierImpl
{
    private final IVerifier verifier;
    
    VerifierImpl( IVerifier verifier, XMLReader reader ) throws VerifierConfigurationException {
        this.verifier = verifier;
        super.reader    = reader;
    }
    
    // we obtain XMLReader through the constructor.
    protected void prepareXMLReader() {}
    
    
    public void setErrorHandler( ErrorHandler handler ) {
        super.setErrorHandler(handler);
        verifier.setErrorHandler(handler);
    }
    
    public VerifierHandler getVerifierHandler() {
        return verifier;
    }
}
