/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

//import com.sun.msv.grammar.*;

/**
 * Non-recursive ReferenceExpRemover with a cache.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ContentModelRefExpRemover {
    
//    public static Expression remove( Expression exp, ExpressionPool pool ) {
//        return exp.getExpandedExp(pool);
//    }
    

    // the class that does the actual job.
    /*
    private static class Remover extends ExpressionCloner {
        public Remover(ExpressionPool pool) {
            super(pool);
        }

        public Expression onElement(ElementExp exp) {
            return exp;
        }

        public Expression onAttribute(AttributeExp exp) {
            Expression content = exp.exp.visit(this);
            if (content == Expression.nullSet) {
                return Expression.nullSet; // this attribute is not allowed
            }
            return pool.createAttribute(exp.nameClass, content, exp.getDefaultValue());
        }

        public Expression onRef(ReferenceExp exp) {
            return exp.exp.visit(this);
        }

        public Expression onOther(OtherExp exp) {
            return exp.exp.visit(this);
        }
    }
    */
}
