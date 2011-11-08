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

import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.InputSource;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.GrammarLoader;

/**
 * VerifierFactory implementation that automatically detects the schema language.
 * 
 * To use this class, see 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TheFactoryImpl extends FactoryImpl {
    public TheFactoryImpl(SAXParserFactory factory) {
        super(factory);
    }

    /**
     * use the default SAXParser.
     */
    public TheFactoryImpl() {
        super();
    }

    protected Grammar parse(InputSource is, GrammarReaderController controller) throws VerifierConfigurationException {
        try {
            return GrammarLoader.loadSchema(is, controller, factory);
        } catch (Exception e) {
            throw new VerifierConfigurationException(e);
        }
    }

    protected Grammar parse(String source, GrammarReaderController controller) throws VerifierConfigurationException {
        try {
            return GrammarLoader.loadSchema(source, controller, factory);
        } catch (Exception e) {
            throw new VerifierConfigurationException(e);
        }
    }
}
