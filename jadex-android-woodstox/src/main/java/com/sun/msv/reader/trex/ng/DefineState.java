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

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;

/**
 * parses &lt;define&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DefineState extends com.sun.msv.reader.trex.DefineState {
    
    private RELAXNGReader.RefExpParseInfo prevNamedPattern;
    private boolean previousDirectRefernce;
    
    protected void startSelf() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        super.startSelf();
        
        // update the currentNamedPattern field.
        prevNamedPattern = reader.currentNamedPattern;    // push
        previousDirectRefernce = reader.directRefernce;
        
        reader.directRefernce = true;
        
        ReferenceExp exp = getReference();
        if(exp==null)
            //abort. there was an error in this declaration
            reader.currentNamedPattern = null;
        else {
            reader.currentNamedPattern = reader.getRefExpParseInfo(exp);
            
            if(reader.currentNamedPattern.redefinition!=
                RELAXNGReader.RefExpParseInfo.notBeingRedefined )
                // if this pattern is being redefined,
                // we must not augument RefParseInfo.refs from this pattern.
                reader.currentNamedPattern = null;
        }
    }
    
    protected void endSelf() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        reader.currentNamedPattern = prevNamedPattern;    // pop
        reader.directRefernce = previousDirectRefernce;
        
        super.endSelf();
    }
    
    /**
     * combines two expressions into one as specified by the combine parameter,
     * and returns a new expression.
     * 
     * If the combine parameter is invalid, then return null.
     */
    protected Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine ) {
        
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(baseExp);

        // RELAX NG allows multiple definitions in the same file.

        if( combine==null ) {
            // this is a head declaration
            if( info.haveHead ) {
                // two head declarations: an error.
                reader.reportError( RELAXNGReader.ERR_COMBINE_MISSING, baseExp.name );
                return baseExp.exp;
            }
            info.haveHead = true;
        } else {
            // check the consistency of the combine method.
            
            if( info.combineMethod==null ) {
                // If this is the first time @combine is used for this pattern...
                info.combineMethod = combine.trim();
                // make sure that the value is ok.
                if( !info.combineMethod.equals("choice")
                &&    !info.combineMethod.equals("interleave") )
                    reader.reportError( RELAXNGReader.ERR_BAD_COMBINE, info.combineMethod );
            } else {
                if( !info.combineMethod.equals(combine) ) {
                    // different combine method.
                    reader.reportError( new Locator[]{location, reader.getDeclaredLocationOf(baseExp)},
                        RELAXNGReader.ERR_INCONSISTENT_COMBINE, new Object[]{baseExp.name} );
                    
                    // reset the combine method to null
                    // to surpress excessive error messages.
                    info.combineMethod = null;
                    
                    return baseExp.exp;    // ignore the new pattern
                }
            }
        }
            
        if( baseExp.exp==null )    // the first definition
            return newExp;
        
        if( info.redefinition!=RELAXNGReader.RefExpParseInfo.notBeingRedefined ) {
            // ignore the new definition
            // because this definition is currently being redefined by
            // the caller.
            
            // the original definition was found.
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalFound;
            return baseExp.exp;
        }
        
        if( info.combineMethod.equals("choice") )
            return reader.pool.createChoice( baseExp.exp, newExp );
        
        if( info.combineMethod.equals("interleave") )
            return reader.pool.createInterleave( baseExp.exp, newExp );
        
        // some kind of error.
        return null;
    }
}
