package jadex.base;

import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentFactory;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.IServiceContainer;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;


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
		
		SServiceProvider.getService(container, ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(container, new ComponentFactorySelector(model, null, ls.getClassLoader())).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(fac!=null ? fac.loadModel(model, null, ls.getClassLoader()) : null);
					}
				});
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
	public static IFuture isLoadable(final IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(container, ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(container, new ComponentFactorySelector(model, null, ls.getClassLoader())).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(fac!=null ? new Boolean(fac.isLoadable(model, null, ls.getClassLoader())) : Boolean.FALSE);
					}
				});
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
	public static IFuture isStartable(final IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(container, ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(container, new ComponentFactorySelector(model, null, ls.getClassLoader())).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(fac!=null ? new Boolean(fac.isStartable(model, null, ls.getClassLoader())) : Boolean.FALSE);
					}
				});
			}
		});

		return ret;
	}

	/**
	 * Get the names of ADF file types supported by this factory.
	 * /
	public static IFuture getFileTypes(IServiceContainer container)
	{
		final Future ret = new Future();
		
		SServiceProvider.getServices(container, IComponentFactory.class).addResultListener(new DefaultResultListener()
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
	}*/

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture getFileTypeIcon(IServiceContainer container, final String type)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(container, new ComponentFactorySelector(type)).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentFactory fac = (IComponentFactory)result;
				ret.setResult(fac!=null ? fac.getComponentTypeIcon(type) : null);
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
		
		SServiceProvider.getService(container, new ComponentFactorySelector(type)).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentFactory fac = (IComponentFactory)result;
				ret.setResult(fac!=null ? fac.getProperties(type) : null);
			}
		});
		
		return ret;
	}

	/**
	 * Get the file type of a model.
	 */
	public static IFuture getFileType(final IServiceContainer container, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(container, ILibraryService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(container, new ComponentFactorySelector(model, null, ls.getClassLoader())).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(fac!=null ? fac.getComponentType(model, null, ls.getClassLoader()) : null);
					}
				});
			}
		});
		
		return ret;
	}

}
