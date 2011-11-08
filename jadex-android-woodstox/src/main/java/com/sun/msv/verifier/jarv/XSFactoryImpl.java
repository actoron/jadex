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

import org.xml.sax.InputSource;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;

/**
 * VerifierFactory implementation for XML Schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XSFactoryImpl extends FactoryImpl {

    protected Grammar parse( InputSource is, GrammarReaderController controller ) {
        return XMLSchemaReader.parse(is,factory,controller);
    }
}
