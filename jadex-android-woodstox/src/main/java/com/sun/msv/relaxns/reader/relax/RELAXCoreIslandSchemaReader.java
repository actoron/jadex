/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader.relax;

import java.util.Map;
import java.util.Set;

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandSchemaReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.relaxns.grammar.ExternalAttributeExp;
import com.sun.msv.relaxns.grammar.ExternalElementExp;
import com.sun.msv.relaxns.grammar.relax.RELAXIslandSchema;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;

/**
 * reads RELAX-Namespace-extended RELAX Core.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXCoreIslandSchemaReader extends RELAXCoreReader
    implements IslandSchemaReader {
    
    public RELAXCoreIslandSchemaReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool,
        String expectedTargetnamespace )
        throws SAXException,ParserConfigurationException
    {
        super(controller,parserFactory,new StateFactory(),pool,expectedTargetnamespace);
    }
    
    private static class StateFactory extends RELAXCoreReader.StateFactory {
        public State interface_(State parent,StartTagInfo tag) { return new InterfaceStateEx(); }
    }
    
    // to allow access within this package.
    protected RELAXModule getModule() { return super.module; }

    /** returns true if the given state can have "occurs" attribute. */
    protected boolean canHaveOccurs( ExpressionState state )
    {
        return super.canHaveOccurs(state) || state instanceof AnyOtherElementState;
    }

    public final IslandSchema getSchema() {
        RELAXModule m = getResult();
        if(m==null)        return null;
        else            return new RELAXIslandSchema( m, pendingAnyOtherElements );
    }
    
    public State createExpressionChildState( State parent,StartTagInfo tag )
    {
        if(! RELAXCoreNamespace.equals(tag.namespaceURI) )    return null;

        if(tag.localName.equals("anyOtherElement"))    return new AnyOtherElementState();
        return super.createExpressionChildState(parent,tag);
    }
    
    /** map from StringPair(namespace,label) to ExternalElementExp. */
    private final Map<StringPair,Expression> externalElementExps = new java.util.HashMap<StringPair,Expression>();
    private ExternalElementExp getExtElementExp( String namespace, String label )
    {
        StringPair name = new StringPair(namespace,label);
        ExternalElementExp exp = (ExternalElementExp)externalElementExps.get(name);
        if( exp!=null )    return exp;
        
        exp = new ExternalElementExp( pool, namespace, label, new LocatorImpl(getLocator()) );
        externalElementExps.put( name, exp );
        return exp;
    }
    
    protected Expression resolveElementRef( String namespace, String label )
    {
        if( namespace!=null )
            return getExtElementExp( namespace, label );
        else
            return super.resolveElementRef(namespace,label);
    }
    protected Expression resolveHedgeRef( String namespace, String label )
    {
        if( namespace!=null )
            return getExtElementExp( namespace, label );
        else
            return super.resolveHedgeRef(namespace,label);
    }
    protected Expression resolveAttPoolRef( String namespace, String label )
    {
        if( namespace!=null )
            return new ExternalAttributeExp(pool,namespace,label,new LocatorImpl(getLocator()));
        else
            return super.resolveAttPoolRef(namespace,label);
    }

    
    /**
     * set of AnyOtherElementExp object.
     * 
     * each object will be invoked to do a wrap up by bind method of IslandSchema.
     */
    protected final Set<Expression> pendingAnyOtherElements = new java.util.HashSet<Expression>();
}
