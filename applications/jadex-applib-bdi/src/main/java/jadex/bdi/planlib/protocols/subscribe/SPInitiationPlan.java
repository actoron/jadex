package jadex.bdi.planlib.protocols.subscribe;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

public class SPInitiationPlan extends Plan
{
	public void body()
	{
		IMessageEvent subReq = createMessageEvent("sp_subscribe");
		subReq.getParameter(SFipa.CONTENT).setValue(getParameter("subscription").getValue());

		subReq.getParameterSet(SFipa.RECEIVERS).addValue(getParameter("receiver").getValue());
		if(getParameter("language").getValue()!=null)
			subReq.getParameter(SFipa.LANGUAGE).setValue(getParameter("language").getValue());
		if(getParameter("ontology").getValue()!=null)
			subReq.getParameter(SFipa.ONTOLOGY).setValue(getParameter("ontology").getValue());
		if(getParameter("conversation_id").getValue()!=null)
			subReq.getParameter(SFipa.CONVERSATION_ID).setValue(getParameter("conversation_id").getValue());
		
		getParameter("subscription_id").setValue(subReq.getParameter(SFipa.CONVERSATION_ID));
		
		getWaitqueue().addReply(subReq);
		IMessageEvent reply = sendMessageAndWait(subReq);
		if (!SFipa.AGREE.equals(reply.getParameter(SFipa.PERFORMATIVE).getValue()))
			fail();
	}
}
