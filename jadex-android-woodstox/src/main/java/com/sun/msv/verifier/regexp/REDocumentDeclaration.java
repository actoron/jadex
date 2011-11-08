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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.DocumentDeclaration;

/**
 * {@link DocumentDeclaration} implementation
 * 
 * Grammar object can be shared among multiple threads, but this object
 * cannot be shared.
 * 
 * <p>
 * REDocumentDeclaration always returns subclasses of {@link ExpressionAcceptor}
 * as the Acceptor implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class REDocumentDeclaration implements DocumentDeclaration
{
    /** start validation from this expression. */
    protected final Expression topLevel;
    
    /** ExpressionPool object that this VGM uses. */
    public final ExpressionPool pool;
    
    public REDocumentDeclaration( Grammar grammar ) {
        this( grammar.getTopLevel(), grammar.getPool() );
    }
    
    /**
     * creates an object that validates the specified expression.
     */
    public REDocumentDeclaration( Expression topLevel, ExpressionPool pool ) {
        this.topLevel = topLevel;
        this.pool = pool;
        
        resCalc        = new ResidualCalculator(pool);
        attFeeder    = new AttributeFeeder(this);
        attPicker    = new AttributePicker(pool);
        attPruner    = new AttributePruner(pool);
        attRemover    = new AttributeRemover(pool);
        cccec        = new CombinedChildContentExpCreator(pool);
        ecc            = new ElementsOfConcernCollector();
        
        attToken    = new AttributeToken(this,null,null,null,null);
    }
    
    
    // thread local objects.
    // for these function objects, one per a thread is enough.
    protected final ResidualCalculator                resCalc;
    protected final CombinedChildContentExpCreator    cccec;
    protected final AttributeFeeder                    attFeeder;
    protected final AttributePruner                    attPruner;
    protected final AttributePicker                    attPicker;
    protected final AttributeRemover                attRemover;
    protected final ElementsOfConcernCollector        ecc;
                                  
    /**
     * the AttributeToken object which is re-used 
     * 
     * <p>
     * Only one AttributeToken object is used during the
     * {@link Acceptor#onAttribute} method.
     * So instead of creating new object every time the method is called, we can
     * kept using one copy.
     * 
     * <p>
     * Note: other parts of the code may never rely on the fact that
     * the attribute token is cached and reused.
     * 
     * <p>
     * Note: this field should be accessed <b>ONLY</b> by the
     * {@link ExpressionAcceptor#onAttribute} method.
     */
    public final AttributeToken                    attToken;

    public Acceptor createAcceptor() {
        // top-level Acceptor cannot have continuation.
        return new SimpleAcceptor(this, topLevel, null, Expression.epsilon);
    }
    
    
    
    
    
    public String localizeMessage( String propertyName, Object[] args ) {
        String format = java.util.ResourceBundle.getBundle(
            "com.sun.msv.verifier.regexp.Messages").getString(propertyName);
        
        return java.text.MessageFormat.format(format, args );
    }
    
    public final String localizeMessage( String propName, Object arg1 ) {
        return localizeMessage(propName, new Object[]{arg1} );
    }
    public final String localizeMessage( String propName, Object arg1, Object arg2 ) {
        return localizeMessage(propName, new Object[]{arg1,arg2} );
    }

    
    public static final String DIAG_ELEMENT_NOT_ALLOWED =
        "Diagnosis.ElementNotAllowed";
    public static final String DIAG_CONTENT_MODEL_IS_NULLSET =
        "Diagnosis.ContentModelIsNullset";
    public static final String DIAG_BAD_TAGNAME_GENERIC =
        "Diagnosis.BadTagName.Generic";
    public static final String DIAG_BAD_TAGNAME_WRAPUP =
        "Diagnosis.BadTagName.WrapUp";
    public static final String DIAG_BAD_TAGNAME_SEPARATOR =
        "Diagnosis.BadTagName.Separator";
    public static final String DIAG_BAD_TAGNAME_MORE =
        "Diagnosis.BadTagName.More";
    public static final String DIAG_BAD_TAGNAME_WRONG_NAMESPACE =
        "Diagnosis.BadTagName.WrongNamespace";
    public static final String DIAG_BAD_TAGNAME_PROBABLY_WRONG_NAMESPACE =
        "Diagnosis.BadTagName.ProbablyWrongNamespace";
    public static final String DIAG_UNDECLARED_ATTRIBUTE =
        "Diagnosis.UndeclaredAttribute";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_GENERIC =
        "Diagnosis.BadAttributeValue.Generic";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_DATATYPE =
        "Diagnosis.BadAttributeValue.DataType";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_WRAPUP =
        "Diagnosis.BadAttributeValue.WrapUp";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_SEPARATOR =
        "Diagnosis.BadAttributeValue.Separator";
    public static final String DIAG_BAD_ATTRIBUTE_VALUE_MORE =
        "Diagnosis.BadAttributeValue.More";
    public static final String DIAG_MISSING_ATTRIBUTE_SIMPLE =
        "Diagnosis.MissingAttribute.Simple";
    public static final String DIAG_MISSING_ATTRIBUTE_GENERIC =
        "Diagnosis.MissingAttribute.Generic";
    public static final String DIAG_MISSING_ATTRIBUTE_WRAPUP =
        "Diagnosis.MissingAttribute.WrapUp";
    public static final String DIAG_MISSING_ATTRIBUTE_SEPARATOR =
        "Diagnosis.MissingAttribute.Separator";
    public static final String DIAG_MISSING_ATTRIBUTE_MORE =
        "Diagnosis.MissingAttribute.More";
    public static final String DIAG_UNCOMPLETED_CONTENT_WRAPUP =
        "Diagnosis.UncompletedContent.WrapUp";
    public static final String DIAG_UNCOMPLETED_CONTENT_SEPARATOR =
        "Diagnosis.UncompletedContent.Separator";
    public static final String DIAG_UNCOMPLETED_CONTENT_MORE =
        "Diagnosis.UncompletedContent.More";
    public static final String DIAG_BAD_LITERAL_WRAPUP = // arg:2
        "Diagnosis.BadLiteral.WrapUp";
    public static final String DIAG_BAD_LITERAL_SEPARATOR =
        "Diagnosis.BadLiteral.Separator";
    public static final String DIAG_BAD_LITERAL_MORE =
        "Diagnosis.BadLiteral.More";
    public static final String DIAG_BAD_LITERAL_GENERIC = // arg:1
        "Diagnosis.BadLiteral.Generic";
    public static final String DIAG_BAD_LITERAL_INCORRECT_VALUE = // arg:2
        "Diagnosis.BadLiteral.IncorrectValue";
    public static final String DIAG_SIMPLE_NAMECLASS =
        "Diagnosis.SimpleNameClass";
    public static final String DIAG_NAMESPACE_NAMECLASS =
        "Diagnosis.NamespaceNameClass";
    public static final String DIAG_NOT_NAMESPACE_NAMECLASS =
        "Diagnosis.NotNamespaceNameClass";
    public static final String DIAG_STRING_NOT_ALLOWED = // arg:1
        "Diagnosis.StringNotAllowed";
    public static final String DIAG_BAD_KEY_VALUE = // arg:1
        "Diagnosis.BadKeyValue";
    public static final String DIAG_BAD_KEY_VALUE2 = // arg:2
        "Diagnosis.BadKeyValue2";
}
