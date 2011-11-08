/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.regex.RegExp;
import com.sun.msv.datatype.xsd.regex.RegExpFactory;
import org.relaxng.datatype.ValidationContext;

import java.text.ParseException;

/**
 * "anyURI" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#anyURI for the spec.
 * type of the value object is <code>java.lang.String</code>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyURIType extends BuiltinAtomicType implements Discrete {

    public static final AnyURIType theInstance = new AnyURIType();
    private AnyURIType() {
        super("anyURI");
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        return regexp.matches(escape(content));
    }

    private static void appendHex(StringBuffer buf, int hex) {
        if (hex < 10)
            buf.append((char) (hex + '0'));
        else
            buf.append((char) (hex - 10 + 'A'));
    }

    private static void appendByte(StringBuffer buf, int ch) {
        buf.append('%');
        appendHex(buf, ch / 16);
        appendHex(buf, ch % 16);
    }

    /** convert one 'char' in BMP to UTF-8 encoding. */
    private static void appendEscaped(StringBuffer buf, char ch) {
        if (ch < 0x7F) {
            appendByte(buf, (int)ch);
            return;
        }

        if (ch < 0x7FF) {
            appendByte(buf, 0xC0 + (ch >> 6));
            appendByte(buf, 0x80 + (ch % 64));
            return;
        }

        if (ch < 0xFFFF) {
            appendByte(buf, 0xE0 + (ch >> 12));
            appendByte(buf, 0x80 + ((ch >> 6) % 64));
            appendByte(buf, 0x80 + (ch % 64));
        }
    }

    /** convert one surrogate pair to UTF-8 encoding. */
    private static void appendEscaped(StringBuffer buf, char ch1, char ch2) {
        int ucs = (((int) (ch1 & 0x3FF)) << 10) + (ch2 & 0x3FF);

        appendByte(buf, 0xF0 + (ucs >> 18));
        appendByte(buf, 0x80 + ((ucs >> 12) % 64));
        appendByte(buf, 0x80 + ((ucs >> 6) % 64));
        appendByte(buf, 0x80 + (ucs % 64));
    }

    /**
     * a table that indicates whether a particular character has to be
     * escaped or not. false indicates it has to be escaped.
     * this table is of length 128.
     */
    private static final boolean[] isUric = createUricMap();

    private static boolean[] createUricMap() {
        boolean r[] = new boolean[128];

        for (int i = 'a'; i <= 'z'; i++)
            r[i] = true;
        for (int i = 'A'; i <= 'Z'; i++)
            r[i] = true;
        for (int i = '0'; i <= '9'; i++)
            r[i] = true;

        char[] mark = new char[] { '-', '_', '.', '!', '~', '*', '\'', '(', ')', '#', '%', '[', ']' };
        for (int i = 0; i < mark.length; i++)
            r[mark[i]] = true;

        char[] reserved = new char[] { ';', '/', '?', ':', '@', '&', '=', '+', '$', ',' };
        for (int i = 0; i < reserved.length; i++)
            r[reserved[i]] = true;

        return r;
    }

    /** escape non-ASCII characters in URL */
    public static String escape(String content) {
        StringBuffer escaped = new StringBuffer(content.length());

        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (ch < 128 && isUric[ch])
                escaped.append(ch);
            else {
                // escape it
                if (0xD800 <= ch && ch < 0xDC00) // surrogate pair
                    appendEscaped(escaped, ch, content.charAt(++i));
                else // other characters.
                    appendEscaped(escaped, ch);
            }
        }
        return new String(escaped);
    }

    final static RegExp regexp = createRegExp();

    static RegExp createRegExp() {
        String alpha = "[a-zA-Z]";
        String alphanum = "[0-9a-zA-Z]";
        String hex = "[0-9a-fA-F]";
        String escaped = "%" + hex + "{2}";
        String mark = "[\\-_\\.!~\\*'\\(\\)]";
        String unreserved = "(" + alphanum + "|" + mark + ")";
        String reserved = "[;/\\?:@&=\\+$,\\[\\]]";
        String uric = "(" + reserved + "|" + unreserved + "|" + escaped + ")";
        String fragment = uric + "*";
        String query = uric + "*";
        String pchar = "(" + unreserved + "|" + escaped + "|[:@&=\\+$,])";
        String param = pchar + "*";
        String segment = "(" + param + "(;" + param + ")*)";
        String pathSegments = "(" + segment + "(/" + segment + ")*)";
        String port = "[0-9]*";
        String __upTo3digits = "[0-9]{1,3}";
        String IPv4address = __upTo3digits + "\\." + __upTo3digits + "\\." + __upTo3digits + "\\." + __upTo3digits;
        String hex4 = hex + "{1,4}";
        String hexseq = hex4 + "(:" + hex4 + ")*";
        String hexpart = "((" + hexseq + "(::(" + hexseq + ")?)?)|(::(" + hexseq + ")?))";
        String IPv6address = "((" + hexpart + "(:" + IPv4address + ")?)|(::" + IPv4address + "))";
        String IPv6reference = "\\[" + IPv6address + "\\]";
        String domainlabel = alphanum + "([0-9A-Za-z\\-]*" + alphanum + ")?";
        String toplabel = alpha + "([0-9A-Za-z\\-]*" + alphanum + ")?";
        String hostname = "(" + domainlabel + "\\.)*" + toplabel + "(\\.)?";
        String host = "((" + hostname + ")|(" + IPv4address + ")|(" + IPv6reference + "))";
        String hostport = host + "(:" + port + ")?";
        String userinfo = "(" + unreserved + "|" + escaped + "|[;:&=\\+$,])*";
        String server = "((" + userinfo + "@)?" + hostport + ")?";
        String regName = "(" + unreserved + "|" + escaped + "|[$,;:@&=\\+])+";
        String authority = "((" + server + ")|(" + regName + "))";
        String scheme = alpha + "[A-Za-z0-9\\+\\-\\.]*";
        String relSegment = "(" + unreserved + "|" + escaped + "|[;@&=\\+$,])+";
        String absPath = "/" + pathSegments;
        String relPath = relSegment + "(" + absPath + ")?";
        String netPath = "//" + authority + "(" + absPath + ")?";
        String uricNoSlash = "(" + unreserved + "|" + escaped + "|[;\\?:@&=\\+$,])";
        String opaquePart = uricNoSlash + "(" + uric + ")*";
        String hierPart = "((" + netPath + ")|(" + absPath + "))(\\?" + query + ")?";
        //        String path            = "(("+absPath+")|("+opaquePart+"))?";
        String relativeURI = "((" + netPath + ")|(" + absPath + ")|(" + relPath + "))(\\?" + query + ")?";
        String absoluteURI = scheme + ":((" + hierPart + ")|(" + opaquePart + "))";
        String uriRef = "(" + absoluteURI + "|" + relativeURI + ")?(#" + fragment + ")?";
        try {
            return RegExpFactory.createFactory().compile(uriRef);
        } catch (ParseException e) {
            // impossible
            throw new Error();
        }
    }

    public Object _createValue(final String content, ValidationContext context) {
        // we can't use java.net.URL (for example, it cannot handle IPv6.)
        if (!regexp.matches(escape(content)))
            return null;

        // the value space and the lexical space is the same.
        // escaped characters are only used for validation.
        return content;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof String)
            return (String)value;
        else
            throw new IllegalArgumentException();
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(FACET_LENGTH)
            || facetName.equals(FACET_MINLENGTH)
            || facetName.equals(FACET_MAXLENGTH)
            || facetName.equals(FACET_PATTERN)
            || facetName.equals(FACET_WHITESPACE)
            || facetName.equals(FACET_ENUMERATION))
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }

    public final int countLength(Object value) {
        // the spec does not define this.
        // TODO: check the update of the spec and modify this if necessary.
        return UnicodeUtil.countLength((String)value);
    }
    public Class getJavaObjectType() {
        return String.class;
    }
    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    // serialization support
    private static final long serialVersionUID = 1;
}
