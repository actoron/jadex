/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.verifier;

import org.iso_relax.dispatcher.Dispatcher;
import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.msv.relaxns.grammar.relax.AnyOtherElementExp;

/**
 * IslandVerifier that validates &lt;anyOtherElement /&gt; of RELAX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyOtherElementVerifier
    extends DefaultHandler
    implements IslandVerifier {
    
    /** this Verifier validates these expressions.
     * 
     * during validation, failed expression is removed.
     */
    private final AnyOtherElementExp[] exps;
    
    public AnyOtherElementVerifier( AnyOtherElementExp[] exps ) {
        this.exps = exps;
    }
    
    protected Dispatcher dispatcher;
    
    public void setDispatcher( Dispatcher disp ) {
        this.dispatcher = disp;
    }
    
    public void startElement( String namespaceURI,
        String localName, String qName, Attributes atts )
        throws SAXException {

        IslandSchema is = dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceURI);
        if( is!=null ) {
            // find an island that has to be validated.
            // switch to the new IslandVerifier.
            IslandVerifier iv = is.createNewVerifier( namespaceURI, is.getElementDecls() );
            dispatcher.switchVerifier(iv);
            iv.startElement(namespaceURI,localName,qName,atts);
            return;
        }
        
        boolean atLeastOneIsValid = false;
        
        for( int i=0; i<exps.length; i++ )
            if( exps[i]!=null ) {
                if( exps[i].getNameClass().accepts( namespaceURI, localName ) )
                    atLeastOneIsValid = true;
                else
                    exps[i] = null;    // this one is no longer valid.
            }

        if(!atLeastOneIsValid)
            // none is valid. report an error.
            dispatcher.getErrorHandler().error(
                new SAXParseException(
                    Localizer.localize( ERR_UNEXPECTED_NAMESPACE, new Object[]{namespaceURI} ),
                    locator ) );
        
    }
    
    public void endChildIsland( String namespaceURI, ElementDecl[] rules ) {
        // error report should have done by child verifier, if any.
        // so just do nothing.
    }
    
    public ElementDecl[] endIsland() {
        // collect satisfied AnyOtherElements and return it as Rules
        int i,j;
        int len=0;
        for( i=0; i<exps.length; i++ )
            if( exps[i]!=null )        len++;
        
        ElementDecl[] r = new ElementDecl[len];
        for( i=0,j=0; i<exps.length; i++ )
            if( exps[i]!=null )    r[j++]=exps[i];
        
        return r;
    }
    
    protected Locator locator;
    
    public void setDocumentLocator( Locator loc ) {
        this.locator = loc;
    }
    
    
    public static final String ERR_UNEXPECTED_NAMESPACE = 
        "AnyOtherElementVerifier.UnexpectedNamespace";
}
