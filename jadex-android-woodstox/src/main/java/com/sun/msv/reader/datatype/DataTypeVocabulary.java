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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * this class is used to parse foreign datatype vocabulary.
 * 
 * Each datatype vocabulary must be associated with one namespace URI.
 * When the element with that namespace URI is first found, this object is
 * instanciated. After that, whenever the element with the namespace URI
 * is found, createTopLevelReaderState method will be used to parse the element
 * (and its descendants.)
 * 
 * And whenever a reference to this vocabulary by name (e.g., "mydt:mytypename")
 * is found, getType method is called to resolve this name into DataType object.
 * 
 * One instance of this class is used throughout the parsing of one grammar.
 * Therefore, implementations are encouraged to take advantages of this property
 * and keep context information (e.g., user-defined named datatypes).
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface DataTypeVocabulary
{
    /**
     * creates a State object that will parse the element specified
     * by tag parameter.
     * 
     * @return null
     *        if given start tag is not recognized by this object.
     * 
     * This method is called when an "island" of this vocabulary was found.
     * The state returned from this method will be used to parse the root
     * element of this island.
     * 
     * The parent state of this state must implement TypeOwner or ExpressionOwner.
     * In either case, the implementation must report its parsing result
     * by calling either interface. If both interface is implemented,
     * the implementation must notify via TypeOwner interface only and may not
     * call methods of ExpressionOwner.
     * 
     * If the parsed island is not a type definition (for example, comments or
     * inclusion), the implementation may not call TypeOwner nor ExpressionOwner.
     */
    State createTopLevelReaderState( StartTagInfo tag );
    
    /**
     * resolves a type name to Datatype object.
     * 
     * @param localTypeName
     *        local part of the qualified name, like "string" or "integer".
     *        prefix part must be removed by the caller.
     * 
     * @return
     *        a non-null valid datatype object.
     * 
     * @exception DatatypeException
     *        if the specified type name is a valid type name.
     */
    Datatype getType( String localTypeName ) throws DatatypeException;
}
