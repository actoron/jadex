package jadex.tools.web.jcc;

import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.core.Response;

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
			InputStream is = SUtil.getResource0(name, agent.getClassLoader());
			sc = new Scanner(is);
			ret = sc.useDelimiter("\\A").next();
			
			System.out.println("loading: "+name+" "+ret.length());
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
	 */
	public IFuture<Response> loadResource(String filename)
	{
		String res = internalLoadResource(filename);
		String mt = SUtil.guessContentTypeByFilename(filename);
		Response r = Response.ok(res).header("Content-Type", mt).build();
		return new Future<Response>(r);
	}
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public abstract IFuture<String> getPluginName();
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public abstract String getPluginUIPath();

}
