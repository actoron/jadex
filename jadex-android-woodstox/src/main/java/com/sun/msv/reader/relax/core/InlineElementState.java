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

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.relax.TagClause;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;element&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InlineElementState extends ExpressionState implements FacetStateParent {
    
    /** this field is set to null if this element has label attribute. */
    protected XSTypeIncubator incubator;
    
    public XSTypeIncubator getIncubator() { return incubator; }
    
    protected State createChildState( StartTagInfo tag ) {
        if( incubator!=null )
            return ((RELAXCoreReader)reader).createFacetState(this,tag);    // facets
        else
            return null;    // nothing is allowed when @label is used.
    }
    
    protected void startSelf() {
        super.startSelf();
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        String type        = startTag.getAttribute("type");
        String label    = startTag.getAttribute("label");
        
        if( type!=null && label!=null )
            reader.reportError( RELAXCoreReader.ERR_CONFLICTING_ATTRIBUTES, "type", "label" );
            // recover by ignoring one attribute.
        
        if( type==null && label==null ) {
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE_2, "element", "type", "label" );
            type="string";
        }
        
        if( label!=null ) {
            incubator = null;
        } else {
            incubator = reader.resolveXSDatatype(type).createIncubator();
        }
    }
    
    protected Expression makeExpression() {
        try {
            final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
            final String name        = startTag.getAttribute("name");
            
            if( name==null ) {
                reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "element","name" );
                // recover by ignoring this element.
                return Expression.nullSet;
            }
            
            Expression contentModel;

            if( incubator!=null ) {
                contentModel = incubator.derive(null,null);
            } else {
                // @label is used
                String label = startTag.getAttribute("label");
                if(label==null)    throw new Error();
                
                contentModel = reader.module.hedgeRules.getOrCreate(label);
                reader.backwardReference.memorizeLink(contentModel);
            }
            
            TagClause c = new TagClause();
            c.nameClass = new SimpleNameClass( ((RELAXCoreReader)reader).module.targetNamespace, name );
            
            final String role = startTag.getAttribute("role");
            if( role==null )    c.exp = Expression.epsilon;    // no attribute
            else {
                // role attribute
                AttPoolClause att = reader.module.attPools.getOrCreate(role);
                c.exp = att;
                reader.backwardReference.memorizeLink(att);
            }
            
            // create anonymous ElementRule. this rule will never be added to
            // RefContainer.
            return new ElementRule( reader.pool, c, contentModel );
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, RELAXCoreReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.nullSet;
        }
    }
}
