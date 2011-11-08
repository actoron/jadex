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

import java.util.Iterator;
import java.util.Set;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;

/**
 * SAX ContentHandler that verifies incoming SAX event stream.
 * 
 * This object can be reused to validate multiple documents.
 * Just be careful NOT to use the same object to validate more than one
 * documents <b>at the same time</b>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Verifier extends AbstractVerifier implements IVerifier {
    protected Acceptor current;

    private static final class Context {
        final Context previous;
        final Acceptor acceptor;
        final int stringCareLevel;
        int panicLevel;
        Context(Context prev, Acceptor acc, int scl, int plv) {
            previous = prev;
            acceptor = acc;
            stringCareLevel = scl;
            panicLevel = plv;
        }
    };

    /** context stack */
    Context stack = null;

    /** current string care level. See Acceptor.getStringCareLevel */
    private int stringCareLevel = Acceptor.STRING_STRICT;

    /** characters that were read (but not processed)  */
    private StringBuffer text = new StringBuffer();

    /** Error handler */
    protected ErrorHandler errorHandler;
    public final ErrorHandler getErrorHandler() {
        return errorHandler;
    }
    public final void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }
    /** This flag will be set to true if an error is found */
    protected boolean hadError;

    /** This flag will be set to true after endDocument method is called. */
    private boolean isFinished;

    /** An object used to store start tag information.
     * the same object is reused. */
    private final StartTagInfo sti = new StartTagInfo(null, null, null, null, (IDContextProvider2)null);

    public final boolean isValid() {
        return !hadError && isFinished;
    }

    /** Schema object against which the validation will be done */
    protected final DocumentDeclaration docDecl;

    /**
     * Panic level.
     * 
     * If the level is non-zero, createChildAcceptors will silently recover
     * from error. This effectively suppresses spurious error messages.
     * 
     * This value is set to INITIAL_PANIC_LEVEL when first an error is encountered,
     * and is decreased by successful stepForward and createChildAcceptor.
     * This value is also propagated to child acceptors.
     */
    protected int panicLevel = 0;

    /**
     * Initial panic level when an error is found.
     * If this value is bigger, MSV will take more time to recover from errors,
     * Setting this value to 0 means turning the panic mode off entirely.
     */
    private int initialPanicLevel = DEFAULT_PANIC_LEVEL;

    private static final int DEFAULT_PANIC_LEVEL = 3;
    public final void setPanicMode( boolean usePanicMode ) {
        initialPanicLevel = usePanicMode?DEFAULT_PANIC_LEVEL:0;
    }
    public Verifier(DocumentDeclaration documentDecl, ErrorHandler errorHandler) {
        this.docDecl = documentDecl;
        this.errorHandler = errorHandler;
    }
    
    /** this field is used to receive type information of character literals. */
    private final DatatypeRef characterType = new DatatypeRef();
    public Datatype[] getLastCharacterType() {
        return characterType.types;
    }
    
    protected void verifyText() throws SAXException {
    
        characterType.types = null;
        switch (stringCareLevel) {
            case Acceptor.STRING_PROHIBITED :
                // only whitespace is allowed.
                final int len = text.length();
                for (int i = 0; i < len; i++) {
                    final char ch = text.charAt(i);
                    if (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n') {
                        // error
                        onError(null, localizeMessage(ERR_UNEXPECTED_TEXT, null), new ErrorInfo.BadText(text));
                        break; // recover by ignoring this token
                    }
                }
                break;
    
            case Acceptor.STRING_STRICT :
                final String txt = new String(text);
                if (!current.onText2(txt, this, null, characterType)) {
                    // error
                    // diagnose error, if possible
                    StringRef err = new StringRef();
                    characterType.types = null;
                    current.onText2(txt, this, err, characterType);
    
                    // report an error
                    onError(err, localizeMessage(ERR_UNEXPECTED_TEXT, null), new ErrorInfo.BadText(text));
                }
                break;
    
            case Acceptor.STRING_IGNORE :
                // if STRING_IGNORE, no text should be appended.
                if (text.length() != 0)
                    throw new Error();
                return;
    
            default :
                throw new Error(); //assertion failed
        }
    
        if (text.length() != 0)
            text = new StringBuffer();
    }
    
    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        
        // work gracefully with misconfigured parsers that don't support namespaces,
        // or other sources that produce broken SAX events.
        if( namespaceUri==null )
            namespaceUri="";
        if( localName==null || localName.length()==0 )
            localName=qName;
        if( qName==null || qName.length()==0 )
            qName=localName;
        
        
        super.startElement(namespaceUri, localName, qName, atts);
    
    
        verifyText(); // verify PCDATA first.
    
        // push context
        stack = new Context(stack, current, stringCareLevel, panicLevel);
    
        sti.reinit(namespaceUri, localName, qName, atts, this);
    
        // get Acceptor that will be used to validate the contents of this element.
        Acceptor next = current.createChildAcceptor(sti, null);
    
        panicLevel = Math.max(panicLevel - 1, 0);
    
        if (next == null) {
            // no child element matchs this one
            // let acceptor recover from this error.
            StringRef ref = new StringRef();
            next = current.createChildAcceptor(sti, ref);
    
            ValidityViolation vv =
                onError(
                    ref,
                    localizeMessage(ERR_UNEXPECTED_STARTTAG, new Object[] { qName }),
                    new ErrorInfo.BadTagName(sti));
    
            if (next == null) {
                throw new ValidationUnrecoverableException(vv);
            }
        }
    
        onNextAcceptorReady(sti, next);
    
        // feed attributes
        final int len = atts.getLength();
        for (int i = 0; i < len; i++)
            feedAttribute(next, atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getValue(i));
    
        // call the endAttributes
        if (!next.onEndAttributes(sti, null)) {
            // error.
    
            // let the acceptor recover from the error.
            StringRef ref = new StringRef();
            next.onEndAttributes(sti, ref);
            onError(
                ref,
                localizeMessage(ERR_MISSING_ATTRIBUTE, new Object[] { qName }),
                new ErrorInfo.MissingAttribute(sti));
        }
    
        stack.panicLevel = panicLevel; // back-patching.
    
        stringCareLevel = next.getStringCareLevel();
        if (stringCareLevel == Acceptor.STRING_IGNORE)
            characterType.types = new Datatype[] { StringType.theInstance };
        current = next;
    }
    
    /**
     * this method is called from the startElement method
     * after the tag name is processed and the child acceptor is created.
     * 
     * <p>
     * This method is called before the attributes are consumed.
     * 
     * <p>
     * derived class can use this method to do something useful.
     */
    protected void onNextAcceptorReady(StartTagInfo sti, Acceptor nextAcceptor) throws SAXException {
    }
    
    /**
     * the same instance is reused by the feedAttribute method to reduce
     * the number of the object creation.
     */
    private final DatatypeRef attributeType = new DatatypeRef();
    
    protected Datatype[] feedAttribute(Acceptor child, String uri, String localName, String qName, String value)
        throws SAXException {
        
        // work gracefully with misconfigured parsers that don't support namespaces,
        // or other sources that produce broken SAX events.
        if( uri==null )
            uri="";
        if( localName==null || localName.length()==0 )
            localName=qName;
        if( qName==null || qName.length()==0 )
            qName=localName;
        
        // ignore xmlns:* attributes, which could be a part of Attributes
        // in some SAX events.
        if( qName.startsWith("xmlns:") || qName.equals("xmlns") )
            return new Datatype[0];
        
    
        attributeType.types = null;
        if (!child.onAttribute2(uri, localName, qName, value, this, null, attributeType)) {
            // error
    
            // let the acceptor recover from the error.
            StringRef ref = new StringRef();
            child.onAttribute2(uri, localName, qName, value, this, ref, null);
            onError(
                ref,
                localizeMessage(ERR_UNEXPECTED_ATTRIBUTE, new Object[] { qName }),
                new ErrorInfo.BadAttribute(sti, qName, uri, localName, value));
        }
    
        return attributeType.types;
    }
    
    public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
        
        // work gracefully with misconfigured parsers that don't support namespaces,
        // or other sources that produce broken SAX events.
        if( namespaceUri==null )
            namespaceUri="";
        if( localName==null || localName.length()==0 )
            localName=qName;
        if( qName==null || qName.length()==0 )
            qName=localName;
        
        
        verifyText();
    
        if (!current.isAcceptState(null) && panicLevel == 0) {
            // error diagnosis
            StringRef errRef = new StringRef();
            current.isAcceptState(errRef);
            onError(
                errRef,
                localizeMessage(ERR_UNCOMPLETED_CONTENT, new Object[] { qName }),
                new ErrorInfo.IncompleteContentModel(qName, namespaceUri, localName));
            // error recovery: pretend as if this state is satisfied
            // fall through is enough
        }
        Acceptor child = current;
    
        // pop context
        current = stack.acceptor;
        stringCareLevel = stack.stringCareLevel;
        panicLevel = Math.max(panicLevel, stack.panicLevel);
        stack = stack.previous;
    
        if (!current.stepForward(child, null)) {
            // error
            StringRef ref = new StringRef();
            current.stepForward(child, ref); // force recovery
    
            onError(ref, localizeMessage(ERR_UNEXPECTED_ELEMENT, new Object[] { qName }), null);
        } else
            panicLevel = Math.max(panicLevel - 1, 0);
    
        super.endElement(namespaceUri, localName, qName);
    }
    
    /**
     * signals an error.
     * 
     * This method can be overrided by the derived class to provide different behavior.
     */
    protected ValidityViolation onError(StringRef ref, String defaultMsg, ErrorInfo ei) throws SAXException {
        if (ref == null)
            return onError(defaultMsg, ei);
        if (ref.str == null)
            return onError(defaultMsg, ei);
        else
            return onError(ref.str, ei);
    }
    
    protected ValidityViolation onError(String msg, ErrorInfo ei) throws SAXException {
        ValidityViolation vv = new ValidityViolation(locator, msg, ei);
        hadError = true;
    
        if (errorHandler != null && panicLevel == 0)
            errorHandler.error(vv);
    
        panicLevel = initialPanicLevel;
        return vv;
    }
    
    public Object getCurrentElementType() {
        return current.getOwnerType();
    }
    
    public void characters(char[] buf, int start, int len) throws SAXException {
        if (stringCareLevel != Acceptor.STRING_IGNORE)
            text.append(buf, start, len);
    }
    public void ignorableWhitespace(char[] buf, int start, int len) throws SAXException {
        if (stringCareLevel != Acceptor.STRING_IGNORE && stringCareLevel != Acceptor.STRING_PROHIBITED)
            // white space is allowed even if the current mode is STRING_PROHIBITED.
            text.append(buf, start, len);
    }
    
    protected void init() {
        super.init();
        hadError = false;
        isFinished = false;
        text = new StringBuffer();
        stack = null;
        if (duplicateIds != null)
            duplicateIds.clear();
    }
    
    public void startDocument() throws SAXException {
        // reset everything.
        // since Verifier maybe reused, initialization is better done here
        // rather than constructor.
        init();
        // if Verifier is used without "divide&validate", 
        // this method is called and the initial acceptor
        // is set by this method.
        // When Verifier is used in IslandVerifierImpl,
        // then initial acceptor is set at the constructor
        // and this method is not called.
        current = docDecl.createAcceptor();
    }
    
    public void endDocument() throws SAXException {
        // ID/IDREF check
        if (performIDcheck) {
            if (!ids.keySet().containsAll(idrefs)) {
                hadError = true;
                Iterator<Object> itr = idrefs.iterator();
                while (itr.hasNext()) {
                    Object idref = itr.next();
                    if (!ids.keySet().contains(idref))
                        onError(localizeMessage(ERR_UNSOLD_IDREF, new Object[] { idref }), null);
                }
            }
            if (duplicateIds != null) {
                Iterator<String> itr = duplicateIds.iterator();
                while (itr.hasNext()) {
                    Object id = itr.next();
                    onError(localizeMessage(ERR_DUPLICATE_ID, new Object[] { id }), null);
                }
            }
        }
    
        isFinished = true;
    }
    
    /**
     * Stores all duplicate id values.
     * Errors are reported at the endDocument method because
     * the onDuplicateId method cannot throw an exception.
     */
    private Set<String> duplicateIds;
    
    public void onDuplicateId(String id) {
        if (duplicateIds == null) {
            duplicateIds = new java.util.HashSet<String>();
        }
        duplicateIds.add(id);
    }
    
    public static String localizeMessage(String propertyName, Object[] args) {
        String format = java.util.ResourceBundle.getBundle("com.sun.msv.verifier.Messages").getString(propertyName);
    
        return java.text.MessageFormat.format(format, args);
    }
    
    public static final String ERR_UNEXPECTED_TEXT = // arg:0
        "Verifier.Error.UnexpectedText";
    public static final String ERR_UNEXPECTED_ATTRIBUTE = // arg:1
        "Verifier.Error.UnexpectedAttribute";
    public static final String ERR_MISSING_ATTRIBUTE = // arg:1
        "Verifier.Error.MissingAttribute";
    public static final String ERR_UNEXPECTED_STARTTAG = // arg:1
        "Verifier.Error.UnexpectedStartTag";
    public static final String ERR_UNCOMPLETED_CONTENT = // arg:1
        "Verifier.Error.UncompletedContent";
    public static final String ERR_UNEXPECTED_ELEMENT = // arg:1
        "Verifier.Error.UnexpectedElement";
    public static final String ERR_UNSOLD_IDREF = // arg:1
        "Verifier.Error.UnsoldIDREF";
    public static final String ERR_DUPLICATE_ID = // arg:1
        "Verifier.Error.DuplicateId";
}
