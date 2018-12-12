package jadex.bdi.examples.blackjack;

import java.util.Iterator;
import java.util.List;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.Tuple;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.collection.MultiCollection;

/**
 *  This class holds all necessary information, that might be shown
 *  on the StatisticGraph.
 */
public class GameStatistics
{
	/** players result-histories (including dealer) */
	private MultiCollection playerModels;
	
	/** highest round-number, needed to scale the x-axis */
	private int maxRound;
	
	/** highest player-/dealer-account-status, needed to scale the y-axis */
	private int maximum;
	
	/** lowest player-/dealer-account-status, needed to scale the y-axis */
	private int minimum;
	
	/** shall the dealer be considered when calculating minimum and maximum ? */
	private boolean showDealer;
	
	/** The helper object for bean events. */
	public SimplePropertyChangeSupport	pcs;

	/**
	 *  The constructor initialises the class-variables
	 */
	public GameStatistics()
	{
		playerModels = new MultiCollection();
		showDealer = true;
		minimum = 0;
		maximum = 0;
		maxRound = 0;
		this.pcs = new SimplePropertyChangeSupport(this);
	}

	/**
	 *  This method is called by the actionPerformed-method of the
	 *  StatisticGraph-object.
	 */
	public void setShowDealer(boolean show)
	{
		this.showDealer = show;
	}

	/**
	 *  This method is called by the master plan whenever
	 *  a game has finished. The new data is added
	 *  and the graph's minimum and maximum is recalculated.
	 */
	public void addGameRound(Dealer dealer, Player[] players)
	{
		// Add data.
		// Todo: clone player objects instead of using tuples?
		this.playerModels.add(dealer, new Tuple(Integer.valueOf(dealer.getGameCount()), Integer.valueOf(dealer.getAccount())));
		for(int i=0; i<players.length; i++)
		{
			this.playerModels.add(players[i], new Tuple(Integer.valueOf(dealer.getGameCount()), Integer.valueOf(players[i].getAccount())));
		}

		// Recalculates the minimum and maximum-values needed to scale
		// the graphs' axes.
		maxRound = playerModels.getCollection(dealer).size();		
		
		maximum = 0;
		minimum = 0;
		
		// calculate maximum and minimum for the player
		Iterator it = playerModels.keySet().iterator();
		while (it.hasNext())
		{
			Player player = (Player)it.next();

			// only calculate maximum and minimum for the dealer, if the dealer 
			// should be shown on the graph
			if(!(player instanceof Dealer) || showDealer)
			{
				List playerData = (List)playerModels.getCollection(player);
				
				for (int i=0; i < playerData.size(); i++)
				{
					Tuple tup = (Tuple)playerData.get(i);
					int val = ((Integer)tup.get(1)).intValue();
					
					if (val > maximum)
						maximum = val;
					
					if (val < minimum)
						minimum = val;
				}
			}
		} // end of while

		pcs.firePropertyChange("players", null, "data");
	}
	
	/** 
	 *  return all the player-names 
	 */
	public Iterator getPlayers()
	{
		return playerModels.keySet().iterator();
	}

	/** Is statistical data available ?
	  * @return flag indicating if data is available
	  */	
	public boolean isDataAvailable()
	{
		return !playerModels.isEmpty();
	}

	/** 
	  * returns an array with roundNumbers, either of an player
	  * or of the dealer
	  */
	public int[] getXArray(Player player)
	{
		int[] retArray;
		
		List coors = (List)playerModels.getCollection(player);
		retArray = new int[coors.size()];
		
		for (int i=0; i < coors.size(); i++)
		{
			Tuple dummy = (Tuple)coors.get(i);			
			retArray[i] = ((Integer)dummy.get(0)).intValue();			
		}
		return retArray;
	}

	/** 
	  * returns an array with account-stati, either of an player
	  * or of the dealer
	  */
	public int[] getYArray(Player player)
	{
		int[] retArray;
		
		List coors = (List)playerModels.getCollection(player);
		retArray = new int[coors.size()];
		
		for (int i=0; i < coors.size(); i++)
		{
			Tuple dummy = (Tuple)coors.get(i);			
			retArray[i] = ((Integer)dummy.get(1)).intValue();			
			
		}
		return retArray;
	}
	
	/** 
	  * returns maximum roundNumber
	  */	
	public int getMaxRound()
	{
		return maxRound;
	}

	/** 
	  * returns highest-account-status of either a player or the dealer
	  */
	public int getMaximum()
	{
		return maximum;
	}
	
	/** 
	  * returns lowest-account-status of either a player or the dealer
	  */		
	public int getMinimum()
	{
		return minimum;
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }

}