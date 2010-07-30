package deco4mas.examples.agentNegotiation.common.negotiationInformation;

import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceContract;
import jadex.bridge.IComponentIdentifier;

public class NegotiationContractInformation extends NegotiationInformation
{
	public static String TENTATIVE_REWARD = "tentativeContract";
	public static String FINAL_REWARD = "finalContract";

	private Boolean[] answers = { Boolean.FALSE, Boolean.FALSE };
	private ServiceContract contract;
	private String state;

	public NegotiationContractInformation(Integer id, String mediumType, ServiceContract contract, String state)
	{
		super(id, mediumType,contract.getServiceType());
			this.contract = contract;
			this.state = state;
	}

	public ServiceContract getContract()
	{
			return contract;
	}

	public String getState()
	{
		synchronized (monitor)
		{
			return state;
		}
	}

	public void setState(String state)
	{
		synchronized (monitor)
		{
			this.state = state;
		}
	}

	public Boolean[] getAnswers()
	{
		synchronized (monitor)
		{
			return answers;
		}
	}

	public void setAnswer(Boolean answer, IComponentIdentifier receiver)
	{
		synchronized (monitor)
		{
			if (receiver.getName().equals(contract.getInitiator().getName()))
			{
				answers[0] = true;
			} else if (receiver.getName().equals(contract.getParticipant().getName()))
			{
				answers[1] = true;
			}
		}
	}

	@Override
	public String toString()
	{
		String answerString = "";
		if (state.equals(TENTATIVE_REWARD))
		{
			answerString = "Answers(" + answers[0] + " , " + answers[1] + ")";
		}
		return "NegotiationContractInformation(" + id + " , " + mediumType + " , " + contract + " , " + state + " , " + answerString + ")";
	}
}
