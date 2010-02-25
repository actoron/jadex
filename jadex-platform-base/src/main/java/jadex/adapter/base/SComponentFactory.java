package jadex.adapter.base;

import jadex.bridge.IComponentFactory;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SUtil;
import jadex.service.IServiceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;

/**
 *  Standard meta component factory. Uses several sub
 *  factories and uses them according to their order
 *  and isLoadable() method.
 */
public class SComponentFactory
{	
	/**
	 *  Load an component model.
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
	 * /
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
					if(fac.isLoadable(model))
					{
						ILoadableComponentModel lmodel = fac.loadModel(model);
						fac.createComponentInstance(null, lmodel, config, args);
						//(name, model, config, args);
						break;
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
	}*/
	
	/**
	 *  Test if a model is startable (e.g. a component).
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
				ret	= (String[])SUtil.joinArrays(ret, fac.getComponentTypes());
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
				ret = fac.getComponentTypeIcon(type);
			}
		}
		return ret;
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public static Map	getProperties(IServiceContainer container, String type)
	{
		Map	ret = null;
		Collection facts = container.getServices(IComponentFactory.class);
		if(facts!=null)
		{
			for(Iterator it=facts.iterator(); it.hasNext() && ret==null; )
			{
				IComponentFactory fac = (IComponentFactory)it.next();
				ret = fac.getProperties(type);
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
				ret = fac.getComponentType(model);
			}
		}
		return ret;
	}

}
