/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javaxx.xml.parsers.SAXParserFactory;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.ChoiceState;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.TerminalState;
import com.sun.msv.reader.datatype.xsd.FacetState;
import com.sun.msv.reader.relax.core.InlineElementState;
import com.sun.msv.util.StartTagInfo;

/**
 * reads RELAX grammar/module by SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class RELAXReader extends GrammarReader
{
    public RELAXReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        StateFactory stateFactory,
        ExpressionPool pool,
        State initialState )
    {
        super(controller,parserFactory,pool,initialState);
        this.sfactory = stateFactory;
    }
    
    /** Namespace URI of RELAX Core */
    public static final String RELAXCoreNamespace = "http://www.xml.gr.jp/xmlns/relaxCore";

    /**
     * creates various State object, which in turn parses grammar.
     * parsing behavior can be customized by implementing custom StateFactory.
     */
    public static class StateFactory {
        protected State refLabel(State parent,StartTagInfo tag)    { return new ElementRefState(); }
        protected State hedgeRef(State parent,StartTagInfo tag)    { return new HedgeRefState(); }
        protected State choice(State parent,StartTagInfo tag)    { return new ChoiceState(); }
        protected State none(State parent,StartTagInfo tag)        { return new TerminalState(Expression.nullSet); }
        protected State empty(State parent,StartTagInfo tag)    { return new TerminalState(Expression.epsilon); }
        protected State sequence(State parent,StartTagInfo tag)    { return new SequenceState(); }
        
        protected FacetState facets(State parent,StartTagInfo tag)    { return new FacetState(); }
    }
    
    public final StateFactory sfactory;
    
    public State createExpressionChildState( State parent, StartTagInfo tag )
    {
        if(tag.localName.equals("ref"))                return sfactory.refLabel(parent,tag);
        if(tag.localName.equals("hedgeRef"))        return sfactory.hedgeRef(parent,tag);
        if(tag.localName.equals("choice"))            return sfactory.choice(parent,tag);
        if(tag.localName.equals("none"))            return sfactory.none(parent,tag);
        if(tag.localName.equals("empty"))            return sfactory.empty(parent,tag);
        if(tag.localName.equals("sequence"))        return sfactory.sequence(parent,tag);
        return null;        // unknown element. let the default error be thrown.
    }
    
    public FacetState createFacetState( State parent, StartTagInfo tag )
    {
        if(! RELAXCoreNamespace.equals(tag.namespaceURI) )    return null;
        
        if( FacetState.facetNames.contains(tag.localName) )    return sfactory.facets(parent,tag);
        else    return null;
    }

    /** returns true if the given state can have "occurs" attribute. */
    protected boolean canHaveOccurs( State state )
    {
        return
            state instanceof SequenceState
        ||    state instanceof ElementRefState
        ||    state instanceof HedgeRefState
        ||    state instanceof ChoiceState
        ||      state instanceof InlineElementState;
    }

    protected Expression interceptExpression( State state, Expression exp )
    {
        // handle occurs attribute here.
        final String occurs= state.getStartTag().getAttribute("occurs");
        
        if( canHaveOccurs(state) )
        {// these are the repeatable expressions
            if( occurs!=null )
            {
                if( occurs.equals("?") )    exp = pool.createOptional(exp);
                else
                if( occurs.equals("+") )    exp = pool.createOneOrMore(exp);
                else
                if( occurs.equals("*") )    exp = pool.createZeroOrMore(exp);
                else
                    reportError( ERR_ILLEGAL_OCCURS, occurs );
                    // recover from error by ignoring this occurs attribute
            }
        }
        else
        {
            if( occurs!=null )
                reportError( ERR_MISPLACED_OCCURS, state.getStartTag().localName );
        }
        return exp;
    }
    
    /**
     * obtains an Expression specified by given (namespace,label) pair.
     * this method is called to parse &lt;ref label="..." /&gt; element.
     */
    protected abstract Expression resolveElementRef( String namespace, String label );
    /**
     * obtains an Expression specified by given (namespace,label) pair.
     * this method is called to parse &lt;hedgeRef label="..." /&gt; element.
     */
    protected abstract Expression resolveHedgeRef( String namespace, String label );
    
    
// error related service
//=============================================

    protected String localizeMessage( String propertyName, Object[] args ) {
        String format;
        
        try {
            format = ResourceBundle.getBundle("com.sun.msv.reader.relax.Messages").getString(propertyName);
        } catch( Exception e ) {
            format = ResourceBundle.getBundle("com.sun.msv.reader.Messages").getString(propertyName);
        }
        
        return MessageFormat.format(format, args );
    }
    

    
    protected ExpressionPool getPool()    { return super.pool; }

    // error message
    public static final String ERR_ILLEGAL_OCCURS    // arg:1
        = "RELAXReader.IllegalOccurs";
    public static final String ERR_MISPLACED_OCCURS    // arg:1
        = "RELAXReader.MisplacedOccurs";
}
