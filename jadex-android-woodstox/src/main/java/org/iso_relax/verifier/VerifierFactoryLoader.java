package org.iso_relax.verifier;

/**
 * A class that provides information about the verifier implementation.
 *
 * <p>
 * Implementations of this interface are discovered through
 * <code>META-INF/services</code>, just like JAXP. This object then
 * provides VerifierFactory implementation for the specified schema language.
 * 
 * @author	<a href="mailto:kohsukekawaguchi@yahoo.com">Kohsuke KAWAGUCHI</a>
 */
public interface VerifierFactoryLoader {
	
	/**
	 * returns a VerifierFactory that supports the specified schema language,
	 * or returns null if it's not supported.
	 */
	VerifierFactory createFactory( String schemaLanguage );
}
