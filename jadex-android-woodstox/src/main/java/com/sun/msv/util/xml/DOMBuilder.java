/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util.xml;

import javaxx.xml.parsers.DocumentBuilderFactory;
import javaxx.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * builds DOM from SAX2 event stream.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DOMBuilder extends DefaultHandler {
    
    private final Document dom;
    private Node parent;
    
    public DOMBuilder( Document document ) {
        this.dom = document;
        parent = dom;
    }
    
    public DOMBuilder() throws ParserConfigurationException {
        this( DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() );
    }
    
    /**
     * returns DOM. This method should be called after the parsing was completed.
     */
    public Document getDocument() {
        return dom;
    }
    
    public void startElement( String ns, String local, String qname, Attributes atts ) {
        Element e = dom.createElementNS( ns, qname );
        parent.appendChild(e);
        parent = e;
        
        for( int i=0; i<atts.getLength(); i++ )
            e.setAttributeNS( atts.getURI(i), atts.getQName(i), atts.getValue(i) );
    }
    
    public void endElement( String ns, String local, String qname ) {
        parent = parent.getParentNode();
    }
    
    public void characters( char[] buf, int start, int len ) {
        parent.appendChild( dom.createTextNode(new String(buf,start,len)) );
    }
    
    public void ignorableWhitespace( char[] buf, int start, int len ) {
        parent.appendChild( dom.createTextNode(new String(buf,start,len)) );
    }
}
