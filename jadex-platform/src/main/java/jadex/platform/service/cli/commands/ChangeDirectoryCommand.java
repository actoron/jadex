/**
 * 
 */
package jadex.platform.service.cli.commands;

import jadex.commons.future.Future;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class ChangeDirectoryCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"cd", "changedir"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Change current directory.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "cd temp : change the directory to temp";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<String> ret = new Future<String>();
		
		final String dir = (String)args.get(null);
		
		try
		{
			File cwd = new File(context.getShell().getWorkingDir()).getCanonicalFile();
		
			if(dir==null)
			{
				String cwdp = cwd.getCanonicalPath();
				ret.setResult(cwdp);
			}
			else if(dir.trim().equals(".."))
			{
				File p = cwd.getParentFile();
				if(p!=null)
				{
					context.getShell().setWorkingDir(p.getCanonicalPath());
					ret.setResult(p.getCanonicalPath());
				}
				else
				{
					String cwdp = cwd.getCanonicalPath();
					ret.setResult(cwdp);
				}
			}
			else
			{
				File nd = new File(cwd, dir);
				if(nd.exists() && nd.isDirectory())
				{
					context.getShell().setWorkingDir(nd.getCanonicalPath());
					ret.setResult(nd.getCanonicalPath());
				}
				else
				{
					nd = new File(dir);
					if(nd.exists() && nd.isDirectory())
					{
						context.getShell().setWorkingDir(nd.getCanonicalPath());
						ret.setResult(nd.getCanonicalPath());
					}
					else
					{
						ret.setException(new RuntimeException("path not found."));
					}
				}
			}
		}
		catch(IOException e)
		{
			ret.setException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo dir = new ArgumentInfo(null, String.class, null, "The directory.", null);
		return new ArgumentInfo[]{dir};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, final Map<String, Object> args)
	{
		return new ResultInfo(String.class, "The current working directory.", null);
	}
}
