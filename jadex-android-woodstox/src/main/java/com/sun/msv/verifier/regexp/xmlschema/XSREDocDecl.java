/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp.xmlschema;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.verifier.regexp.AttributeFeeder;
import com.sun.msv.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * {@link REDocumentDeclaration} that supports several W3C XML Schema
 * specific semantics.
 * 
 * <p>
 * If you do validation by using W3C XML Schema, then you should use
 * this VGM instead of plain <code>REDocumentDeclaration</code>.
 * You should also use {@link IDConstraintChecker} instead of Verifier class.
 * 
 * <p>
 * This package implements the following things:
 * <ol>
 *  <li>the xsi:nil attribute support.
 *  <li>the runtime type substitution by the xsi:type attribute
 * </ol>
 */
public class XSREDocDecl extends REDocumentDeclaration {

    public XSREDocDecl( XMLSchemaGrammar grammar ) {
        super(grammar);
        this.grammar = grammar;
    }

    public Acceptor createAcceptor() {
        // use XSAcceptor instead
        return new XSAcceptor(this, topLevel, null, Expression.epsilon);
    }

    CombinedChildContentExpCreator getCCCEC() { return super.cccec; }
    AttributeFeeder getAttFeeder() { return super.attFeeder; }
    
    /**
     * the grammar which this VGM is using.
     * 
     * For one, this object is used to find the complex type definition
     * by its name.
     */
    final protected XMLSchemaGrammar grammar;
    
    /**
     * AttributeExp that matches to "xsi:***" attributes.
     */
    final protected AttributeExp xsiAttExp =
        new AttributeExp(
            new NamespaceNameClass(XSAcceptor.XSINamespace),
            Expression.anyString);
    
    public String localizeMessage( String propertyName, Object[] args ) {
        try {
            String format = java.util.ResourceBundle.getBundle(
                "com.sun.msv.verifier.regexp.xmlschema.Messages").getString(propertyName);
        
            return java.text.MessageFormat.format(format, args );
        } catch( Exception e ) {
            return super.localizeMessage(propertyName,args);
        }
    }
    
    public static final String ERR_NON_NILLABLE_ELEMENT = // arg:1
        "XMLSchemaVerifier.NonNillableElement";
    public static final String ERR_NOT_SUBSTITUTABLE_TYPE = // arg:1
        "XMLSchemaVerifier.NotSubstitutableType";
    public static final String ERR_UNDEFINED_TYPE = // arg:1
        "XMLSchemaVerifier.UndefinedType";

}
