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

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse &lt;complexType&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexTypeDeclState extends RedefinableDeclState implements AnyAttributeOwner {
    
    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp decl;
    
    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader)reader).currentSchema.complexTypes;
    }
    
    protected void startSelf() {
        super.startSelf();
        
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        String name = startTag.getAttribute("name");
        if( name==null ) {
            if( isGlobal() )
                reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "complexType", "name" );
            decl = new ComplexTypeExp( reader.currentSchema, null );
        } else {
            if( isRedefine() )
                // in redefine mode, use temporary object.
                // parsed complexType will be copied into the original one.
                decl = new ComplexTypeExp( reader.currentSchema, name );
            else {
                decl = reader.currentSchema.complexTypes.getOrCreate(name);
                // MSV has pre-defiend types in xsd namespace (such as xs:anyType)
                // this causes a problem when we are parsing schema4schema.
                // to avoid this problem, we won't issue this error when we are
                // parsing schema4schema.
                //
                // But this is more like a quick hack. What is the correct way to
                // solve this problem?
                if( decl.body.exp!=null && reader.currentSchema!=reader.xsdSchema )
                    reader.reportError( 
                        new Locator[]{this.location,reader.getDeclaredLocationOf(decl)},
                        XMLSchemaReader.ERR_DUPLICATE_COMPLEXTYPE_DEFINITION,
                        new Object[]{name} );
            }
        }
        
        // set the final attribute to ComplexTypeExp.
        decl.finalValue = parseFinalValue( "final", reader.finalDefault );
        decl.block        = parseFinalValue( "block", reader.blockDefault );
    }
    
    /**
     * parses the value of the block/finel attribute.
     */
    private int parseFinalValue( String attName, String defaultValue ) {
        int r = 0;
        String value = startTag.getAttribute(attName);
        if( value==null )    value = defaultValue;
        if( value!=null ) {
            if( value.indexOf("#all")>=0 )
                r |= ComplexTypeExp.RESTRICTION|ComplexTypeExp.EXTENSION;
            if( value.indexOf("extension")>=0 )
                r |= ComplexTypeExp.EXTENSION;
            if( value.indexOf("restriction")>=0 )
                r |= ComplexTypeExp.RESTRICTION;
        }
        return r;
    }

    public void setAttributeWildcard( AttributeWildcard local ) {
        decl.wildcard = local;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        // simpleContent, ComplexContent, group, all, choice, and sequence
        // are allowed only when we haven't seen type definition.
        if(tag.localName.equals("simpleContent") )    return reader.sfactory.simpleContent(this,tag,decl);
        if(tag.localName.equals("complexContent") )    return reader.sfactory.complexContent(this,tag,decl);
        State s = reader.createModelGroupState(this,tag);
        if(s!=null)        return s;
        
        if( super.exp==null ) {
            // no content model was given.
            // I couldn't "decipher" what should we do in this case.
            // I assume "empty" just because it's most likely.
            exp = Expression.epsilon;
        }
        
        // TODO: attributes are prohibited after simpleContent/complexContent.
        
        // attribute, attributeGroup, and anyAttribtue can be specified
        // after content model is given.
        return reader.createAttributeState(this,tag);
    }
    
    protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
        if( halfCastedExpression==null )
            return newChildExpression;        // the first one
        
        // only the first one contains element.
        // the rest consists of attributes.
        // so this order of parameters is fine.
        return reader.pool.createSequence( newChildExpression, halfCastedExpression );
    }
                                                                                                                   
    protected Expression defaultExpression() {
        // if no content model is given, then this complex type is empty
        return Expression.epsilon;
    }
    
    
    protected Expression annealExpression(Expression contentType) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        
        String abstract_ = startTag.getAttribute("abstract");
        if( "false".equals(abstract_) || abstract_==null )
            // allow the content model to directly appear as this type.
            decl.setAbstract(false);
        else {
            decl.setAbstract(true);
            if( !"true".equals(abstract_) )
                reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "abstract", abstract_ );
                // recover by ignoring this error.
        }
        
        
        String mixed = startTag.getAttribute("mixed");
        if( "true".equals(mixed) )
            contentType = reader.pool.createMixed(contentType);
        else
        if( mixed!=null && !"false".equals(mixed) )
            reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "mixed", mixed );
            // recover by ignoring this error.

        decl.body.exp = contentType;

        if( isRedefine() ) {
            // copy new definition back into the original definition.
            oldDecl.redefine(decl);
            decl = (ComplexTypeExp)oldDecl;
        }
        
        reader.setDeclaredLocationOf(decl);
        reader.setDeclaredLocationOf(decl.body);
        return decl;
    }
}
