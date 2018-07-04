package jadex.bdi.testcases.misc;

import java.util.Date;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.fipa.DFServiceDescription;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.SUtil;

/**
 *  Test the df plans.
 */
public class DFTestPlan extends Plan
{
	private static final IDFServiceDescription[] SERVICES = new IDFServiceDescription[]
	{
		new DFServiceDescription("service_a", "a", "a"),
		new DFServiceDescription("service_b", "b", "b"),
		new DFServiceDescription("service_c", "c", "c")
	};
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		int num = 1;
		num = performTests(num, getAgent().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IDF.class, RequiredServiceInfo.SCOPE_PLATFORM))); // test locally
	}
	
	/**
	 *  Perform the tests.
	 */
	public int performTests(int num, IDF df)
	{
		// Register without lease time.
		num = testRegister(num, df, null);
		num = testSearch(num, df, true);
		num	= testDeregister(num, df);

		// Register with lease time.
		num = testRegister(num, df, new Date(getTime()+1000));
		num = testSearch(num, df, true);
		waitFor(2000);
		num = testSearch(num, df, false);
		
		return num;
	}

	/**
	 *  Test registering.
	 */
	protected int testRegister(int num, IDF df, Date lt)
	{
		// Try to register by the df 
		TestReport	tr = new TestReport("#"+num++, lt!=null ? "Test register with lease time." : "Test register.");
		getLogger().info(tr.getDescription());
		try
		{
			df.register(new DFComponentDescription(getAgent().getComponentIdentifier(), SERVICES, null, null, null, lt)).get();
			getLogger().info(" register ok.");
			tr.setSucceeded(true);
		}
		catch(GoalFailureException gfe)
		{
			getLogger().info(" register failed: "+gfe);
			tr.setFailed(gfe);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		return num;
	}

	/**
	 *  Test searching for the agent.
	 */
	protected int testSearch(int num, IDF df, boolean find)
	{
		// Try to search at the df.
		TestReport	tr = new TestReport("#"+num++, "Try to search for registration.");
		getLogger().info("\nTrying to search...");
		try
		{
			IDFComponentDescription[]	results	= df.search(new DFComponentDescription(null, SERVICES, null, null, null, null), null).get();
			if(find==(results.length!=0))
			{
				getLogger().info(" search ok: "+ SUtil.arrayToString(results));
				tr.setSucceeded(true);
			}
			else
			{
				getLogger().info("Searchb not ok: "+find+", "+SUtil.arrayToString(results));
				tr.setFailed("Searchb not ok: "+find+", "+SUtil.arrayToString(results));
			}
		}
		catch(GoalFailureException gfe)
		{
			getLogger().info(" search failed: "+gfe);
			tr.setFailed(gfe);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		return num;
	}
	
	/**
	 *  Test deregistering.
	 */
	protected int testDeregister(int num, IDF df)
	{
		// Try to deregister by the df 
		TestReport	tr = new TestReport("#"+num++, "Test deregister.");
		getLogger().info(tr.getDescription());
		try
		{
			df.deregister(new DFComponentDescription(getAgent().getComponentIdentifier())).get();
			getLogger().info(" deregister ok.");
			tr.setSucceeded(true);
		}
		catch(GoalFailureException gfe)
		{
			getLogger().info(" deregister failed: "+gfe);
			tr.setFailed(gfe);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		return num;
	}
}
