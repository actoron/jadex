package jadex.bridge.service.types.factory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import jadex.base.Starter;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.FactoryFilter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IPriorityComponentStep;
import jadex.bridge.IResourceIdentifier;
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
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.ComposedFilter;
import jadex.commons.FileFilter;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.javaparser.SJavaParser;



/**
 * Standard meta component factory. Uses several sub factories and uses them
 * according to their order and isLoadable() method.
 */
public class SComponentFactory
{
	//-------- todo: move somewhere else? --------
	
	/** The default component features. */
	public static final Collection<IComponentFeatureFactory> DEFAULT_FEATURES;
	
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
						if(facsmap.get(fac.getType())!=null && facsmap.get(suc)!=null)
						{
							dr.addDependency(facsmap.get(suc), facsmap.get(fac.getType()));
						}
//						else
//						{
//							System.out.println("Declared dependency not found, ignoring: "+suc+" "+fac.getType());
//						}
					}
					Set<Class<?>> pres = fac.getPredecessors();
					for(Class<?> pre: pres)
					{
						if(facsmap.get(pre)!=null && facsmap.get(fac.getType())!=null)
						{
							dr.addDependency(facsmap.get(fac.getType()), facsmap.get(pre));
						}
//						else
//						{
//							System.out.println("Declared dependency not found, ignoring: "+pre+" "+fac.getType());
//						}
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
				
				SComponentManagementService.getResourceIdentifier(model, rid, ia).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, IModelInfo>(ret) 
				{
					@Override
					public void customResultAvailable(IResourceIdentifier result) throws Exception 
					{
						IFuture<IComponentFactory> fut = getFactory(new FactoryFilter(model, null, rid), ia);
						fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, IModelInfo>(ret)
						{
							public void customResultAvailable(IComponentFactory fac)
							{
								fac.loadModel(model, null, null, rid).addResultListener(new DelegationResultListener<IModelInfo>(ret));
							}
							
//							public void exceptionOccurred(Exception exception)
//							{
//								if(exception instanceof ServiceNotFoundException)
//								{
//									System.out.println("No factory found: "+model+" "+rid);
//									ret.setResult(null);
//								}
//								else
//								{
//									super.exceptionOccurred(exception);
//								}
//							}
						}));
					}
				});
				
				return ret;
			}
		});
	}
	
//	Collection<IComponentFactory> facs = agent.getFeature(IRequiredServicesFeature.class).getLocalServices(new ServiceQuery<>( IComponentFactory.class, ServiceScope.PLATFORM));
//	FactoryFilter facfilter = new FactoryFilter(filename, null, rid);
//	
//	SFilter.applyFilter(facs, facfilter).addResultListener(new IResultListener<Collection<IComponentFactory>>()
//	{
//		public void resultAvailable(Collection<IComponentFactory> result)
//		{
//			if (result != null && result.size() > 0)
//			{
//				result.iterator().next().loadModel(filename, null, rid)
//					.addResultListener(new DelegationResultListener<IModelInfo>(ret));
//			}
//			else
//			{
//				ret.setResult(null);
//			}
//		}
//		public void exceptionOccurred(Exception exception)
//		{
//			ret.setException(exception);
//		}
//	});

	/**
	 * Test if a model can be loaded by the factory.
	 * @param model The model.
	 * @return True, if model can be loaded.
	 */
	public static IFuture<Boolean> isLoadable(IExternalAccess exta, final String model, final Object pojo, final IResourceIdentifier rid)
	{
		Future<Boolean> ret = new Future<Boolean>();
		
//		System.out.println("Scom isLoad: "+model);
		
		exta.scheduleStep(new IComponentStep<Boolean>()
		{
			@Classname("isLoadable")
			public IFuture<Boolean> execute(final IInternalAccess ia)
			{
				final Future<Boolean> ret = new Future<Boolean>();
//				ia.getServiceContainer().searchService( new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM))
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
//						(IServiceProvider)ia.getServiceContainer().searchService( new ServiceQuery<>( IComponentFactory.class, ServiceScope.PLATFORM, new FactoryFilter(model, null, rid)))
//						ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(model, null, rid)))
						IFuture<IComponentFactory> fut = getFactory(new FactoryFilter(model, null, rid), ia);
						fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, Boolean>(ret)
						{
							public void customResultAvailable(IComponentFactory fac)
							{
								fac.isLoadable(model, pojo, null, rid)
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
		
				final long start = System.currentTimeMillis(); 
				final Future<Boolean> ret = new Future<Boolean>();
				ea.searchServices(new ServiceQuery<>(IComponentFactory.class))
					.addResultListener(createResultListener(new ExceptionDelegationResultListener<Collection<IComponentFactory>, Boolean>(ret)
				{
					public void customResultAvailable(Collection<IComponentFactory> facs)
					{
						long dur = System.currentTimeMillis()-start; 
						//System.out.println("needed search: "+dur+" "+ea);
						if(facs.size()==0)
							System.out.println("found facs: "+facs.size());
						
						// todo: refactor to hide inner factories
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
			//System.out.println("found no fac: "+model+" "+SUtil.arrayToString(facts));
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
			
			facts[i].isLoadable(model, null, null, rid)
				.addResultListener(createResultListener(new DelegationResultListener<Boolean>(ret)
			{
				public void customResultAvailable(Boolean result)
				{
					if(result!=null && result.booleanValue())
					{
						//System.out.println("found model: "+model+" "+result+" "+facts[i]);
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
	public static IFuture<Boolean> isStartable(IExternalAccess exta, final String model, final Object pojo, final IResourceIdentifier rid)
	{
		Future<Boolean> ret = new Future<Boolean>();
				
		exta.scheduleStep(new IComponentStep<Boolean>()
		{
			@Classname("isStartable")
			public IFuture<Boolean> execute(final IInternalAccess ia)
			{
				final Future<Boolean> ret = new Future<Boolean>();
//				ia.getServiceContainer().searchService( new ServiceQuery<>( ILibraryService.class, ServiceScope.PLATFORM))
//					.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						final ILibraryService ls = (ILibraryService)result;
						
//						SServiceProvider.getService((IServiceProvider)ia.getServiceContainer(), IComponentFactory.class, 
//							ServiceScope.PLATFORM, new FactoryFilter(model, null, rid))
//						ia.getServiceContainer().searchService( new ServiceQuery<>( new ComponentFactorySelector(model, null, rid)))
						getFactory(new FactoryFilter(model, null, rid), ia)
							.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentFactory, Boolean>(ret)
						{
							public void customResultAvailable(IComponentFactory fac)
							{
								fac.isStartable(model, pojo, null, rid)
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
//					IComponentFactory.class, ServiceScope.PLATFORM, new FactoryFilter(type));
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
		
		exta.scheduleStep(new IPriorityComponentStep<Object>()
		{
			@Classname("getProperty")
			public IFuture<Object> execute(final IInternalAccess ia)
			{
				final Future<Object> ret = new Future<Object>();
				Collection<IComponentFactory> result	= ia.getFeature(IRequiredServicesFeature.class).getLocalServices(new ServiceQuery<>(IComponentFactory.class));
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
	public static IFuture<IComponentFactory> getFactory(final FactoryFilter filter, IInternalAccess ia)
	{
		Collection<IComponentFactory> facs = ia.getFeature(IRequiredServicesFeature.class).getLocalServices(new ServiceQuery<>(IComponentFactory.class));
		//System.out.println("getFactory: "+facs);
		if(facs!=null && facs.size()>0)
		{
			return doFindFactory(reorderMultiFactory(facs).iterator(), filter);
		}
		else
		{
			return new Future<IComponentFactory>(new ServiceNotFoundException("facs="+facs+", filter="+filter));
		}
	}
	
	/**
	 *  Find a matching factory in the given iterator.
	 */
	protected static IFuture<IComponentFactory>	doFindFactory(Iterator<IComponentFactory> facs, FactoryFilter filter)
	{
		if(facs.hasNext())
		{
			IComponentFactory fac = facs.next();
			IFuture<Boolean> match = filter.filter(fac);
			if(match.isDone())
			{
				// Synchronous version
				if(match.get())
				{
					return new Future<>(fac);
				}
				else
				{
					return doFindFactory(facs, filter);
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
							ret.setResult(fac);
						}
						else
						{
							doFindFactory(facs, filter).addResultListener(new DelegationResultListener<>(ret));
						}
					}
				});
				return ret;
			}
		}
		else
		{
			return new Future<IComponentFactory>(new ServiceNotFoundException("filter="+filter));
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
	
	/** Filter for scanning for kernel agent class files. */
	protected static FileFilter ffilter = new FileFilter("$", false, ".class")
		.addFilenameFilter(new IFilter<String>()
	{
		public boolean filter(String fn)
		{
			return fn.startsWith("Kernel");
		}
	});
	
	/** Filter for scanning for kernel agent class infos. */
	protected static IFilter<ClassInfo>	cfilter	= new IFilter<ClassInfo>()
	{
		public boolean filter(ClassInfo ci) 
		{
			return ci.hasAnnotation("jadex.micro.annotation.Agent");
		}
	};
	
	/**
	 *  Scan files for kernel components.
	 *  @return (suffix -> classname)
	 */
	public static Collection<IFilter<Object>> scanForKernelFilters(List<URL> urls)
	{
		List<IFilter<Object>> ret = new ArrayList<IFilter<Object>>();
		urls = new ArrayList<URL>(urls);
		
//		System.out.println("urls2: "+urls2.size());
//		for(URL u: urls2)
//			System.out.println(u);
		for(Iterator<URL> it=urls.iterator(); it.hasNext(); )
		{
			String u = it.next().toString();
			if(u.indexOf("jre/lib/ext")!=-1
			//	|| u.indexOf("jadex")==-1
				|| u.indexOf("SYSTEMCPRID")!=-1)
			{
				it.remove();
			}
		}
		
		//System.out.println("scan: "+urls2.size());
		
//		System.out.println("urls: "+urls);
		Set<ClassInfo> cis = SReflect.scanForClassInfos(urls.toArray(new URL[urls.size()]), ffilter, cfilter);

		for(ClassInfo ci: cis)
		{
			IFilter<Object> f = getKernelFilter(ci);
			if(f!=null)
			{
				//System.out.println("adding filter for: "+ci);
				ret.add(f);
			}
			/*else
			{
				System.out.println("omitting: "+ci);
			}*/
		}
		
		return ret;
	}
	
	/**
	 *  Add infos about a kernel to the map.
	 */
	public static IFilter<Object> getKernelFilter(ClassInfo ci)
	{
		IFilter<Object> ret = null;
		
		AnnotationInfo ai = ci.getAnnotation("jadex.micro.annotation.Properties");
		if(ai!=null)
		{
			Object[] vals = (Object[])ai.getValue("value");
			if(vals!=null)
			{
				for(Object val: vals)
				{
					AnnotationInfo a = (AnnotationInfo)val;
					String name = (String)a.getValue("name");
					
					if("kernel.filter".equals(name))
					{
						String value = (String)a.getValue("value");
						ret = (IFilter)SJavaParser.evaluateExpression(value, null);
						break; // we prefer filter against types
					}
					else if("kernel.types".equals(name))
					{
						String value = (String)a.getValue("value");
						Object v = SJavaParser.evaluateExpression(value, null);
						if(v instanceof String)
						{
							String type = (String)v;
							
							ret = new IFilter<Object>() 
							{
								public boolean filter(Object obj) 
								{
									String file = null;
									boolean ret = false;
									if(obj instanceof String)
										file = (String)obj;
									else if(obj instanceof JarEntry)
										file = ((JarEntry)obj).getName();
									else if(obj instanceof File)
										file = ((File)obj).getAbsolutePath();
										
									if(file!=null)
										ret = file.endsWith(type);
									//if(ret)
									//	System.out.println("found: "+file);
									return ret;
								}
							};
						}
						else if(v instanceof String[])
						{
							String[] types = (String[])v;
							
							ret = new IFilter<Object>() 
							{
								public boolean filter(Object obj) 
								{
									String file = null;
									boolean ret = false;
									if(obj instanceof String)
										file = (String)obj;
									else if(obj instanceof JarEntry)
										file = ((JarEntry)obj).getName();
									else if(obj instanceof File)
										file = ((File)obj).getAbsolutePath();
										
									if(file!=null)
									{
										for(String type: types)
										{
											ret = file.endsWith(type);
											if(ret)
												break;
										}
									}
									//if(ret)
									//	System.out.println("found: "+file);
									return ret;
								}
							};
						}
//						System.out.println("foound: "+ci.getClassname()+" "+Arrays.toString(types));
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Scans for component models and returns them as stream.
	 *  @return Collection<String[](filename, classname)>>
	 */
	public static ISubscriptionIntermediateFuture<Collection<String[]>> getComponentModelsAsStream(IInternalAccess component)
	{
		// return cached models if already scanned and no changes
		Object models = (Collection<Collection<String[]>>)Starter.getPlatformValue(component.getId().getRoot(), Starter.DATA_COMPONENTMODELS);
		if(models instanceof Collection)
		{
			Collection<Collection<String[]>> models2 = (Collection<Collection<String[]>>)models;
			SubscriptionIntermediateFuture<Collection<String[]>> ret = new SubscriptionIntermediateFuture<Collection<String[]>>();
			for(Iterator<Collection<String[]>> it=models2.iterator(); it.hasNext(); )
			{
				ret.addIntermediateResult(it.next());
			}
			// Set finished not really necessary due to max
			ret.setFinishedIfUndone();
			return ret;
		}
		else if(models instanceof IFuture)
		{
			return (ISubscriptionIntermediateFuture<Collection<String[]>>)models;
		}
		else //if(models==null)
		{
			Collection<Collection<String[]>> allres = new ArrayList<Collection<String[]>>();
			SubscriptionIntermediateDelegationFuture<Collection<String[]>> ret = new SubscriptionIntermediateDelegationFuture<Collection<String[]>>();
			Starter.putPlatformValue(component.getId().getRoot(), Starter.DATA_COMPONENTMODELS, ret);
			component.searchService(new ServiceQuery<ILibraryService>(ILibraryService.class)).then(ls ->
			{
				ls.getAllURLs().then(urls ->
				{
					// remark: this system out does not work for some reason (no printout on console)
					//System.out.println("urls are: "+urls);
					//for(URL u : urls)
					//	System.out.println("url: "+u);
					
					getComponentModelsAsStream(component, urls.toArray(new URL[urls.size()])).next(res ->
					{
						ret.addIntermediateResult(res);
						allres.add(res);
						//for(String[] stra: res)
						//	System.out.println("ires: "+Arrays.toString(stra));
					})
					.max(max -> {/*System.out.println("max: "+max);*/ ret.setMaxResultCount(max);})
					.finished(v ->
					{
						Starter.putPlatformValue(component.getId().getRoot(), Starter.DATA_COMPONENTMODELS, allres);
						ret.setFinishedIfUndone();
						//System.out.println("fini");
					})
					.catchEx(ret);
				})
				.catchEx(ret);
			})
			.catchEx(ret);
			return ret;
		}
	}
	
	/**
	 *  Scans for component models and returns them as stream.
	 *  @return Collection<String[](filename, classname)>>
	 */
	public static ISubscriptionIntermediateFuture<Collection<String[]>> getComponentModelsAsStream(IInternalAccess component, URL[] purls)
	{
		SubscriptionIntermediateFuture<Collection<String[]>> ret = new SubscriptionIntermediateFuture<>();
		
		final URL[] urls = SUtil.removeSystemUrls(purls);	
		final List<URL> urllist = SUtil.arrayToList(urls);
		//System.out.println("getComponentModelsAsStream: "+l.size());
		final int cnt[] = new int[1];
		
		final List<URL> modelurllist = new ArrayList<URL>();
		for(URL u: urls)
		{
			//if(u.toString().indexOf("micro")!=-1 && u.toString().indexOf("application")!=-1)
			//	System.out.println("here");
			//System.out.println(u+" "+existsFile("jadexscan.txt", u));
			if(existsFile("jadexscan", u))
				modelurllist.add(u);
		}

		final Iterator<URL> it = (Iterator<URL>)modelurllist.iterator();
		
		//System.out.println("max to: "+modelurllist.size()+" "+modelurllist);
		ret.setMaxResultCount(modelurllist.size());
		
		IComponentStep<List<String[]>> step = new IComponentStep<List<String[]>>()
		{
			public IFuture<List<String[]>> execute(IInternalAccess ia)
			{
				Future<List<String[]>> ret = new Future<>();
				List<String[]> res = new ArrayList<String[]>();
				
				URL u = it.next();
				URL[] url = new URL[]{u};
				
				IFilter<Object>[] filters = getKernelFilters(component, urllist).toArray(new IFilter[0]);
				
				//for(Object f: filters)
				//	System.out.println("kernelfilter: "+f.getClass());
				
				IFilter fil = new ComposedFilter(filters, ComposedFilter.OR);

				try
				{
					Set<SClassReader.ClassFileInfo> cis = SReflect.scanForClassFileInfos(url, null, fil);
					// todo: use getPackage()
					res = cis.stream().map(a -> new String[]{a.getFilename(), 
						a.getClassInfo().getClassName().substring(0, a.getClassInfo().getClassName().lastIndexOf("."))}).collect(Collectors.toList());
				}
				catch(Exception e)
				{
					System.out.println("scan class file infos: "+e);
				}

				//if(res.size()>0)
				//	System.out.println("found: "+res.size());
				
				//IIntermediateFuture<String> fut = scanForFilesAsync(url[0], mff);
				//fut.next(er -> res.add(new String[]{er, er}))
				//	.finished(v -> ret.setResult(res))
				//	.catchEx(ex -> ret.setException(ex));
				
				//if(url[0].toString().indexOf("applications")!=-1 && url[0].toString().indexOf("bpmn")!=-1)
				//	System.out.println("herrrrree");
				
				String[] res2 = SUtil.EMPTY_STRING_ARRAY;
				try
				{
					res2 = SReflect.scanForFiles(url, fil);
				}
				catch(Exception e)
				{
					System.out.println("scan files: "+e);
				}
				
				for(String r: res2)
				{
					String pname = SUtil.convertPathToPackage(r, urls);
					//String mname = r.substring(r.lastIndexOf(File.separator)+1);
					res.add(new String[]{r, pname});//+"."+mname});
				}
				
				//if(url[0].toString().indexOf("applications")!=-1 && url[0].toString().indexOf("bpmn")!=-1)
				//if(res.size()>0)
				//{
					//System.out.println("found for: "+url[0]+" "+res.size());
					//res.stream().forEach(a -> System.out.println(Arrays.toString(a)));
				//}
				
				ret.setResult(res);
				
				return ret;
			}
		};
		
		component.scheduleStep(step).addResultListener(new IResultListener<List<String[]>>()
		{
			public void resultAvailable(List<String[]> res)
			{
				//System.out.println("resa: "+it.hasNext()+" "+urllist+" "+urllist.size());
				ret.addIntermediateResult(res);
				if(it.hasNext())
				{
					cnt[0]++;
					//System.out.println("cnt: "+cnt[0]+"/"+urllist.size()+" "+res.size());
					component.scheduleStep(step).addResultListener(this);
				}
				else
				{
					//System.out.println("getComponentModelsAsStream finished");
					ret.setFinished();
				}
			}
		
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get all kernel files, i.e. specs to start a kernel.
	 */
	public static Collection<IFilter<Object>> getKernelFilters(IInternalAccess component, List<URL> urls)
	{
		Collection<IFilter<Object>> kernelfilters = (Collection<IFilter<Object>>)Starter.getPlatformValue(component.getId().getRoot(), Starter.DATA_KERNELFILTERS);
		if(kernelfilters==SReflect.NULL)
		{
			kernelfilters = SComponentFactory.scanForKernelFilters(urls);
			Starter.putPlatformValue(component.getId(), Starter.DATA_KERNELFILTERS, kernelfilters);
		}
		else if(kernelfilters==null)
		{
			kernelfilters = SComponentFactory.scanForKernelFilters(urls);
			Starter.putPlatformValue(component.getId(), Starter.DATA_KERNELFILTERS, kernelfilters);
			
			// install library listener for enabling rescan on any resource change
			component.searchService(new ServiceQuery<ILibraryService>(ILibraryService.class)).then(ls -> 
			{
				ls.addLibraryServiceListener(new ILibraryServiceListener() 
				{
					public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, IResourceIdentifier rid) 
					{
						Starter.putPlatformValue(component.getId(), Starter.DATA_KERNELFILTERS, SReflect.NULL);
						Starter.putPlatformValue(component.getId(), Starter.DATA_COMPONENTMODELS, null);
						return IFuture.DONE;
					}					
					
					public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean removable) 
					{
						Starter.putPlatformValue(component.getId(), Starter.DATA_KERNELFILTERS, SReflect.NULL);
						Starter.putPlatformValue(component.getId(), Starter.DATA_COMPONENTMODELS, null);
						return IFuture.DONE;
					}
				});
			}).catchEx(ex -> ex.printStackTrace());
		}
		return kernelfilters;
	}
	
	/**
	 *  Check if a file exists in a dir or jar.
	 *  @param name The name.
	 *  @param url The url.
	 *  @return True, if file exists.
	 */
	public static boolean existsFile(String name, URL url)
	{
		boolean ret = false;
		try
		{
//			System.out.println("url: "+urls[i].toURI());
			File f = new File(url.toURI());
			if(f.getName().endsWith(".jar"))
			{
				JarFile	jar = null;
				try
				{
					jar	= new JarFile(f);
					JarEntry entry = jar.getJarEntry(name);
					if(entry!=null)
						ret = true;
					jar.close();
				}
				catch(Exception e)
				{
//					System.out.println("Error opening jar: "+urls[i]+" "+e.getMessage());
				}
				finally
				{
					if(jar!=null)
					{
						jar.close();
					}
				}
			}
			else if(f.isDirectory())
			{
				File entry = new File(f, name);
				if(entry.exists())
					ret = true;
//				throw new UnsupportedOperationException("Currently only jar files supported: "+f);
			}
		}
		catch(Exception e)
		{
			System.out.println("problem with: "+url);
//			e.printStackTrace();
		}
		
		return ret;
	}
}
