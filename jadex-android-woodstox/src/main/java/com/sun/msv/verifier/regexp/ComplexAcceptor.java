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
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.verifier.Acceptor;

/**
 * Accept that is used when more than one pattern can be applicable to the current context.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ComplexAcceptor extends ComplexAcceptorBaseImpl {
    
    /**
     * each item of this array should be considered as read-only.
     */
    public final ElementExp[]    owners;
    
    private static Expression[] createDefaultContentModels( ElementExp[] owners, ExpressionPool pool ) {
        Expression[] r = new Expression[owners.length];
        for( int i=0; i<owners.length; i++ )
            r[i] = owners[i].contentModel.getExpandedExp(pool);
        return r;
    }
    
    public ComplexAcceptor( REDocumentDeclaration docDecl,
            Expression combined, ElementExp[] primitives ) {
        this( docDecl, combined,
            createDefaultContentModels(primitives,docDecl.pool), primitives );
    }
    
    public ComplexAcceptor(
        REDocumentDeclaration docDecl, Expression combined,
        Expression[] contentModels,    ElementExp[] owners ) {
        
        // since all owners should belong to the same schema language,
        // ignoreUndeclaredAttributes must be the same.
        // that's why I'm using owners[0].
        super( docDecl, combined, contentModels, owners[0].ignoreUndeclaredAttributes );
        this.owners = owners;
    }

    public Acceptor createClone() {
        Expression[] models = new Expression[contents.length];
        System.arraycopy(contents,0, models, 0, contents.length );
        return new ComplexAcceptor( docDecl, getExpression(), models, owners );
    }
    
    /**
     * collects satisfied ElementExps.
     * 
     * "satisfied ElementExps" are ElementExps whose
     * contents is now epsilon reducible.
     */
    public final ElementExp[] getSatisfiedOwners()
    {
        ElementExp[] satisfied;
        
        int i,cnt;
        // count # of satisfied ElementExp.
        for( i=0,cnt=0; i<contents.length; i++ )
            if( contents[i].isEpsilonReducible() )    cnt++;
            
        if(cnt==0)    return new ElementExp[0];    // no one is satisfied.
            
        satisfied = new ElementExp[cnt];
        for( i=0,cnt=0; i<contents.length; i++ )
            if( contents[i].isEpsilonReducible() )
                satisfied[cnt++] = owners[i];
        
        return satisfied;
    }
}
