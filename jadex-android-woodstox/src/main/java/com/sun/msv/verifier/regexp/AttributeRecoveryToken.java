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

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;

/**
 * special AttributeToken that memorizes {@link AttributeExp} that fails to
 * validate itself.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final class AttributeRecoveryToken extends AttributeToken
{
    AttributeRecoveryToken( REDocumentDeclaration docDecl,
        String namespaceURI, String localName, String qName, StringToken value ) {
        
        super( docDecl, namespaceURI, localName, qName, value );
    }
    
    private Expression failedExp = Expression.nullSet;
    
    public boolean match( AttributeExp exp ) {
        
        // Attribute name must meet the constraint of NameClass
        if(!exp.nameClass.accepts(namespaceURI,localName))    return false;
        
        // content model of the attribute must consume the value
        if(!docDecl.resCalc.calcResidual(exp.exp, value).isEpsilonReducible())
            failedExp = docDecl.pool.createChoice( failedExp, exp.exp );
        
        // accept AttributeExp regardless of its content restriction
        return true;
    }

    Expression getFailedExp() { return failedExp; }
}
