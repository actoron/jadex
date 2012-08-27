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
import jadex.commons.transformation.IObjectStringConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
		commands.put("-lp", new ACliCommand()
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
			
			/**
			 *
			 */
			public ResultInfo getResultInfo()
			{
				return new ResultInfo(Collection.class, "desc", new IObjectStringConverter()
				{
					public String convertObject(Object val, Object context)
					{
						StringBuffer buf = new StringBuffer();
						Collection<DiscoveryInfo> res = (Collection<DiscoveryInfo>)val;
						if(res!=null)
						{
							Iterator<DiscoveryInfo> it = res.iterator();
							for(int i=0; it.hasNext(); i++)
							{
								buf.append("(").append(i).append(") ").append(it.next().getComponentIdentifier());
							}
						}
						return buf.toString();
					}
				});
			}
		});
	}
	
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(String line, Object context)
	{
		final Future<String> ret = new Future<String>();
		
		// Split the command line to parts
		String[] parts = SUtil.splitCommandline(line);
		
		// Invoke a command
		boolean exe = false;
		if(parts!=null && parts.length>0)
		{
			// Fetch command
			ICliCommand cmd = commands.get(parts[0]);
		
			if(cmd!=null)
			{
				cmd.invokeCommand(context, null).addResultListener(new DelegationResultListener<String>(ret));
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
