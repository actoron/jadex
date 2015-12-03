package jadex.bdi.examples.blackjack.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jadex.bdi.examples.blackjack.GameState;
import jadex.bdi.examples.blackjack.Player;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.collection.SCollection;

/**
 *	This class represents the JPanel of one player as it is shown in the GUI.
 */
public class GameStateFrame extends JFrame
{
	/** The dealers panel. */
	protected PlayersPanel dealerpan;

	/** The control panel. */
	protected JPanel controlpanel;

	/** The players panel. */
	protected PlayersPanel playerpan;

	/** The top panel. */
	protected JPanel top;

	//-------- constructors --------

	/**
	 *  Create a new game state panel.
	 */
	public GameStateFrame(final GameState gamestate, JPanel controlpan)
	{
		getContentPane().setLayout(new GridBagLayout());

		dealerpan = new PlayersPanel();
		dealerpan.setLayout(new GridLayout(1,1));
		playerpan = new PlayersPanel();
		playerpan.setLayout(new GridLayout(0, 2));

		top = new JPanel(new GridLayout(1,2));
		top.add(dealerpan);
		setControlPanel(controlpan);

		getContentPane().add(top, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(0,0,0,0), 0, 0));
		getContentPane().add(playerpan, new GridBagConstraints(0, 1, 1, 1, 3, 3, GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(0,0,0,0), 0, 0));

		setGameState(gamestate);
	}

	/**
	 *  Set the game state.
	 *  @param gamestate The gamestate.
	 */
	public void setGameState(final GameState gamestate)
	{
		if(gamestate!=null)
		{
			gamestate.addPropertyChangeListener(new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					updatePanel(gamestate);
				}
			});
			updatePanel(gamestate);
		}
		// todo: remove old
	}

	/**
	 *
	 */
	protected void updatePanel(GameState gamestate)
	{
		if(dealerpan.getPlayers().size()==0 && gamestate.getDealer()!=null)
			dealerpan.addPlayer(gamestate.getDealer());
		else if(gamestate.getDealer()!=null && !dealerpan.getPlayers().contains(gamestate.getDealer()))
			dealerpan.replacePlayer((Player)dealerpan.getPlayers().get(0), gamestate.getDealer());
		else if(gamestate.getDealer()==null && dealerpan.getPlayers().size()>0)
			dealerpan.removePlayer((Player)dealerpan.getPlayers().get(0));

		java.util.List curplayers = playerpan.getPlayers();
		Player[] players = gamestate.getPlayers();
		for(int i=0; i<players.length; i++)
		{
			if(!curplayers.remove(players[i]))
			{
				playerpan.addPlayer(players[i]);
			}
		}
		for(int i=0; i<curplayers.size(); i++)
		{
			playerpan.removePlayer((Player)curplayers.get(i));
		}
	}

	/**
	 *  Get the control panel. 
	 *  @return The control panel.
	 */
	public JPanel getControlPanel()
	{
		return controlpanel;
	}

	/**
	 *
	 */
	public void setControlPanel(JPanel control)
	{
		// todo: remove old
		if(control!=null)
		{
			this.controlpanel = control;
			top.add(control);
			pack();
		}
	}

	//-------- methods --------

	/**
	 *  The players panel for multiple players.
	 */
	public class PlayersPanel extends JPanel
	{
		/** The actual players (because invokeLater() may cause delays). */
		protected java.util.ArrayList players;

		/**
		 *  Create a new players panel.
		 */
		public PlayersPanel()
		{
			players = SCollection.createArrayList();
		}

		/**
		 *  Get the list of all players.
		 */
		public java.util.List getPlayers()
		{
			return (java.util.List)players.clone();
		}

		/**
		 *  Called when a player was added.
		 */
		public void	addPlayer(final Player player)
		{
			players.add(player);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					add(new PlayerPanel(player));
					GameStateFrame.this.pack();
					GameStateFrame.this.repaint();
				}
			});
		}

		/**
		 *  Called when a player was removed.
		 */
		public void	removePlayer(final Player player)
		{
			players.remove(player);
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Component[]	panels	= getComponents();
					for(int i=0; i<panels.length; i++)
					{
						if(panels[i] instanceof PlayerPanel && ((PlayerPanel)panels[i]).getPlayer().equals(player))
						{
							remove(panels[i]);
							GameStateFrame.this.pack();
							GameStateFrame.this.repaint();
							break;
						}
					}
				}
			});
		}

		/**
		 *  Replace a player.
		 */
		public void	replacePlayer(final Player oldplayer, final Player newplayer)
		{
			if(!players.remove(oldplayer))
				throw new RuntimeException("Could not remove player: "+oldplayer);
			players.add(newplayer);

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Component[]	panels	= getComponents();
					for(int i=0; i<panels.length; i++)
					{
						if(panels[i] instanceof PlayerPanel && ((PlayerPanel)panels[i]).getPlayer().equals(oldplayer))
						{
							remove(i);
							add(new PlayerPanel(newplayer), i);
							GameStateFrame.this.pack();
							GameStateFrame.this.repaint();
							break;
						}
					}
				}
			});
		}
	}

}