package jadex.platform.service.cron.jobs;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.cron.CronJob;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Arrays;
import java.util.Iterator;

/**
 *  Job that allows executing cli command and scripts.
 */
public class CliJob extends CronJob
{
	/**
	 *  Create a new Ccli job. 
	 */
	public CliJob()
	{
	}
	
	/**
	 *  Create a new Ccli job. 
	 */
	public CliJob(IFilter<Long> filter, String com)
	{
		this(filter, new String[]{com});
	}
	
	/**
	 *  Create a new Ccli job. 
	 */
	public CliJob(IFilter<Long> filter, String[] com)
	{
		super(filter, new CliCommand(com));
	}

	/**
	 *  The create command is used to execute a command line or script.
	 */
	public static class CliCommand implements ICommand<Tuple2<IInternalAccess, Long>>
	{
		/** The command. */
		protected String[] commands;
		
		/**
		 *  Create a new cli command. 
		 */
		public CliCommand(String[] commands)
		{
			this.commands = commands;
		}

		/**
		 *  Execute the command.
		 *  @param args The argument(s) for the call.
		 */
		public void execute(final Tuple2<IInternalAccess, Long> args)
		{
			final IInternalAccess ia = args.getFirstEntity();
			SServiceProvider.getService(ia.getServiceContainer(), ICliService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(ia.createResultListener(new DefaultResultListener<ICliService>()
			{
				public void resultAvailable(ICliService clis)
				{
					executeCommands(clis, Arrays.asList(commands).iterator());
				}
			}));
		}
		
		/**
		 *  Execute a number of commands.
		 */
		protected IFuture<Void> executeCommands(final ICliService cliser, final Iterator<String> cmds)
		{
			final Future<Void> ret = new Future<Void>();
			
			if(cmds.hasNext())
			{
				final String cmd = cmds.next();
				
				Tuple2<String, Integer> sess = new Tuple2<String, Integer>(SUtil.createUniqueId("emailsess"), new Integer(0));
				
				cliser.executeCommand(cmd, sess).addResultListener(new IResultListener<String>()
				{
					public void resultAvailable(String result)
					{
						System.out.println("Executing command: "+cmd);
						System.out.println(result);
						executeCommands(cliser, cmds).addResultListener(new DelegationResultListener<Void>(ret));
					}
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Executing command: "+cmd);
						System.out.println(exception);
						executeCommands(cliser, cmds).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
			
			return ret;
		}

		/**
		 *  Get the commands.
		 *  @return The commands.
		 */
		public String[] getCommands()
		{
			return commands;
		}

		/**
		 *  Set the commands.
		 *  @param commands The commands to set.
		 */
		public void setCommands(String[] commands)
		{
			this.commands = commands;
		}
		
	}
}

