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

import java.util.StringTokenizer;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses &lt;simpleType&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeState extends TypeWithOneChildState
{
    protected State createChildState( StartTagInfo tag ) {
        
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        final String name = startTag.getAttribute("name");
        String uri = getTargetNamespaceUri();
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("restriction") )    return new RestrictionState(uri,name);
        if( tag.localName.equals("list") )            return new ListState(uri,name);
        if( tag.localName.equals("union") )        return new UnionState(uri,name);
        
        return null;    // unrecognized
    }

    protected XSDatatypeExp annealType( final XSDatatypeExp dt ) {
        final String finalValueStr = startTag.getAttribute("final");
        if(finalValueStr!=null) {
            final int finalValue = getFinalValue(finalValueStr);
            
            // create a new type by adding final constraint.
            return dt.createFinalizedType(finalValue,reader);
        } else
            return dt;
    }


    /** parses final attribute */
    public int getFinalValue( String list ) {
        int finalValue = 0;
        StringTokenizer tokens = new StringTokenizer(list);
        while(tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            
            if( token.equals("#all") )
                finalValue |=    XSDatatype.DERIVATION_BY_LIST|
                                XSDatatype.DERIVATION_BY_RESTRICTION|
                                XSDatatype.DERIVATION_BY_UNION;
            else
            if( token.equals("restriction") )
                finalValue |= XSDatatype.DERIVATION_BY_RESTRICTION;
            else
            if( token.equals("list") )
                finalValue |= XSDatatype.DERIVATION_BY_LIST;
            else
            if( token.equals("union") )
                finalValue |= XSDatatype.DERIVATION_BY_UNION;
            else {
                reader.reportError( 
                    GrammarReader.ERR_ILLEGAL_FINAL_VALUE, token );
                return 0;    // abort
            }
        }
        return finalValue;
    }


}
