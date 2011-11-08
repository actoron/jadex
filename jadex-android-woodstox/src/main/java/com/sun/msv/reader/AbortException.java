/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

/**
 * This exception will be thrown when the schema parsing is aborted
 * after all the errors are reported through GrammarReaderController.
 * 
 * To throw this exception, the thrower must report an error to
 * the controller. This exception can be catched anytime by anyone
 * who can recover from the error.
 * The receiver shouldn't report this exception to the application,
 * since it has already been reported by the thrower.
 */
@SuppressWarnings("serial")
public class AbortException extends Exception
{
    private AbortException() {
        super("aborted. Errors should have been reported");
    }
    
    public static final AbortException theInstance = new AbortException();

/*    
    private final Exception nestedException;
    
    public Exception getNestedException() { return nestedException; }
    
    public void printStackTrace( java.io.PrintWriter out ) {
        super.printStackTrace(out);
        if(nestedException!=null) {
            out.println("nested exception:");
            nestedException.printStackTrace(out);
        }
    }
*/
}
