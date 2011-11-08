package com.sun.msv.reader.trex.ng.comp;

import org.xml.sax.Locator;

import com.sun.msv.grammar.relaxng.RELAXNGGrammar;

abstract class CompatibilityChecker {
    
    
    protected final RELAXNGCompReader reader;
    protected final RELAXNGGrammar grammar;
    
    protected CompatibilityChecker( RELAXNGCompReader _reader ) {
        this.reader = _reader;
        this.grammar = (RELAXNGGrammar)_reader.getGrammar();
    }
    
    protected abstract void setCompatibility( boolean val );
    
    /**
     * reports the compatibility related error.
     * 
     * <p>
     * Since the processor is required to validate a schema even if 
     * it's not compatible with some of the features, we cannot report
     * those errors as real "errors".
     */
    protected void reportCompError( Locator[] locs, String propertyName ) {
        // TODO: it maybe useful to implement a switch
        // that makes those warnings as errors.
        reportCompError(locs,propertyName,null);
    }
    protected void reportCompError( Locator[] locs, String propertyName, Object[] args ) {
        setCompatibility(false);
        reader.reportWarning(propertyName,args,locs);
    }
}
