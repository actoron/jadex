package jadex.bridge.service.types.dht;

import jadex.commons.Tuple3;

/**
 * Entry in the storage map containing ID (hash) and value.
 */
public class StoreEntry
{

	private Tuple3<IID, String, String> content;
	
	/**
	 * Constructor.
	 * 
	 * @param hash
	 * @param value
	 */
	public StoreEntry(IID hash, String key, String value)
	{
		content = new Tuple3<IID, String, String>(hash, key, value);
	}

	/**
	 * Get the hash.
	 * 
	 * @return
	 */
	public IID getIdHash()
	{
		return content.getFirstEntity();
	}
	
	/**
	 * Get the key.
	 * 
	 * @return
	 */
	public String getKey()
	{
		return content.getSecondEntity();
	}

	/**
	 * Get the value.
	 * 
	 * @return
	 */
	public String getValue()
	{
		return content.getThirdEntity();
	}

	/**
	 * Get Content for serialization.
	 * @return content
	 */
	public Tuple3<IID, String, String> getContent()
	{
		return content;
	}

	/**
	 * Set content for serialization.
	 * @param content
	 */
	public void setContent(Tuple3<IID, String, String> content)
	{
		this.content = content;
	}
	
	
}