package com.ctc.wstx.exc;

/**
 * Exception class used for notifying about well-formedness errors that
 * writers would create. Such exceptions are thrown when strict output
 * validation is enabled.
 */
public class WstxOutputException
    extends WstxException
{
    private static final long serialVersionUID = 1L;

    public WstxOutputException(String msg) {
        super(msg);
    }
}
