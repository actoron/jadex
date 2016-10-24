package jadex.bdiv3.quickstart.treasurehunt;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.quickstart.treasurehunt.environment.TreasureHunterEnvironment;
import jadex.micro.annotation.Agent;

/**
 *  Basic treasure hunter agent with just the environment.
 */
@Agent
public class TreasureHunterA1BDI
{
	//-------- beliefs --------
	
	/** The treasure hunter world object. */
	@Belief
	protected TreasureHunterEnvironment	env	= new TreasureHunterEnvironment(800, 600);
}
