package jadex.bdi.planlib.protocols;


/**
 *  The interface for calculating offers of auctions (e.g. typically prices or sth. similar)
 *  @see jadex.bdi.planlib.protocols.LinearPriceCalculator
 *  @see jadex.bdi.planlib.protocols.ExponentialPriceCalculator
 */
public interface IOfferGenerator
{
	/**
	 *  Returns the current offer for the round.
	 *  @return The current offer.
	 */
	public Comparable getCurrentOffer();

	/**
	 *  Returns the offer for the last round.
	 *  @return The last offer.
	 */
	public Comparable getLastOffer();
	
	/**
	 *  Returns the limit offer to be reached in order 
	 *  to terminate successfully.
	 *  @return Minimal/maximal offer for this auction to terminate.
	 */
	public Comparable getLimitOffer();

	/**
	 *  Returns the start offer for the auction.
	 *  @return Start offer.
	 */
	public Comparable getStartOffer();
	
	/**
	 *  Get the round.
	 *  @return The round.
	 */
	public int getRound();
	
	/**
	 *  Increase the round.
	 */
	public void setNextRound();
}
