/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import java.util.Iterator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.State;

/**
 * used to parse &lt;any &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyElementState extends AnyState
{
    protected Expression createExpression( final String namespace, final String process ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final XMLSchemaSchema currentSchema = reader.currentSchema;
        
        if( process.equals("skip") ) {
            // "skip" can be expanded now.
            NameClass nc = getNameClass(namespace,currentSchema);
            
            ElementPattern ep = new ElementPattern(nc,Expression.nullSet);
                
            ep.contentModel = 
                // <mixed><zeroOrMore><choice><attribute /><element /></choice></zeroOrMore></mixed>
                reader.pool.createMixed(
                    reader.pool.createZeroOrMore(
                        reader.pool.createChoice(
                            ep,
                            reader.pool.createAttribute(nc)
                        )
                    )
                );
                
            // minOccurs/maxOccurs is processed through interception
            return ep;
        }
        
        // "lax"/"strict" has to be back-patched later.
        final ReferenceExp exp = new ReferenceExp("any("+process+":"+namespace+")");
        reader.addBackPatchJob( new GrammarReader.BackPatch(){
            public State getOwnerState() { return AnyElementState.this; }
            public void patch() {

                if( !process.equals("lax")
                &&  !process.equals("strict") )  {
                    reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "processContents", process );
                    exp.exp = Expression.nullSet;
                    return;
                }
                
                exp.exp = Expression.nullSet;
                NameClass nc = getNameClass(namespace,currentSchema);
                Iterator<Object> itr = reader.grammar.iterateSchemas();
                while( itr.hasNext() ) {
                    XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
                    // nc is built by using NamespaceNameClass.
                    // "strict" allows global element declarations of 
                    // specified namespaces.
                    if(nc.accepts( schema.targetNamespace, NameClass.LOCALNAME_WILDCARD ))
                        // schema.topLevel is choices of globally declared elements.
                        exp.exp = reader.pool.createChoice( exp.exp, schema.topLevel );
                }
                
                if( !process.equals("lax") )
                    return;    // if processContents="strict", the above is fine.
                
                // if "lax", we have to add an expression to
                // match other elements.
                NameClass laxNc = createLaxNameClass( nc,
                    new XMLSchemaReader.RefResolver() {
                        public ReferenceContainer get( XMLSchemaSchema schema ) {
                            return schema.elementDecls;
                        }
                    });
                
                exp.exp = reader.pool.createChoice(
                    new ElementPattern( laxNc,
                        reader.pool.createMixed(
                            reader.pool.createZeroOrMore(
                                reader.pool.createChoice(
                                    reader.pool.createAttribute(NameClass.ALL),
                                    exp)))),
                     exp.exp );
            }
        });
        
        exp.exp = Expression.nullSet;    // dummy for a while.
        
        // minOccurs/maxOccurs is processed through interception
        return exp;
    }

    protected NameClass getNameClassFrom( ReferenceExp exp ) {
        return ((ElementDeclExp)exp).getElementExp().getNameClass();
    }
    
}
