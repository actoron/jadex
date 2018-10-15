package jadex.bridge.component.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.SFuture;
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
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

/**
 *  This feature provides subcomponents.
 */
public class SubcomponentsComponentFeature	extends	AbstractComponentFeature implements ISubcomponentsFeature, IInternalSubcomponentsFeature
{
//	/** The number of children. */
//	protected int childcount;
	
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
	 *  Get the model name of a component type.
	 *  @param ctype The component type.
	 *  @return The model name of this component type.
	 */
	public IFuture<String> getFileName(String ctype)
	{
		return new Future<String>(getComponentFilename(ctype));
	}
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		return getComponent().getDescription().getLocalType();
	}
	
	/**
	 *  Starts a new component.
	 *  
	 *  @param infos Start information.
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public IFuture<IExternalAccess> createComponent(CreationInfo info)
	{
		if (info.getParent() == null || component.getId().equals(info.getParent()))
			return getComponent().createComponent(info, null);
		else
			return component.getExternalAccessAsync(info.getParent()).get().createComponent(info);
	}
	
	/**
	 *  Starts a new component while continuously receiving status events (create, result updates, termination).
	 *  
	 *  @param infos Start information.
	 *  @return Status events.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithEvents(CreationInfo info)
	{
		final SubscriptionIntermediateFuture<CMSStatusEvent> ret = new SubscriptionIntermediateFuture<>();
		
		final boolean keepsusp = Boolean.TRUE.equals(info.getSuspend());
		info.setSuspend(true);
		createComponent(info).addResultListener(new IResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess result)
			{
				info.setSuspend(keepsusp);
				
				ISubscriptionIntermediateFuture<CMSStatusEvent> fut = SComponentManagementService.listenToComponent(result.getId(), component);
				FutureFunctionality.connectDelegationFuture(ret, fut);
				
				if (!keepsusp)
					result.resumeComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		SFuture.avoidCallTimeouts(ret, component);
		return ret;
	}
	
	/**
	 *  Starts a set of new components, in order of dependencies.
	 *  
	 *  @param infos Start information.
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public IIntermediateFuture<IExternalAccess> createComponents(CreationInfo... infos);
	
	/**
	 *  Stops a set of components, in order of dependencies.
	 *  
	 *  @param infos Start information.
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public IIntermediateFuture<Map<String, Object>> killComponents(CreationInfo... infos);
	
//	/**
//	 *  Starts a new child as subcomponent.
//	 *  
//	 *  @param infos Start information.
//	 *  @return The id of the component and the results after the component has been killed.
//	 */
//	public IFuture<IExternalAccess> createChild(CreationInfo info)
//	{
//		if (info.getParent() != null && !component.getId().equals(info.getParent()))
//			return new Future<>(new IllegalArgumentException("Subcomponent cannot be created if parent is specified: " + info + ", specified parent " + info.getParent()));
//		
//		info.setParent(component.getId());
//		return component.createComponent(info, null);
//	}
	
//	/**
//	 *  Create the subcomponents.
//	 */
//	protected IFuture<List<IComponentIdentifier>> createComponents(final ComponentInstanceInfo[] components)
//	{
////		System.out.println("create subcompos: ");
////		for(ComponentInstanceInfo cii: components)
////		{
////			System.out.println(cii.getName()+" "+cii.getTypeName());
////		}
//		
//		final Future<Void> res = new Future<Void>();
//		final List<IComponentIdentifier> cids = new ArrayList<IComponentIdentifier>();
////		IComponentManagementService cms = getComponent().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
//		// NOTE: in current implementation application waits for subcomponents
//		// to be finished and cms implements a hack to get the external
//		// access of an uninited parent.
//		
//		// (NOTE1: parent cannot wait for subcomponents to be all created
//		// before setting itself inited=true, because subcomponents need
//		// the parent external access.)
//		
//		// (NOTE2: subcomponents must be created one by one as they
//		// might depend on each other (e.g. bdi factory must be there for jcc)).
//		
//		createComponent(components, component.getModel(), 0, res, cids);
//		
//		final Future<List<IComponentIdentifier>> ret = new Future<List<IComponentIdentifier>>();
//		res.addResultListener(new ExceptionDelegationResultListener<Void, List<IComponentIdentifier>>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				ret.setResult(cids);
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 * Search for components matching the given description.
	 * @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con)
	{
		return getComponent().searchComponents(adesc, con);
	}
	
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
//	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		createComponents(new ComponentInstanceInfo[]{component}).addResultListener(createResultListener(
//			new ExceptionDelegationResultListener<List<IComponentIdentifier>, IComponentIdentifier>(ret)
//			{
//				public void customResultAvailable(List<IComponentIdentifier> cids)
//				{
//					ret.setResult(cids.get(0));
//				}
//			}));
//		return ret;
//	}
	
	/**
	 *  Create subcomponents.
	 */
	protected void	createComponent(final ComponentInstanceInfo[] components, final IModelInfo model, final int i, final Future<Void> fut, final List<IComponentIdentifier> cids)
	{
		if(i<components.length)
		{			
			final int num = getNumber(components[i], model);
			
//			if(num>0)
//				System.out.println("create comp: "+components[i].getName());
			
			IResultListener<IComponentIdentifier> crl = new CollectionResultListener<IComponentIdentifier>(num, false, 
				component.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IComponentIdentifier>, Void>(fut)
			{
				public void customResultAvailable(Collection<IComponentIdentifier> result)
				{
//					if(num>0)
//						System.out.println("created comp: "+components[i].getName());
					cids.addAll(result);
					createComponent(components, model, i+1, fut, cids);
				}
			}));
			for(int j=0; j<num; j++)
			{
				SubcomponentTypeInfo type = components[i].getType(model);
				if(type!=null)
				{
//					if(type.getFilename().indexOf("Registry")!=-1)
//						System.out.println("reg");
					final Boolean suspend	= components[i].getSuspend()!=null ? components[i].getSuspend() : type.getSuspend();
//					Boolean	master = components[i].getMaster()!=null ? components[i].getMaster() : type.getMaster();
//					Boolean	daemon = components[i].getDaemon()!=null ? components[i].getDaemon() : type.getDaemon();
//					Boolean	autoshutdown = components[i].getAutoShutdown()!=null ? components[i].getAutoShutdown() : type.getAutoShutdown();
					Boolean	synchronous = components[i].getSynchronous()!=null ? components[i].getSynchronous() : type.getSynchronous();
//					Boolean	persistable = components[i].getPersistable()!=null ? components[i].getPersistable() : type.getPersistable();
					PublishEventLevel monitoring = components[i].getMonitoring()!=null ? components[i].getMonitoring() : type.getMonitoring();
					RequiredServiceBinding[] bindings = components[i].getBindings();
					// todo: rid
//					System.out.println("curcall: "+getName(components[i], model, j+1)+" "+CallAccess.getCurrentInvocation().getCause());
//					cms.createComponent(getName(components[i], model, j+1), type.getName(),
					CreationInfo ci = new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), component.getId(),
						suspend,  synchronous, monitoring, model.getAllImports(), bindings, null);
					ci.setName(getName(components[i], model, j+1));
					ci.setFilename(getFilename(components[i], model));
					
					getComponent().createComponent(ci, null).addResultListener(new IResultListener<IExternalAccess>()
					{
						public void resultAvailable(IExternalAccess result) 
						{
							crl.resultAvailable(result.getId());
						}
						
						public void exceptionOccurred(Exception exception)
						{
							crl.exceptionOccurred(exception);
						}
					});
//					cms.createComponent(getName(components[i], model, j+1), getFilename(components[i], model),
//						new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), component.getId(),
//						suspend, master, daemon, autoshutdown, synchronous, persistable, monitoring, model.getAllImports(), bindings, null),
//						null).addResultListener(crl);
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
				if(SJavaParser.isExpressionString(component.getName()))
					ret = (String)SJavaParser.evaluateExpressionPotentially(component.getName(), model.getAllImports(), fetcher, this.component.getClassLoader());
				else
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
	public IFuture<Void> componentCreated(final IComponentDescription desc)
	{
		// Throw component events for extensions (envsupport)
		final IMonitoringComponentFeature	mon	= getComponent().getFeature0(IMonitoringComponentFeature.class);
		if(mon!=null)
		{
			return getComponent().getFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					Future<Void>	ret	= new Future<Void>();
					if(mon.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
					{
						// desc.getCause()
						MonitoringEvent me = new MonitoringEvent(desc.getName(), desc.getCreationTime(), 
							MonitoringEvent.TYPE_COMPONENT_CREATED, desc.getCreationTime(), PublishEventLevel.COARSE);
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
		final IMonitoringComponentFeature	mon	= getComponent().getFeature0(IMonitoringComponentFeature.class);
		if(mon!=null)
		{
			return getComponent().getFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					Future<Void>	ret	= new Future<Void>();
					if(mon.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.COARSE))
					{
//						desc.getCause()
						long time = getComponent().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IClockService.class)).getTime();
						MonitoringEvent me = new MonitoringEvent(desc.getName(), desc.getCreationTime(), 
							MonitoringEvent.TYPE_COMPONENT_DISPOSED, time, PublishEventLevel.COARSE);
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
		return getComponent().getFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
	/**
	 *  Test if the current thread is an external thread.
	 */
	protected boolean isExternalThread()
	{
		return !getComponent().getFeature(IExecutionFeature.class).isComponentThread();
	}

	/**
	 *  Get the childcount.
	 *  @return the childcount.
	 */
	public int getChildcount()
	{
		return ((CMSComponentDescription)getComponent().getDescription()).getChildren().length;
//		return childcount;
	}

//	/**
//	 *  Set the child count.
//	 *  @param childcount the childcount to set.
//	 */
//	public void setChildcount(int childcount)
//	{
//		this.childcount = childcount;
//	}
	
//	/**
//	 *  Inc the child count.
//	 */
//	public int incChildcount()
//	{
//		return ++this.childcount;
//	}
//	
//	/**
//	 *  Dec the child count.
//	 */
//	public int decChildcount()
//	{
//		return childcount>0? --childcount: childcount;
//	}
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @param type The local child type.
	 *  @param parent The parent (null for this).
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type, IComponentIdentifier parent)
	{
		return getComponent().getChildren(type, parent);
	}
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public IFuture<String> getLocalTypeAsync()
	{
		return new Future<String>(getLocalType());
	}
}
