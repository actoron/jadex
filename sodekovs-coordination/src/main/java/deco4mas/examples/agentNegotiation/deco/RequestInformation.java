package deco4mas.examples.agentNegotiation.deco;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluate Proposals
 */
public class RequestInformation
{
	private Map<String, Object> informations = new HashMap<String, Object>();;

	public RequestInformation(Map<String, Object> information)
	{
		informations = information;
	}

	/**
	 * Get the extra information about request
	 */
	public Object get(String information)
	{
		return informations.get(information);
	}
}
