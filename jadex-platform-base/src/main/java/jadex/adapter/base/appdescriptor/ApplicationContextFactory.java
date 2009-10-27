package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.contextservice.IContextFactory;
import jadex.bridge.IContext;
import jadex.service.IServiceContainer;

import java.util.Map;

/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ApplicationContextFactory	implements IContextFactory
{
	//-------- attributes --------
	
	/** The platform. */
	protected IServiceContainer	container;
	
	//-------- constructors --------
	
	/**
	 *  Create a new default context factory.
	 *  @param platform	The platform.
	 */
	public ApplicationContextFactory(IServiceContainer container)
	{
		this.container	= container;
	}
	
	//-------- IContextFactory interface --------
	
	/**
	 *  Create a new context.
	 *  @param name	The name of the context.
	 *  @param parent	The parent of the context (if any).
	 *  @param properties	Initialization properties (if any).
	 */
	public IContext createContext(String name, /*IContext parent,*/ Map properties)
	{
		return new ApplicationContext(name, /*parent,*/ properties, container);
	}
}
