/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.classic;

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;

/**
 * parses &lt;define&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DefineState extends com.sun.msv.reader.trex.DefineState {
    
    /**
     * combines two expressions into one as specified by the combine parameter,
     * and returns a new expression.
     * 
     * If the combine parameter is invalid, then return null.
     */
    protected Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine ) {
        
        final TREXGrammarReader reader = (TREXGrammarReader)this.reader;
        
        if( baseExp.exp==null ) {
            // this is the first time definition
            if( combine!=null )
                // "combine" attribute will be ignored
                reader.reportWarning( TREXGrammarReader.WRN_COMBINE_IGNORED, baseExp.name );
            return newExp;
        }

        // some pattern is already defined under this name.
        
        // make sure that the previous definition was in a different file.
        if( reader.getDeclaredLocationOf(baseExp).getSystemId().equals(
                reader.getLocator().getSystemId() ) ) {
            reader.reportError( TREXGrammarReader.ERR_DUPLICATE_DEFINITION, baseExp.name );
            // recovery by ignoring this definition
            return baseExp.exp;
        }
            
        if( combine==null ) {
            // second definition without @combine.
            reader.reportError( new Locator[]{location, reader.getDeclaredLocationOf(baseExp)},
                TREXGrammarReader.ERR_COMBINE_MISSING, new Object[]{baseExp.name} );
            // recover by ignoring this definition
            return baseExp.exp;
        }

        
        
        if( combine.equals("group") )
            return reader.pool.createSequence( baseExp.exp, newExp );
        else
        if( combine.equals("choice") )
            return reader.pool.createChoice( baseExp.exp, newExp );
        else
        if( combine.equals("replace") )
            return exp;
        else
        if( combine.equals("interleave") )
            return reader.pool.createInterleave( baseExp.exp, newExp );
        else
        if( combine.equals("concur") )
            return reader.pool.createConcur( baseExp.exp, newExp );
        else
            return null;
    }
}
