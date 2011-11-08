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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * visits all DOM elements in the depth-first order (in-order).
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class DOMVisitor
{
    public void visit( Document dom ) {
        visit( dom.getDocumentElement() );
    }
    
    public void visit( Element e ) {
        NodeList lst = e.getChildNodes();
        int len = lst.getLength();
        for( int i=0; i<len; i++ ) {
            Node n = lst.item(i);
            if( n.getNodeType() == Node.ELEMENT_NODE )
                visit( (Element)n );
            else
                visitNode( n );
        }
    }
    
    /**
     * other nodes.
     */
    public void visitNode( Node n ) {}
}
