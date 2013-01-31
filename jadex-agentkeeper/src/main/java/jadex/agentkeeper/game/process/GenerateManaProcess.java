package jadex.agentkeeper.game.process;

import jadex.agentkeeper.game.state.player.SimplePlayerState;
import jadex.agentkeeper.util.ISpaceStrings;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Grid2D;


/**
 * Simple Space Process who is responsible for generating the Players Mana based
 * on his claimed Tiles.
 * 
 * @author Philip Willuweit
 */
public class GenerateManaProcess extends SimplePropertyObject implements ISpaceProcess
{

	private SimplePlayerState	playerState;

	private Grid2D				environment;

	/** Current time stamp */
	private long				timestamp;

	/** The time that has passed according to the environment executor. */
	private long				progress;

	private double				onePerSecondDelta;

	public void start(IClockService clock, IEnvironmentSpace space)
	{
		this.environment = (Grid2D)space;
		this.playerState = (SimplePlayerState)environment.getProperty(ISpaceStrings.PLAYER_STATE);
		this.timestamp = clock.getTime();


	}

	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		updateProgress(clock);

		updateMana();


	}


	private void updateMana()
	{

		playerState.setMana((playerState.getMana() + (onePerSecondDelta * 0.1f * playerState.getClaimedSectors())));
	}

	private void updateProgress(IClockService clock)
	{
		long currenttime = clock.getTime();
		this.progress = currenttime - timestamp;
		this.timestamp = currenttime;
		onePerSecondDelta = progress * 0.001;

	}

	public void shutdown(IEnvironmentSpace space)
	{


	}


}
