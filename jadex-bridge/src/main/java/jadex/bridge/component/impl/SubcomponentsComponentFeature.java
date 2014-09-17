package jadex.bridge.component.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  This feature provides subcomponents.
 */
public class SubcomponentsComponentFeature	extends	AbstractComponentFeature	implements ISubcomponentsFeature, IInternalSubcomponentsFeature
{
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public SubcomponentsComponentFeature()
	{
	}
	
	protected SubcomponentsComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IComponentFeature interface --------
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return ISubcomponentsFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 */
	public IComponentFeature createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new SubcomponentsComponentFeature(access, info);
	}
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void> init()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(component.getConfiguration()!=null)
		{
			final List<IComponentIdentifier> cids = new ArrayList<IComponentIdentifier>();
			ConfigurationInfo conf = component.getModel().getConfiguration(component.getConfiguration());
			final ComponentInstanceInfo[] components = conf.getComponentInstances();
			SServiceProvider.getServiceUpwards(component, IComponentManagementService.class)
				.addResultListener(component.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
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
					
					createComponent(components, cms, component.getModel(), 0, ret, cids);
				}
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
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
					cms.createComponent(getName(components[i], model, j+1), type.getName(),
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
	public IFuture<Void>	componentCreated(IComponentDescription desc, IModelInfo model)
	{
		return IFuture.DONE;
	}
}
