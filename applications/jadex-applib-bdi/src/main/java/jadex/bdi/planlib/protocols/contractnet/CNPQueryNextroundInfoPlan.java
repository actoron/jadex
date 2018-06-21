package jadex.bdi.planlib.protocols.contractnet;

import jadex.bdi.planlib.protocols.IQueryNextroundInfo;
import jadex.bdi.planlib.protocols.NegotiationRecord;
import jadex.bdi.planlib.protocols.ParticipantProposal;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Default plan for the query nextround info goal
 *  of the iterated contract-net protocol.
 */
public class CNPQueryNextroundInfoPlan extends Plan
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

		IQueryNextroundInfo	qnri	= (IQueryNextroundInfo)cfp_info;
		IQueryNextroundInfo.NextroundInfo	nri	= new IQueryNextroundInfo.NextroundInfo(cfp, cfp_info, history[history.length-1].getParticipants());
		boolean	iterate	= qnri.queryNextroundInfo(nri, history, proposals);
		
		getParameter("cfp").setValue(nri.getCfp());
		getParameter("cfp_info").setValue(nri.getCfpInfo());
		getParameterSet("participants").removeValues();
		getParameterSet("participants").addValues(nri.getParticipants());
		
		getParameter("iterate").setValue(Boolean.valueOf(iterate));
	}
}
