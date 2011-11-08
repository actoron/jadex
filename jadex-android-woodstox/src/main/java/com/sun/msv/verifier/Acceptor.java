/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;

/**
 * exposes the low-level validation engine interface.
 * 
 * <p>
 * represents a pseudo-automaton acceptor.
 * this interface is used to validate content models.
 * 
 * 
 * 
 * <h2>Perform Validation</h2>
 * <p>
 * To perform validation, call the createAcceptor method of the DocumentDeclaration
 * interface to obtain an Acceptor for validating the document element.
 * 
 * <pre>
 * Acceptor a = vgm.createAcceptor();
 * </pre>
 * 
 * <p>
 * One acceptor is responsible for validating one element. So you also need
 * some form of stack. If you are using a "push" interface like SAX, you need
 * an explicit stack. If you are validating in "pull" fashion (like DOM), then
 * you can use a recursion instead of an explicit stack.
 * The following explanation assumes SAX-like interface.
 * 
 * <p>
 * Now, get back to the story. Whenever you encounter a start tag, create a new
 * acceptor, which validates the children of newly encountered element.
 * 
 * <pre>
 * stack.push(a);
 * a = a.createChildAcceptor( sti, null );
 * </pre>
 * 
 * <p>
 * If this tag name was unexpected, then this method returns null. See javadoc
 * for more details.
 * 
 * <p>
 * Then, for every attributes, call the {@link #onAttribute} method.
 * After that you call the {@link #onEndAttributes} method.
 * 
 * <pre><xmp>
 * for( int i=0; i<atts.getLength(); i++ )
 *   a.onAttribute( atts.getURI(i), .... );
 * a.onEndAttributes();
 * </xmp></pre>
 * 
 * <p>
 * An error can occur at any method. See the method documentations for details.
 * 
 * <p>
 * If you find an end tag, make sure that the acceptor is satisfied. An acceptor
 * is said to be unsatisfied when it needs more elements/text to complete the content
 * model. For example, if the content model is (A,B,C) and it only sees (A,B), then
 * the acceptor is not satisfied because it needs to see C.
 * 
 * <pre>
 * if(!a.isAcceptState(null))
 *   ; // error because the acceptor is unsatisfied.
 * Acceptor child = a;
 * a = stack.pop();
 * a.stepForward(child,null);
 * </pre>
 * 
 * <p>
 * Then, call the stepForward method of the parent acceptor and pass
 * the child acceptor to it.
 * 
 * <p>
 * Finally, whenever you see a text, call the onText method.
 * If the text was unexpected or not allowed, then this method returns null.
 * See the documentation for details.
 * 
 * <pre>
 * a.onText("text",context,null,null);
 * </pre>
 * 
 * 
 * <p>
 * In this way, you can better control the validation process.
 * 
 * <p>
 * If you need even finer control of the validation process
 * (e.g., you need to know the list of allowed elements/attributes),
 * you may want to rely on the <code>regexp</code> implementation of VGM.
 * see {@link com.sun.msv.verifier.regexp.REDocumentDeclaration} for detail.
 * 
 * 
 * 
 * <h2>Downcasting</h2>
 * 
 * <p>
 * It is often useful to downcast the Acceptor interface to appropriate
 * derived class. For example, if you are using
 * {@link com.sun.msv.verifier.regexp.REDocumentDeclaration}, then you can always
 * downcast an Acceptor to
 * {@link com.sun.msv.verifier.regexp.ExpressionAcceptor}, which provides
 * more predictable behaviors and some useful methods.
 * 
 * 
 * @see DocumentDeclaration
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("deprecation")
public interface Acceptor
{
    /**
     * creates an Acceptor that will accept
     * the content model of the children of this moment.
     * 
     * 
     * <p>
     * Once you create an acceptor, you need to call the
     * {@link #onAttribute} method for each present attribute,
     * and then you need to call the {@link #onEndAttributes} method.
     * 
     * <p>
     * If an error occurs at this method, the bottom line is that the user cannot
     * write this element here.
     * 
     * @param refErr
     *        if this parameter is non-null, the implementation should
     *        try to detect the reason of error and recover from it.
     *        and this object should have the error message as its str field.
     * 
     * @param sti
     *        this parameter provides the information about the start tag to the
     *        acceptor object. Usually attribute information is ignored, but
     *        sometimes they are used as hints.
     * 
     * @return null
     *        If refErr is null, return null if the given start tag is not accepted.
     *        If refErr is non-null, return null only when the recovery is impossible.
     */
    Acceptor createChildAcceptor( StartTagInfo sti, StringRef refErr );
    
    /**
     * processes an attribute.
     * 
     * <p>
     * For every attribute present in the document, you need to call this method.
     * 
     * <p>
     * An error at this method typically indicates that
     * <ol>
     *  <li>this attribute is not allowed to appear here
     *  <li>the attribute name was OK, but the value was incorrect.
     * </ol>
     * 
     * @param    refErr
     *        In case of an error, this object will receive the localized error
     *        message. Null is a valid value for this parameter.
     *        The implementation must provide some kind of message.
     * 
     * @param    refType
     *        If this parameter is non-null, this object will receive the datatype
     *        assigned to the attribute value.
     *    
     *        <p>
     *        This feature is optional and therefore the implementation is
     *        not necessarily    provide this information.
     * 
     * @return
     *        <b>false</b> if an error happens and refErr parameter
     *        was not provided. Otherwise true.
     */
    boolean onAttribute2(
        String namespaceURI, String localName, String qName, String value,
        IDContextProvider2 context, StringRef refErr, DatatypeRef refType );

    /**
     * @deprecated
     */
    boolean onAttribute(
        String namespaceURI, String localName, String qName, String value,
        IDContextProvider context, StringRef refErr, DatatypeRef refType );
    
    
    /**
     * notifies the end of attributes.
     * 
     * <p>
     * This method needs to be called after the {@link #onAttribute}
     * method is called for each present attribute.
     * 
     * <p>
     * An error at this method typically indicates that some required
     * attributes are missing.
     * 
     * @param    sti
     *        This information is used to produce the error message if that is
     *        necessary.
     * 
     * @param    refErr
     *        In case of an error, this object will receive the localized error
     *        message. Null is a valid value for this parameter.
     *        The implementation must provide some kind of message.
     * 
     * @return
     *        <b>false</b> if an error happens and refErr parameter
     *        was not provided. Otherwise true.
     */
    boolean onEndAttributes( StartTagInfo sti, StringRef refErr );
    
    
    /**
     * processes a string literal.
     * 
     * @param context
     *        an object that provides context information necessary to validate
     *        some datatypes.
     * @param refErr
     *        if this parameter is non-null, the implementation should
     *        try to detect the reason of error and recover from it.
     *        and this object should have the error message as its str field.
     * @param refType
     *        if this parameter is non-null and the callee supports
     *        type-assignment, the callee will assign the DataType object
     *        to this variable.
     *        Caller must initialize refType.type to null before calling this method.
     *        If the callee doesn't support type-assignment or type-assignment
     *        is impossible for this literal (possibly by ambiguous grammar),
     *        this variable must kept null.
     * 
     * @return false
     *        if the literal at this position is not allowed.
     */
    boolean onText2( String literal, IDContextProvider2 context, StringRef refErr, DatatypeRef refType );
    
    /**
     * @deprecated
     */
    boolean onText( String literal, IDContextProvider context, StringRef refErr, DatatypeRef refType );
    
    /**
     * eats a child element
     * 
     * <p>
     * A child acceptor created by the {@link createChildAcceptor} method
     * will be ultimately consumed by the parent through this method.
     * 
     * <p>
     * It is the caller's responsibility to make sure that child acceptor
     * is in the accept state. If it's not, that indicates that some required
     * elements are missing (in other words, contents are not allowed to end here).
     * 
     * <p>
     * It is the callee's responsibility to recover from error of
     * unsatisified child acceptor. That is, even if the caller finds that
     * there are missing elements, it is possible to call this method
     * as if there was no such error.
     * 
     * @return false
     *        if an error happens. For example, if the implementation passes
     *        an acceptor which is NOT a child of this acceptor, then
     *        the callee can return <b>false</b>.
     */
    boolean stepForward( Acceptor child, StringRef errRef );
    
    /**
     * checks if this Acceptor is satisifed.
     * 
     * <p>
     * Acceptor is said to be satisfied when given sequence of elements/strings
     * is accepted by the content model. This method should be called before
     * calling the stepForward method to make sure that the children
     * is written properly.
     * 
     * @param errRef
     *        If this value is non-null, implementation can diagnose the error
     *        and sets the message to the object.
     */
    boolean isAcceptState( StringRef errRef );
    
    /**
     * gets the "type" object for which this acceptor is working.
     * 
     * This method is used for type assignment. Actual Java type of
     * return value depends on the implementation.
     * 
     * @return null
     *        the callee should return null when it doesn't support
     *        type-assignment feature, or type-assignment is impossible
     *        for this acceptor (for example by ambiguous grammar).
     */
    Object getOwnerType();
    
    /**
     * clones this acceptor.
     * 
     * <p>
     * You can keep a "bookmark" of the acceptor by cloning it.
     * This is useful when you are trying to perform "partial validation".
     * 
     * <p>
     * Cloned acceptor will behave in exactly the same way as the original one.
     */
    Acceptor createClone();
    
    /**
     * gets how this acceptor handles characters.
     * 
     * <p>
     * This method makes it possible to optimize character handling.
     * For many elements of data-oriented schemas, characters are completely prohibited.
     * For example, In SVG, only handful elements are allowed to have #PCDATA and
     * all other elements have element-only content model. Also, for many elements of
     * document-oriented schemas, #PCDATA is allowed just about anywhere.
     * 
     * <p>
     * In the former case, this method returns {@link #STRING_PROHIBITED}.
     * In other words, this declares that any onText(String) method with
     * non-whitespace characters will always result in a failure.
     * The caller can then exploit this property of the content model and 
     * can immediately signal an error when it finds characters, or discard any
     * whitespace characters without keeping them in memory.
     * 
     * <p>
     * In the latter case, this method returns {@link #STRING_IGNORE}.
     * This declares that any onText(String) call does not change anything at all.
     * The caller can then exploit this property and discard any characeters it found.
     * 
     * <p>
     * If non of the above applies, or the implementation is simply not capable of
     * providing this information, then this method returns {@link #STRING_STRICT}.
     * In this case, the caller has to faithfully call the onText(String) method
     * for all characeters it found.
     * 
     * <p>
     * Although this method can be called anytime, it is intended to be called
     * only once when the acceptor is first created.
     * 
     * @return
     *        one of the three constant values shown below.
     */
    int getStringCareLevel();
    
    /**
     * only whitespaces are allowed. Acceptor will reject any string
     * if it's not whitespaces.
     * 
     * for example, &lt;elementRule&gt; of RELAX doesn't allow
     * characters (except whitespaces) at all.
     */
    static final int STRING_PROHIBITED    = 0x00;
    /**
     * character literals are allowed, but Acceptor doesn't care
     * its contents and where it is appeared.
     * 
     * The caller doesn't need to call onText for literal.
     * This mode is used for mixed contents.
     */
    static final int STRING_IGNORE        = 0x01;
    /**
     * attentive handling of characters is required.
     * 
     * Verifier has to keep track of exact contents of string and
     * it must call onText for string accordingly.
     */
    static final int STRING_STRICT        = 0x02;
    
    // TODO: possible 4th class, STRING_SLOPPY,
    // which requires stepForward invocation but don't care about its content.
}
