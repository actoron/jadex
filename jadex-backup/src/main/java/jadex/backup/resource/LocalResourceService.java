package jadex.backup.resource;

import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
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
	protected IInternalAccess	agent;
	
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
			remote.getFileInfo("/").addResultListener(new ExceptionDelegationResultListener<FileMetaInfo, Collection<BackupEvent>>(ret)
			{
				public void customResultAvailable(FileMetaInfo result)
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
	public ITerminableIntermediateFuture<BackupEvent> updateFromRemote(IResourceService remote, final FileMetaInfo localfi, final FileMetaInfo remotefi)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(localfi!=null && !localfi.getPath().equals(remotefi.getPath()))
		{
			ret.setException(new RuntimeException("File location differs: "+localfi.getPath())); 
		}
		else if(!getResource().isCurrent(remotefi.getPath(), localfi))
		{
			ret.setException(new RuntimeException("Local file has changed: "+localfi.getPath()));
		}
		else if(remotefi.isExisting())
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
							ret.setExceptionIfUndone(e);
						}
					}
				}
			});
		}
		else
		{
			try
			{
				getResource().updateFromRemote(localfi, remotefi, null);
				ret.setFinishedIfUndone();
			}
			catch(Exception e)
			{
				ret.setExceptionIfUndone(e);
			}
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
	public ITerminableIntermediateFuture<BackupEvent> overrideRemoteChange(IResourceService remote, FileMetaInfo localfi, FileMetaInfo remotefi)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(localfi!=null && !localfi.getPath().equals(remotefi.getPath()))
		{
			ret.setException(new RuntimeException("File location differs: "+localfi.getPath())); 
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
	public ITerminableIntermediateFuture<BackupEvent> updateAsCopy(IResourceService remote, final FileMetaInfo localfi, final FileMetaInfo remotefi)
	{
		final TerminableIntermediateFuture<BackupEvent>	ret	= new TerminableIntermediateFuture<BackupEvent>(); 
		
		if(!remote.getResourceId().equals(getResource().getResourceId()))
		{
			ret.setException(new RuntimeException("Resource id differs: "+remote.getResourceId())); 
		}
		else if(localfi!=null && !localfi.getPath().equals(remotefi.getPath()))
		{
			ret.setException(new RuntimeException("File location differs: "+localfi.getPath())); 
		}
		else if(!getResource().isCurrent(remotefi.getPath(), localfi))
		{
			ret.setException(new RuntimeException("Local file has changed: "+localfi.getPath()));
		}
		else if(remotefi.isExisting())
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
							ret.setExceptionIfUndone(e);
						}
					}
				}
			});
		}
		else
		{
			try
			{
				getResource().updateAsCopy(localfi, remotefi, null);
				ret.setFinishedIfUndone();
			}
			catch(Exception e)
			{
				ret.setExceptionIfUndone(e);
			}			
		}

		return ret;		
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the managed resource. 
	 */
	protected IBackupResource	getResource()
	{
		return ((ResourceProviderAgent)agent.getComponentFeature(IPojoComponentFeature.class).getPojoAgent()).getResource();
	}
	
	/**
	 *  Incrementally scan a remote resource for changes.
	 */
	protected void	doScan(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<StackElement> stack)
	{
//		System.out.println("+++do update: "+getResource().getResourceId()+", "+getResource().getLocalId()+", "+remote.getLocalId()+", "+stack);
		
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
				if(top.getFileInfo().getData().isDirectory())
				{
					// Currently only recurses into directory contents (todo: directory update i.e. deletion of files)
					ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_STATE, top.getFileInfo(), null, IBackupResource.FILE_UNCHANGED)); // todo: dir events?
					remote.getDirectoryContents(top.getFileInfo()).addResultListener(new IResultListener<Collection<FileMetaInfo>>()
					{
						public void resultAvailable(Collection<FileMetaInfo> result)
						{
							final StackElement	top	= stack.get(stack.size()-1);
							
							for(FileMetaInfo fi: result)
							{
								top.getSubfiles().add(new StackElement(fi));
							}
							doScan(remote, ret, stack);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("exo: "+exception);
							ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.ERROR, null, top.getFileInfo(), exception));
							doScan(remote, ret, stack);
						}
					});

				}
				else
				{
					Tuple2<FileMetaInfo, String> state = getResource().getState(top.getFileInfo());
					ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.FILE_STATE, state.getFirstEntity(), top.getFileInfo(), state.getSecondEntity()));
					doScan(remote, ret, stack);
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
	protected IFuture<File>	downloadFile(final IResourceService remote, final FileMetaInfo remotefi, final TerminableIntermediateFuture<BackupEvent> ret)
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
						final File	tmp	= getResource().getTempLocation(remotefi.getPath(), remote);
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
										ret.addIntermediateResultIfUndone(new BackupEvent(BackupEvent.DOWNLOAD_STATE, null, remotefi, Double.valueOf(result.doubleValue()/remotefi.getData().getSize())));
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
