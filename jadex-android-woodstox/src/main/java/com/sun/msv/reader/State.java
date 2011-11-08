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

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.Expression;
import com.sun.msv.util.StartTagInfo;

/**
 * base interface of 'parsing state'.
 * 
 * parsing of XML representation of a grammar is done by
 * using various states.
 * 
 * <p>
 * Each State-derived class is responsible for a particular type of
 * declaration of the grammar. For example, SequenceState is responsible
 * for parsing &lt;sequence&gt; element of RELAX module.
 * 
 * <p>
 * State objects interact each other. There are two ways of interaction.
 * 
 * <ul>
 *  <li>from parent to child
 *  <li>from child to parent
 * </ul>
 * 
 * The first type of communication occurs only when a child state object is
 * created. The last type of communication occurs usually (but not limited to)
 * when a child state sees its end tag.
 * 
 * 
 * <p>
 * In this level of inheritance, contract is somewhat abstract.
 * 
 * <ol>
 *  <li>When a State object is created, its init method is called
 *        and various information is set. Particularly, start tag
 *        information (if any) and the parent state is set.
 *        This process should only be initiated by GrammarReader.
 * 
 *  <li>After that, startSelf method is called. Usually,
 *        this is the place to do something useful.
 * 
 *  <li>State object is registered as a ContentHandler, and
 *        therefore will receive SAX events from now on.
 * 
 *  <li>Derived classes are expected to do something useful
 *        by receiving SAX events.
 * 
 *  <li>When a State object finishes its own part, it should
 *        call GrammarReader.popState method. It will remove
 *        the current State object and registers the parent state 
 *        as a ContentHandler again.
 * </ol>
 * 
 * Of course some derived classes introduce more restricted
 * contract. See {@link SimpleState}.
 * 
 * <p>
 * this class also provides:
 * <ul>
 *  <li>access to the parent state
 *  <li>default implementations for all ContentHandler callbacks
 *        except startElement and endElement
 * </ul>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class State implements ContentHandler
{
    /**
     * parent state of this state.
     * 
     * In other words, the parent state is a state who is responsible
     * for the parent element of the current element.
     * 
     * For states responsible for the document element, the parent state is
     * a state who is responsible for the entire document.
     * 
     * For states responsible for the entire document, the parent state is
     * always null.
     */
    protected State parentState;
    public final State getParentState() { return parentState; }

    /**
     * reader object who is the owner of this object.
     * This information is avaiable after init method is called.
     */
    public GrammarReader reader;
                
    /**
     * information of the start tag.
     * This information is avaiable after init method is called.
     */
    protected StartTagInfo startTag;
    public StartTagInfo getStartTag() { return startTag; }
    
    /**
     * Location of the start tag.
     * This information is avaiable after init method is called.
     */
    protected Locator location;
    public Locator getLocation() { return location; }
    
    /**
     * base URI for this state.
     * This information is avaiable after init method is called.
     */
    protected String baseURI;
    public String getBaseURI() { return baseURI; }
    
    protected final void init( GrammarReader reader, State parentState, StartTagInfo startTag ) {
        // use of the constructor, which is usually the preferable way,
        // is intentionally avoided for short hand.
        // if we use the constructor to do the initialization, every derived class
        // has to have a dumb constructor, which simply delegates all the parameter
        // to the super class.
        
        this.reader = reader;
        this.parentState = parentState;
        this.startTag = startTag;
        if( reader.getLocator()!=null )    // locator could be null, in case of the root state.
        this.location = new LocatorImpl( reader.getLocator() );
        
        // handle the xml:base attribute.
        String base=null;
        if( startTag!=null )
            base = startTag.getAttribute("http://www.w3.org/XML/1998/namespace","base");
        
        if( parentState==null )
            // this state is the root state and therefore we don't have locator.
            this.baseURI = null;
        else {
            this.baseURI = parentState.baseURI;
            if( this.baseURI==null )
                this.baseURI = reader.getLocator().getSystemId();
        }
        if( base!=null )
            this.baseURI = reader.combineURI( this.baseURI, base );
        startSelf();
    }
    
    /** performs a task that should be done before reading any child elements.
     * 
     * derived-class can safely read startTag and/or parentState values.
     */
    protected void startSelf() {}
    
    
    public static void _assert( boolean b ) {
        if(!b)  throw new InternalError();
    }

    public void characters(char[] buffer, int from, int len ) throws SAXException {
        // both RELAX and TREX prohibits characters in their grammar.
        for( int i=from; i<len; i++ )
            switch(buffer[i])
            {
            case ' ': case '\t': case '\n': case '\r':
                break;
            default:
                reader.reportError( GrammarReader.ERR_CHARACTERS, new String(buffer,from,len).trim() );
                return;
            }
    }

    protected final Expression callInterceptExpression( Expression exp ) {
        return reader.interceptExpression(this,exp);
    }
        
    
// unused handlers
//----------------------------        
    public void processingInstruction( String target, String data ) throws SAXException {}
    public void ignorableWhitespace(char[] buffer, int from, int len ) throws SAXException {}
    public void skippedEntity( String name ) throws SAXException {}
    public final void startDocument() throws SAXException {}
    public void setDocumentLocator( Locator loc ) {}
    public void startPrefixMapping(String prefix, String uri ) throws SAXException {}
    public void endPrefixMapping(String prefix) throws SAXException {}
}
