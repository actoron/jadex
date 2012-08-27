package jadex.platform.service.cli;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cli.ICliService;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;


/**
 * 
 */
@Agent
@Service
public class CliAgent implements ICliService
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The command line. */
	protected CliPlatform clip; 
	
	/**
	 *  Execute a command line command and
	 *  get back the results.
	 *  @param command The command.
	 *  @return The result of the command.
	 */
	public IFuture<String> executeCommand(String line, Object context)
	{
		return clip.executeCommand(line, context);
	}

}
