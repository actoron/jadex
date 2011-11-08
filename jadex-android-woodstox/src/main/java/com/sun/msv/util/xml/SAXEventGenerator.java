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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

/**
 * produces SAX2 event from a DOM tree.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SAXEventGenerator {
    
    /**
     * scans the specified DOM and sends SAX2 events to the handler.
     */
    public static void parse( Document dom, final ContentHandler handler ) throws SAXException {
        
        DOMVisitor visitor = new DOMVisitor(){
            public void visit( Element e ) {
                int attLen = e.getAttributes().getLength();
                AttributesImpl atts = new AttributesImpl();
                for( int i=0; i<attLen; i++ ) {
                    Attr a = (Attr)e.getAttributes().item(i);
                    
                    String uri = a.getNamespaceURI();
                    String local = a.getLocalName();
                    if(uri==null)    uri="";
                    if(local==null)    local=a.getName();
                    
                    atts.addAttribute( uri,local,
                        a.getName(), null/*no type available*/, a.getValue() );
                }
                
                try {
                    String uri = e.getNamespaceURI();
                    String local = e.getLocalName();
                    if(uri==null)    uri="";
                    if(local==null)    local=e.getNodeName();
                    
                    handler.startElement( uri, local, e.getNodeName(), atts );
                    super.visit(e);
                    handler.endElement( uri, local, e.getNodeName() );
                } catch( SAXException x ) {
                    throw new SAXWrapper(x);
                }
            }
            
            public void visitNode( Node n ) {
                if( n.getNodeType()==Node.TEXT_NODE
                ||  n.getNodeType()==Node.CDATA_SECTION_NODE ) {
                    String text = n.getNodeValue();
                    try {
                        handler.characters( text.toCharArray(), 0, text.length() );
                    } catch( SAXException x ) {
                        throw new SAXWrapper(x);
                    }
                }
                super.visitNode(n);
            }
        };
        
        // set a dummy locator. We cannot provide location information.
        handler.setDocumentLocator( new LocatorImpl() );
        handler.startDocument();
        try {
            visitor.visit(dom);
        } catch( SAXWrapper w ) {
            throw w.e;
        }
        handler.endDocument();
    }
    
    // wrap SAXException into a RuntimeException so that
    // exception can pass through DOMVisitor.
    @SuppressWarnings("serial")
    private static class SAXWrapper extends RuntimeException {
        SAXWrapper( SAXException e ) { this.e=e; }
        SAXException e;
    }
}
