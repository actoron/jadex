/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.verifier;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.iso_relax.dispatcher.AttributesDecl;
import org.iso_relax.dispatcher.AttributesVerifier;
import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.relaxns.grammar.ExternalAttributeExp;
import com.sun.msv.relaxns.grammar.ExternalElementExp;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * base implementation of IslandSchema for MSV.
 * 
 * the iso_relax package doesn't have the distinction between AGM and VGM.
 * For the safety, the implementation of the createNewVerifier method creates
 * a new VGM everytime it is called.
 * 
 * Fortunately, when all island schemas are from MSV, the application can simply
 * treat RELAXGrammar as a normal Grammar object; there is no need to use
 * Dispatcher nor any divide-and-validate framework.
 * 
 * So createNewVerifier method is called only when
 * <ul>
 *  <li>
 *        MSV is used by other RELAX Namespace
 *        implementation or
 *  <li>
 *        other IslandSchema implemntations are used by MSV's RELAXNSReader.
 * </ul>
 * 
 * In that case, the current createNewVerifier method causes a performance problem.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public abstract class IslandSchemaImpl
            implements IslandSchema, java.io.Serializable {
    
    /** map from name to DeclImpl. */
    protected final Map<String,ElementDecl> elementDecls = new java.util.HashMap<String,ElementDecl>();
    
    /** map from name to DeclImpl. */
    protected final Map<String,AttributesDecl> attributesDecls = new java.util.HashMap<String,AttributesDecl>();
    
    public IslandVerifier createNewVerifier( String namespace, ElementDecl[] rules ) {
        // see the class comment.
        // this method is invoked only under certain limited situations.
        DeclImpl[] ri = new DeclImpl[rules.length];
        System.arraycopy( rules,0, ri,0, rules.length );
        
        return new TREXIslandVerifier(
            new RulesAcceptor( 
                new REDocumentDeclaration( getGrammar() ), ri ) );
    }
    
    /** get the grammar object that represents this island. */
    protected abstract Grammar getGrammar();
    
    public ElementDecl getElementDeclByName( String name ) {
        return (ElementDecl)elementDecls.get(name);
    }
    
    public Iterator<ElementDecl> iterateElementDecls() {
        return elementDecls.values().iterator();
    }
    
    public ElementDecl[] getElementDecls() {
        ElementDecl[] r = new DeclImpl[elementDecls.size()];
        elementDecls.values().toArray(r);
        return r;
    }

    public AttributesDecl getAttributesDeclByName( String name ) {
        return (AttributesDecl)attributesDecls.get(name);
    }
    
    public Iterator<AttributesDecl> iterateAttributesDecls() {
        return attributesDecls.values().iterator();
    }
    
    public AttributesDecl[] getAttributesDecls() {
        AttributesDecl[] r = new DeclImpl[attributesDecls.size()];
        attributesDecls.values().toArray(r);
        return r;
    }

    public AttributesVerifier createNewAttributesVerifier(
        String namespaceURI, AttributesDecl[] decls ) {
        throw new Error("not implemented");
    }
    
    protected void bind( ReferenceContainer con, Binder binder ) {
        ReferenceExp[] exps = con.getAll();
        for( int i=0; i<exps.length; i++ )
            exps[i].exp = exps[i].exp.visit(binder);
    }
    
    /**
     * replaces all ExternalElementExp and ExternalAttributeExp
     * by actual definitions.
     * 
     * these two expressions forms the fundamental mechanism of schema interaction.
     */
    public static class Binder extends ExpressionCloner    {
        
        protected final SchemaProvider provider;
        protected final ErrorHandler errorHandler;
        private final Set<Object> boundElements = new java.util.HashSet<Object>();
        
        public Binder(SchemaProvider provider,ErrorHandler errorHandler,ExpressionPool pool)
        {
            super(pool);
            this.provider=provider;
            this.errorHandler=errorHandler;
        }
        
        public Expression onAttribute(AttributeExp exp) { return exp; }
        public Expression onRef(ReferenceExp exp) {
            return exp.exp.visit(this);
        }
        
        public Expression onOther(OtherExp exp) {
            try {
                if( exp instanceof ExternalAttributeExp ) {
                    ExternalAttributeExp eexp = (ExternalAttributeExp)exp;
                    
                    IslandSchema is = provider.getSchemaByNamespace(eexp.namespaceURI);
                    if(is==null) {
                        errorHandler.error( new SAXParseException(
                            localize(ERR_UNDEFINED_NAMESPACE, eexp.namespaceURI),
                            eexp.source) );
                        return exp;
                    }
                    
                    AttributesDecl rule = is.getAttributesDeclByName(eexp.role);
                    if(rule==null) {
                        errorHandler.error( new SAXParseException(
                            localize(ERR_UNEXPORTED_ATTRIBUTE_DECL, eexp.role),
                            eexp.source) );
                        return exp;
                    }
                    
                    if(!(rule instanceof DeclImpl)) {
                        errorHandler.error( new SAXParseException(
                            localize(ERR_UNSUPPROTED_ATTRIBUTES_IMPORT),
                            eexp.source) );
                        return exp;
                    }
                    
                    // bind directly.
                    return ((DeclImpl)rule).exp;
                }
                
                // we don't know how to treat this expression.
                // so simply remove it.
                return exp.exp.visit(this);
            } catch( SAXException e ) {
                return exp;    // ignore this expcetion
            }
        }
        public Expression onElement(ElementExp exp) {
            try {
                if(!(exp instanceof ExternalElementExp)) {
                    // avoid visiting the same element twice to prevent infinite recursion.
                    if( boundElements.contains(exp) )    return exp;
                    boundElements.add(exp);
                    
                    // bind content model
                    exp.contentModel = exp.contentModel.visit(this);
                    return exp;
                }
            
                ExternalElementExp eexp = (ExternalElementExp)exp;
                IslandSchema is = provider.getSchemaByNamespace(eexp.namespaceURI);
                if(is==null) {
                    errorHandler.error( new SAXParseException(
                        localize(ERR_UNDEFINED_NAMESPACE, eexp.namespaceURI),
                        eexp.source) );
                    return exp;
                }
                eexp.rule = is.getElementDeclByName(eexp.ruleName);
                if(eexp.rule==null)
                {
                    errorHandler.error( new SAXParseException(
                        localize(ERR_UNEXPORTED_ELEMENT_DECL, eexp.ruleName),
                        eexp.source) );
                    return exp;
                }
                if(eexp.rule instanceof DeclImpl)
                {
                    // if this rule is from our own implementation,
                    // we can bind "directly" so that we don't have to switch the island.
                    return ((DeclImpl)eexp.rule).exp;
                }
            
                // all set.
                return exp;
            } catch( SAXException e ) {
                // ignore the exception
                return exp;
            }
        }
    
        /**
         * localizes messages.
         * derived class can override this method to provide schema languagespecific
         * error messages.
         */
        public String localize( String propertyName, Object[] args ) {
            String format = java.util.ResourceBundle.getBundle(
                "com.sun.msv.relaxns.verifier.Messages").getString(propertyName);
            return java.text.MessageFormat.format(format, args );
        }
        
        public String localize( String prop )
        { return localize( prop, null ); }
    
        public String localize( String prop, Object arg1 )
        { return localize( prop, new Object[]{arg1} ); }

        public String localize( String prop, Object arg1, Object arg2 )
        { return localize( prop, new Object[]{arg1,arg2} ); }
    
        public static final String ERR_UNEXPORTED_ELEMENT_DECL = // arg:1
            "IslandSchemaImpl.UnexportedElementDecl";            
        public static final String ERR_UNDEFINED_NAMESPACE = // arg:1
            "IslandSchemaImpl.UndefinedNamespace";
        public static final String ERR_UNEXPORTED_ATTRIBUTE_DECL =     // arg:1
            "IslandSchemaImpl.UnexportedAttributeDecl";
        public static final String ERR_UNSUPPROTED_ATTRIBUTES_IMPORT =     // arg:1
            "IslandSchemaImpl.UnsupportedAttributesImport";
    }
    
    
}
