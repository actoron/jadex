/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javaxx.xml.XMLConstants;
import javaxx.xml.parsers.SAXParserFactory;
import javaxx.xml.transform.Source;
import javaxx.xml.transform.TransformerConfigurationException;
import javaxx.xml.transform.TransformerException;

import org.iso_relax.verifier.Schema;
import org.relaxng.datatype.DatatypeException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.OccurrenceExp;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.grammar.xmlschema.XMLSchemaTypeExp;
import com.sun.msv.reader.AbortException;
import com.sun.msv.reader.ChoiceState;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.InterleaveState;
import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetState;
import com.sun.msv.reader.datatype.xsd.SimpleTypeState;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSDatatypeResolver;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.verifier.jarv.XSFactoryImpl;


/**
 * parses XML representation of XML Schema and constructs AGM.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLSchemaReader extends GrammarReader implements XSDatatypeResolver {
	
	/* A schema may be embedded in a larger document with additional prefix/namespace
	 * mappings.
	 */
	
	private Map<String, String> additionalNamespaceMap;
    
    /** loads XML Schema */
    public static XMLSchemaGrammar parse( String grammarURL,
        SAXParserFactory factory, GrammarReaderController controller ) {
        
        XMLSchemaReader reader = new XMLSchemaReader(controller,factory);
        reader.parse(grammarURL);
        
        return reader.getResult();
    }
    
    /** loads XML Schema */
    public static XMLSchemaGrammar parse( InputSource grammar,
        SAXParserFactory factory, GrammarReaderController controller ) {
        
        XMLSchemaReader reader = new XMLSchemaReader(controller,factory);
        reader.parse(grammar);
        
        return reader.getResult();
    }
    
    /**
     * Convenience method to create a reader and read a single scheme.
     * @param schema
     * @param controller
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static XMLSchemaGrammar parse(Source schema, GrammarReaderController controller) throws TransformerConfigurationException, TransformerException {
    	/* If the source is a SAXSource, we will still use a SAXParser,
    	 * so we still create the parser factory.
    	 */
        XMLSchemaReader reader = new XMLSchemaReader(controller);
        reader.parse(schema);
        return reader.getResult();
    	
    }


    /** easy-to-use constructor. */
    public XMLSchemaReader( GrammarReaderController controller ) {
        this(controller,createParserFactory());
    }
    
    public XMLSchemaReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory ) {
        this( controller, parserFactory, new ExpressionPool() );
    }

    public XMLSchemaReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory, ExpressionPool pool ) {
        this( controller, parserFactory, new StateFactory(), pool );
    }
    
    public XMLSchemaReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        StateFactory stateFactory,
        ExpressionPool pool ) {
        
        super(controller,parserFactory,pool,new RootState(stateFactory.schemaHead(null)));
        this.sfactory = stateFactory;
        
        // by putting them into ReferenceExp, # of expressions usually gets smaller
        // because createSequence are right-associative and any attempt to extend
        // this sequence will end up creating new SequenceExps.
        // It's also good for writer: it can generate more compact XML representation.
        ReferenceExp exp = new ReferenceExp( XMLSchemaSchemaLocationAttributes );
        xsiSchemaLocationExp = exp;
        exp.exp =
            pool.createSequence(
            pool.createOptional(
                pool.createAttribute(
                    new SimpleNameClass(XMLSchemaSchema.XMLSchemaInstanceNamespace,"schemaLocation")
                )
            ),
            pool.createOptional(
                pool.createAttribute(
                    new SimpleNameClass(XMLSchemaSchema.XMLSchemaInstanceNamespace,"noNamespaceSchemaLocation")
                )
            )
        );

        this.grammar = new XMLSchemaGrammar(pool);
        
        xsdSchema = new XMLSchemaSchema( XMLSchemaNamespace, grammar );
        // since we might parse the schema-for-schema, we cannot mark
        // this schema as "already defined."
//        markSchemaAsDefined(xsdSchema);
    
        ElementPattern e = new ElementPattern( NameClass.ALL, Expression.nullSet );
        e.contentModel =
            pool.createMixed(
                pool.createZeroOrMore(
                    pool.createChoice(
                        pool.createAttribute( NameClass.ALL ),
                        e )));
        complexUrType = xsdSchema.complexTypes.getOrCreate( "anyType" );
        complexUrType.body.exp = e.contentModel;
        complexUrType.complexBaseType = complexUrType;
        complexUrType.derivationMethod = ComplexTypeExp.RESTRICTION;
        
    }
    
    
    
    /** Schema for schema of W3C XML Schema. */
    protected static Schema xmlSchema4XmlSchema = null;
    
    public static Schema getXmlSchemaForXmlSchema() {
        
        // under the multi-thread environment, more than once s4s could be loaded.
        // it's a waste of resource, but by no means fatal.
        if(xmlSchema4XmlSchema==null) {
            try {
                XSFactoryImpl factory = new XSFactoryImpl();
                factory.setEntityResolver( new EntityResolver() {
                    public InputSource resolveEntity( String publicId, String systemId ) {
                        if(systemId.endsWith("datatypes.xsd"))
                            return new InputSource(XMLSchemaReader.class.getResourceAsStream(
                                "datatypes.xsd"));
                        if(systemId.endsWith("xml.xsd"))
                            return new InputSource(XMLSchemaReader.class.getResourceAsStream(
                                "xml.xsd"));
                        System.out.println("unexpected system ID: "+systemId);
                        return null;
                    }
                });
                xmlSchema4XmlSchema = factory.compileSchema(
                        XMLSchemaReader.class.getResourceAsStream("xmlschema.xsd"));
            } catch( Exception e ) {
                e.printStackTrace();
                throw new Error("unable to load schema-for-schema for W3C XML Schema");
            }
        }
        
        return xmlSchema4XmlSchema;
    }
    
    
    
    
    /**
     * content model that matches to
     * optional xsi:schemaLocation or xsi:noNamespaceSchemaLocation.
     */
    public final ReferenceExp xsiSchemaLocationExp;
    
    public final static String XMLSchemaSchemaLocationAttributes = 
        "____internal_XML_schema_SchemaLocation_attributes";
    
    /**
     * expression that matches to "ur-type" when used as a complex type.
     */
    public final ComplexTypeExp complexUrType;
    
    /** value of the "attributeFormDefault" attribute. */
    protected String attributeFormDefault;
    /** value of the "elementFormDefault" attribute. */
    protected String elementFormDefault;
    /**
     * value of the "finalDefault" attribute.
     * Set to null if the attribute was not specified.
     */
    protected String finalDefault;
    /**
     * value of the "blockDefault" attribute.
     * Set to null if the attribute was not specified.
     */
    protected String blockDefault;
    
    /** grammar object which is being under construction. */
    protected final XMLSchemaGrammar grammar;
    protected XMLSchemaSchema currentSchema;
    
    /**
     * XMLSchemaSchema object that has XMLSchemaNamespace as its target namespace.
     */
    protected final XMLSchemaSchema xsdSchema;
    
    /**
     * tables that store all SystemIds that we've read.
     * 
     * map from target namespace URI to set of system ids.
     * This field is used to prevent double inclusion.
     * 
     * Strictly speaking, comparision based on system id is not enough.
     * The spec calls for <i>"the necessity of establishing identity
     * component by component"</i> (section 4.2.1, last note).
     */
    public final Map<String, Set<String>> parsedFiles =	new HashMap<String, Set<String>>();
    
    public final XMLSchemaGrammar getResult() {
        if(controller.hadError())    return null;
        else                        return grammar;
    }
    public Grammar getResultAsGrammar() {
        return getResult();
    }
    
    /**
     * gets a reference to XMLSchemaGrammar object whose target namespace 
     * is the specified one.
     * 
     * If there is no such object, this method creates a new instance and
     * returns it.
     */
    public XMLSchemaSchema getOrCreateSchema( String namespaceURI ) {
        
        XMLSchemaSchema g = (XMLSchemaSchema)grammar.getByNamespace(namespaceURI);
        if(g!=null)        return g;
        
        // create new one.
        g = new XMLSchemaSchema(namespaceURI,grammar);
        
        // memorize the first link so that we can report the source of error
        // if this namespace turns out to be undefined.
        backwardReference.memorizeLink(g);
        
        return g;
    }
    
    /**
     * creates various State object, which in turn parses grammar.
     * parsing behavior can be customized by implementing custom StateFactory.
     */
    public static class StateFactory {
        public State schemaHead            (String expectedNamespace )    {
            return new SchemaState(expectedNamespace);
        }
        public State schemaIncluded        (State parent, String expectedNamespace )    {
            return new SchemaIncludedState(expectedNamespace);
        }
        
        public State simpleType            (State parent,StartTagInfo tag)    { return new SimpleTypeState(); }
        public State all                    (State parent,StartTagInfo tag)    { return new InterleaveState(); }
        public State choice                (State parent,StartTagInfo tag)    { return new ChoiceState(true); }
        public State sequence            (State parent,StartTagInfo tag)    { return new SequenceState(true); }
        public State group                (State parent,StartTagInfo tag)    { return new GroupState(); }
        public State complexTypeDecl        (State parent,StartTagInfo tag)    { return new ComplexTypeDeclState(); }
        public State attribute            (State parent,StartTagInfo tag)    { return new AttributeState(); }
        public State attributeGroup        (State parent,StartTagInfo tag)    { return new AttributeGroupState(); }
        public State elementDecl            (State parent,StartTagInfo tag)    { return new ElementDeclState(); }
        public State elementRef            (State parent,StartTagInfo tag)    { return new ElementRefState(); }
        public State any                    (State parent,StartTagInfo tag)    { return new AnyElementState(); }
        public State anyAttribute        (State parent,StartTagInfo tag)    { return new AnyAttributeState(); }
        public State include                (State parent,StartTagInfo tag)    { return new IncludeState(); }
        public State import_                (State parent,StartTagInfo tag)    { return new ImportState(); }
        public State redefine            (State parent,StartTagInfo tag)    { return new RedefineState(); }
        public State notation            (State parent,StartTagInfo tag)    { return new IgnoreState(); }
        public State facets                (State parent,StartTagInfo tag)    { return new FacetState(); }

        public State unique                (State parent,StartTagInfo tag)    { return new IdentityConstraintState(); }
        public State key                    (State parent,StartTagInfo tag)    { return new IdentityConstraintState(); }
        public State keyref                (State parent,StartTagInfo tag)    { return new IdentityConstraintState(); }
        
        public State complexContent        (State parent,StartTagInfo tag,ComplexTypeExp decl)    { return new ComplexContentState(decl); }
        // complexContent/restriction
        public State complexRst            (State parent,StartTagInfo tag,ComplexTypeExp decl)    { return new ComplexContentBodyState(decl,false); }
        // complexContent/extension
        public State complexExt            (State parent,StartTagInfo tag,ComplexTypeExp decl)    { return new ComplexContentBodyState(decl,true); }

        public State simpleContent        (State parent,StartTagInfo tag,ComplexTypeExp decl)    { return new SimpleContentState(decl); }
        // simpleContent/restriction
        public State simpleRst            (State parent,StartTagInfo tag,ComplexTypeExp decl)    { return new SimpleContentRestrictionState(decl); }
        // simpleContent/extension
        public State simpleExt            (State parent,StartTagInfo tag,ComplexTypeExp decl)    { return new SimpleContentExtensionState(decl); }
    }
    
    public final StateFactory sfactory;
    
    public State createExpressionChildState( State parent, StartTagInfo tag ) {
        if(tag.localName.equals("element")) {
            if(tag.containsAttribute("ref"))    return sfactory.elementRef(parent,tag);
            else                                return sfactory.elementDecl(parent,tag);
        }
        if(tag.localName.equals("any"))            return sfactory.any(parent,tag);
        
        return createModelGroupState(parent,tag);
    }
    
    /**
     * creates a state object that parses "all"/"group ref"/"choice" and "sequence".
     */
    public State createModelGroupState( State parent, StartTagInfo tag ) {
        if(tag.localName.equals("all"))            return sfactory.all(parent,tag);
        if(tag.localName.equals("choice"))        return sfactory.choice(parent,tag);
        if(tag.localName.equals("sequence"))    return sfactory.sequence(parent,tag);
        if(tag.localName.equals("group"))        return sfactory.group(parent,tag);
    
        return null;
    }
    
    /**
     * creates a state object that parses "attribute","attributeGroup ref", and "anyAttribute".
     */
    public State createAttributeState( State parent, StartTagInfo tag ) {
        if(tag.localName.equals("attribute"))        return sfactory.attribute(parent,tag);
        if(tag.localName.equals("anyAttribute"))    return sfactory.anyAttribute(parent,tag);
        if(tag.localName.equals("attributeGroup"))    return sfactory.attributeGroup(parent,tag);
    
        return null;
    }

    public State createFacetState( State parent, StartTagInfo tag ) {
        if( FacetState.facetNames.contains(tag.localName) )    return sfactory.facets(parent,tag);
        else    return null;
    }
    
    
    
    /** namespace URI of XML Schema declarations. */
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";
    public static final String XMLSchemaNamespace_old = "http://www.w3.org/2000/10/XMLSchema";
    
    private boolean issuedOldNamespaceWarning = false;
    
    protected boolean isGrammarElement( StartTagInfo tag ) {
        
        if(!isSchemaNamespace(tag.namespaceURI))    return false;
        
        // annotation is ignored at this level.
        // by returning false, the entire subtree will be simply ignored.
        if(tag.localName.equals("annotation"))    return false;
        
        return true;
    }

    /** set of XMLSchemaGrammar that is already defined.
     * XMLSchemaGrammar object is created when it is first referenced or defined.
     */
    private final Set<XMLSchemaSchema> definedSchemata = new java.util.HashSet<XMLSchemaSchema>();
    public final void markSchemaAsDefined( XMLSchemaSchema schema ) {
        definedSchemata.add( schema );
    }
    public final boolean isSchemaDefined( XMLSchemaSchema schema ) {
        return definedSchemata.contains(schema);
    }
    
    
    
    protected String resolveNamespaceOfAttributeDecl( String formValue ) {
        return resolveNamespaceOfDeclaration( formValue, attributeFormDefault );
    }
    
    protected String resolveNamespaceOfElementDecl( String formValue ) {
        return resolveNamespaceOfDeclaration( formValue, elementFormDefault );
    }
    
    private String resolveNamespaceOfDeclaration( String formValue, String defaultValue ) {
        if( "qualified".equals(formValue) )
            return currentSchema.targetNamespace;
        
        if( "unqualified".equals(formValue) )
            return "";
        
        if( formValue!=null ) {
            reportError( ERR_BAD_ATTRIBUTE_VALUE, "form", formValue );
            return "$$recover$$";    // recovery by returning whatever
        }
        
        return defaultValue;
    }
    
    /**
     * resolves built-in datatypes (URI: http://www.w3.org/2001/XMLSchema)
     * 
     * @return
     *        null if the type is not defined.
     */
    public XSDatatype resolveBuiltinDataType( String typeLocalName ) {
        // datatypes of XML Schema part 2
        try {
            return DatatypeFactory.getTypeByName(typeLocalName);
        } catch( DatatypeException e ) {
            return null;    // not found.
        }
    }
    
    /**
     * Gets a built-in datatype as SimpleTypeExp.
     * 
     * @return
     *        null if the type is not defined.
     */
    public SimpleTypeExp resolveBuiltinSimpleType( String typeLocalName ) {
        // datatypes of XML Schema part 2
        try {
            XSDatatype dt = DatatypeFactory.getTypeByName(typeLocalName);
            SimpleTypeExp sexp = xsdSchema.simpleTypes.getOrCreate(typeLocalName);
            if(!sexp.isDefined())
                sexp.set( new XSDatatypeExp(dt,pool) );
            return sexp;
        } catch( DatatypeException e ) {
            return null;    // not found.
        }
    }
    
    public boolean isSchemaNamespace( String ns ) {
        if( ns.equals(XMLSchemaNamespace) ) return true;
        
        if( ns.equals(XMLSchemaNamespace_old) ) {
            // old namespace.
            // report a warning only once.
            if( !issuedOldNamespaceWarning )
                reportWarning( WRN_OBSOLETED_NAMESPACE, null );
            issuedOldNamespaceWarning = true;
            return true;
        }
        
        return false;
    }
    
    
    /**
     * Resolves a simple type name into the corresponding XSDatatypeExp object.
     */
    public XSDatatypeExp resolveXSDatatype( String typeQName ) {
        
        final String[] r = splitQName(typeQName);
        if(r==null) {
            reportError( ERR_UNDECLARED_PREFIX, typeQName );
            // TODO: implement UndefinedType, that is used only when an error is encountered.
            // it should accept anything and any facets.
            // recover by assuming string.
            return  new XSDatatypeExp(StringType.theInstance,pool);
        }
        
        if( isSchemaNamespace(r[0]) ) {
            // internal definitions should be consulted first.
            XSDatatype dt = resolveBuiltinDataType(r[1]);
            if(dt!=null) return new XSDatatypeExp(dt,pool);
            
            // the name was not found.
            // maybe we are parsing schema for schema.
            // consult the externally defined types.
        }

        final SimpleTypeExp sexp = getOrCreateSchema(r[0]).simpleTypes.
            getOrCreate(r[1]);
        backwardReference.memorizeLink(sexp);
                 
        // simple type might be re-defined later.
        // therefore, we always need a late-binding datatype,
        // even if the datatype is defined already.
        
        return new XSDatatypeExp(r[0],r[1],this,new XSDatatypeExp.Renderer(){
            public XSDatatype render( XSDatatypeExp.RenderingContext context ) {
                if(sexp.getType()!=null)
                    return sexp.getType().getType(context);
                else
                    // undefined error is alreadyreported by
                    // the detectUndefinedOnes(simpleTypes) method
                    // so silently recover by using some sort of expression
                    return StringType.theInstance;
            }
        });
    }
    
    public static interface RefResolver {
        ReferenceContainer get( XMLSchemaSchema schema );
    }
    public Expression resolveQNameRef( StartTagInfo tag, String attName, RefResolver resolver ) {
        
        String refQName = tag.getAttribute(attName);
        if( refQName==null ) {
            reportError( ERR_MISSING_ATTRIBUTE, tag.qName, attName );
            return null;    // failed.
        }
        
        String[] r = splitQName(refQName);
        if(r==null) {
            reportError( ERR_UNDECLARED_PREFIX, refQName );
            return null;
        }
        
        Expression e =  resolver.get( getOrCreateSchema(r[0]/*uri*/) )._getOrCreate(r[1]/*local name*/);
        backwardReference.memorizeLink(e);
        
        return e;
    }

    /**
     * The intended target namespace of the chameleon schema.
     * 
     * <p>
     * When parsing a chameleon schema (inclusion of a schema without
     * the targetNamespace attribute), this field is set to the target namespace
     * of the callee, so that any reference occured in the chameleon schema
     * is treated correctly.
     * 
     * <p>
     * This field must be set to null in other cases. In that case, QName resolution
     * is handled just normally.
     * 
     * <p>
     * This field is maintained by {@link SchemaIncludedState}.
     */
    protected String chameleonTargetNamespace = null;
    
    /**
     * Resolves a QName into a pair of (namespace URI,local name).
     * 
     * <p>
     * When we are parsing a "chameleon schema", any reference to
     * the default empty namespace("") has to be treated as a reference to
     * the intended target namespace.
     */
    public String[] splitQName( String qName ) {
        String[] r = super.splitQName(qName);
        if(r == null) {
        	/* This code copies code in the base class.
        	 * Perhaps it would be better to push and pop these prefixes,
        	 * but they have to be at the end of the chain so they would 
        	 * have to be pushed at 'parse', which in turn suggests
        	 * pushing this functionality down into the grammar controller,
        	 * which I'm not willing to try today.
        	 */
        	int idx = qName.indexOf(':');
        	if (idx > 0) {
        		String prefix = qName.substring(0, idx);
        		String uri = additionalNamespaceMap.get(prefix);
        		if (uri != null) {
        			return new String[]{uri, qName.substring(idx+1), qName};
        		}
        	}
        	return null;
        }
        if(r[0].length()==0 && chameleonTargetNamespace!=null)
            r[0] = chameleonTargetNamespace;
        return r;
    }
    
    
    
    protected Expression interceptExpression( State state, Expression exp ) {
        // process minOccurs/maxOccurs
        if( state instanceof SequenceState
        ||  state instanceof ChoiceState
        ||  state instanceof InterleaveState
        ||  state instanceof AnyElementState
        ||  state instanceof ElementDeclState
        ||  state instanceof ElementRefState
        ||  state instanceof GroupState )
            // TODO: <all/> is limited upto 1
            return processOccurs(state.getStartTag(),exp);
        
        return exp;
    }
    
    /**
     * Adds maxOccurs/minOccurs semantics to a given expression.
     * 
     * For example, if this method receives A, minOccurs=0, and maxOccurs=3,
     * then this method should return something like (A,(A,A?)?)?
     */
    public Expression processOccurs( StartTagInfo startTag, Expression item ) {
                                                                                 
        String minOccurs = startTag.getAttribute("minOccurs");
        int minOccursValue=1;
        
        if( minOccurs!=null ) {
            try {
                minOccursValue = Integer.parseInt(minOccurs);
                if(minOccursValue<0)    throw new NumberFormatException();
            } catch( NumberFormatException e ) {
                reportError( ERR_BAD_ATTRIBUTE_VALUE, "minOccurs", minOccurs );
                minOccursValue = 1;
            }
        }
        
        String maxOccurs = startTag.getAttribute("maxOccurs");
        int maxOccursValue; // -1 for 'unbounded'
        
        if( maxOccurs==null ) {
            // maxOccurs if not present. make sure that minOccurs<=1
            if( minOccursValue>1 )
                reportError( ERR_MAXOCCURS_IS_NECESSARY );
            maxOccursValue = 1;
        } else
        if( maxOccurs.equals("unbounded") ) {
            maxOccursValue = -1;
        } else {
            try {
                maxOccursValue = Integer.parseInt(maxOccurs);
                if(maxOccursValue<0 || maxOccursValue<minOccursValue)
                    throw new NumberFormatException();
            } catch( NumberFormatException e ) {
                reportError( ERR_BAD_ATTRIBUTE_VALUE, "maxOccurs", maxOccurs );
                maxOccursValue = 1;
            }
        }
        
        return processOccurs( item, minOccursValue, maxOccursValue );
    }
    
    /**
     * Adds maxOccurs/minOccurs semantics to a given expression.
     * 
     * @param   maxOccurs
     *      -1 to represent "unbounded".
     */
    public Expression processOccurs( Expression item, int minOccurs, int maxOccurs ) {
        Expression precise = _processOccurs(item,minOccurs,maxOccurs);
        if(maxOccurs==1)                    return precise;
        if(maxOccurs==-1 && minOccurs<=1 )  return precise;
        return new OccurrenceExp(precise,maxOccurs,minOccurs,item);
    }
    
    private Expression _processOccurs( Expression item, int minOccurs, int maxOccurs ) {

        Expression exp = Expression.epsilon;
        for( int i=0; i<minOccurs; i++ )
            exp = pool.createSequence( item, exp );
        
        if(maxOccurs==-1) {
            if( minOccurs==1 )
                return pool.createOneOrMore(item);
            else
                return pool.createSequence( exp, pool.createZeroOrMore(item) );
        }
                
        // create (A,(A, ... (A?)? ... )?
        Expression tmp = Expression.epsilon;
        for( int i=minOccurs; i<maxOccurs; i++ )
            tmp = pool.createOptional( pool.createSequence( item, tmp ) );
                
        return pool.createSequence( exp, tmp );
    }

    
    
    protected void switchSource( State sourceState, State newRootState ) throws AbortException {
        String schemaLocation = sourceState.getStartTag().getAttribute("schemaLocation");

        if(schemaLocation == null) {
        	LSResourceResolver resolver = controller.getLSResourceResolver();
        	if (resolver != null) {
        		// TODO: push LSResolver thinking down a level.
        		String namespaceURI = sourceState.getStartTag().getAttribute("namespace");
        		if (namespaceURI == null) {
        			reportError("XmlSchemaReader.noLocation", sourceState.getStartTag().qName);
        			return;
        		}
        		
        		LSInput resolved = resolver.resolveResource(XMLConstants.W3C_XML_SCHEMA_NS_URI,
        					namespaceURI, null, null, sourceState.getBaseURI());

        		if (resolved == null) {
        			reportError("XmlSchemaReader.unresolvedSchema",
        					 sourceState.getStartTag().qName,
        					 namespaceURI);
        			return;
        		}
        		
        		// Make the best source we can out of what comes back from the resolver.
        		Source source = GrammarReader.inputSourceFromLSInput(resolved);
        		switchSource(source, newRootState);
        	}
        } else {
        	// parse specified file
        	switchSource( sourceState, schemaLocation, newRootState );
        }
    }
    
    /**
     * a flag that indicates State objects should check duplicate definitions.
     * This flag is set to false when in &lt;redefine&gt;. Otherwise this flag is true.
     */
    public boolean doDuplicateDefinitionCheck = true;
    
    
    
    /**
     * performs final wrap-up of parsing.
     * this method is called by RootState after the parsing of the entire documents
     * has completed.
     */
    protected void wrapUp() {
        
        // mark schema namespace as defined
        markSchemaAsDefined(xsdSchema);        
        
        // TODO: undefined grammar check.
        Expression grammarTopLevel = Expression.nullSet;
        Iterator<Object> itr = grammar.iterateSchemas();
        while( itr.hasNext() ) {
            XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
            
            if( !isSchemaDefined(schema) ) {
                reportError(
                    backwardReference.getReferer(schema),
                    ERR_UNDEFINED_SCHEMA,
                    new Object[]{schema.targetNamespace} );
                return;    // surpress excessive error messages.
            }
            
            // detect undefined declarations.
            detectUndefinedOnes( schema.attributeDecls,        ERR_UNDEFINED_ATTRIBUTE_DECL );
            detectUndefinedOnes( schema.attributeGroups,    ERR_UNDEFINED_ATTRIBUTE_GROUP );
            detectUndefinedOnes( schema.complexTypes,        ERR_UNDEFINED_COMPLEX_TYPE );
            detectUndefinedOnes( schema.elementDecls,        ERR_UNDEFINED_ELEMENT_DECL );
            detectUndefinedOnes( schema.groupDecls,            ERR_UNDEFINED_GROUP );
            detectUndefinedOnes( schema.simpleTypes,        ERR_UNDEFINED_SIMPLE_TYPE );
            
            
            // TODO: it is now possible to check that the derivation doesn't
            // violate the final property of the parent type.
            
            // prepare top-level expression.
            // at the same time, compute the substitutions field of ElementDeclExps.
            // TODO: make sure this is a correct implementation
            // any globally declared element can be a top-level element.
            Expression exp = Expression.nullSet;
            ReferenceExp[] elems = schema.elementDecls.getAll();
            for( int i=0; i<elems.length; i++ )
                exp = pool.createChoice( exp, elems[i] );
            
            schema.topLevel = exp;
            
            // toplevel of the grammar will be choices of toplevels of all modules.
            grammarTopLevel = pool.createChoice( grammarTopLevel, exp );
        }

        // some of the back-patching process relies on this grammar.topLevel field.
        grammar.topLevel = grammarTopLevel;

        // perform all back patching.
        runBackPatchJob();

        
        
        
        // perform substitutability computation
        //-----------------------------------------
        // this process depends on the result of back-patching.
        
        // a buffer which will be used to check the recursive substitution group definition.
        final Set<ElementDeclExp> recursiveSubstBuffer = new java.util.HashSet<ElementDeclExp>();
        
        itr = grammar.iterateSchemas();
        while( itr.hasNext() ) {
            XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();

            ReferenceExp[] elems = schema.elementDecls.getAll();
            for( int i=0; i<elems.length; i++ ) {
                final ElementDeclExp e = (ElementDeclExp)elems[i];
            
                recursiveSubstBuffer.clear();
                
                if(!controller.hadError()) {
                    // set the substitution group
                    // this process is skipped if an error is already found.
                    // the above check is added because I'm not confident
                    // whether the following code works if some of the properties
                    // are broken due to the invalid grammar.
                    for( ElementDeclExp c = e.substitutionAffiliation;
                         c!=null; c=c.substitutionAffiliation ) {
                        
                        if( !recursiveSubstBuffer.add(c) ) {
                            // recursive substitution group
                            reportError(
                                new Locator[]{ getDeclaredLocationOf(c), getDeclaredLocationOf(e) },
                                ERR_RECURSIVE_SUBSTITUTION_GROUP,
                                new Object[]{c.name, e.name} );
                            break;
                        }
                        
                        if( isSubstitutable( c, e ) ) {
                            c.substitutions.exp =
                                pool.createChoice( c.substitutions.exp, e.body );
                        } else {
                        }
                    }
                }
            }
        }
        
        if( controller.hadError() )    return;
        // undefined expressions may interfare with runaway expression check.
        
        // runaway expression check
        RunAwayExpressionChecker.check( this, grammar.topLevel );

        
        // compute the attribute wildcard
        //-----------------------------------------
        // this process traverses the whole grammar, so runaway check has to
        // be done before this process.
        if(!controller.hadError())
            AttributeWildcardComputer.compute( this, grammar.topLevel );
    }
    
    
    
    
    private interface Type {
        int getDerivationMethod();
        int getBlockValue();
        Type getBaseType();
        Object getCore();
    }
    
    private Type getType( XMLSchemaTypeExp exp ) {
        if( exp instanceof ComplexTypeExp ) {
            final ComplexTypeExp cexp = (ComplexTypeExp)exp;
            return new Type(){
                public int getDerivationMethod() { return cexp.derivationMethod; }
                public int getBlockValue() { return cexp.block; }
                public Type getBaseType() {
                    if( cexp.complexBaseType!=null )
                        return getType(cexp.complexBaseType);
                    if( cexp.simpleBaseType!=null )
                        return getType(cexp.simpleBaseType.getCreatedType());
                    return getType(complexUrType);
                }
                public Object getCore() { return cexp; }
            };
        } else {
            return getType( ((SimpleTypeExp)exp).getDatatype() );
        }
    }
    
    private Type getType( final XSDatatype dt ) {
        if(dt==null)    throw new Error();    // invalid argument error
        
        return new Type(){
            public int getDerivationMethod() { return ComplexTypeExp.RESTRICTION; }
            public int getBlockValue() { return 0; }
            public Type getBaseType() {
                XSDatatype base = dt.getBaseType();
                if(base==null)    return getType(complexUrType);
                else            return getType(base);
            }
            public Object getCore() { return dt; }
        };
    }
    
    /**
     * implementation of "SCC: Substitution Group OK (Transitive)".
     * 
     * @return
     *        <b>true</b> if d can validly substitute c.
     * 
     * @param c
     *        the substitution head
     * @param d
     *        a member of the substitution group of c.
     */
    private boolean isSubstitutable( ElementDeclExp c, ElementDeclExp d ) {
        
        // clause 1
        if(c.isSubstitutionBlocked())
            return false;
        
        // clause 2 must be implicitly tested before this method is called.
        
        
        final Type cType = getType(c.getTypeDefinition());
        Type dType = getType(d.getTypeDefinition());
        
        // test clause 3
        int constraint = c.block;
        int derivationMethod = 0;
                        
        while( true ) {
            if( dType.getCore()==cType.getCore() ) {// if they represents the same object,
                if( (constraint&derivationMethod)==0 )
                    // this substitution doesn't violate blocking constraint.
                    return true;
                else
                    // it is rejected by the blocking constraint
                    return false;
            }
            
            derivationMethod |= dType.getDerivationMethod();
            constraint |= dType.getBlockValue();
            
            if( dType.getCore()==complexUrType ) {
                // error: substitution group has to be related by types.
                reportError(
                    new Locator[]{getDeclaredLocationOf(c),getDeclaredLocationOf(d)},
                    ERR_UNRELATED_TYPES_IN_SUBSTITUTIONGROUP,
                    new Object[]{c.name, d.name} );
                return false;
            }
            
            dType = dType.getBaseType();
                            
            /*
            TODO: thre is a bug in the spec.
                            
            According to the "SCC:Element Declaration Properties Correct",
            clause 3, the type of the substitution group head and the type
            of this element declaration has to be related to through
            "Type Derivation OK".
                            
            Now assume that the type of the head is    union of int and token,
            and the type of this declaration is int. These two types satisfies
            the constraint imposed on "Type Derivation OK".
                            
            Let's move to the "SCC:Substitution Group OK (Transitive)".
            Now the problem arises. According to the clause 3, it is assumed
            that there is a chain of derivation from union(int,token) to
            int, but there is no such thing!
                            
            The decision here is to reject them as errors. But it may be
            better to allow them.
            */
        }
    }    
    
    
    protected String localizeMessage( String propertyName, Object[] args ) {
        String format;
        
        try {
            format = ResourceBundle.getBundle("com.sun.msv.reader.xmlschema.Messages").getString(propertyName);
        } catch( Exception e ) {
            format = ResourceBundle.getBundle("com.sun.msv.reader.Messages").getString(propertyName);
        }
        
        return MessageFormat.format(format, args );
    }
    
    
    public static final String ERR_MAXOCCURS_IS_NECESSARY =    // arg:0
        "XMLSchemaReader.MaxOccursIsNecessary";
    public static final String ERR_UNIMPLEMENTED_FEATURE =    // arg:1
        "XMLSchemaReader.UnimplementedFeature";
    public static final String ERR_UNDECLARED_PREFIX =    // arg:1
        "XMLSchemaReader.UndeclaredPrefix";
    public static final String ERR_INCONSISTENT_TARGETNAMESPACE =    // arg:2
        "XMLSchemaReader.InconsistentTargetNamespace";
    public static final String ERR_IMPORTING_SAME_NAMESPACE =    // arg:1
        "XMLSchemaReader.ImportingSameNamespace";
    public static final String ERR_DUPLICATE_SCHEMA_DEFINITION =    // arg:1
        "XMLSchemaReader.DuplicateSchemaDefinition";
    public static final String ERR_UNDEFINED_ELEMENTTYPE =    // arg:1
        "XMLSchemaReader.UndefinedElementType";
    public static final String ERR_UNDEFINED_ATTRIBUTE_DECL =
        "XMLSchemaReader.UndefinedAttributeDecl";
    public static final String ERR_UNDEFINED_ATTRIBUTE_GROUP =
        "XMLSchemaReader.UndefinedAttributeGroup";
    public static final String ERR_UNDEFINED_COMPLEX_TYPE =
        "XMLSchemaReader.UndefinedComplexType";
    public static final String ERR_UNDEFINED_SIMPLE_TYPE =
        "XMLSchemaReader.UndefinedSimpleType";
    public static final String ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE =
        "XMLSchemaReader.UndefinedComplexOrSimpleType";
    public static final String ERR_UNDEFINED_ELEMENT_DECL =
        "XMLSchemaReader.UndefinedElementDecl";
    public static final String ERR_UNDEFINED_GROUP =
        "XMLSchemaReader.UndefinedGroup";
    public static final String ERR_UNDEFINED_SCHEMA =
        "XMLSchemaReader.UndefinedSchema";
    public static final String WRN_UNSUPPORTED_ANYELEMENT = // arg:1
        "XMLSchemaReader.Warning.UnsupportedAnyElement";
    public static final String WRN_OBSOLETED_NAMESPACE = // arg:0
        "XMLSchemaReader.Warning.ObsoletedNamespace";
    public static final String ERR_UNDEFINED_OR_FORWARD_REFERENCED_TYPE = //arg:1
        "XMLSchemaReader.UndefinedOrForwardReferencedType";
    public static final String ERR_REDEFINE_UNDEFINED = // arg:1
        "XMLSchemaReader.RedefineUndefined";
    public static final String ERR_DUPLICATE_ATTRIBUTE_DEFINITION = // arg:1
        "XMLSchemaReader.DuplicateAttributeDefinition";
    public static final String ERR_DUPLICATE_COMPLEXTYPE_DEFINITION = // arg:1
        "XMLSchemaReader.DuplicateComplexTypeDefinition";
    public static final String ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION = // arg:1
        "XMLSchemaReader.DuplicateAttributeGroupDefinition";
    public static final String ERR_DUPLICATE_GROUP_DEFINITION = // arg:1
        "XMLSchemaReader.DuplicateGroupDefinition";
    public static final String ERR_DUPLICATE_ELEMENT_DEFINITION = // arg:1
        "XMLSchemaReader.DuplicateElementDefinition";
    public static final String ERR_DUPLICATE_IDENTITY_CONSTRAINT_DEFINITION = // arg:1
        "XMLSchemaReader.DuplicateIdentityConstraintDefinition";
    public static final String ERR_BAD_XPATH = // arg:1
        "XMLSchemaReader.BadXPath";
    public static final String ERR_UNDEFINED_KEY = // arg:1
        "XMLSchemaReader.UndefinedKey";
    public static final String ERR_INVALID_BASETYPE_FOR_SIMPLECONTENT = // arg:1
        "XMLSchemaReader.InvalidBasetypeForSimpleContent";
    public static final String ERR_KEY_FIELD_NUMBER_MISMATCH = // arg:2
        "XMLSchemaReader.KeyFieldNumberMismatch";
    public static final String ERR_KEYREF_REFERRING_NON_KEY = // arg:1
        "XMLSchemaReader.KeyrefReferringNonKey";
    public static final String ERR_UNRELATED_TYPES_IN_SUBSTITUTIONGROUP = // arg:2
        "XMLSchemaReader.UnrelatedTypesInSubstitutionGroup";
    public static final String ERR_RECURSIVE_SUBSTITUTION_GROUP = // arg:2
        "XMLSchemaReader.RecursiveSubstitutionGroup";
    public static final String ERR_FIXED_AND_DEFAULT = // arg:0
        "XMLSchemaReader.FixedAndDefault";
    public static final String WRN_IMPLICIT_URTYPE_FOR_ELEMENT = // arg:0
        "XMLSchemaReader.Warning.ImplicitUrTypeForElement";

	public Map<String, String> getAdditionalNamespaceMap() {
		return additionalNamespaceMap;
	}

	public void setAdditionalNamespaceMap(Map<String, String> additionalNamespaceMap) {
		this.additionalNamespaceMap = additionalNamespaceMap;
	}
}
