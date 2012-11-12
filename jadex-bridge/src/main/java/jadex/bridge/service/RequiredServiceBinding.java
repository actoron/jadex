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
	
//	/** The component creation name. */
//	protected String creationname;

//	/** The component creation type. */
//	protected String creationtype;
	
	/** The component filename. */
//	protected String componentfilename;
	
	/** Flag if binding is dynamic. */
	protected boolean dynamic;

	/** The search scope. */
	protected String scope;
	
	/** The create flag. */
	protected boolean create;
	
	/** The recover flag. */
	protected boolean recover;
	
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
	public RequiredServiceBinding(String name, String scope)
	{
		this(name, scope, false);
	}
	
	/**
	 *  Create a new binding. 
	 */
	public RequiredServiceBinding(String name, String scope, boolean dynamic)
	{
		this(name, null, null, dynamic, scope, false, false, null, 
			BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null);
	}

	/**
	 *  Create a new binding.
	 */
	public RequiredServiceBinding(String name, String componentname,
		String componenttype, boolean dynamic, String scope, boolean create, boolean recover,
		UnparsedExpression[] interceptors, String proxytype, ComponentInstanceInfo component)//String creationtype, String creationname)
	{
		this.name = name;
		this.componentname = componentname;
		this.componenttype = componenttype;
		this.dynamic = dynamic;
		this.scope = scope;
		this.create = create;
		this.recover = recover;
		this.proxytype = proxytype;
		this.creationinfo = component;
//		this.creationtype = creationtype;
//		this.creationname = creationname;
		if(interceptors!=null)
		{
			for(int i=0; i<interceptors.length; i++)
			{
				addInterceptor(interceptors[i]);
			}
		}
//		this.componentfilename = componentfilename;
	}
	
	/**
	 *  Create a new binding.
	 */
	public RequiredServiceBinding(RequiredServiceBinding orig)
	{
		this(orig.getName(), orig.getComponentName(), orig.getComponentType(), 
			orig.isDynamic(), orig.getScope(), orig.isCreate(), orig.isRecover(), 
			orig.getInterceptors(), orig.getProxytype(), orig.getCreationInfo());//orig.getCreationType(), orig.getCreationName());
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
	 *  Get the dynamic.
	 *  @return the dynamic.
	 */
	public boolean isDynamic()
	{
		return dynamic;
	}

	/**
	 *  Set the dynamic.
	 *  @param dynamic The dynamic to set.
	 */
	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	/**
	 *  Get the scope.
	 *  @return the scope.
	 */
	public String getScope()
	{
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
	 *  Get the create.
	 *  @return The create.
	 */
	public boolean isCreate()
	{
		return create;
	}

	/**
	 *  Set the create.
	 *  @param create The create to set.
	 */
	public void setCreate(boolean create)
	{
		this.create = create;
	}

	/**
	 *  Get the recover.
	 *  @return The recover.
	 */
	public boolean isRecover()
	{
		return recover;
	}

	/**
	 *  Set the recover.
	 *  @param recover The recover to set.
	 */
	public void setRecover(boolean recover)
	{
		this.recover = recover;
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
		return " scope=" + scope + ", dynamic="+ dynamic + ", create=" + create + ", recover=" 
			+ recover+ ", componentname=" + componentname + ", componenttype="+ componenttype
			+" , creationcomp="+creationinfo;//+" , creationname="+creationname;
//			+" , creationtype="+creationtype+" , creationname="+creationname;
	}

	
}
