/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

import org.xml.sax.Attributes;

/**
 * state that ignores the entire subtree.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IgnoreState extends State {
    // the parent state receives startElement event for this state.
    // so we have to start from one, rather than zero.
    private int depth = 1;

    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        depth++;
    }
    
    public final void endElement(String namespaceURI, String localName, String qName) {
        depth--;
        if (depth == 0)
            reader.popState();
    }
    
    public final void endDocument() {
        // assert depth==0
        reader.popState();
    }
    
    public void characters(char[] buffer, int from, int len) {
        // ignore literals
    }

}
    
    
