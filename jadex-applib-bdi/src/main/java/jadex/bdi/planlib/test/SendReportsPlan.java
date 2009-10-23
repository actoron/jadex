package jadex.bdi.planlib.test;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

/**
 *  Send the reports to a test service.
 */
public class SendReportsPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// Compose results in test case.
		TestReport[]	reports	= (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
		int cnt = ((Integer)getBeliefbase().getBelief("testcase_cnt").getFact()).intValue();
		IComponentIdentifier	testcenter	= (IComponentIdentifier)getBeliefbase().getBelief("testcenter").getFact();
		Testcase	testcase	= new Testcase(cnt, reports);
		
		// Send reports to the test service.
		IMessageEvent me = createMessageEvent("inform_reports");
		me.getParameter(SFipa.CONTENT).setValue(testcase);
		// Hack!!! convention agent2testcenter used for convid to allow matching.
		me.getParameter(SFipa.CONVERSATION_ID).setValue(getAgentIdentifier().getLocalName()+"2"+testcenter.getLocalName());
		me.getParameterSet(SFipa.RECEIVERS).addValue(testcenter);
		sendMessage(me);

		if(!((Boolean)getBeliefbase().getBelief("keepalive").getFact()).booleanValue())
			killAgent();
	}
}
