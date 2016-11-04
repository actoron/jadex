package jadex.bridge.component.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  This feature provides subcomponents.
 */
public class SubcomponentsComponentFeature	extends	AbstractComponentFeature	implements ISubcomponentsFeature, IInternalSubcomponentsFeature
{
	/**
	 *  Create the feature.
	 */
	public SubcomponentsComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void> init()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(component.getConfiguration()!=null)
		{
			ConfigurationInfo conf = component.getModel().getConfiguration(component.getConfiguration());
			final ComponentInstanceInfo[] components = conf.getComponentInstances();
			createComponents(components).addResultListener(createResultListener(
				new ExceptionDelegationResultListener<List<IComponentIdentifier>, Void>(ret)
			{
				public void customResultAvailable(List<IComponentIdentifier> cids)
				{
					ret.setResult(null);
				}
			}));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}	
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
	/**
	 *  Get the file name of a component type.
	 *  @param ctype The component type.
	 *  @return The file name of this component type.
	 */
	public String getComponentFilename(final String ctype)
	{
		String ret = null;
		
		SubcomponentTypeInfo[] subcomps = getComponent().getModel().getSubcomponentTypes();
		for(int i=0; ret==null && i<subcomps.length; i++)
		{
			SubcomponentTypeInfo subct = (SubcomponentTypeInfo)subcomps[i];
			if(subct.getName().equals(ctype))
				ret = subct.getFilename();
		}
		
		return ret;
	}
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		return getComponent().getComponentDescription().getLocalType();
	}
	
	/**
	 *  Create the subcomponents.
	 */
	protected IFuture<List<IComponentIdentifier>> createComponents(final ComponentInstanceInfo[] components)
	{
//		System.out.println("create subcompos: ");
//		for(ComponentInstanceInfo cii: components)
//		{
//			System.out.println(cii.getName()+" "+cii.getTypeName());
//		}
		
		final Future<Void> res = new Future<Void>();
		final List<IComponentIdentifier> cids = new ArrayList<IComponentIdentifier>();
		SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(res)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				// NOTE: in current implementation application waits for subcomponents
				// to be finished and cms implements a hack to get the external
				// access of an uninited parent.
				
				// (NOTE1: parent cannot wait for subcomponents to be all created
				// before setting itself inited=true, because subcomponents need
				// the parent external access.)
				
				// (NOTE2: subcomponents must be created one by one as they
				// might depend on each other (e.g. bdi factory must be there for jcc)).
				
				createComponent(components, cms, component.getModel(), 0, res, cids);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				super.exceptionOccurred(exception);
			}
		}));
		
		final Future<List<IComponentIdentifier>> ret = new Future<List<IComponentIdentifier>>();
		res.addResultListener(new ExceptionDelegationResultListener<Void, List<IComponentIdentifier>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setResult(cids);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		createComponents(new ComponentInstanceInfo[]{component}).addResultListener(createResultListener(
			new ExceptionDelegationResultListener<List<IComponentIdentifier>, IComponentIdentifier>(ret)
			{
				public void customResultAvailable(List<IComponentIdentifier> cids)
				{
					ret.setResult(cids.get(0));
				}
			}));
		return ret;
	}
	
	/**
	 *  Create subcomponents.
	 */
	protected void	createComponent(final ComponentInstanceInfo[] components, final IComponentManagementService cms, final IModelInfo model, final int i, final Future<Void> fut, final List<IComponentIdentifier> cids)
	{
		if(i<components.length)
		{
			int num = getNumber(components[i], model);
			IResultListener<IComponentIdentifier> crl = new CollectionResultListener<IComponentIdentifier>(num, false, 
				component.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IComponentIdentifier>, Void>(fut)
			{
				public void customResultAvailable(Collection<IComponentIdentifier> result)
				{
					cids.addAll(result);
					createComponent(components, cms, model, i+1, fut, cids);
				}
			}));
			for(int j=0; j<num; j++)
			{
				SubcomponentTypeInfo type = components[i].getType(model);
				if(type!=null)
				{
					final Boolean suspend	= components[i].getSuspend()!=null ? components[i].getSuspend() : type.getSuspend();
					Boolean	master = components[i].getMaster()!=null ? components[i].getMaster() : type.getMaster();
					Boolean	daemon = components[i].getDaemon()!=null ? components[i].getDaemon() : type.getDaemon();
					Boolean	autoshutdown = components[i].getAutoShutdown()!=null ? components[i].getAutoShutdown() : type.getAutoShutdown();
					Boolean	synchronous = components[i].getSynchronous()!=null ? components[i].getSynchronous() : type.getSynchronous();
					Boolean	persistable = components[i].getPersistable()!=null ? components[i].getPersistable() : type.getPersistable();
					PublishEventLevel monitoring = components[i].getMonitoring()!=null ? components[i].getMonitoring() : type.getMonitoring();
					RequiredServiceBinding[] bindings = components[i].getBindings();
					// todo: rid
//					System.out.println("curcall: "+getName(components[i], model, j+1)+" "+CallAccess.getCurrentInvocation().getCause());
//					cms.createComponent(getName(components[i], model, j+1), type.getName(),
					cms.createComponent(getName(components[i], model, j+1), getFilename(components[i], model),
						new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), component.getComponentIdentifier(),
						suspend, master, daemon, autoshutdown, synchronous, persistable, monitoring, model.getAllImports(), bindings, null), null).addResultListener(crl);
				}
				else
				{
					crl.exceptionOccurred(new RuntimeException("No such component type: "+components[i].getTypeName()));
				}
			}
		}
		else
		{
			fut.setResult(null);
		}
	}
	
	/**
	 *  Get the number of components to start.
	 *  Allows filename to be dynamically evaluated.
	 *  @return The number.
	 */
	protected String getFilename(ComponentInstanceInfo component, IModelInfo model)
	{
		String ret = null;
		SubcomponentTypeInfo si = component.getType(model);
		
		ret = (String)SJavaParser.evaluateExpressionPotentially(si.getFilename(), model.getAllImports(), this.component.getFetcher(), this.component.getClassLoader());
		
//		if(si.getFilename()!=null && si.getFilename().startsWith("%{"))
//		{
//			try
//			{
//				ret = (String)SJavaParser.evaluateExpression(si.getFilename().substring(2, si.getFilename().length()-1), model.getAllImports(), this.component.getFetcher(), this.component.getClassLoader());
//			}
//			catch(Exception e)
//			{
//				ret = si.getFilename();
//			}
//		}
//		else
//		{
//			ret	= si.getFilename();
//		}
		return ret;
	}
	
	/**
	 *  Get the number of components to start.
	 *  @return The number.
	 */
	protected int getNumber(ComponentInstanceInfo component, IModelInfo model)
	{
		Object ret = component.getNumber()!=null? SJavaParser.evaluateExpression(component.getNumber(), model.getAllImports(), this.component.getFetcher(), this.component.getClassLoader()): null;
		return ret instanceof Integer? ((Integer)ret).intValue(): 1;
	}
	
	/**
	 *  Get the name of components to start.
	 *  @return The name.
	 */
	protected String getName(ComponentInstanceInfo component, IModelInfo model, int cnt)
	{
		String ret = component.getName();
		if(ret!=null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(this.component.getFetcher());
			fetcher.setValue("$n", Integer.valueOf(cnt));
			try
			{
				ret = (String)SJavaParser.evaluateExpression(component.getName(), model.getAllImports(), fetcher, this.component.getClassLoader());
				if(ret==null)
					ret = component.getName();
			}
			catch(RuntimeException e)
			{
			}
		}
		return ret;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments as a map of name-value pairs.
	 */
	protected Map<String, Object> getArguments(ComponentInstanceInfo component, IModelInfo model)
	{
		Map<String, Object> ret = null;		
		UnparsedExpression[] arguments = component.getArguments();
		UnparsedExpression argumentsexp = component.getArgumentsExpression();
		
		if(arguments.length>0)
		{
			ret = new HashMap<String, Object>();

			for(int i=0; i<arguments.length; i++)
			{
				// todo: language
				if(arguments[i].getValue()!=null && arguments[i].getValue().length()>0)
				{
					Object val = SJavaParser.evaluateExpression(arguments[i].getValue(), model.getAllImports(), this.component.getFetcher(), this.component.getClassLoader());
					ret.put(arguments[i].getName(), val);
				}
			}
		}
		else if(argumentsexp!=null && argumentsexp.getValue()!=null && argumentsexp.getValue().length()>0)
		{
			// todo: language
			ret = (Map<String, Object>)SJavaParser.evaluateExpression(argumentsexp.getValue(), model.getAllImports(), this.component.getFetcher(), this.component.getClassLoader());
		}
		
		return ret;
	}
	
	//-------- IInternalSubcomponentsFeature interface -------
	
	/**
	 *  Called, when a subcomponent has been created.
	 */
	public IFuture<Void>	componentCreated(final IComponentDescription desc)
	{
		// Throw component events for extensions (envsupport)
		final IMonitoringComponentFeature	mon	= getComponent().getComponentFeature0(IMonitoringComponentFeature.class);
		if(mon!=null)
		{
			return getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					Future<Void>	ret	= new Future<Void>();
					if(mon.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
					{
						MonitoringEvent me = new MonitoringEvent(desc.getName(), desc.getCreationTime(), 
							MonitoringEvent.TYPE_COMPONENT_CREATED, desc.getCause(), desc.getCreationTime(), PublishEventLevel.COARSE);
						me.setProperty("details", desc);
						// for extensions only
						mon.publishEvent(me, PublishTarget.TOALL) .addResultListener(new DelegationResultListener<Void>(ret));
					}
					else
					{
						ret.setResult(null);
					}
					return ret;
				}
			});
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
	/**
	 *  Called, when a subcomponent has been removed.
	 */
	public IFuture<Void> componentRemoved(final IComponentDescription desc)
	{
		// Throw component events for extensions (envsupport)
		final IMonitoringComponentFeature	mon	= getComponent().getComponentFeature0(IMonitoringComponentFeature.class);
		if(mon!=null)
		{
			return getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					Future<Void>	ret	= new Future<Void>();
					if(mon.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
					{
						long time = SServiceProvider.getLocalService(getComponent(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).getTime();
						MonitoringEvent me = new MonitoringEvent(desc.getName(), desc.getCreationTime(), 
							MonitoringEvent.TYPE_COMPONENT_DISPOSED, desc.getCause(), time, PublishEventLevel.COARSE);
						me.setProperty("details", desc);
						// for extensions only
						mon.publishEvent(me, PublishTarget.TOALL) .addResultListener(new DelegationResultListener<Void>(ret));
					}
					else
					{
						ret.setResult(null);
					}
					return ret;
				}
			});
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
	/**
	 * 
	 */
	protected boolean isExternalThread()
	{
		return !getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
}
