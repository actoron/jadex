package deco4mas.examples.agentNegotiation.common.dataObjects;

public class ServiceAgentType
{
	private String typeName;

	private Double blackoutCharacter;
	private Double costCharacter;
	private Double durationCharacter;

	public ServiceAgentType(String typeName, Double costCharacter, Double durationCharacter, Double blackoutCharacter)
	{
		this.typeName = typeName;
		this.blackoutCharacter = blackoutCharacter;
		this.costCharacter = costCharacter;
		this.durationCharacter = durationCharacter;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public Double getBlackoutCharacter()
	{
		return blackoutCharacter;
	}

	public Double getCostCharacter()
	{
		return costCharacter;
	}

	public Double getDurationCharacter()
	{
		return durationCharacter;
	}

}
