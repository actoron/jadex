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
 * Base implementation of Matcher coordinator.
 * 
 * This class behaves as a parent of several other matchers, or as a composite
 * XPath matcher.
 * Those child matchers are not directly registered to IDConstraintChecker.
 * Instead, they receive notifications through this object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class MatcherBundle extends Matcher {
    
    /** child matchers. */
    protected Matcher[] children;
    /** depth. */
    private int depth = 0;
    protected final int getDepth() { return depth; }
    
    /**
     * the derived class must initialize the children field appropriately.
     */
    protected MatcherBundle( IDConstraintChecker owner ) {
        super(owner);
    }
    
    protected void startElement( String namespaceURI, String localName ) throws SAXException {
        
        depth++;
        for( int i=0; i<children.length; i++ )
            children[i].startElement(namespaceURI,localName);
    }
    
    protected void onAttribute( String namespaceURI, String localName, String value, Datatype type ) throws SAXException {
        for( int i=0; i<children.length; i++ )
            children[i].onAttribute(namespaceURI,localName,value,type);
    }
    
    protected void endElement( Datatype type ) throws SAXException {
        for( int i=0; i<children.length; i++ )
            children[i].endElement(type);
        if( depth-- == 0 ) {
            // traversal complete.
            owner.remove(this);
            onRemoved();
        }
    }

    protected void characters( char[] buf, int start, int len ) throws SAXException {
        for( int i=0; i<children.length; i++ )
            children[i].characters(buf,start,len);
    }
    
    /**
     * called when this bundle is deactivated.
     * This method is called by the endElement method when this bundle is
     * removed. A derived class can override this method to do whatever
     * necessary.
     */
    protected void onRemoved() throws SAXException {
    }
}
