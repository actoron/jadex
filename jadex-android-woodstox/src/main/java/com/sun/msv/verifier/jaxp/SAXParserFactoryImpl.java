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

import java.io.IOException;

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParser;
import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sun.msv.verifier.jarv.TheFactoryImpl;

/**
 * SAXParserFactory implementation that supports validation.
 * 
 * <p>
 * This class uses another SAXParserFactory implementation and 
 * adds the validation capability to it.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SAXParserFactoryImpl extends SAXParserFactory
{
    /** Actual SAXParserFactory implementation. */
    private final SAXParserFactory core;
    
    /** JARV VerifierFactory implementation, which will be used to parse schemas. */
    private final VerifierFactory jarvFactory;
    
    /**
     * JARV Schema object which is associated with this factory, or null.
     */
    private Schema schema;
    
    /**
     * Creates a new instance by using the default SAXParserFactory implementation
     * as the underlying parser. This constructor does not set any schema.
     */
    public SAXParserFactoryImpl() {
        this( SAXParserFactory.newInstance() );
    }
    
    /**
     * Creates a new instance by specifying the underlying SAXParserFactory
     * implementation. This constructor does not set any schema.
     */
    public SAXParserFactoryImpl( SAXParserFactory _factory ) {
        this(_factory,null);
    }
    
    /**
     * Creates a new instance by using a default SAXParserFactory implementation
     * and the specified schema object.
     */
    public SAXParserFactoryImpl( Schema schema ) {
        this( SAXParserFactory.newInstance(), schema );
    }
    
    /**
     * Creates a new instance that validates documents against the specified schema.
     */
    public SAXParserFactoryImpl( java.io.File schemaAsFile )
            throws VerifierConfigurationException, SAXException, IOException {
        this();
        schema = jarvFactory.compileSchema(schemaAsFile);
    }

    /**
     * Creates a new instance that validates documents against the specified schema.
     */
    public SAXParserFactoryImpl( org.xml.sax.InputSource _schema )
            throws VerifierConfigurationException, SAXException, IOException {
        this();
        schema = jarvFactory.compileSchema(_schema);
    }

    /**
     * Creates a new instance that validates documents against the specified schema.
     */
    public SAXParserFactoryImpl( String schemaUrl )
            throws VerifierConfigurationException, SAXException, IOException {
        this();
        schema = jarvFactory.compileSchema(schemaUrl);
    }
    
    public SAXParserFactoryImpl( SAXParserFactory _factory, Schema _schema ) {
        this.core = _factory;
        // to make this factory work with JARV, configure it to namespace aware.
        core.setNamespaceAware(true);
        jarvFactory = new TheFactoryImpl(core);
        this.schema = _schema;
    }
    
    
    
    
    
    public boolean getFeature( String name ) throws ParserConfigurationException,
                                                    SAXNotRecognizedException,
                                                    SAXNotSupportedException {
        if( name.equals(com.sun.msv.verifier.jarv.Const.PANIC_MODE_FEATURE) )
            return jarvFactory.isFeature(name);
        return core.getFeature(name);
    }
    
    public void setFeature( String name, boolean value )
        throws ParserConfigurationException, SAXNotRecognizedException,    SAXNotSupportedException {
        
        if( name.equals(com.sun.msv.verifier.jarv.Const.PANIC_MODE_FEATURE) )
            jarvFactory.setFeature(name,value);
        core.setFeature(name,value);
    }
    
    public SAXParser newSAXParser()
        throws ParserConfigurationException, SAXException {
        
        try {
            return new SAXParserImpl(core.newSAXParser(),jarvFactory,
                (schema==null)?null:schema.newVerifier() );
        } catch( VerifierConfigurationException e ) {
            throw new SAXException(e);
        }
    }
    
    
    public void setNamespaceAware( boolean awareness ) {
        core.setNamespaceAware(awareness);
    }
    
    public boolean isNamespaceAware() {
        return core.isNamespaceAware();
    }
    
    public void setValidating( boolean validating ) {
        core.setValidating(validating);
    }
    
    public boolean isValidating() {
        return core.isValidating();
    }
}
