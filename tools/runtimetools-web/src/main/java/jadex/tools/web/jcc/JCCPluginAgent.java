package jadex.tools.web.jcc;

import java.io.InputStream;
import java.util.Scanner;

import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  Abstract base class for plugin agents.
 *  
 *  Supports UI code loading. 
 */
@Agent
public abstract class JCCPluginAgent 
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	/** The plugin component string. */
	protected String component;
	
	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public IInternalAccess getAgent() 
	{
		return agent;
	}

	/**
	 *  Get the plugin component (html).
	 *  @return The plugin code.
	 */
	public IFuture<String> getPluginComponent()
	{
		if(component==null)
			component = internalLoadResource(getPluginUIPath());
		
		return new Future<String>(component);
	}
		
	/**
	 *  Load a resource per resource name.
	 */
	public String internalLoadResource(String name)
	{
		String ret;
		
		Scanner sc = null;
		try
		{
			System.out.println("LOAD " + name);
			InputStream is = SUtil.getResource0(name, agent.getClassLoader());
			sc = new Scanner(is);
			ret = sc.useDelimiter("\\A").next();
			
			//System.out.println("loading: "+name+" "+ret.length());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if(sc!=null)
			{
				sc.close();
			}
		}
		
		return ret;
	}	

	/**
	 *  Load a string-based ressource (style or js).
	 *  @param filename The filename.
	 *  @return The text from the file.
	 * /
	public IFuture<Response> loadResource(String filename)
	{
		String res = internalLoadResource(filename);
		String mt = SUtil.guessContentTypeByFilename(filename);
		Response r = Response.ok(res).header("Content-Type", mt).build();
		return new Future<Response>(r);
	}*/
	
	/**
	 *  Load a string-based ressource (style or js).
	 *  @param filename The filename.
	 *  @return The text from the file.
	 */
	public IFuture<byte[]> loadResource(String filename)
	{System.out.println("LOADRESOURCE!" + filename);
		try
		{
		InputStream is = SUtil.getResource0(filename, agent.getClassLoader());
		//String mt = SUtil.guessContentTypeByFilename(filename);
		
		byte[] data = SUtil.readStream(is);
		/*StreamingOutput so = new StreamingOutput()
		{
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException
			{
				SUtil.copyStream(is, output);
			}
		};*/
		//Response r = Response.ok(data).header("Content-Type", mt).build();
		
		return new Future<byte[]>(data);
		}
		catch(Exception e)
		{
			return new Future<byte[]>(e);
		}
	}
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public abstract IFuture<String> getPluginName();
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public abstract IFuture<Integer> getPriority();
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public abstract String getPluginUIPath();

}
