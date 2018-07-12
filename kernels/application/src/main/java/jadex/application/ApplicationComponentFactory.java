package jadex.application;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.library.ILibraryServiceListener;
import jadex.commons.LazyResource;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.IBootstrapFactory;



/**
 *  Factory for default contexts.
 *  No special properties supported, yet.
 */
public class ApplicationComponentFactory extends BasicService implements IComponentFactory, IBootstrapFactory
{
	//-------- constants --------
	
	/** The supported component types (file extensions).
	 *  Convention used by platform config panel. */
	public static final String[]	FILETYPES	= new String[]{ApplicationModelLoader.FILE_EXTENSION_APPLICATION};
	
	/** The application component file type. */
	public static final String	FILETYPE_APPLICATION = "Application";
	
	/** The image icon. */
	protected static final LazyResource	ICON = new LazyResource(ApplicationComponentFactory.class, "/jadex/application/images/application.png");
	
	//-------- attributes --------
	
	/** The application model loader. */
	protected ApplicationModelLoader loader;
	
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
	 *  Create a new application factory for startup.
	 *  @param platform	The platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public ApplicationComponentFactory(String providerid)
	{
		super(new BasicComponentIdentifier(providerid), IComponentFactory.class, null);
		
		// Todo: hack!!! make extensions configurable also for reflective constructor (how?)
		String[]	extensions	= new String[]
		{
			"jadex.extension.envsupport.MEnvSpaceType", "getXMLMapping",
			"jadex.extension.agr.AGRExtensionService", "getXMLMapping"
		};
		List<Set<?>>	mappings	= new ArrayList<Set<?>>();
		for(int i=0; i<extensions.length; i+=2)
		{
			try
			{
				Class<?>	clazz	= Class.forName(extensions[i], true, getClass().getClassLoader());
				Method	m	= clazz.getMethod(extensions[i+1], new Class[0]);
				mappings.add((Set<?>)m.invoke(null, new Object[0]));
			}
			catch(ClassNotFoundException e)
			{
				// Extension not present -> ignore.
			}
			catch(Exception e)
			{
				e.printStackTrace();				
			}
			
		}
		
		this.loader = new ApplicationModelLoader(mappings.toArray(new Set[0]));
		this.features	= SComponentFactory.DEFAULT_FEATURES;
	}
	
	/**
	 *  Create a new application factory.
	 *  @param platform	The platform.
	 *  @param mappings	The XML reader mappings of supported spaces (if any).
	 */
	public ApplicationComponentFactory(IInternalAccess provider)
	{
		super(provider.getId(), IComponentFactory.class, null);
		this.provider = provider;
		this.features	= SComponentFactory.DEFAULT_FEATURES;
	}
	
	/**
	 *  Start the service.
	 */
	public IFuture<Void> startService(IInternalAccess component, IResourceIdentifier rid)
	{
		this.provider = component;
		this.providerid = provider.getId();
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
				libservice	= provider.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
						
				// Todo: hack!!! make extensions configurable also for reflective constructor (how?)
				String[]	extensions	= new String[]
				{
					"jadex.extension.envsupport.MEnvSpaceType", "getXMLMapping",
					"jadex.extension.agr.AGRExtensionService", "getXMLMapping"
				};
				List<Set<?>>	mappings	= new ArrayList<Set<?>>();
				for(int i=0; i<extensions.length; i+=2)
				{
					try
					{
						Class<?>	clazz	= Class.forName(extensions[i], true, getClass().getClassLoader());
						Method	m	= clazz.getMethod(extensions[i+1], new Class[0]);
						mappings.add((Set<?>)m.invoke(null, new Object[0]));
					}
					catch(ClassNotFoundException e)
					{
						// Extension not present -> ignore.
					}
					catch(Exception e)
					{
						e.printStackTrace();				
					}
					
				}
				
				loader = new ApplicationModelLoader(mappings.toArray(new Set[0]));
				
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
				
				libservice.addLibraryServiceListener(libservicelistener);
				
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
				ApplicationComponentFactory.super.shutdownService()
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
						ret.setResult(loader.loadApplicationModel(model, imports, rid, cl, 
							new Object[]{rid, getProviderId().getRoot()}).getModelInfo());
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
				ret.setResult(loader.loadApplicationModel(model, imports, rid, cl, 
						new Object[]{rid, getProviderId().getRoot()}).getModelInfo());
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
		return new Future<Boolean>(model.endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION));
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION));
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_APPLICATION};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_APPLICATION))
		{
			try
			{
				ret.setResult(ICON.getData());
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
		return new Future<String>(model.toLowerCase().endsWith(ApplicationModelLoader.FILE_EXTENSION_APPLICATION)? FILETYPE_APPLICATION: null);
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
		if(FILETYPE_APPLICATION.equals(type))
		{
			return Collections.emptyMap();
		}
		else
		{
			return null;
		}
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
