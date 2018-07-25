package jadex.bridge.service.types.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.FactoryFilter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ArgumentsResultsComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.ComponentLifecycleFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.component.impl.MonitoringComponentFeature;
import jadex.bridge.component.impl.NFPropertyComponentFeature;
import jadex.bridge.component.impl.PropertiesComponentFeature;
import jadex.bridge.component.impl.RemoteExecutionComponentFeature;
import jadex.bridge.component.impl.SubcomponentsComponentFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;
import jadex.bridge.service.component.RequiredServicesComponentFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;



/**
 * Standard meta component factory. Uses several sub factories and uses them
 * according to their order and isLoadable() method.
 */
public class SComponentFactory
{
	//-------- todo: move somewhere else? --------
	
	/** The default component features. */
	public static final Collection<IComponentFeatureFactory>	DEFAULT_FEATURES;
	
	static
	{
		Collection<IComponentFeatureFactory>	def_features	= new ArrayList<IComponentFeatureFactory>();
		def_features.add(new ComponentFeatureFactory(IExecutionFeature.class, ExecutionComponentFeature.class));
		def_features.add(new ComponentFeatureFactory(IMonitoringComponentFeature.class, MonitoringComponentFeature.class));
		def_features.add(new ComponentFeatureFactory(IArgumentsResultsFeature.class, ArgumentsResultsComponentFeature.class));
		def_features.add(PropertiesComponentFeature.FACTORY);	// After args for logging
		def_features.add(new ComponentFeatureFactory(IRequiredServicesFeature.class, RequiredServicesComponentFeature.class));
		def_features.add(new ComponentFeatureFactory(IProvidedServicesFeature.class, ProvidedServicesComponentFeature.class));
		def_features.add(new ComponentFeatureFactory(ISubcomponentsFeature.class, SubcomponentsComponentFeature.class, new Class[]{IProvidedServicesFeature.class}, null));
		def_features.add(new ComponentFeatureFactory(IMessageFeature.class, MessageComponentFeature.class));
		def_features.add(RemoteExecutionComponentFeature.FACTORY);	// After message for adding handler
		def_features.add(NFPropertyComponentFeature.FACTORY);
		def_features.add(ComponentLifecycleFeature.FACTORY);
		DEFAULT_FEATURES	= Collections.unmodifiableCollection(def_features);
	}
	
	/**
	 *  Build an ordered list of component features.
	 *  @param facss A list of component feature lists.
	 *  @return An ordered list of component features.
	 */
	public static Collection<IComponentFeatureFactory> orderComponentFeatures(String name, Collection<Collection<IComponentFeatureFactory>> facss)
	{
		DependencyResolver<IComponentFeatureFactory> dr = new DependencyResolver<IComponentFeatureFactory>();

		// visualize feature dependencies for debugging
//		Class<?> cl = SReflect.classForName0("jadex.tools.featuredeps.DepViewerPanel", null);
//		if(cl!=null)
//		{
//			try
//			{
//				Method m = cl.getMethod("createFrame", new Class[]{String.class, DependencyResolver.class});
//				m.invoke(null, new Object[]{name, dr});
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		Map<Class<?>, IComponentFeatureFactory> facsmap = new HashMap<Class<?>, IComponentFeatureFactory>();
		
		for(Collection<IComponentFeatureFactory> facs: facss)
		{
			for(IComponentFeatureFactory fac: facs)
			{
				facsmap.put(fac.getType(), fac);
			}
		}

		Map<Class<?>, IComponentFeatureFactory> odeps = new HashMap<Class<?>, IComponentFeatureFactory>();
		
		for(Collection<IComponentFeatureFactory> facs: facss)
		{
//			IComponentFeatureFactory last = null;
			for(IComponentFeatureFactory fac: facs)
			{
				// Only use the last feature of a given type (allows overriding features)
				if(facsmap.get(fac.getType())==fac)
				{
					dr.addNode(fac);
					// If overridden old position is used as dependency!
					if(odeps.containsKey(fac.getType()))
					{
						IComponentFeatureFactory odep = odeps.get(fac.getType());
						if(odep!=null)
						{
							dr.addDependency(fac, odep);
						}
					}
					// else order in current list
//					else 
//					{
//						if(last!=null)
//							dr.addDependency(fac, last);
//						last = fac;
//					}
					
					Set<Class<?>> sucs = fac.getSuccessors();
					for(Class<?> suc: sucs)
					{
						dr.addDependency(facsmap.get(suc), facsmap.get(fac.getType()));
					}
					Set<Class<?>> pres = fac.getPredecessors();
					for(Class<?> pre: pres)
					{
						dr.addDependency(facsmap.get(fac.getType()), facsmap.get(pre));
					}
				}
				// Save original dependency of the feature
//				else if(!odeps.containsKey(fac.getType()))
//				{
//					odeps.put(fac.getType(), last);
//				}
			}
		}

		Collection<IComponentFeatureFactory> ret = dr.resolveDependencies(true);
		return ret;
	}

	//-------- methods --------
	
	/**
	 *  Check if a component is necessary.
	 *  @param target The target component identifier.
	 *  @return The 
	 */
	public static boolean isComponentStepNecessary(IComponentIdentifier target)
	{
		IComponentIdentifier cid = IComponentIdentifier.LOCAL.get();
		return cid==null? true: !cid.equals(target);
//		return true;
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
			throw new IllegalArgumentException("Model must not be null.");
		if(model.length()==0)
			throw new IllegalArgumentException();
		return exta.scheduleStep(new IComponentStep<IModelInfo>()
		{
			@Classname("loadModel")
			public IFuture<IModelInfo> execute(final IInternalAccess ia)
			{
				final Future<IModelInfo> ret = new Future<IModelInfo>();
				
//				ia.getServiceContainer().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
//						(IServiceProvider)ia.getServiceContainer().searchService( new ServiceQuery<>( IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM, new FactoryFilter(model, null, rid)))
//						ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(model, null, rid)))
						IFuture<IComponentFactory> fut = getFactory(new FactoryFilter(model, null, rid), ia);
						fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, IModelInfo>(ret)
						{
							public void customResultAvailable(IComponentFactory fac)
							{
								fac.loadModel(model, null, rid)
									.addResultListener(new DelegationResultListener<IModelInfo>(ret));
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
		Future<Boolean> ret = new Future<Boolean>();
		
		exta.scheduleStep(new IComponentStep<Boolean>()
		{
			@Classname("isLoadable")
			public IFuture<Boolean> execute(final IInternalAccess ia)
			{
				final Future<Boolean> ret = new Future<Boolean>();
//				ia.getServiceContainer().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
//						(IServiceProvider)ia.getServiceContainer().searchService( new ServiceQuery<>( IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM, new FactoryFilter(model, null, rid)))
//						ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(model, null, rid)))
						IFuture<IComponentFactory> fut = getFactory(new FactoryFilter(model, null, rid), ia);
						fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, Boolean>(ret)
						{
							public void customResultAvailable(IComponentFactory fac)
							{
								fac.isLoadable(model, null, rid)
									.addResultListener(new DelegationResultListener<Boolean>(ret));
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
	 * Test if a model can be loaded by the factory.
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	public static IFuture<Boolean> isModelType(final IExternalAccess exta, final String model, final IResourceIdentifier rid)
//	public static IFuture<Boolean> isModelType(final IExternalAccess exta, final String model, final Collection<String> allowedtypes, final IResourceIdentifier rid)
	{
//		return new Future<Boolean>(Boolean.TRUE);
		
		IFuture<Boolean> ret = null;
		if(!isComponentStepNecessary(exta.getId()))
		{
//			System.out.println("direct isModelType");
//			ret = isModelType(model, allowedtypes, rid, exta);
			ret = isModelType(model, rid, exta);
		}
		else
		{
//			System.out.println("stepped isModelTypes");
			ret = (IFuture<Boolean>)exta.scheduleStep(new IComponentStep<Boolean>()
			{
				@Classname("isModelType")
				public IFuture<Boolean> execute(IInternalAccess ia)
				{
//					return isModelType(model, allowedtypes, rid, exta);
					return isModelType(model, rid, exta);
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
//	private static IFuture<Boolean> isModelType(final String model, final Collection<String> allowedtypes, final IResourceIdentifier rid, final IExternalAccess ea)//IInternalAccess ia)
	private static IFuture<Boolean> isModelType(final String model, final IResourceIdentifier rid, final IExternalAccess ea)//IInternalAccess ia)
	{
//		return new Future<Boolean>(Boolean.TRUE);
		
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
		
//				final long start = System.currentTimeMillis(); 
				final Future<Boolean> ret = new Future<Boolean>();
				ea.searchServices(new ServiceQuery<>(IComponentFactory.class))
					.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactory>, Boolean>(ret)
				{
					public void customResultAvailable(Collection<IComponentFactory> facs)
					{
//						long dur = System.currentTimeMillis()-start; 
//						System.out.println("needed search: "+dur);
//						System.out.println("found facs: "+facs.size());
						
						facs = reorderMultiFactory(facs);
						
//						if(model.endsWith("application.xml"))
//							System.out.println("model3:"+model);
						if(facs.size()==0)
						{
							ret.setResult(Boolean.FALSE);
						}
						else
						{
//							checkComponentType(model, facs.toArray(new IComponentFactory[0]), 0, ea, rid, allowedtypes)
//								.addResultListener(createResultListener(new DelegationResultListener<Boolean>(ret), ea));
							checkComponentType(model, facs.toArray(new IComponentFactory[0]), 0, ea, rid)
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
	protected static IFuture<Boolean> checkComponentType(final String model, final IComponentFactory[] facts, final int i, 
		final IExternalAccess ea, final IResourceIdentifier rid)
//	protected static IFuture<Boolean> checkComponentType(final String model, final IComponentFactory[] facts, final int i, 
//		final IExternalAccess ea, final IResourceIdentifier rid, final Collection<String> allowedtypes)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
//		ret.setResult(Boolean.TRUE);
//		return ret;
		
		if(i>=facts.length)
		{
			ret.setResult(Boolean.FALSE);
		}
		else
		{
//			facts[i].getComponentType(model, null, rid)
//				.addResultListener(createResultListener(new ExceptionDelegationResultListener<String, Boolean>(ret)
//			{
//				public void customResultAvailable(String result)
//				{
//					if(result!=null)
//					{
//						ret.setResult(allowedtypes.contains(result));
//					}
//					else
//					{
//						checkComponentType(model, facts, i+1, ea, rid, allowedtypes)
//							.addResultListener(createResultListener(new DelegationResultListener<Boolean>(ret), ea));
//					}
//				}
//			}, ea));
			
			facts[i].isLoadable(model, null, rid)
				.addResultListener(createResultListener(new DelegationResultListener<Boolean>(ret)
			{
				public void customResultAvailable(Boolean result)
				{
					//System.out.println("model: "+model+" "+result+" "+facts[i]);
					if(result!=null && result.booleanValue())
					{
						ret.setResult(result);
					}
					else
					{
						checkComponentType(model, facts, i+1, ea, rid)
							.addResultListener(createResultListener(new DelegationResultListener<Boolean>(ret), ea));
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
//				ia.getServiceContainer().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
//						SServiceProvider.getService((IServiceProvider)ia.getServiceContainer(), IComponentFactory.class, 
//							RequiredServiceInfo.SCOPE_PLATFORM, new FactoryFilter(model, null, rid))
//						ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(model, null, rid)))
						getFactory(new FactoryFilter(model, null, rid), ia)
							.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, Boolean>(ret)
						{
							public void customResultAvailable(IComponentFactory fac)
							{
								fac.isStartable(model, null, rid)
									.addResultListener(new DelegationResultListener<Boolean>(ret));
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
//				IFuture<Collection<IComponentFactory>> fut = SServiceProvider.getServices((IServiceProvider)ia.getServiceContainer(), 
//					IComponentFactory.class, RequiredServiceInfo.SCOPE_PLATFORM, new FactoryFilter(type));
//				ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(type)))
				IFuture<IComponentFactory> fut = getFactory(new FactoryFilter(type), ia);
				fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, byte[]>(ret)
				{
					public void customResultAvailable(IComponentFactory fac)
					{
//						System.out.println("facs: "+type+" "+facs);
//						reorderMultiFactory(facs);
//						IComponentFactory fac = facs.iterator().next();
						
//						System.out.println("selected: "+fac);
						
//						IComponentFactory fac = (IComponentFactory)result;
						fac.getComponentTypeIcon(type).addResultListener(new DelegationResultListener<byte[]>(ret)
						{
							public void customResultAvailable(byte[] result)
							{
//								JFrame f = new JFrame();
//								f.add(new JLabel(new ImageIcon(result)), BorderLayout.CENTER);
//								f.pack();
//								f.show();
//								System.out.println("found icon: "+type+" "+result.length);
								super.customResultAvailable(result);
							}
						});
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
		}).addResultListener(new DelegationResultListener<byte[]>(ret)
		{
//			public void customResultAvailable(byte[] result)
//			{
//				System.out.println("found icon: "+type+" "+(result==null? "null": result.length));
//				super.customResultAvailable(result);
//			}
//			public void exceptionOccurred(Exception exception)
//			{
////				System.out.println("exec: "+exception);
//				super.exceptionOccurred(exception);
//			}
		});
		
		return ret;
	}

	/**
	 * Get a default icon for a file type.
	 */
	public static IFuture<Object> getProperty(IExternalAccess exta, final String type, final String key)
	{
		final Future<Object> ret = new Future<Object>();
		
		exta.scheduleStep(new ImmediateComponentStep<Object>()
		{
			@Classname("getProperty")
			public IFuture<Object> execute(final IInternalAccess ia)
			{
				final Future<Object> ret = new Future<Object>();
				Collection<IComponentFactory> result	= ia.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(IComponentFactory.class));
				boolean found = false;
				if(result!=null)
				{
					for(Iterator<IComponentFactory> it=result.iterator(); it.hasNext() && !found; )
					{
						IComponentFactory fac = (IComponentFactory)it.next();
						if(SUtil.arrayToSet(fac.getComponentTypes()).contains(type))
						{
							Map<String, Object> res = fac.getProperties(type);
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
				return ret;
			}
		}).addResultListener(new DelegationResultListener<Object>(ret));
		
		return ret;
	}

	/**
	 * Get the file type of a model.
	 */
	public static IFuture<String> getFileType(IExternalAccess exta, final String model, final IResourceIdentifier rid)
	{
		final Future<String> ret = new Future<String>();
		
		exta.scheduleStep(new IComponentStep<String>()
		{
			@Classname("getFileType")
			public IFuture<String> execute(final IInternalAccess ia)
			{
				final Future<String> ret = new Future<String>();
				ILibraryService ls = ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
//						IFuture<IComponentFactory> fut = SServiceProvider.getService((IServiceProvider)ia.getServiceContainer(), IComponentFactory.class, 
//							RequiredServiceInfo.SCOPE_PLATFORM, new FactoryFilter(model, null, rid));
//						ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(model, null, rid)))
				IFuture<IComponentFactory> fut = getFactory(new FactoryFilter(model, null, rid), ia);
				fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, String>(ret)
				{
					public void customResultAvailable(IComponentFactory fac)
					{
						fac.getComponentType(model, null, rid)
							.addResultListener(new DelegationResultListener<String>(ret));
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
		}).addResultListener(new DelegationResultListener<String>(ret));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected static IFuture<IComponentFactory> getFactory(final FactoryFilter filter, IInternalAccess ia)
	{
		Collection<IComponentFactory> facs = ia.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(IComponentFactory.class));
		if(facs!=null && facs.size()>0)
		{
			return doFindFactory(facs.iterator(), filter, null);
		}
		else
		{
			return new Future<IComponentFactory>(new ServiceNotFoundException(""+filter));
		}
	}
	
	/**
	 *  Find a matching factory in the given iterator.
	 */
	protected static IFuture<IComponentFactory>	doFindFactory(Iterator<IComponentFactory> facs, FactoryFilter filter, IComponentFactory multi)
	{
		if(facs.hasNext())
		{
			IComponentFactory	fac	= facs.next();
			IFuture<Boolean>	match	= filter.filter(fac);
			if(match.isDone())
			{
				// Synchronous version
				if(match.get())
				{
					Map<String, Object> ps = fac.getProperties(null);
					if(ps==null || !ps.containsKey("multifactory"))
					{
						return new Future<>(fac);
					}					
					else
					{
						// Found fac is multi -> remember and continue search to prefer other factories
						return doFindFactory(facs, filter, fac);						
					}
				}
				else
				{
					return doFindFactory(facs, filter, multi);
				}
			}
			else
			{
				// Asynchronous version
				Future<IComponentFactory>	ret	= new Future<>();
				match.addResultListener(new ExceptionDelegationResultListener<Boolean, IComponentFactory>(ret)
				{
					@Override
					public void customResultAvailable(Boolean result) throws Exception
					{
						if(result)
						{
							Map<String, Object> ps = fac.getProperties(null);
							if(ps==null || !ps.containsKey("multifactory"))
							{
								ret.setResult(fac);
							}					
							else
							{
								// Found fac is multi -> remember and continue search to prefer other factories
								doFindFactory(facs, filter, multi)
									.addResultListener(new DelegationResultListener<>(ret));
							}
						}
						else
						{
							doFindFactory(facs, filter, multi)
								.addResultListener(new DelegationResultListener<>(ret));
						}
					}
				});
				return ret;
			}
		}
		else if(multi!=null)
		{
			return new Future<>(multi);
		}
		else
		{
			return new Future<>(new ServiceNotFoundException(""+filter));
		}
	}
	
	/**
	 *  Exclude the multifactory from a collection.
	 *  @param facs The factories.
	 *  @return cleaned collection.
	 */
	protected static Collection<IComponentFactory> reorderMultiFactory(Collection<IComponentFactory> facs)
	{
		Collection<IComponentFactory> ret = facs;
		if(facs!=null && facs.size()>1)
		{
			IComponentFactory multi = null;
			ret = new ArrayList<IComponentFactory>();
			for(IComponentFactory tmp: facs)
			{
				Map<String, Object> ps = tmp.getProperties(null);
				if(ps==null || !ps.containsKey("multifactory"))
				{
					ret.add(tmp);
				}
				else
				{
					multi = tmp;
				}
			}
			if(multi!=null)
				ret.add(multi);
		}
		return ret;
	}
}
