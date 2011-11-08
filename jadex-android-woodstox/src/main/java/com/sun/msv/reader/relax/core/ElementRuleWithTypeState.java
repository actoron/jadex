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
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;elementRule&gt; with 'type' attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRuleWithTypeState extends ElementRuleBaseState implements FacetStateParent
{
    protected XSTypeIncubator incubator;
    
    public XSTypeIncubator getIncubator()    { return incubator; }
    
    protected void startSelf() {
        super.startSelf();

        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        
        // existance of type attribute has already checked before
        // this state is created.
        incubator = reader.resolveXSDatatype(startTag.getAttribute("type"))
            .createIncubator();
    }
    
    protected Expression getContentModel() {
        try {
            return incubator.derive(null,null);
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, RELAXCoreReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.anyString;
        }
    }
    
    protected State createChildState( StartTagInfo tag ) {
        State next = getReader().createFacetState(this,tag);
        if(next!=null)        return next;            // facets
        
        return super.createChildState(tag);            // or delegate to the base class
    }
}
