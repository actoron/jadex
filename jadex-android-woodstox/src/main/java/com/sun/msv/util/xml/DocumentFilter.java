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

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * SAX DocumentHandler event interceptor.
 * 
 * This object acts as a filter to DocumentHandler events.
 * Derived class should override methods of interest and
 * perform somethings.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("deprecation")
public class DocumentFilter implements DocumentHandler
{
    public DocumentHandler next;
    
    public DocumentFilter( DocumentHandler next ) {
        this.next = next;
    }
    
    public void startDocument() throws SAXException {
        next.startDocument();
    }
    public void endDocument() throws SAXException {
        next.endDocument();
    }
    public void startElement( String name, AttributeList atts ) throws SAXException {
        next.startElement(name,atts);
    }
    public void endElement( String name ) throws SAXException {
        next.endElement(name);
    }
    public void characters( char[] buf, int start, int len ) throws SAXException {
        next.characters(buf,start,len);
    }
    public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
        next.ignorableWhitespace(buf,start,len);
    }
    public void processingInstruction( String target, String data ) throws SAXException {
        next.processingInstruction(target,data);
    }
    public void setDocumentLocator( Locator loc ) {
        next.setDocumentLocator(loc);
    }
}
