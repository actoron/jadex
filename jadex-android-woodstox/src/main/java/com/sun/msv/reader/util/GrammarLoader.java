/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.util;

import java.util.Vector;

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFilter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.Controller;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.ng.comp.RELAXNGCompReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.relaxns.reader.RELAXNSReader;
import com.sun.msv.util.Util;
import com.sun.msv.verifier.jaxp.SAXParserFactoryImpl;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;


/**
 * loads any supported grammar (except XML DTD)
 * by automatically detecting the schema language.
 * 
 * <p>
 * The static version of loadVGM/loadSchema methods provides simple ways to
 * load a grammar.
 * 
 * <p>
 * Another way to use GrammarLoader is
 * 
 * <ol>
 *  <li>To instanciate an object of GrammarLoader
 *  <li>call setXXX methods to configure the parameters
 *  <li>call loadSchema/loadVGM methods (possibly multiple times) to
 *      load grammars.
 * </ol>
 * 
 * This approach will give you finer control.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarLoader
{
//
// static utility methods
//==============================
//
    /**
     * parses the specified schema and returns the result as a VGM.
     * 
     * This method is an utility method for those applications which
     * don't need AGM (e.g., a single thread application).
     * 
     * @return
     *        null if there was an error in the grammar.
     */
    public static REDocumentDeclaration loadVGM( String url,
        GrammarReaderController controller,
        SAXParserFactory factory )
        throws SAXException, ParserConfigurationException, java.io.IOException
    {
        Grammar g = loadSchema(url,controller,factory);
        if(g!=null)        return wrapByVGM(g);
        else            return null;
    }
    
    public static REDocumentDeclaration loadVGM( InputSource source,
        GrammarReaderController controller,
        SAXParserFactory factory )
        throws SAXException, ParserConfigurationException, java.io.IOException
    {
        Grammar g = loadSchema(source,controller,factory);
        if(g!=null)        return wrapByVGM(g);
        else            return null;
    }
    
    private static REDocumentDeclaration wrapByVGM( Grammar g ) {
        if( g instanceof XMLSchemaGrammar )
            return new XSREDocDecl((XMLSchemaGrammar)g);
        else
            return new REDocumentDeclaration(g);
    }
    
    
    /**
     * parses the specified schema and returns the result as a VGM.
     * 
     * This method uses the default SAX parser and throws an exception
     * if there is an error in the schema.
     * 
     * @return
     *        non-null valid VGM object.
     */
    public static REDocumentDeclaration loadVGM( String url )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        try {
            return loadVGM(url, new ThrowController(), null );
        } catch( GrammarLoaderException e ) {
            throw e.e;
        }
    }
    public static REDocumentDeclaration loadVGM( InputSource source )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        try {
            return loadVGM(source, new ThrowController(), null );
        } catch( GrammarLoaderException e ) {
            throw e.e;
        }
    }
    
    /** wrapper exception so that we can throw it from the GrammarReaderController. */
    @SuppressWarnings("serial")
    private static class GrammarLoaderException extends RuntimeException {
        GrammarLoaderException( SAXException e ) {
            super(e.getMessage());
            this.e = e;
        }
        public final SAXException e;
    }
    private static class ThrowController implements GrammarReaderController {
        public void warning( Locator[] locs, String errorMessage ) {}
        public void error( Locator[] locs, String errorMessage, Exception nestedException ) {
            for( int i=0; i<locs.length; i++ )
                if(locs[i]!=null)
                    throw new GrammarLoaderException(
                        new SAXParseException(errorMessage,locs[i],nestedException));
            
            throw new GrammarLoaderException(
                new SAXException(errorMessage,nestedException));
        }
        public InputSource resolveEntity( String p, String s ) { return null; }
        
    }

    
    
    /**
     * parses the specified schema and returns the result as a Grammar object.
     * 
     * @return
     *        null if there was an error in the grammar.
     */
    public static Grammar loadSchema( String url,
        GrammarReaderController controller,
        SAXParserFactory factory )
        throws SAXException, ParserConfigurationException, java.io.IOException
    {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        loader.setSAXParserFactory(factory);
        return loader.parse(url);
    }
    
    public static Grammar loadSchema( InputSource source,
        GrammarReaderController controller,
        SAXParserFactory factory )
        throws SAXException, ParserConfigurationException, java.io.IOException
    {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        loader.setSAXParserFactory(factory);
        return loader.parse(source);
    }
    
    /**
     * returns a thread-safe AGM object, depending on the language used.
     */
    public static Grammar loadSchema( String source,
        GrammarReaderController controller )
            throws SAXException, ParserConfigurationException, java.io.IOException
    {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        return loader.parse(source);
    }

    /**
     * returns a thread-safe AGM object, depending on the language used.
     */
    public static Grammar loadSchema( InputSource source,
        GrammarReaderController controller )
            throws SAXException, ParserConfigurationException, java.io.IOException
    {
        GrammarLoader loader = new GrammarLoader();
        loader.setController(controller);
        return loader.parse(source);
    }
    
    /**
     * parses the specified schema and returns the result as a Grammar object.
     * 
     * This method uses the default SAX parser and throws an exception
     * if there is an error in the schema.
     * 
     * @return
     *        a non-null valid Grammar.
     */
    public static Grammar loadSchema( String url )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        try {
            return loadSchema(url, new ThrowController(), null );
        } catch( GrammarLoaderException e ) {
            throw e.e;
        }
    }
    public static Grammar loadSchema( InputSource source )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        try {
            return loadSchema(source, new ThrowController(), null );
        } catch( GrammarLoaderException e ) {
            throw e.e;
        }
    }
    


//
// finer control can be achieved by using the following methods.
//=================================================================
    public GrammarLoader() {}
    
    private SAXParserFactory factory;
    /**
     * sets the factory object which is used to create XML parsers
     * to parse schema files.
     * The factory must be configured to namespace aware.
     * 
     * <p>
     * If no SAXParserFactory is set, then the default parser is used.
     * (The parser that can be obtained by SAXParserFactory.newInstance()).
     */
    public void setSAXParserFactory( SAXParserFactory factory ) {
        this.factory = factory;
    }
    public SAXParserFactory getSAXParserFactory() {
        if(factory==null) {
            factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
        }
        return factory;
    }
    
    private Controller controller;
    /**
     * sets the GrammarReaderController object that will control
     * various aspects of the parsing. If not set, no error report will be
     * done.
     */
    public void setController( GrammarReaderController controller ) {
        this.controller = new Controller(controller);
    }
    public Controller getController() {
        if(controller==null)
            controller = new Controller(new GrammarReaderController() {
                public void warning( Locator[] locs, String errorMessage ) {}
                public void error( Locator[] locs, String errorMessage, Exception nestedException ) {}
                public InputSource resolveEntity( String s, String p ) { return null; }
            });
        return controller;
    }
    
    private ExpressionPool pool;
    /**
     * Sets the ExpressionPool object that will be used during the loading process.
     * If not set, a fresh one is used for each time the loadXXX method is called.
     */
    public void setPool( ExpressionPool pool ) {
        this.pool = pool;
    }
    public ExpressionPool getPool() {
        if( pool==null)        return new ExpressionPool();
        else                return pool;
    }

    
    private boolean strictCheck = false;
    
    /**
     * Sets the strict check flag. If set to true, schema readers will apply
     * stricter checks so that it can find errors in the schema. If set to false,
     * readers will skip some of the checks.
     * 
     * <p>
     * When this flag is set to false, which is the default, the reader may accept
     * incorrect schemas.
     */
    public void setStrictCheck( boolean value ) {
        strictCheck = value;
    }
    public boolean getStrictCheck() {
        return strictCheck;
    }
    
    
    public Grammar parse( InputSource source )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        
        return _loadSchema(source);
    }
    
    public Grammar parse( String url )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        
        return _loadSchema(url);
    }
    
    public REDocumentDeclaration parseVGM( String url )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        
        Grammar g = _loadSchema(url);
        if(g==null)        return null;
        else            return new REDocumentDeclaration(g);
    }
    
    public REDocumentDeclaration parseVGM( InputSource source )
        throws SAXException, ParserConfigurationException, java.io.IOException {
        
        Grammar g = _loadSchema(source);
        if(g==null)        return null;
        else            return new REDocumentDeclaration(g);
    }
    
    
    
    /**
     * Checks if the specified name has ".dtd" extension.
     */
    private boolean hasDTDextension( String name ) {
        if(name==null)        return false;
        
        int idx = name.length()-4;
        if(idx<0)            return false;
        
        return name.substring(idx).equalsIgnoreCase(".dtd");
    }
    
    /**
     * Actual "meat" of parsing schema.
     * 
     * All other methods will ultimately come down to this method.
     */
    private Grammar _loadSchema( Object source )
            throws SAXException, ParserConfigurationException, java.io.IOException {
        
        // perform the auto detection to decide whether
        // it is XML syntax based schema or DTD.
        // TODO: implement more serious detection algorithm.
                
        // use the file extension to decide language type.
        // sure this is a sloppy job, but works in practice.
        // and easy to implement.
        boolean isDTD = false;
        if( source instanceof String ) {
            if( hasDTDextension( (String)source) )
                isDTD = true;
        }
        if( source instanceof InputSource ) {
            if( hasDTDextension( ((InputSource)source).getSystemId() ) )
                isDTD = true;
        }
        
        if(isDTD) {
            // load as DTD
            if( source instanceof String )
                source = Util.getInputSource((String)source);
            return DTDReader.parse((InputSource)source,getController());
        }

        // otherwise this schema is an XML syntax based schema.
        
        
        // this field will receive the grammar reader 
        final GrammarReader[] reader = new GrammarReader[1];
        
        final XMLReader parser = getSAXParserFactory().newSAXParser().getXMLReader();
        /*
            Use a "sniffer" handler and decide which reader to use.
            Once the schema language is detected, the appropriate reader
            instance is created and events are passed to that handler.
        
            From the performance perspective, it is important not to
            create unnecessary reader objects. Because readers typically
            have a lot of references to other classes, instanciating a
            reader instance will cause a lot of class loadings in the first time,
            which makes non-trivial difference in the performance.
        */
        parser.setContentHandler( new DefaultHandler(){
            private Locator locator;
            private Vector<String[]> prefixes = new Vector<String[]>();
            public void setDocumentLocator( Locator loc ) {
                this.locator = loc;
            }
            public void startPrefixMapping( String prefix, String uri ) {
                prefixes.add( new String[]{prefix,uri} );
            }
            
            /**
             * Sets up the pipe line of "VerifierFilter > GrammarReader"
             * so that the grammar will be properly validated.
             */
            private ContentHandler setupPipeline( Schema schema ) throws SAXException {
                try {
                    Verifier v = schema.newVerifier();
                    v.setErrorHandler(getController());
                    v.setEntityResolver(getController());
                    VerifierFilter filter = v.getVerifierFilter();
                    filter.setContentHandler(reader[0]);
                    return (ContentHandler)filter;
                } catch( VerifierConfigurationException vce ) {
                    throw new SAXException(vce);
                }
            }
            
            public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
                                    throws SAXException {
                ContentHandler winner;
                // sniff the XML and decide the reader to use.
                if( localName.equals("module") ) {
                    // assume RELAX Core.
                    if( strictCheck ) {
                        Schema s = RELAXCoreReader.getRELAXCoreSchema4Schema();
                        reader[0] = new RELAXCoreReader(
                            getController(),
                            new SAXParserFactoryImpl(getSAXParserFactory(),s),
                            getPool() );
                        winner = setupPipeline(s);
                    } else {
                        winner = reader[0] = new RELAXCoreReader(
                            getController(),getSAXParserFactory(),getPool());
                    }
                } else
                if( localName.equals("schema") ) {
                    // assume W3C XML Schema
                    if( strictCheck ) {
                        Schema s = XMLSchemaReader.getXmlSchemaForXmlSchema();
                        reader[0] = new XMLSchemaReader(
                            getController(),
                            new SAXParserFactoryImpl(getSAXParserFactory(),s),
                            getPool() );
                        winner = setupPipeline(s);
                    } else {
                        winner = reader[0] = new XMLSchemaReader(
                            getController(),getSAXParserFactory(),getPool());
                    }
                } else
                if( RELAXNSReader.RELAXNamespaceNamespace.equals(namespaceURI) )
                    // assume RELAX Namespace
                    winner = reader[0] = new RELAXNSReader(
                        getController(), getSAXParserFactory(), getPool() );
                else
                if( TREXGrammarReader.TREXNamespace.equals(namespaceURI)
                ||  namespaceURI.equals("") )
                    // assume TREX
                    winner = reader[0] = new TREXGrammarReader(
                        getController(), getSAXParserFactory(), getPool() ); 
                else {
                    // otherwise assume RELAX NG
                    if( strictCheck ) {
                        Schema s = RELAXNGCompReader.getRELAXNGSchema4Schema();
                        reader[0] = new RELAXNGCompReader(
                            getController(),
                            new SAXParserFactoryImpl(getSAXParserFactory(),s),
                            getPool() );
                        winner = setupPipeline(s);
                    } else {
                        winner = reader[0] = new RELAXNGCompReader(
                            getController(), getSAXParserFactory(), getPool() );
                    }
                }
                
                // simulate the start of the document.
                winner.setDocumentLocator(locator);
                winner.startDocument();
                for( int i=0; i<prefixes.size(); i++ ) {
                    String[] d = (String[])prefixes.get(i);
                    winner.startPrefixMapping( d[0], d[1] );
                }
                winner.startElement(namespaceURI,localName,qName,atts);
                // redirect all successive events to the winner.
                parser.setContentHandler(winner);
            }
        });

        parser.setErrorHandler(getController());
        parser.setEntityResolver(getController());
        if( source instanceof String )    parser.parse( (String)source );
        else                            parser.parse( (InputSource)source );
        
        if(getController().hadError())  return null;
        else            return reader[0].getResultAsGrammar();
    }
    
    
}
