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

import java.util.Iterator;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.impl.AbstractSchemaProviderImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.relaxns.grammar.RELAXGrammar;

/**
 * implementation of SchemaProvider by using RELAX Grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaProviderImpl extends AbstractSchemaProviderImpl {
    
    private final DeclImpl[] topLevel;
    /** top-level expression as AGM. */
    private final Expression topLevelExp;
    /** shared expression pool. */
    private final ExpressionPool pool;
    
    public IslandVerifier createTopLevelVerifier() {
        return new TREXIslandVerifier(
            new RulesAcceptor(
                new com.sun.msv.verifier.regexp.REDocumentDeclaration(topLevelExp,pool), topLevel ) );
    }
    
    /**
     * creates SchemaProvider from generic Grammar (including TREX/RELAX Core)
     */
    public static SchemaProviderImpl fromGrammar( Grammar grammar ) {
        if( grammar instanceof RELAXGrammar )
            return new SchemaProviderImpl( (RELAXGrammar)grammar );
        
        RELAXGrammar g = new RELAXGrammar(grammar.getPool());
        g.topLevel = grammar.getTopLevel();
        
        return new SchemaProviderImpl( g );
    }
    
    /**
     * creates SchemaProvider from existing RELAXGrammar.
     * 
     * Since bind method is already called by RELAXNSReader,
     * the application should not call bind method.
     */
    public SchemaProviderImpl( RELAXGrammar grammar ) {
//        this.grammar = grammar;
        this.pool = grammar.pool;
        this.topLevelExp = grammar.topLevel;
        this.topLevel = new DeclImpl[]{new DeclImpl("##start",grammar.topLevel)};
        
        // add all parsed modules into the provider.
        Iterator<String> itr = grammar.moduleMap.keySet().iterator();
        while( itr.hasNext() ) {
            String namespaceURI = itr.next();
            addSchema(
                namespaceURI, (IslandSchema)grammar.moduleMap.get(namespaceURI) );
        }
    }

    
    /** binds all IslandSchemata. */
    public boolean bind( ErrorHandler handler ) {
        ErrorHandlerFilter filter = new ErrorHandlerFilter(handler);
        
        try {
            Iterator<?> itr = schemata.values().iterator();
            while( itr.hasNext() ) {
                ((IslandSchema)itr.next()).bind( this, filter );
            }
        } catch( SAXException e ) {
            // bind method may throw SAXException.
            return false;
        }
        
        return !filter.hadError;
    }
    
    private static class ErrorHandlerFilter implements ErrorHandler {
        private final ErrorHandler core;
        boolean hadError = false;
        
        ErrorHandlerFilter( ErrorHandler handler ) { this.core=handler; }
        
        public void fatalError( SAXParseException spe ) throws SAXException {
            error(spe);
        }
        
        public void error( SAXParseException spe ) throws SAXException {
            core.error(spe);
            hadError = true;
        }

        public void warning( SAXParseException spe ) throws SAXException {
            core.warning(spe);
        }
    }
}
