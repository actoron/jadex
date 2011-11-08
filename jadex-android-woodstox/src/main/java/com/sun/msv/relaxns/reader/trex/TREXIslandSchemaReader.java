/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader.trex;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandSchemaReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.relaxns.grammar.trex.TREXIslandSchema;

/**
 * reads extended-TREX grammar (extended by RELAX Namespace)
 * and constructs IslandSchema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXIslandSchemaReader
    extends XMLFilterImpl
    implements IslandSchemaReader {
    
    private final TREXGrammarReader baseReader;
    
    public TREXIslandSchemaReader( TREXGrammarReader baseReader ) {
        this.baseReader = baseReader;
        this.setContentHandler(baseReader);
    }
    
    public final IslandSchema getSchema() {
        TREXGrammar g = baseReader.getResult();
        if(g==null)        return null;
        else            return new TREXIslandSchema(g);
    }
}
