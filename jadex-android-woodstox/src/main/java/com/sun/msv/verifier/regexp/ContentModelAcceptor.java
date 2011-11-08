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
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;

/**
 * base implementation for SimpleAcceptor and ComplexAcceptor
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ContentModelAcceptor extends ExpressionAcceptor {
    
    protected ContentModelAcceptor(
        REDocumentDeclaration docDecl, Expression exp,
        boolean ignoreUndeclaredAttributes ) {
    
        super(docDecl,exp,ignoreUndeclaredAttributes);
    }
    
    public boolean stepForward( Acceptor child, StringRef errRef ) {
        // TODO: explicitly mention that where the error recovery should be done.
        if( child instanceof SimpleAcceptor ) {
            SimpleAcceptor sa = (SimpleAcceptor)child;
            if(sa.continuation!=null)
                // if the continuation is available,
                // the stepForward will be very fast.
                return stepForwardByContinuation( sa.continuation, errRef );
            else
                // otherwise we have to compute the residual.
                return stepForward( new ElementToken(new ElementExp[]{sa.owner}), errRef );
        }
        if( child instanceof ComplexAcceptor ) {
            ComplexAcceptor ca = (ComplexAcceptor)child;
            return stepForward(
                new ElementToken(
                    (errRef!=null)?
                        ca.owners:    // in error recovery mode, pretend that every owner is happy.
                        ca.getSatisfiedOwners() ),
                errRef);
        }
        throw new Error();    // child must be either Simple or Complex.
    }
    
    /**
     * creates actual Acceptor object from the computed result.
     */
    protected Acceptor createAcceptor(
        Expression combined, Expression continuation,
        ElementExp[] primitives, int numPrimitives ) {
        
        if( primitives==null || numPrimitives<=1 ) {
            // primitives==null is possible when recovering from error.
            
            // in this special case, combined child pattern and primitive patterns are the same.
            // therefore we don't need to keep track of primitive patterns.
            return new SimpleAcceptor(
                docDecl, combined,
                (primitives==null)?null:primitives[0],
                continuation );
        }

        // TODO: implements MultipleAcceptor for cases that
        // combined expression is unnecessary but there are more than one primitive.
        
        // we need a fresh array.
        ElementExp[] owners = new ElementExp[numPrimitives];
        System.arraycopy( primitives, 0, owners, 0, numPrimitives );
        
        return new ComplexAcceptor( docDecl, combined, owners );
    }
    
    // ContentModelAcceptor does not support type-assignment.
    // This will be supported by SimpleAcceptor only.
    public Object getOwnerType() { return null; }
}
