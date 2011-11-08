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
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.util.Util;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class DTDFactoryImpl extends FactoryImpl {
    public DTDFactoryImpl(SAXParserFactory factory) {
        super(factory);
    }

    /**
     * use the default SAXParser.
     */
    public DTDFactoryImpl() {
        super();
    }

    protected Grammar parse(InputSource is, GrammarReaderController controller) throws VerifierConfigurationException {
        return DTDReader.parse(is,controller);
    }

    protected Grammar parse(String source, GrammarReaderController controller) throws VerifierConfigurationException {
        return DTDReader.parse(Util.getInputSource(source),controller);
    }
}
