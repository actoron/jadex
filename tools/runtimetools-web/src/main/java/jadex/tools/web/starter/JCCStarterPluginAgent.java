package jadex.tools.web.starter;

import java.util.Collection;
import java.util.Map;

import jadex.base.SRemoteGui;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.MethodInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Starter web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="starterweb", type=IJCCStarterService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCStarterPluginAgent extends JCCPluginAgent implements IJCCStarterService
{
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("starter");
	}
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(100);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/starter/starter.tag";
	}
	
	/**
	 *  Get all startable component models.
	 *  @return The file names of the component models.
	 */
	public IFuture<Collection<String[]>> getComponentModels(final IComponentIdentifier cid)
	{
		Future<Collection<String[]>> ret = new Future<>();
		
		if(cid==null || cid.hasSameRoot(cid))
		{
			ILibraryService ls = agent.getLocalService(ILibraryService.class);
			ls.getComponentModels().delegate(ret);
		}
		else
		{
			agent.searchService(new ServiceQuery<ILibraryService>(ILibraryService.class).setPlatform(cid).setScope(ServiceScope.PLATFORM))
				.thenAccept(libs -> {libs.getComponentModels().delegate(ret);}).exceptionally(ret);
		}
		
		return ret;
	}
	
	/**
	 *  Create a component for a model.
	 * /
	public IFuture<IComponentIdentifier> createComponent(String filename)
	{
//		System.out.println("webjcc start: "+filename);
		
		IExternalAccess comp = agent.createComponent(new CreationInfo().setFilename(filename)).get();
		return new Future<IComponentIdentifier>(comp.getId());
	}*/
	
	/**
	 *  Create a component for a model.
	 */
	public IFuture<IComponentIdentifier> createComponent(CreationInfo ci, IComponentIdentifier cid)
	{
		//System.out.println("webjcc start: "+ci+", "+Thread.currentThread());
		
		IExternalAccess comp = agent.getExternalAccess(cid!=null? cid.getRoot(): agent.getId().getRoot()).createComponent(ci).get();
		return new Future<IComponentIdentifier>(comp.getId());
	}
	
	/**
	 *  Load a component model.
	 *  @param filename The filename.
	 *  @return The component model.
	 */
	public IFuture<IModelInfo> loadComponentModel(String filename, IComponentIdentifier cid)
	{
		return SComponentFactory.loadModel(cid!=null? agent.getExternalAccess(cid): agent, filename, null);
	}
	
	/**
	 *  Get the component descriptions.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions(IComponentIdentifier cid)
	{
		//System.out.println("getCompDescs start");
		IExternalAccess ea = cid==null? agent: agent.getExternalAccess(cid);
		return ea.getDescriptions();
	}
	
	/**
	 * Get a default icon for a file type.
	 */
	public IFuture<byte[]> loadComponentIcon(String type, IComponentIdentifier cid)
	{
		return SComponentFactory.getFileTypeIcon(cid!=null? agent.getExternalAccess(cid): agent, type);
	}
	
	/**
	 *  Subscribe to component events
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> subscribeToComponentChanges(IComponentIdentifier cid)
	{
		IExternalAccess ea = cid==null? agent: agent.getExternalAccess(cid);
		return ea.listenToAll();
	}

	// todo: second parameter with platform cid?! test
	
	/**
	 *  Get infos about services (provided, required).
	 *  @param cid The component id
	 */
	public IFuture<Object[]> getServiceInfos(IComponentIdentifier cid)
	{
		// can answer directly instead of delegation (schedules on component in SRemoteGui)
		// todo: make service call instead of SRemoteGui
		return SRemoteGui.getServiceInfos(agent.getExternalAccess(cid));
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param cid The component id.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos(IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req)
	{
		final Future<Map<String, INFPropertyMetaInfo>> ret = new Future<>();
		
		IExternalAccess ea = cid!=null? agent.getExternalAccess(cid): sid!=null? agent.getExternalAccess(sid.getProviderId()): null;
		
		// required services and methods
		if(req!=null && req.booleanValue())
		{
			if(mi!=null)
			{
				ea.getRequiredMethodNFPropertyMetaInfos(sid, mi).delegate(ret);
			}
			else
			{
				ea.getRequiredNFPropertyMetaInfos(sid).delegate(ret);
			}
		}
		// provided services and methods
		else if(sid!=null)
		{
			if(mi!=null)
			{
				ea.getMethodNFPropertyMetaInfos(sid, mi).delegate(ret);
			}
			else
			{
				ea.getNFPropertyMetaInfos(sid).delegate(ret);
			}
		}
		// components
		else if(ea!=null)
		{
			ea.getNFPropertyMetaInfos().delegate(ret);
		}
		else
		{
			ret.setException(new RuntimeException("Provider not set."));
		}
		
		return ret;
	}
}
