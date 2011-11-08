package org.iso_relax.verifier;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.xml.sax.SAXException;

/**
 * VerifierException
 *
 * @since   Feb. 23, 2001
 * @version Mar.  4, 2001
 * @author  ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class VerifierException extends SAXException {

	public VerifierException(String message) {
	   super(message);
	}

    public VerifierException(Exception e) {
		super(e);
    }

    public VerifierException(String message, Exception e) {
		super(message,e);
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

		Exception cause = super.getException();
		if (cause != null) {
		    writer.println();
		    writer.println("StackTrace of Original Exception:");
		    cause.printStackTrace(writer);
		}
    }
}
