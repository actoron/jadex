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
import java.util.StringTokenizer;

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.xmlschema.LaxDefaultNameClass;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.ExpressionWithoutChildState;

/**
 * base implementation of AnyAttributeState and AnyElementState.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class AnyState extends ExpressionWithoutChildState {

    protected final Expression makeExpression() {
        return createExpression(
            startTag.getDefaultedAttribute("namespace","##any"),
            startTag.getDefaultedAttribute("processContents","strict") );
    }
    
    /**
     * creates AGM that corresponds to the specified parameters.
     */
    protected abstract Expression createExpression( String namespace, String process );
    
    /**
     * processes 'namepsace' attribute and gets corresponding NameClass object.
     */
    protected NameClass getNameClass( String namespace, XMLSchemaSchema currentSchema ) {
        // we have to get currentSchema through parameter because
        // this method is also used while back-patching, and 
        // reader.currentSchema points to the invalid schema in that case.
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        namespace = namespace.trim();
        
        if( namespace.equals("##any") )
            return NameClass.ALL;
        
        if( namespace.equals("##other") )
            // ##other means anything other than the target namespace and local.
            return new NotNameClass(
                new ChoiceNameClass(
                    new NamespaceNameClass(currentSchema.targetNamespace),
                    new NamespaceNameClass("")) );
        
        NameClass choices=null;
        
        StringTokenizer tokens = new StringTokenizer(namespace);
        while( tokens.hasMoreTokens() ) {
            String token = tokens.nextToken();
            
            NameClass nc;
            if( token.equals("##targetNamespace") )
                nc = new NamespaceNameClass(currentSchema.targetNamespace);
            else
            if( token.equals("##local") )
                nc = new NamespaceNameClass("");
            else
                nc = new NamespaceNameClass(token);
            
            if( choices==null )        choices = nc;
            else                    choices = new ChoiceNameClass(choices,nc);
        }
        
        if( choices==null ) {
            // no item was found.
            reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "namespace", namespace );
            return NameClass.ALL;
        }
        
        return choices;
    }
    
    protected abstract NameClass getNameClassFrom( ReferenceExp exp );
                    
    protected NameClass createLaxNameClass( NameClass allowedNc, XMLSchemaReader.RefResolver res ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        LaxDefaultNameClass laxNc = new LaxDefaultNameClass(allowedNc);
                
        Iterator<Object> itr = reader.grammar.iterateSchemas();
        while( itr.hasNext() ) {
            XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
            if(allowedNc.accepts( schema.targetNamespace, NameClass.LOCALNAME_WILDCARD )) {
                ReferenceExp[] refs = res.get(schema).getAll();
                for( int i=0; i<refs.length; i++ ) {
                    NameClass name = getNameClassFrom(refs[i]);
                            
                    if(!(name instanceof SimpleNameClass ))
                        // assertion failed.
                        // XML Schema's element declaration is always simple name.
                        throw new Error();
                    SimpleNameClass snc = (SimpleNameClass)name;
                            
                    laxNc.addName(snc.namespaceURI,snc.localName);
                }
            }
        }

        // laxNc - names in namespaces that are not allowed.
        return new DifferenceNameClass( laxNc, new NotNameClass(allowedNc) );
    }
}
