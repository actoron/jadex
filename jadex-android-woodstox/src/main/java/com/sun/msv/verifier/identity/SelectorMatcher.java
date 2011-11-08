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

import com.sun.msv.grammar.xmlschema.IdentityConstraint;

/**
 * XPath matcher that tests the selector of an identity constraint.
 * 
 * This object is created whenever an element with identity constraints is found.
 * XML Schema guarantees that we can see if an element has id constraints at the
 * startElement method.
 * 
 * This mathcer then monitor startElement/endElement and find matches to the
 * specified XPath. Every time it finds a match ("target node" in XML Schema
 * terminology), it creates a FieldsMatcher.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SelectorMatcher extends PathMatcher {
    
    protected IdentityConstraint idConst;

    SelectorMatcher(
                IDConstraintChecker owner, IdentityConstraint idConst,
                String namespaceURI, String localName ) throws SAXException {
        super(owner, idConst.selectors );
        this.idConst = idConst;
        
        // register this scope as active.
        owner.pushActiveScope(idConst,this);
        

        super.start(namespaceURI,localName);
    }

    protected void onRemoved() throws SAXException {
        super.onRemoved();
        // this scope is no longer active.
        owner.popActiveScope(idConst,this);
    }

    
    protected void onElementMatched( String namespaceURI, String localName ) throws SAXException {
            
        // this element matches the path.
        owner.add( new FieldsMatcher(this, namespaceURI,localName) );
    }
    
    protected void onAttributeMatched(
        String namespaceURI, String localName, String value, Datatype type ) {
        
        // assertion failed:
        // selectors cannot contain attribute steps.
        throw new Error();
    }
    
}
