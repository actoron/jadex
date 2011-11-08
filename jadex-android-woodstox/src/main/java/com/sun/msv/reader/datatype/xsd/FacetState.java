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

import java.util.Set;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.reader.ChildlessState;
import com.sun.msv.reader.GrammarReader;

/**
 * state that reads facets.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FacetState extends ChildlessState
{
    /** set of recognizable facet names */
    public static final Set<String> facetNames = initFacetNames();
    
    static private Set<String> initFacetNames()
    {
        Set<String> s = new java.util.HashSet<String>();
        s.add("length");
        s.add("minLength");
        s.add("maxLength");
        s.add("pattern");
        s.add("enumeration");
        s.add("maxInclusive");
        s.add("minInclusive");
        s.add("maxExclusive");
        s.add("minExclusive");
        s.add("whiteSpace");
        s.add("fractionDigits");
        s.add("totalDigits");
        return s;
    }
    
    protected void startSelf()
    {
        super.startSelf();
        final String value = startTag.getAttribute("value");
        
        if( value==null )
        {
            reader.reportError( GrammarReader.ERR_MISSING_ATTRIBUTE, startTag.localName, "value" );
            // recover by ignoring this facet.
        } else {
            try {
                ((FacetStateParent)parentState).getIncubator().addFacet(
                    startTag.localName, value, "true".equals(startTag.getAttribute("fixed")), reader );
            } catch( DatatypeException e ) {
                reader.reportError( e, GrammarReader.ERR_BAD_TYPE, e.getMessage() );
                // recover by ignoring this facet
            }
        }
    }
}
