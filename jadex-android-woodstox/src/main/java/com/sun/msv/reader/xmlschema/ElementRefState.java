/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.ExpressionWithoutChildState;

/**
 * used to parse &lt;element &gt; element with ref attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRefState extends ExpressionWithoutChildState {

    protected Expression makeExpression() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        // TODO: what attributes can be used with @ref?
        
        if( !startTag.containsAttribute("ref") )
            // existance of @ref must be checked before instanciation of this object.
            throw new Error();
        
        // this this tag has @ref.
        Expression exp = reader.resolveQNameRef(
            startTag, "ref",
            new XMLSchemaReader.RefResolver() {
                public ReferenceContainer get( XMLSchemaSchema g ) {
                    return g.elementDecls;
                }
            } );
        if( exp==null )        return Expression.epsilon;    // couldn't resolve QName.
        
        // minOccurs/maxOccurs is processed through interception
        return exp;
    }
}
