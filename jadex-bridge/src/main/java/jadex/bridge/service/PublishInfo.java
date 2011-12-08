package jadex.bridge.service;

import jadex.bridge.ClassInfo;

/**
 *  Info struct for service publishing details.
 */
public class PublishInfo
{
	//-------- attributes --------
	
	/** The publish id. */
	protected String pid;
	
	/** The publish type. */
	protected String publishtype;
	
	/** The service type. */
	protected ClassInfo servicetype;

	//-------- constructors --------

	/**
	 *  Create a new publish info.
	 */
	public PublishInfo()
	{
	}

	/**
	 *  Create a new publish info.
	 *  @param pid The publish id, e.g. url.
	 *  @param publishtype The publish type.
	 */
	public PublishInfo(String pid, String publishtype, Class servicetype)
	{
		this.pid = pid;
		this.publishtype = publishtype;
		this.servicetype = new ClassInfo(servicetype);
	}

	//-------- methods --------

	/**
	 *  Get the publishid.
	 *  @return the publishid.
	 */
	public String getPublishId()
	{
		return pid;
	}

	/**
	 *  Set the publishid.
	 *  @param publishid The publishid to set.
	 */
	public void setPublishId(String pid)
	{
		this.pid = pid;
	}

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getPublishType()
	{
		return publishtype;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setPublishType(String type)
	{
		this.publishtype = type;
	}

	/**
	 *  Get the servicetype.
	 *  @return The servicetype.
	 */
	public ClassInfo getServiceType()
	{
		return servicetype;
	}

	/**
	 *  Set the servicetype.
	 *  @param servicetype The servicetype to set.
	 */
	public void setServiceType(ClassInfo servicetype)
	{
		this.servicetype = servicetype;
	}
}
