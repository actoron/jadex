package jadex.base;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.ServiceNotFoundException;
import jadex.commons.service.library.ILibraryService;


/**
 * Standard meta component factory. Uses several sub factories and uses them
 * according to their order and isLoadable() method.
 */
public class SComponentFactory
{
	/**
	 * Load an component model.
	 * @param model The model.
	 * @return The loaded model.
	 */
	public static IFuture loadModel(final IServiceProvider provider, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, ILibraryService.class)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(provider, new ComponentFactorySelector(model, null, ls.getClassLoader()))
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(fac.loadModel(model, null, ls.getClassLoader()));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						if(exception instanceof ServiceNotFoundException)
						{
							ret.setResult(null);
						}
						else
						{
							super.exceptionOccurred(source, exception);
						}
					}
				});
			}
		});
		return ret;
	}

	/**
	 * Test if a model can be loaded by the factory.
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	public static IFuture isLoadable(final IServiceProvider provider, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, ILibraryService.class)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(provider, new ComponentFactorySelector(model, null, ls.getClassLoader()))
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(new Boolean(fac.isLoadable(model, null, ls.getClassLoader())));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						if(exception instanceof ServiceNotFoundException)
						{
							ret.setResult(Boolean.FALSE);
						}
						else
						{
							super.exceptionOccurred(source, exception);
						}
					}
				});
			}
		});

		return ret;
	}

	/**
	 * Test if a model is startable (e.g. a component).
	 * @param model The model.
	 * @return True, if startable (and should therefore also be loadable).
	 */
	public static IFuture isStartable(final IServiceProvider provider, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, ILibraryService.class)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(provider, new ComponentFactorySelector(model, null, ls.getClassLoader()))
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(new Boolean(fac.isStartable(model, null, ls.getClassLoader())));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						if(exception instanceof ServiceNotFoundException)
						{
							ret.setResult(Boolean.FALSE);
						}
						else
						{
							super.exceptionOccurred(source, exception);
						}
					}
				});
			}			
		});

		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture getFileTypeIcon(IServiceProvider provider, final String type)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, new ComponentFactorySelector(type))
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IComponentFactory fac = (IComponentFactory)result;
				ret.setResult(fac.getComponentTypeIcon(type));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				if(exception instanceof ServiceNotFoundException)
				{
					ret.setResult(null);
				}
				else
				{
					super.exceptionOccurred(source, exception);
				}
			}
		});

		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture getProperties(IServiceProvider provider, final String type)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, new ComponentFactorySelector(type))
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IComponentFactory fac = (IComponentFactory)result;
				ret.setResult(fac.getProperties(type));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				if(exception instanceof ServiceNotFoundException)
				{
					ret.setResult(null);
				}
				else
				{
					super.exceptionOccurred(source, exception);
				}
			}
		});
		
		return ret;
	}

	/**
	 * Get the file type of a model.
	 */
	public static IFuture getFileType(final IServiceProvider provider, final String model)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, ILibraryService.class).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final ILibraryService ls = (ILibraryService)result;
				
				SServiceProvider.getService(provider, new ComponentFactorySelector(model, null, ls.getClassLoader()))
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						ret.setResult(fac.getComponentType(model, null, ls.getClassLoader()));
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						if(exception instanceof ServiceNotFoundException)
						{
							ret.setResult(null);
						}
						else
						{
							super.exceptionOccurred(source, exception);
						}
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Find the class loader for a component.
	 *  Use component class loader for local components
	 *  and current platform class loader for remote components.
	 *  @param cid	The component id.
	 *  @return	The class loader.
	 */
	public static IFuture getClassLoader(final IComponentIdentifier cid, final IControlCenter jcc)
	{
		final Future	ret	= new Future();
		
		// Local component when platform name is same as JCC platform name
		if(cid.getPlatformName().equals(jcc.getComponentIdentifier().getPlatformName()))
		{
			SServiceProvider.getService(jcc.getServiceProvider(), IComponentManagementService.class)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object source, Object result)
				{
					IComponentManagementService	cms	= (IComponentManagementService)result;
					cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object source, Object result)
						{
							IExternalAccess	ea	= (IExternalAccess)result;
							ret.setResult(ea.getModel().getClassLoader());
						}
					});
				}
			});
		}
		
		// Remote component
		else
		{
			SServiceProvider.getService(jcc.getServiceProvider(), ILibraryService.class)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object source, Object result)
				{
					ILibraryService	ls	= (ILibraryService)result;
					ret.setResult(ls.getClassLoader());
				}
			});
		}
		return ret;
	}

}
