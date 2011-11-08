package com.sun.msv.reader.trex.ng.comp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.util.RefExpRemover;
import com.sun.msv.util.StringPair;
import com.sun.msv.verifier.regexp.ResidualCalculator;
import com.sun.msv.verifier.regexp.StringToken;

class DefAttCompatibilityChecker extends CompatibilityChecker {
    
    DefAttCompatibilityChecker( RELAXNGCompReader _reader, Map<AttributeExp,String> _defaultedAttributes ) {
        super(_reader);
        this.defaultedAttributes = _defaultedAttributes;
    }
    
    protected void setCompatibility( boolean val ) {
        grammar.isDefaultAttributeValueCompatible = val;
    }
    
    private final Map<AttributeExp,String> defaultedAttributes;

    /**
     * used to abort the check.
     */
    @SuppressWarnings("serial")
    private static final class Abort extends RuntimeException {}

    private static final class DefAttMap {
        /**
         * the map from the attribute name (as StringPair) to the default value (as String).
         */
        final Map<StringPair,String> defaultAttributes;
        /**
         * one of the ElementExps that have this particular element name.
         * used only for the error reporting.
         */
        final ElementExp sampleDecl;
        
        DefAttMap( ElementExp sample, Map<StringPair,String> atts ) { this.sampleDecl=sample; this.defaultAttributes=atts; }
    }
    
    private final RefExpRemover refRemover =
        new RefExpRemover(reader.pool,false);
    /**
     * returns true if the exp is equivalent to &lt;empty/&gt; after
     * the simplification.
     */
    private boolean isEpsilon( Expression exp ) {
        if(exp==Expression.epsilon)        return true;
        return exp.visit(refRemover) == Expression.epsilon;
    }
    
    /**
     * tests the compatibility with the default attribute feature.
     */
    public void test() {
        grammar.isDefaultAttributeValueCompatible = true;
        
        if( defaultedAttributes.size()==0 )
            return;    // no default attribute is used. no need for the check.
        
        Iterator<Map.Entry<AttributeExp,String>> itr = defaultedAttributes.entrySet().iterator();
        ResidualCalculator resCalc = new ResidualCalculator(reader.pool);
            
        while( itr.hasNext() ) {
            Map.Entry<AttributeExp,String> item = itr.next();
            AttributeExp exp = (AttributeExp)item.getKey();
            String value = (String)item.getValue();
                    
            // tests if the name class is simple
            if(!(exp.nameClass instanceof SimpleNameClass))
                reportCompError(
                    new Locator[]{reader.getDeclaredLocationOf(exp)},
                    CERR_DEFVALUE_NAME_IS_NOT_SIMPLE);
                    
            // tests if there is no context-dependent datatypes
            try {
                exp.exp.visit( contextDependentTypeChecker );
            } catch( Abort a ) {
                continue;    // abort further check. this error is already reported.
            }
                    
            // tests if the default value matches the content model of this attribute
            StringToken token = new StringToken(resCalc,value,null,null);
            if(!resCalc.calcResidual( exp.exp, token ).isEpsilonReducible() ) {
                // the default value was rejected by the content model.
                reportCompError(
                    new Locator[]{reader.getDeclaredLocationOf(exp)},
                    CERR_DEFVALUE_INVALID, new Object[]{value});
            }
                    
        }
        
        if( !grammar.isDefaultAttributeValueCompatible )
            // if there already is an error, abort further check.
            return;
                
        // a map from element names to DefAttMap
        final Map<StringPair,Object> name2value = new HashMap<StringPair,Object>();
        
        // a set of all ElementExps in the grammar.
        final Set<Expression> elements = new HashSet<Expression>();
        
        // tests if defaulted attributes are optional and doesn't have
        // oneOrMoreAncestor.
        // also (element name,attribute name)->default value
        // map is created here.
        grammar.visit( new ExpressionWalker() {
            // in the first pass, the elements variable
            // is used to record visited ElementExps.
            
            // condition that has to be met for default attributes to be valid.
            private boolean inOneOrMore = false;
            private boolean inChoice = false;
            private boolean inOptionalChoice = false;
            private boolean inSimpleElement = false;
                    
            /**
             * A map from attribute name to defaulted AttributeExps
             * of the current element.
             */
            private Map<StringPair,String> currentAttributes = null;
                    
            /**
             * name of the current ElementExp
             * within which we are currently processing.
             */
            private SimpleNameClass currentElementName = null;
                    
            public void onElement( ElementExp exp ) {
                if( !elements.add(exp) )
                    return;    // this element is already checked.
                        
                // otherwise check the content model of this element.
                        
                // update the value of the isSimpleElement field.
                final boolean oldSE        = inSimpleElement;
                final boolean oldOC        = inOptionalChoice;
                final boolean oldC        = inChoice;
                final boolean oldOOM    = inOneOrMore;
                final SimpleNameClass prevElemName = currentElementName;
                final Map<StringPair,String> oldCA = currentAttributes;
                        
                inSimpleElement = (exp.getNameClass() instanceof SimpleNameClass);
                inOptionalChoice = true;
                inChoice = false;
                inOneOrMore = false;
                
                StringPair en = null;
                if(inSimpleElement) {
                    currentElementName = (SimpleNameClass)exp.getNameClass();
                    en = new StringPair(currentElementName);
                    currentAttributes = new HashMap<StringPair,String>();
                } else
                    currentElementName = null;
                
                exp.contentModel.visit(this);

                if(en!=null) {
                    DefAttMap m = (DefAttMap)name2value.get(en);
                    if(m==null) {
                        name2value.put(en, new DefAttMap(exp,currentAttributes));
                    } else {
                        // there was another ElementExp with the same name.
                        // we need to check that their default attribute values
                        // are consistent.
                        if(!m.defaultAttributes.equals(currentAttributes)) {
                            // TODO: provide better message
                            reportCompError(
                                new Locator[]{
                                    reader.getDeclaredLocationOf(m.sampleDecl),
                                reader.getDeclaredLocationOf(exp)},
                                CERR_DEFVALUE_COMPETING_ELEMENTS,
                                new Object[]{
                                    ((SimpleNameClass)m.sampleDecl.getNameClass()).localName
                                });
                            // make this element name fresh
                            // so that the user won't see excessive error messages.
                            name2value.remove(en);
                        }
                    }
                }
                
                inSimpleElement = oldSE;
                inOptionalChoice = oldOC;
                inChoice = oldC;
                inOneOrMore = oldOOM;
                currentElementName = prevElemName;
                currentAttributes = oldCA;
            }
                    
            public void onOneOrMore( OneOrMoreExp exp ) {
                final boolean oldOOM = inOneOrMore;
                inOneOrMore = true;
                exp.exp.visit(this);
                inOneOrMore=oldOOM;
            }
                    
            public void onChoice( ChoiceExp exp ) {
                final boolean oldOC = inOptionalChoice;
                final boolean oldC = inChoice;
                        
                inChoice = true;
                if(!isEpsilon(exp.exp1) && !isEpsilon(exp.exp2))
                    inOptionalChoice = false;
                super.onChoice(exp);
                        
                inOptionalChoice = oldOC;
                inChoice = oldC;
            }
                    
            public void onAttribute( AttributeExp exp ) {
                if( defaultedAttributes.containsKey(exp) ) {
                    // this attribute has a default value.
                    if(!inOptionalChoice || !inChoice) {
                        reportCompError(
                            new Locator[]{reader.getDeclaredLocationOf(exp)},
                            CERR_DEFVALUE_NOT_OPTIONAL);
                        return;    // abort
                    }
                    if(inOneOrMore) {
                        reportCompError(
                            new Locator[]{reader.getDeclaredLocationOf(exp)},
                            CERR_DEFVALUE_REPEATABLE);
                        return; // abort
                    }
                    if(!inSimpleElement) {
                        reportCompError(
                            new Locator[]{reader.getDeclaredLocationOf(exp)},
                            CERR_DEFVALUE_COMPLEX_ELEMENTNAME);
                        return; // abort
                    }
                    
                    String value = (String)defaultedAttributes.get(exp);
                    String v = (String)currentAttributes.put(
                        new StringPair((SimpleNameClass)exp.nameClass), value );
                            
                    if(v!=null) {
                        // make sure that this value and the previous value
                        // are the same
                        if(!v.equals(value))
                            reportCompError(
                                new Locator[]{reader.getDeclaredLocationOf(exp)},
                                CERR_DEFVALUE_DIFFERENT_VALUES,
                                new Object[]{v,value,
                                currentElementName.localName,
                                ((SimpleNameClass)exp.nameClass).localName});
                    }
                }
            }
                    
                    
            public void onList( ListExp exp ) {
                // ListExp may never contain ElementExp nor AttributeExp,
                // so visiting its children is nothing but a waste of time.
            }
        });
                
        
        // test that the competing elements also has the same default values.
        Iterator<Expression> exprs = elements.iterator();
        while(exprs.hasNext()) {
            final ElementExp eexp = (ElementExp)exprs.next();
            
            NameClass nc = eexp.getNameClass();
            if(!(nc instanceof SimpleNameClass)) {
                // if the element has a complex name class,
                // it cannot have a default attribute value.
                // (this is checked within the first pass.
                // so in this case, we just need to make sure that
                // any competing elements do not have defaulted attributes.
                Iterator<Map.Entry<StringPair, Object>> jtr = name2value.entrySet().iterator();
                while(jtr.hasNext()) {
                    Map.Entry<StringPair,Object> e = jtr.next();
                    if(nc.accepts((StringPair)e.getKey())) {
                        // this element competes with this eexp.
                        DefAttMap defAtts = (DefAttMap)e.getValue();
                        if(defAtts.defaultAttributes.size()>0) {
                            // TODO: what should the error message be?
                            reportCompError(
                                new Locator[]{
                                    reader.getDeclaredLocationOf(defAtts.sampleDecl),
                                    reader.getDeclaredLocationOf(eexp)},
                                CERR_DEFVALUE_COMPETING_ELEMENTS,
                                new Object[]{
                                    ((SimpleNameClass)defAtts.sampleDecl.getNameClass()).localName
                                });
                            return;    // abort the check
                        }
                    }
                }
            } /* else {
                if the element has a simple name, then
                all checks are done in the 1st pass.
            } */
        }
    }

    /**
     * checks if the expression contains context-dependent datatypes.
     * If so, it reports an error and throws an Abort exception.
     */
    private ExpressionWalker contextDependentTypeChecker = new ExpressionWalker(){
        public void onData( DataExp exp )    { check(exp.dt,exp.name); }
        public void onValue( ValueExp exp ) { check(exp.dt,exp.name); }
        private void check( Datatype dt, StringPair name ) {
            if(dt.isContextDependent()) {
                reportCompError(
                    null,
                    CERR_DEFVALUE_CONTEXT_DEPENDENT_TYPE,
                    new Object[]{name.localName});
                throw new Abort();
            }
        }
    };

    public static final String CERR_DEFVALUE_NAME_IS_NOT_SIMPLE = // arg:0
        "RELAXNGReader.Compatibility.DefaultValue.NameIsNotSimple";
    public static final String CERR_DEFVALUE_INVALID = // arg:1
        "RELAXNGReader.Compatibility.DefaultValue.Invalid";
    public static final String CERR_DEFVALUE_NOT_OPTIONAL = // arg:0
        "RELAXNGReader.Compatibility.DefaultValue.NotOptional";
    public static final String CERR_DEFVALUE_REPEATABLE = // arg:0
        "RELAXNGReader.Compatibility.DefaultValue.Repeatable";
    public static final String CERR_DEFVALUE_COMPLEX_ELEMENTNAME = // arg:0
        "RELAXNGReader.Compatibility.DefaultValue.ComplexElementName";
    public static final String CERR_DEFVALUE_DIFFERENT_VALUES     = // arg:4
        "RELAXNGReader.Compatibility.DefaultValue.DifferentValues";
//    public static final String CERR_DEFVALUE_POSSIBLY_UNCOMPATIBLE_TYPE = // arg:1
//        "RELAXNGReader.Compatibility.DefaultValue.PossiblyUncompatibleType";
    public static final String CERR_DEFVALUE_CONTEXT_DEPENDENT_TYPE = // arg:1
        "RELAXNGReader.Compatibility.DefaultValue.ContextDependentType";
    public static final String CERR_DEFVALUE_COMPETING_ELEMENTS = // arg:1
        "RELAXNGReader.Compatibility.DefaultValue.CompetingElements";
    
}
