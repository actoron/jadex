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

import com.sun.msv.grammar.util.RefExpRemover;

/**
 * Primitive of the tree regular expression.
 * 
 * most of the derived class is immutable (except ReferenceExp, ElementExp and OtherExp).
 * 
 * <p>
 * By making it immutable, it becomes possible to share subexpressions among expressions.
 * This is very important for regular-expression-derivation based validation algorithm,
 * as well as for smaller memory footprint.
 * This sharing is automatically achieved by ExpressionPool.
 * 
 * <p>
 * ReferebceExp, ElementExp, and OtherExp are also placed in the pool,
 * but these are not unified. Since they are not unified,
 * application can derive classes from these expressions
 * and mix them into AGM. This technique is heavily used to introduce schema language
 * specific primitives into AGM. See various sub-packages of this package for examples.
 * 
 * <p>
 * The equals method must be implemented by the derived type. equals method will be
 * used to unify the expressions. equals method can safely assume that its children
 * are already unified (therefore == can be used to test the equality, rather than
 * equals method).
 * 
 * <p>
 * To achieve unification, we overload the equals method so that
 * <code>o1.equals(o2)</code> is true if o1 and o2 are identical.
 * There, those two objects must return the same hash code. For this purpose,
 * the hash code is calculated statically and cached internally.
 *
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class Expression implements java.io.Serializable {
    
    /** cached value of epsilon reducibility.
     * 
     * Epsilon reducibility can only be calculated after parsing the entire expression,
     * because of forward reference to other pattern.
     */
    private Boolean epsilonReducibility;

    /** returns true if this expression accepts empty sequence.
     * 
     * <p>
     * If this method is called while creating Expressions, then this method
     * may return approximated value. When this method is used while validation,
     * this method is guaranteed to return the correct value.
     */
    public boolean isEpsilonReducible() {
        // epsilon reducibility is cached internally.
        if (epsilonReducibility == null)
            epsilonReducibility = calcEpsilonReducibility() ? Boolean.TRUE : Boolean.FALSE;

        return epsilonReducibility.booleanValue();
    }

    /** computes epsilon reducibility */
    protected abstract boolean calcEpsilonReducibility();

    /**
     * Cached value of the expression after ReferenceExps are removed.
     * This value is computed on demand.
     */
    private Expression expandedExp = null;

    /**
     * Gets the expression after removing all ReferenceExps, until child 
     * AttributeExp or ElementExp.
     */
    public Expression getExpandedExp(ExpressionPool pool) {
        if (expandedExp == null) {
            // this part of the code may be called by the multiple threads
            // even if that happens, there is no consistency problem
            // because two thread will compute the same value.
            expandedExp = this.visit(new RefExpRemover(pool, false));
        }
        return expandedExp;
    }

    /**
     * Peels the occurence expressions from this expression.
     * <p>
     * In AGM, 'X?','X+' and 'X*' are represented by using
     * other primitives. This method returns the 'X' part by
     * removing occurence related expressions.
     */
    public final Expression peelOccurence() {
        // 'X?' is represented as 'choice(X,epsilon)'.
        if (this instanceof ChoiceExp) {
            ChoiceExp cexp = (ChoiceExp)this;
            if (cexp.exp1 == Expression.epsilon)
                return cexp.exp2.peelOccurence();
            if (cexp.exp2 == Expression.epsilon)
                return cexp.exp1.peelOccurence();

            // note that epsilon may be in some branch deep under the tree.
            // for example, when the expression is ((A|epsilon)|B)
            // the above code won't be able to peel the epsilon in it.
            // but this is OK, since this method still returns ChoiceExp,
            // and the type of the expression is what matters.
        }

        // 'X+' is represented as 'oneOrMore(X)'
        if (this instanceof OneOrMoreExp)
            return ((OneOrMoreExp)this).exp.peelOccurence();

        // 'X*' is represented as '(X+)?'
        // therefore it is important to recursively process it.

        // otherwise we've finished.
        return this;
    }

    protected Expression(int hashCode) {
        setHashCode(hashCode);
    }
    
    /**
     * this constructor can be used for the ununified expressions.
     * the only reason there are two parameters is to prevent unintentional
     * use of the default constructor.
     */
    protected Expression() {
        this.cachedHashCode = System.identityHashCode(this);
    }

    /**
     * this field can be used by Verifier implementation to speed up
     * validation. Due to its nature, this field is not serialized.
     * 
     * TODO: revisit this decision of not to serialize this field.
     */
    public transient Object verifierTag = null;

    public abstract Object visit(ExpressionVisitor visitor);
    public abstract Expression visit(ExpressionVisitorExpression visitor);
    public abstract boolean visit(ExpressionVisitorBoolean visitor);
    public abstract void visit(ExpressionVisitorVoid visitor);
    
// if you don't need RELAX capability at all, cut these lines
    public Object visit(com.sun.msv.grammar.relax.RELAXExpressionVisitor visitor) {
        return visit((ExpressionVisitor)visitor);
    }
    public Expression visit(com.sun.msv.grammar.relax.RELAXExpressionVisitorExpression visitor) {
        return visit((ExpressionVisitorExpression)visitor);
    }
    public boolean visit(com.sun.msv.grammar.relax.RELAXExpressionVisitorBoolean visitor) {
        return visit((ExpressionVisitorBoolean)visitor);
    }
    public void visit(com.sun.msv.grammar.relax.RELAXExpressionVisitorVoid visitor) {
        visit((ExpressionVisitorVoid)visitor);
    }
// until here
    
    /**
     * Hash code of this object.
     * 
     * <p>
     * To memorize every sub expression, hash code is frequently used.
     * And computation of the hash code requires full-traversal of
     * the expression. Therefore, hash code is computed when the object
     * is constructed, and kept cached thereafter.
     * 
     * <p>
     * This field is essentially final, but because of the serialization
     * support, we cannot declare it as such. 
     */
    private transient int cachedHashCode;

    public final int hashCode() {
        return cachedHashCode;
    }
    
    private final void setHashCode(int hashCode) {
        this.cachedHashCode = hashCode^getClass().hashCode();
    }
    
    /**
     * Computes the hashCode again.
     * <p>
     * This method and the parameter to the constructor has to be
     * the same. This method is used when the object is being read
     * from the stream. 
     */
    protected abstract int calcHashCode();

    public abstract boolean equals(Object o);

    static protected int hashCode(Object o1, Object o2, int hashKey) {
        // TODO: more efficient hashing algorithm
        return o1.hashCode() + o2.hashCode() + hashKey;
    }

    static protected int hashCode(Object o, int hashKey) {
        // TODO: more efficient hashing algorithm
        return o.hashCode() + hashKey;
    }

    private static class EpsilonExpression extends Expression {
        EpsilonExpression() {
        }
        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }
        public Object visit(ExpressionVisitor visitor) {
            return visitor.onEpsilon();
        }
        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onEpsilon();
        }
        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onEpsilon();
        }
        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onEpsilon();
        }
        protected boolean calcEpsilonReducibility() {
            return true;
        }
        public boolean equals(Object o) {
            return this == o;
        } // this class is used as singleton.

        // serialization support
        private static final long serialVersionUID = 1;
        protected Object readResolve() {
            return Expression.epsilon;
        }
    };
    /**
     * Special expression object that represents epsilon (&#x3B5;).
     * This expression matches to "empty".
     * Epsilon can be thought as an empty sequence.
     */
    public static final Expression epsilon = new EpsilonExpression();

    private static class NullSetExpression extends Expression {
        NullSetExpression() {
        }
        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }
        public Object visit(ExpressionVisitor visitor) {
            return visitor.onNullSet();
        }
        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onNullSet();
        }
        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onNullSet();
        }
        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onNullSet();
        }
        protected boolean calcEpsilonReducibility() {
            return false;
        }
        public boolean equals(Object o) {
            return this == o;
        } // this class is used as singleton.

        // serialization support
        private static final long serialVersionUID = 1;
        protected Object readResolve() {
            return Expression.nullSet;
        }
    };
    /**
     * special expression object that represents the empty set (&#x3A6;).
     * This expression doesn't match to anything.
     * NullSet can be thought as an empty choice.
     */
    public static final Expression nullSet = new NullSetExpression();

    private static class AnyStringExpression extends Expression {
        AnyStringExpression() {
        }
        protected final int calcHashCode() {
            return System.identityHashCode(this);
        }
        public Object visit(ExpressionVisitor visitor) {
            return visitor.onAnyString();
        }
        public Expression visit(ExpressionVisitorExpression visitor) {
            return visitor.onAnyString();
        }
        public boolean visit(ExpressionVisitorBoolean visitor) {
            return visitor.onAnyString();
        }
        public void visit(ExpressionVisitorVoid visitor) {
            visitor.onAnyString();
        }
        // anyString is consider to be epsilon reducible.
        // In other words, one can always ignore anyString.
        // 
        // Instead, anyString will remain in the expression even after
        // consuming some StringToken.
        // That is, residual of anyString by StringToken is not the epsilon but an anyString.
        protected boolean calcEpsilonReducibility() {
            return true;
        }
        public boolean equals(Object o) {
            return this == o;
        } // this class is used as singleton.

        // serialization support
        private static final long serialVersionUID = 1;
        protected Object readResolve() {
            return Expression.anyString;
        }
    };
    /**
     * special expression object that represents "any string".
     * It is close to xsd:string datatype, but they have different semantics
     * in several things.
     * 
     * <p>
     * This object is used as &lt;anyString/> pattern of TREX and
     * &lt;text/> pattern of RELAX NG.
     */
    public static final Expression anyString = new AnyStringExpression();


    protected Object readResolve() {
        setHashCode(calcHashCode());
        return this;
    }
    
    private static final long serialVersionUID = -569561418606215601L;
}
