package com.sun.msv.grammar;

/**
 * &lt;interleave&gt; pattern of TREX, or &lt;all&gt; particle of XML Schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class InterleaveExp extends BinaryExp {
    InterleaveExp( Expression left, Expression right ) {
        super(left,right);
    }
    
    public Object visit( ExpressionVisitor visitor ) {
        return visitor.onInterleave(this);
    }
    public Expression visit( ExpressionVisitorExpression visitor ) {
        return visitor.onInterleave(this);
    }
    public boolean visit( ExpressionVisitorBoolean visitor ) {
        return visitor.onInterleave(this);
    }
    public void visit( ExpressionVisitorVoid visitor ) {
        visitor.onInterleave(this);
    }
    protected boolean calcEpsilonReducibility() {
        return exp1.isEpsilonReducible() && exp2.isEpsilonReducible();
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
