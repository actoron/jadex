package jadex.base;

import jadex.bridge.ComponentFactorySelector;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.IServiceProvider;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.ServiceNotFoundException;
import jadex.commons.service.library.ILibraryService;
import jadex.xml.annotation.XMLClassname;


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
	public static IFuture loadModel(IExternalAccess exta, final String model)
	{
		final Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("loadModel")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				
				SServiceProvider.getService(ia.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceProvider(), new ComponentFactorySelector(model, null, ls.getClassLoader()))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.loadModel(model, null, ls.getClassLoader())
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ServiceNotFoundException)
								{
									ret.setResult(null);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						}));
					}
				}));
				
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}

	/**
	 * Test if a model can be loaded by the factory.
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	public static IFuture isLoadable(IExternalAccess exta, final String model)
	{
		Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("isLoadable")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceProvider(), new ComponentFactorySelector(model, null, ls.getClassLoader()))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.isLoadable(model, null, ls.getClassLoader())
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ServiceNotFoundException)
								{
									ret.setResult(Boolean.FALSE);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						}));
					}
				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));

		return ret;
	}

	/**
	 * Test if a model is startable (e.g. a component).
	 * @param model The model.
	 * @return True, if startable (and should therefore also be loadable).
	 */
	public static IFuture isStartable(IExternalAccess exta, final String model)
	{
		Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("isStartable")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceProvider(), new ComponentFactorySelector(model, null, ls.getClassLoader()))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.isStartable(model, null, ls.getClassLoader())
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ServiceNotFoundException)
								{
									ret.setResult(Boolean.FALSE);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						}));
					}			
				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));

		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture getFileTypeIcon(IExternalAccess exta, final String type)
	{
		Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("getFileTypeIcon")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceProvider(), new ComponentFactorySelector(type))
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
						fac.getComponentTypeIcon(type).addResultListener(new DelegationResultListener(ret));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(exception instanceof ServiceNotFoundException)
						{
							ret.setResult(null);
						}
						else
						{
							super.exceptionOccurred(exception);
						}
					}
				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
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
			public void customResultAvailable(Object result)
			{
				IComponentFactory fac = (IComponentFactory)result;
				ret.setResult(fac.getProperties(type));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof ServiceNotFoundException)
				{
					ret.setResult(null);
				}
				else
				{
					super.exceptionOccurred(exception);
				}
			}
		});
		
		return ret;
	}

	/**
	 * Get the file type of a model.
	 */
	public static IFuture getFileType(IExternalAccess exta, final String model)
	{
		final Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("getFileType")
			public Object execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceProvider(), new ComponentFactorySelector(model, null, ls.getClassLoader()))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.getComponentType(model, null, ls.getClassLoader())
									.addResultListener(new DelegationResultListener(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(exception instanceof ServiceNotFoundException)
								{
									ret.setResult(null);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						}));
					}
				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
}
