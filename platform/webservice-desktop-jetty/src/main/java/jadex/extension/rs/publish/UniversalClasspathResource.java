package jadex.extension.rs.publish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

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
		return new UniversalClasspathResource(this.path+path);
	}

	public java.net.URL getURL()
	{
		return getClass().getClassLoader().getResource(path);
	}

	public boolean isContainedIn(Resource r) throws java.net.MalformedURLException
	{
		throw new UnsupportedOperationException();
	}

	public boolean isDirectory()
	{
		try
		{
			return getFile().isDirectory();
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
			return getFile().lastModified();
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
			return getFile().length();
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
			return getFile().list();
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
		throw new UnsupportedOperationException();
	}

	public boolean delete() throws SecurityException
	{
		throw new UnsupportedOperationException();
	}

	public boolean exists()
	{
		try
		{
			return getFile().exists();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public java.io.File getFile() throws IOException
	{
		if("file".equals(getURL().getProtocol()))
		{
			return SUtil.getFile(getURL());
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
			return new JarAsDirectory(new URL(jar).getPath(), new ZipEntry(entry));
		}
		throw new UnsupportedOperationException();
	}

	public java.io.InputStream getInputStream() throws IOException
	{
		File	f	= getFile();
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