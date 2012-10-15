package jadex.backup.resource;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.SUtil;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.IPojoMicroAgent;
import jadex.micro.MicroAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
	
	/** The resource provider agent. */
	protected ResourceProviderAgent	rpa;
	
	/** Synchronization requests that are queued or in progress. */
	protected Map<IResourceService, TerminableIntermediateFuture<BackupEvent>>	updates;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@ServiceStart
	public void	start()
	{
		rpa	= (ResourceProviderAgent)((IPojoMicroAgent)agent).getPojoAgent();
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
	 *  Get the local resource id.
	 *  The local resource id is a unique id that is
	 *  used to identify an individual instance of a
	 *  distributed resource on a specific host.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the resource.
	 */
	public String	getLocalId()
	{
		return rpa.getResource().getLocalId();
	}
	
	/**
	 *  Update the local resource with all
	 *  changes from all available remote resource.
	 *  @return All changed files and directories that are being processed.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	updateAll()
	{
		final int[]	finished	= new int[2]; // [search_finished, queued_updates]
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>();
		agent.getServiceContainer().searchServices(IResourceService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new IntermediateDefaultResultListener<IResourceService>()
		{
			public void intermediateResultAvailable(IResourceService result)
			{
				// Synchronize with matching remote resources, but exclude self.
				if(!ret.isDone() && result.getResourceId().equals(rpa.getResource().getResourceId())
					&& !result.getLocalId().equals(rpa.getResource().getLocalId()))
				{
					System.out.println("updateAll found: "+result+" "+result.getLocalId()+" "+rpa.getResource().getResourceId());
					finished[1]++;
					update(result).addResultListener(new IntermediateDefaultResultListener<BackupEvent>()
					{
						public void intermediateResultAvailable(BackupEvent file)
						{
							ret.addIntermediateResultIfUndone(file);
						}
						
						public void finished()
						{
							System.out.println("update finished");
							finished[1]--;
							checkFinished();
						}
		
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("update finished: "+exception);
							finished[1]--;
							checkFinished();
						}
					});
				}
			}
			
			public void finished()
			{
				System.out.println("search finished");
				finished[0]++;
				checkFinished();
			}

			public void exceptionOccurred(Exception exception)
			{
				System.out.println("search finished: "+exception);
				finished[0]++;
				checkFinished();
			}
			
			protected void	checkFinished()
			{
				System.out.println("finished: "+SUtil.arrayToString(finished));
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
		
		if(!remote.getResourceId().equals(rpa.getResource().getResourceId()))
		{
			ret	= new TerminableIntermediateFuture<BackupEvent>(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(updates!=null && updates.containsKey(remote))
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

			remote.getFileInfo("/").addResultListener(new IResultListener<FileInfo>()
			{
				public void resultAvailable(FileInfo result)
				{
					List<StackElement>	stack	= new ArrayList<StackElement>();
					stack.add(new StackElement(result));
					doUpdate(remote, ret, stack);
				}

				public void exceptionOccurred(Exception exception)
				{
					ret.setExceptionIfUndone(exception);
					updates.remove(remote);
					startNextUpdate();
				}
			});
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
//		System.out.println("+++do update: "+rpa.getResource().getResourceId()+", "+rpa.getResource().getLocalId()+", "+remote.getLocalId()+", "+stack);
		
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
				StackElement	next	= top.getNextSubfile();
				if(next!=null)
				{
					stack.add(next);
				}
				
				// No more sub elements -> pop element from stack
				else
				{
					stack.remove(stack.size()-1);
					try
					{
						if(top.getFileInfo().isDirectory())
						{
							// todo: synchronize directory in atomic step, i.e. copy new files from temporary location.
							// todo: delete old files.
//							resource.updateDirectory(top.getFileInfo(), top.getSubfiles());
						}
//						ret.addIntermediateResult(new BackupEvent(BackupResource.FILE_UNCHANGED, new FileData(rpa.getResource().getFile(top.getFileInfo().getLocation()))));
						ret.addIntermediateResultIfUndone(new BackupEvent(BackupResource.FILE_UNCHANGED, top.getFileInfo()));
					}
					catch(Exception e)
					{
//						ret.addIntermediateResult(new BackupEvent(BackupEvent.ERROR, new FileData(rpa.getResource().getFile(top.getFileInfo().getLocation())), e));
						ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.ERROR, top.getFileInfo(), e));
					}
				}
				
				doUpdate(remote, ret, stack);
			}
			
			// New top entry in stack -> do update and push sub directories (if any). 
			else
			{
//				ret.addIntermediateResult(new BackupEvent("synchronizing", new FileData(rpa.getResource().getFile(top.getFileInfo().getLocation()))));
				top.setSubfiles(new ArrayList<StackElement>());
				try
				{
					// Always update directories (hack?)
					if(top.getFileInfo().isDirectory())
					{
						updateDirectory(remote, ret, stack, top.getFileInfo());
					}
					else if(rpa.getResource().needsUpdate(top.getFileInfo()))
					{
						updateFile(remote, ret, stack, top.getFileInfo());
					}
					else
					{
						doUpdate(remote, ret, stack);
					}
				}
				catch(Exception e)
				{
					ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.ERROR, top.getFileInfo(), e));
					doUpdate(remote, ret, stack);
				}
			}		

		}		
	}
	
	/**
	 *  Update a directory.
	 */
	protected void	updateDirectory(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack, final FileInfo dir)
	{
		// Currently only recurses into directory contents as directory update (i.e. deletion of files) is done elsewhere (hack?)
		final File	fdir = rpa.getResource().getFile(dir.getLocation());
		fdir.mkdirs();
		ret.addIntermediateResultIfUndone(new BackupEvent(BackupResource.FILE_UNCHANGED, rpa.getResource().getFileInfo(fdir))); // todo: dir events?
		remote.getDirectoryContents(dir).addResultListener(new IResultListener<FileInfo[]>()
		{
			public void resultAvailable(FileInfo[] result)
			{
				final StackElement	top	= stack.get(stack.size()-1);
				
				for(FileInfo fi: result)
				{
					top.getSubfiles().add(new StackElement(fi));
				}
//				ret.addIntermediateResult(new BackupEvent("updated", new FileData(fdir), -1));
				doUpdate(remote, ret, stack);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.addIntermediateResultIfUndone(new BackupEvent(BackupResource.FILE_CONFLICT, rpa.getResource().getFileInfo(fdir)));
//				ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(fdir), -1));
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
//		final File	file	= rpa.getResource().getFile(fi.getLocation());
		String state = rpa.getResource().getState(fi);
		ret.addIntermediateResultIfUndone(new BackupEvent(state, fi, 0));
		doUpdate(remote, ret, stack);
	}
	
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<BackupEvent> updateFile(final IResourceService remote, final FileInfo fi)
	{
		final SubscriptionIntermediateFuture<BackupEvent> ret = new SubscriptionIntermediateFuture<BackupEvent>();
		
		final File	file = rpa.getResource().getFile(fi.getLocation());
		ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_UPDATE_START, rpa.getResource().getFileInfo(file), new Double(0)));
		remote.getFileContents(fi).addResultListener(new IResultListener<IInputConnection>()
		{
			public void resultAvailable(IInputConnection result)
			{
				try
				{
					final File	tmp	= rpa.getResource().getTempLocation(fi.getLocation(), remote);
					FileOutputStream	fos	= new FileOutputStream(tmp);
					result.writeToOutputStream(fos, agent.getExternalAccess()).addResultListener(new IIntermediateResultListener<Long>()
					{
						long time;
						public void intermediateResultAvailable(Long result)
						{
							if(time==0 || System.currentTimeMillis()-time>1000)
							{
								ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_UPDATE_STATE, fi, new Double(result.doubleValue()/fi.getSize())));
								time = System.currentTimeMillis();
							}
						}
						
						public void finished()
						{
							try
							{
								rpa.getResource().updateFile(fi, tmp);
								ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_UPDATE_END, fi, new Double(1)));
								ret.setFinished();
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
							ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_UPDATE_ERROR, fi, exception));
							ret.setException(exception);
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
				ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_UPDATE_ERROR, fi, exception));
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Update a file.
//	 *  Downloads the file to a temporary location.
//	 *  Afterwards renames the file and updates the meta information.
//	 */
//	protected void	updateFile(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack, final FileInfo fi)
//	{
//		final File	file	= rpa.getResource().getFile(fi.getLocation());
//		ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), 0));
//		remote.getFileContents(fi).addResultListener(new IResultListener<IInputConnection>()
//		{
//			public void resultAvailable(IInputConnection result)
//			{
//				try
//				{
//					final File	tmp	= rpa.getResource().getTempLocation(fi.getLocation(), remote);
//					FileOutputStream	fos	= new FileOutputStream(tmp);
//					result.writeToOutputStream(fos, agent.getExternalAccess()).addResultListener(new IIntermediateResultListener<Long>()
//					{
//						public void intermediateResultAvailable(Long result)
//						{
//							ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), result.doubleValue()/fi.getSize()));
//						}
//						
//						public void finished()
//						{
//							try
//							{
//								rpa.getResource().updateFile(fi, tmp);
//								ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), 1));
//								doUpdate(remote, ret, stack);
//							}
//							catch(Exception e)
//							{
//								exceptionOccurred(e);
//							}
//						}
//						
//						public void resultAvailable(Collection<Long> result)
//						{
//							finished();
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(file), -1));
//							doUpdate(remote, ret, stack);
//						}
//					});
//				}
//				catch(Exception e)
//				{
//					exceptionOccurred(e);
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(file), -1));
//				doUpdate(remote, ret, stack);
//			}
//		});
//	}
}
