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
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.util.DatatypeRef;

/**
 * represents attribute and its value.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeToken extends Token
{
    public String                        namespaceURI;
    public String                        localName;
    public String                        qName;
    public StringToken                    value;
    protected REDocumentDeclaration        docDecl;
    
    /**
     * holds a reference to the assigned type.
     * 
     * If this AttributeToken is successfully consumed, then this field
     * contains the AttributeExp which consumed this token.
     * 
     * If this token is not consumed or several different AttributeExps
     * consumed this token, then null.
     */
    public AttributeExp matchedExp = null;
    
    /**
     * If this value is false, the "matched" field must always null. This indicates
     * that no AttributeExp has consumed this token yet.
     * If this value is true and the "matched" field is non-null, then it means
     * that AttributeExp has consumed this token.
     * If this value is true and the "matched" field is null, then more than
     * one AttributeExps have consumed this token.
     */
    private boolean saturated = false;
    
    AttributeToken( REDocumentDeclaration docDecl ) {
        this.docDecl = docDecl;
    }
    
    protected AttributeToken( REDocumentDeclaration docDecl,
            String namespaceURI, String localName, String qName, String value, IDContextProvider2 context ) {
        this( docDecl, namespaceURI, localName, qName,
            new StringToken(docDecl,value,context,new DatatypeRef()) );
    }
    protected AttributeToken( REDocumentDeclaration docDecl,
            String namespaceURI, String localName, String qName, StringToken value ) {
        this(docDecl);
        reinit( namespaceURI, localName, qName, value );
    }
    
    void reinit( String namespaceURI, String localName, String qName, StringToken value ) {
        this.namespaceURI    = namespaceURI;
        this.localName        = localName;
        this.qName            = qName;
        this.value            = value;
        
        matchedExp = null;
        saturated = false;
    }
    
    /**
     * creates a special AttributeToken which matchs any content restrictions.
     * 
     * This token acts like a wild card for the attribute. This method is
     * used for error recovery.
     */
    final AttributeRecoveryToken createRecoveryAttToken() {
        return new AttributeRecoveryToken( docDecl, namespaceURI, localName, qName, value );
    }
    
    public boolean match( AttributeExp exp ) {
        // Attribute name must meet the constraint of NameClass
        if(!exp.nameClass.accepts(namespaceURI,localName))    return false;
        
        // content model of the attribute must consume the value
        boolean satisfied = false;
        if(value.literal.trim().length()==0 && exp.exp.isEpsilonReducible())
            satisfied = true;
        else
        if(docDecl.resCalc.calcResidual(exp.exp, value).isEpsilonReducible())
            satisfied = true;
        
        if(satisfied) {
            // store the expression who consumed this token.
            if( !saturated || exp==matchedExp )        matchedExp=exp;
            else                                    matchedExp=null;
        /*    the above is the shortened form of:
            if( !saturated )
                matchedExp = exp;
            else
                if( exp!=matchedExp )
                    matchedExp = null;
         */
            saturated = true;
            return true;
        }
        
        return false;
    }

}
