package org.iso_relax.verifier;

/**
 * The compiled representation of schemas.
 * 
 * <p>
 * <code>Schema</code> object must be thread-safe; multiple-threads can access
 * one <code>Schema</code> obejct at the same time.
 * 
 * <p>
 * The schema object allows an application to "cache" a schema by compiling it
 * once and using it many times, possibly by different threads.
 */
public interface Schema {
	
	/**
	 * creates a new Verifier object that validates documents with this schema.
	 * 
	 * @return
	 *		a valid non-null instance of a Verifier.
	 */
	Verifier newVerifier() throws VerifierConfigurationException;
}
