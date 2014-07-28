package jadex.bridge.service.types.factory;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.ComponentFactorySelector;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.IRemoteFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;



/**
 * Standard meta component factory. Uses several sub factories and uses them
 * according to their order and isLoadable() method.
 */
public class SComponentFactory
{
	/**
	 *  Check if a component is necessary.
	 *  @param target The target component identifier.
	 *  @return The 
	 */
	public static boolean isComponentStepNecessary(IComponentIdentifier target)
	{
		IComponentIdentifier cid = IComponentIdentifier.LOCAL.get();
		return cid==null? true: !cid.equals(target);
	}
	
	/**
	 *  Create a result listener which is executed as an component step.
	 *  @param The original listener to be called.
	 *  @return The listener.
	 */
	public static <T> IResultListener<T> createResultListener(IResultListener<T> listener, IExternalAccess ea)
	{
		return new ComponentResultListener<T>(listener, ea);
	}

//	/**
//	 *  Create a result listener which is executed as an component step.
//	 *  @param The original listener to be called.
//	 *  @return The listener.
//	 */
//	public <T> IIntermediateResultListener<T> createResultListener(IIntermediateResultListener<T> listener, IExternalAccess ea)
//	{
//		return new IntermediateComponentResultListener<T>(listener, ea);
//	}
	
	/**
	 * Load an component model.
	 * @param model The model.
	 * @return The loaded model.
	 */
	public static IFuture<IModelInfo> loadModel(IExternalAccess exta, final String model, final IResourceIdentifier rid)
	{
		if(model==null)
			throw new NullPointerException();
		if(model.length()==0)
			throw new IllegalArgumentException();
		return exta.scheduleStep(new IComponentStep<IModelInfo>()
		{
			@Classname("loadModel")
			public IFuture<IModelInfo> execute(final IInternalAccess ia)
			{
				final Future<IModelInfo> ret = new Future<IModelInfo>();
				
//				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(model, null, rid))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.loadModel(model, null, rid)
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
//					}
//				}));
				
				return ret;
			}
		});
	}

	/**
	 * Test if a model can be loaded by the factory.
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	public static IFuture<Boolean> isLoadable(IExternalAccess exta, final String model, final IResourceIdentifier rid)
	{
		Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep<Boolean>()
		{
			@Classname("isLoadable")
			public IFuture<Boolean> execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
//				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(model, null, rid))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.isLoadable(model, null, rid)
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
//					}
//				}));
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
	public static IFuture<Boolean> isModelType(final IExternalAccess exta, final String model, final Collection allowedtypes, final IResourceIdentifier rid)
	{
//		return new Future<Boolean>(Boolean.TRUE);
		
		IFuture<Boolean> ret = null;
		if(!isComponentStepNecessary(exta.getComponentIdentifier()))
		{
//			System.out.println("direct isModelType");
			ret = isModelType(model, allowedtypes, rid, exta);
		}
		else
		{
			System.out.println("stepped isModelTypes");
			ret = (IFuture<Boolean>)exta.scheduleStep(new IComponentStep<Boolean>()
			{
				@Classname("isModelType")
				public IFuture<Boolean> execute(IInternalAccess ia)
				{
					return isModelType(model, allowedtypes, rid, exta);
				}
				
				// For debugging intermediate future bug. Used in MicroAgentInterpreter
				public String toString()
				{
					return "IsModelType("+model+")";
				}
			});
		}
		return ret;
	}
	
	/**
	 * Test if a model can be loaded by the factory.
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	private static IFuture<Boolean> isModelType(final String model, final Collection allowedtypes, final IResourceIdentifier rid, final IExternalAccess ea)//IInternalAccess ia)
	{
//		Future<Boolean> ret = new Future<Boolean>();
//		if(model.endsWith("application.xml"))
//			System.out.println("model1:"+model);
		
//		exta.scheduleStep(new IComponentStep<Boolean>()
//		{
//			@Classname("isModelType")
//			public IFuture<Boolean> execute(final IInternalAccess ia)
//			{
//				if(model.endsWith("application.xml"))
//					System.out.println("model2:"+model);
				final Future<Boolean> ret = new Future<Boolean>();
				SServiceProvider.getServices(ea.getServiceProvider(), IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactory>, Boolean>(ret)
				{
					public void customResultAvailable(Collection<IComponentFactory> facs)
					{
//						if(model.endsWith("application.xml"))
//							System.out.println("model3:"+model);
						if(facs.size()==0)
						{
							ret.setResult(Boolean.FALSE);
						}
						else
						{
							checkComponentType(model, facs.toArray(new IComponentFactory[0]), 0, ea, rid, allowedtypes)
								.addResultListener(createResultListener(new DelegationResultListener<Boolean>(ret), ea));
						}
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
				}, ea));
				return ret;
//			}
//		}).addResultListener(new DelegationResultListener(ret));
//
//		return ret;
	}

	/**
	 * 
	 */
	protected static IFuture checkComponentType(final String model, final IComponentFactory[] facts, final int i, 
		final IExternalAccess ea, final IResourceIdentifier rid, final Collection allowedtypes)
	{
		final Future ret = new Future();
		if(i>=facts.length)
		{
			ret.setResult(Boolean.FALSE);
		}
		else
		{
			facts[i].getComponentType(model, null, rid)
				.addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					if(result!=null)
					{
						ret.setResult(allowedtypes.contains(result));
					}
					else
					{
						checkComponentType(model, facts, i+1, ea, rid, allowedtypes)
							.addResultListener(createResultListener(new DelegationResultListener(ret), ea));
					}
				}
			}, ea));
		}
		return ret;
	}
	
	/**
	 * Test if a model is startable (e.g. a component).
	 * @param model The model.
	 * @return True, if startable (and should therefore also be loadable).
	 */
	public static IFuture<Boolean> isStartable(IExternalAccess exta, final String model, final IResourceIdentifier rid)
	{
		Future<Boolean> ret = new Future<Boolean>();
		
		exta.scheduleStep(new IComponentStep<Boolean>()
		{
			@Classname("isStartable")
			public IFuture<Boolean> execute(final IInternalAccess ia)
			{
				final Future<Boolean> ret = new Future<Boolean>();
//				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(model, null, rid))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.isStartable(model, null, rid)
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
//					}			
//				}));
				return ret;
			}
		}).addResultListener(new DelegationResultListener<Boolean>(ret));

		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture<byte[]> getFileTypeIcon(IExternalAccess exta, final String type)
	{
		Future<byte[]> ret = new Future<byte[]>();
		
		exta.scheduleStep(new IComponentStep<byte[]>()
		{
			@Classname("getFileTypeIcon")
			public IFuture<byte[]> execute(final IInternalAccess ia)
			{
				final Future<byte[]> ret = new Future<byte[]>();
				SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(type))
					.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<Object, byte[]>(ret)
				{
					public void customResultAvailable(Object result)
					{
						IComponentFactory fac = (IComponentFactory)result;
//						System.out.println("fac: "+type+" "+fac);
						fac.getComponentTypeIcon(type).addResultListener(new DelegationResultListener<byte[]>(ret)
						{
							public void customResultAvailable(byte[] result)
							{
//								JFrame f = new JFrame();
//								f.add(new JLabel(new ImageIcon(result)), BorderLayout.CENTER);
//								f.pack();
//								f.show();
								super.customResultAvailable(result);
							}
						}
						);
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
		}).addResultListener(new DelegationResultListener<byte[]>(ret));
		
		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture<Object> getProperty(IExternalAccess exta, final String type, final String key)
	{
		final Future<Object> ret = new Future<Object>();
		
		exta.scheduleImmediate(new IComponentStep<Object>()
		{
			@Classname("getProperty")
			public IFuture<Object> execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getServices(ia.getServiceContainer(), IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						boolean found = false;
						if(result!=null)
						{
							for(Iterator it=((Collection)result).iterator(); it.hasNext() && !found; )
							{
								IComponentFactory fac = (IComponentFactory)it.next();
								if(SUtil.arrayToSet(fac.getComponentTypes()).contains(type))
								{
									Map res = fac.getProperties(type);
									if(res!=null && res.containsKey(key))
									{
										ret.setResult(res.get(key));
										found = true;
									}
								}
							}
							if(!found)
								ret.setResult(null);
						}
						else
						{
							ret.setResult(null);
						}
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
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}

	/**
	 * Get the file type of a model.
	 */
	public static IFuture<String> getFileType(IExternalAccess exta, final String model, final IResourceIdentifier rid)
	{
		final Future ret = new Future();
		
		exta.scheduleStep(new IComponentStep<String>()
		{
			@Classname("getFileType")
			public IFuture<String> execute(final IInternalAccess ia)
			{
				final Future ret = new Future();
				SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						final ILibraryService ls = (ILibraryService)result;
						
						SServiceProvider.getService(ia.getServiceContainer(), new ComponentFactorySelector(model, null, rid))
							.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IComponentFactory fac = (IComponentFactory)result;
								fac.getComponentType(model, null, rid)
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
