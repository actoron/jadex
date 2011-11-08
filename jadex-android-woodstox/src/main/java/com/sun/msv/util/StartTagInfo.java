/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util;

import org.xml.sax.Attributes;

import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.grammar.util.IDContextProviderWrapper;

/**
 * immutable start tag information
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("deprecation")
public class StartTagInfo {
    
    public String        namespaceURI;
    public String        localName;
    public String        qName;
    public Attributes    attributes;
    /**
     * object that provides additional information which is necessary
     * for validating some datatypes
     */
    public IDContextProvider2 context;
    
    protected StartTagInfo() {}
    
    /** @deprecated */
    public StartTagInfo(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider context ) {
        reinit(namespaceURI,localName,qName,attributes,context);
    }
    
    public StartTagInfo(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider2 context ) {
        reinit(namespaceURI,localName,qName,attributes,context);
    }

    public StartTagInfo(
        String namespaceURI, String localName, String qName, Attributes attributes ) {
        reinit(namespaceURI,localName,qName,attributes,(IDContextProvider2)null);
    }

    /** @deprecated */
    public void reinit(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider context ) {
            
        reinit( namespaceURI, localName, qName, attributes, IDContextProviderWrapper.create(context) );
    }
    
    /** re-initialize the object with brand new parameters. */
    public void reinit(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider2 context ) {
        this.namespaceURI    = namespaceURI;
        this.localName        = localName;
        this.qName            = qName;
        this.attributes        = attributes;
        this.context        = context;
    }
    
    public final boolean containsAttribute( String attrName ) {
        return containsAttribute("",attrName);
    }
    
    public final boolean containsAttribute( String namespaceURI, String attrName ) {
        return attributes.getIndex(namespaceURI,attrName)!=-1;
    }
    
    /**
     * gets value of the specified attribute.
     * 
     * @return null        attribute does not exist.
     */
    public final String getAttribute( String attrName ) {
        return getAttribute("",attrName);
    }
    
    public final String getAttribute( String namespaceURI, String attrName ) {
        return attributes.getValue(namespaceURI,attrName);
    }
    
    public final String getCollapsedAttribute( String attrName ) {
        String s = getAttribute(attrName);
        if(s==null)        return null;
        return WhiteSpaceProcessor.collapse(s);
    }
    
    public final String getDefaultedAttribute( String attrName, String defaultValue ) {
        return getDefaultedAttribute("",attrName,defaultValue);
    }
    
    public final String getDefaultedAttribute( String namespaceURI, String attrName, String defaultValue ) {
        String v = getAttribute(namespaceURI,attrName);
        if(v!=null)        return v;
        else            return defaultValue;
    }
}
