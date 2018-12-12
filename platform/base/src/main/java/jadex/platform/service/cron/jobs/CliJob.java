package jadex.platform.service.cron.jobs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.cron.CronJob;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.platform.service.cron.TimePatternFilter;

/**
 *  Job that allows executing cli command and scripts.
 */
public class CliJob extends CronJob<String>
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
	public CliJob(String pattern, String com)
	{
		this(pattern, new TimePatternFilter(pattern), new String[]{com});
	}
	
	/**
	 *  Create a new Ccli job. 
	 */
	public CliJob(String pattern, String[] com)
	{
		super(pattern, new TimePatternFilter(pattern), new CliCommand(com));
	}
	
	/**
	 *  Create a new Ccli job. 
	 */
	public CliJob(String pattern, IFilter<Long> filter, String[] com)
	{
		super(pattern, filter, new CliCommand(com));
	}

	/**
	 *  The create command is used to execute a command line or script.
	 */
	public static class CliCommand implements IResultCommand<ISubscriptionIntermediateFuture<String>, Tuple2<IInternalAccess, Long>>
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
		public ISubscriptionIntermediateFuture<String> execute(final Tuple2<IInternalAccess, Long> args)
		{
			final SubscriptionIntermediateFuture<String> ret = new SubscriptionIntermediateFuture<String>();
//			final Future<String> ret = new Future<String>();
			final IInternalAccess ia = args.getFirstEntity();
			ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ICliService.class, ServiceScope.PLATFORM))
				.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<ICliService>()
			{
				public void resultAvailable(ICliService clis)
				{
					final StringBuffer buf = new StringBuffer();
					executeCommands(clis, Arrays.asList(commands).iterator(), buf).addResultListener(
						new ExceptionDelegationResultListener<Void, Collection<String>>(ret)
					{
						public void customResultAvailable(Void result)
						{
//							ret.setResult(buf.toString());
							ret.addIntermediateResult(buf.toString());
							ret.setFinished();
						}
					});
				}
			}));
			return ret;
		}
		
		/**
		 *  Execute a number of commands.
		 */
		protected IFuture<Void> executeCommands(final ICliService cliser, final Iterator<String> cmds, final StringBuffer buf)
		{
			final Future<Void> ret = new Future<Void>();
			
			if(cmds.hasNext())
			{
				final String cmd = cmds.next();
				
				Tuple2<String, Integer> sess = new Tuple2<String, Integer>(SUtil.createUniqueId("emailsess"), Integer.valueOf(0));
				
				cliser.executeCommand(cmd, sess).addResultListener(new IResultListener<String>()
				{
					public void resultAvailable(String result)
					{
						System.out.println("Executing command: "+cmd);
						System.out.println(result);
						buf.append("Executing command: "+cmd).append(cmd).append(result);
						executeCommands(cliser, cmds, buf).addResultListener(new DelegationResultListener<Void>(ret));
					}
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Executing command: "+cmd);
						System.out.println(exception);
						buf.append("Executing command: "+cmd).append(cmd).append(exception);
						executeCommands(cliser, cmds, buf).addResultListener(new DelegationResultListener<Void>(ret));
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

