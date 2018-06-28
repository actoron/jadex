package jadex.bdi.testcases.planlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdi.planlib.protocols.IProposalEvaluator;
import jadex.bdi.planlib.protocols.NegotiationRecord;
import jadex.bdi.planlib.protocols.ParticipantProposal;
import jadex.bdi.planlib.protocols.ProposalEvaluator;
import jadex.bdi.testcases.AbstractMultipleAgentsPlan;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Test the cnp protocol execution.
 */
public class CNPTestPlan extends AbstractMultipleAgentsPlan
{
	//-------- methods --------
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IProposalEvaluator	cfp_info	= new ProposalEvaluator(Integer.valueOf(5), false);

		performTest("one", "Test executing one proposal using custom plans", false, null);
		performTest("one-default",  "Test executing one proposal using default plans", false, cfp_info);
		performTest("all",  "Test executing all proposals using custom plans", true, null);
		performTest("all-default",  "Test executing all proposals using default plans", true, cfp_info);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Test initiating a contract net interaction with given settings.
	 *  @param executeall	True, if all acceptable proposals should be executed.
	 *  @param cfp_info	Use given cfp info (e.g. for default plans) 
	 */
	protected void	performTest(String name, String description, boolean executeall, Object cfp_info)
	{
		// Create 3 participants with different offers.
		Map[] args = new Map[]{new HashMap(), new HashMap(), new HashMap()};
		
		args[0].put("offer", Integer.valueOf(5));
		args[0].put("execute", Boolean.TRUE);
		
		args[1].put("offer", Integer.valueOf(1));
		args[1].put("execute", Boolean.TRUE);
		
		args[2].put("offer", Integer.valueOf(7));
		args[2].put("execute", Boolean.valueOf(executeall));	// Fails when only one is needed, to check if proposal 5 gets executed.

		List agents = createAgents("/jadex/bdi/testcases/planlib/CNPReceiver.agent.xml", args);	

		TestReport tr = new TestReport(name, description);
		if(assureTest(tr))
		{
			try
			{
				IGoal cnpini = createGoal("cnpcap.cnp_initiate");
				cnpini.getParameterSet("receivers").addValues(agents.toArray(new IComponentIdentifier[agents.size()]));
				cnpini.getParameter("cfp").setValue("CFP for a task.");
				cnpini.getParameter("cfp_info").setValue(cfp_info);
				cnpini.getParameter("executeall").setValue(Boolean.valueOf(executeall));
				dispatchSubgoalAndWait(cnpini);
				getLogger().info("CNP result:"+ SUtil.arrayToString(cnpini.getParameterSet("result").getValues()));
				
				// Check final proposals executed in last round.
				NegotiationRecord[]	history	= (NegotiationRecord[])cnpini.getParameterSet("history").getValues();
				ParticipantProposal[]	finalproposals	= history[history.length-1].getProposals();
				
				if(executeall)
				{
					// Check number of proposals.
					if(finalproposals.length==2)
					{
						// Check values of proposals.
						if(((Number)finalproposals[0].getProposal()).intValue()==7
							&& ((Number)finalproposals[1].getProposal()).intValue()==5)
						{
							// For executeall, both evaluation values should be success. 
							if("success".equals(finalproposals[0].getEvaluation())
								&& "success".equals(finalproposals[1].getEvaluation()))
							{
								tr.setSucceeded(true);
							}
							else
							{
								tr.setFailed("Wrong evaluations for accepted proposals: "+SUtil.arrayToString(finalproposals));
							}
						}
						else
						{
							tr.setFailed("Wrong values for accepted proposals: "+SUtil.arrayToString(finalproposals));
						}
					}
					else
					{
						tr.setFailed("Wrong number of accepted proposals: "+SUtil.arrayToString(finalproposals));
					}
				}
				else
				{
					// Check number of proposals.
					if(finalproposals.length==1)
					{
						// Check values of proposals.
						if(((Number)finalproposals[0].getProposal()).intValue()==5)
						{
							// For not executeall, evaluation value should be success. 
							if("success".equals(finalproposals[0].getEvaluation()))
							{
								tr.setSucceeded(true);
							}
							else
							{
								tr.setFailed("Wrong evaluations for accepted proposals: "+SUtil.arrayToString(finalproposals));
							}
						}
						else
						{
							tr.setFailed("Wrong values for accepted proposals: "+SUtil.arrayToString(finalproposals));
						}
					}
					else
					{
						tr.setFailed("Wrong number of accepted proposals: "+SUtil.arrayToString(finalproposals));
					}
				}
			}
			catch(GoalFailureException e)
			{
				tr.setFailed("Exception occurred: "+e);
			}
		}

		// Destroy agents first, as setting report might abort plan unexpectedly.
		destroyAgents();
		
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
