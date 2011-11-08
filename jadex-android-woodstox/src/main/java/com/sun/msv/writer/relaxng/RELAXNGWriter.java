/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer.relaxng;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.util.PossibleNamesCollector;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.util.StringPair;
import com.sun.msv.writer.GrammarWriter;
import com.sun.msv.writer.SAXRuntimeException;
import com.sun.msv.writer.XMLWriter;

/**
 * converts any Grammar into RELAX NG XML representation through SAX1 events.
 * 
 * <h2>How it works</h2>
 * 
 * <p>
 *   {@link Grammar} object can be thought as a (possibly) cyclic graph
 *   made from {@link Expression}. For example, the following simple
 *   TREX pattern will be represented as following AGM.
 * </p>
 * <pre><xmp>
 * <grammar>
 *   <start name="X">
 *     <element name="foo">
 *       <choice>
 *         <string> abc </string>
 *         <ref name="Y" />
 *       </choice>
 *     </element>
 *   </start>
 *   <define name="Y">
 *     <element name="bar">
 *       <string> abc </string>
 *       <optional>
 *         <ref name="X" />
 *       </optional>
 *     </element>
 *   </define>
 * </grammar>
 * </xmp></pre>
 * <img src="doc-files/simpleAGM.gif" />
 * 
 * <p>
 *   Note that
 * </p>
 * <ul>
 *   <li>sub expressions are shared (see &lt;string&gt; expression).
 *   <li>there is a cycle in the graph.
 *   <li>several syntax elements are replaced by others
 *       (e.g., &lt;optional&gt;P&lt;/optional&gt; -&gt; &lt;choice&gt;&lt;empty/&gt;P&lt;/choice&gt;)
 * </ul>
 * 
 * <p>
 *   To write these expressions into TREX XML representation,
 *   we have to take care of cycles, since cyclic references cannot be written into
 *   XML without first cut it and use &lt;ref&gt;/&lt;define&gt; pair.
 * </p>
 * 
 * <p>
 *   First, this algorithm splits the grammar into <i>"islands"</i>.
 *   Island is a tree of expressions; it has a <i>head</i> expression
 *   and most importantly it doesn't contain any cycles in it. Member of an island
 *   can be always reached from its head.
 * </p>
 * <img src="doc-files/island.gif"/>
 * <p>
 *   TREXWriter will make every {@link ElementExp} and
 *   {@link ReferenceExp} a head of their own island. So each of them
 *   has their own island.
 * </p><p>
 *   It is guaranteed that this split will always give islands without inner cycles.
 *   Several islands can form a cycle, but one island can never have a cycle in it.
 *   This is because there is always at least one ElementExp in any cycle.
 * </p>
 * <img src="doc-files/island_before.gif" />
 * <p>
 *   Note that since expressions are shared, one expression can be
 *   a member of several islands (although this isn't depicted in the above figure.)
 * </p>
 * <p>
 *   Then, this algorithm merges some islands. For example, island E is
 *   referenced only once (from island D). This means that there is no need to
 *   give a name to this pattern. Instead, island E can simply written as a
 *   subordinate of island D.
 * </p><p>
 *   In other words, any island who is only referenced at most once is merged
 *   into its referer. This step makes the output more compact.
 * </p>
 * <img src="doc-files/island_merged.gif" />
 * <p>
 *   Next, TREXWriter assigns a name to each island. It tries to use the name of
 *   the head expression. If a head is anonymous ReferenceExp (ReferenceExp whose
 *   name field is <code>null</code>) or there is a name conflict, TREXWriter
 *   will add some suffix to make the name unique.
 * </p><p>
 *   Finally, each island is written as one named pattern under &lt;define&gt;
 *   element. All inter-island references are replaced by &lt;ref&gt; element.
 * </p>
 * 
 * <h2>Why SAX1?</h2>
 * <p>
 *   Due to the bug and insufficient supports for the serialization through SAX2,
 *   The decision is made to use SAX1. SAX1 allows us to control namespace prefix
 *   mappings better than SAX2.
 * </p>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("deprecation")
public class RELAXNGWriter implements GrammarWriter, Context {
    
    protected XMLWriter writer = new XMLWriter();
    public XMLWriter getWriter() { return writer; }
    
    public void setDocumentHandler( DocumentHandler handler ) {
        writer.setDocumentHandler(handler);
    }
    
    public void write( Grammar g ) throws SAXException {
        // find a namespace URI that can be used as default "ns" attribute.
        write(g,sniffDefaultNs(g.getTopLevel()));
    }
    
    /**
     * generates SAX2 events of the specified grammar.
     * 
     * @param defaultNs
     *        if specified, this namespace URI is used as "ns" attribute
     *        of grammar element. Can be null.
     * 
     * @exception IllegalArgumentException
     *        If the given grammar is beyond the expressive power of TREX
     *        (e.g., some RELAX NG grammar), then this exception is thrown.
     */
    public void write( Grammar g, String _defaultNs ) throws SAXException {
        
        this.defaultNs = _defaultNs;
        this.grammar = g;
        
        // collect all reachable ElementExps and ReferenceExps.
        final Set<Expression> nodes = new HashSet<Expression>();
        // ElementExps and ReferenceExps who are referenced more than once.
        final Set<Expression> heads = new HashSet<Expression>();
        
        g.getTopLevel().visit( new ExpressionWalker(){
            // ExpressionWalker class traverses expressions in depth-first order.
            // So this invokation traverses the all reachable expressions from
            // the top level expression.
            
            // Whenever visiting elements and RefExps, they are memorized
            // to identify head of islands.
            public void onElement( ElementExp exp ) {
                if(nodes.contains(exp)) {
                    heads.add(exp);
                    return;    // prevent infinite recursion.
                }
                nodes.add(exp);
                super.onElement(exp);
            }
            public void onRef( ReferenceExp exp ) {
                if(nodes.contains(exp)) {
                    heads.add(exp);
                    return;    // prevent infinite recursion.
                }
                nodes.add(exp);
                super.onRef(exp);
            }
        });
        
        // now heads contain all expressions that work as heads of islands.
        
        
        // create (name->RefExp) map while resolving name conflicts
        // 
        Map<String,Expression> name2exp = new HashMap<String,Expression>();
        {
            int cnt=0;    // use to name anonymous RefExp.
        
            Iterator<Expression> itr = heads.iterator();
            while( itr.hasNext() ) {
                Expression exp = itr.next();
                if( exp instanceof ReferenceExp ) {
                    ReferenceExp rexp = (ReferenceExp)exp;
                    if( rexp.name == null ) {
                        // generate unique name
                        while( name2exp.containsKey("anonymous"+cnt) )
                            cnt++;
                        name2exp.put( "anonymous"+cnt, exp );
                    } else
                    if( name2exp.containsKey(rexp.name) ) {
                        // name conflict. try to add suffix.
                        int i = 2;
                        while( name2exp.containsKey(rexp.name+i) )
                            i++;
                        name2exp.put( rexp.name+i, exp );
                    } else {
                        // name of this RefExp can be directly used without modification.
                        name2exp.put( rexp.name, exp );
                    }
                }
                else
                if( exp instanceof ElementExp ) {
                    ElementExp eexp = (ElementExp)exp;
                    NameClass nc = eexp.getNameClass();
                    
                    if( nc instanceof SimpleNameClass
                     && !name2exp.containsKey( ((SimpleNameClass)nc).localName ) )
                        name2exp.put( ((SimpleNameClass)nc).localName, exp );
                    else {
                        // generate unique name
                        while( name2exp.containsKey("element"+cnt) )
                            cnt++;
                        name2exp.put( "element"+cnt, exp );
                    }
                } else
                    throw new Error();    // assertion failed.
                    // it must be ElementExp or ReferenceExp.
            }
        }
        
        // then reverse name2ref to ref2name
        exp2name = new HashMap<Expression,String>();
        {
            Iterator<String> itr = name2exp.keySet().iterator();
            while( itr.hasNext() ) {
                String name = itr.next();
                Expression expr = name2exp.get(name);
                exp2name.put(expr, name);
            }
        }
        
        nameClassWriter = createNameClassWriter();        
 
        // generates SAX events
        try {
            final DocumentHandler handler = writer.getDocumentHandler();
            handler.setDocumentLocator( new LocatorImpl() );
            handler.startDocument();

            // to work around the bug of current serializer,
            // report xmlns declarations as attributes.
            
            if( defaultNs!=null )
                writer.start("grammar",new String[]{
                    "ns",defaultNs,
                    "xmlns",RELAXNGReader.RELAXNGNamespace,
                    "datatypeLibrary", XSDVocabulary.XMLSchemaNamespace });
            else
                writer.start("grammar", new String[]{
                    "xmlns",RELAXNGReader.RELAXNGNamespace,
                    "datatypeLibrary", XSDVocabulary.XMLSchemaNamespace });
            
            
            {// write start pattern.
                writer.start("start");
                writeIsland( g.getTopLevel() );
                writer.end("start");
            }
            
            // write all named expressions
            Iterator<Expression> itr = exp2name.keySet().iterator();
            while( itr.hasNext() ) {
                Expression exp = itr.next();
                String name = exp2name.get(exp);
                if( exp instanceof ReferenceExp )
                    exp = ((ReferenceExp)exp).exp;
                writer.start("define",new String[]{"name",name});
                writeIsland( exp );
                writer.end("define");
            }
            
            writer.end("grammar");
            handler.endDocument();
        } catch( SAXRuntimeException sw ) {
            throw sw.e;
        }
    }
    
    /**
     * writes a bunch of expression into one tree.
     */
    protected void writeIsland( Expression exp ) {
        // pattern writer will traverse the island and generates XML representation.
        if( exp instanceof ElementExp )
            patternWriter.writeElement( (ElementExp)exp );
        else
            patternWriter.visitUnary(exp);
    }
    
    
    /** Grammar object which we are writing. */
    protected Grammar grammar;
    
    /**
     * map from ReferenceExp/ElementExp to its unique name.
     * "unique name" is used to write/reference this ReferenceExp.
     * ReferenceExps who are not in this list can be directly written into XML.
     */
    protected Map<Expression,String> exp2name;
    
    /**
     * sniffs namespace URI that can be used as default 'ns' attribute
     * from expression.
     * 
     * find an element or attribute, then use its namespace URI.
     */
    protected String sniffDefaultNs( Expression exp ) {
        return (String)exp.visit( new ExpressionVisitor(){
            public Object onElement( ElementExp exp ) {
                return sniff(exp.getNameClass());
            }
            public Object onAttribute( AttributeExp exp ) {
                return sniff(exp.nameClass);
            }
            protected String sniff(NameClass nc) {
                if( nc instanceof SimpleNameClass )
                    return ((SimpleNameClass)nc).namespaceURI;
                else
                    return null;
            }
            public Object onChoice( ChoiceExp exp ) {
                return onBinExp(exp);
            }
            public Object onSequence( SequenceExp exp ) {
                return onBinExp(exp);
            }
            public Object onInterleave( InterleaveExp exp ) {
                return onBinExp(exp);
            }
            public Object onConcur( ConcurExp exp ) {
                return onBinExp(exp);
            }
            public Object onBinExp( BinaryExp exp ) {
                Object o = exp.exp1.visit(this);
                if(o==null)    o = exp.exp2.visit(this);
                return o;
            }
            public Object onMixed( MixedExp exp ) {
                return exp.exp.visit(this);
            }
            public Object onOneOrMore( OneOrMoreExp exp ) {
                return exp.exp.visit(this);
            }
            public Object onRef( ReferenceExp exp ) {
                return exp.exp.visit(this);
            }
            public Object onOther( OtherExp exp ) {
                return exp.exp.visit(this);
            }
            public Object onNullSet() {
                return null;
            }
            public Object onEpsilon() {
                return null;
            }
            public Object onAnyString() {
                return null;
            }
            public Object onData( DataExp exp ) {
                return null;
            }
            public Object onValue( ValueExp exp ) {
                return null;
            }
            public Object onList( ListExp exp ) {
                return null;
            }
        });
    }
    
    
    /**
     * namespace URI currently implied through "ns" attribute propagation.
     */
    protected String defaultNs;
    public String getTargetNamespace() { return defaultNs; }
    

    
    public void writeNameClass( NameClass src ) {
        final String MAGIC = PossibleNamesCollector.MAGIC;
        Set<StringPair> names = PossibleNamesCollector.calc(src);
        
        // convert a name class to the canonical form.
        StringPair[] values = (StringPair[])names.toArray(new StringPair[names.size()]);

        Set<String> uriset = new HashSet<String>();
        for( int i=0; i<values.length; i++ ) {
            uriset.add( values[i].namespaceURI );
        }
        
        NameClass r = null;
        String[] uris = (String[])uriset.toArray(new String[uriset.size()]);
        for( int i=0; i<uris.length; i++ ) {
            if( uris[i]==MAGIC )    continue;
            
            NameClass tmp = null;
            
            for( int j=0; j<values.length; j++ ) {
                if( !values[j].namespaceURI.equals(uris[i]) ) continue;
                if( values[j].localName==MAGIC ) continue;
                
                if( src.accepts(values[j])!=src.accepts(uris[i],MAGIC) ) {
                    if(tmp==null)    tmp = new SimpleNameClass(values[j]);
                    else            tmp = new ChoiceNameClass( tmp, new SimpleNameClass(values[j]) );
                }
            }
            
            if( src.accepts(uris[i],MAGIC)!=src.accepts(MAGIC,MAGIC) ) {
                if(tmp==null)
                    tmp = new NamespaceNameClass(uris[i]);
                else
                    tmp = new DifferenceNameClass( new NamespaceNameClass(uris[i]), tmp );
            }
            
            if(r==null)        r = tmp;
            else            r = new ChoiceNameClass(r,tmp);
        }
        
        if( src.accepts(MAGIC,MAGIC) ) {
            if( r==null )
                r = NameClass.ALL;
            else
                r = new DifferenceNameClass( NameClass.ALL, r );
        } else {
            if(r==null) {
                // this name class accepts nothing.
                // by adding notAllowed to the content model, this element
                // will match nothing.
                writer.element("anyName");
                writer.element("notAllowed");
                return;
            }
        }
        
        r.visit(nameClassWriter);
        
    }
    
    protected NameClassVisitor nameClassWriter;
    protected NameClassVisitor createNameClassWriter() {
        return new NameClassWriter(this);
    }
    
    protected SmartPatternWriter patternWriter = new SmartPatternWriter(this);
    
    /**
     * PatternWriter that performs some optimization for human eyes.
     */
    class SmartPatternWriter extends PatternWriter {
    
        SmartPatternWriter( Context context ) { super(context); }
        
        public void onOther( OtherExp exp ) {
            exp.exp.visit(this);    // ignore otherexp
        }
        public void onRef( ReferenceExp exp ) {
            String uniqueName = (String)exp2name.get(exp);
            if( uniqueName!=null )
                this.writer.element("ref", new String[]{"name",uniqueName});
            else
                // this expression will not be written as a named pattern.
                exp.exp.visit(this);
        }
    
        public void onElement( ElementExp exp ) {
            String uniqueName = (String)exp2name.get(exp);
            if( uniqueName!=null ) {
                // this element will be written as a named pattern
                this.writer.element("ref", new String[]{"name",uniqueName} );
                return;
            } else
                writeElement(exp);
        }
    
        public void onAttribute( AttributeExp exp ) {
            if( exp.nameClass instanceof SimpleNameClass
            &&  ((SimpleNameClass)exp.nameClass).namespaceURI.equals("") ) {
                // we can use name attribute.
                this.writer.start("attribute", new String[]{"name",
                    ((SimpleNameClass)exp.nameClass).localName} );
            }
            else {
                this.writer.start("attribute");
                context.writeNameClass(exp.nameClass);
            }
            if( exp.exp != Expression.anyString )
                // we can omit <anyString/> in the attribute.
                visitUnary(exp.exp);
            this.writer.end("attribute");
        }

        protected void writeElement( ElementExp exp ) {
            NameClass nc = exp.getNameClass();
            if( nc instanceof SimpleNameClass
            &&  ((SimpleNameClass)nc).namespaceURI.equals(defaultNs) )
                // we can use name attribute to simplify output.
                this.writer.start("element",new String[]{"name",
                    ((SimpleNameClass)nc).localName} );
            else {
                this.writer.start("element");
                writeNameClass(exp.getNameClass());
            }
            visitUnary(simplify(exp.contentModel));
            this.writer.end("element");
        }
    
        /**
         * remove unnecessary ReferenceExp from content model.
         * this will sometimes makes content model smaller.
         */
        public Expression simplify( Expression exp ) {
            return exp.visit( new ExpressionCloner(grammar.getPool()){
                public Expression onRef( ReferenceExp exp ) {
                    if( exp2name.containsKey(exp) )
                        // this ReferenceExp will be written as a named pattern.
                        return exp;
                    else
                        // bind contents
                        return exp.exp.visit(this);
                }
                public Expression onOther( OtherExp exp ) {
                    return exp.exp.visit(this);
                }
                public Expression onElement( ElementExp exp ) {
                    return exp;
                }
                public Expression onAttribute( AttributeExp exp ) {
                    return exp;
                }
            });
        }
    };
    
    
    
    
    
}
