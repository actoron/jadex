package com.sun.msv.writer.relaxng;

import java.util.*;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.ConcreteType;
import com.sun.msv.datatype.xsd.DataTypeWithFacet;
import com.sun.msv.datatype.xsd.EnumerationFacet;
import com.sun.msv.datatype.xsd.FinalComponent;
import com.sun.msv.datatype.xsd.FractionDigitsFacet;
import com.sun.msv.datatype.xsd.LengthFacet;
import com.sun.msv.datatype.xsd.ListType;
import com.sun.msv.datatype.xsd.MaxLengthFacet;
import com.sun.msv.datatype.xsd.MinLengthFacet;
import com.sun.msv.datatype.xsd.PatternFacet;
import com.sun.msv.datatype.xsd.RangeFacet;
import com.sun.msv.datatype.xsd.TokenType;
import com.sun.msv.datatype.xsd.TotalDigitsFacet;
import com.sun.msv.datatype.xsd.UnionType;
import com.sun.msv.datatype.xsd.WhiteSpaceFacet;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.writer.XMLWriter;

/**
 * Visits Expression and writes it as RELAX NG.
 */
public abstract class PatternWriter implements ExpressionVisitorVoid {
    public PatternWriter(Context ctxt) {
        this.writer = ctxt.getWriter();
        this.context = ctxt;
    }
    
    protected final XMLWriter writer;
    protected final Context context;
    
    public abstract void onOther(OtherExp exp);
    public abstract void onRef(ReferenceExp exp);
    
    public void onElement(ElementExp exp) {
        writer.start("element");
        context.writeNameClass(exp.getNameClass());
        visitUnary(exp.contentModel);
        writer.end("element");
    }
    
    public void onEpsilon() {
        writer.element("empty");
    }
    
    public void onNullSet() {
        writer.element("notAllowed");
    }
    
    public void onAnyString() {
        writer.element("text");
    }
    
    public void onInterleave(InterleaveExp exp) {
        visitBinExp("interleave", exp, InterleaveExp.class);
    }
    
    public void onConcur(ConcurExp exp) {
        throw new IllegalArgumentException("the grammar includes concur, which is not supported");
    }
    
    public void onList(ListExp exp) {
        writer.start("list");
        visitUnary(exp.exp);
        writer.end("list");
    }
    
    protected void onOptional(Expression exp) {
        if (exp instanceof OneOrMoreExp) {
            // (X+)? == X*
            onZeroOrMore((OneOrMoreExp)exp);
            return;
        }
        writer.start("optional");
        visitUnary(exp);
        writer.end("optional");
    }
    
    public void onChoice(ChoiceExp exp) {
        // use optional instead of <choice> p <empty/> </choice>
        if (exp.exp1 == Expression.epsilon) {
            onOptional(exp.exp2);
            return;
        }
        if (exp.exp2 == Expression.epsilon) {
            onOptional(exp.exp1);
            return;
        }
    
        visitBinExp("choice", exp, ChoiceExp.class);
    }
    
    public void onSequence(SequenceExp exp) {
        visitBinExp("group", exp, SequenceExp.class);
    }
    
    public void visitBinExp(String elementName, BinaryExp exp, Class<?> type) {
        // since AGM is binarized,
        // <choice> a b c </choice> is represented as
        // <choice> a <choice> b c </choice></choice>
        // this method print them as <choice> a b c </choice>
        writer.start(elementName);
        Expression[] children = exp.getChildren();
        for (int i = 0; i < children.length; i++)
            children[i].visit(this);
        writer.end(elementName);
    }
    
    public void onMixed(MixedExp exp) {
        writer.start("mixed");
        visitUnary(exp.exp);
        writer.end("mixed");
    }
    
    public void onOneOrMore(OneOrMoreExp exp) {
        writer.start("oneOrMore");
        visitUnary(exp.exp);
        writer.end("oneOrMore");
    }
    
    protected void onZeroOrMore(OneOrMoreExp exp) {
        // note that this method is not a member of TREXPatternVisitor.
        writer.start("zeroOrMore");
        visitUnary(exp.exp);
        writer.end("zeroOrMore");
    }
    
    public void onAttribute(AttributeExp exp) {
        writer.start("attribute");
        context.writeNameClass(exp.nameClass);
        visitUnary(exp.exp);
        writer.end("attribute");
    }
    
    /**
     * print expression but surpress unnecessary sequence.
     */
    public void visitUnary(Expression exp) {
        // TREX treats <zeroOrMore> p q </zeroOrMore>
        // as <zeroOrMore><group> p q </group></zeroOrMore>
        // This method tries to exploit this property to
        // simplify the result.
        if (exp instanceof SequenceExp) {
            SequenceExp seq = (SequenceExp)exp;
            visitUnary(seq.exp1);
            seq.exp2.visit(this);
        } else
            exp.visit(this);
    }
    
    public void onValue( ValueExp exp ) {
        if (exp.dt instanceof XSDatatypeImpl) {
            XSDatatypeImpl base = (XSDatatypeImpl)exp.dt;

            final List<String> ns = new ArrayList<String>();

            String lex = base.convertToLexicalValue(exp.value, new SerializationContext() {
                public String getNamespacePrefix(String namespaceURI) {
                    int cnt = ns.size() / 2;
                    ns.add("xmlns:ns" + cnt);
                    ns.add(namespaceURI);
                    return "ns" + cnt;
                }
            });

            if (base != TokenType.theInstance) {
                // if the type is token, we don't need @type.
                ns.add("type");
                ns.add(base.getName());
            }

            writer.start("value", (String[])ns.toArray(new String[0]));
            writer.characters(lex);
            writer.end("value");
            return;
        }
            
        throw new UnsupportedOperationException( exp.dt.getClass().getName() );
    }
        
    public void onData(DataExp exp) {
        Datatype dt = exp.dt;

        if (dt instanceof XSDatatypeImpl) {
            XSDatatypeImpl dti = (XSDatatypeImpl)dt;

            if (isPredefinedType(dt)) {
                // it's a pre-defined types.
                writer.element("data", new String[] { "type", dti.getName()});
            } else {
                serializeDataType(dti);
            }
            return;
        }

        // unknown datatype
        writer.element("data-unknown", new String[] { "class", dt.getClass().getName()});
    }
        
        
    /**
     * serializes the given datatype.
     * 
     * The caller should generate events for &lt;simpleType&gt; element
     * if necessary.
     */
    protected void serializeDataType(XSDatatype dt) {

        if (dt instanceof UnionType) {
            serializeUnionType((UnionType)dt);
            return;
        }

        // store names of the applied facets into this set
        Set<String> appliedFacets = new HashSet<String>();

        // store effective facets (those which are not shadowed by another facet).
        Vector<XSDatatype> effectiveFacets = new Vector<XSDatatype>();

        XSDatatype x = dt;
        while (x instanceof DataTypeWithFacet || x instanceof FinalComponent) {

            if (x instanceof FinalComponent) {
                // skip FinalComponent
                x = x.getBaseType();
                continue;
            }

            String facetName = ((DataTypeWithFacet)x).facetName;

            if (facetName.equals(XSDatatypeImpl.FACET_ENUMERATION)) {
                // if it contains enumeration, then we will serialize this
                // by using <value>s.
                serializeEnumeration((XSDatatypeImpl)dt, (EnumerationFacet)x);
                return;
            }

            if (facetName.equals(XSDatatypeImpl.FACET_WHITESPACE)) {
                // TODO: better error handling
                System.err.println("warning: unsupported whiteSpace facet is ignored");
                x = x.getBaseType();
                continue;
            }

            // find the same facet twice.
            // pattern is allowed more than once.
            if (!appliedFacets.contains(facetName) || appliedFacets.equals(XSDatatypeImpl.FACET_PATTERN)) {

                appliedFacets.add(facetName);
                effectiveFacets.add(x);
            }

            x = ((DataTypeWithFacet)x).baseType;
        }

        if (x instanceof ListType) {
            // the base type is list.
            serializeListType((XSDatatypeImpl)dt);
            return;
        }

        // it cannot be the union type. Union type cannot be derived by
        // restriction.

        // so this must be one of the pre-defined types.
        if (!(x instanceof ConcreteType))
            throw new Error(x.getClass().getName());

        if (x instanceof com.sun.msv.grammar.relax.EmptyStringType) {
            // empty token will do.
            writer.element("value");
            return;
        }
        if (x instanceof com.sun.msv.grammar.relax.NoneType) {
            // "none" is equal to <notAllowed/>
            writer.element("notAllowed");
            return;
        }

        writer.start("data", new String[] { "type", x.getName()});

        // serialize effective facets
        for (int i = effectiveFacets.size() - 1; i >= 0; i--) {
            DataTypeWithFacet dtf = (DataTypeWithFacet)effectiveFacets.get(i);

            if (dtf instanceof LengthFacet) {
                param("length", Long.toString(((LengthFacet)dtf).length));
            } else if (dtf instanceof MinLengthFacet) {
                param("minLength", Long.toString(((MinLengthFacet)dtf).minLength));
            } else if (dtf instanceof MaxLengthFacet) {
                param("maxLength", Long.toString(((MaxLengthFacet)dtf).maxLength));
            } else if (dtf instanceof PatternFacet) {
                String pattern = "";
                PatternFacet pf = (PatternFacet)dtf;
                for (int j = 0; j < pf.getRegExps().length; j++) {
                    if (pattern.length() != 0)
                        pattern += "|";
                    pattern += pf.patterns[j];
                }
                param("pattern", pattern);
            } else if (dtf instanceof TotalDigitsFacet) {
                param("totalDigits", Long.toString(((TotalDigitsFacet)dtf).precision));
            } else if (dtf instanceof FractionDigitsFacet) {
                param("fractionDigits", Long.toString(((FractionDigitsFacet)dtf).scale));
            } else if (dtf instanceof RangeFacet) {
                param(dtf.facetName, dtf.convertToLexicalValue(((RangeFacet)dtf).limitValue, null));
                // we don't need to pass SerializationContext because it is only
                // for QName.
            } else if (dtf instanceof WhiteSpaceFacet) {
                ; // do nothing.
            } else
                // undefined facet type
                throw new Error();
        }

        writer.end("data");
    }
    
    protected void param(String name, String value) {
        writer.start("param", new String[] { "name", name });
        writer.characters(value);
        writer.end("param");
    }
        
    /**
     * returns true if the specified type is a pre-defined XSD type
     * without any facet.
     */
    protected boolean isPredefinedType(Datatype x) {
        return !(
            x instanceof DataTypeWithFacet
                || x instanceof UnionType
                || x instanceof ListType
                || x instanceof FinalComponent
                || x instanceof com.sun.msv.grammar.relax.EmptyStringType
                || x instanceof com.sun.msv.grammar.relax.NoneType);
    }
        
    /**
     * serializes a union type.
     * this method is called by serializeDataType method.
     */
    protected void serializeUnionType(UnionType dt) {
        writer.start("choice");

        // serialize member types.
        for (int i = 0; i < dt.memberTypes.length; i++)
            serializeDataType(dt.memberTypes[i]);

        writer.end("choice");
    }
        
    /**
     * serializes a list type.
     * this method is called by serializeDataType method.
     */
    protected void serializeListType(XSDatatypeImpl dt) {

        ListType base = (ListType)dt.getConcreteType();

        if (dt.getFacetObject(XSDatatype.FACET_LENGTH) != null) {
            // with the length facet.
            int len = ((LengthFacet)dt.getFacetObject(XSDatatype.FACET_LENGTH)).length;
            writer.start("list");
            for (int i = 0; i < len; i++)
                serializeDataType(base.itemType);
            writer.end("list");

            return;
        }

        if (dt.getFacetObject(XSDatatype.FACET_MAXLENGTH) != null)
            throw new UnsupportedOperationException("warning: maxLength facet to list type is not properly converted.");

        MinLengthFacet minLength = (MinLengthFacet)dt.getFacetObject(XSDatatype.FACET_MINLENGTH);

        writer.start("list");
        if (minLength != null) {
            // list n times
            for (int i = 0; i < minLength.minLength; i++)
                serializeDataType(base.itemType);
        }
        writer.start("zeroOrMore");
        serializeDataType(base.itemType);
        writer.end("zeroOrMore");
        writer.end("list");
    }

    /**
     * serializes a type with enumeration.
     * this method is called by serializeDataType method.
     */
    protected void serializeEnumeration(XSDatatypeImpl dt, EnumerationFacet enums) {

        Object[] values = enums.values.toArray();

        if (values.length > 1)
            writer.start("choice");

        for (int i = 0; i < values.length; i++) {
            final Vector<String> ns = new Vector<String>();

            String lex = dt.convertToLexicalValue(values[i], new SerializationContext() {
                public String getNamespacePrefix(String namespaceURI) {
                    int cnt = ns.size() / 2;
                    ns.add("xmlns:ns" + cnt);
                    ns.add(namespaceURI);
                    return "ns" + cnt;
                }
            });

            // make sure that the converted lexical value is allowed by this type.
            // sometimes, facets that are added later rejects some of
            // enumeration values.

            boolean allowed = dt.isValid(lex, new ValidationContext() {

                public String resolveNamespacePrefix(String prefix) {
                    if (!prefix.startsWith("ns"))
                        return null;
                    int i = Integer.parseInt(prefix.substring(2));
                    return (String)ns.get(i * 2 + 1);
                }

                public boolean isUnparsedEntity(String name) {
                    return true;
                }
                public boolean isNotation(String name) {
                    return true;
                }
                public String getBaseUri() {
                    return null;
                }
            });

            ns.add("type");
            ns.add(dt.getConcreteType().getName());

            if (allowed) {
                writer.start("value", (String[])ns.toArray(new String[0]));
                writer.characters(lex);
                writer.end("value");
            }
        }

        if (values.length > 1)
            writer.end("choice");
    }
}
