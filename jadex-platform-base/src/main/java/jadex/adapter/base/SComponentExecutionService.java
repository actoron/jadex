package jadex.adapter.base;

import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentFactory;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 */
public class SComponentExecutionService
{
	/**
	 *  Create a new element on the platform.
	 *  The element will not run before the {@link startElement()}
	 *  method is called.
	 *  Ensures (in non error case) that the aid of
	 *  the new element is added to the AMS when call returns.
	 *  @param name The element name (null for auto creation)
	 *  @param model The model name.
	 *  @param config The configuration.
	 *  @param args The arguments map (name->value).
	 *  @param listener The result listener (if any).
	 *  @param creator The creator (if any).
	 */
	public static void	createComponent(IServiceContainer container, String name, String model, String config, Map args, IResultListener listener, Object creator)
	{
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IComponentExecutionService es = (IComponentExecutionService)it.next();
				if(es.isResponsible(model))
				{
					es.createComponent(name, model, config, args, listener, creator);
					break;
				}
			}
		}
	}
	
	/**
	 *  Start a previously created element on the platform.
	 *  @param elementid The id of the previously created element.
	 */
	public static void	startComponent(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IComponentExecutionService es = (IComponentExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.startComponent(elementid, listener);
					break;
				}
			}
		}
	}
	
	/**
	 *  Destroy (forcefully terminate) an element on the platform.
	 *  @param elementid	The element to destroy.
	 */
	public static void destroyComponent(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IComponentExecutionService es = (IComponentExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.destroyComponent(elementid, listener);
					break;
				}
			}
		}
	}

	/**
	 *  Suspend the execution of an element.
	 *  @param elementid The element identifier.
	 */
	public static void suspendComponent(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IComponentExecutionService es = (IComponentExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.suspendComponent(elementid, listener);
					break;
				}
			}
		}
	}
	
	/**
	 *  Resume the execution of an element.
	 *  @param elementid The element identifier.
	 */
	public static void resumeComponent(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IComponentExecutionService es = (IComponentExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.destroyComponent(elementid, listener);
					break;
				}
			}
		}
	}

}
