/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.identity;

import org.relaxng.datatype.Datatype;
import org.xml.sax.SAXException;

/**
 * Base abstract implementation of XPath matcher.
 * 
 * XPath mathcer tracks the startElement event and the endElement event.
 * The characters event is also used by some derived classes.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class Matcher {
    
    protected final IDConstraintChecker owner;
    Matcher( IDConstraintChecker owner ) {
        this.owner = owner;
    }
    
    protected abstract void startElement( String namespaceURI, String localName ) throws SAXException;
    protected abstract void onAttribute( String namespaceURI, String localName, String value, Datatype type ) throws SAXException;
    protected abstract void endElement( Datatype type ) throws SAXException;
    
    protected void characters( char[] buf, int start, int len ) throws SAXException {
        // do nothing by default.
    }
}
