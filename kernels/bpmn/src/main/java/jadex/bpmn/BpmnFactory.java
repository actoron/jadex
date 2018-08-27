package jadex.bpmn;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import jadex.bpmn.features.impl.BpmnComponentFeature;
import jadex.bpmn.features.impl.BpmnExecutionFeature;
import jadex.bpmn.features.impl.BpmnMessageComponentFeature;
import jadex.bpmn.features.impl.BpmnMonitoringComponentFeature;
import jadex.bpmn.features.impl.BpmnProvidedServicesFeature;
import jadex.bpmn.model.MBpmnModel;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IBootstrapFactory;
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


/**
 *  Factory for loading bpmn processes.
 */
public class BpmnFactory extends BasicService implements IComponentFactory, IBootstrapFactory
{
	//-------- constants --------
	
	/** The supported component types (file extensions).
	 *  Convention used by platform config panel. */
	public static final String[]	FILETYPES	= new String[]{".bpmn", ".bpmn2"};
	
	/** The bpmn process file type. */
	public static final String	FILETYPE_BPMNPROCESS = "BPMN Process";
	
	/** The bpmn legacy process file type. */
	public static final String	FILETYPE_BPMNLEGACYPROCESS = "BPMN Legacy Process";
	
	/** The image icon. */
	protected static final LazyResource ICON = new LazyResource(BpmnFactory.class, "/jadex/bpmn/images/bpmn_process.png");
	
	public static final Collection<IComponentFeatureFactory> BPMN_FEATURES = Collections.unmodifiableCollection(
		Arrays.asList(
			new ComponentFeatureFactory(IProvidedServicesFeature.class, BpmnProvidedServicesFeature.class),
			BpmnComponentFeature.FACTORY,
			new ComponentFeatureFactory(IExecutionFeature.class, BpmnExecutionFeature.class),
			BpmnMessageComponentFeature.FACTORY,
			new ComponentFeatureFactory(IMonitoringComponentFeature.class, BpmnMonitoringComponentFeature.class)
		));
	
	//-------- attributes --------
	
	/** The provider. */
	protected IInternalAccess provider;
	
	/** The model loader */
	protected BpmnModelLoader loader;
	
	/** The library service. */
	protected ILibraryService libservice;
	
	/** The library service listener */
	protected ILibraryServiceListener libservicelistener;
	
	/** The properties. */
	protected Map<String, Object> fproperties;
	
	/** The standard + micro component features. */
	protected Collection<IComponentFeatureFactory>	features;
	
	//-------- constructors --------
	
	/**
	 *  Create a new factory for startup.
	 *  @param platform	The platform.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public BpmnFactory(String providerid)
	{
		super(new BasicComponentIdentifier(providerid), IComponentFactory.class, null);
		this.loader = new BpmnModelLoader();
		this.features = SComponentFactory.orderComponentFeatures(SReflect.getUnqualifiedClassName(getClass()), Arrays.asList(SComponentFactory.DEFAULT_FEATURES, BPMN_FEATURES));
	}
	
	/**
	 *  Create a new BpmnProcessService.
	 */
	public BpmnFactory(IInternalAccess provider, Map<String, Object> properties)
	{
		super(provider.getId(), IComponentFactory.class, null);

		this.provider = provider;
		this.loader = new BpmnModelLoader();
		this.fproperties	= properties;
		this.features = SComponentFactory.orderComponentFeatures(SReflect.getUnqualifiedClassName(getClass()), Arrays.asList(SComponentFactory.DEFAULT_FEATURES, BPMN_FEATURES));

		this.libservicelistener = new ILibraryServiceListener()
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
		libservice = provider.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ILibraryService.class));
		libservice.addLibraryServiceListener(libservicelistener);	// TODO: wait for future?
		return BpmnFactory.super.startService();
	}
	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return super.startService();
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 */
	public synchronized IFuture<Void>	shutdownService()
	{
		final Future<Void>	ret	= new Future<Void>();
		libservice.removeLibraryServiceListener(libservicelistener)
			.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				BpmnFactory.super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture<IModelInfo> loadModel(final String model, final String[] imports, final IResourceIdentifier rid)
	{
		final Future<IModelInfo> ret = new Future<IModelInfo>();
//		System.out.println("filename: "+filename);
		
		if(libservice!=null)
		{
			libservice.getClassLoader(rid).addResultListener(
				new ExceptionDelegationResultListener<ClassLoader, IModelInfo>(ret)
			{
				public void customResultAvailable(ClassLoader cl)
				{
					try
					{
						MBpmnModel amodel = loader.loadBpmnModel(model, imports, cl, new Object[]{rid, getProviderId().getRoot()});
						ret.setResult(amodel.getModelInfo());
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
				MBpmnModel amodel = loader.loadBpmnModel(model, imports, cl, new Object[]{rid, getProviderId().getRoot()});
				ret.setResult(amodel.getModelInfo());
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
		return new Future<Boolean>(model.endsWith(".bpmn") || model.endsWith(".bpmn2")? Boolean.TRUE: Boolean.FALSE);
	}

	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture<Boolean> isStartable(String model, String[] imports, IResourceIdentifier rid)
	{
		return new Future<Boolean>(model.endsWith(".bpmn") || model.endsWith(".bpmn2")? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_BPMNPROCESS, FILETYPE_BPMNLEGACYPROCESS};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture<byte[]> getComponentTypeIcon(String type)
	{
		Future<byte[]>	ret	= new Future<byte[]>();
		if(type.equals(FILETYPE_BPMNPROCESS) || type.equals(FILETYPE_BPMNLEGACYPROCESS))
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
		return new Future<String>(model.endsWith(".bpmn") ? FILETYPE_BPMNLEGACYPROCESS : model.endsWith(".bpmn2") ? FILETYPE_BPMNPROCESS : null);
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
		return FILETYPE_BPMNPROCESS.equals(type) || FILETYPE_BPMNLEGACYPROCESS.equals(type)
		? fproperties : null;
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
