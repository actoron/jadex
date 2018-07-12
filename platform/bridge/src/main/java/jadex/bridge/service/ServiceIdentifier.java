package jadex.bridge.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;


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
	
	/** The service super types. */
	protected ClassInfo[] supertypes;

	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** The scope. */
	protected String scope;
	
	/** The network names (shared object with security service). */
	protected Set<String> networknames;
	
	/** Is the service unrestricted. */
	protected boolean unrestricted;
	
	/** The string representation (cached for reducing memory consumption). */
	protected String tostring;
	
	/** The tags. */
	protected Set<String> tags;
	
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
	public ServiceIdentifier(IInternalAccess provider, Class<?> type, String servicename, IResourceIdentifier rid, String scope)
	{
//		if(!type.isInterface())
//		{
//			System.out.println("dreck");
//		}
		
		List<ClassInfo> superinfos = new ArrayList<ClassInfo>();
		for(Class<?> sin: SReflect.getSuperInterfaces(new Class[]{type}))
		{
			if(sin.isAnnotationPresent(Service.class))
			{
				superinfos.add(new ClassInfo(sin));
			}
		}
		this.providerid = provider.getId();
		this.type	= new ClassInfo(type);
		this.supertypes = superinfos.toArray(new ClassInfo[superinfos.size()]);
		this.servicename = servicename;
		this.rid = rid;
		this.scope = scope;
		@SuppressWarnings("unused")
		Set<String>	networknames = (Set<String>)Starter.getPlatformValue(providerid, Starter.DATA_NETWORKNAMESCACHE);
		this.networknames	= networknames;
		this.unrestricted = isUnrestricted(provider, type);
	}
	
	/**
	 *  Create a new service identifier.
	 */
	public ServiceIdentifier(IComponentIdentifier providerid, ClassInfo type, ClassInfo[] supertypes, String servicename, IResourceIdentifier rid, String scope, Set<String> networknames, boolean unrestricted)
	{
		this.providerid = providerid;
		this.type	= type;
		this.supertypes = supertypes;
		this.servicename = servicename;
		this.rid = rid;
		this.scope = scope;
		this.networknames = networknames;
		this.unrestricted = unrestricted;
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
	 *  Get the service super types.
	 *  @return The service super types.
	 */
	public ClassInfo[] getServiceSuperTypes()
	{
		return supertypes;
	}

	/**
	 *  Set the service super types.
	 *  @param type The service super types.
	 */
	public void	setServiceSuperTypes(ClassInfo[] supertypes)
	{
		this.supertypes	= supertypes;
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
	 *  Get the network names.
	 *  @return the network names
	 */
	public Set<String> getNetworkNames()
	{
		return networknames;
	}

	/**
	 *  Set the network names.
	 *  @param networknames The network names to set
	 */
	public void setNetworkNames(Set<String> networknames)
	{
		this.networknames = networknames;
	}
	
	/**
	 *  Check if the service has unrestricted access. 
	 *  @return True, if it is unrestricted.
	 */
	public boolean isUnrestricted()
	{
		return unrestricted;
	}
	
	/**
	 *  Set the unrestricted flag.
	 *  @param unrestricted The unrestricted flag.
	 */
	public void setUnrestricted(boolean unrestricted) 
	{
		this.unrestricted = unrestricted;
	}
	
	/**
	 *  Get the service tags.
	 *  @return The tags.
	 */
	public Set<String> getTags()
	{
		return tags;
	}
	
	/**
	 *  Set the tags.
	 *  @param tags the tags to set
	 */
	public void setTags(Set<String> tags)
	{
		this.tags = tags;
	}

	/**
	 *  Test if the service is a system service.
	 *  Checks wether the system property is set in properties annotation.
	 *  @param iftype The interface type. 
	 */
	public static boolean isSystemService(Class<?> iftype)
	{
		// Hack cast
		//Class<?> itype = psi.getType().getType();
		boolean ret = false;
		if(iftype!=null)
		{
			Service ser = iftype.getAnnotation(Service.class);
			if(ser!=null && ser.system())
			{
				ret = true;
			}
			
//			Properties[] props = iftype.getAnnotationsByType(Properties.class);
//			for(Properties ps: props)
//			{
//				for(NameValue nv: ps.value())
//				{
//					if(nv.name().equals("system"))
//					{
//						Boolean res = (Boolean)SJavaParser.evaluateExpression(nv.value(), null);
//						if(res!=null)
//							ret = res.booleanValue();
//						break;
//					}
//				}
//			}
		}
		return ret;
	}
	
	/**
	 *  Method to provide the security level.
	 */
	public static Security getSecurityLevel(Class<?> ctype)
	{
		return ctype!=null ? ctype.getAnnotation(Security.class) : null;
	}
	
	/**
	 *  Is the service unrestricted.
	 *  @param access The access.
	 *  @param ctype The service interface.
	 *  @return True, if is unrestricted.
	 */
	public static boolean isUnrestricted(IInternalAccess access, ClassInfo ctype)
	{
		Set<String>	roles	= getRoles(getSecurityLevel(ctype.getType(access.getClassLoader(), access.getModel().getAllImports())), access);
		return roles!=null && roles.contains(Security.UNRESTRICTED);
	}
	
	/**
	 *  Is the service unrestricted.
	 *  @param access The access.
	 *  @param ctype The service interface.
	 *  @return True, if is unrestricted.
	 */
	public static boolean isUnrestricted(IInternalAccess access, Class<?> ctype)
	{
		Set<String>	roles	= getRoles(getSecurityLevel(ctype), access);
		return roles!=null && roles.contains(Security.UNRESTRICTED);
	}
	
	/**
	 *  Get the roles from an annotation.
	 *  @param sec	The security annotation or null.
	 *  @param provider	The component that owns the service.
	 *  @return The roles, if any or null, if none given or sec==null.
	 */
	public static Set<String>	getRoles(Security sec, IInternalAccess provider)
	{
		Set<String>	ret	= null;
		String[]	roles	= sec!=null ? sec.roles() : null;
		if(roles!=null && roles.length>0)
		{
			// Evaluate, if a role is given as expression.
			ret	= new HashSet<String>();
			for(String role: roles)
			{
				ret.add((String)SJavaParser.evaluateExpressionPotentially(role, provider.getModel().getAllImports(), provider.getFetcher(), provider.getClassLoader()));
			}
		}
		return ret;
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
