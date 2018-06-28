package jadex.bdi.examples.blackjack.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jadex.bdi.examples.blackjack.Card;
import jadex.bdi.examples.blackjack.CardSet;
import jadex.bdi.examples.blackjack.Player;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;

/**
 *	This class represents the JPanel of one player as it is shown in the GUI.
 */
public class PlayerPanel extends JPanel
{
	//-------- attributes --------

	/** The player object. */
	protected Player	player;
	
	/** Label containing the player's state. */
	protected JLabel	state;
	
	/** Label containing the player's account. */
	protected JLabel	account;

	/** Panel containing card images. */
	protected JPanel	cardpanel;
	
	/** Label containing the player's bet. */
	protected JLabel	bet;
		
	/** Label containing the player's strategy name. */
	protected JLabel	strategy;

	//-------- constructors --------

	/**
	 *  Create a new playerPanel.
	 *  A playerPanel contains all neccessary Information about the player
	 *  @param player The player
	 */
	public PlayerPanel(Player player)
	{
		super(new BorderLayout());
		this.player	= player;
		this.setBorder(BorderFactory.createTitledBorder(" Player " + player.getName() + " "));
		this.setBackground(Color.WHITE);

		JPanel	stateaccountpanel = new JPanel(new GridLayout(1,2));
		stateaccountpanel.setBackground(new Color(192,192,192));
		this.state		= new JLabel("State: "+player.getState());
		this.account	= new JLabel("Account: "+player.getAccount());
		stateaccountpanel.add(state);
		stateaccountpanel.add(account);

		JPanel betstrategypanel = new JPanel();	// FlowLayout()
		betstrategypanel.setBackground(new Color(192,192,192));
		this.bet		= new JLabel("   Bet: ");
		this.strategy	= new JLabel("   " + (player.getStrategyName()!=null ? player.getStrategyName() : ""));
		betstrategypanel.add(bet);
		betstrategypanel.add(strategy);

		if(player.getColor() != null)
		{
			stateaccountpanel.setBackground(player.getColor());
			betstrategypanel.setBackground(player.getColor());
		}

		this.cardpanel = new JPanel();
		cardpanel.setBorder(BorderFactory.createTitledBorder(" Cards "));
		cardpanel.setBackground(Color.WHITE);
		cardpanel.setLayout(new BoxLayout(cardpanel, BoxLayout.X_AXIS));
		
		this.add(stateaccountpanel, BorderLayout.NORTH);		
		this.add(cardpanel, BorderLayout.CENTER);
		this.add(betstrategypanel, BorderLayout.SOUTH);

		playerChanged();

		player.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						playerChanged();
					}
				});
			}
		});
	}

	//-------- methods --------
	
	/**
	 *	This method is called when a change in the 
	 *	player is detected, it just updates the panel.
	 */
	public void playerChanged()
	{
		// Update state,  bet and account values.
		state.setText("State: "+player.getState());
		bet.setText("   Bet: " + player.getBet());
		account.setText("   Account: " + player.getAccount());

		// Add Player cards.
		Card[]	cards	= player.getCards();
		cardpanel.removeAll();
		cardpanel.setLayout(new BoxLayout(cardpanel, BoxLayout.X_AXIS));
		cardpanel.add(Box.createRigidArea(new Dimension(10, GUIImageLoader.getCardIconHeight())));
		if(cards.length>0)
		{
			for(int i=0; i<cards.length; i++)
			{
				// draw the image of one card
				ImageIcon image = GUIImageLoader.getImage(cards[i].getType()+"_"+cards[i].getColor());
				cardpanel.add(new JLabel(image));
			}
			// calculate the overall-value of the deck
			int deckValue = CardSet.calculateDeckValue(cards);
			JLabel deckValueLabel = new JLabel(" deck value: " + deckValue+" ");
			// more than 21 points means, that the player has lost (draw in red letters)
			if (deckValue > 21)
			{
//				deckValueLabel.setForeground(Color.RED);
				deckValueLabel.setBackground(new Color(255, 192, 128));
				deckValueLabel.setOpaque(true);
			}
			else if(player.getState().equals(Player.STATE_FINISHED))
			{
				deckValueLabel.setBackground(new Color(192, 255, 128));
				deckValueLabel.setOpaque(true);
			}
			cardpanel.add(new JLabel("   "));
			cardpanel.add(deckValueLabel);
			cardpanel.add(Box.createHorizontalGlue());
		}
		else
		{
			cardpanel.add(new JLabel("no cards"));
		}

		validate();
		repaint();
	}

	/**
	 *  Get the player.
	 */
	public Player	getPlayer()
	{
		return player;
	}
}
