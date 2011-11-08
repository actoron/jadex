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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.DataTypeVocabulary;
import com.sun.msv.util.StartTagInfo;

/**
 * XSD implementation of {@link DataTypeVocabulary}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class XSDVocabulary implements DataTypeVocabulary, java.io.Serializable {
    
    /** namespace URI of XML Schema */
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema-datatypes";
    public static final String XMLSchemaNamespace2= "http://www.w3.org/2001/XMLSchema";
    
    public State createTopLevelReaderState( StartTagInfo tag ) {
        if( tag.localName.equals("simpleType") )    return new SimpleTypeState();
        else    return null;
    }
    
    public Datatype getType( String localTypeName ) throws DatatypeException {
        return DatatypeFactory.getTypeByName(localTypeName);
    }
}
