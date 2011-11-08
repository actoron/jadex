/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.helpers.DatatypeLibraryLoader;

import com.sun.msv.grammar.relaxng.datatype.BuiltinDatatypeLibrary;
import com.sun.msv.grammar.relaxng.datatype.CompatibilityDatatypeLibrary;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;

/**
 * Default implementation of Datatype
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class DefaultDatatypeLibraryFactory implements DatatypeLibraryFactory {

    private final DatatypeLibraryFactory loader = new DatatypeLibraryLoader();
    
    private DatatypeLibrary xsdlib;
    
    private DatatypeLibrary compatibilityLib;

    /**
     * @see org.relaxng.datatype.DatatypeLibraryFactory#createDatatypeLibrary(java.lang.String)
     */
    public DatatypeLibrary createDatatypeLibrary(String namespaceURI) {
        
        DatatypeLibrary lib = loader.createDatatypeLibrary(namespaceURI);
        if(lib!=null)       return lib;
        
        // if failed to dynamically locate the library, use static ones.
        
        if( namespaceURI.equals("") )
            return BuiltinDatatypeLibrary.theInstance;
        
        // We have the built-in support for XML Schema Part 2.
        if( namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace)
        ||  namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace2) ) {
            if(xsdlib==null)
                xsdlib = new com.sun.msv.datatype.xsd.ngimpl.DataTypeLibraryImpl();
            return xsdlib;
        }
        
        // RELAX NG compatibiltiy datatypes library is also supported
        if( namespaceURI.equals(CompatibilityDatatypeLibrary.namespaceURI) ) {
            if( compatibilityLib==null )
                compatibilityLib = new CompatibilityDatatypeLibrary();
            return compatibilityLib;
        }
        
        return null;
    }
    
}
