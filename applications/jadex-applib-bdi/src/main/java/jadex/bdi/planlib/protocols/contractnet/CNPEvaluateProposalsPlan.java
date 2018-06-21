package jadex.bdi.planlib.protocols.contractnet;

import jadex.bdi.planlib.protocols.IProposalEvaluator;
import jadex.bdi.planlib.protocols.NegotiationRecord;
import jadex.bdi.planlib.protocols.ParticipantProposal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Evaluate proposals using the proposal evaluator interface.
 */
public class CNPEvaluateProposalsPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		Object	cfp	= getParameter("cfp").getValue();
		Object	cfp_info	= getParameter("cfp_info").getValue();
		NegotiationRecord[]	history	= (NegotiationRecord[])getParameterSet("history").getValues();
		ParticipantProposal[]	proposals	= (ParticipantProposal[])getParameterSet("proposals").getValues();

		IProposalEvaluator	evaluator	= (IProposalEvaluator)cfp_info;
		ParticipantProposal[]	acceptables	= evaluator.evaluateProposals(cfp, cfp_info, history, proposals);
		
		getParameterSet("acceptables").addValues(acceptables);
	}
}
