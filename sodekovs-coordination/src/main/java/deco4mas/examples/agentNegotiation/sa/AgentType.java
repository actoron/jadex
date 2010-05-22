package deco4mas.examples.agentNegotiation.sa;

public class AgentType
{
	private String typeName;
	
	private Double blackoutCharacter;	
	private Double costCharacter;
	private Double durationCharacter;
	
	public AgentType(String typeName, Double blackoutCharacter, Double costCharacter, Double durationCharacter)
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
