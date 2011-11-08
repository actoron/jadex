/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.ValidationContext;


/**
 * "language" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#language for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LanguageType extends TokenType {
    
    public static final LanguageType theInstance = new LanguageType();
    private LanguageType() { super("language",false); }
    
    final public XSDatatype getBaseType() {
        return TokenType.theInstance;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        /*    RFC1766 defines the following BNF
        
             Language-Tag = Primary-tag *( "-" Subtag )
             Primary-tag = 1*8ALPHA
             Subtag = 1*8ALPHA

            Whitespace is not allowed within the tag.
            All tags are to be treated as case insensitive.
        */
        
        final int len = content.length();
        int i=0; int tokenSize=0;
        
        while( i<len ) {
            final char ch = content.charAt(i++);
            if( ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
                tokenSize++;
                if( tokenSize==9 )
                    return null;    // maximum 8 characters are allowed.
            } else
            if( ch=='-' ) {
                if( tokenSize==0 )    return null;    // at least one alphabet preceeds '-'
                tokenSize=0;
            } else
                return null;    // invalid characters
        }
        
        if( tokenSize==0 )    return null;    // this means either string is empty or ends with '-'
        
        return content.toLowerCase();
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
