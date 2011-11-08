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
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;attribute&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends ExpressionState implements FacetStateParent
{
    protected XSTypeIncubator incubator;
    
    public XSTypeIncubator getIncubator() { return incubator; }
    
    protected void startSelf() {
        super.startSelf();
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        
        String type        = startTag.getAttribute("type");
        if(type==null)    type="string";
        incubator = reader.resolveXSDatatype(type).createIncubator();
    }
    
    protected Expression makeExpression() {
        try    {
            final String name        = startTag.getAttribute("name");
            final String required    = startTag.getAttribute("required");
            
            if( name==null ) {
                reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "attribute","name" );
                // recover by ignoring this attribute.
                // since attributes are combined by sequence, so epsilon is appropriate.
                return Expression.epsilon;
            }
            
            Expression exp = reader.pool.createAttribute(
                new SimpleNameClass("",name),
                incubator.derive(null,null), null );
            
            // unless required attribute is specified, it is considered optional
            if(! "true".equals(required) )
                exp = reader.pool.createOptional(exp);
            
            return exp;
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, RELAXCoreReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.anyString;
        }
    }
    
    protected State createChildState( StartTagInfo tag ) {
        return ((RELAXCoreReader)reader).createFacetState(this,tag);    // facets
    }
}
