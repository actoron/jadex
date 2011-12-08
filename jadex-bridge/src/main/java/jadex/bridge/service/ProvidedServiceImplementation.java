package jadex.bridge.service;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  Contains information for provided service implementation:
 *  - implementation class or
 *  - creation expression or
 *  - implementation forward to other component via binding 
 */
public class ProvidedServiceImplementation
{
	// todo: use UnparsedExpression instead of implementation and expression text?
	
	//-------- attributes --------
	
	/** The implementation class. */
	protected ClassInfo implementation;

	/** The creation expression. */
	protected String expression;
		
	/** The binding for forwarding service calls. */
	protected RequiredServiceBinding binding;

	/** The proxy type. */
	protected String proxytype;
	
	/** The list of interceptors. */
	protected List<UnparsedExpression> interceptors;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service implementation.
	 */
	public ProvidedServiceImplementation()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a new service implementation.
	 */
	public ProvidedServiceImplementation(Class implementation,
		String expression, String proxytype, RequiredServiceBinding binding, UnparsedExpression[] interceptors)
	{
		this(implementation!=null? new ClassInfo(implementation): null, expression, proxytype, binding, interceptors);
	}
		
	/**
	 *  Create a new service implementation.
	 */
	public ProvidedServiceImplementation(ClassInfo implementation,
		String expression, String proxytype, RequiredServiceBinding binding, UnparsedExpression[] interceptors)
	{
		setImplementation(implementation);
		this.expression = expression;
		this.proxytype = proxytype;
		this.binding = binding;
		if(interceptors!=null)
		{
			for(int i=0; i<interceptors.length; i++)
			{
				addInterceptor(interceptors[i]);
			}
		}
	}
	
	/**
	 *  Create a new service implementation.
	 */
	public ProvidedServiceImplementation(ProvidedServiceImplementation prov)
	{
		this(prov.implementation, prov.getExpression(), prov.getProxytype(), prov.getBinding()!=null? 
			new RequiredServiceBinding(prov.getBinding()): null, prov.getInterceptors());
	}

	//-------- methods --------
	
//	/**
//	 *  Get the implementation name.
//	 *  @return the implementation name.
//	 */
//	public String getImplementationName()
//	{
//		return implname;
//	}
//
//	/**
//	 *  Set the implementation name.
//	 *  @param name The implementation name to set.
//	 */
//	public void setImplementationName(String implname)
//	{
//		this.implname = implname;
//	}

	/**
	 *  Get the implementation.
	 *  @return The implementation.
	 */
	public ClassInfo getImplementation()
	{
		return implementation;
	}

	/**
	 *  Set the implementation.
	 *  @param implementation The implementation to set.
	 */
	public void setImplementation(ClassInfo implementation)
	{
		this.implementation = implementation;
	}

	/**
	 *  Get the expression.
	 *  @return The expression.
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 *  Set the expression.
	 *  @param expression The expression to set.
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 *  Get the proxy type.
	 *  @return The proxy type.
	 */
	public String getProxytype()
	{
		return proxytype;
	}

	/**
	 *  Set the proxy type.
	 *  @param proxytype The proxy type to set.
	 */
	public void	setProxytype(String proxytype)
	{
		this.proxytype	= proxytype;
	}

	/**
	 *  Get the binding.
	 *  @return The binding.
	 */
	public RequiredServiceBinding getBinding()
	{
		return binding;
	}

	/**
	 *  Set the binding.
	 *  @param binding The binding to set.
	 */
	public void setBinding(RequiredServiceBinding binding)
	{
		this.binding = binding;
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return implementation!=null? implementation.getTypeName(): 
			expression!=null? expression: binding!=null? 
			binding.getComponentName()!=null? binding.getComponentName(): 
				binding.getComponentType(): "";
	}
}
