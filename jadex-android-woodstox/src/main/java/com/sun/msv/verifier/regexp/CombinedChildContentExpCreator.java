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

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;

/**
 * creates "combined child content expression" and gathers "elements of concern"
 * and its "attribute-pruned" content model.
 * 
 * Intuitively, "combined child content expression" is a set of 
 * content models of "elements of concern",
 * which is appropriately combined to express dependency between elements.
 * 
 * "Elements of concern" are ElementExps that are possibly applicable to
 * the next element. These gathered element declarations are then tested against
 * next XML element.
 * 
 * "Attribute-pruned" content model is a content model after consuming
 * AttributeTokens and removing unused AttributeExp nodes.
 * 
 * <p>
 * For example, when the current expression is
 * <PRE><XMP>    <!-- javadoc escape -->
 *   <choice>
 *     <concur>
 *       <element> ..(A).. </element>
 *       <group>
 *         <element> ..(B).. </element>
 *         ...
 *       </group>
 *     </concur>
 *     <group>
 *       <element> ..(C).. </element>
 *       ....
 *     </group>
 *   </choice>
 * </XMP></PRE>
 * 
 * then the combined child expression is
 * 
 * <PRE><XMP>
 *   <choice>
 *     <concur>
 *       ..(A').. 
 *       ..(B').. 
 *     </concur>
 *     ..(C').. 
 *   </choice>
 * </XMP></PRE>
 * 
 * and elements of concern and its attribute-pruned content models are
 * 
 * <XMP>
 * <element> ..(A).. </element>  ->   ..(A')..
 * <element> ..(B).. </element>  ->   ..(B')..
 * <element> ..(C).. </element>  ->   ..(C')..
 * </XMP>
 * 
 * (A'),(B'), and (C') are attribute-pruned content models of (A),(B), and (C)
 * respectively.
 * 
 * Note that combined child pattern contains only &lt;choice&gt; and &lt;concur&gt; as 
 * its grue (of course, except ..(A').. , ..(B').. , and ..(C').. ).
 * 
 * 
 * This function object also calculates "continuation", which is the residual
 * expression after eating elements of concern.
 * 
 * For example, say the expression is "(A|(B,C))?,D".
 * 
 * When EoC is B, then the continuation will be C,D.
 * When EoC is A, then the continuation will be D.
 * When EoC is D, then the continuation will be epsilon.
 * 
 * When there are multiple EoC, (say A and B), then
 * the continuation will be meaningless (because it depends on which EoC will
 * be accepted), and thus won't be used.
 * 
 * However, the implementator must be aware that it is possible for a
 * binary operator to have EoC on both branch and EoC is still unique.
 * The following expression is an example.
 * 
 * (A|B)*,C?,(A|B)*
 * 
 * when A is EoC, SequenceExp of (A|B)* and C?,(A|B)* has EoC on both branch.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CombinedChildContentExpCreator implements ExpressionVisitorVoid {
    protected final ExpressionPool pool;

    // these variables are set each time 'get' method is called
    private StartTagInfo tagInfo;
    /**
     * matched elements. the buffer is usually bigger than {@link #numElements},
     * but only first {@link #numElements} items are valid result.
     */
    private ElementExp[] result = new ElementExp[4];
    private int numElements;
    private boolean checkTagName;

    // TODO: do we gain some performance if we stop creating combined child content expression
    // (for RELAX, c.c.c.e is unnecessary)

    // TODO: how many object instanciation can we avoid
    // if we keep one local reusable copy of OwnerAndContent?
    
    public static class ExpressionPair {
        public final Expression content;
        public final Expression continuation;
        public ExpressionPair(Expression content, Expression continuation) {
            this.content = content;
            this.continuation = continuation;
        }
    }

    /*
        Ideally, these two fields should be return values from onXXX methods.
        In fact, this class was once made in that way.
    
        However, to return two values, we need to create an object to wrap them,
        and it takes memory and time. 
        
        An experiment shows that ExpressionPairs used for that purpose occupies
        1/4 (in number) of object instanciation of the entire obejcts created in
        Verifier.startElement method.
    
        So now it is rewritten to use this instance fields instead of return values.
    */
    private Expression content;
    private Expression continuation;

    protected CombinedChildContentExpCreator(ExpressionPool pool) {
        this.pool = pool;
    }

    /**
     * computes a combined child content pattern and its continuation, with error recovery.
     * 
     * After calling this method, caller can call getElementsOfConcern to obtain
     * each EoC.
     * If both feedAttributes and checkTagName are false, then StartTagInfo is
     * also unnecessary.
     * 
     * @param feedAttributes
     *        if this flag is false, Attribute feeding & pruning are skipped and 
     *        AttributeExps are fully remained in the resulting expression.
     * @param checkTagName
     *        if this flag is false, tag name check is skipped.
     */
    public ExpressionPair get(Expression combinedPattern, StartTagInfo info, boolean checkTagName) {
        numElements = 0;
        return continueGet(combinedPattern, info, checkTagName);
    }

    public final ExpressionPair continueGet(Expression combinedPattern, StartTagInfo info, boolean checkTagName) {
        foundConcur = false;
        this.tagInfo = info;
        this.checkTagName = checkTagName;
        combinedPattern.visit(this);

        // when more than one element of concern is found,
        // continuation cannot be used.
        if (numElements != 1)
            continuation = null;
        return new ExpressionPair(content, continuation);
    }

    /** computes a combined child content pattern and (,if possible,) its continuation. */
    public ExpressionPair get(Expression combinedPattern, StartTagInfo info) {
        StringPair sp = null;

        // check the cache
        if (combinedPattern.verifierTag != null) {
            OptimizationTag ot = (OptimizationTag)combinedPattern.verifierTag;
            sp = new StringPair(info.namespaceURI, info.localName);
            OptimizationTag.OwnerAndCont cache = (OptimizationTag.OwnerAndCont)ot.transitions.get(sp);

            if (cache != null) {
                // cache hit
                numElements = 1;
                result[0] = cache.owner;
                return new ExpressionPair(cache.owner.contentModel.getExpandedExp(pool), cache.continuation);
            }
        }

        ExpressionPair r = (ExpressionPair)get(combinedPattern, info, true);

        if (numElements == 1) {
            // only one element matchs this tag name. cache this result
            OptimizationTag ot = (OptimizationTag)combinedPattern.verifierTag;
            if (ot == null)
                combinedPattern.verifierTag = ot = new OptimizationTag();

            if (sp == null)
                sp = new StringPair(info.namespaceURI, info.localName);

            ot.transitions.put(sp, new OptimizationTag.OwnerAndCont(result[0], r.continuation));
        }
        return r;
    }

    /**
     * obtains matched elements.
     * 
     * This method should be called after calling the get method. The result is
     * in effect until the next invocation of get method. 
     * 
     * <p>
     * The extra care should be taken not to hold reference to the result
     * longer than necessary.
     * The contents of the result is valid only until the next invocation.
     * Because OwnerAndContent objects are reused.
     * 
     * <p>
     * Apparently this is a bad design, but this design gives us better performance.
     */
    public final ElementExp[] getMatchedElements() {
        return result;
    }

    /** gets the number of matched elements.
     * 
     * This method should be called after calling get method. The result is
     * in effect until next invocation of get method.
     * Apparently this is a bad design, but this design gives us better performance.
     */
    public final int numMatchedElements() {
        return numElements;
    }

    /**
     * a flag that indicates that we have 'concur' element to combine
     * elements of concern.
     * 
     * If 'concur' is used, we have to keep track of combined child content
     * expression to detect errors. If 'concur' is not used, then
     * keeping track of all primitive child content expressions are enough
     * to detect errors.
     */
    private boolean foundConcur;

    public void onConcur(ConcurExp exp) {
        foundConcur = true;
        exp.exp1.visit(this);
        Expression content1 = content;
        Expression continuation1 = continuation;

        exp.exp2.visit(this);

        content = pool.createConcur(content, content1);
        continuation = pool.createConcur(continuation, continuation1);
    }

    public void onInterleave(InterleaveExp exp) {
        exp.exp1.visit(this);
        if (content == Expression.nullSet) {
            exp.exp2.visit(this);
            continuation = pool.createInterleave(continuation, exp.exp1);
            return;
        }

        Expression content1 = content;
        Expression continuation1 = continuation;

        exp.exp2.visit(this);

        if (content == Expression.nullSet) {
            content = content1;
            continuation = pool.createInterleave(continuation1, exp.exp2);
            return;
        }

        // now the situation is something like (A,X)^(A,Y).
        // both accepts this token. So continuation will be X^(A,Y).
        content = pool.createChoice(content, content1);
        continuation = pool.createInterleave(continuation1, exp.exp2);
    }

    /**
     * checks if the result of 'get' method is not the union of all
     * elements of concern.
     * 
     * Within this class, combined child content expression is
     * always the union of all elements of concern. However, some derived
     * class does not guarantee this property.
     * 
     * @return
     *        true if the combined child content expression is not
     *            the union of all elements of concern.
     *        false if otherwise.
     */
    public final boolean isComplex() {
        return foundConcur;
    }

    public void onElement(ElementExp exp) {
        // TODO: may check result and remove duplicate result

        // if tag name is invalid, then remove this element from candidate.
        if (checkTagName && !exp.getNameClass().accepts(tagInfo.namespaceURI, tagInfo.localName)) {
            content = continuation = Expression.nullSet;
            return;
        }

        // check result and see if the same element is already registered.
        // this will reduce the complexity of the result.
        // some RELAX grammar may contain something like
        // (A|B)* C? (A|B)* to implement interleaving of (A|B)* and C.
        // this check becomes important for cases like this.
        for (int i = 0; i < numElements; i++)
            if (result[i] == exp) {
                // the same element is found.
                content = exp.contentModel.getExpandedExp(pool);
                continuation = Expression.epsilon;
                return;
            }

        // also, feeding and pruning attributes are relatively expensive operation.
        // so this is the good place to check other redundancy.

        // create a new result object
        if (numElements == result.length) {
            // expand the buffer if it's full.
            ElementExp[] buf = new ElementExp[result.length * 2];
            System.arraycopy(result, 0, buf, 0, result.length);
            result = buf;
        }
        result[numElements++] = exp;

        content = exp.contentModel.getExpandedExp(pool);
        continuation = Expression.epsilon;
    }

    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
        continuation = pool.createSequence(continuation, pool.createZeroOrMore(exp.exp));
    }
    public void onMixed(MixedExp exp) {
        exp.exp.visit(this);
        continuation = pool.createMixed(continuation);
    }
    
    public void onAttribute( AttributeExp exp ) { content = continuation = Expression.nullSet; }
    public void onEpsilon()                     { content = continuation = Expression.nullSet; }
    public void onNullSet()                     { content = continuation = Expression.nullSet; }
    public void onAnyString()                   { content = continuation = Expression.nullSet; }
    public void onData( DataExp exp )           { content = continuation = Expression.nullSet; }
    public void onValue( ValueExp exp )         { content = continuation = Expression.nullSet; }
    public void onList( ListExp exp )           { content = continuation = Expression.nullSet; }
    public void onRef(ReferenceExp exp) {
        exp.exp.visit(this);
    }
    public void onOther(OtherExp exp) {
        exp.exp.visit(this);
    }
    public void onChoice(ChoiceExp exp) {
        exp.exp1.visit(this);
        Expression content1 = content;
        Expression continuation1 = continuation;

        exp.exp2.visit(this);

        content = pool.createChoice(content, content1);
        continuation = pool.createChoice(continuation, continuation1);
    }
    public void onSequence(SequenceExp exp) {
        exp.exp1.visit(this);
        continuation = pool.createSequence(continuation, exp.exp2);

        if (!exp.exp1.isEpsilonReducible())
            return;

        Expression content1 = content;
        Expression continuation1 = continuation;

        exp.exp2.visit(this);

        if (content == Expression.nullSet) {
            content = content1;
            continuation = continuation1;
            return;
        }

        if (content1 == Expression.nullSet) {
            // exp1 is epsilon reducible but didn't accept this token.
            // so continuation will be that of exp2.
            return;
        }

        // now, we have candidates in both left and right.
        // say,
        // exp = (A,X)?,(A,Y)
        content = pool.createChoice(content, content1);
        continuation = pool.createChoice(continuation1, continuation);
    }
}
