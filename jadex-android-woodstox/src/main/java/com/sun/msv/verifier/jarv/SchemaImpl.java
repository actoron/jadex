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

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.IVerifier;

/**
 * base implementation of RELAXFactoryImpl and TREXFactoryImpl
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaImpl implements Schema
{
    protected final Grammar grammar;
    protected final SAXParserFactory factory;
    
    protected SchemaImpl( Grammar grammar, SAXParserFactory factory,
        boolean _usePanicMode ) {
        
        this.grammar = grammar;
        this.factory = factory;
        this.usePanicMode = _usePanicMode;
    }
    
    public SchemaImpl( Grammar grammar ) {
        this.grammar = grammar;
        this.factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.usePanicMode = false;
    }
    
    public Verifier newVerifier() throws VerifierConfigurationException {
        IVerifier core = FactoryImpl.createVerifier(grammar);
        core.setPanicMode(usePanicMode);
        return new VerifierImpl( core, createXMLReader() );
    }
    
    private synchronized XMLReader createXMLReader() throws VerifierConfigurationException {
        // SAXParserFactory is not thread-safe. Thus we need to
        // synchronize this method.
        try {
            return factory.newSAXParser().getXMLReader();
        } catch( SAXException e ) {
            throw new VerifierConfigurationException(e);
        } catch( ParserConfigurationException e ) {
            throw new VerifierConfigurationException(e);
        }
    }
    
    private boolean usePanicMode;
}
