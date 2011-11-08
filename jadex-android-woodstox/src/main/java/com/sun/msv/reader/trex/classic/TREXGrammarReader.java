/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.classic;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javaxx.xml.parsers.SAXParserFactory;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.xml.sax.InputSource;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.reader.State;
import com.sun.msv.reader.TerminalState;
import com.sun.msv.reader.datatype.DataTypeVocabulary;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSDatatypeResolver;
import com.sun.msv.reader.trex.IncludePatternState;
import com.sun.msv.reader.trex.RootState;
import com.sun.msv.reader.trex.TREXBaseReader;
import com.sun.msv.reader.trex.TREXSequencedStringChecker;
import com.sun.msv.util.StartTagInfo;

/**
 * reads TREX grammar from SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXGrammarReader extends TREXBaseReader implements XSDatatypeResolver {
    
    /** loads TREX pattern */
    public static TREXGrammar parse( String grammarURL,
        SAXParserFactory factory, GrammarReaderController controller )
    {
        TREXGrammarReader reader = new TREXGrammarReader(controller,factory,new ExpressionPool());
        reader.parse(grammarURL);
        
        return reader.getResult();
    }
    
    /** loads TREX pattern */
    public static TREXGrammar parse( InputSource grammar,
        SAXParserFactory factory, GrammarReaderController controller )
    {
        TREXGrammarReader reader = new TREXGrammarReader(controller,factory,new ExpressionPool());
        reader.parse(grammar);
        
        return reader.getResult();
    }

    
    /** easy-to-use constructor. */
    public TREXGrammarReader( GrammarReaderController controller) {
        this(controller,createParserFactory(),new ExpressionPool());
    }

    /** easy-to-use constructor. */
    public TREXGrammarReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool ) {
        this(controller,parserFactory,new StateFactory(),pool);
    }
    
    /** full constructor */
    public TREXGrammarReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        StateFactory stateFactory,
        ExpressionPool pool ) {
        
        super( controller, parserFactory, pool, stateFactory, new RootState() );
    }
    
    protected String localizeMessage( String propertyName, Object[] args ) {
        String format;
        
        try {
            format = ResourceBundle.getBundle("com.sun.msv.reader.trex.classic.Messages").getString(propertyName);
        } catch( Exception e ) {
            return super.localizeMessage(propertyName,args);
        }
        
        return MessageFormat.format(format, args );
    }
    
    
    /**
     * TREX allows either
     *    (1) the predefined namespace for TREX or
     *    (2) default namespace ""
     * 
     *  as its namespace. This variable holds which namespace is currently in use.
     */
    protected String currentGrammarURI;
    
    
    protected TREXGrammar getGrammar() {
        return grammar;
    }
    
    /** Namespace URI of TREX */
    public static final String TREXNamespace = "http://www.thaiopensource.com/trex";

    protected boolean isGrammarElement( StartTagInfo tag ) {
        if( currentGrammarURI==null ) {
            // first time.
            if( tag.namespaceURI.equals(TREXNamespace) ) {
                currentGrammarURI = TREXNamespace;
                return true;
            }
            if( tag.namespaceURI.equals("") ) {
                currentGrammarURI = "";
                return true;
            }
            return false;
        } else {
            if(currentGrammarURI.equals(tag.namespaceURI))    return true;
            if(tag.containsAttribute(TREXNamespace,"role"))    return true;
            
            return false;
        }
    }
    
    /**
     * creates various State object, which in turn parses grammar.
     * parsing behavior can be customized by implementing custom StateFactory.
     */
    public static class StateFactory extends TREXBaseReader.StateFactory {
        public State concur        ( State parent, StartTagInfo tag )    { return new ConcurState(); }
        public State anyString    ( State parent, StartTagInfo tag )    { return new TerminalState(Expression.anyString); }
        public State string        ( State parent, StartTagInfo tag )    { return new StringState(); }
        public State data        ( State parent, StartTagInfo tag )    { return new DataState(); }
        public State define        ( State parent, StartTagInfo tag )    { return new DefineState(); }
        public State includePattern( State parent, StartTagInfo tag ) { return new IncludePatternState(); }
    }
    protected StateFactory getStateFactory() {
        return (StateFactory)super.sfactory;
    }
    
    private boolean issueObsoletedXMLSchemaNamespace = false;
    /**
     * maps obsoleted XML Schema namespace to the current one.
     */
    private String mapNamespace( String namespace ) {
        if(namespace.equals("http://www.w3.org/2000/10/XMLSchema")
        || namespace.equals("http://www.w3.org/2000/10/XMLSchema-datatypes")) {
            // namespace of CR version.
            if( !issueObsoletedXMLSchemaNamespace )
                // report warning only once.
                reportWarning(WRN_OBSOLETED_XMLSCHEMA_NAMSPACE,namespace);
            issueObsoletedXMLSchemaNamespace = true;
            return com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace;
        }
        return namespace;
    }
    
    public State createExpressionChildState( State parent, StartTagInfo tag )
    {
        if(tag.localName.equals("concur"))        return getStateFactory().concur(parent,tag);
        if(tag.localName.equals("anyString"))    return getStateFactory().anyString(parent,tag);
        if(tag.localName.equals("string"))        return getStateFactory().string(parent,tag);
        if(tag.localName.equals("data"))        return getStateFactory().data(parent,tag);
        if(tag.localName.equals("include"))        return getStateFactory().includePattern(parent,tag);

        final String role = tag.getAttribute(TREXNamespace,"role");
        if("datatype".equals(role)) {
            String namespaceURI = mapNamespace(tag.namespaceURI);
            DataTypeVocabulary v = grammar.dataTypes.get(namespaceURI);
        
            if(v==null) {
                reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, tag.namespaceURI );
                // put a dummy vocabulary into the map
                // so that user will never receive the same error again.
                grammar.dataTypes.put( tag.namespaceURI, new UndefinedDataTypeVocabulary() );
                return new IgnoreState();    // recover by ignoring this element.
            }            
            
            return v.createTopLevelReaderState(tag);
        }
            
        
        return super.createExpressionChildState(parent,tag);
    }

    public XSDatatypeExp resolveXSDatatype( String qName ) {
        return new XSDatatypeExp( (XSDatatype)resolveDatatype(qName),pool);
    }
    
    /** obtains a named DataType object referenced by a QName.
     */
    public Datatype resolveDatatype( String qName ) {
        String[] s = splitQName(qName);
        if(s==null) {
            reportError( ERR_UNDECLARED_PREFIX, qName );
            // recover by using a dummy DataType
            return StringType.theInstance;
        }
        
        s[0] = mapNamespace(s[0]);    // s[0] == namespace URI
        
        DataTypeVocabulary v = grammar.dataTypes.get(s[0]);
        if(v==null) {
            reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, s[0] );
            // put a dummy vocabulary into the map
            // so that user will never receive the same error again.
            grammar.dataTypes.put( s[0], new UndefinedDataTypeVocabulary() );
        } else {
            try {
                return v.getType( s[1] );    // s[1] == local name
            } catch( DatatypeException e ) {
                reportError( ERR_UNDEFINED_DATATYPE, qName );
            }
        }
        // recover by using a dummy DataType
        return StringType.theInstance;
    }


    
    public void wrapUp() {
        
        // make sure that there is no recurisve patterns.
        RunAwayExpressionChecker.check(this,grammar);
        
        if( !controller.hadError() )
            // make sure that there is no sequenced string.
            // when run-away expression is found, calling this method results in
            // stack overflow.
            grammar.visit( new TREXSequencedStringChecker(this,false) );
    }
    
    
    /**
     * Dummy DataTypeVocabulary for better error recovery.
     * 
     * If DataTypeVocabulary is not found, the error is reported
     * and this class is used to prevent further repetitive error messages.
     */
    private static class UndefinedDataTypeVocabulary implements DataTypeVocabulary
    {
        public State createTopLevelReaderState( StartTagInfo tag )
        { return new IgnoreState(); }    // ignore everything
        public Datatype getType( String localTypeName )
        { return StringType.theInstance; }    // accepts any type name

    }

    // error messages
}
