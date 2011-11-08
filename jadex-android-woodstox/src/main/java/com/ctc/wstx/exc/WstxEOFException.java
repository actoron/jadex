package com.ctc.wstx.exc;

import javaxx.xml.stream.Location;

/**
 * Exception thrown during parsing, if an unexpected EOF is encountered.
 * Location usually signals starting position of current Node.
 */
public class WstxEOFException
    extends WstxParsingException
{
    private static final long serialVersionUID = 1L;

    public WstxEOFException(String msg, Location loc) {
        super(msg, loc);
    }
}
