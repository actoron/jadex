package jadex.platform.service.cli;

import io.airlift.command.Arguments;
import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Command;
import io.airlift.command.Help;
import io.airlift.command.Option;
import io.airlift.command.OptionType;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 */
public class CliPlatform implements ICliService
{
	/** The commands. */
	protected Map<String, ICliCommand> commands;
	
	/**
	 * 
	 */
	public CliPlatform()
	{
		commands = new HashMap<String, ICliCommand>();
		commands.put("-lp", new ICliCommand()
		{
			public Object invokeCommand(Object context, Object[] args)
			{
				final Future<Collection<DiscoveryInfo>> ret = new Future<Collection<DiscoveryInfo>>();
				IExternalAccess comp = (IExternalAccess)context;
				SServiceProvider.getService(comp.getServiceProvider(), IAwarenessManagementService.class)
					.addResultListener(new ExceptionDelegationResultListener<IAwarenessManagementService, Collection<DiscoveryInfo>>(ret)
				{
					public void customResultAvailable(IAwarenessManagementService awas)
					{
						awas.getKnownPlatforms().addResultListener(new DelegationResultListener<Collection<DiscoveryInfo>>(ret));
					}
				});
				return ret;
			}
			
			public Class<?>[] getArgumentTypes()
			{
				return null;
			}
		});
	}
	
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(Object context, String line)
	{
		final Future<String> ret = new Future<String>();
		
		// Split the command line to parts
		String[] parts = SUtil.splitCommandline(line);
		
		// Invoke a command
		if(parts!=null && parts.length>0)
		{
			// Fetch command
			ICliCommand cmd = commands.get(parts[0]);
		
			if(cmd!=null)
			{
				// Convert arguments
				Class<?>[] argtypes = cmd.getArgumentTypes();
				
				Object[] args = null;
				if(argtypes!=null && argtypes.length>0)
				{
					args = new Object[argtypes.length];
					for(int i=0; i<argtypes.length; i++)
					{
						// todo: convert
					}
				}
	
				// Invoke command
				Object res = cmd.invokeCommand(context, args);
			
				// Result conversion
				if(res instanceof IFuture)
				{
					IFuture<String> fut = (IFuture<String>)res;
					fut.addResultListener(new Dele)
				}
			}
		}
		
		return ret;
	}
	
	
	
	
	public static void main(String[] args)
    {
		args = new String[]{"git", "add", "-p", "file"};
		
        CliBuilder<Runnable> builder = Cli.buildCli("git", Runnable.class)
                .withDescription("the stupid content tracker")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class,
                        Add.class);

        builder.withGroup("remote")
                .withDescription("Manage set of tracked repositories")
                .withDefaultCommand(RemoteShow.class)
                .withCommands(RemoteShow.class,
                        RemoteAdd.class);

        Cli<Runnable> gitParser = builder.build();

        gitParser.parse(args).run();
    }

    public static class GitCommand implements Runnable
    {
        @Option(type = OptionType.GLOBAL, name = "-v", description = "Verbose mode")
        public boolean verbose;

        public void run()
        {
            System.out.println(getClass().getSimpleName());
        }
    }

    @Command(name = "add", description = "Add file contents to the index")
    public static class Add extends GitCommand
    {
        @Arguments(description = "Patterns of files to be added")
        public List<String> patterns;

        @Option(name = "-i", description = "Add modified contents interactively.")
        public boolean interactive;
    }

    @Command(name = "show", description = "Gives some information about the remote <name>")
    public static class RemoteShow extends GitCommand
    {
        @Option(name = "-n", description = "Do not query remote heads")
        public boolean noQuery;

        @Arguments(description = "Remote to show")
        public String remote;
    }

    @Command(name = "add", description = "Adds a remote")
    public static class RemoteAdd extends GitCommand
    {
        @Option(name = "-t", description = "Track only a specific branch")
        public String branch;

        @Arguments(description = "Remote repository to add")
        public List<String> remote;
    }
}
