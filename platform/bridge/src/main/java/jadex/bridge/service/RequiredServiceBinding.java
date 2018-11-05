package jadex.bridge.service;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Required service binding information.
 */
public class RequiredServiceBinding
{
	//-------- attributes --------
	
	/** The service name. */
	protected String name;
	
	/** The component name used for searching. */
	protected String componentname;
	
	/** The component type, i.e. the model name used for searching. */
	protected String componenttype;

	/** Information about the component to create. */
	protected ComponentInstanceInfo creationinfo;
	
	// Decided to drop caching support for v4
//	/** Flag if binding is dynamic. */
//	protected boolean dynamic;

	/** The search scope. */
	protected ServiceScope scope;
	
//	/** The create flag. */
//	protected boolean create;
	// Dropped support for v4
	
//	/** The recover flag. */
//	protected boolean recover;
	// Dropped support for v4
	
	/** The interceptors. */
	protected List<UnparsedExpression> interceptors;
	
	/** The proxytype. */
	protected String proxytype;

	//-------- constructors --------

	/**
	 *  Create a new binding. 
	 */
	public RequiredServiceBinding()
	{
	}
	
	/**
	 *  Create a new binding. 
	 */
	public RequiredServiceBinding(String name, ServiceScope scope)
	{
		this(name, null, null,
			scope, null, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED);
	}

	/**
	 *  Create a new binding.
	 */
	public RequiredServiceBinding(String name, String componentname, String componenttype,
		ServiceScope scope, UnparsedExpression[] interceptors, String proxytype)
	{
		this.name = name;
		this.componentname = componentname;
		this.componenttype = componenttype;
		this.scope = scope;
		this.proxytype = proxytype;
		if(interceptors!=null)
		{
			for(int i=0; i<interceptors.length; i++)
			{
				addInterceptor(interceptors[i]);
			}
		}
	}
	
	/**
	 *  Create a new binding.
	 */
	public RequiredServiceBinding(RequiredServiceBinding orig)
	{
		this(orig.getName(), orig.getComponentName(), orig.getComponentType(), 
			orig.getScope(), orig.getInterceptors(), orig.getProxytype());
	}

	//-------- methods --------
	
	/**
	 *  Get the creationinfo.
	 *  @return The creationinfo.
	 */
	public ComponentInstanceInfo getCreationInfo()
	{
		return creationinfo;
	}

	/**
	 *  Set the creationinfo.
	 *  @param creationinfo The creationinfo to set.
	 */
	public void setCreationInfo(ComponentInstanceInfo creationinfo)
	{
		this.creationinfo = creationinfo;
	}

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
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the componentname.
	 *  @return the componentname.
	 */
	public String getComponentName()
	{
		return componentname;
	}

	/**
	 *  Set the componentname.
	 *  @param componentname The componentname to set.
	 */
	public void setComponentName(String componentname)
	{
		this.componentname = componentname;
	}

	/**
	 *  Get the componenttype.
	 *  @return the componenttype.
	 */
	public String getComponentType()
	{
		return componenttype;
	}

	/**
	 *  Set the componenttype.
	 *  @param componenttype The componenttype to set.
	 */
	public void setComponentType(String componenttype)
	{
		this.componenttype = componenttype;
	}

	/**
	 *  Get the scope.
	 *  @return the scope.
	 */
	public ServiceScope getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set.
	 */
	public void setScope(ServiceScope scope)
	{
		this.scope = scope;
	}
	
	/**
	 *  Add an interceptor.
	 *  @param interceptor The interceptor.
	 */
	public void addInterceptor(UnparsedExpression interceptor)
	{
		if(interceptors==null)
			interceptors = new ArrayList<UnparsedExpression>();
		interceptors.add(interceptor);
	}
	
	/**
	 *  Remove an interceptor.
	 *  @param interceptor The interceptor.
	 */
	public void removeInterceptor(UnparsedExpression interceptor)
	{
		interceptors.remove(interceptor);
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
	 *  Get the proxytype.
	 *  @return the proxytype.
	 */
	public String getProxytype()
	{
		return proxytype;
	}

	/**
	 *  Set the proxytype.
	 *  @param proxytype The proxytype to set.
	 */
	public void setProxytype(String proxytype)
	{
		this.proxytype = proxytype;
	}
	
//	/**
//	 *  Get the creationname.
//	 *  @return The creationname.
//	 */
//	public String getCreationName()
//	{
//		return creationname;
//	}
//
//	/**
//	 *  Set the creationname.
//	 *  @param creationname The creationname to set.
//	 */
//	public void setCreationName(String creationname)
//	{
//		this.creationname = creationname;
//	}
//
//	/**
//	 *  Get the creationtype.
//	 *  @return The creationtype.
//	 */
//	public String getCreationType()
//	{
//		return creationtype;
//	}
//
//	/**
//	 *  Set the creationtype.
//	 *  @param creationtype The creationtype to set.
//	 */
//	public void setCreationType(String creationtype)
//	{
//		this.creationtype = creationtype;
//	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return " scope=" + scope + ", componentname=" + componentname
			+ ", componenttype="+ componenttype	+" , creationcomp="+creationinfo;
	}

	
}
