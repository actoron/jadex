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

import com.sun.msv.util.StartTagInfo;

/**
 * Instances of this class is attached to {@link ValidityViolation} to
 * provide detailed information about the error.
 * 
 * <p>
 * <em>WARNING:</em> error information is highly sensitive to the internal
 * change of MSV. A new subclass of <code>ErrorInfo</code> may be added, or
 * existing subclasses may be removed.
 * So your code that uses this information may be affected by
 * future changes of MSV.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ErrorInfo {
    
    /** Base class for element related errors. */
    public static abstract class ElementErrorInfo extends ErrorInfo {
        /** tag name information. */
        public final String qName;
        public final String namespaceURI;
        public final String localName;
        public ElementErrorInfo( String qn, String ns, String loc ) {
            qName = qn;
            namespaceURI = ns;
            localName = loc;
        }
        public ElementErrorInfo( StartTagInfo sti ) {
            this( sti.qName, sti.namespaceURI, sti.localName );
        }
    }
    
    
    /**
     * Bad text.
     * 
     * <p>
     * This type of errors indicate that the document contains PCDATA but it
     * is not good. Here are typical examples of this error.
     * 
     * <ol><li>
     * PCDATA appears where no text is allowed at all. For example:
     *   
     * <pre><xmp>
     * <html>
     *   <head> ... </head>
     *   ** invalid text **
     *   <body> ... </body>
     * </html>
     * </xmp></pre>
     * 
     * <li>
     * text was not a correct value for the given datatype. For example,
     * 
     * <pre><xmp>
     * <!-- when "integer" is expected -->
     * <length> five </length>
     * </xmp></pre>
     * 
     * </ol>
     * 
     * <p>
     * Usually, the user can fix this error by removing or chaning the text.
     * 
     * <p>
     * MSV validates text after it collects the whole string. That means
     * MSV does not validate text in the SAX's <code>characters</code> callback.
     * As a result, this error is reported after
     * the next start tag or end tag is found.
     * 
     * <p>
     * For example, in the above example, the error is reported when MSV
     * reads <code>&lt;body></code>(start tag) and
     * <code>&lt;/length></code> (end tag) respectively.
     */
    public static class BadText extends ErrorInfo {
        /** The actual text that caused the error. */
        public final String literal;
        public BadText( String _literal ) { literal = _literal; }
        public BadText( StringBuffer _literal ) { literal = _literal.toString(); }
    }
    
    
    /**
     * Bad tag name.
     * 
     * <p>
     * This type of error occurs when MSV finds an unexpected tag name.
     * For example:
     * 
     * <ol><li>
     * When a tag name appears where it is not valid to appear.
     * <pre><xmp>
     * <html>
     *   <head> ... </head>
     *   <head> ... </head> <!-- head cannot appear here -->
     *   <body> ... </body>
     * </html>
     * </pre></xmp>
     * 
     * <li>
     * When there is a typo in the tag name.
     * <pre><xmp>
     * <html>
     *   <heed> ... </heed> <!-- typo -->
     *   <body> ... </body>
     * </pre></xmp>
     * 
     * <li>
     * When an element appears where no element is allowed at all.
     * <pre><xmp>
     * <html>
     *   <head>
     *     <meta ...>
     *       <junk/>   <!-- meta cannot have any children -->
     * </xmp></pre>
     * 
     * </ol>
     * 
     * <p>
     * This error is reported when the startElement callback is called.
     */
    public static class BadTagName extends ElementErrorInfo {
        public BadTagName( String qn, String ns, String loc )    { super(qn,ns,loc); }
        public BadTagName( StartTagInfo sti )                    { super(sti); }
    }
    
    
    /**
     * Bad attribute.
     * 
     * <p>
     * This is an error when the attribute name is wrong, or the attribute value 
     * is wrong. For example:
     * 
     * <ol><li>
     * When MSV sees an unexpected attribute name:
     * <pre><xmp>
     * <img hreeef="logo.gif"/>
     * </xmp><pre>
     * 
     * <li>
     * When an attribute value does not match the specified datatype.
     * For example, the following document causes this error if the "width"
     * attribute is specified as the int type.
     * 
     * <pre><xmp>
     * <img href="logo.gif" width="twenty four pixels" />
     * </xmp></pre>
     * 
     * </ol>
     * 
     * <p>
     * This error is reported in the startElement callback.
     * 
     * <p>
     * Currently, the application cannot easily distinguish whether
     * this error is caused by an invalid attribute value, or invalid
     * attribute name. It is also non-trivial for MSV to detect this difference
     * correctly. But there maybe applications to which this difference is
     * important. <a href="mailto:kohsuke.kawaguchi@sun.com">
     * I welcome any comments on this issue.</a>
     * 
     */
    public static class BadAttribute extends ElementErrorInfo {
        /** information about the attribute that caused the error. */
        public final String attQName;
        public final String attNamespaceURI;
        public final String attLocalName;
        public final String attValue;
        protected BadAttribute( StartTagInfo sti, String qn, String ns, String loc, String v ) {
            super(sti);
            attQName            = qn;
            attNamespaceURI        = ns;
            attLocalName        = loc;
            attValue            = v;
        }
    }
    
    /**
     * Missing attribute.
     * 
     * <p>
     * This type of error occurs only when required attributes are missing.
     * Note that the fields of this class provide information about the element,
     * not the attribute which is missing.
     * 
     * <pre><xmp>
     * <img/>  <!-- where is the href attribute? -->
     * </xmp></pre>
     */
    public static class MissingAttribute extends ElementErrorInfo {
        public MissingAttribute( String qn, String ns, String loc )    { super(qn,ns,loc); }
        public MissingAttribute( StartTagInfo sti )                    { super(sti); }
    }

    /**
     * This error occurs when MSV sees an end tag unexpectedly. For example:
     * 
     * <pre><xmp>
     * <html>
     *   <head> ... </head>
     * </html>  <!-- where is the body element? -->
     * </xmp></pre>
     * 
     * <p>
     * Usually, this means that additional elements and/or PCDATA are necessary
     * to make the document valid. 
     * 
     * <p>
     * Fields of the class provide information about enclosing tag. (In case of
     * the above example, it is <code>html</code>.)
     */
    public static class IncompleteContentModel extends ElementErrorInfo {
        public IncompleteContentModel( String qn, String ns, String loc )    { super(qn,ns,loc); }
        public IncompleteContentModel( StartTagInfo sti )                    { super(sti); }
    }
}
