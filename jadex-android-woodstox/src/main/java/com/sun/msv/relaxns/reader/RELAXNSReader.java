/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader;

import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.dispatcher.IslandSchemaReader;
import org.iso_relax.dispatcher.SchemaProvider;
import org.relaxng.datatype.Datatype;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.relax.RELAXReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.relaxns.grammar.ExternalElementExp;
import com.sun.msv.relaxns.grammar.RELAXGrammar;
import com.sun.msv.util.StartTagInfo;

/**
 * parses RELAX Namespace XML and constructs a SchemaProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNSReader extends RELAXReader {
    
    /** namespace URI of RELAX Namespace. */
    public static final String RELAXNamespaceNamespace = "http://www.xml.gr.jp/xmlns/relaxNamespace";
    
    /** loads RELAX grammar */
    public static RELAXGrammar parse( String moduleURL,
        SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool )
    {
        RELAXNSReader reader = new RELAXNSReader(controller,factory,pool);
        reader.parse(moduleURL);
        
        return reader.getResult();
    }

    /** loads RELAX grammar */
    public static RELAXGrammar parse( InputSource module,
        SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool )
    {
        RELAXNSReader reader = new RELAXNSReader(controller,factory,pool);
        reader.parse(module);
        
        return reader.getResult();
    }
    
    public RELAXNSReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool )
    {
        super(controller,parserFactory,new StateFactory(),pool,new RootGrammarState());
        grammar = new RELAXGrammar(pool);
    }
    
    /** RELAX grammar that is currentlt being loaded */
    public final RELAXGrammar grammar;

    /** obtains parsed grammar object only if parsing was successful. */
    public final RELAXGrammar getResult() {
        if(controller.hadError())    return null;
        else                        return grammar;
    }
    public Grammar getResultAsGrammar() {
        return getResult();
    }
    
    protected SchemaProvider schemaProvider;
    /** obtains parsed grammar object as SchemaProvider
     * only if parsing was successful. */
    public final SchemaProvider getSchemaProvider() {
        if(controller.hadError())    return null;
        else                        return schemaProvider;
    }
    
    
    /**
     * creates an {@link IslandSchemaReader} that can parse the specified language.
     * 
     * This method can be overrided by the derived class to incorporate other
     * language implementations.
     * 
     * @return
     *        return null if the given language is unrecognized.
     *        error will be handled by the caller. So this method should not attempt
     *        to report nor recover from error.
     */
    public IslandSchemaReader getIslandSchemaReader(
        String language, String expectedTargetNamespace ) {
        
        try {
            if( language.equals( RELAXCoreNamespace ) )    // RELAX Core
                return new com.sun.msv.relaxns.reader.relax.RELAXCoreIslandSchemaReader(
                    controller,parserFactory,(ExpressionPool)pool,expectedTargetNamespace);
            if( language.equals( TREXGrammarReader.TREXNamespace ) ) // TREX
                return new com.sun.msv.relaxns.reader.trex.TREXIslandSchemaReader(
                    new TREXGrammarReader(
                        controller,parserFactory,
                        new com.sun.msv.reader.trex.classic.TREXGrammarReader.StateFactory(),
                        pool) );

        } catch( javaxx.xml.parsers.ParserConfigurationException e ) {
            controller.error(e, null );
        } catch( SAXException e ) {
            controller.error(e, null );
        }

        return null;
    }
    
    public Datatype resolveDataType( String typeName ) {
        // should never be called.
        // because in top-level content model, datatype reference can never occur.
        throw new Error();
    }
    
    protected boolean isGrammarElement( StartTagInfo tag )
    {
        if( !RELAXNamespaceNamespace.equals(tag.namespaceURI) )
            return false;
        
        // annotation is ignored at this level.
        // by returning false, the entire subtree will be simply ignored.
        if(tag.localName.equals("annotation"))    return false;
        
        return true;
    }
    
    protected Expression resolveElementRef( String namespace, String label ) {
        return resolveRef(namespace,label,"ref");
    }
    
    protected Expression resolveHedgeRef( String namespace, String label ) {
        return resolveRef(namespace,label,"hedgeRef");
    }
    
    private Expression resolveRef( String namespace, String label, String tagName ) {
        if( namespace==null ) {
            reportError( ERR_MISSING_ATTRIBUTE, tagName, "namespace" );
            return Expression.nullSet;
        }
        return new ExternalElementExp( pool, namespace, label, new LocatorImpl(getLocator()) );
    }
    
    
    
    protected String localizeMessage( String propertyName, Object[] args ) {
        return super.localizeMessage(propertyName,args);
    }
    
    public static final String WRN_ILLEGAL_RELAXNAMESPACE_VERSION    // arg:1
        = "RELAXNSReader.Warning.IllegalRelaxNamespaceVersion";
    public static final String ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE    // arg:0
        = "RELAXNSReader.TopLevelParticleMustBeRelaxCore";
    public static final String ERR_INLINEMODULE_NOT_FOUND    // arg:0
        = "RELAXNSReader.InlineModuleNotFound";
    public static final String ERR_UNKNOWN_LANGUAGE
        = "RELAXNSReader.UnknownLanguage";    // arg:1
    public static final String ERR_NAMESPACE_COLLISION    // arg:1
        = "RELAXNSReader.NamespaceCollision";
}
