package jadex.bridge.service;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;


/**
 *  Service identifier for uniquely identifying a service.
 *  Is composed of the container id and the service name.
 */
public class ServiceIdentifier implements IServiceIdentifier
{
	//-------- attributes --------
	
	/** The provider identifier. */
	protected IComponentIdentifier providerid;
		
	/** The service name. */
	protected String servicename;
	
	/** The service type. */
	protected ClassInfo type;

	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** The scope. */
	protected String scope;
	
	/** The string representation (cached for reducing memory consumption). */
	protected String tostring;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service identifier.
	 */
	public ServiceIdentifier()
	{
	}
	
	/**
	 *  Create a new service identifier.
	 */
	public ServiceIdentifier(IComponentIdentifier providerid, Class<?> type, String servicename, IResourceIdentifier rid, String scope)
	{
		this.providerid = providerid;
		this.type	= new ClassInfo(type);
		this.servicename = servicename;
		this.rid = rid;
		this.scope = scope;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service provider identifier.
	 *  @return The provider id.
	 */
	public IComponentIdentifier getProviderId()
	{
		return providerid;
	}
	
	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderId(IComponentIdentifier providerid)
	{
		this.providerid = providerid;
	}
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public ClassInfo getServiceType()
	{
		return type;
	}

	/**
	 *  Set the service type.
	 *  @param type The service type.
	 */
	public void	setServiceType(ClassInfo type)
	{
		this.type	= type;
	}

	/**
	 *  Get the service name.
	 *  @return The service name.
	 */
	public String getServiceName()
	{
		return servicename;
	}
	
	/**
	 *  Set the servicename.
	 *  @param servicename The servicename to set.
	 */
	public void setServiceName(String servicename)
	{
		this.servicename = servicename;
	}

	/** 
	 *  Get the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return rid;
	}

	/**
	 *  Set the resource identifier. 
	 *  @param rid The resource identifier.
	 */
	public void setReourceIdentifier(IResourceIdentifier rid)
	{
		this.rid = rid;
	}
	
	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public String getScope()
	{
//		return scope==null? RequiredServiceInfo.SCOPE_GLOBAL: scope;
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set.
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	/**
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((providerid == null) ? 0 : providerid.hashCode());
		result = prime * result + ((servicename == null) ? 0 : servicename.hashCode());
		return result;
	}

	/**
	 *  Test if an object is equal to this one.
	 *  @param obj The object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof IServiceIdentifier)
		{
			IServiceIdentifier sid = (IServiceIdentifier)obj;
			ret = sid.getProviderId().equals(getProviderId()) && sid.getServiceName().equals(getServiceName());
		}
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		if(tostring==null)
		{
			tostring	= getServiceName()+"@"+getProviderId();
//		return "ServiceIdentifier(providerid=" + providerid + ", type=" + type
//				+ ", servicename=" + servicename + ")";
		}
		return tostring;
	}
}
