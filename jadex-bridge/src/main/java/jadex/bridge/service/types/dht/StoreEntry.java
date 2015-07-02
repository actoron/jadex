package jadex.bridge.service.types.dht;

import jadex.commons.Tuple3;

/**
 * Entry in the storage map containing ID (hash) and value.
 */
public class StoreEntry
{

	private Tuple3<IID, String, Object> content;

	/**
	 * Empty constructor.
	 */
	public StoreEntry()
	{
	}
	
	/**
	 * Constructor.
	 * 
	 * @param hash
	 * @param value
	 */
	public StoreEntry(IID hash, String key, Object value)
	{
		content = new Tuple3<IID, String, Object>(hash, key, value);
	}

	/**
	 * Copy Constructor.
	 */
//	public StoreEntry(StoreEntry entry)
//	{
//		this(entry.getIdHash(), entry.getKey(), entry.getValue());
//	}

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
	public Object getValue()
	{
		return content.getThirdEntity();
	}

	/**
	 * Get Content for serialization.
	 * @return content
	 */
	public Tuple3<IID, String, Object> getContent()
	{
		return content;
	}

	/**
	 * Set content for serialization.
	 * @param content
	 */
	public void setContent(Tuple3<IID, String, Object> content)
	{
		this.content = content;
	}
	
	
}