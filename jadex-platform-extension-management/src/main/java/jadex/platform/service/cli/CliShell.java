package jadex.platform.service.cli;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  The cli shell contains the commands and
 *  allows for executing a command.
 *  
 *  Use addAllCommandsFromClassPath() to add
 *  all commands from the current classpath or
 *  addCommand() to add them manually.
 */
public class CliShell extends ACliShell 
{
	//-------- attributes --------
	
	/** The commands. */
	protected Map<String, ICliCommand> commands;
	
	/** The context. */
	protected CliContext context;
	
	/** The subshells. */
	protected ACliShell subshell;
	
	/** The prompt. */
	protected String prompt;
	
	// todo: use rid?
	/** The class loader. */
	protected ClassLoader cl;
	
	//-------- constructors --------
	
	/**
	 *  Create a new cli.
	 */
	public CliShell(Object context, String prompt, Tuple2<String, Integer> sessionid, ClassLoader cl)
	{
		super(sessionid);
		this.commands = new LinkedHashMap<String, ICliCommand>();
		this.context = new CliContext(this, context);
		this.prompt = prompt;
		this.cl = cl;
	}
	
	//-------- methods --------
	
	/**
	 *  Add all commands from classpath.
	 *  @param cl The classloader to use.
	 */
	public IFuture<Void> addAllCommandsFromClassPath()//ClassLoader cl)
	{
		IFuture<Void> ret;
		
		if(subshell!=null)
		{
			ret = subshell.addAllCommandsFromClassPath();
		}
		else
		{
			//URL[] urls = null;
//			ClassLoader cl = getClass().getClassLoader();
//			if(cl instanceof URLClassLoader)
//			{
//				urls = ((URLClassLoader)cl).getURLs();
//			}
			Class<?>[] cmds = SReflect.scanForClasses(cl, new IFilter()
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
					else if(obj instanceof JarEntry)
					{
						name = ((JarEntry) obj).getName();
					}
					
					if(name!=null)
					{
						// Avoid loading e.g. CommanderBDI...
//						ret = name.endsWith(".class") && name.indexOf("Command")!=-1;
						ret = name.endsWith("Command.class");
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
			}, false);
			
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
			
			ret = IFuture.DONE;
		}
		return ret;
	}
	
	/**
	 *  Add a command.
	 *  @param cmd The command.
	 */
	public IFuture<Void> addCommand(ICliCommand cmd)
	{
		IFuture<Void> ret;
		
		if(subshell!=null)
		{
			ret = subshell.addCommand(cmd);
		}
		else
		{
			String[] names = cmd.getNames();
			for(String name: names)
			{
				if(commands.containsKey(name))
					throw new RuntimeException("Command name already regsisterd: "+name);
				commands.put(name, cmd);
			}
			ret = IFuture.DONE;
		}
		
		return ret;
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
	public IFuture<String> executeCommand(String line)
	{
		final Future<String> ret = new Future<String>();
		
		if(subshell!=null)
		{
			subshell.executeCommand(line).addResultListener(new DelegationResultListener<String>(ret)
			{
				public void exceptionOccurred(final Exception exception)
				{
					if(exception instanceof CloseShellException)
					{
						removeSubshell().addResultListener(new ExceptionDelegationResultListener<Boolean, String>(ret)
						{
							public void customResultAvailable(Boolean result)
							{
								if(result.booleanValue())
								{
									ret.setResult(null);
								}
								else
								{
									super.exceptionOccurred(exception);
								}
							}
						});
					}
					else
					{
						super.exceptionOccurred(exception);
					}
				}
			});
		}
		else
		{
			return doExecuteCommand(line);
		}
		
		return ret;
	}
	
	/**
	 *  Do execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> doExecuteCommand(String line)
	{
		final Future<String> ret = new Future<String>();
		
//		CliContext ccontext = new CliContext(this, context);
						
		// Split the command line to parts
		String[] parts = SUtil.splitCommandline(line);
		for(int i=0; parts!=null && i<parts.length; i++)
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
	
	/**
	 *  Add a subshell.
	 */
	public void addSubshell(ACliShell subshell)
	{
		if(this.subshell==null)
		{
			this.subshell = subshell;
		}
		else
		{
			this.subshell.addSubshell(subshell);
		}
	}
	
	/**
	 *  Remove a subshell.
	 */
	public IFuture<Boolean> removeSubshell()
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		if(subshell!=null)
		{
			subshell.removeSubshell().addResultListener(new DelegationResultListener<Boolean>(ret)
			{
				public void customResultAvailable(Boolean result)
				{
					if(result.booleanValue())
					{
						ret.setResult(result);
					}
					else
					{
						subshell = null;
						ret.setResult(Boolean.TRUE);
					}
				}
			});
		}
		else
		{
			ret.setResult(Boolean.FALSE);
		}
		
		return ret;
	}
	
	/**
	 *  Get the complete prompt (internal method).
	 *  Calls subshells getPrompt().
	 *  @return the complete prompt;
	 */
	public IFuture<String> internalGetShellPrompt()
	{
		final Future<String> ret = new Future<String>();
		
		if(subshell!=null)
		{
			subshell.internalGetShellPrompt().addResultListener(new DelegationResultListener<String>(ret)
			{
				public void customResultAvailable(String result)
				{
					ret.setResult(getPrompt()+"/"+result);
				}
			});
		}
		else
		{
			ret.setResult(getPrompt());
		}
		
		return ret;
	}
	
	/**
	 *  Get the promt only for this shell part.
	 *  @return The local promt part.
	 */
	public String getPrompt()
	{
		return prompt==null? "prompt": prompt;
	}
}
