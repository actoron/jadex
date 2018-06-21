package jadex.extension.ws.invoke;


/**
 *  Mapping information about the web service.
 *  Is used to determine at runtime how a web service
 *  method should be invoked.
 */
public class WebServiceMappingInfo
{
	//-------- attributes --------
	
	/** The porttype. */
	protected Class service;
	
	/** The porttype. */
	protected String porttype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new mapping.
	 */
	public WebServiceMappingInfo()
	{
	}
	
	/**
	 *  Create a new mapping.
	 */
	public WebServiceMappingInfo(Class service, String porttype)
	{
		this.service = service;
		this.porttype = porttype;
	}

	//-------- methods --------
	
	/**
	 *  Get the service.
	 *  @return the service.
	 */
	public Class getService()
	{
		return service;
	}

	/**
	 *  Set the service.
	 *  @param service The service to set.
	 */
	public void setService(Class service)
	{
		this.service = service;
	}
	
	/**
	 *  Get the porttype.
	 *  @return the porttype.
	 */
	public String getPortType()
	{
		return porttype;
	}

	/**
	 *  Set the porttype.
	 *  @param porttype The porttype to set.
	 */
	public void setPortType(String porttype)
	{
		this.porttype = porttype;
	}
	
}
