/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import java.util.Set;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;include&gt; element as a child of &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeMergeState extends com.sun.msv.reader.trex.IncludeMergeState
            implements ExpressionOwner {
    
    protected State createChildState( StartTagInfo tag ) {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        if(tag.localName.equals("define"))    return reader.getStateFactory().redefine(this,tag);
        if(tag.localName.equals("start"))    return reader.getStateFactory().redefineStart(this,tag);
        return null;
    }
    
    /** set of ReferenceExps which are redefined by this inclusion. */
    private final Set<Expression> redefinedPatterns = new java.util.HashSet<Expression>();
    
    // this class has to implement ExpressionOwner because
    // <define> state requires this interface.
    public void onEndChild( Expression child ) {
        // if child <define> element has an error,
        // then it may not return ReferenceExp.
        if(!(child instanceof ReferenceExp))    return;
        
        redefinedPatterns.add(child);
    }
    
    public void endSelf() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        ReferenceExp[] patterns = (ReferenceExp[])redefinedPatterns.toArray(new ReferenceExp[0]);
        RELAXNGReader.RefExpParseInfo[] old = new RELAXNGReader.RefExpParseInfo[patterns.length];
        
        // back-up the current values of RefExpParseInfo,
        // and reset the values.
        for( int i=0; i<patterns.length; i++ ) {
            RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(patterns[i]);
            
            old[i] = new RELAXNGReader.RefExpParseInfo();
            old[i].set( info );
            info.haveHead = false;
            info.combineMethod = null;
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalNotFoundYet;
        }
        
        // process inclusion.
        super.endSelf();
        
        // make sure that originals are found.
        for( int i=0; i<patterns.length; i++ ) {
            RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(patterns[i]);
            
            if( info.redefinition==RELAXNGReader.RefExpParseInfo.originalNotFoundYet )
                // the original definition was not found.
                reader.reportError( RELAXNGReader.ERR_REDEFINING_UNDEFINED, patterns[i].name );

            // then restore the values.
            reader.getRefExpParseInfo(patterns[i]).set( old[i] );
        }
    }
}
