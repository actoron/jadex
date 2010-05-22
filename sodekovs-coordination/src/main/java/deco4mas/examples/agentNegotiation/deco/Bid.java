package deco4mas.examples.agentNegotiation.deco;

import java.util.HashMap;
import java.util.Map;

public class Bid
{
	private Map<String, Double> bidFactor = new HashMap<String, Double>();
	
	public void setBid(String bidFactorName, Double bid)
	{
		bidFactor.put(bidFactorName, bid);
	}
	
	public Double getBidFactor(String bidFactorName)
	{
		return bidFactor.get(bidFactorName);
	}
	
}
