package jadex.commons.security;

/**
 *  Entropy source for seeding PRNGs.
 *
 */
public interface IEntropySource
{
	/**
	 *  Gets entropy from the source to fill the byte array.
	 *  
	 *  @param bytes The byte array to fill.
	 */
	public void getEntropy(byte[] bytes);
}
