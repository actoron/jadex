/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;

/**
 * base implementation of ComplexAcceptor.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ComplexAcceptorBaseImpl extends ContentModelAcceptor
{
    protected final Expression[]    contents;
    
    public ComplexAcceptorBaseImpl(
        REDocumentDeclaration docDecl, Expression combined, Expression[] contents,
        boolean ignoreUndeclaredAttributes ) {
        
        super( docDecl, combined, ignoreUndeclaredAttributes );
        this.contents = contents;
    }

    /** eats string literal */
    public final boolean onText2( String literal, IDContextProvider2 context, StringRef refErr, DatatypeRef refType ) {
        if(!super.onText2(literal,context,refErr,refType))    return false;
        
        final StringToken token = new StringToken(docDecl,literal,context);
        final ResidualCalculator res = docDecl.resCalc;
        
        // some may become invalid, but at least one always remain valid
        for( int i=0; i<contents.length; i++ )
            contents[i] = res.calcResidual( contents[i], token );
        
        return true;
    }
    
    public final boolean stepForward( Acceptor child, StringRef errRef ) {
        if(!super.stepForward(child,errRef))    return false;

        final ResidualCalculator res = docDecl.resCalc;
        Token token;
        
        if( child instanceof SimpleAcceptor ) {
            // this is possible although it is very rare.
            // continuation cannot be used here, because
            // some contents[i] may reject this owner.
            ElementExp cowner = ((SimpleAcceptor)child).owner;
            if( cowner==null )
                // cowner==null means we are currently recovering from an error.
                // so use AnyElementToken to make contents[i] happy.
                token = AnyElementToken.theInstance;
            else
                token = new ElementToken( new ElementExp[]{cowner} );
        } else {
            if( errRef!=null )
                // in error recovery mode
                // pretend that every candidate of child ComplexAcceptor is happy
                token = new ElementToken( ((ComplexAcceptor)child).owners );
            else
                // in normal mode, collect only those satisfied owners.
                token = new ElementToken( ((ComplexAcceptor)child).getSatisfiedOwners() );
        }
        
        for( int i=0; i<contents.length; i++ )
            contents[i] = res.calcResidual( contents[i], token );
        
        return true;
    }
    
    protected boolean onAttribute( AttributeToken token, StringRef refErr ) {
        
        if(!super.onAttribute(token,refErr))    return false;
        
        for( int i=0; i<contents.length; i++ )
            contents[i] = docDecl.attFeeder.feed( contents[i], token, ignoreUndeclaredAttributes );
        
        return true;
    }
    
    public boolean onEndAttributes( StartTagInfo sti, StringRef refErr ) {
        if(!super.onEndAttributes(sti,refErr))    return false;
        
        for( int i=0; i<contents.length; i++ )
            contents[i] = docDecl.attPruner.prune(contents[i]);
        
        return true;
    }
}
