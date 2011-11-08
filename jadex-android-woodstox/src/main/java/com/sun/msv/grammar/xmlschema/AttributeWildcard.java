/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import java.util.Iterator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Attribute wildcard property of the schema component.
 * 
 * <p>
 * This object is used during the parsing process to keep the intermediate information.
 * Once the parsing is finished, attribute wildcard is kept as an expression.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeWildcard {
    
    public AttributeWildcard( NameClass name, int processMode ) {
        this.name = name;
        this.processMode = processMode;
    }
    
    private NameClass name;
    
    /** Gets the target of the name class. */
    public NameClass getName() { return name; }
    
    private int processMode;
    
    /** Gets the processing model of the wildcard. */
    public int getProcessMode() { return processMode; }
    
    public static final int SKIP    = 0;
    public static final int LAX        = 1;
    public static final int STRICT    = 2;
    
    public AttributeWildcard copy() {
        return new AttributeWildcard(name,processMode);
    }
    
    /**
     * Creates the expression that corresponds to
     * the current attribute wildcard specification.
     */
    public Expression createExpression( XMLSchemaGrammar grammar ) {
        final ExpressionPool pool = grammar.pool;
        
        switch(processMode) {
        case SKIP:
            return pool.createZeroOrMore(pool.createAttribute(name));
            
        case STRICT:
        case LAX:
            
            Expression exp = Expression.epsilon;
            LaxDefaultNameClass laxNc = new LaxDefaultNameClass(name);
            
            Iterator<Object> itr = grammar.iterateSchemas();
            while( itr.hasNext() ) {
                XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
                // nc is built by using NamespaceNameClass.
                // "strict" allows global element declarations of 
                // specified namespaces.
                if(name.accepts( schema.targetNamespace, NameClass.LOCALNAME_WILDCARD )) {
                    
                    // gather global attributes.
                    ReferenceExp[] atts = schema.attributeDecls.getAll();
                    for( int i=0; i<atts.length; i++ ) {
                        exp = pool.createSequence( pool.createOptional(atts[i]), exp );
                        laxNc.addName( schema.targetNamespace, atts[i].name );
                    }
                }
            }
                
            if( processMode==STRICT )
                // if processContents="strict", then that's it.
                return exp;
                
            // if "lax", we have to add an expression to
            // match other attributes.
            return pool.createSequence(
                pool.createZeroOrMore(pool.createAttribute(laxNc)), exp );
        
        default:
            throw new Error("undefined process mode:"+processMode);
        }
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
