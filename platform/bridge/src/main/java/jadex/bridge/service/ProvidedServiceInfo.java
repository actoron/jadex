package jadex.bridge.service;

import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.annotation.Security;
import jadex.commons.SReflect;


/**
 *  Info for provided services.
 */
public class ProvidedServiceInfo
{
	//-------- attributes --------

	/** The name (used for referencing). */
	protected String name;
	
	/** The service interface type as string. */
	protected String typename;
	
	/** The type. */
	protected ClassInfo type;
	
	/** The service implementation. */
	protected ProvidedServiceImplementation implementation;
	
	/** Publish information. */
	protected PublishInfo publish;
	
	/** The scope. */
	protected ServiceScope scope;
	
	/** The scope expression (if any). */
	protected UnparsedExpression scopeexpression;
	
	/** The security settings (empty roles if unset). */
	protected Security security;
	
	/** The service properties. */
	protected List<UnparsedExpression> properties;
	
	/** Flag if it is a system service. */
	protected boolean systemservice;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, ClassInfo type, ProvidedServiceImplementation implementation)
	{
		this(name, type, implementation, null, null, null, null, null, false);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, Class<?> type, ProvidedServiceImplementation implementation)
	{
		this(name, type, implementation, null, null, null, null, null);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, Class<?> type, ProvidedServiceImplementation implementation, ServiceScope scope, UnparsedExpression scopeexpression, Security security, PublishInfo publish, List<UnparsedExpression> properties)
	{
		this(name, type!=null? new ClassInfo(SReflect.getClassName(type)): null, implementation, scope, scopeexpression, security, publish, properties, ServiceIdentifier.isSystemService(type));
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, ClassInfo type, ProvidedServiceImplementation implementation, ServiceScope scope, UnparsedExpression scopeexpression, Security security, PublishInfo publish, List<UnparsedExpression> properties, boolean systemservice)
	{
		this.name = name;
		this.implementation = implementation;
		this.publish = publish;
		this.properties = properties;
		this.scope = scope;
		this.scopeexpression = scopeexpression;
		this.security = security;
		this.systemservice = systemservice;
		setType(type);
	}
	
//	/**
//	 *  Create a new service info.
//	 */
//	public ProvidedServiceInfo(ProvidedServiceInfo orig)
//	{
//		this(orig.getType(), new ProvidedServiceImplementation(orig.getImplementation()));
//	}
	
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
	public void setName(String name)
	{
		this.name = name;
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
	public void setType(ClassInfo type)
	{
		this.type = type;
	}

	/**
	 *  Get the implementation.
	 *  @return The implementation.
	 */
	public ProvidedServiceImplementation getImplementation()
	{
		return implementation;
	}

	/**
	 *  Set the implementation.
	 *  @param implementation The implementation to set.
	 */
	public void setImplementation(ProvidedServiceImplementation implementation)
	{
		this.implementation = implementation;
	}
	
	/**
	 *  Get the publish.
	 *  @return The publish.
	 */
	public PublishInfo getPublish()
	{
		return publish;
	}

	/**
	 *  Set the publish.
	 *  @param publish The publish to set.
	 */
	public void setPublish(PublishInfo publish)
	{
		this.publish = publish;
	}

	/**
	 * @return the properties
	 */
	public List<UnparsedExpression> getProperties()
	{
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<UnparsedExpression> properties)
	{
		this.properties = properties;
	}
	
	/**
	 *  Get the scope expression.
	 *  @return The scope expression.
	 */
	public UnparsedExpression getScopeExpression()
	{
		return scopeexpression;
	}

	/**
	 *  Set the scope expression.
	 *  @param expression The scope expression to set.
	 */
	public void setScopeExpression(UnparsedExpression expression)
	{
		this.scopeexpression = expression;
	}
	
	/**
	 *  Get the scope.
	 *  @return The scope.
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
	 *  Get the security settings.
	 *  @return The security settings.
	 */
	public Security getSecurity()
	{
		return security;
	}

	/**
	 *  Set the security settings.
	 *  @param security The new security settings.
	 */
	public void setSecurity(Security security)
	{
		this.security = security;
	}
	
	/**
	 *  Get the systemservice.
	 *  @return The systemservice
	 */
	public boolean isSystemService()
	{
		return systemservice;
	}

	/**
	 *  Set the systemservice.
	 *  @param systemservice The systemservice to set
	 */
	public void setSystemService(boolean systemservice)
	{
		this.systemservice = systemservice;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ProvidedServiceInfo(name="+name+", type="+ type + ", implementation="+ implementation + ")";
	}
}
