/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core.checker;

import java.util.Iterator;
import java.util.Set;

import org.relaxng.datatype.Datatype;

import com.sun.msv.datatype.xsd.IDREFType;
import com.sun.msv.datatype.xsd.IDType;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.ElementRules;
import com.sun.msv.grammar.relax.HedgeRules;
import com.sun.msv.grammar.relax.RELAXExpressionVisitorVoid;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.grammar.relax.TagClause;
import com.sun.msv.reader.relax.core.RELAXCoreReader;

/**
 * makes sure that ID/IDREF are not abused.
 * 
 * RELAX has the following constraint over the use of ID/IDREF.
 * 
 * <p>
 * First, ID and IDREF can be only used as attribute values.
 * They cannot be used from type attribute of elementRules.
 * 
 * <p>
 * Second, if &lt;tag&gt; clause declares directly or indirectly
 * (by referencing attPool) one of its attribute as ID/IDREF type,
 * it must satisfies either (or both) of the following statement.
 * 
 * <ol>
 *  <li>no other tag clause shares the same tag name.
 *  <li>no other attribute of non-ID/IDREF types shares the same attribute name.
 * </ol>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IdAbuseChecker implements RELAXExpressionVisitorVoid {
    
    /** set of Strings: tag names that are used in this module */
    private final Set<String> tagNames = new java.util.HashSet<String>();
    /** set of Strings: tag names that are used more than once in this module */
    private final Set<String> overloadedNames = new java.util.HashSet<String>();
    /** set of Strings: names of non-ID/IDREF attributes */
    private final Set<String> nonIdAttrNames = new java.util.HashSet<String>();
    /** set of AttributeExps: that have ID/IDREF values */
    private final Set<AttributeExp> idAttributes = new java.util.HashSet<AttributeExp>();

    private final RELAXModule module;
    private final RELAXCoreReader reader;
    
    private String currentTagName;
    
    private IdAbuseChecker(RELAXCoreReader r,RELAXModule m) {
        this.reader = r;
        this.module = m;
    }

    public static void check( RELAXCoreReader reader, RELAXModule module ) {
        new IdAbuseChecker(reader,module).run();
    }
    
    private void run() {
        Iterator<?> itr;
        // extracts all tag names and
        // classify attribute names into
        //   (1) names that are used as ID/IDREF
        //   (2) names that are used as other datatypes.
        itr= module.tags.iterator();
        while( itr.hasNext() )
        {
            final TagClause tag = (TagClause)itr.next();
            if( tag.nameClass instanceof SimpleNameClass )
            {
                SimpleNameClass snc = (SimpleNameClass)tag.nameClass;
                if( tagNames.contains(snc.localName) )
                    overloadedNames.add(snc.localName);
                else
                    tagNames.add(snc.localName);
            }
        }
            
        // 1st filter: collect those AttributeExps which have overloaded tag names.
        itr= module.tags.iterator();
        while( itr.hasNext() ) {
            final TagClause tag = (TagClause)itr.next();
            if( tag.nameClass instanceof SimpleNameClass )
                currentTagName = ((SimpleNameClass)tag.nameClass).localName;
            else
                currentTagName = null;    // indicates wild card
            
            tag.exp.visit(this);
        }
        
        // make sure that filtered AttributeExp satisifies the second statement
        itr = idAttributes.iterator();
        while( itr.hasNext() ) {
            final AttributeExp atr = (AttributeExp)itr.next();
            
            if( atr.nameClass instanceof SimpleNameClass ) {
                final String name = ((SimpleNameClass)atr.nameClass).localName;
                if( nonIdAttrNames.contains(name) )
                    reader.reportError( RELAXCoreReader.ERR_ID_ABUSE_1, name );
            } else
                reader.reportError( RELAXCoreReader.ERR_ID_ABUSE );
        }
    }


    public void onAttribute( AttributeExp exp ) {
        if(!(exp.nameClass instanceof SimpleNameClass ))    return;
        
        Expression body = exp.exp.getExpandedExp(reader.pool);
        if(!(body instanceof DataExp ))                    return;
        
        SimpleNameClass snc = (SimpleNameClass)exp.nameClass;
        if(!snc.namespaceURI.equals(""))    return;
        
        Datatype dt = ((DataExp)body).dt;
        if( dt==IDType.theInstance || dt==IDREFType.theInstance ) {
            if( currentTagName==null
                // complex attribute name is used.
                // ID/IDREF must have an unique attribute name
            ||  overloadedNames.contains(currentTagName) )
                idAttributes.add(exp);    // possibility of abuse.
            
            // use of ID/IDREF in this way is OK.
        } else
            nonIdAttrNames.add(snc.localName);
    }
    public void onChoice( ChoiceExp exp )        { exp.exp1.visit(this);exp.exp2.visit(this); }
    public void onElement( ElementExp exp )        { throw new Error(); }
    public void onOneOrMore( OneOrMoreExp exp )    { exp.exp.visit(this); }
    public void onMixed( MixedExp exp )            { throw new Error(); }
    public void onRef( ReferenceExp exp )        { exp.exp.visit(this); }
    public void onOther( OtherExp exp )            { exp.exp.visit(this); }
    public void onEpsilon()                        {}
    public void onNullSet()                        {}
    public void onAnyString()                    {}
    public void onSequence( SequenceExp exp )    {}
    public void onData( DataExp exp )                { throw new Error(); }
    public void onValue( ValueExp exp )                { throw new Error(); }
    public void onAttPool( AttPoolClause exp )        { exp.exp.visit(this); }
    public void onTag( TagClause exp )                { throw new Error(); }
    public void onElementRules( ElementRules exp )    { throw new Error(); }
    public void onHedgeRules( HedgeRules exp )        { throw new Error(); }
    public void onInterleave( InterleaveExp exp )    { throw new Error(); }
    public void onConcur( ConcurExp exp )            { throw new Error(); }
    public void onList( ListExp exp )                { throw new Error(); }

}
