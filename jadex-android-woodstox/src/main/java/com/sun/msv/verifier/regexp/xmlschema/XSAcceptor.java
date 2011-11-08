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

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.QnameType;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.grammar.xmlschema.XMLSchemaTypeExp;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.regexp.AttributeToken;
import com.sun.msv.verifier.regexp.SimpleAcceptor;

/**
 * Acceptor implementation for XSREDocDecl.
 * 
 * <p>
 * This class should be considered as a "quick-hack" to
 * better accomodate W3C XML Schema.
 */
public class XSAcceptor extends SimpleAcceptor {

    public static final String XSINamespace = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";
    
    
    public XSAcceptor( XSREDocDecl docDecl, Expression combined, ElementExp owner, Expression continuation ) {
        super(docDecl,combined,owner,continuation);
        this._docDecl = docDecl;
    }

    public Acceptor createClone() {
        return new XSAcceptor( _docDecl, getExpression(), owner, continuation );
    }
    
    /**
     * holds the same value as the docDecl field, but this one has
     * already down casted to XSREDocDecl.
     */
    private final XSREDocDecl _docDecl;
    
    /**
     * creates an XSAcceptor instead of default acceptor implementations.
     */
    protected Acceptor createAcceptor(
        Expression combined, Expression continuation,
        ElementExp[] primitives, int numPrimitives ) {
        
        if( primitives==null || numPrimitives<=1 ) {
            // primitives==null is possible when recovering from error.
            
            // in this special case, combined child pattern and primitive patterns are the same.
            // therefore we don't need to keep track of primitive patterns.
            return new XSAcceptor(
                (XSREDocDecl)docDecl, combined,
                (primitives==null)?null:primitives[0],
                continuation );
        }

        // we don't want to use complex acceptor because it doesn't implement
        // XML Schema semantics.
        
        // And in XML Schema, content model can never be ambiguous.
        // so complex acceptor is used only for error recovery.
        //
        // So, as a workaround, create a simple acceptor from the first possible choice,
        // and throw other options away.
        
        return new XSAcceptor(
            (XSREDocDecl)docDecl,
            primitives[0].contentModel.getExpandedExp(docDecl.pool),
            primitives[0],
            null );
        
    }
    
    
    protected boolean onAttribute( AttributeToken token, StringRef refErr ) {
        // xsi:*** attribute is ignored.
        // TODO: maybe we should issue an error for unrecognized xsi:*** attributes.
        if( token.namespaceURI.equals(XSINamespace) ) {
            token.match(_docDecl.xsiAttExp);
            return true;
        }
        return super.onAttribute( token, refErr );
    }
    

    public Acceptor createChildAcceptor( StartTagInfo sti, StringRef refErr ) {
        
        final String type = sti.getAttribute(XSINamespace,"type");
        final String nil = sti.getAttribute(XSINamespace,"nil");
            
        if( type==null && nil==null )
            // no need for the special handling.
            return super.createChildAcceptor(sti,refErr);
        
    //    
    // craetes combined child content model.
    // it should be uniquely computed.
    //
        /*CombinedChildContentExpCreator.ExpressionPair result =*/
        _docDecl.getCCCEC().get( getExpression(), sti, true );
        
        switch( _docDecl.getCCCEC().numMatchedElements() ) {
        case 0:
            // no element matches. The default implementation would properly
            // handle this situation and recover from this error.
            return super.createChildAcceptor(sti,refErr);
        case 1:
            break;
        default:
            // ambiguous. Generally, this is not possible for XML Schema,
            // but this maybe because of the error recovery.
            // abandon XML Schema specific processing and delegate to the
            // default implementation.
            return super.createChildAcceptor(sti,refErr);
        }
        
        // obtain the ElementExp that mathced this start tag.
        final ElementExp element = _docDecl.getCCCEC().getMatchedElements()[0];
        
        if(!(element instanceof ElementDeclExp.XSElementExp)) {
            // it's not an element of XML Schema.
            // we don't need to handle xsi:*** for this element.
            return super.createChildAcceptor(sti,refErr);
        }
        final ElementDeclExp.XSElementExp xe = (ElementDeclExp.XSElementExp)element;
        
        
        // see if there is a nil attribute
        if(nil!=null) {
            if( !xe.parent.isNillable ) {
                // error
                if( refErr==null ) return null;
                
                refErr.str = _docDecl.localizeMessage( XSREDocDecl.ERR_NON_NILLABLE_ELEMENT, sti.qName );
                return new XSAcceptor( _docDecl, Expression.epsilon, xe, null );
            }
            if( nil.trim().equals("true") ) {
                
                // it should only accept empty tag without any attribute.
                return new XSAcceptor( _docDecl, Expression.epsilon, xe, null );
            }
            // TODO: should we issue an error if the value is something strange?
        }
        
        if(type==null) // there was no xsi:type. Use the default implementation.
            return super.createChildAcceptor(sti,refErr);
        
        String[] typeName = (String[])QnameType.theInstance.createJavaObject(type,sti.context);
        if(typeName==null) {
            return onTypeResolutionFailure(sti,type,refErr);
        }
        
        Expression contentModel;
        
        if( typeName[0].equals(XMLSchemaNamespace) ) {
            // special handling is required for built-in datatypes.
            try {
                contentModel = _docDecl.grammar.getPool().createData(
                    DatatypeFactory.getTypeByName(typeName[1]) );
            } catch( DatatypeException e ) {
                return onTypeResolutionFailure(sti,type,refErr);
            }
        } else {
            XMLSchemaSchema schema = _docDecl.grammar.getByNamespace(typeName[0]);
            if(schema==null) {
                return onTypeResolutionFailure(sti,type,refErr);
            }

            final XMLSchemaTypeExp currentType = xe.parent.getTypeDefinition();
            ComplexTypeExp cexp = schema.complexTypes.get(typeName[1]);
            if(cexp!=null) {
                if(cexp.isDerivedTypeOf( currentType, xe.parent.block|currentType.getBlock() )) {
                    // this type can substitute the current type.
                    contentModel = cexp;
                // 08-Oct-2010, tatu: Fix to specific symptom of GitHub Issue#2:
                } else if ("anyType".equals(currentType.name)
                        && (currentType instanceof ComplexTypeExp)
                        && XMLSchemaReader.XMLSchemaNamespace.equals(((ComplexTypeExp) currentType).parent.targetNamespace)) 
                {
                    // xs:anyType
                    contentModel = cexp;
                } else {
                    return onNotSubstitutableType(sti,type,refErr);
                }
            } else {
                SimpleTypeExp sexp = schema.simpleTypes.get(typeName[1]);
                if(sexp==null) {
                    return onTypeResolutionFailure(sti,type,refErr);
                }
                if(!(currentType instanceof SimpleTypeExp)) {
                    return onNotSubstitutableType(sti,type,refErr);
                }
                SimpleTypeExp curT = (SimpleTypeExp)currentType;
                if(sexp.getDatatype().isDerivedTypeOf(
                    curT.getDatatype(), !xe.parent.isRestrictionBlocked() )) {
                    contentModel = sexp;
                } else {
                    return onNotSubstitutableType(sti,type,refErr);
                }
            }
        }
        
        return new XSAcceptor( _docDecl, contentModel, xe, null );
    }



    private Acceptor onNotSubstitutableType( StartTagInfo sti, String type, StringRef refErr ) {
        if(refErr==null) {
            return null;
        }
        refErr.str = _docDecl.localizeMessage( XSREDocDecl.ERR_NOT_SUBSTITUTABLE_TYPE, type );
        return super.createChildAcceptor(sti,refErr);
    }
    
    private Acceptor onTypeResolutionFailure( StartTagInfo sti, String type, StringRef refErr ) {
        if(refErr==null) {
            return null;
        }
        refErr.str = _docDecl.localizeMessage( XSREDocDecl.ERR_UNDEFINED_TYPE, type );
        return super.createChildAcceptor(sti,refErr);
    }
    
    
    

}
