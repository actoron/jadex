/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.datatype.ErrorDatatypeLibrary;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.ChoiceState;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.TerminalState;
import com.sun.msv.reader.trex.DivInGrammarState;
import com.sun.msv.reader.trex.IncludePatternState;
import com.sun.msv.reader.trex.NameClassChoiceState;
import com.sun.msv.reader.trex.RootState;
import com.sun.msv.reader.trex.TREXBaseReader;
import com.sun.msv.reader.trex.TREXSequencedStringChecker;
import com.sun.msv.util.LightStack;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.Util;

/**
 * reads RELAX NG grammar from SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGReader extends TREXBaseReader {
    
    /** loads RELAX NG pattern */
    public static TREXGrammar parse( String grammarURL,
        SAXParserFactory factory, GrammarReaderController controller )
    {
        RELAXNGReader reader = new RELAXNGReader(controller,factory);
        reader.parse(grammarURL);
        
        return reader.getResult();
    }
    
    /** loads RELAX NG pattern */
    public static TREXGrammar parse( InputSource grammar,
        SAXParserFactory factory, GrammarReaderController controller )
    {
        RELAXNGReader reader = new RELAXNGReader(controller,factory);
        reader.parse(grammar);
        
        return reader.getResult();
    }

    /** easy-to-use constructor. */
    public RELAXNGReader( GrammarReaderController controller ) {
        this(controller,createParserFactory());
    }
    
    /** easy-to-use constructor. */
    public RELAXNGReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory) {
        // TODO: add s4s
        this(controller,parserFactory,new StateFactory(),new ExpressionPool());
    }
    
    /** full constructor */
    public RELAXNGReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        StateFactory stateFactory,
        ExpressionPool pool ) {
        
        super( controller, parserFactory, pool, stateFactory, new RootState() );
    }
    
    /**
     * Schema for schema of RELAX NG.
     */
    protected static Schema relaxNGSchema4Schema = null;
    
    public static Schema getRELAXNGSchema4Schema() {
        
        // under the multi-thread environment, more than once s4s could be loaded.
        // it's a waste of resource, but by no means fatal.
        if(relaxNGSchema4Schema==null) {
            try {
                relaxNGSchema4Schema =
                    new com.sun.msv.verifier.jarv.RELAXNGFactoryImpl().compileSchema(
                        RELAXNGReader.class.getResourceAsStream("relaxng.rng"));
            } catch( Exception e ) {
                throw new RuntimeException("unable to load schema-for-schema for RELAX NG", e);
            }
        }
        return relaxNGSchema4Schema;
    }
    
    protected String localizeMessage( String propertyName, Object[] args ) {
        String format;
        
        try {
            format = ResourceBundle.getBundle("com.sun.msv.reader.trex.ng.Messages").getString(propertyName);
        } catch( Exception e ) {
            return super.localizeMessage(propertyName,args);
        }
        
        return MessageFormat.format(format, args );
    }
    
    protected TREXGrammar getGrammar() {
        return grammar;
    }
    
    /** Map from ReferenceExps to RefExpParseInfos. */
    private final Map<ReferenceExp,RefExpParseInfo> refExpParseInfos = new java.util.HashMap<ReferenceExp,RefExpParseInfo>();
    
    /** Gets RefExpParseInfo object for the specified ReferenceExp. */
    protected RefExpParseInfo getRefExpParseInfo( ReferenceExp exp ) {
        RefExpParseInfo r = refExpParseInfos.get(exp);
        if(r==null) {
            r = new RefExpParseInfo();
            refExpParseInfos.put(exp, r);
        }
        return r;
    }
    
    /**
     * Info about the current ReferenceExp object which is being defined.
     * This field is maintained by DefineState.
     * <p>
     * This field is set to null when there is an error, or the pattern being
     * defined is being re-defined.
     * 
     * <p>
     * This is a part of the process of the recursive self reference error detection.
     */
    protected RefExpParseInfo currentNamedPattern = null;
    
    /**
     * Flag to indicate whether we saw &lt;element> or not. If we don't see
     * any &lt;element> between &lt;define>/&lt;start> and &lt;ref>/&lt;parentRef>,
     * then that reference will go to <code>currentNamedPattern.refs</code>.
     * 
     * <p>
     * This is a part of the process of the recursive self reference error detection.
     */
    protected boolean directRefernce = true;
    
    /**
     * information necessary to correctly parse pattern definitions.
     */
    protected static class RefExpParseInfo {
        /**
         * This field is set to true once the head declaration is found.
         * A head declaration is a define element without the combine attribute.
         * It is an error that two head declarations share the same name.
         */
        public boolean haveHead = false;
        
        /**
         * The combine method which is used to combine this pattern.
         * this field is set to null if combine attribute is not yet used.
         */
        public String combineMethod = null;
        
        public static class RedefinitionStatus {}
        /**
         * This named pattern is not being redefined.
         * So it will be a part of the grammar.
         */
        public static RedefinitionStatus notBeingRedefined = new RedefinitionStatus();
        /**
         * This named pattern is being redefined. So even if we'll see some
         * &lt;define> with this name, it will not be a part of the grammar.
         * This state means that we don't yet see the definition of the original.
         * We need to issue an error if the pattern is redefined but there is no original
         * in the included grammar.
         */
        public static RedefinitionStatus originalNotFoundYet = new RedefinitionStatus();
        /**
         * The same as {@link #originalNotFoundYet}, but we saw the original definition.
         */
        public static RedefinitionStatus originalFound = new RedefinitionStatus();
        
        /**
         * Current redefinition status.
         */
        public RedefinitionStatus redefinition = notBeingRedefined;
        
        /**
         * Copies the contents of rhs into this object.
         */
        public void set( RefExpParseInfo rhs ) {
            this.haveHead = rhs.haveHead;
            this.combineMethod = rhs.combineMethod;
            this.redefinition = rhs.redefinition;
        }
        
        /**
         * ReferenceExps which are referenced from this pattern directly
         * (without having ElementExp in between.)
         * 
         * <p>
         * This is used to detect recursive self reference errors.
         */
        public final Vector<ReferenceExp> directRefs = new Vector<ReferenceExp>();
        
        /**
         * ReferenceExps which are referenced from this pattern indirectly
         * (with ElementExp in between.)
         */
        public final Vector<ReferenceExp> indirectRefs = new Vector<ReferenceExp>();
    }
    
    /** Namespace URI of RELAX NG */
    public static final String RELAXNGNamespace = "http://relaxng.org/ns/structure/1.0";
    protected boolean isGrammarElement( StartTagInfo tag ) {
        return RELAXNGNamespace.equals(tag.namespaceURI)
        // allow old namespace URI for now.
        || "http://relaxng.org/ns/structure/0.9".equals(tag.namespaceURI);
    }
    
    /**
     * DatatypeLibrary factory object.
     */
    private DatatypeLibraryFactory datatypeLibraryFactory = new DefaultDatatypeLibraryFactory();
    
    /**
     * Returns the datatypeLibraryFactory.
     */
    public DatatypeLibraryFactory getDatatypeLibraryFactory() {
        return datatypeLibraryFactory;
    }

    /**
     * Sets the datatypeLibraryFactory.
     */
    public void setDatatypeLibraryFactory(DatatypeLibraryFactory datatypeLibraryFactory) {
        this.datatypeLibraryFactory = datatypeLibraryFactory;
    }
    
    
    /**
     * creates various State object, which in turn parses grammar.
     * parsing behavior can be customized by implementing custom StateFactory.
     */
    public static class StateFactory extends TREXBaseReader.StateFactory {
        public State nsAnyName    ( State parent, StartTagInfo tag ) { return new NGNameState.AnyNameState(); }
        public State nsNsName    ( State parent, StartTagInfo tag ) { return new NGNameState.NsNameState(); }
        public State nsExcept    ( State parent, StartTagInfo tag ) { return new NameClassChoiceState(); }
        
        public State text            ( State parent, StartTagInfo tag ) { return new TerminalState(Expression.anyString); }
        public State data            ( State parent, StartTagInfo tag ) { return new DataState(); }
        public State dataParam        ( State parent, StartTagInfo tag ) { return new DataParamState(); }
        public State value            ( State parent, StartTagInfo tag ) { return new ValueState(); }
        public State list            ( State parent, StartTagInfo tag ) { return new ListState(); }
        public State define            ( State parent, StartTagInfo tag ) { return new DefineState(); }
        public State start            ( State parent, StartTagInfo tag ) { return new StartState(); }
        public State redefine        ( State parent, StartTagInfo tag ) { return new DefineState(); }
        public State redefineStart   ( State parent, StartTagInfo tag ) { return new StartState(); }
        public State includeGrammar    ( State parent, StartTagInfo tag ) { return new IncludeMergeState(); }
        public State externalRef    ( State parent, StartTagInfo tag ) { return new IncludePatternState(); }
        public State divInGrammar    ( State parent, StartTagInfo tag ) { return new DivInGrammarState(); }
        public State dataExcept        ( State parent, StartTagInfo tag ) { return new ChoiceState(); }
        public State attribute        ( State parent, StartTagInfo tag ) { return new AttributeState(); }
        public State element        ( State parent, StartTagInfo tag ) { return new ElementState(); }
        public State grammar        ( State parent, StartTagInfo tag ) { return new GrammarState(); }
        public State ref            ( State parent, StartTagInfo tag ) { return new RefState(false); }
        public State parentRef        ( State parent, StartTagInfo tag ) { return new RefState(true); }

        /**
         * to cause errors if someone is deriving this method.
         * this method is no longer used.
         * 
         * @deprecated
         */
        protected final DatatypeLibrary getDatatypeLibrary( String namespaceURI ) throws Exception {
            // unused
            throw new UnsupportedOperationException();
        }
    }
    protected StateFactory getStateFactory() {
        return (StateFactory)super.sfactory;
    }
    
    protected State createNameClassChildState( State parent, StartTagInfo tag ) {
        if(tag.localName.equals("name"))        return sfactory.nsName(parent,tag);
        if(tag.localName.equals("anyName"))        return sfactory.nsAnyName(parent,tag);
        if(tag.localName.equals("nsName"))        return sfactory.nsNsName(parent,tag);
        if(tag.localName.equals("choice"))        return sfactory.nsChoice(parent,tag);
        
        return null;        // unknown element. let the default error be thrown.
    }
    
    public State createExpressionChildState( State parent, StartTagInfo tag ) {
        
        if(tag.localName.equals("text"))        return getStateFactory().text(parent,tag);
        if(tag.localName.equals("data"))        return getStateFactory().data(parent,tag);
        if(tag.localName.equals("value"))        return getStateFactory().value(parent,tag);
        if(tag.localName.equals("list"))        return getStateFactory().list(parent,tag);
        if(tag.localName.equals("externalRef"))    return getStateFactory().externalRef(parent,tag);
        if(tag.localName.equals("parentRef"))    return getStateFactory().parentRef(parent,tag);
        
        return super.createExpressionChildState(parent,tag);
    }
    
    /** obtains a named DataType object referenced by a local name. */
    public Datatype resolveDataType( String localName ) {
        
        try {
            return getCurrentDatatypeLibrary().createDatatype(localName);
        } catch( DatatypeException dte ) {
            reportError( ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage() );
            return com.sun.msv.datatype.xsd.StringType.theInstance;
        }
    }
    
    /**
     * obtains the DataTypeLibrary that represents the specified namespace URI.
     * 
     * If the specified URI is undefined, then this method issues an error to
     * the user and must return a dummy datatype library.
     */
    public DatatypeLibrary resolveDataTypeLibrary( String uri ) {
        try {
            DatatypeLibrary lib = datatypeLibraryFactory.createDatatypeLibrary(uri);
            if(lib!=null)        return lib;
        
            // issue an error
            reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, uri );
        } catch( Throwable e ) {
            reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY_1, uri, e.toString() );
        }
        return ErrorDatatypeLibrary.theInstance;
    }
    
    @SuppressWarnings("serial")
    private static class AbortException extends Exception {}
    
    private void checkRunawayExpression(
        ReferenceExp node, Stack<ReferenceExp> items, Set<ReferenceExp> visitedExps ) throws AbortException {
                                                                                    
        if( !visitedExps.add(node) )
            return;        // this ReferenceExp has already been processed.
        items.push(node);
        
        // test direct references
        Iterator<ReferenceExp> itr = getRefExpParseInfo(node).directRefs.iterator();
        while( itr.hasNext() ) {
            ReferenceExp child = (ReferenceExp)itr.next();
            
            int idx = items.lastIndexOf(child);
            if(idx!=-1) {
                // find a cycle.
                
                String s = "";
                Vector<Locator> locs = new Vector<Locator>();
            
                for( ; idx<items.size(); idx++ ) {
                    ReferenceExp e = (ReferenceExp)items.get(idx);
                    if( e.name==null )    continue;    // skip anonymous ref.
                    
                    if( s.length()!=0 )     s += " > ";
                    s += e.name;
                    
                    Locator loc = getDeclaredLocationOf(e);
                    if(loc==null) {
                        continue;
                    }
                    locs.add(loc);
                }
                
                s += " > " + child.name;
                
                reportError(
                    (Locator[])locs.toArray(new Locator[locs.size()]),
                    ERR_RUNAWAY_EXPRESSION, new Object[]{s} );
                
                throw new AbortException();
            }
            
            checkRunawayExpression( child, items, visitedExps );
        }

        // test indirect references
        Stack<ReferenceExp> empty = new Stack<ReferenceExp>();
        itr = getRefExpParseInfo(node).indirectRefs.iterator();
        while( itr.hasNext() ) {
            checkRunawayExpression(itr.next(), empty, visitedExps );
        }        
        items.pop();
    }
    
    
    /**
     * Contextual restriction checker.
     */
    protected final RestrictionChecker restrictionChecker =
        new RestrictionChecker(this);
    
    public void wrapUp() {
        
        // checks the runaway expression
        try {
            checkRunawayExpression( grammar, new Stack<ReferenceExp>(), new java.util.HashSet<ReferenceExp>() );
        } catch( AbortException e ) {;}
        
        if( !controller.hadError() )
            // make sure that there is no sequenced string.
            // when run-away expression is found, calling this method results in
            // stack overflow.
            grammar.visit( new TREXSequencedStringChecker(this,true) );
        
        if( !controller.hadError() )
            // check RELAX NG contextual restrictions
            restrictionChecker.check();
            // this algorithm does not work if there is a runaway expression
    }
    
    
    
// propagatable attributes
//--------------------------------
    /**
     * currently active datatype library.
     * This field is maintained by this class.
     * In case of an error, this field may be set to null after issuing an error.
     * So care should be taken not to report the same error again.
     */
    private DatatypeLibrary datatypeLib = resolveDataTypeLibrary("");
    
    public DatatypeLibrary getCurrentDatatypeLibrary() {
        if(datatypeLib==null) {
            // load it.
            // the resolution of the datatypeLibrary has to be delayed until
            // it is actually used.
            datatypeLib = resolveDataTypeLibrary(datatypeLibURI);
            
            // assertion failed
            if(datatypeLib==null)    throw new Error();
        }
        return datatypeLib;
    }
    
    /**
     * the namespace URI of the currently active datatype library.
     * The empty string indicates the built-in datatype library.
     */
    protected String datatypeLibURI = "";
    
    private final LightStack dtLibStack = new LightStack();
    private final LightStack dtLibURIStack = new LightStack();

    public String resolveNamespacePrefix( String prefix ) {
        // In RELAX NG grammar, the default namespace should be resolved 
        // to the current value of the ns attribute.
        if(prefix.equals(""))    return targetNamespace;
        else                    return super.resolveNamespacePrefix(prefix);
    }
    
    public void startDocument() throws SAXException {
        // the datatypeLibrary attribute does not do chameleon
        dtLibStack.push(datatypeLib);
        dtLibURIStack.push(datatypeLibURI);
        datatypeLib = resolveDataTypeLibrary("");
        datatypeLibURI = "";

        super.startDocument();
    }
    public void endDocument() throws SAXException {
        super.endDocument();
        datatypeLib = (DatatypeLibrary)dtLibStack.pop();
        datatypeLibURI = (String)dtLibURIStack.pop();
    }
    
    public void startElement( String a, String b, String c, Attributes d ) throws SAXException {
        // handle 'datatypeLibrary' attribute propagation
        dtLibStack.push(datatypeLib);
        dtLibURIStack.push(datatypeLibURI);
        if( d.getIndex("datatypeLibrary")!=-1 ) {
            datatypeLibURI = d.getValue("datatypeLibrary");
            datatypeLib = null;
            
            if( !Util.isAbsoluteURI(datatypeLibURI) )
                reportError( ERR_NOT_ABSOLUTE_URI, datatypeLibURI );
            if( datatypeLibURI.indexOf('#')>=0 )
                reportError( ERR_FRAGMENT_IDENTIFIER, datatypeLibURI );
                
        }
        
        // if nothing specified, datatype library stays the same.
        super.startElement(a,b,c,d);
    }
    public void endElement( String a, String b, String c ) throws SAXException {
        super.endElement(a,b,c);
        datatypeLib = (DatatypeLibrary)dtLibStack.pop();
        datatypeLibURI = (String)dtLibURIStack.pop();
    }
    
    
    
    
    // error messages
    public static final String ERR_BAD_FACET = // arg:2
        "RELAXNGReader.BadFacet";
    public static final String ERR_INVALID_PARAMETERS = // arg:1
        "RELAXNGReader.InvalidParameters";
    public static final String ERR_BAD_DATA_VALUE = // arg:2
        "RELAXNGReader.BadDataValue";
    public static final String ERR_UNDEFINED_KEY = // arg:1
        "RELAXNGReader.UndefinedKey";
    public static final String ERR_UNDEFINED_DATATYPE_1 = // arg:2
        "RELAXNGReader.UndefinedDataType1";
    public static final String ERR_INCONSISTENT_KEY_TYPE = // arg:1
        "RELAXNGReader.InconsistentKeyType";
    public static final String ERR_INCONSISTENT_COMBINE = // arg:1
        "RELAXNGReader.InconsistentCombine";
    public static final String ERR_REDEFINING_UNDEFINED = // arg:1
        "RELAXNGReader.RedefiningUndefined";
    public static final String ERR_UNKNOWN_DATATYPE_VOCABULARY_1 = // arg:2
        "RELAXNGReader.UnknownDatatypeVocabulary1";
    public static final String ERR_MULTIPLE_EXCEPT = // arg:0
        "RELAXNGReader.MultipleExcept";
    public static final String ERR_NOT_ABSOLUTE_URI = // arg:1
        "RELAXNGReader.NotAbsoluteURI";
    public static final String ERR_INFOSET_URI_ATTRIBUTE = // arg:0
        "RELAXNGReader.InfosetUriAttribute";
    public static final String ERR_XMLNS_ATTRIBUTE = // arg:0
        "RELAXNGReader.XmlnsAttribute";
    public static final String ERR_NAKED_INFINITE_ATTRIBUTE_NAMECLASS = //arg:0
        "RELAXNGReader.NakedInfiniteAttributeNameClass";

}
