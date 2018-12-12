package jadex.bdi.examples.blackjack;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestDraw of blackjack_beans ontology.
 */
public class RequestDraw implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot cards. */
	protected List cards;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestDraw</code>.
	 */
	public RequestDraw()
	{
		this.cards = new ArrayList();
	}

	//-------- accessor methods --------

	/**
	 *  Get the cards of this RequestDraw.
	 * @return cards
	 */
	public Card[] getCards()
	{
		return (Card[])cards.toArray(new Card[cards.size()]);
	}

	/**
	 *  Set the cards of this RequestDraw.
	 * @param cards the value to be set
	 */
	public void setCards(Card[] cards)
	{
		this.cards.clear();
		for(int i = 0; i < cards.length; i++)
			this.cards.add(cards[i]);
	}

	/**
	 *  Get an cards of this RequestDraw.
	 *  @param idx The index.
	 *  @return cards
	 */
	public Card getCard(int idx)
	{
		return (Card)this.cards.get(idx);
	}

	/**
	 *  Set a card to this RequestDraw.
	 *  @param idx The index.
	 *  @param card a value to be added
	 */
	public void setCard(int idx, Card card)
	{
		this.cards.set(idx, card);
	}

	/**
	 *  Add a card to this RequestDraw.
	 *  @param card a value to be removed
	 */
	public void addCard(Card card)
	{
		this.cards.add(card);
	}

	/**
	 *  Remove a card from this RequestDraw.
	 *  @param card a value to be removed
	 *  @return  True when the cards have changed.
	 */
	public boolean removeCard(Card card)
	{
		return this.cards.remove(card);
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this RequestDraw.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestDraw(" + ")";
	}

}
