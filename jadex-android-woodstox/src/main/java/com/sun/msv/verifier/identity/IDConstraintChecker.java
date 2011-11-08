/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.identity;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.relaxng.datatype.Datatype;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.LightStack;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.ErrorInfo;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;

/**
 * Verifier with XML Schema-related enforcement.
 * 
 * <p>
 * This class can be used in the same way as {@link Verifier}.
 * This class also checks XML Schema's identity constraint.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDConstraintChecker extends Verifier {

    public IDConstraintChecker( XMLSchemaGrammar grammar, ErrorHandler errorHandler ) {
        super(new XSREDocDecl(grammar),errorHandler);
        this.grammar = grammar;
    }
    
    /** the grammar object against which we are validating. */
    protected final XMLSchemaGrammar grammar;
    
    /** active mathcers. */
    protected final Vector<Matcher> matchers = new Vector<Matcher>();
    
    protected void add( Matcher matcher ) {
        matchers.add(matcher);
    }
    protected void remove( Matcher matcher ) {
        matchers.remove(matcher);
    }
    
    /**
     * a map from <code>SelectorMatcher</code> to set of <code>KeyValue</code>s.
     * 
     * One SelectorMatcher correponds to one scope of the identity constraint.
     */
    private final Map<SelectorMatcher,Set<Object>> keyValues = new java.util.HashMap<SelectorMatcher,Set<Object>>();
    
    /**
     * a map from keyref <code>SelectorMatcher</code> to key/unique
     * <code>SelectorMatcher</code>.
     * 
     * Given a keyref scope, this map stores which key scope should it refer to.
     */
    private final Map<SelectorMatcher,SelectorMatcher> referenceScope = new java.util.HashMap<SelectorMatcher,SelectorMatcher>();
    
    /**
     * a map from <code>IdentityConstraint</code> to a <code>LightStack</code> of
     * <code>SelectorMatcher</code>.
     * 
     * Each stack top keeps the currently active scope for the given IdentityConstraint.
     */
    private final Map<IdentityConstraint,LightStack> activeScopes = new java.util.HashMap<IdentityConstraint,LightStack>();
    protected SelectorMatcher getActiveScope( IdentityConstraint c ) {
        LightStack s = activeScopes.get(c);
        if(s==null) return null;
        if(s.size()==0) return null;
        return (SelectorMatcher)s.top();
    }
    protected void pushActiveScope( IdentityConstraint c, SelectorMatcher matcher ) {
        LightStack s = activeScopes.get(c);
        if(s==null) {
            activeScopes.put(c,s=new LightStack());
        }
        s.push(matcher);
    }
    protected void popActiveScope( IdentityConstraint c, SelectorMatcher matcher ) {
        LightStack s = activeScopes.get(c);
        if(s==null) {
            // since it's trying to pop, there must be a non-empty stack.
            throw new Error();
        }
        if(s.pop()!=matcher) {
            // trying to pop a non-active scope.
            throw new Error();
        }
    }
        
    
    /**
     * adds a new KeyValue to the value set.
     * @return true        if this is a new value.
     */
    protected boolean addKeyValue( SelectorMatcher scope, KeyValue value ) {
        Set<Object> keys = keyValues.get(scope);
        if(keys==null) {
            keyValues.put(scope, keys = new java.util.HashSet<Object>());
        }
        return keys.add(value);
    }
    /**
     * gets the all <code>KeyValue</code>s that were added within the specified scope.
     */
    protected KeyValue[] getKeyValues( SelectorMatcher scope ) {
        Set<Object> keys = keyValues.get(scope);
        if(keys==null) {
            return new KeyValue[0];
        }
        return (KeyValue[])keys.toArray(new KeyValue[keys.size()]);
    }
    
    public void startDocument() throws SAXException {
        super.startDocument();
        keyValues.clear();
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        
        // keyref check
        @SuppressWarnings("unchecked")
        Map.Entry<Object,Object>[] scopes = (Map.Entry<Object,Object>[])
            keyValues.entrySet().toArray(new Map.Entry<?,?>[keyValues.size()]);
        
        for( int i=0; i<scopes.length; i++ ) {
            final SelectorMatcher key = (SelectorMatcher)scopes[i].getKey();
            final Set<?> value = (Set<?>)scopes[i].getValue();
            
            if( key.idConst instanceof KeyRefConstraint ) {
                // get the set of corresponding keys.
                Set<Object> keys = keyValues.get( referenceScope.get(key) );
                KeyValue[] keyrefs = (KeyValue[])
                    value.toArray(new KeyValue[value.size()]);
                
                for( int j=0; j<keyrefs.length; j++ ) {
                    if( keys==null || !keys.contains(keyrefs[j]) )
                        // this keyref doesn't have a corresponding key.
                        reportError( keyrefs[j].locator, null, ERR_UNDEFINED_KEY,
                            new Object[]{
                                key.idConst.namespaceURI,
                                key.idConst.localName} );
                }
            }
        }
    }
    
    protected void onNextAcceptorReady( StartTagInfo sti, Acceptor next ) throws SAXException {
        
        // call matchers
        int len = matchers.size();
        for( int i=0; i<len; i++ ) {
            Matcher m = (Matcher)matchers.get(i);
            m.startElement(sti.namespaceURI,sti.localName);
        }
        
        // introduce newly found identity constraints.
        Object e = next.getOwnerType();
        if( e instanceof ElementDeclExp.XSElementExp ) {
            ElementDeclExp.XSElementExp exp = (ElementDeclExp.XSElementExp)e;
            if( exp.identityConstraints!=null ) {
                int m = exp.identityConstraints.size();
                for( int i=0; i<m; i++ )
                    add( new SelectorMatcher( this,
                            (IdentityConstraint)exp.identityConstraints.get(i),
                            sti.namespaceURI, sti.localName ) );
                
                // SelectorMathcers will register themselves as active scopes 
                // in their constructor.
                
                // augment the referenceScope field by adding newly introduced keyrefs.
                for( int i=0; i<m; i++ ) {
                    IdentityConstraint c = (IdentityConstraint)
                        exp.identityConstraints.get(i);
                    if(c instanceof KeyRefConstraint) {
                        SelectorMatcher keyScope =
                            getActiveScope( ((KeyRefConstraint)c).key );
                        if(keyScope==null)
                            ;    // there is no active scope of the key scope now.
                        
                        referenceScope.put(
                            getActiveScope(c),
                            keyScope );
                    }
                }
            }
        }
    }

    protected Datatype[] feedAttribute( Acceptor child, String uri, String localName, String qName, String value ) throws SAXException {
        Datatype[] result = super.feedAttribute( child, uri, localName, qName, value );
        
        final int len = matchers.size();
        // call matchers for attributes.
        for( int i=0; i<len; i++ ) {
            Matcher m = (Matcher)matchers.get(i);
            m.onAttribute( uri, localName, value, 
                (result==null || result.length==0)?null:result[0] );
        }
        
        return result;
    }

    
    
    public void characters( char[] buf, int start, int len ) throws SAXException {
        super.characters(buf,start,len);
        
        int m = matchers.size();
        for( int i=0; i<m; i++ )
            ((Matcher)matchers.get(i)).characters(buf,start,len);
    }


    public void endElement( String namespaceUri, String localName, String qName )
                                throws SAXException {
        super.endElement(namespaceUri,localName,qName);
        
        // getLastCharacterType may sometimes return null. For example,
        // 1) this element should be empty and there was only whitespace characters.
        Datatype dt;
        Datatype[] lastType = getLastCharacterType();
        if( lastType==null || lastType.length==0 )    dt = null;
        else                                        dt = getLastCharacterType()[0];
            
        // call matchers
        int len = matchers.size();
        for( int i=len-1; i>=0; i-- ) {
            // Matcher may remove itself from the vector.
            // Therefore, to make it work correctly, we have to
            // enumerate Matcher in reverse direction.
            ((Matcher)matchers.get(i)).endElement( dt );
        }
    }
    

    
    /** reports an error. */
    protected void reportError( ErrorInfo ei, String propKey, Object[] args ) throws SAXException {
        // use the current location.
        reportError( getLocator(), ei, propKey, args );
    }
    
    protected void reportError( Locator loc, ErrorInfo ei, String propKey, Object[] args ) throws SAXException {
        hadError = true;
        errorHandler.error( new ValidityViolation( loc,
                localizeMessage(propKey,args), ei ) );
    }
    
    public static String localizeMessage( String propertyName, Object arg ) {
        return localizeMessage( propertyName, new Object[]{arg} );
    }

    public static String localizeMessage( String propertyName, Object[] args ) {
        String format = java.util.ResourceBundle.getBundle(
            "com.sun.msv.verifier.identity.Messages").getString(propertyName);
        
        return java.text.MessageFormat.format(format, args );
    }
    
    public static final String ERR_UNMATCHED_KEY_FIELD =
        "IdentityConstraint.UnmatchedKeyField";    // arg :3
    public static final String ERR_NOT_UNIQUE =
        "IdentityConstraint.NotUnique"; // arg:2
    public static final String ERR_NOT_UNIQUE_DIAG =
        "IdentityConstraint.NotUnique.Diag";    // arg:2
    public static final String ERR_DOUBLE_MATCH =
        "IdentityConstraint.DoubleMatch"; // arg:3
    public static final String ERR_UNDEFINED_KEY =
        "IdentityConstraint.UndefinedKey"; // arg:2 
    
}
