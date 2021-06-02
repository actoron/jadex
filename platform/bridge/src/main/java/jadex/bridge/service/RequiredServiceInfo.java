package jadex.bridge.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.NFRPropertyInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SReflect;

/**
 *  Struct for information about a required service.
 */
public class RequiredServiceInfo
{
	//-------- legacy constants --------
	
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_NONE = ServiceScope.NONE;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_PARENT = ServiceScope.PARENT;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_COMPONENT_ONLY = ServiceScope.COMPONENT_ONLY;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_COMPONENT = ServiceScope.COMPONENT;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_APPLICATION = ServiceScope.APPLICATION;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_PLATFORM = ServiceScope.PLATFORM;	
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_APPLICATION_NETWORK = ServiceScope.APPLICATION_NETWORK;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_NETWORK = ServiceScope.NETWORK;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_APPLICATION_GLOBAL = ServiceScope.APPLICATION_GLOBAL;
	/** @deprecated Use {@link ServiceScope} instead */
	public static final ServiceScope SCOPE_GLOBAL = ServiceScope.GLOBAL;
	
//	/** The scopes local to a platform. */
//	public static final Set<String> LOCAL_SCOPES;
//	static
//	{
//		Set<String> localscopes = new HashSet<>();
//		localscopes.add(null);
//		localscopes.add(SCOPE_NONE);
//		localscopes.add(SCOPE_COMPONENT_ONLY);
//		localscopes.add(SCOPE_COMPONENT);
//		localscopes.add(SCOPE_APPLICATION);
//		localscopes.add(SCOPE_PLATFORM);
//		localscopes.add(SCOPE_PARENT);
//		LOCAL_SCOPES = Collections.unmodifiableSet(localscopes);
//	}
//	
//	/** The global scopes. */
//	public static final Set<String> GLOBAL_SCOPES;
//	static
//	{
//		Set<String> localscopes = new HashSet<>();
//		localscopes.add(SCOPE_GLOBAL);
//		localscopes.add(SCOPE_APPLICATION_GLOBAL);
//		GLOBAL_SCOPES = Collections.unmodifiableSet(localscopes);
//	}
//	
//	/** The network scopes. */
//	public static final Set<String> NETWORK_SCOPES;
//	static
//	{
//		Set<String> localscopes = new HashSet<>();
//		localscopes.add(SCOPE_NETWORK);
//		localscopes.add(SCOPE_APPLICATION_NETWORK);
//		NETWORK_SCOPES = Collections.unmodifiableSet(localscopes);
//	}
	
	
//	/** Global application scope. */
//	public static final String SCOPE_GLOBAL_APPLICATION = "global_application";
	
//	/** Upwards scope. */
//	public static final String SCOPE_UPWARDS = "upwards";
	
	/** Constant for multiplicity many. */
	public static final int MANY = -1;
	
	/** Constant for multiplicity undefined. */
	public static final int UNDEFINED = -2;
	
	//-------- attributes --------

	// service description
	
	/** The component internal service name. */
	protected String name;
	
	/** The type. */
	protected ClassInfo type;
	
	/** The service tags to search for. */
	protected Collection<String> tags;
	
	///** Flag if multiple services should be returned. */
	//protected boolean multiple;
	
	/** The min number of services. */
	protected int min;
	
	/** The max number of services. */
	protected int max;
	
//	/** The multiplex type. */
//	protected ClassInfo multiplextype;
	// Dropped support for v4

	// binding specification
	
	/** The default binding. */
	protected RequiredServiceBinding binding;
	
	/** The list of interceptors. */
	protected List<UnparsedExpression> interceptors;
	
	// nf props for required service
	
	/** The nf props. This is not for search but adds NF props*/
	protected List<NFRPropertyInfo> nfproperties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo()
	{
		// bean constructor
		
		// Hack!!! Initialize with default values to resemble annotation behavior.
		this(null, null);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class<?> type)
	{
		this(name, type, ServiceScope.APPLICATION);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(Class<?> type)
	{
		this(null, type, ServiceScope.APPLICATION);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class<?> type, ServiceScope scope)
	{
		this(name, type, UNDEFINED, UNDEFINED, new RequiredServiceBinding(name, scope), null, null);
	}
	
	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, Class<?> type, int min, int max, //boolean multiple, 
		RequiredServiceBinding binding, List<NFRPropertyInfo> nfprops, Collection<String> tags)
	{
		this(name, type!=null? new ClassInfo(SReflect.getClassName(type)) : null,
			min, max, binding, nfprops, tags);
	}

	/**
	 *  Create a new service info.
	 */
	public RequiredServiceInfo(String name, ClassInfo type, int min, int max, //boolean multiple, 
		RequiredServiceBinding binding, List<NFRPropertyInfo> nfprops, Collection<String> tags)
	{
		this.name = name; //(name==null || name.length()==0)? SUtil.createPlainRandomId(type!=null? type.getTypeName(): "req_service", 5): name;
		//System.out.println("reqname: "+this.name);
		this.type	= type;
		//this.multiple = multiple;
		this.min = min;
		this.max = max;
		this.binding = binding;
		this.nfproperties = nfprops;
		this.tags = tags;
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public RequiredServiceInfo setName(String name)
	{
		this.name = name;
		return this;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public ClassInfo getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public RequiredServiceInfo setType(ClassInfo type)
	{
		this.type = type;
		return this;
	}
	
//	/**
//	 *  Get the multiple.
//	 *  @return the multiple.
//	 */
//	public boolean isMultiple()
//	{
//		return multiple;
//	}
//
//	/**
//	 *  Set the multiple.
//	 *  @param multiple The multiple to set.
//	 */
//	public void setMultiple(boolean multiple)
//	{
//		this.multiple = multiple;
//	}
	
	/**
	 *  Get the max number of services.
	 *  @return The max number.
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 *  Set the max number of services.
	 *  @param max The max number to set.
	 */
	public RequiredServiceInfo setMax(int max)
	{
		this.max = max;
		return this;
	}
	
	/**
	 *  Get the minimum number of services.
	 *  @return The min number of services.
	 */
	public int getMin()
	{
		return min;
	}

	/** 
	 *  Set the min number of services.
	 *  @param min The min number to set.
	 */
	public RequiredServiceInfo setMin(int min)
	{
		this.min = min;
		return this;
	}

	/**
	 *  Get the binding.
	 *  @return the binding.
	 */
	public RequiredServiceBinding getDefaultBinding()
	{
		return binding;
	}

	/**
	 *  Set the binding.
	 *  @param binding The binding to set.
	 */
	public RequiredServiceInfo setDefaultBinding(RequiredServiceBinding binding)
	{
		this.binding = binding;
		return this;
	}
	
	/**
	 *  Add an interceptor.
	 *  @param interceptor The interceptor.
	 */
	public RequiredServiceInfo addInterceptor(UnparsedExpression interceptor)
	{
		if(interceptors==null)
			interceptors = new ArrayList<UnparsedExpression>();
		interceptors.add(interceptor);
		return this;
	}
	
	/**
	 *  Remove an interceptor.
	 *  @param interceptor The interceptor.
	 */
	public RequiredServiceInfo removeInterceptor(UnparsedExpression interceptor)
	{
		interceptors.remove(interceptor);
		return this;
	}
	
	/**
	 *  Get the interceptors.
	 *  @return All interceptors.
	 */
	public UnparsedExpression[] getInterceptors()
	{
		return interceptors==null? new UnparsedExpression[0]: 
			interceptors.toArray(new UnparsedExpression[interceptors.size()]);
	}
	
	/**
	 *  Get the nfproperties.
	 *  @return The nfproperties.
	 */
	public List<NFRPropertyInfo> getNFRProperties()
	{
		return nfproperties;
	}

	/**
	 *  Set the nfproperties.
	 *  @param nfproperties The nfproperties to set.
	 */
	public RequiredServiceInfo setNFRProperties(List<NFRPropertyInfo> nfproperties)
	{
		this.nfproperties = nfproperties;
		return this;
	}

	/**
	 *  Get the tags.
	 *  @return the tags
	 */
	public Collection<String> getTags()
	{
		return tags;
	}

	/**
	 *  Set the tags.
	 *  @param tags The tags to set
	 */
	public RequiredServiceInfo setTags(Collection<String> tags)
	{
		this.tags = tags;
		return this;
	}
//	
//	/**
//	 *  Check if the scope not remote.
//	 *  @return True, scope on the local platform.
//	 */
//	public static final boolean isScopeOnLocalPlatform(String scope)
//	{
//		return LOCAL_SCOPES.contains(scope);
//	}
//	
//	/**
//	 *  Check if the scope is global.
//	 */
//	public static final boolean isGlobalScope(String scope)
//	{
//		return GLOBAL_SCOPES.contains(scope);
//	}
//	
//	/**
//	 *  Check if the scope is a network scope.
//	 */
//	public static final boolean isNetworkScope(String scope)
//	{
//		return NETWORK_SCOPES.contains(scope);
//	}
}
