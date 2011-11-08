/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.dtd;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.datatype.xsd.ngimpl.DataTypeLibraryImpl;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.dtd.LocalNameClass;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.Controller;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.scanner.dtd.DTDEventListener;
import com.sun.msv.scanner.dtd.DTDParser;
import com.sun.msv.scanner.dtd.InputEntity;
import com.sun.msv.util.StringPair;

/**
 * constructs {@link TREXGrammar} object that exactly matches to
 * the parsed DTD.
 * 
 * <p>
 * Each element declaration will have its own ReferenceExp by the name
 * of the element.
 * 
 * <p>
 * Note that this class does NOT extend GrammarReader, because DTD
 * is not written in XML format.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DTDReader implements DTDEventListener {
    
    public DTDReader( GrammarReaderController controller, ExpressionPool pool ) {
        this.controller = new Controller(controller);
        grammar = new TREXGrammar(pool);
    }
    
    public static TREXGrammar parse( InputSource source,
        GrammarReaderController controller ) {
        
        return parse( source, controller, new ExpressionPool() );
    }
    
    public static TREXGrammar parse( InputSource source,
        GrammarReaderController controller,
        ExpressionPool pool ) {
        
        try {
            DTDReader reader = new DTDReader(controller,pool);
            DTDParser parser = new DTDParser();
            parser.setDtdHandler(reader);
            parser.setEntityResolver(controller);
            parser.parse(source);
        
            return reader.getResult();
        } catch( SAXParseException e ) {
            return null;    // this error was already handled by GrammarReaderController
        } catch( Exception e ) {
            controller.error( new Locator[0], e.getMessage(), e );
            return null;
        }
    }
    
    protected final Controller controller;
    
    /**
     * Used to create Datatype objects. This datatype library
     * should be able to handle XML Schema datatypes because
     * those are the names we ask to this library.
     */
    private DatatypeLibrary datatypeLibrary = new DataTypeLibraryImpl();
    
    public void setDatatypeLibrary( DatatypeLibrary datatypeLibrary ) {
        this.datatypeLibrary = datatypeLibrary;
    }
    
    /**
     * Obtains a Datatype object from its name.
     */
    public Datatype createDatatype( String name ) {
        try {
            if( DTDParser.TYPE_CDATA.equals(name) )
                return datatypeLibrary.createDatatype("normalizedString");
            if( DTDParser.TYPE_ENUMERATION.equals(name) )
                return datatypeLibrary.createDatatype("token");
            
            return datatypeLibrary.createDatatype(name);
        } catch( DatatypeException e ) {
            // we expect this datatype library to be complete
            e.printStackTrace();
            throw new InternalError();
        }
    }

    /**
     * map from prefix to set of possible namespace URI.
     * default namespace (without prefix) is stored by using "" as a key.
     */
    protected final Map<String, Set<String>> namespaces = createInitialNamespaceMap();
    
    protected final static Map<String, Set<String>> createInitialNamespaceMap() {
        Map<String, Set<String>> m = new java.util.HashMap<String, Set<String>>();
        // prefix xml is implicitly declared.
        Set<String> s = new java.util.HashSet<String>();
        s.add("http://www.w3.org/XML/1998/namespace");
        m.put("xml",s);
        return m;
    }
    
    /**
     * when this value is in the above set, that indicates
     * we couldn't detect what URIs are going to be used with that prefix.
     */
    protected static final String ABANDON_URI_SNIFFING = "*";
    
    protected NameClass getNameClass( String maybeQName, boolean handleAsAttribute ) {
        String[] s = splitQName(maybeQName);
        
        if(s[0].length()==0 && handleAsAttribute )
            // if this is an attribute and unprefixed, it is local to the element.
            return new SimpleNameClass(s[0],s[1]);
        
        Set<String> vec = namespaces.get(s[0]/*uri*/);
        if(vec==null) {
            if(s[0].equals(""))
                // this DTD does not attempt to use namespace.
                // this is OK and we assume anonymous namespace.
                return new SimpleNameClass("",s[1]);
            
            // we found element name like "html:p" but 
            // we haven't see any "xmlns:html" attribute declaration.
            // this is considered as an error for MSV.
            controller.error( new Locator[]{locator},
                Localizer.localize( ERR_UNDECLARED_PREFIX, s[0] ), null );
            
            // recover by returning something
            return new LocalNameClass( s[1]/*local*/ );
        }
        
        if( vec.contains(ABANDON_URI_SNIFFING) ) {
//            System.out.println("sniffing abandoned for "+s[0]);
            // possibly multiple URI can be assigned.
            // so fall back to use LocalNameClass to at least check local part.
            return new LocalNameClass( s[1] );
        }
        
        // create choice of all possible namespace, and
        // return it.
        String[] candidates = (String[])vec.toArray(new String[vec.size()]);
        NameClass nc = new SimpleNameClass( candidates[0], s[1] );
//        System.out.println("candidate for "+s[0]+" is "+ candidates[0] );
        for( int i=1; i<vec.size(); i++ ) {
            nc = new ChoiceNameClass( nc,
                    new SimpleNameClass( candidates[i], s[1] ) );
//            System.out.println("candidate for "+s[0]+" is "+ candidates[i] );
        }
        return nc;
    }
    
    /**
     * returns an array of (URI,localName).
     */
    protected String[] splitQName( String maybeQName ) {
        int idx = maybeQName.indexOf(':');
        if(idx<0)
            return new String[]{"",maybeQName};    // it wasn't a qname.
        return new String[]{maybeQName.substring(0,idx), maybeQName.substring(idx+1)};
    }
    
    
    protected final TREXGrammar grammar;
    /**
     * Obtains the parsed grammar object.
     * 
     * @return
     *        null if there was an error. Otherwise a parsed grammar
     *        object will be returned.
     */
    public TREXGrammar getResult() {
        if(controller.hadError())    return null;
        else                        return grammar;
    }
        
    protected Locator locator;
    
    public void setDocumentLocator( Locator loc ) {
        this.locator = loc;
    }
    
    /** map from element name to its content model. */
    protected final Map<String,Object> elementDecls = new java.util.HashMap<String,Object>();
    /** map from element name to (map from attribute name to AttModel). */
    protected final Map<String,Map<String,Object>> attributeDecls = new java.util.HashMap<String,Map<String,Object>>();
    
    private static class AttModel {
        Expression    value;
        boolean            required;
        AttModel( Expression value, boolean required ) {
            this.value = value;
            this.required = required;
        }
    }
    
    
    public void startContentModel( String elementName, short type ) {
        if( contentModel!=null )
            // assertion failed.
            // this must be a bug of DTDScanner.
            throw new Error();
        
        if( type==CONTENT_MODEL_MIXED )
            contentModel = Expression.nullSet;    // initial set up.
        if( type==CONTENT_MODEL_ANY )
            // if ANY is used, then refer to the special hedgeRule.
            contentModel = getAnyExp();
        if( type==CONTENT_MODEL_EMPTY )
            contentModel = Expression.epsilon;
    }
    
    
    /**
     * Gets the expression that corresponds to the "ANY".
     * <p>
     * Its defeinition will be later filled by the choice of all elements.
     */
    protected final ReferenceExp getAnyExp() {
        return grammar.namedPatterns.getOrCreate("$  all  $");
    }

    public void endContentModel( String elementName, short type ) {
        if( contentModel==null )
            // assertion failed.
            // this must be a bug of DTDScanner.
            throw new Error();
        
        switch(type) {
        case CONTENT_MODEL_CHILDREN:
        case CONTENT_MODEL_ANY:
            break;    // do nothing.
        case CONTENT_MODEL_EMPTY:
            contentModel = Expression.epsilon;
            break;
        case CONTENT_MODEL_MIXED:
            if( contentModel != Expression.nullSet )
                contentModel = grammar.pool.createMixed(
                    grammar.pool.createZeroOrMore(contentModel));
            else
                // this happens when mixed content model is #PCDATA only.
                contentModel = Expression.anyString;
            break;
        }
        
        // memorize the location
        setDeclaredLocationOf( grammar.namedPatterns.getOrCreate(elementName) );
        
        // memorize parsed content model.
        elementDecls.put( elementName, contentModel );
        contentModel = null;
    }
    
    /**
     * processes occurence (?,+,*) of the given expression
     */
    protected Expression processOccurs( Expression item, short occurence ) {
        switch( occurence ) {
        case OCCURENCE_ONCE:            return item;
        case OCCURENCE_ONE_OR_MORE:        return grammar.pool.createOneOrMore(item);
        case OCCURENCE_ZERO_OR_MORE:    return grammar.pool.createZeroOrMore(item);
        case OCCURENCE_ZERO_OR_ONE:        return grammar.pool.createOptional(item);
        default:        // assertion failed. this must be a bug of DTDScanner.
                                        throw new Error();
        }
    }
    
    protected class Context {
        final Expression    exp;
        final short        connectorType;
        final Context        previous;
        Context( Context prev, Expression exp, short connector ) {
            this.exp = exp;
            this.connectorType = connector;
            this.previous = prev;
        }
    }
    
    protected Context        contextStack;
    protected Expression    contentModel;
    protected short            connectorType;
    protected final short    CONNECTOR_UNKNOWN = -999;
    
    public void childElement( String elementName, short occurence ) {
        Expression exp = processOccurs(
                grammar.namedPatterns.getOrCreate(elementName),
                occurence);
        
        if( connectorType == CONNECTOR_UNKNOWN ) {
            // this must be the first child element within this model group.
            if( contentModel!=null )    throw new Error();
            contentModel = exp;
        } else {
            combineToContentModel(exp);
        }
    }
    
    protected void combineToContentModel( Expression exp ) {
        switch( connectorType ) {
        case CHOICE:
            contentModel = grammar.pool.createChoice( contentModel, exp );
            break;
        case SEQUENCE:
            contentModel = grammar.pool.createSequence( contentModel, exp );
            break;
        default:
            // assertion failed. no such connector.
            throw new Error();
        }
    }
    
    public void mixedElement( String elementName ) {
        if( contentModel==null )
            // assertion failed. contentModel must be prepared by startContentModel method.
            throw new Error();
        
        contentModel = grammar.pool.createChoice( contentModel,
            grammar.namedPatterns.getOrCreate(elementName) );
    }
    
    public void startModelGroup() {
        // push context
        contextStack = new Context( contextStack, contentModel, connectorType );
        contentModel = null;
        connectorType = CONNECTOR_UNKNOWN;
    }
    
    public void endModelGroup( short occurence ) {
        Expression exp = processOccurs( contentModel, occurence );
        // pop context
        contentModel = contextStack.exp;
        connectorType = contextStack.connectorType;
        contextStack = contextStack.previous;
        
        if( contentModel==null )
            // this model group is the first item in the parent model group.
            contentModel = exp;
        else
            combineToContentModel(exp);
    }
    
    public void connector( short type ) throws SAXException {
        if( this.connectorType==CONNECTOR_UNKNOWN )
            this.connectorType = type;
        else
        if( this.connectorType!=type )
            // assertion failed.
            // within a model group, operator must be the same.
            throw new Error();
    }

    private Set<String> getPossibleNamespaces( String prefix ) {
        Set<String> s = namespaces.get(prefix);
        if(s == null) {
            s = new java.util.HashSet<String>();
            namespaces.put(prefix,s);
        }
        return s;
    }
    
    /**
     * this flag is set to true after reporting WRN_ATTEMPT_TO_USE_NAMESPACE.
     * this is used to prevent issuing the same warning more than once.
     */
    private boolean reportedXmlnsWarning = false;
    
    public void attributeDecl(
        String elementName, String attributeName, String attributeType,
        String[] enums, short attributeUse, String defaultValue )
        throws SAXException {
        
        if( attributeName.startsWith("xmlns") ) {
            // this is namespace declaration
            
            if( !reportedXmlnsWarning )
                controller.warning( new Locator[]{locator},
                    Localizer.localize( WRN_ATTEMPT_TO_USE_NAMESPACE ) );
            reportedXmlnsWarning = true;
            
            
            if( defaultValue==null )
                // we don't have a default value, so no way to determine URI.
                defaultValue = ABANDON_URI_SNIFFING;
            
            Set<String> s;
            if( attributeName.equals("xmlns") )
                s = getPossibleNamespaces("");
            else
                s = getPossibleNamespaces( attributeName.substring(6) );
            
            s.add( defaultValue );
//            System.out.println("add " + defaultValue + " for att name " + attributeName );
            
            // xmlns:* cannot be added to attr constraint expression.
            return;
        }
        
        Map<String,Object> attList = attributeDecls.get(elementName);
        if( attList==null ) {
            // the first attribute for this element.
            attList = new java.util.HashMap<String,Object>();
            attributeDecls.put(elementName,attList);
        }
        
        Expression body = createAttributeBody(
            elementName,attributeName,attributeType,enums,
            attributeUse,defaultValue);
    
        AttModel am = new AttModel( body, attributeUse==USE_REQUIRED );
        setDeclaredLocationOf(am);
        
        // add it to the list.
        attList.put( attributeName, am );
    }
        
    /**
     * Creates an attribute body from the declaration
     * found in the DTD.
     */
    protected Expression createAttributeBody(
        String elementName, String attributeName, String attributeType,
        String[] enums, short attributeUse, String defaultValue )
            throws SAXException {
        
        // create Datatype that validates attribute value.
        Datatype dt = createDatatype(attributeType);
        
        StringPair str = new StringPair("",attributeType);
        
        if(enums!=null) {
            Expression exp = Expression.nullSet;
            for( int i=0; i<enums.length; i++ )
                exp = grammar.pool.createChoice( exp,
                    grammar.pool.createValue(dt,str,
                        dt.createValue(enums[i],null)));
            return exp;
        }
    
        if( attributeUse == USE_FIXED ) {
            return grammar.pool.createValue(dt,str,
                dt.createValue(defaultValue,null));
        }
        
        return grammar.pool.createData(dt,str);
    }

    /**
     * Creates an element declaration in the grammar object
     * by using the parsed result.
     * 
     * @return
     *        ReferenceExp that corresponds to the created element declaration.
     */
    protected ReferenceExp createElementDeclaration( String elementName ) {
        final Map<String,Object> attList = attributeDecls.get(elementName);
        
        Expression contentModel = Expression.epsilon;
        
        if(attList!=null) {
            // create AttributeExps and append it to tag.
            Iterator<String> jtr = attList.keySet().iterator();
            while( jtr.hasNext() ) {
                String attName = (String)jtr.next();
                AttModel model = (AttModel)attList.get(attName);
                        
                // wrap it by AttributeExp.
                Expression exp = grammar.pool.createAttribute(
                    getNameClass(attName,true), model.value, null);
        
                // apply attribute use.
                // unless USE_REQUIRED, the attribute is optional.
                if( !model.required )
                    exp = grammar.pool.createOptional(exp);
        
                // append it to attribute list.
                contentModel = grammar.pool.createSequence( contentModel, exp );
            }
        }
            
        ReferenceExp er = grammar.namedPatterns.getOrCreate(elementName);
        er.exp = new ElementPattern(
            getNameClass(elementName,false), 
            grammar.pool.createSequence(contentModel,
                (Expression)elementDecls.get(elementName) ) );
        
        // copy the declared location
        declaredLocations.put( er.exp, getDeclaredLocationOf(er) );
        
        return er;
    }
    
    /**
     * Creates element declarations from the parsed result.
     * 
     * @return
     *        An expression that corresponds to the choice of all
     *        element declarations. This will be used to implement "ANY".
     */
    protected Expression createElementDeclarations() {
        Expression allExp = Expression.nullSet;
        
        // create declarations
        Iterator<String> itr = elementDecls.keySet().iterator();
        while( itr.hasNext() ) {
            Expression exp = createElementDeclaration( (String)itr.next() );
            allExp = grammar.pool.createChoice( allExp, exp );
        }
        
        return allExp;
    }
    
    public void endDTD() throws SAXException {
        // perform final wrap-up.
        
        final Expression allExp = createElementDeclarations();
        
        
        // set this allExp as the content model of "all" hedgeRule.
        // this special hedgeRule is used to implement ANY content model,
        // and this hedgeRule is also exported.
        getAnyExp().exp = grammar.pool.createMixed(grammar.pool.createZeroOrMore(allExp));
        
        // also this allExp is used as top-level expression.
        grammar.exp = allExp;
        
        // check undefined element.
        ReferenceExp[] exps = grammar.namedPatterns.getAll();
        for( int i=0; i<exps.length; i++ )
            if( exps[i].exp==null ) {
                // this element is referenced but not defined.
                controller.error( new Locator[]{locator},
                    Localizer.localize( ERR_UNDEFINED_ELEMENT, new Object[]{exps[i].name} ), null );
            }
    }

    public void fatalError(SAXParseException e) throws SAXException {
        controller.fatalError(e);
    }

    public void error(SAXParseException e) throws SAXException {
        controller.error(e);
    }

    public void warning(SAXParseException e) throws SAXException {
        controller.warning(e);
    }
    
    /** this map remembers where ReferenceExps are defined,
     * and where user defined types are defined.
     * 
     * some ReferenceExp can be defined
     * in more than one location.
     * In those cases, the last one is always memorized.
     * This behavior is essential to correctly implement
     * TREX constraint that no two &lt;define&gt; is allowed in the same file.
     */
    private final Map<Object,Locator> declaredLocations = new java.util.HashMap<Object,Locator>();
    
    public void setDeclaredLocationOf( Object o ) {
        declaredLocations.put(o, new LocatorImpl(locator) );
    }
    public Locator getDeclaredLocationOf( Object o ) {
        return (Locator)declaredLocations.get(o);
    }
    
// validation context provider methods
//----------------------------------------
    public boolean isUnparsedEntity( String entityName ) {
        // if this method is called, then it must be from TypeIncubator
        // that adds enumeration value to EntityType.
        
        // TODO: if all DTD declares unparsed entity *before*
        // they are referenced (possibly by default value for ENTITY type),
        // then this method can check whether given name is actually 
        // declared as entity.
        
        // currently, this method always returns true to make 
        // derivation happy.
        return true;
    }
    
    public String resolveNamespacePrefix( String prefix ) {
        // types that relys on QName are not used in DTD.
        // therefore this method shall never be called.
        throw new Error();
    }
    
    
// unused methods
//---------------------

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    public void unparsedEntityDecl(String name, String publicId, 
                                   String systemId, String notationName) throws SAXException {
    }

    public void externalGeneralEntityDecl(String n, String p, String s)  throws SAXException {
    }

    public void internalGeneralEntityDecl (String n, String v) throws SAXException {
    }

    public void externalParameterEntityDecl (String n, String p, String s) throws SAXException {
    }

    public void internalParameterEntityDecl (String n, String v) throws SAXException {
    }

    public void startDTD (InputEntity in) throws SAXException {
    }

    public void comment (String n) throws SAXException {
    }

    public void characters (char ch[], int start, int length) throws SAXException {
    }

    public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
    }

    public void startCDATA () throws SAXException {
    }

    public void endCDATA () throws SAXException {
    }
    
    public static final String ERR_UNDEFINED_ELEMENT = // arg:1
        "DTDReader.UndefinedElement";
    public static final String WRN_ATTEMPT_TO_USE_NAMESPACE = // arg:0
        "DTDReader.Warning.AttemptToUseNamespace";
    public static final String ERR_UNDECLARED_PREFIX = // arg:1
        "DTDReader.UndeclaredPrefix";
}
