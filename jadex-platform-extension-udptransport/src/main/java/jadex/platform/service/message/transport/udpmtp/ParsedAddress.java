package jadex.platform.service.message.transport.udpmtp;

/**
 *  This class represents a parsed address.
 *
 */
public class ParsedAddress
{
	/** The schema. */
	protected String schema;
	
	/** The hostname. */
	protected String hostname;
	
	/** The port. */
	protected int port;
	
	private ParsedAddress(String schema, String address) throws Exception
	{
		this.schema = schema;
		int colpos = address.lastIndexOf(":");
		hostname = address.substring(schema.length(), colpos);
		port = Integer.parseInt(address.substring(colpos + 1));
	}
	
	
	
	/**
	 *  Gets the schema.
	 *
	 *  @return The schema.
	 */
	public String getSchema()
	{
		return schema;
	}

	/**
	 *  Gets the hostname.
	 *
	 *  @return The hostname.
	 */
	public String getHostname()
	{
		return hostname;
	}

	/**
	 *  Gets the port.
	 *
	 *  @return The port.
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 *  Generates the hash code.
	 */
	public int hashCode()
	{
		int ret = schema.hashCode();
		ret *= 37; 
		ret += hostname.hashCode();
		ret *= 37;
		ret += port;
		return ret;
	}
	
	/**
	 *  Tests for equality.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof ParsedAddress)
		{
			ParsedAddress other = (ParsedAddress) obj;
			return schema.equals(other.schema) && hostname.equals(other.hostname) && port == other.port;
		}
		return false;
	}
	
	public static final ParsedAddress parseAddress(String address)
	{
		ParsedAddress ret = null;
		String schema = getSchema(address);
		if (schema != null)
		{
			try
			{
				ret = new ParsedAddress(schema, address);
			}
			catch (Exception e)
			{
			}
		}
		return ret;
	}

	/**
	 *  Extracts the schema of an address.
	 *  
	 *  @param address The address.
	 *  @return	The schema.
	 */
	protected static final String getSchema(String address)
	{
		for (int i = 0; i < UDPTransport.SCHEMAS.length; ++i)
		{
			if (address.startsWith(UDPTransport.SCHEMAS[i]))
				return UDPTransport.SCHEMAS[i];
		}
		return null;
	}
}
