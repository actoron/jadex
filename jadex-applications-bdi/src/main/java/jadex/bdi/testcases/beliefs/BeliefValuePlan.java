package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Test initial belief values.
 */
public class BeliefValuePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		TestReport tr = new TestReport("#1", "Test agent_initial_bel default value.");
//		BDIXModel	model	= (BDIXModel)getScope().getAgentModel();
//		SBDIModel.findBeliefDefaultValue(model, model.getCapability().getBelief("abel"), configname);
//		Map<String, Object>	check = new HashMap<String, Object>();
//		check.put("timeout", Long.valueOf(20000));
//		check.put("abel", "agent_initial_bel");
//		check.put("bbel", "capability_initial_bel");
//		check.put("cbel", "capability_default_bel");
//		UnparsedExpression[]	args	= getScope().getAgentModel().getConfiguration(getScope().getConfigurationName()).getArguments();
//		boolean	ok	= args.length==check.size();
//		for(int i=0; ok && i<args.length; i++)
//		{
//			ok	= SUtil.equals(
//				SJavaParser.getParsedValue(args[i], getScope().getAgentModel().getAllImports(), getAgent().getFetcher(), getScope().getClassLoader()),
//				check.get(args[i].getName()));
//		}
//		if(ok)
//		{
//			tr.setSucceeded(true);
//		}
//		else
//		{
//			tr.setFailed("Argument values incorrect: "+SUtil.arrayToString(args)+", "+check);
//		}
//		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		
		
		TestReport	tr = new TestReport("#2", "Test initial belief values.");

		String bel_a = (String)getBeliefbase().getBelief("cap_a.bel").getFact();
		String bel_b = (String)getBeliefbase().getBelief("cap_b.bel").getFact();
		String bel_c = (String)getBeliefbase().getBelief("cap_c.bel").getFact();

		String[] belset_a = (String[])getBeliefbase().getBeliefSet("cap_a.belset").getFacts();
		String[] belset_b = (String[])getBeliefbase().getBeliefSet("cap_b.belset").getFacts();
		String[] belset_c = (String[])getBeliefbase().getBeliefSet("cap_c.belset").getFacts();

		tr.setSucceeded(true);
		if(!bel_a.equals("agent_initial_bel"))
		{
			getLogger().info("bel_a error: "+bel_a);
			tr.setSucceeded(false);
		}
		if(!bel_b.equals("capability_initial_bel"))
		{
			getLogger().info("bel_b error: "+bel_b);
			tr.setSucceeded(false);
		}
		if(!bel_c.equals("capability_default_bel"))
		{
			getLogger().info("bel_c error: "+bel_c);
			tr.setSucceeded(false);
		}

		if(!belset_a[0].equals("agent_initial_belset_0") || !belset_a[1].equals("agent_initial_belset_1"))
		{
			getLogger().info("belset_a error: "+ SUtil.arrayToString(belset_a));
			tr.setSucceeded(false);
		}
		if(!belset_b[0].equals("capability_initial_belset_0") || !belset_b[1].equals("capability_initial_belset_1"))
		{
			getLogger().info("belset_b error: "+ SUtil.arrayToString(belset_b));
			tr.setSucceeded(false);
		}
		if(!belset_c[0].equals("capability_default_belset_0") || !belset_c[1].equals("capability_default_belset_1"))
		{
			getLogger().info("belset_c error: "+ SUtil.arrayToString(belset_c));
			tr.setSucceeded(false);
		}

		if(!tr.isSucceeded())
		{
			tr.setReason("Some initial value was not set correctly.");
		}

		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}

