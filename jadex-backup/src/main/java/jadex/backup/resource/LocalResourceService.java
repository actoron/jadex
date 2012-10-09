package jadex.backup.resource;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.IPojoMicroAgent;
import jadex.micro.MicroAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Local (i.e. user-oriented) interface to a resource. 
 */
@Service
public class LocalResourceService	implements ILocalResourceService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected MicroAgent	agent;
	
	/** The resource meta information. */
	protected BackupResource	resource;
	
	/** Synchronization requests that are queued or in progress. */
	protected Map<IResourceService, TerminableIntermediateFuture<BackupEvent>>	updates;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@ServiceStart
	public void	start()
	{
		resource	= ((ResourceProviderAgent)((IPojoMicroAgent)agent).getPojoAgent()).getResource();
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public void	stop()
	{
		if(updates!=null)
		{
			for(ITerminableIntermediateFuture<BackupEvent> update: updates.values())
			{
				update.terminate();
			}
		}
	}
	

	//-------- ILocalResourceService interface --------
	
	/**
	 *  Update the local resource with all
	 *  changes from all available remote resource.
	 *  @return All changed files and directories that are being processed.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	updateAll()
	{
		final int[]	finished	= new int[2]; // [search_finished, queued_updates]
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>();
		agent.getServiceContainer().searchServices(IResourceService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IntermediateDefaultResultListener<IResourceService>()
		{
			public void intermediateResultAvailable(IResourceService result)
			{
				// Synchronize with matching remote resources, but exclude self.
				System.out.println("updateAll found: "+result+" "+result.getLocalId()+" "+resource.getResourceId());
				if(!ret.isDone() && result.getResourceId().equals(resource.getResourceId())
					&& !result.getLocalId().equals(resource.getLocalId()))
				{
					finished[1]++;
					update(result).addResultListener(new IntermediateDefaultResultListener<BackupEvent>()
					{
						public void intermediateResultAvailable(BackupEvent file)
						{
							ret.addIntermediateResultIfUndone(file);
						}
						
						public void finished()
						{
							finished[1]--;
							checkFinished();
						}
		
						public void exceptionOccurred(Exception exception)
						{
							finished[1]--;
							checkFinished();
						}
					});
				}
			}
			
			public void finished()
			{
				finished[0]++;
				checkFinished();
			}

			public void exceptionOccurred(Exception exception)
			{
				finished[0]++;
				checkFinished();
			}
			
			protected void	checkFinished()
			{
				if(finished[0]>0 && finished[1]==0)
				{
					ret.setFinishedIfUndone();
				}
			}
		});
		return ret;
	}

	/**
	 *  Update the local resource with all
	 *  changes from the given remote resource.
	 *  @param remote	The remote resource to synchronize to.
	 *  @return All changed files and directories that are being processed.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	update(final IResourceService remote)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret;
		if(updates!=null && updates.containsKey(remote))
		{
			ret	= updates.get(remote);
		}
		else
		{
			ret	= new TerminableIntermediateFuture<BackupEvent>();
			if(updates==null)
			{
				updates	= new LinkedHashMap<IResourceService, TerminableIntermediateFuture<BackupEvent>>();
			}
			updates.put(remote, ret);
			
			if(updates.size()==1)
			{
				startNextUpdate();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Start a queued update.
	 */
	protected void	startNextUpdate()
	{
		if(updates!=null && !updates.isEmpty())
		{
			final IResourceService	remote	= updates.keySet().iterator().next();
			final TerminableIntermediateFuture<BackupEvent>	ret	= updates.get(remote);

			List<StackElement>	stack	= new ArrayList<StackElement>();
			stack.add(new StackElement("/"));
			doUpdate(remote, ret, stack);
		}
		else
		{
			updates	= null;
		}
	}
	
	/**
	 *  Incrementally update the resource.
	 */
	protected void	doUpdate(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack)
	{
		System.out.println("+++do update: "+resource.getResourceId()+", "+resource.getLocalId()+", "+remote.getLocalId()+", "+stack);
		
		// All done.
		if(stack.isEmpty())
		{
			ret.setFinishedIfUndone();
			updates.remove(remote);
			startNextUpdate();
		}
		
		// Select next element from stack		
		else
		{
			final StackElement	top	= stack.get(stack.size()-1);
			
			// Sub elements already known
			if(top.getSubfiles()!=null)
			{
				// Get next element from list and push on stack.
				if(top.getSubfiles().size()<top.getIndex())
				{
					String	file	= top.getNextSubfile();
					stack.add(new StackElement(file));
					ret.addIntermediateResult(new BackupEvent("synchronizing", new FileData(resource.getFile(file)), -1));
				}
				
				// No more sub elements -> pop element from stack
				else
				{
					stack.remove(stack.size()-1);
					try
					{
						if(top.getFileInfo().isDirectory())
						{
//							resource.updateDirectory(top.getFileInfo(), top.getSubfiles());
						}
						ret.addIntermediateResult(new BackupEvent("synchronized", new FileData(resource.getFile(top.getLocation())), -1));
					}
					catch(Exception e)
					{
						ret.addIntermediateResult(new BackupEvent("Problem: "+e, new FileData(resource.getFile(top.getLocation())), -1));
					}
				}
				
				doUpdate(remote, ret, stack);
			}
			
			// New top entry in stack -> do update and push sub directories (if any). 
			else
			{
				top.setSubfiles(new ArrayList<String>());
				remote.getFileInfo(top.getLocation()).addResultListener(new IResultListener<FileInfo>()
				{
					public void resultAvailable(FileInfo result)
					{
						try
						{
							// Always update directories (hack?)
							if(result.isDirectory())
							{
								updateDirectory(remote, ret, stack, result);
							}
							else if(resource.needsUpdate(result))
							{
								updateFile(remote, ret, stack, result);
							}
							else
							{
								doUpdate(remote, ret, stack);
							}
						}
						catch(Exception e)
						{
							exceptionOccurred(e);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(resource.getFile(top.getLocation())), -1));
						doUpdate(remote, ret, stack);
					}
				});
			}		

		}		
	}
	
	/**
	 *  Update a directory.
	 */
	protected void	updateDirectory(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack, final FileInfo dir)
	{
		// Currently only recurses into directory contents as directory update (i.e. deletion of files) is done elsewhere (hack?)
		final File	fdir	= resource.getFile(dir.getLocation());
		fdir.mkdirs();
		ret.addIntermediateResult(new BackupEvent("updating", new FileData(fdir), -1));
		remote.getDirectoryContents(dir).addResultListener(new IResultListener<String[]>()
		{
			public void resultAvailable(String[] result)
			{
				try
				{
					stack.get(stack.size()-1).setSubfiles(Arrays.asList(result));
					ret.addIntermediateResult(new BackupEvent("updated", new FileData(fdir), -1));
					doUpdate(remote, ret, stack);
				}
				catch(Exception e)
				{
					exceptionOccurred(e);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(fdir), -1));
				doUpdate(remote, ret, stack);
			}
		});
	}
	
	/**
	 *  Update a file.
	 *  Downloads the file to a temporary location.
	 *  Afterwards renames the file and updates the meta information.
	 */
	protected void	updateFile(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack, final FileInfo fi)
	{
		final File	file	= resource.getFile(fi.getLocation());
		ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), 0));
		remote.getFileContents(fi).addResultListener(new IResultListener<IInputConnection>()
		{
			public void resultAvailable(IInputConnection result)
			{
				try
				{
					final File	tmp	= resource.getTempLocation(fi.getLocation(), remote);
					FileOutputStream	fos	= new FileOutputStream(tmp);
					result.writeToOutputStream(fos, agent.getExternalAccess()).addResultListener(new IIntermediateResultListener<Long>()
					{
						public void intermediateResultAvailable(Long result)
						{
							ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), result.doubleValue()/fi.getSize()));
						}
						
						public void finished()
						{
							try
							{
								resource.updateFile(fi, tmp);
								ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), 1));
								doUpdate(remote, ret, stack);
							}
							catch(Exception e)
							{
								exceptionOccurred(e);
							}
						}
						
						public void resultAvailable(Collection<Long> result)
						{
							finished();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(file), -1));
							doUpdate(remote, ret, stack);
						}
					});
				}
				catch(Exception e)
				{
					exceptionOccurred(e);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(file), -1));
				doUpdate(remote, ret, stack);
			}
		});
	}
}
