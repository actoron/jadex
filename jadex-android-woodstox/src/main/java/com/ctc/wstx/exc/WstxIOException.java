package com.ctc.wstx.exc;

import java.io.IOException;

/**
 * Simple wrapper for {@link IOException}s; needed when StAX does not expose
 * underlying I/O exceptions via its methods.
 */
public class WstxIOException
    extends WstxException
{
    private static final long serialVersionUID = 1L;

    public WstxIOException(IOException ie) {
        super(ie);
    }

    public WstxIOException(String msg) {
        super(msg);
    }
}
