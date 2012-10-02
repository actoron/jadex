package jadex.backup.resource;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 *  A component that publishes a local folder.
 */
@Arguments(
	@Argument(name="dir", clazz=String.class, description="The directory to publish.")
)
@ProvidedServices(
	@ProvidedService(type=IResourceService.class, implementation=@Implementation(expression="$pojoagent"))
)
@Agent
@Service
public class ResourceProviderAgent	implements IResourceService
{
	//-------- attributes --------
	
	/** The directory to publish as resource. */
	@AgentArgument
	protected String	dir;
	
	/** The resource id. */
	protected String	id;
	
	/** The resource directory. */
	protected File	root;
	
	//-------- constructors --------
	
	/**
	 *  Called at startup.
	 */
	@AgentCreated
	public void	start()
	{
		this.root	= new File(dir);
		root.mkdirs();
		File	fprops	= new File(root, ".jadexbackup/resource.properties");
		if(fprops.exists())
		{
			try
			{
				FileInputStream	fips	= new FileInputStream(fprops);
				Properties	props	= new Properties();
				props.load(fips);
				fips.close();
				id	= props.getProperty("id");
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			this.id	= root.getName()+"_"+UUID.randomUUID().toString();
			Properties	props	= new Properties();
			props.setProperty("id", id);
			fprops.getParentFile().mkdirs();
			try
			{
				FileOutputStream	fops	= new FileOutputStream(fprops);
				props.store(fops, "Jadex Backup meta information.");
				fops.close();
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	//-------- IResourceService interface --------

	/**
	 *  Get the resource id.
	 *  The resource id is a globally unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 *  
	 *  The id is static, i.e. it does not change during
	 *  the lifetime of the service.
	 */
	public String	getResourceId()
	{
		return id;
	}
	
	/**
	 *  Get information about local files.
	 */
	public IFuture<FileInfo[]>	getFiles(FileInfo dir)
	{
		List<FileInfo>	ret	= null;
		File	fdir	= dir!=null ? dir.toFile(root) : root;
		if(dir==null || fdir.lastModified()>dir.getTimeStamp())
		{
			ret	= new ArrayList<FileInfo>();
			for(String file: fdir.list())
			{
				FileInfo	fi	= FileInfo.fromFile(root, new File(fdir, file));
				if(!".jadexbackup".equals(fi.getLocation()))
				{
					ret.add(fi);
				}
			}
		}
		return new Future<FileInfo[]>(ret==null ? null : ret.toArray(new FileInfo[ret.size()]));
	}
}
