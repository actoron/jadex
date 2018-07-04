package jadex.bdiv3x;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import jadex.bdiv3.features.impl.BDIExecutionComponentFeature;
import jadex.bdiv3.features.impl.BDIMonitoringComponentFeature;
import jadex.bdiv3.features.impl.BDIProvidedServicesComponentFeature;
import jadex.bdiv3.features.impl.BDIRequiredServicesComponentFeature;
import jadex.bdiv3x.features.BDIXAgentFeature;
import jadex.bdiv3x.features.BDIXArgumentsResultsComponentFeature;
import jadex.bdiv3x.features.BDIXLifecycleAgentFeature;
import jadex.bdiv3x.features.BDIXMessageComponentFeature;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.LazyResource;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.IBootstrapFactory;



/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class BDIXComponentFactory extends BasicService implements IComponentFactory, IBootstrapFactory
{
	//-------- constants --------
	
	/** The supported component types (file extensions).
	 *  Convention used by platform config panel. */
	public static final String[]	FILETYPES	= new String[]{BDIXModelLoader.FILE_EXTENSION_AGENT, BDIXModelLoader.FILE_EXTENSION_CAPABILITY};
	
	/** The agent file type name. */
	public static final String	FILETYPE_AGENT = "BDIV3X Agent";
	
	/** The agent file type name. */
	public static final String	FILETYPE_CAPABILITY = "BDIV3X Capability";
	
	/** The agent icon. */
	protected static final LazyResource	ICON_AGENT = new LazyResource(BDIXComponentFactory.class, "/jadex/bdiv3x/images/bdi_agent.png");
	
	/** The capability icon. */
	protected static final LazyResource	ICON_CAPABILITY = new LazyResource(BDIXComponentFactory.class, "/jadex/bdiv3x/images/bdi_capability.png");

	/** The specific component features for micro agents. */
	public static final Collection<IComponentFeatureFactory> BDI_FEATURES = Collections.unmodifiableCollection(
		Arrays.asList(
			new ComponentFeatureFactory(IArgumentsResultsFeature.class, BDIXArgumentsResultsComponentFeature.class),
			new ComponentFeatureFactory(IProvidedServicesFeature.class, BDIProvidedServicesComponentFeature.class),
			BDIXAgentFeature.FACTORY, 
			BDIXLifecycleAgentFeature.FACTORY,
			BDIXMessageComponentFeature.FACTORY,
			new ComponentFeatureFactory(IExecutionFeature.class, BDIExecutionComponentFeature.class),
			new ComponentFeatureFactory(IMonitoringComponentFeature.class, BDIMonitoringComponentFeature.class),
			new ComponentFeatureFactory(IRequiredServicesFeature.class, BDIRequiredServicesComponentFeature.class)
		));
	
	//-------- attributes --------
	
	/** The application model loader. */
	protected BDIXModelLoader loader;
	
	/** The provider. */
	protected IInternalAccess provider;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	/** The standard + XML component features. */
	protected Collection<IComponentFeatureFactory>	features;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component factory for startup.
	 *  @param providerid	The platform name.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public BDIXComponentFactory(String providerid)
	{
		super(new BasicComponentIdentifier(providerid), IComponentFactory.class, null);
		this.loader = new BDIXModelLoader();
	}
	
	/**
	 *  Create a new component factory.
	 *  @param provider	The component.
	 */
	public BDIXComponentFactory(IInternalAccess provider, Map<String, Object> properties)
	{
		super(provider.getComponentIdentifier(), IComponentFactory.class, properties);
		this.provider = provider;
		this.features	= SComponentFactory.orderComponentFeatures(SReflect.getUnqualifiedClassName(getClass()), Arrays.asList(SComponentFactory.DEFAULT_FEATURES, BDI_FEATURES));
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService(IInternalAccess component, IResourceIdentifier rid)
	{
		this.provider = component;
		this.providerid = provider.getComponentIdentifier();
		setServiceIdentifier(createServiceIdentifier(provider, "BootstrapFactory", IComponentFactory.class, IComponentFactory.class, rid, null));
		return startService();
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService()
	{
		final Future<Void> ret = new Future<Void>();
		super.startService().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				libservice	= provider.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
				loader = new BDIXModelLoader();
				
				libservicelistener = new ILibraryServiceListener()
				{
					public IFuture<Void> resourceIdentifierRemoved(IResourceIdentifier parid, IResourceIdentifier rid)
					{
						loader.clearModelCache();
						return IFuture.DONE;
					}
					
					public IFuture<Void> resourceIdentifierAdded(IResourceIdentifier parid, IResourceIdentifier rid, boolean rem)
					{
						loader.clearModelCache();
						return IFuture.DONE;
					}
				};
				
				libservice.addLibraryServiceListener(libservicelistener);	// TODO: wait for future?
						
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public IFuture<Void> shutdownService()
	{
		final Future<Void>	ret	= new Future<Void>();
		libservice.removeLibraryServiceListener(libservicelistener)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				BDIXComponentFactory.super.shutdownService()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
			
		return ret;
	}
	
	//-------- IComponentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		final Future<IModelInfo> ret = new Future<IModelInfo>();
		
		if(libservice!=null)
		{
			libservice.getClassLoader(rid).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, IModelInfo>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						if(model!=null && model.endsWith(BDIXModelLoader.FILE_EXTENSION_CAPABILITY))
						{
							ret.setResult(loader.loadCapabilityModel(model, imports, rid, cl, 
								new Object[]{rid, getProviderId().getRoot()}).getModelInfo());							
						}
						else
						{
							ret.setResult(loader.loadAgentModel(model, imports, rid, cl, 
								new Object[]{rid, getProviderId().getRoot()}).getModelInfo());
						}
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			});
		}
		else
		{
			try
			{
				ClassLoader cl = getClass().getClassLoader();
				if(model!=null && model.endsWith(BDIXModelLoader.FILE_EXTENSION_CAPABILITY))
				{
					ret.setResult(loader.loadCapabilityModel(model, imports, rid, cl, 
						new Object[]{rid, getProviderId().getRoot()}).getModelInfo());							
				}
				else
				{
					ret.setResult(loader.loadAgentModel(model, imports, rid, cl, 
						new Object[]{rid, getProviderId().getRoot()}).getModelInfo());
				}
			}
			catch(Exception e)
			{
				ret.setException(e);
			}			
		}

		return ret;
	}
		
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture<Boolean> isLoadable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(BDIXModelLoader.FILE_EXTENSION_AGENT) || model.endsWith(BDIXModelLoader.FILE_EXTENSION_CAPABILITY));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(BDIXModelLoader.FILE_EXTENSION_AGENT));
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_AGENT, FILETYPE_CAPABILITY};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_AGENT))
		{
			try
			{
				ret.setResult(ICON_AGENT.getData());
			}
			catch(IOException e)
			{
				ret.setException(e);
			}
		}
		else if(type.equals(FILETYPE_CAPABILITY))
		{
			try
			{
				ret.setResult(ICON_CAPABILITY.getData());
			}
			catch(IOException e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}	

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture<String> getComponentType(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<String>(model.toLowerCase().endsWith(BDIXModelLoader.FILE_EXTENSION_AGENT)? FILETYPE_AGENT: model.toLowerCase().endsWith(BDIXModelLoader.FILE_EXTENSION_CAPABILITY)? FILETYPE_CAPABILITY : null);
	}

	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map<String, Object>	getProperties(String type)
	{
		return FILETYPE_AGENT.equals(type) || FILETYPE_CAPABILITY.equals(type)
			? super.getPropertyMap(): null;
	}
	
	/**
	 *  Get the component features for a model.
	 *  @param model The component model.
	 *  @return The component features.
	 */
	public IFuture<Collection<IComponentFeatureFactory>> getComponentFeatures(IModelInfo model)
	{
		return new Future<Collection<IComponentFeatureFactory>>(features);
	}
}
