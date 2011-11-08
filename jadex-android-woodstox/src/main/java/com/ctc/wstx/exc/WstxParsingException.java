package com.ctc.wstx.exc;

import javaxx.xml.stream.Location;

/**
 * Intermediate base class for reporting actual Wstx parsing problems.
 */
public class WstxParsingException
    extends WstxException
{
    private static final long serialVersionUID = 1L;

    public WstxParsingException(String msg, Location loc) {
        super(msg, loc);
    }

    // !!! 13-Sep-2008, tatus: Only needed for DOMWrapping reader, for now
    public WstxParsingException(String msg) { super(msg); }
}
