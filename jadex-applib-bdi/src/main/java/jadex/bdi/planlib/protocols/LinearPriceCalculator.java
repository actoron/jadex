package jadex.bdi.planlib.protocols;


import java.util.List;

import jadex.commons.collection.SCollection;

/**
 *  A sample implementation of the IPriceCalculator. Calculates 
 *  the price for the rounds of an english auction.
 */
public class LinearPriceCalculator implements IOfferGenerator
{
	//-------- attributes --------

	/** The negotiation round. */
	protected int round;

	/** The start price. */
	protected double startprice;
	
	/** The limit price. */
	protected double limitprice;

	/** The prices. */
	protected List prices;
	
	/** The variance. */
	protected double variation;

	/** The base (base^round). */
	protected double stepwidth;
	
	/** The minimum step width. */
	protected double minstepwidth;
	
	/** Flag indicating if increasing or decreasing prices are calculated. */
	protected boolean increasing;

	
	//-------- constructors --------

	/**
	 *  Create a new linear price calculator.
	 *  @param startprice The start price.
	 *  @param limitprice The minimal price for a successful auction.
	 *  @param base The base for the exponential function, should be
	 *         positive for english and negative for dutch auctions.
	 */
	public LinearPriceCalculator(double startprice, double limitprice, double base)
	{
		this(startprice, limitprice, base, 0.0, 0.0);
	}

	/**
	 *  Create a new linear price calculator.
	 *  @param startprice the start price
	 *  @param limitprice the minimal price for a successful auction
	 *  @param stepwidth The normal increment/decrement value.
	 *  @param variation The variation in percentage (0->1) for the increment.
	 *  	   The increment will be used to adapt the increment by multiplying
	 *  	   it with (+/-) a random value that has maximal the variation
	 *  	   percentage influence, e.g. setting 0.1 will cause the increment
	 *  	   being maximally +/-10% changed.
	 */
	public LinearPriceCalculator(double startprice, double limitprice,
		double stepwidth, double variation, double minstepwidth)
	{
		this.startprice = startprice;
		this.prices = SCollection.createArrayList();
		this.prices.add(Double.valueOf(startprice));
		this.limitprice = limitprice;
		this.stepwidth = stepwidth;
		this.variation = variation;
		this.minstepwidth = minstepwidth;
		if(stepwidth<0)
			throw new IllegalArgumentException("Step width need to be > 0.");
		
		this.round = 1;
		this.increasing = limitprice > startprice;
	}
	
	//-------- methods --------

	/**
	 *  Get the current price.
	 *  @return The current price.
	 */
	public Comparable getCurrentOffer()
	{
		double ret;
		if(prices.size() == getRound())
		{
			ret = ((Double)prices.get(getRound()-1)).doubleValue();
		}
		else
		{
			// Linear change: price(round) = price(round)*
			
			double lastprice = ((Double)prices.get(getRound()-2)).doubleValue();
			double var = (Math.random()-0.5)*2*variation;
			if(increasing)
				ret = lastprice+stepwidth+(stepwidth*var);
			else
				ret = lastprice-stepwidth+(stepwidth*var);
			
			
			// Ensure monotony
			if(increasing && ret<lastprice)
				ret = lastprice+minstepwidth;
			else if(!increasing && ret>lastprice)
				ret = lastprice-minstepwidth;
						
			// Ensure that price is not out of bounds
			if((increasing && ret>limitprice) || (!increasing && ret<limitprice))
				ret = limitprice;
			
			prices.add(Double.valueOf(ret));
		}
		
		return Double.valueOf(ret);
	}
	
	/**
	 *  Returns the offer for the last round.
	 *  @return The last offer.
	 */
	public Comparable getLastOffer()
	{
		return prices.size()>1? (Comparable)prices.get(prices.size()-2): null;
	}

	/**
	 *  Get the min price.
	 *  @return The minprice.
	 */
	public Comparable getLimitOffer()
	{
		return Double.valueOf(limitprice);
	}

	/**
	 *  Get the start price.
	 *  @return The startprice
	 */
	public Comparable getStartOffer()
	{
		return Double.valueOf(startprice);
	}
	
	/**
	 *  Get the round.
	 *  @return The round.
	 */
	public int getRound()
	{
		return round;
	}

	/**
	 *  Increase the round.
	 */
	public void setNextRound()
	{
		round++;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		IOfferGenerator pc = new LinearPriceCalculator(100, 10, 10, 0.3, 1.0);
		for(int i=0; i<20; i++)
		{
			System.out.println("Price "+i+": "+pc.getCurrentOffer());
			pc.setNextRound();
		}
		System.out.println();
		
		pc = new LinearPriceCalculator(100, 250, 10, 0.3, 1.0);
		for(int i=0; i<20; i++)
		{
			System.out.println("Price "+i+": "+pc.getCurrentOffer());
			pc.setNextRound();
		}
	}
}
