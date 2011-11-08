/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.grammar.trex;

import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;

/**
 * IslandSchema implementation for TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class TREXIslandSchema extends IslandSchemaImpl
{
    /** underlying TREX pattern which this IslandSchema is representing */
    protected final TREXGrammar grammar;
    
    public TREXIslandSchema( TREXGrammar grammar ) {
        this.grammar = grammar;
        
        // export all named patterns.
        // TODO: modify to export only those element declarations.
        ReferenceExp[] refs = grammar.namedPatterns.getAll();
        for( int i=0; i<refs.length; i++ )
            elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
    }
    
    protected Grammar getGrammar() {
        return grammar;
    }
    
    public void bind( SchemaProvider provider, ErrorHandler handler ) {
        Binder binder = new Binder( provider, handler, grammar.pool );
        bind( grammar.namedPatterns, binder );
    }
}
