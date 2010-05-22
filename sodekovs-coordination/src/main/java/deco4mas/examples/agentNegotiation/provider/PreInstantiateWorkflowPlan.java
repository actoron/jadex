package deco4mas.examples.agentNegotiation.provider;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.logger.GnuFormatter;

/**
 * Creates a workflow
 */
public class PreInstantiateWorkflowPlan extends Plan
{
	static Integer id = new Integer(0);

	public void body()
	{
		final Logger workflowExe = Logger.getAnonymousLogger();
		try
		{
			FileHandler fh = new FileHandler("workflow_execution"
				+ this.getComponentName().substring(this.getComponentName().length() - 1) + ".log",true);
			fh.setFormatter(new GnuFormatter());

			workflowExe.addHandler(fh);
			workflowExe.setLevel(Level.FINEST);
			
//			getInterpreter().getModel().getProperties().put("logging.file", this.getComponentName() + ".log");
//			getInterpreter().getModel().getProperties().put("addConsoleHandler", null);
		} catch (SecurityException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		final IBDIExternalAccess myself = this.getExternalAccess();
		IComponentManagementService cms = (IComponentManagementService) interpreter.getAgentAdapter().getServiceContainer()
			.getService(IComponentManagementService.class);
		try
		{
			// create workflow
			Map args = new HashMap();
			args.put("provider", getComponentIdentifier());

			IResultListener killLis = new IResultListener()
			{

				@Override
				public void resultAvailable(Object source, Object result)
				{
					//LOG
					workflowExe.finest("End " + getClock().getTime());
					System.out.println("*** workflow executed ***");
					IInternalEvent workflowExe = myself.createInternalEvent("workflowExecuted");
					myself.dispatchInternalEvent(workflowExe);
				}

				@Override
				public void exceptionOccurred(Object source, Exception exception)
				{
					System.out.println("Workflow not finished");
				}
			};
			String workflowName = "workflowNet" + id + "(" + getComponentName() + ")";
			String workflowType = "deco4mas/examples/agentNegotiation/provider/workflow/"
				+ (String) getBeliefbase().getBelief("workflowName").getFact() + ".bpmn";
			IFuture fut = cms.createComponent(workflowName, workflowType, new CreationInfo(null, args, this.getComponentIdentifier(), true,
				false), killLis);
//			waitFor(1000); //HACK!!!
			IComponentIdentifier workflowId = (IComponentIdentifier) fut.get(this);

			IFuture fut2 = cms.createComponent(workflowName + "asd", workflowType, new CreationInfo(null, args, this.getComponentIdentifier(), true,
				false), killLis);
			IComponentIdentifier workflowId2 = (IComponentIdentifier) fut2.get(this);
			getBeliefbase().getBelief("workflow").setFact(workflowId);
			//LOG
			workflowExe.finest("Creation " + getClock().getTime());

			// create smas
			IGoal goal = createGoal("createSmaForWorkflow");
			goal.getParameter("workflow").setValue(getBeliefbase().getBelief("workflow").getFact());
			goal.getParameter("ID").setValue(id);
			dispatchSubgoalAndWait(goal);
			id++;

			waitForInternalEvent("workflowExecuted");
			IGoal restart = createGoal("restartWorkflow");
			dispatchTopLevelGoal(restart);
		} catch (Exception e)
		{
			System.out.println(this.getType());
			e.printStackTrace();
			fail(e);
		}
	}
}
