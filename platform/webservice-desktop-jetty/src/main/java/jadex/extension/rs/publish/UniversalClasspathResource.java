package jadex.extension.rs.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import org.eclipse.jetty.util.resource.Resource;

import jadex.base.JarAsDirectory;
import jadex.commons.SUtil;

/**
 *  Single resource that can handle all items in classpath.
 */
public class UniversalClasspathResource extends Resource
{
	//-------- attributes --------
	
	/** The path of the resource (relative to classpath, e.g. package directory). */
	protected String path;
	
	/** URL cached for speed. */
	protected URL	url;
	
	/** File cached for speed. */
	protected File	file;
		
	//-------- constructors --------
	
	/**
	 *	Create a resource for the given path in classpath. 
	 */
	public UniversalClasspathResource(String rootpath)
	{
		this.path = rootpath;
	}

	//-------- Resource abstract methods --------
	
	public Resource addPath(String path) throws IOException ,java.net.MalformedURLException
	{
		if("/".equals(path))
			return this;
		else
			return new UniversalClasspathResource(this.path+path);
	}

	public java.net.URL getURL()
	{
		if(url==null)
		{
			url = getClass().getClassLoader().getResource(path);
			if(url==null && path.startsWith("/"))
				url = getClass().getClassLoader().getResource(path.substring(1));
			if(url==null && "/".equals(path))
				url = getClass().getClassLoader().getResource("index.html");
		}
		return url;
	}

	public boolean isContainedIn(Resource r) throws java.net.MalformedURLException
	{
		throw new UnsupportedOperationException();
	}

	public boolean isDirectory()
	{
		try
		{
			return asFile().isDirectory();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public long lastModified()
	{
		try
		{
			return asFile().lastModified();
		}
		catch(Exception e)
		{
			return -1;
		}
	}

	public long length()
	{
		try
		{
			return asFile().length();
		}
		catch(Exception e)
		{
			return -1;
		}
	}

	public String[] list()
	{
		try
		{
			return asFile().list();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public boolean renameTo(Resource dest) throws SecurityException
	{
		throw new UnsupportedOperationException();
	}

	public void close()
	{
		// this is called! nop?
		//throw new UnsupportedOperationException();
		url	= null;
		file	= null;
	}

	public boolean delete() throws SecurityException
	{
		// nop?
//		throw new UnsupportedOperationException();
		return false;
	}

	public boolean exists()
	{
		try
		{
			return asFile().exists();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 *  File representation of resource, including entries inside jar files.
	 */
	protected java.io.File asFile() throws IOException
	{
		if(file==null)
		{
			if(getURL()!=null)
			{
				if("file".equals(getURL().getProtocol()))
				{
					file	= SUtil.getFile(getURL());
				}
				else if("jar".equals(getURL().getProtocol()))
				{
					String	jar	= getURL().getPath();
					String	entry	= null;
					if(jar.contains("!/"))
					{
						entry	= jar.substring(jar.indexOf("!/")+2);
						jar	= jar.substring(0, jar.indexOf("!/"));
					}
					String path	= new URL(jar).getPath();
					try(ZipFile zip	= new ZipFile(path))
					{
						file	= new JarAsDirectory(path, zip.getEntry(entry));
					}
				}
			}
		}
		
		return file;
	}
	
	@Override
	public File getFile() throws IOException
	{
		// Do not expose entries in jar file as normal files, as jetty tries to access it using NIO File API and fails.
		return asFile() instanceof JarAsDirectory ? null : asFile();
	}

	@SuppressWarnings("resource")	// Inputstream gets closed by Jetty, no other resources held by JarFile
	public java.io.InputStream getInputStream() throws IOException
	{
		File	f	= asFile();
		if(f instanceof JarAsDirectory)
		{
			return new JarFile(((JarAsDirectory)f).getJarPath()).getInputStream(((JarAsDirectory)f).getZipEntry());
		}
		else
		{
			return new FileInputStream(getFile());
		}
	}

	public String getName()
	{
		throw new UnsupportedOperationException();
	}

	public java.nio.channels.ReadableByteChannel getReadableByteChannel() throws IOException
	{
		return Channels.newChannel(getInputStream());
	}
}