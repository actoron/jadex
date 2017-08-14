package jadex.bridge.service.types.address;

import jadex.bridge.IComponentIdentifier;

/**
 *  Class representing a transport address of a specific platform.
 */
public class TransportAddress
{
	/** ID of the platform. */
	protected IComponentIdentifier platformid;
	
	/** Type of transport the address supports. */
	protected String transporttype;
	
	/** The address. */
	protected String address;
	
	/**
	 *  Bean constructor.
	 */
	public TransportAddress()
	{
	}
	
	/**
	 *  Creates the address.
	 * 
	 *  @param platformid The platform ID.
	 *  @param transporttype The type of transport.
	 *  @param address The address.
	 */
	public TransportAddress(IComponentIdentifier platformid, String transporttype, String address)
	{
		this.platformid = platformid;
		this.transporttype = transporttype;
		this.address = address;
	}
	
	/**
	 *  Gets the ID of the platform owning the address.
	 * 
	 *  @return The ID of the platform owning the address.
	 */
	public IComponentIdentifier getPlatformId()
	{
		return platformid;
	}
	
	/**
	 *  Sets the ID of the platform owning the address.
	 * 
	 *  @param platformid The ID of the platform owning the address.
	 */
	public void setPlatformId(IComponentIdentifier platformid)
	{
		this.platformid = platformid;
	}
	
	/**
	 *  Gets the type of transport using the address.
	 *  
	 *  @return The type of transport.
	 */
	public String getTransportType()
	{
		return transporttype;
	}
	
	/**
	 *  Sets the type of transport using the address.
	 *  
	 *  @param transporttype The type of transport.
	 */
	public void setTransportYype(String transporttype)
	{
		this.transporttype = transporttype;
	}

	/**
	 *  Gets the address.
	 *  
	 *  @return The address.
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 *  Sets the address.
	 *
	 *  @param address The address.
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
}
