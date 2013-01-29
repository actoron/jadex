package jadex.agentkeeper.game.state.player;

/**
 * Just a first pre-implementation of the Player State, mainly for the GUI
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class SimplePlayerState
{
	private int playerId;
	
	private int claimedSectors = 0;
	
	private double mana;
	
	private int gold;
	
	public SimplePlayerState(int playerId)
	{
		this.playerId = playerId;
	}

	public int getPlayerId()
	{
		return playerId;
	}

	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}
	
	public void addClaimedSector()
	{
		this.claimedSectors++;
	}

	public int getClaimedSectors()
	{
		return claimedSectors;
	}

	public void setClaimedSectors(int claimedSectors)
	{
		this.claimedSectors = claimedSectors;
	}


	public int getGold()
	{
		return gold;
	}

	public void setGold(int gold)
	{
		this.gold = gold;
	}

	public double getMana()
	{
		return mana;
	}

	public void setMana(double mana)
	{
		this.mana = mana;
	}

}
