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
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.msv.util.StartTagInfo;

/**
 * base interface of the most of parsing states.
 * 
 * <p>
 * In this level of inheritance, contract is as follows.
 * 
 * <ol>
 *  <li>startElement(<x>) event is received by the parent state.
 *        It usually creates a child state by this event.
 * 
 *  <li>startSelf method of the child SimpleState is called.
 *        derived classes should perform necessary things
 *        by reading start tag information.
 * 
 *  <li>Whenever startElement method is received by
 *        SimpleState object, createChildState method is
 *        called to create a child state.
 *        Derived classes are responsible for providing
 *        appropriate child state objects.
 * 
 *  <li>Child state handles descendants. Usually, it finishes
 *        parsing when it sees endElement.
 * 
 *  <li>When endElement(</x>) event is received by this object,
 *        it calls endSelf method and reverts to the parent state.
 *        Derived classes are responsible for doing anything
 *        necessary within endSelf method.
 * </ol>
 * 
 * In other words, this state is only active for one hierarchy of XML elements
 * and derived classes are responsible for three abstract methods.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class SimpleState extends State
{
    /** checks if this element belongs to the grammar. */
    protected boolean isGrammarElement( StartTagInfo tag ) {
        return reader.isGrammarElement(tag);
    }
    
    public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) {
        final StartTagInfo tag = new StartTagInfo(
            namespaceURI,localName,qName,new AttributesImpl(atts));
        // we have to copy Attributes, otherwise it will be mutated by SAX parser
            
        if( isGrammarElement(tag) ) {
            // this is a grammar element.
            // creates appropriate child state for it.
            
            State nextState = createChildState(tag);
            if(nextState!=null) {
                reader.pushState(nextState,this,tag);
                return;
            }
            
            // unacceptable element
            reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName );
            // try to recover from error by just ignoring it.
        } else {
            // usually, foreign elements are silently ignored.
            // However, for the document element, we have to report an error
            if( parentState==null ) {
                reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName );
                // probably user is using a wrong namespace.
                reader.reportError(GrammarReader.WRN_MAYBE_WRONG_NAMESPACE, tag.namespaceURI );
            }
        }
        
        // element of a foreign namespace. skip subtree
        reader.pushState(new IgnoreState(),this,tag);
    }
    
    /** creates appropriate child state object for this element */
    abstract protected State createChildState( StartTagInfo tag );
    
        
    public final void endElement( String namespaceURI, String localName, String qName ) {
        // while processing endSelf, error should be reported for its start tag.
        Locator prevLoc = reader.getLocator();
        try {
            reader.setLocator(this.location);
            endSelf();
        } finally {
            reader.setLocator(prevLoc);
        }
        
        reader.popState();
    }
    
    public final void endDocument() {
        // top-level state receives endDocument event instead of endElement event.
        endSelf();
        reader.popState();
    }


    /**
     * this method is called in endElement method
     * when the state is about to be removed.
     * 
     * derived-class should perform any wrap-up job 
     */
    protected void endSelf() {}
    
}
