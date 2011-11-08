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

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.reader.datatype.xsd.XSTypeOwner;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse restriction/extension element as a child of &lt;simpleContent&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleContentRestrictionState extends SequenceState
    implements FacetStateParent,XSTypeOwner,AnyAttributeOwner {
    

    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp parentDecl;
    
    protected SimpleContentRestrictionState( ComplexTypeExp parentDecl ) {
        this.parentDecl = parentDecl;
    }

    public void setAttributeWildcard( AttributeWildcard local ) {
        parentDecl.wildcard = local;
    }
    
    /** used to restrict simpleType */
    protected XSTypeIncubator incubator;
    public XSTypeIncubator getIncubator() {
        // If no incubator is specified,
        // we consider that there is no <simpleType> in the children.
        //
        // In that case, we use the content type of the base type.
        if(incubator==null) {
            final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            
            if(baseTypeName==null) {
                // there was an error and we cannot determine the base type.
                // assume xs:string and recover
                incubator = new XSDatatypeExp(StringType.theInstance,reader.pool).createIncubator();
            } else {
                
                // we need a special handling for built-in types
                if(reader.isSchemaNamespace(baseTypeName[0])) {
                    XSDatatype dt = reader.resolveBuiltinDataType(baseTypeName[1]);
                    if(dt!=null)
                        incubator = new XSDatatypeExp(dt,reader.pool).createIncubator();
                    
                    // maybe we are parsing the schema for schema.
                    // consult externally specified schema.
                }
                
                if(incubator==null) {
                    // we don't know whether it's a simple type or a complex type.
                    // so use a late binding.
                    incubator = new XSDatatypeExp(baseTypeName[0],baseTypeName[1],reader,
                        new BaseContentTypeRenderer()).createIncubator();
                }
            }
        }
        return incubator;
    }
    
    
    private class BaseContentTypeRenderer implements  XSDatatypeExp.Renderer {
        public XSDatatype render( XSDatatypeExp.RenderingContext context ) {
            final XMLSchemaReader reader = (XMLSchemaReader)
                SimpleContentRestrictionState.this.reader;
            
            SimpleTypeExp sexp = baseSchema.simpleTypes.get(baseTypeName[1]);
            if(sexp!=null) {
                // we've found the simple type as the base type
                return sexp.getType().getType(context);
            }
            ComplexTypeExp cexp = baseSchema.complexTypes.get(baseTypeName[1]);
            if(cexp!=null) {
                // we've found the complex type as the base type
                // look for XSDatatypeExp inside
                final XSDatatypeExp[] dexp = new XSDatatypeExp[1];
                cexp.body.visit(new ExpressionWalker() {
                    public void onAttribute( AttributeExp exp ) {
                        // don't visit inside attributes
                    }
                    public void onRef( ReferenceExp exp ) {
                        if(exp instanceof XSDatatypeExp) {
                            dexp[0] = (XSDatatypeExp)exp;
                            return;
                        }
                                        
                        super.onRef(exp);
                    }
                });
                if(dexp[0]==null) {
                    // we didn't find any XSDatatypeExp in it.
                    reader.reportError(
                        XMLSchemaReader.ERR_INVALID_BASETYPE_FOR_SIMPLECONTENT, base );
                    return StringType.theInstance;
                }
                return dexp[0].getType(context);
            }
                            
            // this is an error, but
            // we don't need to report an error here.
            // this error should be reported as a part of
            // back patch job.
            // recover by assuming string type
            return StringType.theInstance;
        }
    }


    public String getTargetNamespaceUri() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.currentSchema.targetNamespace;
    }
    
    public void onEndChild( XSDatatypeExp child ) {
        if( incubator!=null )
            // assertion failed.
            // createChildState should reject 2nd <simpleType> element.
            throw new Error();
        
        incubator = child.createIncubator();
    }
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if( incubator==null && tag.localName.equals("simpleType") )
            return reader.sfactory.simpleType(this,tag);
        
        State s = reader.createAttributeState(this,tag);
        if(s!=null )    return s;
        
        return reader.createFacetState(this,tag);    // facets
    }
    
    protected Expression initialExpression() {
        // without this statement,
        // <extension>/<restriction> without any attribute will be prohibited.
        return Expression.epsilon;
    }

    /** value of the base attribute. null in case of an error. */
    private String base;
    
    /**
     * Namespace URI and local name of the base attribute.
     * null in case of an error.
     */
    private String[] baseTypeName;
    
    /**
     * XMLSchemaSchema object of the base type.
     * We should have this reference so that
     * XMLSchemaReader can detect an error if this schema is undefined.
     * 
     * Null in case of an error.
     */
    private XMLSchemaSchema baseSchema;
    
    
    protected void startSelf() {
        super.startSelf();
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        base = startTag.getAttribute("base");
        if(base==null) {
            // in extension, base attribute is mandatory.
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, startTag.localName, "base");
            return;
        }
        
        baseTypeName = reader.splitQName(base);
        if( baseTypeName==null ) {
            reader.reportError( XMLSchemaReader.ERR_UNDECLARED_PREFIX, base );
            return;
        }
        
        baseSchema = reader.grammar.getByNamespace(baseTypeName[0]);
    }
    
    protected Expression annealExpression( Expression exp ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;

        parentDecl.derivationMethod = ComplexTypeExp.RESTRICTION;
        
        try {
            XSDatatypeExp p = getIncubator().derive(null,null);
            exp = reader.pool.createSequence(
                 super.annealExpression(exp),
                 p );
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, XMLSchemaReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.nullSet;
        }
        

        // we need a special handling for built-in types
        if(reader.isSchemaNamespace(baseTypeName[0])) {
            XSDatatype dt = reader.resolveBuiltinDataType(baseTypeName[1]);
            if(dt!=null) {
                parentDecl.simpleBaseType = new XSDatatypeExp(dt,reader.pool);
                return exp;
            }
        }
        
        // in other cases, we don't know the base type yet.
        // let back patch it
                
        // compute the base type and sets the appropriate property
        // (either simpleBaseType or complexBaseType.)
        reader.addBackPatchJob( new GrammarReader.BackPatch(){
            public State getOwnerState() {
                return SimpleContentRestrictionState.this;
            }
            public void patch() {
                SimpleTypeExp sexp = baseSchema.simpleTypes.get(baseTypeName[1]);
                if(sexp!=null) {
                    // we've found the simple type
                    parentDecl.simpleBaseType = sexp.getType();
                    _assert(parentDecl.simpleBaseType!=null);
                    return;
                }
                ComplexTypeExp cexp = baseSchema.complexTypes.get(baseTypeName[1]);
                if(cexp!=null) {
                    // we've found the complex type as the base type
                    parentDecl.complexBaseType = cexp;
                    return;
                }
                
                // there is no base type.
                reader.reportError( XMLSchemaReader.ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE, base );
            }
        });
        
        return exp;
    }
}
