/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javaxx.xml.parsers.ParserConfigurationException;
import javaxx.xml.parsers.SAXParserFactory;
import javaxx.xml.transform.Source;
import javaxx.xml.transform.TransformerConfigurationException;
import javaxx.xml.transform.TransformerException;
import javaxx.xml.transform.TransformerFactory;
import javaxx.xml.transform.dom.DOMSource;
import javaxx.xml.transform.sax.SAXResult;
import javaxx.xml.transform.sax.SAXSource;

import org.relaxng.datatype.Datatype;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.Uri;
import com.sun.msv.verifier.regexp.StringToken;

/**
 * base implementation of grammar readers that read grammar from SAX2 stream.
 * 
 * GrammarReader class can be used as a ContentHandler that parses a grammar.
 * So the typical usage is
 * <PRE><XMP>
 * 
 * GrammarReader reader = new RELAXGrammarReader(...);
 * XMLReader parser = .... // create a new XMLReader here
 * 
 * parser.setContentHandler(reader);
 * parser.parse(whateverYouLike);
 * return reader.grammar;  // obtain parsed grammar.
 * </XMP></PRE>
 * 
 * Or you may want to use several pre-defined static "parse" methods for
 * ease of use.
 * 
 * @seealso com.sun.msv.reader.relax.RELAXReader#parse
 * @seealso com.sun.msv.reader.trex.TREXGrammarReader#parse
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class GrammarReader
    extends XMLFilterImpl
    implements IDContextProvider2
{
    /** document Locator that is given by XML reader */
    private Locator locator;
    
    /** this object receives errors and warnings */
    public final Controller controller;
    
    /** Reader may create another SAXParser from this factory */
    public final SAXParserFactory parserFactory;

    /** this object must be used to create a new expression */
    public final ExpressionPool pool;
    
    /**
     * Creates a default SAXParserFactory.
     */
    protected static SAXParserFactory createParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }
    
    
    /** constructor that should be called from parse method. */
    protected GrammarReader(
        GrammarReaderController _controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool,
        State initialState ) {
        
        this.controller = new Controller(_controller);
        this.parserFactory = parserFactory;
        // parserFactory may be null when using javaxx.xml.transform.
        if (parserFactory != null && !parserFactory.isNamespaceAware()) {
            throw new IllegalArgumentException("parser factory must be namespace-aware");
        }
        this.pool = pool;
        pushState( initialState, null, null );
    }
    
    /**
     * gets the parsed AGM.
     * 
     * Should any error happens, this method should returns null.
     * 
     * derived classes should implement type-safe getGrammar method,
     * along with this method.
     */
    public abstract Grammar getResultAsGrammar();
    
    
    
    /** checks if given element is that of the grammar elements. */
    protected abstract boolean isGrammarElement( StartTagInfo tag );
    
    /**
     * namespace prefix to URI conversion map.
     * this variable is evacuated to InclusionContext when the parser is switched.
     */
    public static interface PrefixResolver {
        /** returns URI. Or null if the prefix is not declared. */
        String resolve( String prefix );
    }
    
    /**
     * The namespace prefix resolver that only resolves "xml" prefix.
     * This class should be used as the base resolver.
     */
    public static final PrefixResolver basePrefixResolver = new PrefixResolver() {
        public String resolve( String prefix ) {
            if(prefix.equals("xml"))    return "http://www.w3.org/XML/1998/namespace";
            else                        return null;
        }
    };
    public class ChainPrefixResolver implements PrefixResolver {
        public ChainPrefixResolver( String prefix, String uri ) {
            this.prefix=prefix;this.uri=uri;
            this.previous = prefixResolver;
        }
        public String resolve( String p ) {
            if(p.equals(prefix))    return uri;
            else                    return previous.resolve(p);
        }
        public final PrefixResolver previous;
        public final String prefix;
        public final String uri;
    }
//    public NamespaceSupport namespaceSupport = new NamespaceSupport();
    public PrefixResolver prefixResolver = basePrefixResolver;

    public void startPrefixMapping( String prefix, String uri ) throws SAXException {
        prefixResolver = new ChainPrefixResolver(prefix,uri);
        super.startPrefixMapping(prefix,uri);
    }
    public void endPrefixMapping(String prefix) throws SAXException {
        prefixResolver = ((ChainPrefixResolver)prefixResolver).previous;
        super.endPrefixMapping(prefix);
    }
    
    /**
     * Iterates Map.Entry objects which has the prefix as key and
     * the namespace URI as value.
     */
    public Iterator<Object> iterateInscopeNamespaces() {
        return new Iterator<Object>() {
            private PrefixResolver resolver = proceed(prefixResolver);
            public Object next() {
                final ChainPrefixResolver cpr = (ChainPrefixResolver)resolver;
                resolver = proceed(cpr.previous);
                
                return new Map.Entry<Object,Object>() {
                    public Object getKey() { return cpr.prefix; }
                    public Object getValue() { return cpr.uri; }
                    public Object setValue(Object o) { throw new UnsupportedOperationException(); }
                };
            }
            public boolean hasNext() {
                return resolver instanceof ChainPrefixResolver;
            }
            private PrefixResolver proceed( PrefixResolver resolver ) {
                while(true) {
                    if(!(resolver instanceof ChainPrefixResolver))
                        return resolver;    // reached at the end
                
                    ChainPrefixResolver cpr = (ChainPrefixResolver)resolver;
                    if(resolveNamespacePrefix(cpr.prefix)==cpr.uri)
                        return resolver;    // this resolver is in-scope
                    
                    resolver = cpr.previous;
                }
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    
    /**
     * Resolves a QName into a pair of (namespace URI,local name).
     * Therefore this method returns null if it fails to process QName.
     */
    public String[] splitQName( String qName ) {
        int idx = qName.indexOf(':');
        if(idx<0) {
            String ns = prefixResolver.resolve("");
            // if the default namespace is not bounded, return "".
            // this behavior is consistent with SAX.
            if(ns==null)    ns="";
            return new String[]{ns,qName,qName};
        }
        
        String uri = prefixResolver.resolve(qName.substring(0,idx));
        if(uri==null)    return null;    // prefix is not defined.
        
        return new String[]{uri, qName.substring(idx+1), qName};
    }
    


    /**
     * intercepts an expression made by ExpressionState
     * before it is passed to the parent state.
     * 
     * derived class can perform further wrap-up before it is received by the parent.
     * This mechanism is used by RELAXReader to handle occurs attribute.
     */
    protected Expression interceptExpression( State state, Expression exp ) {
        return exp;
    }
    


    
    /**
     * tries to obtain a DataType object by resolving obsolete names.
     * this method is useful for backward compatibility purpose.
     */
    public XSDatatype getBackwardCompatibleType( String typeName ) {
        /*
            This method is not heavily used.
            So it is a good idea not to create a reference to the actual instance
            unless it's absolutely necessary, so that the class loader doesn't load
            the datatype class easily.
        
            If we use a map, it makes the class loader loads all classes. 
        */
        XSDatatype dt = null;
        
        if( typeName.equals("uriReference") )
            dt = com.sun.msv.datatype.xsd.AnyURIType.theInstance;
        else
        if( typeName.equals("number") )
            dt = com.sun.msv.datatype.xsd.NumberType.theInstance;
        else
        if( typeName.equals("timeDuration") )
            dt = com.sun.msv.datatype.xsd.DurationType.theInstance;
        else
        if( typeName.equals("CDATA") )
            dt = com.sun.msv.datatype.xsd.NormalizedStringType.theInstance;
        else
        if( typeName.equals("year") )
            dt = com.sun.msv.datatype.xsd.GYearType.theInstance;
        else
        if( typeName.equals("yearMonth") )
            dt = com.sun.msv.datatype.xsd.GYearMonthType.theInstance;
        else
        if( typeName.equals("month") )
            dt = com.sun.msv.datatype.xsd.GMonthType.theInstance;
        else
        if( typeName.equals("monthDay") )
            dt = com.sun.msv.datatype.xsd.GMonthDayType.theInstance;
        else
        if( typeName.equals("day") )
            dt = com.sun.msv.datatype.xsd.GDayType.theInstance;

        if( dt!=null )
            reportWarning( WRN_DEPRECATED_TYPENAME, typeName, dt.displayName() );
        
        return dt;
    }
    
    
    
    
// parsing and related services
//======================================================
    
    /**
     * information that must be sheltered before switching InputSource
     * (typically by inclusion).
     * 
     * It is chained by previousContext field and used as a stack.
     */
    private class InclusionContext {
        final PrefixResolver    prefixResolver;
        final Locator            locator;
        final String            systemId;

        final InclusionContext    previousContext;

        InclusionContext( PrefixResolver prefix, Locator loc, String sysId, InclusionContext prev ) {
            this.prefixResolver = prefix;
            this.locator = loc;
            this.systemId = sysId;
            this.previousContext = prev;
        }
    }
    
    /** current inclusion context */
    private InclusionContext pendingIncludes;
    
    private void pushInclusionContext( ) {
        pendingIncludes = new InclusionContext(
            prefixResolver, getLocator(), getLocator().getSystemId(),
            pendingIncludes );
        
        prefixResolver = basePrefixResolver;
        setLocator(null);
    }
    
    private void popInclusionContext() {
        prefixResolver        = pendingIncludes.prefixResolver;
        setLocator(pendingIncludes.locator);
        
        pendingIncludes = pendingIncludes.previousContext;
    }
    
    /**
     * obtains InputSource for the specified url.
     * 
     * Also this method allows GrammarReaderController to redirect or
     * prohibit inclusion.
     * 
     * @param sourceState
     *        The base URI of this state is used to resolve the resource.
     * 
     * @return
     *        always return non-null valid object
     */
    @SuppressWarnings("deprecation")
    public final InputSource resolveLocation( State sourceState, String uri )
        throws AbortException {
        
        try {
            // resolve a relative URI to an absolute one
            uri = combineURI( sourceState.getBaseURI(), uri );
    
            InputSource source = controller.resolveEntity(null,uri);
            if(source==null)    return new InputSource(uri);    // default handling
            else                return source;
            
        // in case of an error, throw the AbortException
        } catch( IOException e ) {
            controller.error(e,getLocator());
        } catch( SAXException e ) {
            controller.error(e,getLocator());
        }
        throw AbortException.theInstance;
    }

    /**
     * converts the relative URL to the absolute one by using the specified base URL.
     */
    public final String combineURI( String baseURI, String relativeURI ) {
        return Uri.resolve(baseURI,relativeURI);
    }

    /**
     * @deprecated use the combineURI method.
     */
    public final String combineURL( String baseURI, String relativeURI ) {
        return Uri.resolve(baseURI,relativeURI);
    }
    
    /**
     * Switchs InputSource to the specified URL and
     * parses it by the specified state.
     * 
     * The method will return after the parsing of the new source is completed.
     * derived classes can use this method to realize semantics of 'include'.
     * 
     * @param sourceState
     *        this state is used to resolve the URL.
     * @param newState
     *        this state will parse top-level of new XML source.
     *        this state receives document element by its createChildState method.
     */
    public void switchSource( State sourceState, String url, State newState ) throws AbortException {
        
        if( url.indexOf('#')>=0 ) {
            // this href contains the fragment identifier.
            // we cannot handle them properly.
            reportError( ERR_FRAGMENT_IDENTIFIER, url );
            throw AbortException.theInstance;
        }
        
        switchSource(
            resolveLocation(sourceState,url), newState );
    }
    
    /**
     * start reading input from a source defined by a SAX InputSource.
     * @param source
     * @param newState
     */
    public void switchSource(InputSource source, State newState) {
    	switchSource(new SAXSource(source), newState);
    }
    
    /**
     * Start reading input from a source defined by a javaxx.xml.transform source.
     * @param source
     * @param newState
     */
    public void switchSource(Source source, State newState ) {
        String url = source.getSystemId();
        
        for( InclusionContext ic = pendingIncludes; ic!=null; ic=ic.previousContext )
            if (ic.systemId != null && ic.systemId.equals(url) ) {
                
                // recursive include.
                // computes what files are recurisve.
                String s="";
                for( InclusionContext i = pendingIncludes; i!=ic; i=i.previousContext )
                    s = i.systemId + " > " + s;
                
                s = url + " > " + s + url;
                
                reportError( ERR_RECURSIVE_INCLUDE, s );
                return;    // recover by ignoring this include.
            }
        
        pushInclusionContext();
        State currentState = getCurrentState();
        try {
            // this state will receive endDocument event.
            pushState( newState, null, null );
            try {
				parse(source);
			} catch (TransformerConfigurationException e) {
				controller.error("transform error", e);
			} catch (TransformerException e) {
				controller.error("transform error", e);
			}
        } finally {
            // restore the current state.
            super.setContentHandler(currentState);
            popInclusionContext();
        }
    }
    
    /** parses a grammar from the specified source */
    public final void parse( String source ) {
        _parse(source,null);
    }
    
    /** parses a grammar from the specified source */
    public final void parse( InputSource source ) {
        _parse(source,null);
    }
    
    /**
     * Parse from an arbitrary javaxx.xml.transform source.
     * If the Source can be processed by {@link SAXSource#sourceToInputSource(Source)},
     * then this API will use that. Otherwise, it will use a transformer to 
     * create a stream of SAX events. In that later case, the grammar controller 
     * will not be called for any errors on the source side or to resolve
     * any references; the caller is responsible for making separate arrangements.
     * @param source
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void parse(Source source) throws TransformerConfigurationException, TransformerException {
    	InputSource saxSource = SAXSource.sourceToInputSource(source);
    	if (saxSource != null) {
    		parse(saxSource);
    	} else {
    		/* a sax parser will always set up a locator to the ID information
    		 * in the InputSource. TraX does not do this, so we have to.
    		 */
    		LocatorImpl sourceLocator = new LocatorImpl();
    		sourceLocator.setSystemId(source.getSystemId());
    		setLocator(sourceLocator);
    		
    		// take an arbitrary TraX source.
    		TransformerFactory factory = TransformerFactory.newInstance();
    		SAXResult result = new SAXResult(this);
    		factory.newTransformer().transform(source, result);
    	}
    }
    
    /** parses a grammar from the specified source */
    public final void _parse( Object source, Locator errorSource ) {
        try {
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(this);
            reader.setErrorHandler(controller);
            reader.setEntityResolver(controller);
            
            // invoke XMLReader
            if( source instanceof InputSource )        reader.parse((InputSource)source);
            if( source instanceof String )            reader.parse((String)source);
        } catch( ParserConfigurationException e ) {
            controller.error(e,errorSource);
        } catch( IOException e ) {
            controller.error(e,errorSource);
        } catch( SAXParseException e ) {
            controller.error(e);
        } catch( SAXException e ) {
            controller.error( e, errorSource );
        }
        // TODO: shall we throw AbortException here?
    }
    
    
    

    
    
    
    /**
     * memorizes what declarations are referenced from where.
     * 
     * this information is used to report the source of errors.
     */
    public class BackwardReferenceMap {
        private final Map<Object,List<Object>> impl = new java.util.HashMap<Object,List<Object>>();
                                                         
        /** memorize a reference to an object. */
        public void memorizeLink( Object target ) {
            List<Object> list;
            if( impl.containsKey(target) ) list = (List<Object>)impl.get(target);
            else {
                // new target.
                list = new ArrayList<Object>();
                impl.put(target,list);
            }
            
            list.add(new LocatorImpl(getLocator()));
        }
        
        /**
         * gets all the refer who have a reference to this object.
         * @return null
         *        if no one refers it.
         */
        public Locator[] getReferer( Object target ) {
            // TODO: does anyone want to get all of the referer?
            if( impl.containsKey(target) ) {
                List<Object> lst = (List<Object>)impl.get(target);
                Locator[] locs = new Locator[lst.size()];
                lst.toArray(locs);
                return locs;
            }
            return null;
        }
    }
    /** keeps track of all backward references to every ReferenceExp.
     * 
     * this map should be used to report the source of error
     * of undefined-something.
     */
    public final BackwardReferenceMap backwardReference = new BackwardReferenceMap();
    
    

    
    /** this map remembers where ReferenceExps are defined,
     * and where user defined types are defined.
     * 
     * some ReferenceExp can be defined
     * in more than one location.
     * In those cases, the last one is always memorized.
     * This behavior is essential to correctly implement
     * TREX constraint that no two &lt;define&gt; is allowed in the same file.
     */
    private final Map<Object, Locator> declaredLocations = new java.util.HashMap<Object, Locator>();
    
    public void setDeclaredLocationOf( Object o ) {
        declaredLocations.put(o, new LocatorImpl(getLocator()) );
    }
    public Locator getDeclaredLocationOf( Object o ) {
        return (Locator)declaredLocations.get(o);
    }

    /**
     * detects undefined ReferenceExp and reports it as an error.
     * 
     * this method is used in the final wrap-up process of parsing.
     */
    public void detectUndefinedOnes( ReferenceContainer container, String errMsg ) {
        Iterator<ReferenceExp> itr = container.iterator();
        while( itr.hasNext() ) {
            // ReferenceExp object is created when it is first referenced or defined.
            // its exp field is supplied when it is defined.
            // therefore, ReferenceExp with its exp field null means
            // it is referenced but not defined.
            
            ReferenceExp ref = itr.next();
            if( !ref.isDefined() ) {
                reportError( backwardReference.getReferer(ref),
                            errMsg, new Object[]{ref.name} );
                ref.exp=Expression.nullSet;
                // recover by assuming a null definition.
            }
        }
    }

    
    
    
    
//
// stack of State objects and related services
//============================================================
    
    /** pushs the current state into the stack and sets new one */
    public void pushState( State newState, State parentState, StartTagInfo startTag )
    {
        super.setContentHandler(newState);
        newState.init( this, parentState, startTag );
        
        // this order of statements ensures that
        // getCurrentState can be implemented by using getContentHandler()
    }
    
    /** pops the previous state from the stack */
    public void popState()
    {
        State currentState = getCurrentState();
        
        if( currentState.parentState!=null )
            super.setContentHandler( currentState.parentState );
        else    // if the root state is poped, supply a dummy.
            super.setContentHandler( new org.xml.sax.helpers.DefaultHandler() );
    }

    /** gets current State object. */
    public final State getCurrentState() { return (State)super.getContentHandler(); }

    /**
     * this method must be implemented by the derived class to create
     * language-default expresion state.
     * 
     * @return null if the start tag is an error.
     */
    public abstract State createExpressionChildState( State parent, StartTagInfo tag );
    
    
    
    public void setDocumentLocator( Locator loc ) {
        super.setDocumentLocator(loc);
        this.setLocator(loc);
    }


    
// validation context provider
//============================================
    // implementing ValidationContextProvider is neccessary
    // to correctly handle facets.
    
    public String resolveNamespacePrefix( String prefix ) {
        return prefixResolver.resolve(prefix);
    }
    
    public boolean isUnparsedEntity( String entityName ) {
        // we have to allow everything here?
        return true;
    }
    public boolean isNotation( String notationName ) {
        return true;
    }
    
    public String getBaseUri() {
        return getCurrentState().getBaseURI();
    }
    
    // when the user uses enumeration over ID type,
    // this method will be called.
    // To make it work, simply allow everything.
    public final void onID( Datatype dt, StringToken token ) {}

    
    
    
    
// back patching
//===========================================
/*
    several things cannot be done at the moment when the declaration is seen.
    These things have to be postponed until all the necessary information is
    prepared.
    (e.g., generating the expression that matches to <any />).
    
    those jobs are queued here and processed after the parsing is completed.
    Note that there is no mechanism in this class to execute jobs.
    The derived class has to decide its own timing to perform jobs.
*/
    
    public static interface BackPatch {
        /** do back-patching. */
        void patch();
        /** gets State object who has submitted this patch job. */
        State getOwnerState();
    }
    
    private final Vector<BackPatch> backPatchJobs = new Vector<BackPatch>();
    private final Vector<BackPatch> delayedBackPatchJobs = new Vector<BackPatch>();
    public final void addBackPatchJob( BackPatch job ) {
        backPatchJobs.add(job);
    }
    public final void addBackPatchJob( XSDatatypeExp job ) {
        // UGLY. DatatypeExp patching needs to run after
        // other back patch jobs, so we use two sets.
        delayedBackPatchJobs.add(job);
    }
    
    /** Performs all back-patchings. */
    public final void runBackPatchJob() {
        Locator oldLoc = getLocator();
        runBackPatchJob(backPatchJobs);
        runBackPatchJob(delayedBackPatchJobs);
        setLocator(oldLoc);
    }
    
    private final void runBackPatchJob( Vector<BackPatch> vec ) {
        Iterator<BackPatch> itr = vec.iterator();
        while( itr.hasNext() ) {
            BackPatch job = itr.next();
            // so that errors reported in the patch job will have 
            // position of its start tag.
            setLocator(job.getOwnerState().getLocation());
            job.patch();
        }
    }
    
    
    
    

// error related services
//========================================================
    
    
    public final void reportError( String propertyName )
    { reportError( propertyName, null, null, null ); }
    public final void reportError( String propertyName, Object arg1 )
    { reportError( propertyName, new Object[]{arg1}, null, null ); }
    public final void reportError( String propertyName, Object arg1, Object arg2 )
    { reportError( propertyName, new Object[]{arg1,arg2}, null, null ); }
    public final void reportError( String propertyName, Object arg1, Object arg2, Object arg3 )
    { reportError( propertyName, new Object[]{arg1,arg2,arg3}, null, null ); }
    public final void reportError( Exception nestedException, String propertyName )
    { reportError( propertyName, null, nestedException, null ); }
    public final void reportError( Exception nestedException, String propertyName, Object arg1 )
    { reportError( propertyName, new Object[]{arg1}, nestedException, null ); }
    public final void reportError( Locator[] locs, String propertyName, Object[] args )
    { reportError( propertyName, args, null, locs ); }

    public final void reportWarning( String propertyName )
    { reportWarning( propertyName, null, null ); }
    public final void reportWarning( String propertyName, Object arg1 )
    { reportWarning( propertyName, new Object[]{arg1}, null ); }
    public final void reportWarning( String propertyName, Object arg1, Object arg2 )
    { reportWarning( propertyName, new Object[]{arg1,arg2}, null ); }

    private Locator[] prepareLocation( Locator[] param ) {
        // if null is given, use the current location.
        if( param!=null ) {
            int cnt=0;
            for( int i=0; i<param.length; i++ )
                if( param[i]!=null )    cnt++;
            
            if( param.length==cnt ) return param;
            
            // remove null from the array.
            Locator[] locs = new Locator[cnt];
            cnt=0;
            for( int i=0; i<param.length; i++ )
                if( param[i]!=null )    locs[cnt++] = param[i];
            
            return locs;
        }
        if( getLocator()!=null )        return new Locator[]{getLocator()};
        else                    return new Locator[0];
    }
    
    /** reports an error to the controller */
    public final void reportError( String propertyName, Object[] args, Exception nestedException, Locator[] errorLocations ) {
        controller.error(
            prepareLocation(errorLocations),
            localizeMessage(propertyName,args), nestedException );
    }
    
    
    /** reports a warning to the controller */
    public final void reportWarning( String propertyName, Object[] args, Locator[] locations ) {
        controller.warning( prepareLocation(locations),
                            localizeMessage(propertyName,args) );
    }
    
    /**
     * This is a stopgap until we can wean more of this code from SAX-dependence.
     * 

     * @param input
     * @return
     */
    public static Source inputSourceFromLSInput(LSInput input) {
        Source source;
        if (input instanceof DOMLSInput) {
            DOMLSInput domLSInput = (DOMLSInput)input;
            source = new DOMSource(domLSInput.getElement());
        } else {

            InputSource inputSource = new InputSource();

            /*
             * 1. LSInput.characterStream 2. LSInput.byteStream 3. LSInput.stringData 4. LSInput.systemId 5.
             * LSInput.publicId
             */
            if (input.getCharacterStream() != null) {
                inputSource.setCharacterStream(input.getCharacterStream());
            }
            if (input.getByteStream() != null) {
                inputSource.setByteStream(input.getByteStream());
            }
            if (input.getStringData() != null) {
                inputSource.setCharacterStream(new StringReader(input.getStringData()));
            }
            if (input.getPublicId() != null) {
                // trax sources don't have public IDs.
                inputSource.setPublicId(input.getPublicId());
            }
            source = new SAXSource(inputSource);
        }
        
        if (input.getSystemId() != null) {
            source.setSystemId(input.getSystemId());
        }
        return source;
    }
    
    
    /** formats localized message with arguments */
    protected abstract String localizeMessage( String propertyName, Object[] args );

    
    public void setLocator(Locator locator) {
		this.locator = locator;
	}


	public Locator getLocator() {
		return locator;
	}


	public static final String ERR_MALPLACED_ELEMENT =    // arg:1
        "GrammarReader.MalplacedElement";
//    public static final String ERR_IO_EXCEPTION =    // arg:1
//        "GrammarReader.IOException";
//    public static final String ERR_SAX_EXCEPTION =    // arg:1
//        "GrammarReader.SAXException";
//    public static final String ERR_XMLPARSERFACTORY_EXCEPTION =    // arg:1
//        "GrammarReader.XMLParserFactoryException";
    public static final String ERR_CHARACTERS =        // arg:1
        "GrammarReader.Characters";
    public static final String ERR_DISALLOWED_ATTRIBUTE = // arg:2
        "GrammarReader.DisallowedAttribute";
    public static final String ERR_MISSING_ATTRIBUTE = // arg:2
        "GrammarReader.MissingAttribute";
    public static final String ERR_BAD_ATTRIBUTE_VALUE = // arg:2
        "GrammarReader.BadAttributeValue";
    public static final String ERR_MISSING_ATTRIBUTE_2 = // arg:3
        "GrammarReader.MissingAttribute.2";
    public static final String ERR_CONFLICTING_ATTRIBUTES = // arg:2
        "GrammarReader.ConflictingAttribute";
    public static final String ERR_RECURSIVE_INCLUDE = // arg:1
        "GrammarReader.RecursiveInclude";
    public static final String ERR_FRAGMENT_IDENTIFIER = // arg:1
        "GrammarReader.FragmentIdentifier";
    public static final String ERR_UNDEFINED_DATATYPE = // arg:1
        "GrammarReader.UndefinedDataType";
    public static final String ERR_DATATYPE_ALREADY_DEFINED =    // arg:1
        "GrammarReader.DataTypeAlreadyDefined";
    public static final String ERR_MISSING_CHILD_EXPRESSION =    // arg:none
        "GrammarReader.Abstract.MissingChildExpression";
    public static final String ERR_MORE_THAN_ONE_CHILD_EXPRESSION =    // arg:none
        "GrammarReader.Abstract.MoreThanOneChildExpression";
    public static final String ERR_MORE_THAN_ONE_CHILD_TYPE = // arg:none
        "GrammarReader.Abstract.MoreThanOneChildType";
    public static final String ERR_MISSING_CHILD_TYPE = // arg:none
        "GrammarReader.Abstract.MissingChildType";
    public static final String ERR_ILLEGAL_FINAL_VALUE =
        "GrammarReader.IllegalFinalValue";
    public static final String ERR_RUNAWAY_EXPRESSION = // arg:1
        "GrammarReader.Abstract.RunAwayExpression";
    public static final String ERR_MISSING_TOPLEVEL    = // arg:0
        "GrammarReader.Abstract.MissingTopLevel";
    public static final String WRN_MAYBE_WRONG_NAMESPACE = // arg:1
        "GrammarReader.Warning.MaybeWrongNamespace";
    public static final String WRN_DEPRECATED_TYPENAME = // arg:2
        "GrammarReader.Warning.DeprecatedTypeName";
    public static final String ERR_BAD_TYPE    =    // arg:1
        "GrammarReader.BadType";
    public static final String ERR_RECURSIVE_DATATYPE = // arg:0
        "GrammarReader.RecursiveDatatypeDefinition";
}
