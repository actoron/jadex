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

		subReq.getParameterSet("receivers").addValue(getParameter("receiver").getValue());
		if(getParameter("language").getValue()!=null)
			subReq.getParameter("language").setValue(getParameter("language").getValue());
		if(getParameter("ontology").getValue()!=null)
			subReq.getParameter("ontology").setValue(getParameter("ontology").getValue());
		if(getParameter("conversation_id").getValue()!=null)
			subReq.getParameter("conversation_id").setValue(getParameter("conversation_id").getValue());
		
		getParameter("subscription_id").setValue(subReq.getParameter(SFipa.CONVERSATION_ID));
		
		getWaitqueue().addReply(subReq);
		IMessageEvent reply = sendMessageAndWait(subReq);
		if (!SFipa.AGREE.equals(reply.getParameter(SFipa.PERFORMATIVE).getValue()))
			fail();
	}
}
