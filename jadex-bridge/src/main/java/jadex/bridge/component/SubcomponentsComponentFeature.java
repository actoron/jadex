package jadex.bridge.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  This feature provides subcomponents.
 */
public class SubcomponentsComponentFeature	extends	AbstractComponentFeature	implements ISubcomponentsFeature
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
		
		if(config!=null)
		{
			final List<IComponentIdentifier> cids = new ArrayList<IComponentIdentifier>();
			ConfigurationInfo conf = model.getConfiguration(config);
			final ComponentInstanceInfo[] components = conf.getComponentInstances();
			SServiceProvider.getServiceUpwards(getServiceContainer(), IComponentManagementService.class)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
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
					
					createComponent(components, cms, model, 0, ret, cids);
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
		assert !getComponentAdapter().isExternalThread();
		
		if(i<components.length)
		{
			int num = getNumber(components[i], model);
			IResultListener<IComponentIdentifier> crl = new CollectionResultListener<IComponentIdentifier>(num, false, 
				createResultListener(new ExceptionDelegationResultListener<Collection<IComponentIdentifier>, Void>(fut)
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
						new CreationInfo(components[i].getConfiguration(), getArguments(components[i], model), getComponentAdapter().getComponentIdentifier(),
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
}
