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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse extension element as a child of &lt;simpleContent&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleContentExtensionState extends SequenceState
    implements AnyAttributeOwner {
    
    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp parentDecl;
    
    protected SimpleContentExtensionState( ComplexTypeExp parentDecl ) {
        this.parentDecl = parentDecl;
    }

    public void setAttributeWildcard( AttributeWildcard local ) {
        parentDecl.wildcard = local;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.createAttributeState(this,tag);
    }
    
    
    protected Expression initialExpression() {
        // without this statement,
        // <extension> without any attribute will be prohibited.
        return Expression.epsilon;
    }
    
    protected Expression annealExpression( Expression exp ) {
        parentDecl.derivationMethod = ComplexTypeExp.EXTENSION;
        return reader.pool.createSequence(
            super.annealExpression(exp),
            getBody());
    }
    
    /**
     * Gets the expression for the base type.
     */
    private Expression getBody() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        final String base = startTag.getAttribute("base");
        if(base==null) {
            // in extension, base attribute must is mandatory.
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, startTag.localName, "base");
            return Expression.nullSet;
        }
        
        final String[] baseTypeName = reader.splitQName(base);
        if( baseTypeName==null ) {
            reader.reportError( XMLSchemaReader.ERR_UNDECLARED_PREFIX, base );
            return Expression.nullSet;
        }
        
        // we need a special handling for built-in types
        if(reader.isSchemaNamespace(baseTypeName[0])) {
            XSDatatype dt = reader.resolveBuiltinDataType(baseTypeName[1]);
            if(dt!=null) {
                XSDatatypeExp dtexp = new XSDatatypeExp(dt,reader.pool);
                parentDecl.simpleBaseType = dtexp;
                return dtexp;
            }
            
            // maybe we are parsing the schema for schema.
            // consult externally specified schema.
        }
        
        final XMLSchemaSchema schema = reader.grammar.getByNamespace(baseTypeName[0]);
        
        // we don't know whether it's a complex type or a simple type.
        // so back patch it
        final ReferenceExp ref = new ReferenceExp(null);
        reader.addBackPatchJob( new GrammarReader.BackPatch(){
            public State getOwnerState() {
                return SimpleContentExtensionState.this;
            }
            public void patch() {
                SimpleTypeExp sexp = schema.simpleTypes.get(baseTypeName[1]);
                if(sexp!=null) {
                    // we've found the simple type
                    ref.exp = sexp;
                    parentDecl.simpleBaseType = sexp.getType();
                    _assert(parentDecl.simpleBaseType!=null);
                    return;
                }
                ComplexTypeExp cexp = schema.complexTypes.get(baseTypeName[1]);
                if(cexp!=null) {
                    // we've found the complex type as the base type
                    ref.exp = cexp.body;
                    parentDecl.complexBaseType = cexp;
                    return;
                }
                
                // there is no base type.
                reader.reportError( XMLSchemaReader.ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE, base );
            }
        });
        
        return ref;
    }
}
