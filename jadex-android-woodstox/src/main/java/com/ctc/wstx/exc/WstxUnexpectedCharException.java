package com.ctc.wstx.exc;

import javaxx.xml.stream.Location;

/**
 * Generic exception type that indicates that tokenizer/parser encountered
 * unexpected (but not necessarily invalid per se) character; character that
 * is not legal in current context. Could happen, for example, if white space
 * was missing between attribute value and name of next attribute.
 */
public class WstxUnexpectedCharException
    extends WstxParsingException
{
    private static final long serialVersionUID = 1L;

    final char mChar;

    public WstxUnexpectedCharException(String msg, Location loc, char c) {
        super(msg, loc);
        mChar = c;
    }

    public char getChar() {
        return mChar;
    }
}
