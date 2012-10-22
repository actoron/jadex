package jadex.backup.resource;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.IPojoMicroAgent;
import jadex.micro.MicroAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		return getResource().getLocalId();
	}	

	/**
	 *  Scan for changes at the given remote resource
	 *  that need to be synchronized with the local resource.
	 *  @param remote	The remote resource to synchronize to.
	 *  @return Events of detected remote changes.
	 */
	public ITerminableIntermediateFuture<BackupEvent>	scanForChanges(final IResourceService remote)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else
		{
			remote.getFileInfo("/").addResultListener(new ExceptionDelegationResultListener<FileInfo, Collection<BackupEvent>>(ret)
			{
				public void customResultAvailable(FileInfo result)
				{
					List<StackElement>	stack	= new ArrayList<StackElement>();
					stack.add(new StackElement(result));
					doScan(remote, ret, stack);
				}
			});			
		}

		return ret;
	}
	
	/**
	 *  Update or revert the local file with the remote version.
	 *  Checks if local and remote file are unchanged with respect to the previous file infos.
	 *  @param remote The remote resource.
	 *  @param localfi The local file info.
	 *  @param remotefi The remote file info.
	 *  @return Status events while the file is being downloaded.
	 *  @throws Exception, e.g. when local or remote file have changed after the last scan.
	 */
	public ITerminableIntermediateFuture<BackupEvent> updateFromRemote(IResourceService remote, final FileInfo localfi, final FileInfo remotefi)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(localfi!=null && !localfi.getLocation().equals(remotefi.getLocation()))
		{
			ret.setException(new RuntimeException("File location differs: "+localfi.getLocation())); 
		}
		else if(!getResource().isCurrent(remotefi.getLocation(), localfi))
		{
			ret.setException(new RuntimeException("Local file has changed: "+localfi.getLocation()));
		}
		else
		{
			downloadFile(remote, remotefi, ret)
				.addResultListener(new ExceptionDelegationResultListener<File, Collection<BackupEvent>>(ret)
			{
				public void customResultAvailable(File tmp)
				{
					if(!ret.isDone())
					{
						try
						{
							getResource().updateFromRemote(localfi, remotefi, tmp);
							ret.setFinishedIfUndone();
						}
						catch(Exception e)
						{
							exceptionOccurred(e);
						}
					}
				}
			});
		}

		return ret;		
	}
	
	
	/**
	 *  Ignore the remote change and set the local file state as being newer.
	 *  As a result, the remote file will be reverted the next time the remote resource synchronizes to this resource. 
	 *  Checks if local and remote file are unchanged with respect to the previous file infos.
	 *  @param remote The remote resource.
	 *  @param localfi The local file info.
	 *  @param remotefi The remote file info.
	 *  @return Status events of the override operation.
	 *  @throws Exception when local or remote file have changed after the last scan.
	 */
	public ITerminableIntermediateFuture<BackupEvent> overrideRemoteChange(IResourceService remote, FileInfo localfi, FileInfo remotefi)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(localfi!=null && !localfi.getLocation().equals(remotefi.getLocation()))
		{
			ret.setException(new RuntimeException("File location differs: "+localfi.getLocation())); 
		}
		else
		{
			try
			{
				getResource().overrideRemoteChange(localfi, remotefi);
				ret.setFinishedIfUndone();
			}
			catch(Exception e)
			{
				ret.setException(e);
			}		
		}

		return ret;		
	}
	
	/**
	 *  Copy the local file before downloading the remote version.
	 *  If the remote version would be saved as copy, the change would be detected again on next scan.
	 *  Therefore the remote version is used as new local version whereas
	 *  the old local version is given a new name of the form 'name.copy.ext'.
	 *  @param remote The remote resource.
	 *  @param localfi The local file info.
	 *  @param remotefi The remote file info.
	 *  @return Status events while the file is being downloaded.
	 *  @throws Exception, e.g. when local or remote file have changed after the last scan.
	 */
	public ITerminableIntermediateFuture<BackupEvent> updateAsCopy(IResourceService remote, final FileInfo localfi, final FileInfo remotefi)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(localfi!=null && !localfi.getLocation().equals(remotefi.getLocation()))
		{
			ret.setException(new RuntimeException("File location differs: "+localfi.getLocation())); 
		}
		else if(!getResource().isCurrent(remotefi.getLocation(), localfi))
		{
			ret.setException(new RuntimeException("Local file has changed: "+localfi.getLocation()));
		}
		else
		{
			downloadFile(remote, remotefi, ret)
				.addResultListener(new ExceptionDelegationResultListener<File, Collection<BackupEvent>>(ret)
			{
				public void customResultAvailable(File tmp)
				{
					if(!ret.isDone())
					{
						try
						{
							getResource().updateAsCopy(localfi, remotefi, tmp);
							ret.setFinishedIfUndone();
						}
						catch(Exception e)
						{
							exceptionOccurred(e);
						}
					}
				}
			});
		}

		return ret;		
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the managed resource. 
	 */
	protected BackupResource	getResource()
	{
		return ((ResourceProviderAgent)((IPojoMicroAgent)agent).getPojoAgent()).getResource();
	}
	
	
	/**
	 *  Incrementally scan a remote resource for changes.
	 */
	protected void	doScan(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack)
	{
//		System.out.println("+++do update: "+rpa.getResource().getResourceId()+", "+rpa.getResource().getLocalId()+", "+remote.getLocalId()+", "+stack);
		
		// All done.
		if(stack.isEmpty() || ret.isDone())
		{
			ret.setFinishedIfUndone();
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
				}
				
				doScan(remote, ret, stack);
			}
			
			// New top entry in stack -> do update and push sub directories (if any). 
			else
			{
				top.setSubfiles(new ArrayList<StackElement>());
				// Always update (hack?)
				if(top.getFileInfo().isDirectory())
				{
					// Currently only recurses into directory contents (todo: directory update i.e. deletion of files)
					ret.addIntermediateResultIfUndone(new BackupEvent(BackupResource.FILE_UNCHANGED, top.getFileInfo())); // todo: dir events?
					remote.getDirectoryContents(top.getFileInfo()).addResultListener(new IResultListener<FileInfo[]>()
					{
						public void resultAvailable(FileInfo[] result)
						{
							final StackElement	top	= stack.get(stack.size()-1);
							
							for(FileInfo fi: result)
							{
								top.getSubfiles().add(new StackElement(fi));
							}
							doScan(remote, ret, stack);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.ERROR, null, top.getFileInfo(), exception));
							doScan(remote, ret, stack);
						}
					});

				}
				else
				{
					try
					{
						Tuple2<FileInfo, String> state = getResource().getState(top.getFileInfo());
						ret.addIntermediateResultIfUndone(new BackupEvent(state.getSecondEntity(), state.getFirstEntity(), top.getFileInfo(), 0));
						doScan(remote, ret, stack);
					}
					catch(Exception e)
					{
						ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.ERROR, null, top.getFileInfo(), e));
						doScan(remote, ret, stack);
					}
				}
			}		

		}		
	}
	
	/**
	 *  Download a remote file.
	 *  @param remote	The remote resource.
	 *  @param remotefi	The remote file info.
	 *  @param ret	The intermediate future to post download events.
	 *  @return The temporary location where the file was downloaded to.
	 */
	protected IFuture<File>	downloadFile(final IResourceService remote, final FileInfo remotefi, final TerminableIntermediateFuture<BackupEvent> ret)
	{
		final Future<File>	fut	= new Future<File>(); 
		remote.getFileContents(remotefi).addResultListener(new ExceptionDelegationResultListener<IInputConnection, Collection<BackupEvent>>(ret)
		{
			public void customResultAvailable(final IInputConnection icon)
			{
				if(!ret.isDone())
				{
					try
					{
						final File	tmp	= getResource().getTempLocation(remotefi.getLocation(), remote);
						FileOutputStream	fos	= new FileOutputStream(tmp);
						final ISubscriptionIntermediateFuture<Long>	write	= icon.writeToOutputStream(fos, agent.getExternalAccess());
						write.addResultListener(new IntermediateExceptionDelegationResultListener<Long, Collection<BackupEvent>>(ret)
						{
							long time;
							public void intermediateResultAvailable(Long result)
							{
								if(ret.isDone())
								{
									write.terminate();
									icon.close();
								}
								else
								{
									if(time==0 || System.currentTimeMillis()-time>1000)
									{
										ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.DOWNLOAD_STATE, null, remotefi, new Double(result.doubleValue()/remotefi.getSize())));
										time = System.currentTimeMillis();
									}
								}
							}
							
							public void finished()
							{
								fut.setResult(tmp);
							}
							
							public void customResultAvailable(Collection<Long> result)
							{
								finished();
							}
						});
					}
					catch(Exception e)
					{
						exceptionOccurred(e);
					}
				}
				else
				{
					icon.close();
				}
			}
		});
		return fut;
	}
}
