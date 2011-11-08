/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

/**
 * represents an identity constraint.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IdentityConstraint implements java.io.Serializable {
    
    /**
     * selectors of the identity constraint.
     * each XPath separated by '|' will be treated as one entity.
     */
    public final XPath[] selectors;
    
    /** namespace URI of the identity constraint. */
    public final String namespaceURI;
    /** local name of the identity constraint. */
    public final String localName;
    
    /** fields of this constraint. */
    public final Field[] fields;
    
    public IdentityConstraint( String namespaceURI, String localName, XPath[] selectors, Field[] fields ) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.selectors = selectors;
        this.fields = fields;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
