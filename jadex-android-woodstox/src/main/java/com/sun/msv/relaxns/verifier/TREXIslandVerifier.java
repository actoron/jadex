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

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.iso_relax.dispatcher.Dispatcher;
import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.relaxns.grammar.ExternalElementExp;
import com.sun.msv.relaxns.grammar.relax.AnyOtherElementExp;
import com.sun.msv.verifier.regexp.ComplexAcceptor;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.SimpleAcceptor;

/**
 * IslandVerifier for RELAX Core and TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TREXIslandVerifier
    extends com.sun.msv.verifier.Verifier
    implements org.iso_relax.dispatcher.IslandVerifier
{
    protected Dispatcher dispatcher;
    
    public void setDispatcher( Dispatcher disp ) {
        this.dispatcher = disp;
        this.errorHandler = new ErrorHandlerAdaptor( disp );
    }
    
    /**
     * lazily constructed map from Rule object to ExternalElementExp.
     * 
     * Rule object <code>r</code> and ExternalElementExp whose rule field
     * is <code>r</code> are registered to this map when child island is found.
     * 
     * this map is used in endChildIsland method.
     */
    protected final Map<ElementDecl,Expression> rule2exp = new java.util.HashMap<ElementDecl,Expression>();
    
    TREXIslandVerifier( RulesAcceptor initialAcceptor ) {
        super( null, null );        // quick hack.
        current = initialAcceptor;
    }
    
    public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
        throws SAXException {
        super.startElement(namespaceUri,localName,qName,atts);
        
        // check current Acceptor object to see if switching is necessary.
        
        if( current instanceof SimpleAcceptor ) {
            SimpleAcceptor sa = (SimpleAcceptor)current;
            
            // if sa.owner==null, we are in error recovery mode.
            // so don't let another IslandVerifier kick in.
            // just continue validation by using the current handler.
            
            if( sa.owner instanceof ExternalElementExp ) {
                // switch to the child island.
                switchToChildIsland( new ExternalElementExp[]{(ExternalElementExp)sa.owner}, namespaceUri, localName, qName, atts );
                return;
            }
            
            if( sa.owner instanceof AnyOtherElementExp ) {
                // switch to anyOtherElement.
                switchToAnyOtherElement( new AnyOtherElementExp[]{(AnyOtherElementExp)sa.owner}, namespaceUri, localName, qName, atts );
                return;
            }
            
            return;
        }
        
        if( current instanceof ComplexAcceptor ) {
            ComplexAcceptor ca = (ComplexAcceptor)current;
            
            Vector<Object> vec = null;
            
            for( int i=0; i<ca.owners.length; i++ )
                if( ca.owners[i] instanceof ExternalElementExp ) {    
                    // bingo
                    if(vec==null)    vec=new Vector<Object>();
                    vec.add( ca.owners[i] );
                }
            
            // ExternalElementExp can be mixed with normal ElementExps.
            // e.g., TREX:
            //    <choice>
            //        <import label="..." namespace="..." />   <!-- import -->
            //        <element><anyName /> ... </element>
            //    </choice>
            
            // Even if this is the case, this implementation switches to
            // the new child Verifier. Therefore
            //        <element><anyName /> ... </element>
            // won't be used.
            
            if( vec!=null ) {
                // switch to the child island.
                ExternalElementExp[] exps = new ExternalElementExp[vec.size()];
                vec.toArray(exps);
                switchToChildIsland(exps, namespaceUri, localName, qName, atts );
                return;
            }

            // see if there is anyOtherElementExp
            for( int i=0; i<ca.owners.length; i++ )
                if( ca.owners[i] instanceof AnyOtherElementExp ) {    
                    // bingo
                    if(vec==null) vec=new Vector<Object>();
                    vec.add( ca.owners[i] );
                }
            
            if( vec!=null ) {
                // switch to anyOtherElement state.
                AnyOtherElementExp[] exps = new AnyOtherElementExp[vec.size()];
                vec.toArray(exps);
                switchToAnyOtherElement(exps, namespaceUri, localName, qName, atts );
                return;
            }

            return;
        }
        
        // Acceptor must be SimpleAcceptor or ComplexAcceptor.
        // we don't know how to handle other acceptors.
        throw new Error();    // assertion failed.
    }
    
    /**
     * switch to another IslandVerifier.
     */
    protected void switchToChildIsland( ExternalElementExp[] exps,
        String namespaceUri, String localName, String qName, Attributes atts )
        throws SAXException {
        // we've found ExternalElementExps.
        // switch to another IslandVerifier to have it validate them.
            
        // remember tag names (these will be used in endChildIsland method)
        lastNamaespaceUri = namespaceUri;
        lastLocalName = localName;
        lastQName = qName;
            
        // memorize ExternalElementExps to the map
        // so that it will be easy to obtain what ExternalElementExps are accepted.
        ElementDecl[] rules = new ElementDecl[exps.length];
        for( int i=0; i<exps.length; i++ ) {
            rules[i] = exps[i].rule;
            rule2exp.put( rules[i], exps[i] );
        }
            
        if( rule2exp.size()!=rules.length )
            // no two ExternalElementExp shall never share the same Rule object.
            throw new Error();
            
        // error check is already done in bind phase.
        // thus getSchemaByNamespace shall never fail.
        IslandSchema is = dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceUri);

        // switch to the child Verifier.
        IslandVerifier iv = is.createNewVerifier(namespaceUri,rules);
        dispatcher.switchVerifier( iv );
        
        // simulate this startElement event
        iv.startElement( namespaceUri, localName, qName, atts );
    }
    
    
    /**
     * switch to another IslandVerifier to validate anyOtherElement.
     */
    protected void switchToAnyOtherElement( AnyOtherElementExp[] exps,
        String namespaceUri, String localName, String qName, Attributes atts )
        throws SAXException {

        // memorize AnyOtherElementExps to the map
        for( int i=0; i<exps.length; i++ ) {
            rule2exp.put( exps[i], exps[i] );
        }
        IslandVerifier iv = new AnyOtherElementVerifier(exps);
        dispatcher.switchVerifier(iv);
        
        // remember tag names (these will be used in endChildIsland method)
        lastNamaespaceUri = namespaceUri;
        lastLocalName = localName;
        lastQName = qName;
        
        // simulate this startElement event
        iv.startElement( namespaceUri, localName, qName, atts );
    }
    
    private String lastNamaespaceUri;
    private String lastLocalName;
    private String lastQName;
    
    public void endChildIsland( String childURI, ElementDecl[] ruleSet ) throws SAXException
    {
        ElementExp[] exps = new ElementExp[ruleSet.length];
        for( int i=0; i<ruleSet.length; i++ )
        {
            exps[i] = (ElementExp)rule2exp.get(ruleSet[i]);
            if( exps[i]==null )    throw new Error();    // assertion failed.
                                                    // it must be registered.
        }
        
        Expression [] epsilons = new Expression[exps.length];
        for( int i=0; i<epsilons.length; i++ )
            epsilons[i] = Expression.epsilon;
        
        // change current Acceptor to a new Acceptor.
        // this new Acceptor is made to accept those satisfied rules only.
        current = new ComplexAcceptor(
            (REDocumentDeclaration)docDecl,
            (ruleSet.length==0)?Expression.nullSet:Expression.epsilon,
            epsilons, exps );

        // call endElement to let Verifier do the job.
        super.endElement( lastNamaespaceUri, lastLocalName, lastQName );
    }
    
    public ElementDecl[] endIsland() {
        return ((RulesAcceptor)current).getSatisfiedElementDecls();
    }
    
    /**
     * set of unparsed entity names.
     * this set is created on demand.
     */
    private Set<String> unparsedEntities;
    
    // IslandVerifier resolves unparsed entity through dispatcher
    public boolean isUnparsedEntity( String entityName ) {
        // create the set only when it is used.
        if( unparsedEntities==null ) {
            unparsedEntities = new java.util.HashSet<String>();
            int len = dispatcher.countUnparsedEntityDecls();
            for( int i=0; i<len; i++ )
                unparsedEntities.add( dispatcher.getUnparsedEntityDecl(i).name );
        }
        
        return unparsedEntities.contains(entityName);
    }

}
