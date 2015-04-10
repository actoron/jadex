package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IEvent;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.SUtil;

import java.util.Arrays;
import java.util.Date;

/**
 *  Test the df plans.
 */
public class DFTestPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		waitFor(300);	// Allow initial register to happen first.
		int num = 1;
		num	= performInitialTests(num);
		num = performTests(num, null); // test locally
		
		// Todo: support remote DF agent!?
		IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getServiceUpwards(
			(IServiceProvider)getScope().getServiceContainer(), IComponentManagementService.class).get(this);
//		IComponentIdentifier da = ces.createComponentIdentifier(SFipa.DF_COMPONENT, getComponentIdentifier(), null);
		IComponentIdentifier da = new BasicComponentIdentifier(SFipa.DF_COMPONENT, getComponentIdentifier());
		performTests(num, da); // test remotely
	}
	
	/**
	 *  Test initial keep registered goal.
	 */
	public int	performInitialTests(int num)
	{
		IDFComponentDescription desc = ((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getScope().getServiceContainer(), IDF.class).get(this))
			.createDFComponentDescription(null, new IDFServiceDescription[]
			{
				((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
					.createDFServiceDescription("service_a", "a", "a"),
				((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
					.createDFServiceDescription("service_b", "b", "b"),
				((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
					.createDFServiceDescription("service_c", "c", "c")
			}, null, null, null, null);

		// Try to search at the df.
		TestReport	tr = new TestReport("#"+num++, "Try to search for initial registration.");
		getLogger().info("\nTrying to search...");
		IGoal search = createGoal("dfcap.df_search");
		search.getParameter("description").setValue(desc);
		try
		{
			dispatchSubgoalAndWait(search);
			getLogger().info(" search ok: "+ SUtil.arrayToString(search.getParameterSet("result").getValues()));
			tr.setSucceeded(true);
		}
		catch(GoalFailureException gfe)
		{
			getLogger().info(" search failed. "+Arrays.toString(search.getParameterSet("result").getValues()));
			tr.setReason("Search failed. "+SUtil.arrayToString(search.getParameterSet("result").getValues()));
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Check if keep goal continues to modify.
		getLogger().info("Trying to keep registered...");
		tr = new TestReport("#"+num++, "Try to keep (modify) initial registration.");
		try
		{
//			IGoal[]	keeps	= getGoalbase().getGoals("dfcap.df_keep_registered");
//			waitForGoal(keeps[0], 6000);
//			tr.setSucceeded(true);
//			keeps[0].drop();

			IGoal	keep	= waitForGoal("dfcap.df_keep_registered", 6000);
			tr.setSucceeded(true);
			keep.drop();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			getLogger().info(" modify failed.");
			tr.setReason("Modify failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		// Deregister by the df to asure clean state before next tests.
		IGoal deregister = createGoal("dfcap.df_deregister");
		deregister.getParameter("description").setValue(desc);
		try
		{
			dispatchSubgoalAndWait(deregister);
		}
		catch(GoalFailureException gfe)
		{
			// No prob, registration already removed.
		}
		
		return num;
	}

	/**
	 *  Perform the tests.
	 */
	public int performTests(int num, IComponentIdentifier df)
	{
		IDFComponentDescription desc = ((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
			.createDFComponentDescription(null, new IDFServiceDescription[]
			{
				((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
					.createDFServiceDescription("service_a", "a", "a"),
				((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
					.createDFServiceDescription("service_b", "b", "b"),
				((IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this))
					.createDFServiceDescription("service_c", "c", "c")
			}, null, null, null, null);
		
		long olt = getTime()+2000;
//		desc_clone.setLeaseTime(new Date(olt));
		
		IDF dfservice = (IDF)SServiceProvider.getServiceUpwards((IServiceProvider)getServiceContainer(), IDF.class).get(this);
		// Hack! does not clone services
		IDFComponentDescription desc_clone = dfservice.createDFComponentDescription(desc.getName(), desc.getServices(), desc.getLanguages(), desc.getOntologies(), desc.getProtocols(), new Date(olt));

		// Try to register by the df for some lease time.
		TestReport tr = new TestReport("#"+num++, "Test of lease time.");
		getLogger().info("Testing lease time...");
		IGoal register = createGoal("dfcap.df_register");
		register.getParameter("description").setValue(desc_clone);
		register.getParameter("df").setValue(df);
		//register.getParameter("leasetime").setValue(new Long(2000));

		try
		{
			dispatchSubgoalAndWait(register);
			getLogger().info(" register ok.");
			waitFor(2200);
			//desc_clone.setLeaseTime(null);
			//desc_clone.setName(null);
			IGoal search = createGoal("dfcap.df_search");
			search.getParameter("description").setValue(desc_clone);
			search.getParameter("df").setValue(df);
			dispatchSubgoalAndWait(search);
			if(search.getParameterSet("result").getValues().length>0)
			{
				getLogger().info(" lease time test failed. "+Arrays.toString(search.getParameterSet("result").getValues()));
				tr.setReason("Lease time test failed. "+SUtil.arrayToString(search.getParameterSet("result").getValues()));
			}
			else
			{
				tr.setSucceeded(true);
			}
		}
		catch(GoalFailureException gfe)
		{
			//getLogger().info(" register failed. "+register.getResult());
			getLogger().info(" register failed. "+register.getParameter("result").getValue());
			tr.setReason("Register failed. "+register.getParameter("result").getValue());
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		//waitFor(2000);

		// Try to register by the df.
		tr = new TestReport("#"+num++, "Try to register.");
		getLogger().info("Trying to register...");
		register = createGoal("dfcap.df_register");
		register.getParameter("description").setValue(desc);
		register.getParameter("df").setValue(df);
		try
		{
			dispatchSubgoalAndWait(register);
			getLogger().info(" register ok.");
			tr.setSucceeded(true);
		}
		catch(GoalFailureException gfe)
		{
			//getLogger().info(" register failed. "+register.getResult());
			getLogger().info(" register failed. "+register.getParameter("result").getValue());
			tr.setReason("Register failed. "+register.getParameter("result").getValue());
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		//waitFor(2000);

		// Try to search at the df.
		tr = new TestReport("#"+num++, "Try to search.");
		getLogger().info("\nTrying to search...");
		IGoal search = createGoal("dfcap.df_search");
		search.getParameter("description").setValue(desc);
		search.getParameter("df").setValue(df);
		try
		{
			dispatchSubgoalAndWait(search);
			//getLogger().info(" search ok: "+SUtil.arrayToString(search.getResult()));
			getLogger().info(" search ok: "+ SUtil.arrayToString(search.getParameterSet("result").getValues()));
			tr.setSucceeded(true);
		}
		catch(GoalFailureException gfe)
		{
			//getLogger().info(" search failed. "+search.getResult());
			getLogger().info(" search failed. "+SUtil.arrayToString(search.getParameterSet("result").getValues()));
			tr.setReason("Search failed. "+SUtil.arrayToString(search.getParameterSet("result").getValues()));
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		//waitFor(2000);

		// Try to deregister by the df.
		tr = new TestReport("#"+num++, "Try to deregister.");
		getLogger().info("\nTrying to deregister...");
		IGoal deregister = createGoal("dfcap.df_deregister");
		deregister.getParameter("description").setValue(desc);
		deregister.getParameter("df").setValue(df);
		try
		{
			dispatchSubgoalAndWait(deregister);
			getLogger().info(" deregister ok.");
			tr.setSucceeded(true);
		}
		catch(GoalFailureException gfe)
		{
			getLogger().info(" deregister failed. "+deregister);
			tr.setReason("Deregister failed: "+deregister);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Try to keep registered by the df.
		tr = new TestReport("#"+num++, "Try to keep registered (initial register).");
		getLogger().info("Trying to keep registered (should modify each 5 sec)...");
		IGoal keep = createGoal("dfcap.df_keep_registered");
		keep.getParameter("description").setValue(desc);
		keep.getParameter("leasetime").setValue(Integer.valueOf(5000));
		keep.getParameter("df").setValue(df);
		IEvent event = null;
		try
		{
			dispatchSubgoalAndWait(keep);
		}
		catch(GoalFailureException e)
		{
//			e.printStackTrace();
			getLogger().warning("Exception: "+e);
		}
		if(keep.isSucceeded())
		{
			getLogger().info(" initial register ok.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info(" initial register failed: "+keep.getLifecycleState()+", "+event);
			tr.setReason("Initial register failed: "+keep.getLifecycleState()+", "+event);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		//waitFor(2000);
		getLogger().info("Trying to keep registered...");
		tr = new TestReport("#"+num++, "Try to keep registered (modify).");
		try
		{
			waitForGoal(keep, 6000);
			if(keep.isSucceeded())
				getLogger().info(" modify succeeded. ");
			tr.setSucceeded(true);
		}
		catch(TimeoutException e)
		{
			getLogger().info(" modify failed. "+keep);
			tr.setReason("Modify failed. "+keep);
		}
		catch(GoalFailureException e)
		{
			getLogger().info(" modify failed. "+keep);
			tr.setReason("Modify failed. "+keep);
		}
		keep.drop();
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		//getLogger().info("\nAll tests finished");

		// Deregister and delete agent.
		try
		{
			IGoal	deregister2 = createGoal("dfcap.df_deregister");
			deregister2.getParameter("description").setValue(desc);
			deregister2.getParameter("df").setValue(df);
			dispatchSubgoalAndWait(deregister2);
		}
		catch(GoalFailureException e)
		{
		}
		return num;
	}
}
