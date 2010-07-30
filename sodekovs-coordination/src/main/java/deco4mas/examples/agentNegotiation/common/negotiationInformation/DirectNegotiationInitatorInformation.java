package deco4mas.examples.agentNegotiation.common.negotiationInformation;

import jadex.bridge.IComponentIdentifier;
import java.util.Collections;
import java.util.Map;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.deco.medien.NegotiationMechanism;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.ISelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy.IUtilityFunction;

public class DirectNegotiationInitatorInformation extends NegotiationInformation
{
	private IComponentIdentifier initiator;
	private IUtilityFunction utilityFunction;
	private ISelectionStrategy selector;
	private ExtraInformation informations;

	public DirectNegotiationInitatorInformation(Integer id, IComponentIdentifier initiator, ServiceType serviceType,
		IUtilityFunction utilityFunction, ISelectionStrategy selector)
	{
		super(id, NegotiationMechanism.NAME, serviceType);
		this.initiator = initiator;
		this.utilityFunction = utilityFunction;
		this.selector = selector;
	}

	public DirectNegotiationInitatorInformation(Integer id, IComponentIdentifier initiator, ServiceType serviceType,
		IUtilityFunction utilityFunction, ISelectionStrategy selector, Map<String, Object> informations)
	{
		this(id, initiator, serviceType, utilityFunction, selector);
		this.informations = new ExtraInformation(informations);
	}

	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	public IUtilityFunction getUtilityFunction()
	{
		return utilityFunction;
	}

	public ISelectionStrategy getSelector()
	{
		return selector;
	}

	public Object getInfo(String information)
	{
		return informations.get(information);
	}

	@Override
	public String toString()
	{
		return "DirectNegotiationInitatorInformation(" + id + " , " + mediumType + " , " + initiator + " , " + serviceType + " , "
			+ utilityFunction + " , " + informations + ")";
	}

	/**
	 * Holds some extra Informations synchronized for concurrent access
	 */
	private class ExtraInformation
	{
		private Map<String, Object> informations;

		protected ExtraInformation(Map<String, Object> informations)
		{
			this.informations = Collections.synchronizedMap(informations);
		}

		/**
		 * Get the extra information about request
		 */
		public Object get(String information)
		{
			return informations.get(information);
		}

		@Override
		public String toString()
		{
			StringBuffer infoString = new StringBuffer("|");
			for (Map.Entry<String, Object> info : informations.entrySet())
			{
				infoString.append(info.getKey() + " , " + info.getValue() + "|");
			}
			return "ExtraInformation(" + infoString.toString() + ")";
		}
	}
}
