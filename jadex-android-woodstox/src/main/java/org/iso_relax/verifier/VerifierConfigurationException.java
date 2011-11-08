package org.iso_relax.verifier;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * VerifierConfigurationException
 *
 * @since   Feb. 23, 2001
 * @version Apr. 17, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class VerifierConfigurationException extends Exception {
    private Exception cause_ = null;

    public VerifierConfigurationException(String message) {
	super(message);
    }

    public VerifierConfigurationException(Exception e) {
	super(e.getMessage());
	cause_ = e;
    }

    public VerifierConfigurationException(String message, Exception e) {
	super(message);
	cause_ = e;
    }

    public Exception getException() {
	if (cause_ != null) {
	    return (cause_);
	} else {
	    return (this);
	}
    }

    public Exception getCauseException() {
	return (cause_);
    }

    public void printStackTrace() {
	printStackTrace(new PrintWriter(System.err, true));
    }

    public void printStackTrace(PrintStream out) {
	printStackTrace(new PrintWriter(out));
    }

    public void printStackTrace(PrintWriter writer) {
	if (writer == null) {
	    writer = new PrintWriter(System.err, true);
	}
	super.printStackTrace(writer);
	if (cause_ != null) {
	    writer.println();
	    writer.println("StackTrace of Original Exception:");
	    cause_.printStackTrace(writer);
	}
    }
}
