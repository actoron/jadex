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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.xmlschema.KeyConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;

/**
 * Coordinator of FieldMatcher.
 * 
 * This object is created when SelectorMatcher finds a match.
 * This object then creates FieldMatcher for each field, and
 * let them find their field matchs.
 * When leaving the element that matched the selector, it collects
 * field values and registers a key value to IDConstraintChecker.
 * 
 * <p>
 * Depending on the type of the constraint, it works differently.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldsMatcher extends MatcherBundle {
    
    /**
     * location of the start tag.
     * It is usually preferable as a source of error.
     */
    protected final Locator startTag;
    
    /**
     * the parent SelectorMatcher.
     */
    protected final SelectorMatcher selector;
    
    protected FieldsMatcher( SelectorMatcher selector, String namespaceURI, String localName ) throws SAXException {
        super(selector.owner);
        
        this.selector = selector;
        if(owner.getLocator()==null)
            this.startTag = null;
        else
            this.startTag = new LocatorImpl(owner.getLocator());
        
        children = new Matcher[selector.idConst.fields.length];
        for( int i=0; i<selector.idConst.fields.length; i++ )
            children[i] = new FieldMatcher(
                this,selector.idConst.fields[i], namespaceURI,localName);
    }
    
    protected void onRemoved() throws SAXException {
        Object[] values = new Object[children.length];
            
        // copy matched values into "values" variable,
        // while checking any unmatched fields.
        for( int i=0; i<children.length; i++ )
            if( (values[i]=((FieldMatcher)children[i]).value) == null ) {
                if(!(selector.idConst instanceof KeyConstraint))
                    // some fields didn't match to anything.
                    // In case of KeyRef and Unique constraints,
                    // we can ignore this node.
                    return;
                    
                // if this is the key constraint, it is an error
                owner.reportError(
                    startTag, null, 
                    IDConstraintChecker.ERR_UNMATCHED_KEY_FIELD,
                    new Object[]{
                        selector.idConst.namespaceURI,
                        selector.idConst.localName,
                        new Integer(i+1)} );
                return;
            }

        
        KeyValue kv = new KeyValue(values,startTag);
        if(owner.addKeyValue( selector, kv ))
            return;
        
        // the same value already exists.
        
        if( selector.idConst instanceof KeyRefConstraint )
            // multiple reference to the same key value.
            // not a problem.
            return;
        
        // find a value that collides with kv
        Object[] items = owner.getKeyValues(selector);
        int i;
        for( i=0; i<values.length; i++ )
            if( items[i].equals(kv) )
                break;
        
        // violates uniqueness constraint.
        // this set already has this value.
        owner.reportError(
            startTag, null,
            IDConstraintChecker.ERR_NOT_UNIQUE,
            new Object[]{
                selector.idConst.namespaceURI, selector.idConst.localName} );
        owner.reportError(
            ((KeyValue)items[i]).locator, null,
            IDConstraintChecker.ERR_NOT_UNIQUE_DIAG,
            new Object[]{
                selector.idConst.namespaceURI, selector.idConst.localName} );
    }
    
}
