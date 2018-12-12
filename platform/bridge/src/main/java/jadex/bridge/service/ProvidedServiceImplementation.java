package jadex.bridge.service;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Contains information for provided service implementation:
 *  - implementation class or
 *  - creation expression or
 *  - implementation forward to other component via binding 
 */
public class ProvidedServiceImplementation	extends UnparsedExpression
{
	//-------- attributes --------
	
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
		
		// Set default proxy type (Hack!!! specify in xml mapping?)
		proxytype	= BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED;
	}
	
	/**
	 *  Create a new service implementation.
	 */
	public ProvidedServiceImplementation(Class<?> implementation, String expression, 
		String proxytype, RequiredServiceBinding binding, UnparsedExpression[] interceptors)
	{
		super(null, implementation, expression, null);
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
		setName(prov.getName());
		setClazz(prov.getClazz());
		setValue(prov.getValue());
		setLanguage(prov.getLanguage());
		
		setProxytype(prov.getProxytype());
		setBinding(prov.getBinding());
		UnparsedExpression[]	ints	= prov.getInterceptors();	
		for(int i=0; i<ints.length; i++)
		{
			addInterceptor(ints[i]);
		}
	}

	//-------- methods --------
	
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
		return getClazz()!=null? getClazz().getTypeName(): 
			getValue()!=null? getValue(): binding!=null? 
			binding.getComponentName()!=null? binding.getComponentName(): 
				binding.getComponentType(): "";
	}
}
