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
import java.io.InputStream;

import javaxx.xml.parsers.SAXParser;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

/**
 * SAXParser implementation that supports validation.
 * 
 * <p>
 * This class uses another SAXParser implementation and 
 * adds the validation capability to it.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class SAXParserImpl extends SAXParser
{
    /** The underlying SAX parser. */
    private final SAXParser core;
    /**
     * JARV verifier object that performs the validation for this SAXParserImpl.
     * This field is null when no schema is set.
     */
    private Verifier verifier;
    /** A reference to VerifierFactory that can be used to parse a schema. */
    private final VerifierFactory factory;
    
    
    SAXParserImpl( SAXParser core, VerifierFactory _jarvFactory, Verifier _verifier ) {
        this.core = core;
        this.factory = _jarvFactory;
        this.verifier = _verifier;
    }
    
        /** @deprecated */
    public org.xml.sax.Parser getParser() throws SAXException {
        // maybe we should throw an UnsupportedOperationException,
        // rather than doing a trick like this.
        return new XMLReaderAdapter(getXMLReader());
    }
    
    public Object getProperty( String name )
        throws SAXNotRecognizedException, SAXNotSupportedException {
        
        return core.getProperty(name);
    }
    
    public void setProperty( String name, Object value )
        throws SAXNotRecognizedException, SAXNotSupportedException {
        
        if( Const.SCHEMA_PROPNAME.equals(name) ) {
            try {
                if(value instanceof String) {
                    verifier = factory.newVerifier( (String)value );
                    return;
                }
                if(value instanceof File) {
                    verifier = factory.newVerifier( (File)value );
                    return;
                }
                if(value instanceof InputSource) {
                    verifier = factory.newVerifier( (InputSource)value );
                    return;
                }
                if(value instanceof InputStream) {
                    verifier = factory.newVerifier( (InputStream)value );
                    return;
                }
                if(value instanceof Schema) {
                    verifier = ((Schema)value).newVerifier();
                    return;
                }
                throw new SAXNotSupportedException("unrecognized value type: "+value.getClass().getName() );
            } catch( Exception e ) {
                // TODO: what is the correct exception type?
                throw new SAXNotRecognizedException(e.toString());
            }
        }
        
        core.setProperty(name,value);
    }
    
    public XMLReader getXMLReader() throws SAXException {
        XMLReader reader = core.getXMLReader();
        if(verifier==null)    return reader;    // no validation is necessary.
        
        XMLFilter filter = verifier.getVerifierFilter();
        filter.setParent(reader);
        return filter;
    }
    
    public boolean isNamespaceAware() {
        return core.isNamespaceAware();
    }
    
    public boolean isValidating() {
        return core.isValidating();
    }
}
