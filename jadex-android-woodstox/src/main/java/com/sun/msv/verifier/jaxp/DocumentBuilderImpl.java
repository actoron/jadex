/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.jaxp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javaxx.xml.parsers.DocumentBuilder;
import javaxx.xml.parsers.ParserConfigurationException;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DocumentBuilder implementation that supports validation.
 * 
 * <p>
 * This class uses another DocumentBuilder implementation and 
 * adds the validation capability to it.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class DocumentBuilderImpl extends DocumentBuilder
{
    /**
     * Wrapped DocumentBuilder that does everything else.
     */
    private final DocumentBuilder core;

    /**
     * The validation will be performed using this verifier.
     */
    private final Verifier verifier;
    
    DocumentBuilderImpl( DocumentBuilder _core, Schema _schema ) throws ParserConfigurationException {
        this.core = _core;
        try {
            verifier = _schema.newVerifier();
        } catch( Exception e ) {
            // this will not happen with our implementation of JARV.
            throw new ParserConfigurationException(e.toString());
        }
        // set an error handler to throw an exception in case of error.
        verifier.setErrorHandler( com.sun.msv.verifier.util.ErrorHandlerImpl.theInstance );
    }
    
    
    public DOMImplementation getDOMImplementation() {
        return core.getDOMImplementation();
    }
    
    public boolean isNamespaceAware() {
        return core.isNamespaceAware();
    }
    
    public boolean isValidating() {
        return true;
    }
    
    public Document newDocument() {
        return core.newDocument();
    }
    
    public Document parse( InputSource is ) throws SAXException, IOException {
        return verify(core.parse(is));
    }
    
    public Document parse( File f ) throws SAXException, IOException {
        return verify(core.parse(f));
    }
    
    public Document parse( InputStream is ) throws SAXException, IOException {
        return verify(core.parse(is));
    }
    
    public Document parse( InputStream is, String systemId ) throws SAXException, IOException {
        return verify(core.parse(is,systemId));
    }
    
    public Document parse( String url ) throws SAXException, IOException {
        return verify(core.parse(url));
    }
    
    public void setEntityResolver( EntityResolver resolver ) {
        verifier.setEntityResolver(resolver);
        core.setEntityResolver(resolver);
    }
    
    public void setErrorHandler( ErrorHandler handler ) {
        verifier.setErrorHandler(handler);
        core.setErrorHandler(handler);
    }
    
    
    
    
    /**
     * Validates a given DOM and returns it if it is valid. Otherwise throw an exception.
     */
    private Document verify( Document dom ) throws SAXException, IOException {
        if(verifier.verify(dom))
            return dom;    // the document is valid
        
        // this is strange because if any error happens, the error handler
        // will throw an exception.
        throw new SAXException("the document is invalid");
    }
}
