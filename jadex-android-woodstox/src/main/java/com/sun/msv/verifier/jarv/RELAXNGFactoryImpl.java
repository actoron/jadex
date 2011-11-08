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

import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.trex.ng.RELAXNGReader;

/**
 * VerifierFactory implementation of RELAX NG.
 * 
 * This implementation supports the "datatypeLibraryFactory" property
 * which configures RELAX NG parser with a datatype library factory.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGFactoryImpl extends FactoryImpl {
    
    private DatatypeLibraryFactory datatypeLibraryFactory = null;
    
    private static final String PROP_NAME = "datatypeLibraryFactory";
    
    protected Grammar parse( InputSource is, GrammarReaderController controller ) {
        RELAXNGReader reader = new RELAXNGReader(controller,factory);
        if( datatypeLibraryFactory!=null )
            reader.setDatatypeLibraryFactory(datatypeLibraryFactory);
        reader.parse(is);
        
        return reader.getResult();
    }
    
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals(PROP_NAME) )
            return datatypeLibraryFactory;
        return super.getProperty(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals(PROP_NAME) ) {
            datatypeLibraryFactory = (DatatypeLibraryFactory)value;
            return;
        }
        super.setProperty(name, value);
    }

}
