package jadex.bridge.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  Scopes for service publication (provided) and search (e.g. required).
 */
public enum ServiceScope
{
	/** Marker for default scope to be replaced automatically according to context;
	 *  for required services (search scope): the default scope is 'platform' for system services and 'application' for non-system services,
	 *  for provided services (publication scope): the default scope is always platform.*/
	DEFAULT,

	/** None component scope (nothing will be searched, forces required service creation). */
	NONE,

	/** Available in immediate parent and all direct and indirect subcomponents. */
	PARENT,
	
	// todo: rename (COMPONENT_LOCAL)
	/** Available in component itself. */
	COMPONENT_ONLY,
	
	/** Available in component and all direct and indirect subcomponents. */
	COMPONENT,
	
	// todo: rename (APPLICATION_PLATFORM) or remove
	/** Available in local application, i.e. second level component plus direct and indirect subcomponents. */
	APPLICATION,

	/** Available in all components on the local platform. */
	PLATFORM,

	
	/** Application network scope (any platform with which a secret is shared and application tag must be shared). */
	APPLICATION_NETWORK,
//	public static final String SCOPE_APPLICATION_CLOUD = "application_cloud";
	
	/** Network scope (any platform with which a secret is shared). */
	NETWORK,
//	public static final String SCOPE_CLOUD = "cloud";
		
	// needed?!
	/** Global application scope. */
	APPLICATION_GLOBAL,
	
	/** Global scope (any reachable platform including those with unrestricted services). */
	GLOBAL;

	//-------- constants --------
	
	/** The scopes local to a platform. */
	public static final Set<ServiceScope> LOCAL_SCOPES;
	static
	{
		Set<ServiceScope> localscopes = new HashSet<>();
		localscopes.add(null);
		localscopes.add(NONE);
		localscopes.add(COMPONENT_ONLY);
		localscopes.add(COMPONENT);
		localscopes.add(APPLICATION);
		localscopes.add(PLATFORM);
		localscopes.add(PARENT);
		LOCAL_SCOPES = Collections.unmodifiableSet(localscopes);
	}

	/** The global scopes. */
	public static final Set<ServiceScope> GLOBAL_SCOPES;
	static
	{
		Set<ServiceScope> localscopes = new HashSet<>();
		localscopes.add(GLOBAL);
		localscopes.add(APPLICATION_GLOBAL);
		GLOBAL_SCOPES = Collections.unmodifiableSet(localscopes);
	}
	
	/** The network scopes. */
	public static final Set<ServiceScope> NETWORK_SCOPES;
	static
	{
		Set<ServiceScope> localscopes = new HashSet<>();
		localscopes.add(NETWORK);
		localscopes.add(APPLICATION_NETWORK);
		NETWORK_SCOPES = Collections.unmodifiableSet(localscopes);
	}
	
	//-------- methods --------
	
	/**
	 *  Check if the scope not remote.
	 *  @return True, scope on the local platform.
	 */
	public boolean isLocal()
	{
		return LOCAL_SCOPES.contains(this);
	}
	
	/**
	 *  Check if the scope is global.
	 */
	public boolean isGlobal()
	{
		return GLOBAL_SCOPES.contains(this);
	}
	
	/**
	 *  Check if the scope is a network scope.
	 */
	public boolean isNetwork()
	{
		return NETWORK_SCOPES.contains(this);
	}
}
