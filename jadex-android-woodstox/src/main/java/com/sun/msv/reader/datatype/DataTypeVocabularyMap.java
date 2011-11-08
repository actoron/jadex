/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype;

import java.util.Map;

/**
 * a map from namespace URI to DataTypeVocabulary
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class DataTypeVocabularyMap implements java.io.Serializable {
    
    /** map from namespace URI to DataTypeVocabulary */
    private final Map<String,DataTypeVocabulary> impl = new java.util.HashMap<String,DataTypeVocabulary>();
    
    /**
     * obtains an DataTypeVocabulary associated to the namespace.
     * 
     * If necessary, Vocabulary is located and instanciated.
     */
    public DataTypeVocabulary get( String namespaceURI ) {
        
        DataTypeVocabulary v = (DataTypeVocabulary)impl.get(namespaceURI);
        if(v!=null) return v;
        
        // TODO: generic way to load a vocabulary
        if( namespaceURI.equals( com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace ) ) {
            v = new com.sun.msv.reader.datatype.xsd.XSDVocabulary();
            impl.put( com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace, v );
            impl.put( com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace2, v );
        }
        return v;
    }
    
    /** manually adds DataTypeVocabulary into this map. */
    public void put( String namespaceURI, DataTypeVocabulary voc ) {
        impl.put( namespaceURI, voc );
    }
}
