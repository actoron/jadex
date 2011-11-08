/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng.comp;

import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;

import javaxx.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.relaxng.RELAXNGGrammar;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.util.LightStack;
import com.sun.msv.util.StartTagInfo;

/**
 * reads RELAX NG grammar with DTD compatibility annotation
 * and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGCompReader extends RELAXNGReader {
    
    /** loads RELAX NG pattern.
     * 
     * @return
     *        it always returns {@link RELAXNGGrammar}, but due to the 
     *        restriction imposed by Java language, I cannot change the
     *        signature of this method.
     */
    public static TREXGrammar parse( String grammarURL, GrammarReaderController controller )
    {
        RELAXNGCompReader reader = new RELAXNGCompReader(controller);
        reader.parse(grammarURL);
        
        return reader.getResult();
    }
    
    /** loads RELAX NG pattern.
     * 
     * @return
     *        it always returns {@link RELAXNGGrammar}, but due to the 
     *        restriction imposed by Java language, I cannot change the
     *        signature of this method.
     */
    public static TREXGrammar parse( InputSource grammar, GrammarReaderController controller )
    {
        RELAXNGCompReader reader = new RELAXNGCompReader(controller);
        reader.parse(grammar);
        
        return reader.getResult();
    }

    /** easy-to-use constructor. */
    public RELAXNGCompReader( GrammarReaderController controller ) {
        this(controller,createParserFactory(),new ExpressionPool());
    }
    
    /** easy-to-use constructor. */
    public RELAXNGCompReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool ) {
        this(controller,parserFactory,new StateFactory(),pool);
    }
    
    /** full constructor */
    public RELAXNGCompReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        StateFactory stateFactory,
        ExpressionPool pool ) {
        
        super( controller, parserFactory, stateFactory, pool );
        
        lastRNGElement.push(null);
    }

    private final Map<AttributeExp,String> defaultedAttributes = new java.util.HashMap<AttributeExp,String>();
    /**
     * note that the specified expression is marked with the default value.
     * this method is called when a:defaultValue is found.
     */
    protected final void addDefaultValue( AttributeExp exp, String value ) {
        // record the location where this attribute is declared.
        setDeclaredLocationOf(exp);
        
        if(defaultedAttributes.put(exp,value)!=null)
            throw new Error();    // it is not possible for one AttributeExp to be processed twice.
    }
    
    protected TREXGrammar getGrammar() {
        return grammar;
    }
    
    protected String localizeMessage( String propertyName, Object[] args ) {
        String format;
        
        try {
            format = ResourceBundle.getBundle("com.sun.msv.reader.trex.ng.comp.Messages").getString(propertyName);
        } catch( Exception e ) {
            return super.localizeMessage(propertyName,args);
        }
        
        return MessageFormat.format(format, args );
    }
    
    
    
    /** Namespace URI of RELAX NG DTD compatibility annotation */
    public static final String AnnotationNamespace =
        "http://relaxng.org/ns/compatibility/annotations/1.0";

    /**
     * creates various State object, which in turn parses grammar.
     * parsing behavior can be customized by implementing custom StateFactory.
     */
    public static class StateFactory extends RELAXNGReader.StateFactory {
        public State attribute    ( State parent, StartTagInfo tag ) { return new CompAttributeState(); }
        public TREXGrammar createGrammar( ExpressionPool pool, TREXGrammar parent ) {
            return new RELAXNGGrammar(pool,parent);
        }
    }
//    protected StateFactory getStateFactory() {
//        return (StateFactory)super.sfactory;
//    }
    
    
    public void wrapUp() {
        super.wrapUp();
        
        if(!controller.hadError()) {
            // do not check the compatibilities if some errors
            // are already reported.
            
            new DefAttCompatibilityChecker(this,defaultedAttributes).test();
            new IDCompatibilityChecker(this).test();
        }
    }
    
    /**
     * pair of an element name and an attribute name.
     */
/*    private final static class ElemAttrNamePair {
        public final StringPair element;
        public final StringPair attribute;
        
        public int hashCode() {
            return element.hashCode()^attribute.hashCode();
        }
        public boolean equals( Object o ) {
            if(!(o instanceof ElemAttrNamePair))    return false;
            ElemAttrNamePair rhs = (ElemAttrNamePair)o;
            
            return element.equals(rhs.element) && attribute.equals(rhs.attribute);
        }
        
        public ElemAttrNamePair( StringPair e, StringPair a ) {
            element=e; attribute=a;
        }
        public ElemAttrNamePair( String e_uri, String e_local, String a_uri, String a_local ) {
            this( new StringPair(e_uri,e_local), new StringPair(a_uri,a_local) );
        }
        public ElemAttrNamePair( SimpleNameClass e, SimpleNameClass a ) {
            this( e.namespaceURI, e.localName, a.namespaceURI, a.localName );
        }
    }
*/    
    
    

    /**
     * The local name of the preceding RELAX NG element sibling.
     */
    private final LightStack lastRNGElement = new LightStack();
    
    private boolean inAnnotation = false;
    
    public void startElement( String uri, String local, String qname, Attributes atts ) throws SAXException {
        super.startElement(uri,local,qname,atts);
        
        if(inAnnotation) {
            // we found a child element for a:annotation.
            // this is not OK.
            reportWarning( CERR_ANN_CHILD_ELEMENT, null, new Locator[]{getLocator()} );
            ((RELAXNGGrammar)grammar).isAnnotationCompatible = false;
        }
        
        if(uri.equals(AnnotationNamespace) && local.equals("annotation")) {
            // check the compatibility with the annotation feature.
            
            for( int i=0; i<atts.getLength(); i++ ) {
                String attUri = atts.getURI(i);
                if(attUri.equals("")
                || attUri.equals(AnnotationNamespace)
                || attUri.equals(RELAXNGNamespace) ) {
                    // it contains an invalid attribute
                    reportWarning( CERR_ANN_INVALID_ATTRIBUTE, new Object[]{atts.getQName(i)}, new Locator[]{getLocator()} );
                    ((RELAXNGGrammar)grammar).isAnnotationCompatible = false;
                    break;    // abort further check.
                }
            }
            
            if(lastRNGElement.size()!=0 ) {
                if(lastRNGElement.top()!=null
                && !"value".equals(lastRNGElement.top())
                && !"param".equals(lastRNGElement.top())
                && !"name".equals(lastRNGElement.top())) {
                    reportWarning( CERR_ANN_MISPLACED, new Object[]{lastRNGElement.top()}, new Locator[]{getLocator()} );
                    ((RELAXNGGrammar)grammar).isAnnotationCompatible = false;
                }
            }
        
            inAnnotation = true;    
        }
        
        lastRNGElement.push(null);
    }
    public void endElement( String uri, String local, String qname ) throws SAXException {
        super.endElement(uri,local,qname);
        
        inAnnotation = false;
        
        lastRNGElement.pop();
        if( uri.equals(RELAXNGNamespace) ) {
            lastRNGElement.pop();
            lastRNGElement.push(local);
        }
    }
    
    
        
    
    public static final String CERR_ANN_CHILD_ELEMENT = // arg:0
        "RELAXNGReader.Compatibility.Annotation.ChildElement";
    public static final String CERR_ANN_MISPLACED = // arg:1
        "RELAXNGReader.Compatibility.Annotation.Misplaced";
    public static final String CERR_ANN_INVALID_ATTRIBUTE = // arg:1
        "RELAXNGReader.Compatibility.Annotation.InvalidAttribute";

}
