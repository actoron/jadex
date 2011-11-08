/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng.comp;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.trex.ng.AttributeState;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CompAttributeState extends AttributeState {
    protected Expression annealExpression( Expression contentModel ) {
        
        final Expression exp = super.annealExpression(contentModel);
        
        String defaultValue = startTag.getAttribute(
            RELAXNGCompReader.AnnotationNamespace, "defaultValue" );
        if(defaultValue!=null && (exp instanceof AttributeExp)) {
//            if(!nameClass instanceof SimpleNameClass)
//                // attribute with a:defaultValue must be a simple name.
//                reader.reportError(
//                    RELAXNGCompReader.ERR_NAME_IS_NOT_SIMPLE_FOR_DEFAULTEDATTRIBUTE );
//        
            // remember that a default value is specified for this attribute.
            // Since AttributeExps are not unified, it is safe to use it as a key.
            // all checks are performed later.
            RELAXNGCompReader reader = (RELAXNGCompReader)this.reader;
            reader.addDefaultValue((AttributeExp)exp,defaultValue);
        }
                
        return exp;
    }
}
