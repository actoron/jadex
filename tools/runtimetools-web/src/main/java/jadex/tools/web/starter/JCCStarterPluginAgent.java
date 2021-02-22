package jadex.tools.web.starter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.base.SRemoteGui;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
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
import jadex.commons.ICommand;
import jadex.commons.MethodInfo;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
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
		return new Future<String>("Starter");
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
		//return "jadex/tools/web/starter/starter.tag";
		return "jadex/tools/web/starter/starter.js";
	}
	
	/**
	 *  Get the plugin icon.
	 *  @return The plugin icon.
	 */
	public IFuture<byte[]> getPluginIcon()
	{
		return loadResource("jadex/tools/web/starter/images/starter.png");
	}
	
	/**
	 *  Get all startable component models.
	 *  @return The file names of the component models.
	 * /
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
				.then(libs -> {libs.getComponentModels().delegate(ret);}).catchEx(ret);
		}
		
		return ret;
	}*/
	
	/**
	 *  Get all startable component models.
	 *  @return The file names of the component models.
	 */
	public ISubscriptionIntermediateFuture<Collection<String[]>> getComponentModelsAsStream(IComponentIdentifier cid)
	{
		final SubscriptionIntermediateFuture<Collection<String[]>> ret = new SubscriptionIntermediateFuture<>();
		if(cid==null || cid.hasSameRoot(cid))
		{
			SComponentFactory.getComponentModelsAsStream(agent).addResultListener(new IIntermediateResultListener<Collection<String[]>>()
			{
				public void intermediateResultAvailable(Collection<String[]> result)
				{
					ret.addIntermediateResult(result);
				}
				
				public void resultAvailable(Collection<Collection<String[]>> result)
				{
					for(Collection<String[]> res: result)
					{
						intermediateResultAvailable(res);
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
				
			    public void finished()
			    {
			    	ret.setFinished();
			    }
			    
			    public void maxResultCountAvailable(int max)
			    {
			    	ret.setMaxResultCount(max);
			    	//System.out.println("max count is: "+max);
			    }
			});
		}
		else
		{
			agent.getExternalAccess(cid).scheduleStep(ia ->
			{
				SComponentFactory.getComponentModelsAsStream(ia).addResultListener(new IIntermediateResultListener<Collection<String[]>>()
				{
					public void intermediateResultAvailable(Collection<String[]> result)
					{
						ret.addIntermediateResult(result);
					}
					
					public void resultAvailable(Collection<Collection<String[]>> result)
					{
						for(Collection<String[]> res: result)
						{
							intermediateResultAvailable(res);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
					
				    public void finished()
				    {
				    	ret.setFinished();
				    }
				    
				    public void maxResultCountAvailable(int max)
				    {
				    	ret.setMaxResultCount(max);
				    	//System.out.println("max count is: "+max);
				    }
				});
				return IFuture.DONE;
			}).catchEx(ret);
		}
			
		return ret;
	}
	
	/*public ISubscriptionIntermediateFuture<String[]> getComponentModelsAsStream(IComponentIdentifier cid)
	{
		final SubscriptionIntermediateFuture<String[]> ret = new SubscriptionIntermediateFuture<>();
		//if(cid==null || cid.hasSameRoot(cid))
		//{
			ILibraryService ls = agent.getLocalService(ILibraryService.class);
			ls.getComponentModelsAsStream().addResultListener(new IIntermediateResultListener<Collection<String[]>>()
			{
				public void intermediateResultAvailable(Collection<String[]> result)
				{
					for(String[] res: result)
					{
						ret.addIntermediateResult(res);
					}
				}
				
				public void resultAvailable(Collection<Collection<String[]>> result)
				{
					for(Collection<String[]> res: result)
					{
						intermediateResultAvailable(res);
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
				
			    public void finished()
			    {
			    	ret.setFinished();
			    }
			    
			    public void maxResultCountAvailable(int max)
			    {
			    	ret.setMaxResultCount(max);
			    	System.out.println("max count is: "+max);
			    }
			});
		/*}
		else
		{
			agent.searchService(new ServiceQuery<ILibraryService>(ILibraryService.class).setPlatform(cid).setScope(ServiceScope.PLATFORM))
				.thenAccept(libs -> {libs.getComponentModels().delegate(ret);}).exceptionally(ret);
		}* /
			
		return ret;
	}*/
	
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
	 *  Kill a component.
	 *  @param cid The component id.
	 *  @return The component id.
	 */
	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier id, IComponentIdentifier cid)
	{
		return agent.getExternalAccess(id).killComponent();
	}

	
	/**
	 *  Load a component model.
	 *  @param filename The filename.
	 *  @return The component model.
	 */
	public IFuture<IModelInfo> loadComponentModel(String filename, IComponentIdentifier cid)
	{
		IFuture<IModelInfo> ret =  SComponentFactory.loadModel(cid!=null? agent.getExternalAccess(cid): agent, filename, null);
		IModelInfo m = ret.get();
		if(m==null)
			System.out.println("Loading: "+filename+" "+m);
		return ret;
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
	 *  Get the child component descriptions.
	 *  @param parent The component id of the parent.
	 *  @return The component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildComponentDescriptions(IComponentIdentifier cid, IComponentIdentifier parent)
	{
		final Future<IComponentDescription[]> ret = new Future<IComponentDescription[]>();
		IExternalAccess ea = cid==null? agent: agent.getExternalAccess(cid);
		ea.getChildren(null, parent).then(cids -> 
		{
			FutureBarrier<IComponentDescription> barrier = new FutureBarrier<IComponentDescription>();
			for(int i=0; i<cids.length; i++)
			{
				IFuture<IComponentDescription>fut = ea.getDescription(cids[i]);
				barrier.addFuture(fut);
			}
			barrier.waitForResults().then(descs -> ret.setResult(descs==null? null: descs.toArray(new IComponentDescription[cids.length])))
				.catchEx(ex -> ret.setException(ex));
		});
		return ret;
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
	
	/**
	 *  Get the value of a nf property by name.
	 *  @param name The prop name.
	 *  @return The value.
	 */
	public IFuture<Object> getNFValue(IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req, String name)
	{
		IFuture<Object> ret = null;
		
		IExternalAccess ea = cid!=null? agent.getExternalAccess(cid): sid!=null? agent.getExternalAccess(sid.getProviderId()): null;
		
		if(req!=null && req.booleanValue())
		{
			if(mi!=null)
			{
				//ret = ea.getRequiredMethodNFPropertyValue(sid, mi, name);
				ret = (Future)ea.getRequiredMethodNFPropertyPrettyPrintValue(sid, mi, name);
			}
			else
			{
				//ret = ea.getRequiredNFPropertyValue(sid, name);
				ret = (Future)ea.getRequiredNFPropertyPrettyPrintValue(sid, name);
			}
		}
		// provided services and methods
		else if(sid!=null)
		{
			if(mi!=null)
			{
				//ret = ea.getMethodNFPropertyValue(sid, mi, name);
				ret = (Future)ea.getMethodNFPropertyPrettyPrintValue(sid, mi, name);
			}
			else
			{
				//ret = ea.getNFPropertyValue(sid, name);
				ret = (Future)ea.getNFPropertyPrettyPrintValue(sid, name);
			}
		}
		// components
		else if(ea!=null)
		{
			//ret = ea.getNFPropertyValue(name);
			ret = (Future)ea.getNFPropertyPrettyPrintValue(name);
		}
		else
		{
			ret = new Future<>(new RuntimeException("No provider set"));
		}		
		
		return ret;
	}
	
	//protected int cnt = 0;
	/**
	 *  Returns the values about a non-functional property of this service.
	 *  @param cid The component id.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, Object>> getNFPropertyValues(IComponentIdentifier cid, IServiceIdentifier sid, MethodInfo mi, Boolean req, String name)
	{
		//int mycnt = cnt++;
		//System.out.println("getNFPropertyValues "+mycnt);
		final Future<Map<String, Object>> ret = new Future<>();
//		ret.addResultListener(new IResultListener<Map<String,Object>>()
//		{
//			@Override
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("getNFPropertyValues out ex"+mycnt+" "+exception);
//			}
//			
//			@Override
//			public void resultAvailable(Map<String, Object> result)
//			{
//				System.out.println("getNFPropertyValues out "+mycnt);
//			}
//		});
		
		final Map<String, Object> res = new HashMap<>();
		
		if(name!=null)
		{
			getNFValue(cid, sid, mi, req, name).then(val ->
			{
				res.put(name, val);
				ret.setResult(res);
			});
		}
		else
		{
			IExternalAccess ea = cid!=null? agent.getExternalAccess(cid): sid!=null? agent.getExternalAccess(sid.getProviderId()): null;
			
			final ICommand<Map<String, INFPropertyMetaInfo>> getvals = new ICommand<Map<String, INFPropertyMetaInfo>>()
			{
				public void execute(Map<String, INFPropertyMetaInfo> vals)
				{
					FutureBarrier<Object> bar = new FutureBarrier<>();
					
					vals.values().forEach(meti -> 
					{
						IFuture<Object> valfut = getNFValue(cid, sid, mi, req, meti.getName());
								
						if(valfut!=null)
						{
							bar.addFuture(valfut);
							valfut.then(val -> 
							{
								res.put(meti.getName(), val);
							});
						}
					});
					
					bar.waitFor().then(Void -> ret.setResult(res)).catchEx(ret);
				}
			};
			
			// required services and methods
			if(req!=null && req.booleanValue())
			{
				if(mi!=null)
				{
					ea.getRequiredMethodNFPropertyMetaInfos(sid, mi).then(mis -> getvals.execute(mis));
				}
				else
				{
					ea.getRequiredNFPropertyMetaInfos(sid).then(mis -> getvals.execute(mis));
				}
			}
			// provided services and methods
			else if(sid!=null)
			{
				if(mi!=null)
				{
					ea.getMethodNFPropertyMetaInfos(sid, mi).then(mis -> getvals.execute(mis));
				}
				else
				{
					ea.getNFPropertyMetaInfos(sid).then(mis -> getvals.execute(mis));
				}
			}
			// components
			else if(ea!=null)
			{
				ea.getNFPropertyMetaInfos().then(mis -> getvals.execute(mis));
			}
			else
			{
				ret.setException(new RuntimeException("Provider not set."));
			}
		}
		
		return ret;
	}

}
