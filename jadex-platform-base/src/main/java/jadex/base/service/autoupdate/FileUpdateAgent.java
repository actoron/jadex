package jadex.base.service.autoupdate;

import jadex.bridge.service.types.daemon.StartOptions;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Arguments(
{
	@Argument(name="cur", clazz=String.class, defaultvalue="jadex-2.1.zip"),
	@Argument(name="scandir", clazz=String.class, defaultvalue="."),
})
@Agent
public class FileUpdateAgent extends UpdateAgent
{
	@AgentArgument
	protected String scandir;

	@AgentArgument
	protected String cur;

	/**
	 * 
	 */
	protected StartOptions generateStartOptions(UpdateInfo ui)
	{
		final StartOptions ret = super.generateStartOptions(ui);
		
		if(ui.getAccess()!=null)
		{
			File dir = new File((String)ui.getAccess());
			File[] jars = dir.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".jar");
				}
			});
			
			StringBuffer buf = new StringBuffer();
			for(int i=0; i<jars.length; i++)
			{
				buf.append(jars[i]);
				if(i+1<jars.length)
					buf.append(File.pathSeparator);
			}
			ret.setClassPath(buf.toString());
		}
		
		return ret;
	}
	
	/**
	 *  Check if an update is available.
	 */
	protected IFuture<UpdateInfo> checkForUpdate()
	{
		final Future<UpdateInfo> ret = new Future<UpdateInfo>();
		
		File distdir = new File(scandir);
		File curdist = new File(distdir, cur);
		if(distdir.exists() && curdist.exists())
		{
			File[] dists = distdir.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().startsWith("jadex") && name.endsWith(".zip");
				}
			});
			
			long curdate = curdist.lastModified();
			File newdist = null;
			for(File dist: dists)
			{
				if(dist.lastModified()>curdate)
				{
					newdist = dist;
				}
			}
			
			if(newdist!=null)
			{
				try
				{
					unzip(new ZipFile(newdist), null);
				}
				catch(Exception e)
				{
				}
			}
		}
		
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static void unzip(ZipFile zip, File dir)
	{
		Enumeration<? extends ZipEntry> files = zip.entries();
		FileOutputStream fos = null;
		InputStream is = null;
		
		for(ZipEntry entry=files.nextElement(); files.hasMoreElements(); )
		{
			try
			{
				is = zip.getInputStream(entry);
				byte[] buffer = new byte[1024];
				int bytesRead = 0;

				File f = new File(dir.getAbsolutePath()+ File.separator + entry.getName());

				if(entry.isDirectory())
				{
					f.mkdirs();
					continue;
				}
				else
				{
					f.getParentFile().mkdirs();
					f.createNewFile();
				}

				fos = new FileOutputStream(f);

				while((bytesRead = is.read(buffer))!= -1)
				{
					fos.write(buffer, 0, bytesRead);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(fos!=null)
				{
					try
					{
						fos.close();
					}
					catch(IOException e)
					{
					}
				}
			}
		}
		if(is!=null)
		{
			try
			{
				is.close();
			}
			catch(IOException e)
			{
			}
		}
	}
}
