/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.datatype.DataTypeVocabularyMap;

/**
 * TREX grammar, which is expressed as &lt;grammar&gt; element.
 * 
 * <p>
 * The <code>exp</code> field keeps the start pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXGrammar extends ReferenceExp implements Grammar{
    
    @SuppressWarnings("serial")
    static final public class RefContainer extends ReferenceContainer {
        public ReferenceExp getOrCreate( String name ) {
            return super._getOrCreate(name);
        }
        protected ReferenceExp createReference( String name ) {
            return new ReferenceExp(name);
        }
    }
    
    /** named patterns which are defined by using &lt;define&gt; element.
     * 
     * this is a map from pattern name to RefPattern object
     */
    public final RefContainer namedPatterns = new RefContainer();
    
    /** gets the start pattern.
     * 
     * The pattern defined under &lt;start&gt; element.
     * This pattern will be used to verify document element.
     */
    public Expression getTopLevel() { return this.exp; }
    
    /** ExpressionPool that is associated with this grammar */
    public final ExpressionPool pool;
    public ExpressionPool getPool() { return pool; }
    
    /** in case of nested grammar, this variable points to the parent grammar.
     *  Otherwise null.
     */
    protected final TREXGrammar parentGrammar;
    
    /**
     * gets a parent TREXGrammar.
     * 
     * In case of nested grammar, the parent grammar will be returned.
     * Otherwise, it returns null.
     */
    public final TREXGrammar getParentGrammar() { return parentGrammar; }
    
    /**
     * data type vocabularies used and defined by this grammar.
     */
    public final DataTypeVocabularyMap dataTypes = new DataTypeVocabularyMap();
    
    /**
     * 
     * @param parentGrammar
     *        this object is used to resolve &lt;ref&gt; element with parent
     *        attribute.
     */
    public TREXGrammar( ExpressionPool pool, TREXGrammar parentGrammar ) {
        super(null);
        this.pool = pool;
        this.parentGrammar = parentGrammar;
    }
    
    public TREXGrammar( ExpressionPool pool )    { this(pool,null); }
    public TREXGrammar() { this(new ExpressionPool(),null); }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
