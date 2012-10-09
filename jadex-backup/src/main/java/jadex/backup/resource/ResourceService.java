package jadex.backup.resource;

import jadex.bridge.IInputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IPojoMicroAgent;
import jadex.micro.MicroAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 *  Local (i.e. user-oriented) interface to a resource. 
 */
@Service
public class ResourceService	implements IResourceService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected MicroAgent	agent;
	
	/** The resource meta information. */
	protected BackupResource	resource;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@ServiceStart
	public void	start()
	{
		resource	= ((ResourceProviderAgent)((IPojoMicroAgent)agent).getPojoAgent()).getResource();
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
//			resource.checkForConflicts(dir);	// fail if modified in mean time.
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
			soc.writeFromInputStream(new FileInputStream(resource.getFile(file.getLocation())), agent.getExternalAccess());
			ret.setResult(soc.getInputConnection());
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
}
