package jadex.adapter.base;

import jadex.bridge.IApplicationFactory;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;

/**
 *  Standard meta agent factory. Uses several sub
 *  factories and uses them according to their order
 *  and isLoadable() method.
 */
public class SComponentFactory
{
	/**
	 *  Get the element type.
	 *  @return The element type (e.g. an agent, application or process).
	 * /
	public static String getElementType(IServiceContainer container, String model)
	{
		String ret = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && ret==null; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				if(fac.isLoadable(model))
					ret = fac.getElementType();
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Create a kernel agent.
	 *  @param adapter	The platform adapter for the agent. 
	 *  @param model	The agent model file (i.e. the name of the XML file).
	 *  @param config	The name of the configuration (or null for default configuration) 
	 *  @param arguments	The arguments for the agent as name/value pairs.
	 *  @return	An instance of a kernel agent.
	 * /
	public static IComponentInstance createKernelAgent(IServiceContainer container, IAgentAdapter adapter, String model, String config, Map arguments)	
	{
		IComponentInstance ret = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && ret==null; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				
				if(fac.isLoadable(model))
					ret = fac.createComponentInstance(adapter, model, config, arguments);
			}
		}
		
		return ret;
	
	/**
	 *  Load an agent model.
	 *  @param model The model.
	 *  @return The loaded model.
	 */
	public static ILoadableComponentModel loadModel(IServiceContainer container, String model)
	{
		ILoadableComponentModel ret = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && ret==null; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				if(fac.isLoadable(model))
					ret = fac.loadModel(model);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model.
	 *  @return True, if model can be loaded.
	 */
	public static boolean isLoadable(IServiceContainer container, String model)
	{
		boolean ret = false;
		
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); !ret && it.hasNext();)
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				ret = fac.isLoadable(model);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create an application.
	 */
	public static void createApplication(IServiceContainer container, String name, String model, String config, Map args)
	{
		try
		{
			Collection facts = container.getServices(IComponentFactory.class);
			if(facts!=null)
			{
				for(Iterator it=facts.iterator(); it.hasNext(); )
				{
					IComponentFactory fac = (IComponentFactory)it.next();
					if(fac instanceof IApplicationFactory)
					{
						IApplicationFactory afac = (IApplicationFactory)fac;
						if(afac.isLoadable(model))
						{
							afac.createApplication(name, model, config, args);
							break;
						}
					}
				}
			}
//			getApplicationFactory().createApplication(name, model, config, args);
		}
		catch(Exception e)
		{
			System.err.println("Exception occurred while creating application: ");
			e.printStackTrace();
		}
	}
	
	/**
	 *  Test if a model is startable (e.g. an agent).
	 *  @param model The model.
	 *  @return True, if startable (and should therefore also be loadable).
	 */
	public static boolean isStartable(IServiceContainer container, String model)
	{
		boolean ret = false;
		
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && !ret; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				ret = fac.isStartable(model);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public static String[] getFileTypes(IServiceContainer container)
	{
		String[]	ret	= new String[0];
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext(); )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				ret	= (String[])SUtil.joinArrays(ret, fac.getFileTypes());
			}
		}
		return ret;
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public static Icon getFileTypeIcon(IServiceContainer container, String type)
	{
		Icon	ret = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && ret==null; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				ret = fac.getFileTypeIcon(type);
			}
		}
		return ret;
	}


	/**
	 *  Get the file type of a model.
	 */
	public static String getFileType(IServiceContainer container, String model)
	{
		String	ret = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && ret==null; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				ret = fac.getFileType(model);
			}
		}
		return ret;
	}

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
		IComponentFactory factory = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); factory==null && it.hasNext(); )
			{
				IComponentFactory	cf	= (IComponentFactory)it.next();
				if(cf.isLoadable(model))
				{
					factory	= cf;
				}
			}
		}
//		IComponentIdentifier cid = ces.createComponentIdentifier(name, true, null, true);

		IComponentExecutionService	ces	= (IComponentExecutionService)container.getService(IComponentExecutionService.class);
		ces.createComponent(container, name, model, config, args, listener, creator, factory);
	}
}
