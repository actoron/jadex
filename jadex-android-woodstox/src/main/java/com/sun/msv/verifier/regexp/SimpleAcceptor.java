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
import com.sun.msv.verifier.Acceptor;

/**
 * Acceptor that will be used when only one ElementExp matches
 * the start tag.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleAcceptor extends ContentModelAcceptor {
    
    /**
     * the expression that should be used by the parent acceptor
     * once if this acceptor is satisfied.
     * 
     * This field can be null. In that case, the continuation has to be computed.
     */
    public final Expression continuation;
    
    /**
     * ElementExp that accepted the start tag.
     * 
     * This acceptor is verifying the content model of this ElementExp.
     * This value is usually non-null, but can be null when Verifier is
     * recovering from eariler errors.
     * null owner means this acceptor is "synthesized" just for proper error recovery,
     * therefor there is no owner element expression.
     */
    public final ElementExp owner;

    public final Object getOwnerType()    { return owner; }

    public SimpleAcceptor(
        REDocumentDeclaration docDecl,
        Expression combined,
        ElementExp owner,
        Expression continuation )
    {
        super(docDecl,combined,
            // ignore undeclared attributes if we are recovering from errors.
            (owner==null)?true:owner.ignoreUndeclaredAttributes);
        this.continuation    = continuation;
        this.owner            = owner;
    }
    
    public Acceptor createClone() {
        return new SimpleAcceptor( docDecl, getExpression(), owner, continuation );
    }
}
