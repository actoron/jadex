package jadex.platform.service.cli.commands;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.NullOutputStream;
import jadex.commons.StreamCopy;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Execute a file.
 */
public class ExecuteFileCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"ef", "exec", "run"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Execute a file.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "ef jadex.bat : execute the jadex.bat file";
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
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		SServiceProvider.searchService(comp, new ServiceQuery<>( IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IDaemonThreadPoolService, String>(ret)
		{
			public void customResultAvailable(IDaemonThreadPoolService tp)
			{
				try
				{
					File cwd = new File(context.getShell().getWorkingDir()).getCanonicalFile();
				
					if(dir!=null)
					{
						File f = new File(cwd, dir);
						if(f.exists() && !f.isDirectory())
						{
							executeFile(f, tp);
							ret.setResult(cwd.getCanonicalPath());
						}
						else
						{
							f = new File(dir);
							if(f.exists() && !f.isDirectory())
							{
								executeFile(f, tp);
								ret.setResult(cwd.getCanonicalPath());
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
			}
		});
		
		return ret;
	}
	
	/**
	 *  Execute a file.
	 */
	protected void executeFile(File f, IThreadPool tp) throws IOException
	{
		Process proc = Runtime.getRuntime().exec(f.getCanonicalPath());
		tp.execute(new StreamCopy(proc.getInputStream(), new NullOutputStream())); // the input is the output stream :-(
		tp.execute(new StreamCopy(proc.getErrorStream(), new NullOutputStream()));
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		// todo: support command line option for e.g. bin/sh cmd
		
		ArgumentInfo dir = new ArgumentInfo(null, String.class, null, "The file or directory to delete.", null);
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
