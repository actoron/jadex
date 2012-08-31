package jadex.platform.service.cli;

import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *  The cli shell contains the commands and
 *  allows for executing a command.
 *  
 *  Use addAllCommandsFromClassPath() to add
 *  all commands from the current classpath or
 *  addCommand() to add them manually.
 */
public class CliShell implements ICliService
{
	//-------- attributes --------
	
	/** The commands. */
	protected Map<String, ICliCommand> commands;
	
	/** The context. */
	protected CliContext context;
	
	//-------- constructors --------
	
	/**
	 *  Create a new cli.
	 */
	public CliShell(Object context)
	{
		this.commands = new LinkedHashMap<String, ICliCommand>();
		this.context = new CliContext(this, context);
	}
	
	//-------- methods --------
	
	/**
	 *  Add all commands from classpath.
	 *  @param cl The classloader to use.
	 */
	public void addAllCommandsFromClassPath(ClassLoader cl)
	{
		URL[] urls = null;
//		ClassLoader cl = getClass().getClassLoader();
		if(cl instanceof URLClassLoader)
		{
			urls = ((URLClassLoader)cl).getURLs();
		}
		
		Class<?>[] cmds = SReflect.scanForClasses(urls, cl, new IFilter()
		{
			public boolean filter(Object obj)
			{
				boolean ret = false;
				String name =null;
				if(obj instanceof File)
				{
					name = ((File)obj).getName();
				}
				else if(obj instanceof String)
				{
					name = ((String)obj);
				}
				
				if(name!=null)
				{
					ret = name.endsWith(".class") && name.indexOf("Command")!=-1;
				}
				
				return ret;
			}
		}, 
		new IFilter<Class<?>>()
		{
			public boolean filter(Class<?> cl)
			{
				return !cl.isInterface() && !Modifier.isAbstract(cl.getModifiers()) 
					&& ICliCommand.class.isAssignableFrom(cl);
			}
		});
		
		for(int i=0; i<cmds.length; i++)
		{
			try
			{
				ICliCommand cmd = (ICliCommand)cmds[i].newInstance();
				addCommand(cmd);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Add a command.
	 *  @param cmd The command.
	 */
	public void addCommand(ICliCommand cmd)
	{
		String[] names = cmd.getNames();
		for(String name: names)
		{
			if(commands.containsKey(name))
				throw new RuntimeException("Command name already regsisterd: "+name);
			commands.put(name, cmd);
		}
	}
	
	/**
	 *  Get the commands.
	 *  @return The commands.
	 */
	public Map<String, ICliCommand> getCommands()
	{
		return commands;
	}

	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(String line)//, Object context)
	{
		final Future<String> ret = new Future<String>();
		
//		CliContext ccontext = new CliContext(this, context);
		
		// Split the command line to parts
		String[] parts = SUtil.splitCommandline(line);
		for(int i=0; i<parts.length; i++)
		{
			parts[i] = parts[i].trim();
		}
		
		// Invoke a command
		boolean exe = false;
		if(parts!=null && parts.length>0)
		{
			// Fetch command
			String cmdstr = parts[0].trim();
			if(cmdstr.startsWith("-"))
			{
				cmdstr = cmdstr.substring(1);
			}
			ICliCommand cmd = commands.get(cmdstr);
		
			if(cmd!=null)
			{
				String[] nargs = null;
				if(parts.length>1)
				{
					nargs = new String[parts.length-1];
					System.arraycopy(parts, 1, nargs, 0, parts.length-1);
				}
				cmd.invokeCommand(context, nargs).addResultListener(new DelegationResultListener<String>(ret));
				exe = true;
			}
		}
		
		if(!exe)
		{
			ret.setException(new RuntimeException("Command not found: "+line));
		}
		
		return ret;
	}
}
