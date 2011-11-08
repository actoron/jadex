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

import org.iso_relax.dispatcher.ElementDecl;
import org.xml.sax.Locator;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;

/**
 * place holder for imported element declaration.
 * 
 * This class derives ElementExp because "elementDecl" is a constraint over one element.
 * This class also provides stub methods so that programs who are not aware to
 * divide&validate can gracefully degrade.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class ExternalElementExp extends ElementExp
{
    public NameClass getNameClass() { return nameClass; }

    /** NamespaceNameClass object that matchs this namespace. */
    private final NamespaceNameClass nameClass;
    
    /** namespace URI that this ExternalElementExp belongs to. */
    public final String namespaceURI;
    
    /** name of the imported Rule */
    public final String ruleName;
    
    /** where did this reference is written in the source file.
     * 
     * can be set to null (to reduce memory usage) at anytime.
     */
    public transient Locator source;
    
    /**
     * imported ElementDecl object that actually validates this element.
     * this variable is set during binding phase.
     */
    public ElementDecl rule;

    public ExternalElementExp(
        ExpressionPool pool, String namespaceURI, String ruleName,
        Locator loc )
    {
        // set content model to nullSet
        // to make this elementExp accept absolutely nothing.
        // "ignoreUndeclaredAttributes" flag is also meaningless here
        // because actual validation of this element will be done by a different
        // IslandVerifier.
        super(Expression.nullSet,false);
        
        this.ruleName = ruleName;
        this.namespaceURI = namespaceURI;
        this.nameClass = new NamespaceNameClass(namespaceURI);
        this.source = loc;
        
        /* provide dummy content model
        
            <mixed>
                <zeroOrMore>
                    <choice>
                        <attribute>
                            <nsName/>
                        </attribute>
                        <<<< this >>>>
                    </choice>
                </zeroOrMore>
            </mixed>
        */
        this.contentModel = pool.createZeroOrMore(
            pool.createMixed(
                pool.createChoice(
                    pool.createAttribute(nameClass),
                    this ) ) );
    }
    
}
