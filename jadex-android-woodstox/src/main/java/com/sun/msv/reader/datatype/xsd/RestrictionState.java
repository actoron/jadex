/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * state that parses &lt;restriction&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RestrictionState extends TypeWithOneChildState implements FacetStateParent {
    
    protected final String newTypeUri;
    protected final String newTypeName;
    
    protected RestrictionState( String newTypeUri, String newTypeName ) {
        this.newTypeUri  = newTypeUri;
        this.newTypeName = newTypeName;
    }
    
    protected XSTypeIncubator incubator;
    public final XSTypeIncubator getIncubator() {
        return incubator;
    }

    protected XSDatatypeExp annealType( XSDatatypeExp baseType ) throws DatatypeException {
        return incubator.derive(newTypeUri,newTypeName);
    }
    
    public void onEndChild( XSDatatypeExp child ) {
        super.onEndChild(child);
        createTypeIncubator();
    }
    
    private void createTypeIncubator() {
        incubator = type.createIncubator();
    }

    
    protected void startSelf() {
        super.startSelf();
        
        // if the base attribute is used, try to load it.
        String base = startTag.getAttribute("base");
        if(base!=null)
            onEndChild( ((XSDatatypeResolver)reader).resolveXSDatatype(base) );
    }

    protected State createChildState( StartTagInfo tag ) {
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("simpleType") )    return new SimpleTypeState();
        if( FacetState.facetNames.contains(tag.localName) ) {
            if( incubator==null ) {
                reader.reportError( GrammarReader.ERR_MISSING_ATTRIBUTE, "restriction", "base" );
                onEndChild(new XSDatatypeExp(StringType.theInstance,reader.pool));
            }
            return new FacetState();
        }
        
        return null;    // unrecognized
    }
}
