/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * &lt;mixed&gt; of RELAX.
 * 
 * For TREX, this operator is not an essential one. You can use
 * <xmp>
 *   <interleave>
 *     <anyString />
 *     ...
 *   </interleave>
 * </xmp>
 * 
 * However, by introducing "mixed" as a primitive, 
 * RELAX module can be expressed without using interleave.
 * 
 * Also, mixed makes validation faster.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class MixedExp extends UnaryExp {
    MixedExp(Expression exp) {
        super(exp);
    }

    public Object visit(ExpressionVisitor visitor) {
        return visitor.onMixed(this);
    }
    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onMixed(this);
    }
    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onMixed(this);
    }
    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onMixed(this);
    }

    protected boolean calcEpsilonReducibility() {
        return exp.isEpsilonReducible();
    }

    // serialization support
    private static final long serialVersionUID = 1;
}
