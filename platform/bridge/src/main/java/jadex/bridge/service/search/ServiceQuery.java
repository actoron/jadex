package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple3;

/**
 *  Service query definition. T is the return type for search methods.
 */
public class ServiceQuery<T>
{
	//-------- query multiplicity --------
	
	/**
	 *  Define cases for multiplicity.
	 */
	public static class	Multiplicity
	{
		//-------- constants --------
		
		/** '0..1' multiplicity for single optional service. */
		public static Multiplicity	ZERO_ONE	= new Multiplicity(0, 1);
		
		/** '1' multiplicity for required service (default for searchService methods). */
		public static Multiplicity	ONE			= new Multiplicity(1, 1);
		
		/** '0..*' multiplicity for optional multi service (default for searchServices methods). */
		public static Multiplicity	ZERO_MANY	= new Multiplicity(0, -1);

		/** '1..*' multiplicity for required service (default for searchService methods). */
		public static Multiplicity	ONE_MANY	= new Multiplicity(1, -1);
		
		//-------- attributes --------
		
		/** The minimal number of services required. Otherwise search ends with ServiceNotFoundException. */
		private int	from;
		
		/** The maximal number of services returned. Afterwards search/query will terminate. */
		private int to;
		
		//-------- constructors --------

		/**
		 *  Bean constructor.
		 *  Not meant for direct use.
		 *  Defaults to invalid multiplicity ('0..0')!
		 */
		public Multiplicity()
		{
		}
		
		/**
		 *  Create a multiplicity.
		 *  @param from The minimal number of services for the search/query being considered successful (positive integer or 0).
		 *  @param to The maximal number of services returned by the search/query (positive integer or -1 for unlimited).
		 */
		public Multiplicity(int from, int to)
		{
			setFrom(from);
			setTo(to);
		}
		
		//-------- methods --------
		
		/**
		 *  Get the 'from' value, i.e. the minimal number of services required.
		 *  Otherwise search ends with ServiceNotFoundException. 
		 */
		public int getFrom()
		{
			return from;
		}
		
		/**
		 *  Set the 'from' value, i.e. the minimal number of services required.
		 *  Otherwise search ends with ServiceNotFoundException. 
		 *  @param from Positive integer or 0
		 */
		public void setFrom(int from)
		{
			if(from<0)
				throw new IllegalArgumentException("'from' must be a positive value or 0.");
				
			this.from = from;
		}
		
		/**
		 *  Get the 'to' value, i.e. The maximal number of services returned.
		 *  Afterwards search/query will terminate.
		 */
		public int getTo()
		{
			return to;
		}
		
		/**
		 *  Get the 'to' value, i.e. The maximal number of services returned.
		 *  Afterwards search/query will terminate.
		 *  @param to	Positive integer or -1 for unlimited.
		 */
		public void setTo(int to)
		{
			if(to!=-1 && to<1)
				throw new IllegalArgumentException("'to' must be a positive value or -1.");
			
			this.to = to;
		}
		
		/**
		 *  Get a string representation of the multiplicity.		
		 */
		@Override
		public String toString()
		{
			return from==to ? Integer.toString(from) : from + ".." + (to==-1 ? "*" : Integer.toString(to));
		}
	}
	
	//-------- constants --------
	
	/** Marker for networks not set. */
	//Hack!!! should not be public??? 
	public static final String[]	NETWORKS_NOT_SET	= new String[]{"__NETWORKS_NOT_SET__"};	// TODO: new String[0] for better performance, but unable to check remotely after marshalling!
	
	//-------- attributes --------
	
	/** The service type. */
	protected ClassInfo servicetype;
	
	/** Tags of the service. */
	protected String[] servicetags;
	
	/** The service provider. (rename serviceowner?) */
	//protected IComponentIdentifier provider;
	
	/** Starting point for the search scoping. */
	protected IComponentIdentifier searchstart;
	
	/** The service platform. (Find a service from another known platform, e.g. cms) */
	protected IComponentIdentifier platform;
	
	/** The service ID of the target service. Fast lookup of a service by id. */
	protected IServiceIdentifier serviceidentifier;
	
	/** The network names. */
	protected String[] networknames;
	
	/** Should the service be unrestricted. */
	protected Boolean unrestricted;
	
	/** The search scope. */
	protected String scope;
	
	/** The query owner. (rename queryowner?) */
	protected IComponentIdentifier owner;
	
	//-------- influence the result --------
	
	/** Flag, if service by the query owner should be excluded, i.e. do not return my own service. */
	protected boolean excludeowner;
	
	/** The multiple flag. Search for multiple services */
	protected Multiplicity	multiplicity;
	
	/** The return type. Tell registry to return services or service events. */
	protected ClassInfo returntype;
	
	/** The matching mode for multivalued terms. True is and and false is or. */
	protected Map<String, Boolean> matchingmodes;
	
	//-------- identification of a query --------
	
	/** The query id. Id is used for hashcode and equals and the same is used by servicequeryinfo class.
	    Allows for hashing queryies and use a queryinfo object for lookup. */
	protected String id;
	
//	/** Is the query prepared? Prepared means that the query is ready to be processed by the registry. */ 
//	protected boolean prepared;
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery()
	{
		// Not public to not encourage user to use it.
		// Here it does NOT set the networknames automatically because used for serialization.
	}
	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier owner)
//	{
//		this(servicetype, scope, (IFilter<T>) null, null, owner);
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(servicetype, scope, (IFilter<T>) null, provider, owner);
//	}
//	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(Class<T> servicetype)
	{
		this(servicetype, null, null, null);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(Class<T> servicetype, String scope)
	{
		this(servicetype, scope, null, null);
	}
	
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(Class<T> servicetype, String scope, IFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
////		this(new ClassInfo(servicetype), scope, filter, provider, owner);this.returntype = servicetype;
//		this.servicetype = new ClassInfo(servicetype);
//		this.returntype = this.servicetype;
//		// todo: what is the best place for this?
//		this.scope = scope==null && ServiceIdentifier.isSystemService(servicetype)? RequiredServiceInfo.SCOPE_PLATFORM: scope;
//		this.filter = filter;
//		this.provider = provider;
//		this.owner = owner;
//		
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this.returntype = servicetype;
//		this.servicetype = servicetype;
//		this.scope = scope;
//		this.filter = filter;
//		this.provider = provider;
//		this.owner = owner;
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(Class<T> returntype, Class<?> servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(new ClassInfo(returntype), new ClassInfo(servicetype), scope, filter, provider, owner);
//	}
	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo returntype, ClassInfo servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this.returntype = returntype;
//		this.servicetype = servicetype;
//		this.scope = scope;
//		this.filter = filter;
//		this.provider = provider;
//		this.owner = owner;
//	}
//	
//	/**
//	 *  Create a new service query.
//	 */
//	public ServiceQuery(ClassInfo servicetype, String scope, IAsyncFilter<T> filter, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(servicetype, servicetype, scope, filter, provider, owner);
////		this.returntype = servicetype;
////		this.servicetype = servicetype;
////		this.scope = scope;
////		this.filter = filter;
////		this.provider = provider;
////		this.owner = owner;
//	}
	
	/**
	 *  Create a new service query.
	 *  owner = startpoint
	 */
//	public ServiceQuery(Class<?> servicetype, String scope, IComponentIdentifier provider, IComponentIdentifier owner)
//	{
//		this(servicetype, scope, provider, owner, null);
//	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(Class<?> servicetype, String scope, IComponentIdentifier owner, Class<?> returntype)
	{
		this(servicetype!=null? new ClassInfo(servicetype): null, scope,
			owner, returntype!=null? new ClassInfo(returntype): null);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier owner)
	{
		this(servicetype, scope, owner, servicetype);
	}
	
	/**
	 *  Create a new service query.
	 */
	public ServiceQuery(ClassInfo servicetype, String scope, IComponentIdentifier owner, ClassInfo returntype)
	{
//		if(owner==null)
//			throw new IllegalArgumentException("Owner must not null");
		
		this.servicetype = servicetype;
		this.scope = scope;
		this.owner = owner;
		this.returntype = returntype;
		
		this.id = SUtil.createUniqueId();
		this.networknames = NETWORKS_NOT_SET;
	}
	
	/**
	 *  Shallow copy constructor.
	 *  @param original Original query.
	 */
	public ServiceQuery(ServiceQuery<T> original)
	{
		this.returntype = original.returntype;
		this.servicetype = original.servicetype;
		this.scope = original.scope;
		this.servicetags = original.servicetags;
		this.owner = original.owner;
		this.id = original.id;
		this.networknames = original.networknames;
		this.matchingmodes = original.matchingmodes;
		this.platform	= original.platform;
		this.searchstart	= original.searchstart;
		this.unrestricted = original.unrestricted;
	}

	/**
	 *  Get the service type.
	 *  @return The service type
	 */
	public ClassInfo getServiceType()
	{
		return servicetype;
	}

	/**
	 *  Set the service type.
	 *  @param type The service type to set
	 */
	public ServiceQuery<T> setServiceType(ClassInfo servicetype)
	{
		this.servicetype = servicetype;
		return this;
	}
	
	/**
	 *  Get the return type.
	 *  @return The return type
	 */
	public ClassInfo getReturnType()
	{
		return returntype;
	}

	/**
	 *  Set the return type.
	 *  @param type The return type to set
	 */
	public ServiceQuery<T> setReturnType(ClassInfo returntype)
	{
		this.returntype = returntype;
		return this;
	}
	
	/**
	 *  Get the scope.
	 *  @return The scope
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set
	 */
	public ServiceQuery<T> setScope(String scope)
	{
		this.scope = scope;
		return this;
	}
	
	/**
	 *  Gets the service tags.
	 *  
	 *  @return The service tags. 
	 */
	public String[] getServiceTags()
	{
		return servicetags;
	}
	
	/**
	 *  Sets the service tags.
	 *  @param servicetags The service tags. 
	 */
	public ServiceQuery<T> setServiceTags(String... servicetags)
	{
		TagProperty.checkReservedTags(servicetags);
		this.servicetags = servicetags;
		return this;
	}
	
	/**
	 *  Sets the service tags.
	 *  @param servicetags The service tags.
	 *  
	 *  todo: move or refactor to hide complexity!?
	 */
	public ServiceQuery<T> setServiceTags(String[] servicetags, IExternalAccess component)
	{
		this.servicetags = TagProperty.createRuntimeTags(servicetags, component).toArray(new String[servicetags!=null ? servicetags.length : 0]);
		return this;
	}

	/**
	 *  Set the provider.
	 *  @param provider The provider to set
	 */
	public ServiceQuery<T> setProvider(IComponentIdentifier provider)
	{
		this.searchstart = provider;
		this.scope = RequiredServiceInfo.SCOPE_COMPONENT_ONLY;
		return this;
	}
	
	/**
	 *  Get the provider.
	 *  @return The provider
	 */
	public IComponentIdentifier getSearchStart()
	{
		return searchstart;
	}

	/**
	 *  Set the provider.
	 *  @param provider The provider to set
	 */
	public ServiceQuery<T> setSearchStart(IComponentIdentifier searchstart)
	{
		this.searchstart = searchstart;
		return this;
	}
	
	/**
	 *  Get the platform.
	 *  @return The platform
	 */
	public IComponentIdentifier getPlatform()
	{
		return platform;
	}
	
	/**
	 *  Set the platform.
	 *  @param platform The platform
	 */
	public ServiceQuery<T> setPlatform(IComponentIdentifier platform)
	{
		this.platform = platform;
		return this;
	}
	
	/**
	 *  Gets the service identifier.
	 *
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return serviceidentifier;
	}

	/**
	 *  Sets the service identifier.
	 *
	 *  @param serviceidentifier The service identifier.
	 */
	public ServiceQuery<T> setServiceIdentifier(IServiceIdentifier serviceidentifier)
	{
		this.serviceidentifier = serviceidentifier;
		return this;
	}

	/**
	 *  Get the owner.
	 *  @return The owner
	 */
	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	/**
	 *  Set the owner.
	 *  @param owner The owner to set
	 */
	public ServiceQuery<T> setOwner(IComponentIdentifier owner)
	{
		this.owner = owner;
		return this;
	}
	
	/**
	 *  Checks if service of the query owner should be excluded.
	 *  
	 *  @return True, if the services should be excluded.
	 */
	public boolean isExcludeOwner()
	{
		return excludeowner;
	}
	
	/**
	 *  Sets if service of the query owner should be excluded.
	 *  
	 *  @param excludeowner True, if the services should be excluded.
	 */
	public ServiceQuery<T> setExcludeOwner(boolean excludeowner)
	{
		this.excludeowner = excludeowner;
		return this;
	}
	
	/**
	 *  Get the id.
	 *  @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set
	 */
	public ServiceQuery<T> setId(String id)
	{
		this.id = id;
		return this;
	}
	
	/**
	 *  Get the multiplicity.
	 *  @return the multiplicity
	 */
	public Multiplicity	getMultiplicity()
	{
		return multiplicity;
	}

	/**
	 *  Set the multiplicity.
	 *  @param multiplicity The multiplicity to set
	 */
	public ServiceQuery<T> setMultiplicity(Multiplicity multiplicity)
	{
		this.multiplicity = multiplicity;
		return this;
	}

	/**
	 *  Gets the specification for the indexer.
	 *  Query needs to be enhanced before calling this method. See RequiredServiceFeature.enhanceQuery()
	 *  
	 *  @return The specification for the indexer.
	 */
	public List<Tuple3<String, String[], Boolean>> getIndexerSearchSpec()
	{
		List<Tuple3<String, String[], Boolean>> ret = new ArrayList<Tuple3<String,String[],Boolean>>();
		
		if(platform != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_PLATFORM, new String[]{platform.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_PLATFORM)));
		
		if(RequiredServiceInfo.SCOPE_COMPONENT_ONLY.equals(scope))
		{
			if (searchstart != null)
				ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_PROVIDER, new String[]{searchstart.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_PROVIDER)));
			else
				ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_PROVIDER, new String[]{owner.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_PROVIDER)));
		}
		
		if(servicetype != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_INTERFACE, new String[]{servicetype.getGenericTypeName()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_INTERFACE)));
		
		if(servicetags != null && servicetags.length > 0)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_TAGS, servicetags, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_TAGS)));
		
		if(serviceidentifier != null)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_SID, new String[]{serviceidentifier.toString()}, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_SID)));
		
		assert !Arrays.equals(networknames, NETWORKS_NOT_SET) : "Problem: query not enhanced before processing.";
	
		if(networknames != null && networknames.length > 0)
			ret.add(new Tuple3<String, String[], Boolean>(ServiceKeyExtractor.KEY_TYPE_NETWORKS, networknames, getMatchingMode(ServiceKeyExtractor.KEY_TYPE_NETWORKS)));
		
		return ret;
	}
	
	
		
	/**
	 *  Get the matching mode for a key.
	 *  @param key The key name.
	 *  @return True for and, false for or.
	 */
	public Boolean getMatchingMode(String key)
	{
		return matchingmodes!=null? matchingmodes.get(key): null;
	}
	
	/**
	 *  Set a matching mode.
	 *  @param key The key name.
	 *  @param and True for and.
	 */
	public ServiceQuery<T> setMatchingMode(String key, Boolean and)
	{
		if(matchingmodes==null)
			matchingmodes = new HashMap<String, Boolean>();
		matchingmodes.put(key, and);
		return this;
	}
	
	
	/**
	 *  Get the unrestricted mode.
	 *  @return The unrestricted mode.
	 */
	public Boolean isUnrestricted()
	{
		return unrestricted;
	}

	/**
	 *  Set the unrestricted mode.
	 *  @param unrestricted the unrestricted to set
	 */
	public ServiceQuery<T> setUnrestricted(Boolean unrestricted)
	{
		this.unrestricted = unrestricted;
		return this;
	}

	/**
	 *  Tests if the query keys matches a service.
	 *  
	 *  @param service The service.
	 *  @return True, if the service matches the keys.
	 */
	protected boolean matchesKeys(IServiceIdentifier service)
	{
		if (servicetype != null && !service.getServiceType().getGenericTypeName().equals(servicetype))
			return false;
		
		if (servicetags != null)
		{
			Set<String> tagsset = ServiceKeyExtractor.getKeysStatic(ServiceKeyExtractor.KEY_TYPE_TAGS, service);
			if (tagsset == null)
				return false;
			
			for (String tag : servicetags)
			{
				if (!tagsset.contains(tag))
				{
					return false;
				}
			}
		}
		
		if (RequiredServiceInfo.SCOPE_COMPONENT_ONLY.equals(scope) &&
			!((searchstart != null && service.getProviderId().equals(searchstart)) ||
			service.getProviderId().equals(owner)))
			return false;
		
//		if (provider != null && !provider.equals(service.getProviderId()))
//			return false;
		
		if (platform != null && !platform.equals(service.getProviderId().getRoot()))
			return false;
		
		return true;
	}
	
//	/**
//	 *  Get the prepared.
//	 *  @return the prepared
//	 */
//	public boolean isPrepared()
//	{
//		return prepared;
//	}
//
//	/**
//	 *  Set the prepared.
//	 *  @param prepared The prepared to set
//	 */
//	public void setPrepared(boolean prepared)
//	{
//		this.prepared = prepared;
//	}
	
//	/**
//	 *  Prepare the query.
//	 */
//	public void prepare(IComponentIdentifier cid)
//	{
//		if(!prepared)
//		{
//			networknames = getNetworkNames(cid);
//			prepared = true;
//		}
//	}
	
	
	/**
	 *  Get the networknames.
	 *  @return the networknames
	 */
	public String[] getNetworkNames()
	{
		return networknames;
	}

	/**
	 *  Set the networknames.
	 *  @param networknames The networknames to set
	 */
	public ServiceQuery<T> setNetworkNames(String... networknames)
	{
		this.networknames = networknames;
		return this;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return id.hashCode()*13;
	}

	/**
	 *  Test if other object equals this one.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof ServiceQuery)
		{
			ServiceQuery<?> other = (ServiceQuery<?>)obj;
			ret = SUtil.equals(getId(), other.getId());
		}
		return ret;
	}
	
	/**
	 *  Get the target platform if specified (using platform and provider).
	 *  @return The target platform.
	 */
	public IComponentIdentifier getTargetPlatform()
	{
		if (getPlatform()!=null)
			return getPlatform().getRoot();
		
		if (RequiredServiceInfo.SCOPE_COMPONENT_ONLY.equals(scope))
			return searchstart != null ? searchstart.getRoot() : owner.getRoot();
			
		return null;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		StringBuffer	ret	= new StringBuffer("ServiceQuery(");
		if(servicetype!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("servicetype=");
			ret.append(servicetype);
		}
		
		if(serviceidentifier!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("serviceidentifier=");
			ret.append(serviceidentifier);
		}
		
		if(multiplicity!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("multiplicity=");
			ret.append(multiplicity);
		}
		
		if(servicetags!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("servicetags=");
			ret.append(Arrays.toString(servicetags));
		}
		if(searchstart!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("searchstart=");
			ret.append(searchstart);
		}
		if(platform!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("platform=");
			ret.append(platform);
		}
		if(networknames!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("networknames=");
			ret.append(Arrays.toString(networknames));
		}

		if(unrestricted!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("unrestricted=");
			ret.append(unrestricted);
		}

		if(scope!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("scope=");
			ret.append(scope);
		}

		if(owner!=null)
		{
			ret.append(ret.length()==13?"":", ");
			ret.append("owner=");
			ret.append(owner);
		}
			
		ret.append(")");
		return ret.toString();
	}
}
