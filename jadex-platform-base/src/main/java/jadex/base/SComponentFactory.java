package jadex.base;

import jadex.bridge.IComponentFactory;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.IServiceContainer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;


/**
 * Standard meta component factory. Uses several sub factories and uses them
 * according to their order and isLoadable() method.
 */
public class SComponentFactory
{
	/**
	 * Load an component model.
	 * 
	 * @param model The model.
	 * @return The loaded model.
	 */
//	public static ILoadableComponentModel loadModel(IServiceContainer container, String model)
	public static IFuture loadModel(final IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;

				ILoadableComponentModel res = null;
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); it.hasNext() && res == null;)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						if(fac.isLoadable(model, null))
							res = fac.loadModel(model, null);
					}
				}
				ret.setResult(res);
			}
		});

		return ret;
	}

	/**
	 * Test if a model can be loaded by the factory.
	 * 
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	public static IFuture isLoadable(IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				boolean res = false;
		
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); !res && it.hasNext();)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						res = fac.isLoadable(model, null);
					}
				}
				ret.setResult(res? Boolean.TRUE: Boolean.FALSE);
			}
		});

		return ret;
	}

	/**
	 * Create an application. / public static void
	 * createApplication(IServiceContainer container, String name, String model,
	 * String config, Map args) { try { Collection facts =
	 * container.getServices(IComponentFactory.class); if(facts!=null) {
	 * for(Iterator it=facts.iterator(); it.hasNext(); ) { IComponentFactory fac
	 * = (IComponentFactory)it.next(); if(fac.isLoadable(model)) {
	 * ILoadableComponentModel lmodel = fac.loadModel(model);
	 * fac.createComponentInstance(null, lmodel, config, args); //(name, model,
	 * config, args); break; } } } //
	 * getApplicationFactory().createApplication(name, model, config, args); }
	 * catch(Exception e) {
	 * System.err.println("Exception occurred while creating application: ");
	 * e.printStackTrace(); } }
	 */

	/**
	 * Test if a model is startable (e.g. a component).
	 * 
	 * @param model The model.
	 * @return True, if startable (and should therefore also be loadable).
	 */
	public static IFuture isStartable(IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				boolean res = false;
		
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); !res && it.hasNext();)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						res = fac.isStartable(model, null);
					}
				}
				ret.setResult(res? Boolean.TRUE: Boolean.FALSE);
			}
		});

		return ret;
	}

	/**
	 * Get the names of ADF file types supported by this factory.
	 */
	public static IFuture getFileTypes(IServiceContainer container)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				String[] res = new String[0];
		
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); it.hasNext();)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						res = (String[])SUtil.joinArrays(res, fac.getComponentTypes());
					}
				}
				ret.setResult(res);
			}
		});

		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture getFileTypeIcon(IServiceContainer container, final String type)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				Icon res = null;
		
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); it.hasNext() && res == null;)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						res = fac.getComponentTypeIcon(type);
					}
				}
				ret.setResult(res);
			}
		});

		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture getProperties(IServiceContainer container, final String type)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				Map res = null;
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); it.hasNext() && res == null;)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						res = fac.getProperties(type);
					}
				}
				ret.setResult(res);
			}
		});
		
		return ret;
	}

	/**
	 * Get the file type of a model.
	 */
	public static IFuture getFileType(IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		container.getServices(IComponentFactory.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				Collection facts = (Collection)result;
				String res = null;
				if(facts != null)
				{
					for(Iterator it = facts.iterator(); it.hasNext() && res == null;)
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						res = fac.getComponentType(model, null);
					}
				}
				ret.setResult(res);
			}
		});
		
		return ret;
	}

}
