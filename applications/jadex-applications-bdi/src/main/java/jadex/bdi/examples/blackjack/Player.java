package jadex.bdi.examples.blackjack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jadex.bdi.examples.blackjack.player.strategies.AbstractStrategy;
import jadex.bdi.examples.blackjack.player.strategies.IStrategy;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;


/**
 *  Representation of a player.
 */
public class Player
{
	//-------- constants --------

	/** State of a player that is not involved in a game. */
	public static final String STATE_UNREGISTERED = "unregistered";

	/** State of a player that is not involved in a game. */
	public static final String STATE_IDLE = "idle";

	/** State of a player at game start. */
	public static final String STATE_GAME_STARTED = "game started";

	/** State of a player, after bet is made. */
	public static final String STATE_PLAYING = "playing";

	/** State of a player when all cards are drawn. */
	public static final String STATE_FINISHED = "finished";

	//-------- attributes --------

	/** The player's name. */
	protected String name;

	/** The player's account. */
	protected int account;

	/** The player's color. */
	protected Color color;

	/** Hack, necessary because in Java Color is not a bean. */
	protected Integer colorvalue;

	/** The strategyname. */
	protected String strategyname;

	/** The player's strategy. */
	protected IStrategy strategy;

	/** The player's agent id. */
	protected IComponentIdentifier aid;

	/** The player state. */
	protected String state;

	/** The player's current bet. */
	protected int bet;

	/** The player's decision if to draw another card. */
	protected boolean drawcard;

	/** The cards held by the player. */
	protected List cards;

	/** The game counter (incremented when a game is started). */
	protected int games;

	/** The helper object for bean events. */
	protected SimplePropertyChangeSupport pcs;

	//-------- constructors --------

	/**
	 *  Empty bean constructor. 
	 */
	public Player()
	{
		this(null, 0, null, null);
	}

	/**
	 *  Create a new Player.
	 */
	public Player(String name, int account, Color color, String strategyname)
	{
		this(null, name, account, color, strategyname);
	}

	/**
	 *  Create a new Player.
	 */
	public Player(IComponentIdentifier aid, String name, int account, Color color, String strategyname)
	{
		this.aid = aid;
		this.name = name;
		this.account = account;
		this.color = color;
		this.strategyname = strategyname;
		//if(strategyname!=null)
		//	this.strategy	= AbstractStrategy.getStrategy(strategyname);
		this.state = STATE_UNREGISTERED;
		this.pcs = new SimplePropertyChangeSupport(this);
	}

	//-------- attributes --------

	/**
	 *  Get the name of the player.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name of the player.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the account of the player.
	 */
	public int getAccount()
	{
		return account;
	}

	/**
	 *  Set the account of the player.
	 */
	public void setAccount(int account)
	{
		this.account = account;
	}

	/**
	 *  Get the color of the player.
	 */
	public Color getColor()
	{
		if(color == null)
			color = new Color(colorvalue.intValue());
		return color;
	}

	/**
	 *  Set the color of the player.
	 * /
	public void setColor(Color color)
	{
		this.color = color;
		if(color != null)
			colorvalue = color.getRGB();
	}*/

	/**
	 *  Get the color value.
	 *  @return The color value.
	 */
	public int getColorValue()
	{
		return colorvalue!=null? colorvalue.intValue(): color.getRGB();
	}

	/**
	 *  Set the color value.
	 *  @param colorvalue	The color value.
	 */
	public void setColorValue(int colorvalue)
	{
		this.color	= null;
		this.colorvalue = Integer.valueOf(colorvalue);
	}

	/**
	 *  Get the strategy of the player.
	 */
	public IStrategy getStrategy()
	{
		if(strategy == null && strategyname != null)
			strategy = AbstractStrategy.getStrategy(strategyname);
		return strategy;
	}

	/**
	 *  Set the strategy of the player.
	 * /
	public void setStrategy(IStrategy strategy)
	{
		this.strategy = strategy;
	}*/

	/**
	 *  Get the strategy name.
	 *  @return The strategy name.
	 */
	public String getStrategyName()
	{
		return strategyname;
	}

	/**
	 *  Set the strategy name.
	 *  @param strategyname The strategy name.
	 */
	public void setStrategyName(String strategyname)
	{
		this.strategyname = strategyname;
	}

	/**
	 *  Get the aid of the player.
	 */
	public IComponentIdentifier getAgentID()
	{
		return aid;
	}

	/**
	 *  Set the aid of the player.
	 */
	public void setAgentID(IComponentIdentifier aid)
	{
		this.aid = aid;
	}

	/**
	 *  Add a card.
	 */
	public void addCard(Card card)
	{
		if(cards == null)
			this.cards = new ArrayList();
		cards.add(card);
		//		System.out.println("new_card");
		//pcs.firePropertyChange("new_card", null, card);
		pcs.firePropertyChange("cards", null, cards);
		pcs.firePropertyChange("cardCnt", Integer.valueOf(cards.size()-1), Integer.valueOf(cards.size()));
	}

	/**
	 *  Get the cards held by the player.
	 */
	public Card[] getCards()
	{
		return cards == null ? new Card[0] : (Card[])cards.toArray(new Card[cards.size()]);
	}

	/**
	 *  Get the cards held by the player.
	 */
	public void setCards(Card[] cards)
	{
		this.cards = SUtil.arrayToList(cards);
		//		System.out.println("new_cards");
		//pcs.firePropertyChange("new_cards", null, cards);
		pcs.firePropertyChange("cards", null, cards);
		pcs.firePropertyChange("cardCnt", Integer.valueOf(cards.length-1), Integer.valueOf(cards.length));
	}

	/**
	 *  Get a card.
	 */
	public Card getCard(int idx)
	{
		return (Card)cards.get(idx);
	}

	/**
	 *  Get a card.
	 */
	public void setCard(int idx, Card card)
	{
		cards.add(idx, card);
	}
	
	/**
	 *  Get the number of cards.
	 */
	public int getCardCnt()
	{
		return cards == null? 0: cards.size();
	}
	
	/**
	 *  Get the state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 */
	public void setState(String state)
	{
		this.state = state;
		if(state.equals(STATE_GAME_STARTED))
		{
			this.games++;
			setCards(new Card[0]);
			setBet(0);
		}
		if(state.equals(STATE_IDLE))
		{
			setCards(new Card[0]);
			setBet(0);
		}
		//		System.out.println("new_state");
		pcs.firePropertyChange("playingstate", null, state);
	}

	/**
	 *  Get the bet.
	 */
	public int getBet()
	{
		return bet;
	}

	/**
	 *  Set the bet.
	 */
	public void setBet(int bet)
	{
		this.bet = bet;
		//		System.out.println("new_bet");
		pcs.firePropertyChange("bet", null, Integer.valueOf(bet));
	}

	/**
	 *  Make a bet.
	 */
	public void makeBet(int bet)
	{
		if(bet > account)
			throw new RuntimeException("Insufficient cover for bet " + bet + ": " + this);
		this.account -= bet;
		this.bet = bet;
		//		System.out.println("new_bet");
		pcs.firePropertyChange("bet", null, Integer.valueOf(bet));
	}

	/**
	 *  Get the game counter.
	 */
	public int getGameCount()
	{
		return games;
	}

	/**
	 *  Get the money won in the current game.
	 */
	public int getMoneyWon(Card[] dealercards)
	{
		int moneywon;
		int dealerval = CardSet.calculateDeckValue(dealercards);
		int playerval = CardSet.calculateDeckValue(getCards());

		// there's missing something here ... :-(
		// check if the dealer has blackjack (== 21 points and only two cards)
		// sorry, will be implemented in the next release

		if((playerval <= 21) && ((playerval >= dealerval) || (dealerval > 21)))
		{
			if(playerval == dealerval)
				moneywon = this.bet; // draw
			else
				moneywon = this.bet * 2; // player wins
		}
		else
		{
			moneywon = 0; // player lost
		}

		return moneywon;
	}

	/**
	 *  Test if this player equals another object.
	 */
	public boolean equals(Object o)
	{
		//return o==this || o instanceof Player && (this.aid!=null && this.aid.equals(((Player)o).getAgentID())
		//	|| this.name.equals(((Player)o).getName()));
		return o == this || o instanceof Player && this.name.equals(((Player)o).getName());
	}

	/**
	 *  Get the hash code of this player.
	 */
	public int hashCode()
	{
		// Use hash code of aid, if available.
		//return aid!=null ? aid.hashCode() : name.hashCode();
		return name.hashCode();
	}

	public String toString()
	{
		return "Player(name=" + name + ", state=" + state + ", cards=" + cards + ", money=" + account + ")";
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

	//-------- static part --------

	/*public static String	color2Hex(Color color)
	{
		String	hex	= Integer.toHexString(0xFFFFFF & color.getRGB()).toUpperCase();
		return "#" + "000000".substring(hex.length()) + hex;
	}*/
}
