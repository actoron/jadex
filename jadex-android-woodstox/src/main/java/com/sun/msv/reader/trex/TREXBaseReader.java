/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javaxx.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.ChoiceState;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.InterleaveState;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.TerminalState;
import com.sun.msv.util.LightStack;
import com.sun.msv.util.StartTagInfo;

/**
 * reads TREX grammar from SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class TREXBaseReader extends GrammarReader {
    
    /** full constructor */
    public TREXBaseReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool,
        StateFactory stateFactory,
        State rootState ) {
        
        super(controller,parserFactory,pool,rootState);
        this.sfactory = stateFactory;
    }
    
    protected String localizeMessage( String propertyName, Object[] args ) {
        String format;
        
        try {
            format = ResourceBundle.getBundle("com.sun.msv.reader.trex.Messages").getString(propertyName);
        } catch( Exception e ) {
            format = ResourceBundle.getBundle("com.sun.msv.reader.Messages").getString(propertyName);
        }
        
        return MessageFormat.format(format, args );
    }
    
    /** grammar object currently being loaded. */
    protected TREXGrammar grammar;
    /** obtains parsed grammar object only if parsing was successful. */
    public final TREXGrammar getResult() {
        if(controller.hadError())    return null;
        else                        return grammar;
    }
    public Grammar getResultAsGrammar() {
        return getResult();
    }
    
    /** stack that stores value of ancestor 'ns' attribute. */
    private LightStack nsStack = new LightStack();
    /** target namespace: currently active 'ns' attribute */
    protected String targetNamespace ="";
    public final String getTargetNamespace() { return targetNamespace; }
    
    /**
     * creates various State object, which in turn parses grammar.
     * parsing behavior can be customized by implementing custom StateFactory.
     */
    public static abstract class StateFactory {
        public State nsName        ( State parent, StartTagInfo tag ) { return new NameClassNameState(); }
        public State nsAnyName    ( State parent, StartTagInfo tag ) { return new NameClassAnyNameState(); }
        public State nsNsName    ( State parent, StartTagInfo tag ) { return new NameClassNsNameState(); }
        public State nsNot        ( State parent, StartTagInfo tag ) { return new NameClassNotState(); }
        public State nsDifference( State parent, StartTagInfo tag ) { return new NameClassDifferenceState(); }
        public State nsChoice    ( State parent, StartTagInfo tag ) { return new NameClassChoiceState(); }

        public State element    ( State parent, StartTagInfo tag ) { return new ElementState(); }
        public State attribute    ( State parent, StartTagInfo tag ) { return new AttributeState(); }
        public State group        ( State parent, StartTagInfo tag ) { return new SequenceState(); }
        public State interleave    ( State parent, StartTagInfo tag ) { return new InterleaveState(); }
        public State choice        ( State parent, StartTagInfo tag ) { return new ChoiceState(); }
        public State optional    ( State parent, StartTagInfo tag ) { return new OptionalState(); }
        public State zeroOrMore    ( State parent, StartTagInfo tag ) { return new ZeroOrMoreState(); }
        public State oneOrMore    ( State parent, StartTagInfo tag ) { return new OneOrMoreState(); }
        public State mixed        ( State parent, StartTagInfo tag ) { return new MixedState(); }
        public State empty        ( State parent, StartTagInfo tag ) { return new TerminalState(Expression.epsilon); }
        public State notAllowed    ( State parent, StartTagInfo tag ) { return new TerminalState(Expression.nullSet); }
        public State includeGrammar( State parent, StartTagInfo tag ) { return new IncludeMergeState(); }
        public State grammar    ( State parent, StartTagInfo tag ) { return new GrammarState(); }
        public State start        ( State parent, StartTagInfo tag ) { return new StartState(); }
        public abstract State define( State parent, StartTagInfo tag );
        public State ref        ( State parent, StartTagInfo tag ) {
            return new RefState("true".equals(tag.getAttribute("parent")));
        }
        // <div>s in the <grammar> element is not available by default.
        public State divInGrammar( State parent, StartTagInfo tag ) { return null; }
        
        public TREXGrammar createGrammar( ExpressionPool pool, TREXGrammar parent ) {
            return new TREXGrammar(pool,parent);
        }

        public State includedGrammar() {
            return new RootMergedGrammarState();
        }
    }
    public final StateFactory sfactory;
    
    protected State createNameClassChildState( State parent, StartTagInfo tag )
    {
        if(tag.localName.equals("name"))        return sfactory.nsName(parent,tag);
        if(tag.localName.equals("anyName"))        return sfactory.nsAnyName(parent,tag);
        if(tag.localName.equals("nsName"))        return sfactory.nsNsName(parent,tag);
        if(tag.localName.equals("not"))            return sfactory.nsNot(parent,tag);
        if(tag.localName.equals("difference"))    return sfactory.nsDifference(parent,tag);
        if(tag.localName.equals("choice"))        return sfactory.nsChoice(parent,tag);
        
        return null;        // unknown element. let the default error be thrown.
    }
    
    public State createExpressionChildState( State parent, StartTagInfo tag )
    {
        if(tag.localName.equals("element"))        return sfactory.element(parent,tag);
        if(tag.localName.equals("attribute"))    return sfactory.attribute(parent,tag);
        if(tag.localName.equals("group"))        return sfactory.group(parent,tag);
        if(tag.localName.equals("interleave"))    return sfactory.interleave(parent,tag);
        if(tag.localName.equals("choice"))        return sfactory.choice(parent,tag);
//        if(tag.localName.equals("concur"))        return sfactory.concur(parent,tag);
        if(tag.localName.equals("optional"))    return sfactory.optional(parent,tag);
        if(tag.localName.equals("zeroOrMore"))    return sfactory.zeroOrMore(parent,tag);
        if(tag.localName.equals("oneOrMore"))    return sfactory.oneOrMore(parent,tag);
        if(tag.localName.equals("mixed"))        return sfactory.mixed(parent,tag);
        if(tag.localName.equals("ref"))            return sfactory.ref(parent,tag);
        if(tag.localName.equals("empty"))        return sfactory.empty(parent,tag);
//        if(tag.localName.equals("anyString"))    return sfactory.anyString(parent,tag);
//        if(tag.localName.equals("string"))        return sfactory.string(parent,tag);
//        if(tag.localName.equals("data"))        return sfactory.data(parent,tag);
        if(tag.localName.equals("notAllowed"))    return sfactory.notAllowed(parent,tag);
        if(tag.localName.equals("grammar"))        return sfactory.grammar(parent,tag);

        return null;        // unknown element. let the default error be thrown.
    }
    
    /**
     * performs final wrap-up.
     * This method is called from the RootState object, after the parsing is completed.
     * 
     * <p>
     * This method has to be called after the run-away expression check is done.
     */
    public void wrapUp() {}



    
    
// SAX event interception
//--------------------------------
    public void startElement( String a, String b, String c, Attributes d ) throws SAXException
    {
        // handle 'ns' attribute propagation
        nsStack.push(targetNamespace);
        if( d.getIndex("ns")!=-1 )
            targetNamespace = d.getValue("ns");
        // if nothing specified, targetNamespace stays the same.
        // for root state, startTag is null.
        
        super.startElement(a,b,c,d);
    }
    public void endElement( String a, String b, String c ) throws SAXException
    {
        super.endElement(a,b,c);
        targetNamespace = (String)nsStack.pop();
    }


    // error messages
    
    public static final String ERR_MISSING_CHILD_NAMECLASS = // arg:0
        "TREXGrammarReader.MissingChildNameClass";
    public static final String ERR_MORE_THAN_ONE_NAMECLASS = // arg:0
        "TREXGrammarReader.MoreThanOneNameClass";
    public static final String ERR_UNDECLARED_PREFIX = // arg:1
        "TREXGrammarReader.UndeclaredPrefix";
    public static final String ERR_UNDEFINED_PATTERN = // arg:1
        "TREXGrammarReader.UndefinedPattern";
    public static final String ERR_UNKNOWN_DATATYPE_VOCABULARY = // arg:1
        "TREXGrammarReader.UnknownDataTypeVocabulary";
    public static final String ERR_BAD_COMBINE = // arg:1
        "TREXGrammarReader.BadCombine";
    public static final String ERR_COMBINE_MISSING = // arg:1
        "TREXGrammarReader.CombineMissing";
    public static final String WRN_COMBINE_IGNORED =
        "TREXGrammarReader.Warning.CombineIgnored";
    public static final String WRN_OBSOLETED_XMLSCHEMA_NAMSPACE =
        "TREXGrammarReader.Warning.ObsoletedXMLSchemaNamespace";
    public static final String ERR_DUPLICATE_DEFINITION =
        "TREXGrammarReader.DuplicateDefinition";
    public static final String ERR_NONEXISTENT_PARENT_GRAMMAR =
        "TREXGrammarReader.NonExistentParentGrammar";
    public static final String ERR_INTERLEAVED_STRING =
        "TREXGrammarReader.InterleavedString";
    public static final String ERR_SEQUENCED_STRING =
        "TREXGrammarReader.SequencedString";
    public static final String ERR_REPEATED_STRING =
        "TREXGrammarReader.RepeatedString";
    public static final String ERR_INTERLEAVED_ANYSTRING =
        "TREXGrammarReader.InterleavedAnyString";
        
}
