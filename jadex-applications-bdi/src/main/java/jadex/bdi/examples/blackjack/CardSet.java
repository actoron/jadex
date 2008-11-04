package jadex.bdi.examples.blackjack;

import java.util.ArrayList;
import java.util.List;


/**
 *  This class represents a whole blackjack-cardSet.
 *  Blackjack-cardSets are usually composed of 6 'normal' cardSets, 
 *  each containing 52 cards.
 */
public class CardSet
{
	public final static int NUMBER_PACKS = 6;

	public final static int NUMBER_CARDS = 52;

	public final static String JACK = "J";

	public final static String QUEEN = "Q";

	public final static String KING = "K";

	public final static String ACE = "A";

	public final static String DIAMOND = "DIAMOND";

	public final static String HEART = "HEART";

	public final static String SPADE = "SPADE";

	public final static String CLUB = "CLUB";

	private List cards = new ArrayList();

	/**
	 * Cards have to be encoded as Strings, cause they are being sent as a message's content
	 * and only Strings are allowed as the content-objects of ACLMessages
	 * (exceptions are Predicates and Actions, but these are ontology-specific objects)
	 */
	public CardSet()
	{
		for(int i = 0; i < NUMBER_PACKS; i++)
		{
			for(int j = 2; j <= 10; j++)
			{
				cards.add(new Card("" + j, DIAMOND, j));
				cards.add(new Card("" + j, HEART, j));
				cards.add(new Card("" + j, SPADE, j));
				cards.add(new Card("" + j, CLUB, j));
			}
			cards.add(new Card(JACK, DIAMOND, 10));
			cards.add(new Card(JACK, HEART, 10));
			cards.add(new Card(JACK, SPADE, 10));
			cards.add(new Card(JACK, CLUB, 10));
			cards.add(new Card(QUEEN, DIAMOND, 10));
			cards.add(new Card(QUEEN, HEART, 10));
			cards.add(new Card(QUEEN, SPADE, 10));
			cards.add(new Card(QUEEN, CLUB, 10));
			cards.add(new Card(KING, DIAMOND, 10));
			cards.add(new Card(KING, HEART, 10));
			cards.add(new Card(KING, SPADE, 10));
			cards.add(new Card(KING, CLUB, 10));
			cards.add(new Card(ACE, DIAMOND, 11));
			cards.add(new Card(ACE, HEART, 11));
			cards.add(new Card(ACE, SPADE, 11));
			cards.add(new Card(ACE, CLUB, 11));
		}
	}

	/**
	 *  This method returns a randomly chosen card from the cardSet.
	 *  @return randomly chosen card, encoded as a String-object.
	 */
	public Card drawCard()
	{
		int rand = new java.util.Random().nextInt(cards.size());
		return (Card)cards.remove(rand);
	}

	/**
	 *	This method calculates the overall value of a cardSet.
	 *	The only thing to be mentioned is, that aces may count as 1 or 11,
	 *	and that there is no way to influence this. Aces count 11 as long
	 *	as the overAll deckValue is beneath 21. Only in case the overAll value
	 *	lies above 21 and there are aces present, one (or more) aces count as a 1.
	 */
	public static int calculateDeckValue(Card[] cards)
	{
		int value = 0;
		int numberOfAces = 0;
		for(int i = 0; i < cards.length; i++)
		{
			if(cards[i].getType().equals(ACE))
				numberOfAces++;
			value += cards[i].getValue();
		}

		// check if overall deckValue > 21, AND if the deck contains aces
		// if both conditions apply, aces may count as 1 instead of 11,
		// so decrease the deckValue by 10 ( 11 - 1 )
		while((numberOfAces > 0) && (value > 21))
		{
			value -= 10;
			numberOfAces--;
		}

		return value;
	}

	/**
	 *  Create a string representation of the card set.
	 *  @return A string representation of the card set.
	 */
	public String toString()
	{
		return "CardSet consisting of " + NUMBER_PACKS + " packs with " + NUMBER_CARDS + " cards each. " + cards.size() + " left in this round";
	}
}
