/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import java.util.Iterator;
import java.util.Map;

import javaxx.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.relaxng.datatype.DatatypeException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.EmptyStringType;
import com.sun.msv.grammar.relax.Exportable;
import com.sun.msv.grammar.relax.HedgeRules;
import com.sun.msv.grammar.relax.NoneType;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.grammar.relax.TagClause;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSDatatypeResolver;
import com.sun.msv.reader.relax.RELAXReader;
import com.sun.msv.reader.relax.core.checker.DblAttrConstraintChecker;
import com.sun.msv.reader.relax.core.checker.ExportedHedgeRuleChecker;
import com.sun.msv.reader.relax.core.checker.IdAbuseChecker;
import com.sun.msv.util.StartTagInfo;

/**
 * reads RELAX module (classic RELAX module; no namespace extension)
 * by SAX2 and constructs abstract grammar model.
 * 
 * This class does not recognize extensions introduced by RELAX Namespace
 * (like anyOtherElement, or &lt;ref label="..." namespace="..." /&gt;.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXCoreReader extends RELAXReader implements XSDatatypeResolver {
    
    /** loads RELAX module */
    public static RELAXModule parse( String moduleURL,
        SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool ) {
        
        RELAXCoreReader reader = new RELAXCoreReader(controller,factory,pool);
        reader.parse(moduleURL);
        return reader.getResult();
    }

    /** loads RELAX module */
    public static RELAXModule parse( InputSource module,
        SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool ) {
        
        RELAXCoreReader reader = new RELAXCoreReader(controller,factory,pool);
        reader.parse(module);
        return reader.getResult();
    }

    /**
     * Schema for schema of RELAX Core.
     * 
     * Unless overrided, this schema for schema will be used to parse a RELAX Core schema.
     * To override, call the full constructor of this class and change the parameter.
     */
    protected static Schema relaxCoreSchema4Schema = null;
    
    public static Schema getRELAXCoreSchema4Schema() {
        
        // under the multi-thread environment, more than once s4s could be loaded.
        // it's a waste of resource, but by no means fatal.
        if(relaxCoreSchema4Schema==null) {
            try {
                relaxCoreSchema4Schema =
                    new com.sun.msv.verifier.jarv.RELAXCoreFactoryImpl().compileSchema(
                        RELAXCoreReader.class.getResourceAsStream("relaxCore.rlx"));
            } catch( Exception e ) {
                e.printStackTrace();
                throw new Error("unable to load schema-for-schema for RELAX Core");
            }
        }
        
        return relaxCoreSchema4Schema;
    }
    
    public RELAXCoreReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool ) {
        this(controller,parserFactory,new StateFactory(),pool,null);
    }

    /**
     * full constructor.
     * 
     * @param stateFactory
     *        this object creates all parsing state object.
     *        Parsing behavior can be modified by changing this object.
     * @param expectedTargetNamespace
     *        expected value of 'targetNamespace' attribute.
     *        If this value is null, then the module must have 'targetNamepsace'
     *        attribute. If this value is non-null and module doesn't have
     *        targetNamespace attribute, then expectedTargetNamespace is used
     *        as the module's target namespace (chameleon effect).
     *        If expectedNamespace differs from the module's targetNamespace attribute,
     *        then an error will be issued.
     */
    public RELAXCoreReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        StateFactory stateFactory,
        ExpressionPool pool, String expectedTargetNamespace) {
        
        super(controller,parserFactory,stateFactory,pool,new RootModuleState(expectedTargetNamespace));
    }
    
    /**
     * RELAX module object being under construction.
     * 
     * object is created when target namespace is identified.
     */
    protected RELAXModule module;

    /** obtains parsed grammar object only if parsing was successful. */
    public final RELAXModule getResult() {
        if(controller.hadError())    return null;
        else                        return module;
    }
    public final Grammar getResultAsGrammar() {
        return getResult();
    }
    
    /**
     * contains all expressions that are going to be combined.
     * ReferenceExp is used to wrap an expression to provide location information.
     * (attPool element with combine attribute).
     */
    @SuppressWarnings("serial")
    protected final ReferenceContainer combinedAttPools = new ReferenceContainer() {
        protected ReferenceExp createReference( String name ) {
            return new ReferenceExp(name);
        }
    };

    protected boolean isGrammarElement( StartTagInfo tag ) {
        if( !RELAXCoreNamespace.equals(tag.namespaceURI) )
            return false;
        
        // annotation is ignored at this level.
        // by returning false, the entire subtree will be simply ignored.
        if(tag.localName.equals("annotation"))    return false;
        
        return true;
    }

    
    /**
     * User-defined datatypes (from type name to XSDatatypeExp object.
     */
    private final Map<String,XSDatatypeExp> userDefinedTypes = new java.util.HashMap<String,XSDatatypeExp>();
    
    public final void addUserDefinedType( XSDatatypeExp exp ) {
        userDefinedTypes.put( exp.name, exp );
    }
    
    /**
     * gets DataType object from type name.
     * 
     * If undefined type name is specified, this method is responsible
     * to report an error, and recovers.
     */
    public XSDatatypeExp resolveXSDatatype( String typeName ) {
        // look up user defined types first
        try {
            XSDatatypeExp e = (XSDatatypeExp)userDefinedTypes.get(typeName);
            if(e!=null)     return e;
            
            // then try as a built-in type
            // if this method fails, it throws an exception
            return new XSDatatypeExp( DatatypeFactory.getTypeByName(typeName), pool );
        } catch( DatatypeException e ) {
            
            XSDatatype dt = getBackwardCompatibleType(typeName);
            
            if(typeName.equals("none"))         dt = NoneType.theInstance;
            if(typeName.equals("emptyString"))  dt = EmptyStringType.theInstance;
            
            if(dt==null) {
                reportError( ERR_UNDEFINED_DATATYPE, typeName );
                dt = NoneType.theInstance;    // recover by assuming a valid DataType
            }
            
            return new XSDatatypeExp( dt, pool );
        }
    }

    public static class StateFactory extends RELAXReader.StateFactory {
        protected State mixed(State parent,StartTagInfo tag)        { return new MixedState(); }
        protected State element(State parent,StartTagInfo tag)        { return new InlineElementState(); }
        protected State attribute(State parent,StartTagInfo tag)    { return new AttributeState(); }
        protected State refRole(State parent,StartTagInfo tag)        { return new AttPoolRefState(); }
        protected State divInModule(State parent,StartTagInfo tag)    { return new DivInModuleState(); }
        protected State hedgeRule(State parent,StartTagInfo tag)    { return new HedgeRuleState(); }
        protected State tag(State parent,StartTagInfo tag)            { return new TagState(); }
        protected State tagInline(State parent,StartTagInfo tag)    { return new InlineTagState(); }
        protected State attPool(State parent,StartTagInfo tag)        { return new AttPoolState(); }
        protected State include(State parent,StartTagInfo tag)        { return new IncludeModuleState(); }
        protected State interface_(State parent,StartTagInfo tag)    { return new InterfaceState(); }
        protected State elementRule(State parent,StartTagInfo tag) {
            if(tag.containsAttribute("type"))    return new ElementRuleWithTypeState();
            else                                return new ElementRuleWithHedgeState();
        }
        
        protected XSDVocabulary vocabulary = new XSDVocabulary();
        protected State simpleType( State parent, StartTagInfo tag) {
            return vocabulary.createTopLevelReaderState(tag);
        }
    }

    protected final StateFactory getStateFactory() {
        return (StateFactory)super.sfactory;
    }
    
    public State createExpressionChildState( State parent, StartTagInfo tag ) {
        
        if(! RELAXCoreNamespace.equals(tag.namespaceURI) )    return null;
        if(tag.localName.equals("mixed"))            return getStateFactory().mixed(parent,tag);
        if(tag.localName.equals("element"))            return getStateFactory().element(parent,tag);
        
        return super.createExpressionChildState(parent,tag);
    }

    /** returns true if the given state can have "occurs" attribute. */
    protected boolean canHaveOccurs( ExpressionState state ) {
        return
            super.canHaveOccurs(state)
        ||    state instanceof InlineElementState;
    }

    protected Expression resolveElementRef( String namespace, String label ) {
        if( namespace!=null ) {
            reportError( ERR_NAMESPACE_NOT_SUPPROTED );
            return Expression.nullSet;
        }
        Expression exp = module.elementRules.getOrCreate(label);
        backwardReference.memorizeLink(exp);
        return exp;
    }
    protected Expression resolveHedgeRef( String namespace, String label ) {
        if( namespace!=null ) {
            reportError( ERR_NAMESPACE_NOT_SUPPROTED );
            return Expression.nullSet;
        }
        Expression exp = module.hedgeRules.getOrCreate(label);
        backwardReference.memorizeLink(exp);
        return exp;
    }
    
    protected Expression resolveAttPoolRef( String namespace, String role ) {
        if( namespace!=null ) {
            reportError( ERR_NAMESPACE_NOT_SUPPROTED );
            return Expression.nullSet;
        }
        
        AttPoolClause c = module.attPools.getOrCreate(role);
        backwardReference.memorizeLink(c);
        return c;
    }
    
    
    protected void wrapUp() {
        
        runBackPatchJob();
        
        // register user-defined types to the module
        Iterator<Map.Entry<String,XSDatatypeExp>> itr = userDefinedTypes.entrySet().iterator();
        while( itr.hasNext() ) {
            XSDatatypeExp e = itr.next().getValue();
            module.datatypes.add(e.getCreatedType());
        }
        
        // combine expressions to their masters.
        // if no master is found, then create a new AttPool.
        {
            ReferenceExp[] combines = combinedAttPools.getAll();
            for ( int i=0; i<combines.length; i++ ) {
                
                AttPoolClause ac = module.attPools.get(combines[i].name);
                if( ac!=null ) {
                    // ac.exp==null means no master is found but someone
                    // has a reference to this clause.
                    // this is OK.
                    if( ac.exp==null )        ac.exp=Expression.epsilon;
                    ac.exp = pool.createSequence( ac.exp, combines[i].exp );
                    continue;
                }
                
                TagClause tc = module.tags.get(combines[i].name);
                if( tc!=null && tc.exp!=null ) {
                    // tc.exp==null means no master is found.
                    // In this case, we can't combine us to TagClause.
                    tc.exp = pool.createSequence( tc.exp, combines[i].exp );
                    continue;
                }
                
                // no master is found. Create a new one.
                ac = module.attPools.getOrCreate(combines[i].name);
                ac.exp = combines[i].exp;
            }
        }

        // role collision check.
        detectCollision( module.tags, module.attPools, ERR_ROLE_COLLISION );
        
        
        // detect undefined elementRules, hedgeRules, and so on.
        // dummy definitions are given for undefined ones.
        detectUndefinedOnes( module.elementRules,ERR_UNDEFINED_ELEMENTRULE );
        detectUndefinedOnes( module.hedgeRules,    ERR_UNDEFINED_HEDGERULE );
        detectUndefinedOnes( module.tags,        ERR_UNDEFINED_TAG );
        detectUndefinedOnes( module.attPools,    ERR_UNDEFINED_ATTPOOL );
        
        // label collision detection should be done after
        // undefined label detection because
        // sometimes people use <ref label/> for hedgeRule,
        
        // detect label collision.
        // it is prohibited for elementRule and hedgeRule to share the same label.
        detectCollision( module.elementRules, module.hedgeRules, ERR_LABEL_COLLISION );
                        
        detectDoubleAttributeConstraints( module );
                        
        // checks ID abuse
        IdAbuseChecker.check( this, module );
        
        // supply top-level expression.
        Expression exp =
            pool.createChoice(
                choiceOfExported( module.elementRules ),
                choiceOfExported( module.hedgeRules ) );
            
        if( exp==Expression.nullSet )
            // at least one element must be exported or
            // the grammar accepts nothing.
            reportWarning( WRN_NO_EXPROTED_LABEL );
            
        module.topLevel = exp;
        
        // make sure that there is no recurisve hedge rules.
        RunAwayExpressionChecker.check( this, module.topLevel );
            
        
        {// make sure that there is no exported hedgeRule that references a label in the other namespace.
            Iterator<ReferenceExp> jtr = module.hedgeRules.iterator();
            while(jtr.hasNext()) {
                HedgeRules hr = (HedgeRules)jtr.next();
                if(!hr.exported)    continue;
                        
                ExportedHedgeRuleChecker ehrc = new ExportedHedgeRuleChecker(module);
                if(!hr.visit( ehrc )) {
                    // this hedgeRule directly/indirectly references exported labels.
                    // report it to the user.
                            
                    // TODO: source information?
                    String dependency="";
                    for( int i=0; i<ehrc.errorSnapshot.length-1; i++ )
                        dependency+= ehrc.errorSnapshot[i].name + " > ";
                            
                    dependency += ehrc.errorSnapshot[ehrc.errorSnapshot.length-1].name;
                            
                    reportError( ERR_EXPROTED_HEDGERULE_CONSTRAINT, dependency );
                            
                }
            }
        }
    }


    private Expression choiceOfExported( ReferenceContainer con )
    {
        Iterator<ReferenceExp> itr = con.iterator();
        Expression r = Expression.nullSet;
        while( itr.hasNext() ) {
            Exportable ex= (Exportable)itr.next();
            if( ex.isExported() ) {
                r = pool.createChoice(r,(Expression)ex);
            }
        }
        return r;
    }
    
        
    /** detect two AttributeExps that share the same target name.
     * 
     * See {@link DblAttrConstraintChecker} for details.
     */
    private void detectDoubleAttributeConstraints( RELAXModule module ) {
        final DblAttrConstraintChecker checker = new DblAttrConstraintChecker();
        
        Iterator<?> itr = module.tags.iterator();
        while( itr.hasNext() )
            // errors will be reported within this method
            // no recovery is necessary.
            checker.check( (TagClause)itr.next(), this );
    }

    
    private void detectCollision( ReferenceContainer col1, ReferenceContainer col2, String errMsg ) {
        Iterator<?> itr = col1.iterator();
        while( itr.hasNext() ) {
            ReferenceExp r1    = (ReferenceExp)itr.next();
            ReferenceExp r2    = col2._get( r1.name );
            // if the grammar references elementRule by hedgeRef,
            // (or hedgeRule by ref),  HedgeRules object and ElementRules object
            // are created under the same name.
            // And it is inappropriate to report this situation as "label collision".
            // Therefore, we have to check both have definitions before reporting an error.
            if( r2!=null && r1.exp!=null && r2.exp!=null )
                reportError(
                    new Locator[]{ getDeclaredLocationOf(r1),
                                   getDeclaredLocationOf(r2) },
                    errMsg,    new Object[]{r1.name} );
        }
    }
    
    
    
// error messages    
    protected String localizeMessage( String propertyName, Object[] args ) {
        return super.localizeMessage(propertyName,args);
    }
    
    public static final String ERR_NAMESPACE_NOT_SUPPROTED =
        "RELAXReader.NamespaceNotSupported";        // arg:0
    public static final String ERR_INCONSISTENT_TARGET_NAMESPACE    // arg:2
        = "RELAXReader.InconsistentTargetNamespace";
    public static final String ERR_MISSING_TARGET_NAMESPACE    // arg:0
        = "RELAXReader.MissingTargetNamespace";
    public static final String ERR_MULTIPLE_TAG_DECLARATIONS    // arg:1
        = "RELAXReader.MultipleTagDeclarations";
    public static final String ERR_MORE_THAN_ONE_INLINE_TAG    // arg:0
        = "RELAXReader.MoreThanOneInlineTag";
    public static final String ERR_MULTIPLE_ATTPOOL_DECLARATIONS    // arg:1
        = "RELAXReader.MultipleAttPoolDeclarations";
    public static final String ERR_UNDEFINED_ELEMENTRULE    // arg:1
        = "RELAXReader.UndefinedElementRule";
    public static final String ERR_UNDEFINED_HEDGERULE    // arg:1
        = "RELAXReader.UndefinedHedgeRule";
    public static final String ERR_UNDEFINED_TAG    // arg:1
        = "RELAXReader.UndefinedTag";
    public static final String ERR_UNDEFINED_ATTPOOL    // arg:1
        = "RELAXReader.UndefinedAttPool";
    public static final String ERR_LABEL_COLLISION    // arg:1
        = "RELAXReader.LabelCollision";
    public static final String ERR_ROLE_COLLISION    // arg:1
        = "RELAXReader.RoleCollision";
    public static final String WRN_NO_EXPROTED_LABEL    // arg:0
        = "RELAXReader.NoExportedLabel";
    public static final String ERR_EXPROTED_HEDGERULE_CONSTRAINT
        = "RELAXReader.ExportedHedgeRuleConstraint";    // arg:1
    public static final String ERR_MULTIPLE_ATTRIBUTE_CONSTRAINT // arg:1
        = "RELAXReader.MultipleAttributeConstraint";
    public static final String ERR_ID_ABUSE // arg:0
        = "RELAXReader.IdAbuse";
    public static final String ERR_ID_ABUSE_1 // arg:1
        = "RELAXReader.IdAbuse.1";
//    public static final String ERR_REFERENCE_TO_MERGED_ATTPOOL // arg:1
//        = "RELAXReader.ReferenceToMergedAttPool";
//    public static final String WRN_DEPRECATED_TYPENAME = // arg:2
//        "RELAXReader.Warning.DeprecatedTypeName";
    public static final String WRN_ILLEGAL_RELAXCORE_VERSION    // arg:1
        = "RELAXReader.Warning.IllegalRelaxCoreVersion";
}
