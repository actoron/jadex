package jadex.bridge.component.streams;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.transformation.annotations.Alias;

/**
 *  Info sent as content of the init message.
 */
@Alias("jadex.base.service.message.streams.InitInfo")
public class InitInfo
{
	/** The initiator. */
	protected IComponentIdentifier initiator;
	
	/** The participant. */
	protected IComponentIdentifier participant;
	
	/** The non-functional properties. */
	protected Map<String, Object> nonfunc;

	/**
	 *  Create a new init info.
	 */
	public InitInfo()
	{
	}

	/**
	 *  Create a new init info.
	 */
	public InitInfo(IComponentIdentifier initiator,
		IComponentIdentifier participant, Map<String, Object> nonfunc)
	{
		this.initiator = initiator;
		this.participant = participant;
		this.nonfunc = nonfunc;
	}

	/**
	 *  Get the initiator.
	 *  @return the initiator.
	 */
	public IComponentIdentifier getInitiator()
	{
		return initiator;
	}

	/**
	 *  Set the initiator.
	 *  @param initiator The initiator to set.
	 */
	public void setInitiator(IComponentIdentifier initiator)
	{
		this.initiator = initiator;
	}

	/**
	 *  Get the participant.
	 *  @return the participant.
	 */
	public IComponentIdentifier getParticipant()
	{
		return participant;
	}

	/**
	 *  Set the participant.
	 *  @param participant The participant to set.
	 */
	public void setParticipant(IComponentIdentifier participant)
	{
		this.participant = participant;
	}

	/**
	 *  Get the nonFunctionalProperties.
	 *  @return the nonFunctionalProperties.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
		return nonfunc;
	}

	/**
	 *  Set the nonFunctionalProperties.
	 *  @param nonFunctionalProperties The nonFunctionalProperties to set.
	 */
	public void setNonFunctionalProperties(Map<String, Object> nonFunctionalProperties)
	{
		this.nonfunc = nonFunctionalProperties;
	}
}
