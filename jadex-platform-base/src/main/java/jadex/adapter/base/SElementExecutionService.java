package jadex.adapter.base;

import jadex.bridge.IElementExecutionService;
import jadex.bridge.IElementFactory;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 */
public class SElementExecutionService
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
	public static void	createElement(IServiceContainer container, String name, String model, String config, Map args, IResultListener listener, Object creator)
	{
		Collection facts = container.getServices(IElementFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IElementExecutionService es = (IElementExecutionService)it.next();
				if(es.isResponsible(model))
				{
					es.createElement(name, model, config, args, listener, creator);
					break;
				}
			}
		}
	}
	
	/**
	 *  Start a previously created element on the platform.
	 *  @param elementid The id of the previously created element.
	 */
	public static void	startElement(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IElementFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IElementExecutionService es = (IElementExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.startElement(elementid, listener);
					break;
				}
			}
		}
	}
	
	/**
	 *  Destroy (forcefully terminate) an element on the platform.
	 *  @param elementid	The element to destroy.
	 */
	public static void destroyElement(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IElementFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IElementExecutionService es = (IElementExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.destroyElement(elementid, listener);
					break;
				}
			}
		}
	}

	/**
	 *  Suspend the execution of an element.
	 *  @param elementid The element identifier.
	 */
	public static void suspendElement(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IElementFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IElementExecutionService es = (IElementExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.suspendElement(elementid, listener);
					break;
				}
			}
		}
	}
	
	/**
	 *  Resume the execution of an element.
	 *  @param elementid The element identifier.
	 */
	public static void resumeElement(IServiceContainer container, Object elementid, IResultListener listener)
	{
		Collection facts = container.getServices(IElementFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IElementExecutionService es = (IElementExecutionService)it.next();
				if(es.isResponsible(elementid))
				{
					es.destroyElement(elementid, listener);
					break;
				}
			}
		}
	}

}
