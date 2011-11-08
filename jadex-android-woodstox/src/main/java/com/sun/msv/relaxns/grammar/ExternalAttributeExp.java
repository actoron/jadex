/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.grammar;

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;

/**
 * place holder for imported attributes declaration.
 * 
 * This class also provides stub methods so that programs who are not aware to
 * divide&validate can gracefully degrade.
 * 
 * <p>
 * In MSV, importing AttributesDecl from different implementations is
 * not supported. ExternalAttributeExp is always replaced by their target Expression
 * before validation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class ExternalAttributeExp extends OtherExp {
    
    public ExternalAttributeExp(
        ExpressionPool pool, String namespaceURI, String role, Locator loc ) {
        
        this.source = loc;
        this.namespaceURI = namespaceURI;
        this.role = role;
        this.exp = Expression.epsilon;
    }
    
    /** namespace URI that this object belongs to. */
    public final String namespaceURI;
    
    /** name of the imported AttributesDecl */
    public final String role;
    
    /**
     * where did this reference is written in the source file.
     * can be set to null (to reduce memory usage) at anytime.
     */
    public transient Locator source;
}
