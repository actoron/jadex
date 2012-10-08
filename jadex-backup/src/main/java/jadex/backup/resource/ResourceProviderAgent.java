package jadex.backup.resource;

import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.deployment.FileData;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  A component that publishes a local folder.
 */
@Arguments({
	@Argument(name="dir", clazz=String.class, description="The directory to publish."),
	@Argument(name="id", clazz=String.class, description="The unique id of the global resource.")
})
@ProvidedServices({
	@ProvidedService(type=IResourceService.class, implementation=@Implementation(expression="$pojoagent")),
	@ProvidedService(type=ILocalResourceService.class, implementation=@Implementation(expression="$pojoagent"))
})
@Agent
@Service(IResourceService.class)
public class ResourceProviderAgent	implements IResourceService, ILocalResourceService
{
	//-------- attributes --------
	
	/** The component. */
	@Agent
	protected IInternalAccess	component;
	
	/** The directory to publish as resource. */
	@AgentArgument
	protected String	dir;
	
	/** The global resource id. */
	@AgentArgument
	protected String	id;
	
	/** The resource meta information. */
	protected BackupResource	resource;
	
	/** Synchronization requests that are queued or in progress. */
	protected Map<IResourceService, TerminableIntermediateFuture<BackupEvent>>	updates;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@AgentCreated
	public IFuture<Void>	start()
	{
		if(dir==null)
		{
			return new Future<Void>(new IllegalArgumentException("Dir nulls."));
		}
		if(id==null)
		{
			return new Future<Void>(new IllegalArgumentException("Id nulls."));
		}
		
		Future<Void>	ret	= new Future<Void>();
		try
		{
			this.resource	= new BackupResource(id, new File(dir), component.getComponentIdentifier());
			ret.setResult(null);			
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public void	stop()
	{
		if(updates!=null)
		{
			for(ITerminableIntermediateFuture<BackupEvent> update: updates.values())
			{
				update.terminate();
			}
		}
		
		resource.dispose();
	}
	
	//-------- IResourceService interface --------

	/**
	 *  Get the global resource id.
	 *  The global resource id is a unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the resource.
	 */
	public String	getResourceId()	
	{
		return resource.getResourceId();
	}
	
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
		return resource.getLocalId();
	}
	
	/**
	 *  Get information about a local file or directory.
	 *  @param file	The resource path of the file.
	 *  @return	The file info with all known time stamps.
	 */
	public IFuture<FileInfo>	getFileInfo(String file)
	{
		return new Future<FileInfo>(resource.getFileInfo(resource.getFile(file)));
	}
	
	/**
	 *  Get the contents of a directory.
	 *  @param dir	The file info of the directory.
	 *  @return	A list of plain file names (i.e. without path).
	 *  @throws Exception if the supplied file info is outdated.
	 */
	public IFuture<String[]>	getDirectoryContents(FileInfo dir)
	{
		Future<String[]>	ret	= new Future<String[]>();
		try
		{
			File	fdir	= resource.getFile(dir.getLocation());
			if(!fdir.isDirectory())
			{
				throw new IllegalArgumentException("Not a directory: "+dir.getLocation());
			}
			String[]	list = fdir.list(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return !".jadexbackup".equals(name);
				}
			});
			if(list==null)
			{
				throw new IOException("Could not read directory: "+dir.getLocation());
			}
			resource.checkForConflicts(dir);	// fail if modified in mean time.
			ret.setResult(list);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the contents of a file.
	 *  @param file	The file info of the file.
	 *  @return	A list of plain file names (i.e. without path).
	 *  @throws Exception if the supplied file info is outdated.
	 */
	public IFuture<IInputConnection>	getFileContents(FileInfo file)
	{
		Future<IInputConnection>	ret	= new Future<IInputConnection>();
		try
		{
			ServiceOutputConnection	soc	= new ServiceOutputConnection();
			soc.writeFromInputStream(new FileInputStream(resource.getFile(file.getLocation())), component.getExternalAccess());
			ret.setResult(soc.getInputConnection());
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
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
		component.getServiceContainer().searchServices(IResourceService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IntermediateDefaultResultListener<IResourceService>()
		{
			public void intermediateResultAvailable(IResourceService result)
			{
				// Synchronize with matching remote resources, but exclude self.
				System.out.println("result: "+result+" "+result.getLocalId()+" "+resource.getResourceId());
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

			List<String>	stack	= new ArrayList<String>();
			stack.add("/");
			doUpdate(remote, ret, stack, new ArrayList<List<String>>());
		}
		else
		{
			updates	= null;
		}
	}
	
	/**
	 *  Incrementally update the resource.
	 */
	protected void	doUpdate(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<String> stack, final List<List<String>> subdirs)
	{
		System.out.println("+++do update: "+stack+", "+subdirs);
		
		// All done.
		if(stack.isEmpty())
		{
			ret.setFinishedIfUndone();
			updates.remove(remote);
			startNextUpdate();
		}
		
		// Select next sub element from list
		else if(stack.size()==subdirs.size())
		{
			List<String>	files	= subdirs.get(subdirs.size()-1);
			
			// No more subdirectories -> pop empty list from stack
			if(files.isEmpty())
			{
				subdirs.remove(subdirs.size()-1);
				String file	= stack.remove(stack.size()-1);
				ret.addIntermediateResult(new BackupEvent("synchronized", new FileData(resource.getFile(file)), -1));
			}
			
			// Remove first element from list and push on stack.
			else
			{
				String	file	= files.remove(files.size()-1);
				String	parent	= stack.get(stack.size()-1);
				stack.add("/".equals(parent) ? parent + file : parent + "/" + file);
				ret.addIntermediateResult(new BackupEvent("synchronizing", new FileData(resource.getFile(file)), -1));
			}
			doUpdate(remote, ret, stack, subdirs);
		}
		
		// New top entry in stack -> do update and push sub directories (if any). 
		else
		{
			final String	next	= stack.get(stack.size()-1);
			subdirs.add(new ArrayList<String>());	// Add empty list to be filled with children or indicating that processing is done.
			remote.getFileInfo(next).addResultListener(new IResultListener<FileInfo>()
			{
				public void resultAvailable(FileInfo result)
				{
					try
					{
						// Hack!!! update always (for testing)
//						if(resource.needsUpdate(result))
						{
							if(result.isDirectory())
							{
								updateDirectory(remote, ret, stack, subdirs, result);
							}
							else
							{
								updateFile(remote, ret, stack, subdirs, result);
							}
						}
//						else
//						{
//							doUpdate(remote, ret, stack, tree);
//						}
					}
					catch(Exception e)
					{
						exceptionOccurred(e);
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(resource.getFile(next)), -1));
					doUpdate(remote, ret, stack, subdirs);
				}
			});
		}		
	}
	
	/**
	 *  Update a directory.
	 */
	protected void	updateDirectory(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<String> stack, final List<List<String>> subdirs, final FileInfo dir)
	{
		final File	fdir	= resource.getFile(dir.getLocation());
		fdir.mkdirs();
		ret.addIntermediateResult(new BackupEvent("updating", new FileData(fdir), -1));
		remote.getDirectoryContents(dir).addResultListener(new IResultListener<String[]>()
		{
			public void resultAvailable(String[] result)
			{
				// Todo: delete missing files
				try
				{
					// Todo: update only after subtree sync completed.
					resource.updateFileInfo(dir);
					subdirs.get(subdirs.size()-1).addAll(Arrays.asList(result));
					ret.addIntermediateResult(new BackupEvent("updated", new FileData(fdir), -1));
					doUpdate(remote, ret, stack, subdirs);
				}
				catch(Exception e)
				{
					exceptionOccurred(e);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.addIntermediateResult(new BackupEvent("Problem: "+exception, new FileData(fdir), -1));
				doUpdate(remote, ret, stack, subdirs);
			}
		});
	}
	
	/**
	 *  Update a file.
	 */
	protected void	updateFile(final IResourceService remote, final TerminableIntermediateFuture<BackupEvent> ret, final List<String> stack, final List<List<String>> subdirs, final FileInfo fi)
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
					result.writeToOutputStream(fos, component.getExternalAccess()).addResultListener(new IIntermediateResultListener<Long>()
					{
						public void intermediateResultAvailable(Long result)
						{
							ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), result.doubleValue()/fi.getSize()));
						}
						
						public void finished()
						{
							try
							{
								resource.updateFileInfo(fi);
								File	orig	= resource.getFile(fi.getLocation());
								orig.delete();
								tmp.renameTo(orig);
								ret.addIntermediateResult(new BackupEvent("updating", new FileData(file), 1));
								doUpdate(remote, ret, stack, subdirs);
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
							doUpdate(remote, ret, stack, subdirs);
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
				doUpdate(remote, ret, stack, subdirs);
			}
		});
	}
}
