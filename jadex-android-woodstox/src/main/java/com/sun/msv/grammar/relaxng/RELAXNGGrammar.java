/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relaxng;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.trex.TREXGrammar;

/**
 * Grammar for RELAX NG (with DTD compatibility annotation).
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGGrammar extends TREXGrammar {

    public RELAXNGGrammar( ExpressionPool pool, TREXGrammar parentGrammar ) {
        super(pool,parentGrammar);
    }
    public RELAXNGGrammar( ExpressionPool pool )    { super(pool); }
    public RELAXNGGrammar() { super(); }

    /**
     * the compatibility property of the ID/IDREF feature.
     * 
     * This flag is set to true if this schema is compatible in terms of
     * ID/IDREF, as defined in the spec.
     */
    public boolean isIDcompatible = true;
    
    /**
     * the compatibility property of the default attribute feature.
     * 
     * This flag is set to true if this schema is compatible in terms of
     * the default attribute value feature, as defined in the spec.
     * 
     * <p>
     * Note that the currently we don't support infoset augmentation.
     */
    public boolean isDefaultAttributeValueCompatible = true;
    
    /**
     * the compatibility property of the annotation feature.
     * 
     * This flag is set to true if this schema is compatible in terms of
     * the annotation feature, as defined in the spec.
     */
    public boolean isAnnotationCompatible = true;
    
    
    
/*
    /**
     * add an entry to the default value table.
     * 
     * @return
     *        <b>false</b> if the default attribute value for this element and attribute
     *        was already specified. <b>true</b> if this is the first time.
     */
/*    public final boolean addDefaultValue(
            String elementNamespaceURI, String elementLocalName,
            String attributeNamespaceURI, String attributeLocalName,
            String value ) {
        
        if(defaultValues==null)
            defaultValues = new HashMap();
        
        return defaultValues.put(
            new ElemAttrNamePair(elementNamespaceURI,elementLocalName,attributeNamespaceURI,attributeLocalName),
            value )==null;
    }
    
    /**
     * checks if this grammar has any attribute default value.
     */
/*    public final boolean hasDefaultValue() {
        return defaultValues!=null && defaultValues.size()!=0;
    }
    
    /**
     * gets the list of defaultable attributes of the given element.
     * 
     * @param (namespaceURI,localName)
     *        this pair designates the name of the element.
     * 
     * @return
     *        if there is no defaultable attribute, it returns empty array.
     *        this method never returns null.
     */
/*    public final String[] getDefaultableAttributes( String namespaceURI, String localName ) {
        
    }
    
    // TODO: this can be implemented much efficiently, I guess.
    
    // map from NamePair (element name) to
    //        (a map from NamePair (attribute name) to attribute value)
    private Map defaultValues;
*/
/*    
    public static final int KEYTYPE_NONE    =0;
    public static final int KEYTYPE_ID        =1;
    public static final int KEYTYPE_IDREF    =2;
    
    public final int getAttributeKeyType(
            String elementNamespaceURI, String elementLocalName,
            String attributeNamespaceURI, String attributeLocalName,
            String value ) {
        
    }
    
*/
        
    // serialization support
    private static final long serialVersionUID = 1;    
}
