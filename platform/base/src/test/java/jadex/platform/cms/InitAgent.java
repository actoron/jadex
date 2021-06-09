package jadex.platform.cms;

import java.util.function.Consumer;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;

/**
 *  An agent that provides access to its init and start behavoir.
 */
@Agent
public class InitAgent
{
	protected Consumer<IInternalAccess>	init;
	protected Consumer<IInternalAccess>	start;
	
	/**
	 *  Create an init agent and supply the init and start behavior.
	 */
	public InitAgent(Consumer<IInternalAccess> init, Consumer<IInternalAccess> start)
	{
		this.init	= init;
		this.start	= start;
	}
	
	@OnInit
	protected void init(IInternalAccess ia)
	{
		if(init!=null)
			init.accept(ia);
	}

	@OnStart
	protected void start(IInternalAccess ia)
	{
		if(start!=null)
			start.accept(ia);
	}
}
