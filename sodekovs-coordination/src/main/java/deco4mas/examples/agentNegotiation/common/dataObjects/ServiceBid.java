package deco4mas.examples.agentNegotiation.common.dataObjects;

import java.util.HashMap;
import java.util.Map;

public class ServiceBid
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

	@Override
	public String toString()
	{
		StringBuffer bidString = new StringBuffer("|");
		for (Map.Entry<String, Double> bid : bidFactor.entrySet())
		{
			bidString.append(bid.getKey() + " " + bid.getValue() + "|");
		}

		return "Bid(" + bidString.toString() + ")";
	}
}
